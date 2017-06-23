package org.yinwang.pysonar.ast;

public class Index extends Node {

	public Node value;

	public Index(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.INDEX, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Index:" + value + ">";
	}

}
