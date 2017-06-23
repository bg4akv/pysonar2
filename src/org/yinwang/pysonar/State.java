package org.yinwang.pysonar;

import org.yinwang.pysonar.ast.Node;
import org.yinwang.pysonar.types.ModuleType;
import org.yinwang.pysonar.types.Type;
import org.yinwang.pysonar.types.Types;
import org.yinwang.pysonar.types.UnionType;

import java.util.*;
import java.util.Map.Entry;


public class State {
	public enum StateType {
		CLASS,
		INSTANCE,
		FUNCTION,
		MODULE,
		GLOBAL,
		SCOPE
	}

	public Map<String, Set<Binding>> table = new HashMap<>(0);

	public State parent; // all are non-null except global table

	public State forwarding; // link to the closest non-class scope, for lifting functions out

	public List<State> supers;

	public Set<String> globalNames;
	public StateType stateType;
	public Type type;

	public String path = "";

	public State(State parent, StateType type) {
		this.parent = parent;
		stateType = type;

		if (type == StateType.CLASS) {
			forwarding = parent == null ? null : parent.getForwarding();
		} else {
			forwarding = this;
		}
	}

	public State(State s) {
		table = new HashMap<>();
		table.putAll(s.table);
		parent = s.parent;
		stateType = s.stateType;
		forwarding = s.forwarding;
		supers = s.supers;
		globalNames = s.globalNames;
		type = s.type;
		path = s.path;
	}

	// erase and overwrite this to s's contents
	public void overwrite(State s)
	{
		table = s.table;
		parent = s.parent;
		stateType = s.stateType;
		forwarding = s.forwarding;
		supers = s.supers;
		globalNames = s.globalNames;
		type = s.type;
		path = s.path;
	}

	public State copy()
	{
		return new State(this);
	}

	public void merge(State other)
	{
		for (Map.Entry<String, Set<Binding>> e2 : other.table.entrySet()) {
			Set<Binding> b1 = table.get(e2.getKey());
			Set<Binding> b2 = e2.getValue();

			if (b1 != null && b2 != null) {
				b1.addAll(b2);
			} else if (b1 == null && b2 != null) {
				table.put(e2.getKey(), b2);
			}
		}
	}

	public static State merge(State state1, State state2)
	{
		State ret = state1.copy();
		ret.merge(state2);
		return ret;
	}

	public void setParent(State parent)
	{
		this.parent = parent;
	}

	public State getForwarding()
	{
		if (forwarding != null) {
			return forwarding;
		} else {
			return this;
		}
	}

	public void addSuper(State sup)
	{
		if (supers == null) {
			supers = new ArrayList<>();
		}
		supers.add(sup);
	}

	public void setStateType(StateType type)
	{
		stateType = type;
	}

	public void addGlobalName(String name)
	{
		if (globalNames == null) {
			globalNames = new HashSet<>(1);
		}
		globalNames.add(name);
	}

	public boolean isGlobalName(String name)
	{
		if (globalNames != null) {
			return globalNames.contains(name);
		} else if (parent != null) {
			return parent.isGlobalName(name);
		} else {
			return false;
		}
	}

	public void remove(String id)
	{
		table.remove(id);
	}

	// create new binding and insert
	public void insert(String id, Node node, Type type, Binding.Kind kind)
	{
		Binding b = new Binding(id, node, type, kind);
		if (type instanceof ModuleType) {
			b.setQname(type.asModuleType().qname);
		} else {
			b.setQname(extendPath(id));
		}
		update(id, b);
	}

	// directly insert a given binding

	public Set<Binding> update(String id, Set<Binding> bs)
	{
		table.put(id, bs);
		return bs;
	}

	public Set<Binding> update(String id, Binding b)
	{
		Set<Binding> bs = new HashSet<>(1);
		bs.add(b);
		table.put(id, bs);
		return bs;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	/**
	 * Look up a name in the current symbol table only. Don't recurse on the
	 * parent table.
	 */

	public Set<Binding> lookupLocal(String name)
	{
		return table.get(name);
	}

	/**
	 * Look up a name (String) in the current symbol table.  If not found,
	 * recurse on the parent table.
	 */

	public Set<Binding> lookup(String name)
	{
		Set<Binding> b = getModuleBindingIfGlobal(name);
		if (b != null) {
			return b;
		} else {
			Set<Binding> ent = lookupLocal(name);
			if (ent != null) {
				return ent;
			} else {
				if (parent != null) {
					return parent.lookup(name);
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * Look up a name in the module if it is declared as global, otherwise look
	 * it up locally.
	 */

	public Set<Binding> lookupScope(String name)
	{
		Set<Binding> b = getModuleBindingIfGlobal(name);
		if (b != null) {
			return b;
		} else {
			return lookupLocal(name);
		}
	}

	/**
	 * Look up an attribute in the type hierarchy.  Don't look at parent link,
	 * because the enclosing scope may not be a super class. The search is
	 * "depth first, left to right" as in Python's (old) multiple inheritance
	 * rule. The new MRO can be implemented, but will probably not introduce
	 * much difference.
	 */

	private static Set<State> looked = new HashSet<>(); // circularity prevention

	public Set<Binding> lookupAttr(String attr)
	{
		if (looked.contains(this)) {
			return null;
		} else {
			Set<Binding> b = lookupLocal(attr);
			if (b != null) {
				return b;
			} else {
				if (supers != null && !supers.isEmpty()) {
					looked.add(this);
					for (State p : supers) {
						b = p.lookupAttr(attr);
						if (b != null) {
							looked.remove(this);
							return b;
						}
					}
					looked.remove(this);
					return null;
				} else {
					return null;
				}
			}
		}
	}

	/**
	 * Look for a binding named {@code name} and if found, return its type.
	 */

	public Type lookupType(String name)
	{
		Set<Binding> bs = lookup(name);
		if (bs == null) {
			return null;
		} else {
			return makeUnion(bs);
		}
	}

	/**
	 * Look for a attribute named {@code attr} and if found, return its type.
	 */

	public Type lookupAttrType(String attr)
	{
		Set<Binding> bs = lookupAttr(attr);
		if (bs == null) {
			return null;
		} else {
			return makeUnion(bs);
		}
	}

	public static Type makeUnion(Set<Binding> bs)
	{
		Type t = Types.UNKNOWN;
		for (Binding b : bs) {
			t = UnionType.union(t, b.type);
		}
		return t;
	}

	/**
	 * Find a symbol table of a certain type in the enclosing scopes.
	 */

	public State getStateOfType(StateType type)
	{
		if (stateType == type) {
			return this;
		} else if (parent == null) {
			return null;
		} else {
			return parent.getStateOfType(type);
		}
	}

	/**
	 * Returns the global scope (i.e. the module scope for the current module).
	 */

	public State getGlobalTable()
	{
		State result = getStateOfType(StateType.MODULE);
		if (result != null) {
			return result;
		} else {
			$.die("Couldn't find global table. Shouldn't happen");
			return this;
		}
	}

	/**
	 * If {@code name} is declared as a global, return the module binding.
	 */

	private Set<Binding> getModuleBindingIfGlobal(String name)
	{
		if (isGlobalName(name)) {
			State module = getGlobalTable();
			if (module != this) {
				return module.lookupLocal(name);
			}
		}
		return null;
	}

	public void putAll(State other)
	{
		table.putAll(other.table);
	}

	public Set<String> keySet()
	{
		return table.keySet();
	}

	public Collection<Binding> values()
	{
		Set<Binding> ret = new HashSet<>();
		for (Set<Binding> bs : table.values()) {
			ret.addAll(bs);
		}
		return ret;
	}

	public Set<Entry<String, Set<Binding>>> entrySet()
	{
		return table.entrySet();
	}

	public boolean isEmpty()
	{
		return table.isEmpty();
	}

	public String extendPath(String name)
	{
		name = $.moduleName(name);
		if (path.equals("")) {
			return name;
		}
		return path + "." + name;
	}

	@Override
	public String toString()
	{
		return "<State:" + stateType + ":" + table.keySet() + ">";
	}

}
