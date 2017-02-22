package org.yinwang.pysonar.ast;



import java.util.List;

public class Global extends Node {

    public List<Name> names;

    public Global(List<Name> names, String file, int start, int end, int line, int col) {
        super(NodeType.GLOBAL, file, start, end, line, col);
        this.names = names;
        addChildren(names);
    }

    
    @Override
    public String toString() {
        return "<Global:" + names + ">";
    }

}
