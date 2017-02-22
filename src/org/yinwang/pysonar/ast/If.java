package org.yinwang.pysonar.ast;



public class If extends Node {

    
    public Node test;
    public Node body;
    public Node orelse;

    public If( Node test, Node body, Node orelse, String file, int start, int end, int line, int col) {
        super(NodeType.IF, file, start, end, line, col);
        this.test = test;
        this.body = body;
        this.orelse = orelse;
        addChildren(test, body, orelse);
    }

    
    @Override
    public String toString() {
        return "<If:" + start + ":" + test + ":" + body + ":" + orelse + ">";
    }

}
