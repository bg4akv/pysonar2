package org.yinwang.pysonar.ast;



public class Attribute extends Node {

    
    public Node target;
    
    public Name attr;

    public Attribute( Node target,  Name attr, String file, int start, int end, int line, int col) {
        super(NodeType.ATTRIBUTE, file, start, end, line, col);
        this.target = target;
        this.attr = attr;
        addChildren(target, attr);
    }

    
    @Override
    public String toString() {
        return "<Attribute:" + line + ":" + col + ":" + target + "." + attr.id + ">";
    }
}
