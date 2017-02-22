package org.yinwang.pysonar.ast;



public class Starred extends Node {

    public Node value;

    public Starred(Node n, String file, int start, int end, int line, int col) {
        super(NodeType.STARRED, file, start, end, line, col);
        this.value = n;
        addChildren(n);
    }

    
    @Override
    public String toString() {
        return "<starred:" + value + ">";
    }

}
