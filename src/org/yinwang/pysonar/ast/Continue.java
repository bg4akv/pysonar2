package org.yinwang.pysonar.ast;



public class Continue extends Node {

    public Continue(String file, int start, int end, int line, int col) {
        super(NodeType.CONTINUE, file, start, end, line, col);
    }

    
    @Override
    public String toString() {
        return "(continue)";
    }

}
