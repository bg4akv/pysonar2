package org.yinwang.pysonar.ast;

public class Pass extends Node {

	public Pass(String file, int start, int end, int line, int col) {
		super(NodeType.PASS, file, start, end, line, col);
	}

	@Override
	public String toString()
	{
		return "<Pass>";
	}

}
