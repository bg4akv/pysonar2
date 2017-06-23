package org.yinwang.pysonar.ast;

public class Break extends Node {

	public Break(String file, int start, int end, int line, int col) {
		super(NodeType.BREAK, file, start, end, line, col);
	}

	@Override
	public String toString()
	{
		return "(break)";
	}
}
