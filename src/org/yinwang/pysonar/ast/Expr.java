package org.yinwang.pysonar.ast;

/**
 * Expression statement.
 */
public class Expr extends Node {

	public Node value;

	public Expr(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.EXPR, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Expr:" + value + ">";
	}

}
