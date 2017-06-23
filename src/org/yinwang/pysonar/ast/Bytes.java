package org.yinwang.pysonar.ast;

public class Bytes extends Node {

	public Object value;

	public Bytes(Object value, String file, int start, int end, int line, int col) {
		super(NodeType.BYTES, file, start, end, line, col);
		this.value = value.toString();
	}

	@Override
	public String toString()
	{
		return "(bytes: " + value + ")";
	}

}
