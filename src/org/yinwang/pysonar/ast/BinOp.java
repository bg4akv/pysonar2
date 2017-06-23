package org.yinwang.pysonar.ast;

public class BinOp extends Node {

	public Node left;

	public Node right;

	public Op op;

	public BinOp(Op op, Node left, Node right, String file, int start, int end, int line, int col) {
		super(NodeType.BINOP, file, start, end, line, col);
		this.left = left;
		this.right = right;
		this.op = op;
		addChildren(left, right);
	}

	@Override
	public String toString()
	{
		return "(" + left + " " + op + " " + right + ")";
	}

}
