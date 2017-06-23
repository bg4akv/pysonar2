package org.yinwang.pysonar.ast;

public class Yield extends Node {

	public Node value;

	public Yield(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.YIELD, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Yield:" + start + ":" + value + ">";
	}

}
