package org.yinwang.pysonar.ast;



import java.util.List;

public class Block extends Node {

    
    public List<Node> seq;

    public Block( List<Node> seq, String file, int start, int end, int line, int col) {
        super(NodeType.BLOCK, file, start, end, line, col);
        this.seq = seq;
        addChildren(seq);
    }

    
    @Override
    public String toString() {
        return "(block:" + seq + ")";
    }

}
