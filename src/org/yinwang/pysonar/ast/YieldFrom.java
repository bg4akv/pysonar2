package org.yinwang.pysonar.ast;

public class YieldFrom extends Node {

	public Node value;

	public YieldFrom(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.YIELDFROM, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<YieldFrom:" + start + ":" + value + ">";
	}

}
