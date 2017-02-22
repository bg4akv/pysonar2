package org.yinwang.pysonar.ast;




public class Subscript extends Node {

    
    public Node value;
    
    public Node slice;  // an NIndex or NSlice

    public Subscript( Node value,  Node slice, String file, int start, int end, int line, int col) {
        super(NodeType.SUBSCRIPT, file, start, end, line, col);
        this.value = value;
        this.slice = slice;
        addChildren(value, slice);
    }

    
    @Override
    public String toString() {
        return "<Subscript:" + value + ":" + slice + ">";
    }

}
