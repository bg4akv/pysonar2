package org.yinwang.pysonar.ast;




/**
 * A name alias.  Used for the components of import and import-from statements.
 */
public class Withitem extends Node {

    
    public Node optional_vars;
    
    public Node context_expr;

    public Withitem( Node context_expr,  Node optional_vars, String file, int start, int end, int line, int col) {
        super(NodeType.WITHITEM, file, start, end, line, col);
        this.context_expr = context_expr;
        this.optional_vars = optional_vars;
        addChildren(context_expr, optional_vars);
    }

    
    @Override
    public String toString() {
        return "(withitem:" + context_expr + " as " + optional_vars + ")";
    }

}
