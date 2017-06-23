package org.yinwang.pysonar.ast;

public class Return extends Node {

	public Node value;

	public Return(Node n, String file, int start, int end, int line, int col) {
		super(NodeType.RETURN, file, start, end, line, col);
		value = n;
		addChildren(n);
	}

	@Override
	public String toString()
	{
		return "<Return:" + value + ">";
	}

}
