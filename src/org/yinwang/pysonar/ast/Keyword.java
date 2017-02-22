package org.yinwang.pysonar.ast;



/**
 * Represents a keyword argument (name=value) in a function call.
 */
public class Keyword extends Node {

    public String arg;
    
    public Node value;

    public Keyword(String arg,  Node value, String file, int start, int end, int line, int col) {
        super(NodeType.KEYWORD, file, start, end, line, col);
        this.arg = arg;
        this.value = value;
        addChildren(value);
    }

    
    @Override
    public String toString() {
        return "(keyword:" + arg + ":" + value + ")";
    }

    
    @Override
    public String toDisplay() {
        return arg;
    }

}
