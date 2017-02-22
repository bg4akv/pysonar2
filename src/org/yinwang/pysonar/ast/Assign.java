package org.yinwang.pysonar.ast;



public class Assign extends Node {

    
    public Node target;
    
    public Node value;

    public Assign( Node target,  Node value, String file, int start, int end, int line, int col) {
        super(NodeType.ASSIGN, file, start, end, line, col);
        this.target = target;
        this.value = value;
        addChildren(target);
        addChildren(value);
    }

    
    @Override
    public String toString() {
        return "(" + target + " = " + value + ")";
    }
}
