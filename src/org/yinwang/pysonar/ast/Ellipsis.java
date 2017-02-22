package org.yinwang.pysonar.ast;



public class Ellipsis extends Node {

    public Ellipsis(String file, int start, int end, int line, int col) {
        super(NodeType.ELLIPSIS, file, start, end, line, col);
    }

    
    @Override
    public String toString() {
        return "...";
    }

}
