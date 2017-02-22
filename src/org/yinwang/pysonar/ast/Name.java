package org.yinwang.pysonar.ast;



public class Name extends Node {

    
    public final String id;  // identifier
    public NameType type;

    public Name(String id) {
        // generated name
        this(id, null, -1, -1, -1, -1);
    }

    public Name( String id, String file, int start, int end, int line, int col) {
        super(NodeType.NAME, file, start, end, line, col);
        this.id = id;
        this.name = id;
        this.type = NameType.LOCAL;
    }

    public Name( String id, NameType type, String file, int start, int end, int line, int col) {
        super(NodeType.NAME, file, start, end, line, col);
        this.id = id;
        this.type = type;
    }

    /**
     * Returns {@code true} if this name node is the {@code attr} child
     * (i.e. the attribute being accessed) of an {@link Attribute} node.
     */
    public boolean isAttribute() {
        return parent instanceof Attribute
               && ((Attribute) parent).attr == this;
    }

    
    @Override
    public String toString() {
        return "(" + id + ":" + line + ":" + col + ")";
    }

    
    @Override
    public String toDisplay() {
        return id;
    }

}
