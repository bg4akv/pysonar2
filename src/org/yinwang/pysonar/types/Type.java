package org.yinwang.pysonar.types;

import org.yinwang.pysonar.State;
import org.yinwang.pysonar.TypeStack;
import org.yinwang.pysonar.$;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class Type {

	public State table = new State(null, State.StateType.SCOPE);
	public String file = null;

	protected static TypeStack typeStack = new TypeStack();

	public Type() {
	}

	@Override
	public boolean equals(Object other)
	{
		return typeEquals(other);
	}

	public abstract boolean typeEquals(Object other);

	public void setTable(State table)
	{
		this.table = table;
	}

	public void setFile(String file)
	{
		this.file = file;
	}

	public boolean isNumType()
	{
		return this == Types.IntInstance || this == Types.FloatInstance;
	}

	public boolean isUnknownType()
	{
		return this == Types.UNKNOWN;
	}

	public ModuleType asModuleType()
	{
		if (this instanceof UnionType) {
			for (Type t : ((UnionType) this).types) {
				if (t instanceof ModuleType) {
					return t.asModuleType();
				}
			}
			$.die("Not containing a ModuleType");
			// can't get here, just to make the  annotation happy
			return new ModuleType(null, null, null);
		} else if (this instanceof ModuleType) {
			return (ModuleType) this;
		} else {
			$.die("Not a ModuleType");
			// can't get here, just to make the  annotation happy
			return new ModuleType(null, null, null);
		}
	}

	/**
	 * Internal class to support printing in the presence of type-graph cycles.
	 */
	protected class CyclicTypeRecorder {
		int count = 0;

		private Map<Type, Integer> elements = new HashMap<>();

		private Set<Type> used = new HashSet<>();

		public Integer push(Type t)
		{
			count += 1;
			elements.put(t, count);
			return count;
		}

		public void pop(Type t)
		{
			elements.remove(t);
			used.remove(t);
		}

		public Integer visit(Type t)
		{
			Integer i = elements.get(t);
			if (i != null) {
				used.add(t);
			}
			return i;
		}

		public boolean isUsed(Type t)
		{
			return used.contains(t);
		}
	}

	protected abstract String printType(CyclicTypeRecorder ctr);

	@Override
	public String toString()
	{
		return printType(new CyclicTypeRecorder());
	}

}
