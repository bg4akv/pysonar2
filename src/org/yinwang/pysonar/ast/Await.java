package org.yinwang.pysonar.ast;

public class Await extends Node {

	public Node value;

	public Await(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.AWAIT, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Await:" + value + ">";
	}

}
