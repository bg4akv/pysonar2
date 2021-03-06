package org.yinwang.pysonar.ast;

public class UnaryOp extends Node {

	public Op op;
	public Node operand;

	public UnaryOp(Op op, Node operand, String file, int start, int end, int line, int col) {
		super(NodeType.UNARYOP, file, start, end, line, col);
		this.op = op;
		this.operand = operand;
		addChildren(operand);
	}

	@Override
	public String toString()
	{
		return "(" + op + " " + operand + ")";
	}

}
