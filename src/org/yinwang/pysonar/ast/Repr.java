package org.yinwang.pysonar.ast;

public class Repr extends Node {

	public Node value;

	public Repr(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.REPR, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Repr:" + value + ">";
	}

}
