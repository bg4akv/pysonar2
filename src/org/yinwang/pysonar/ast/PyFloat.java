package org.yinwang.pysonar.ast;

public class PyFloat extends Node {

	public double value;

	public PyFloat(String s, String file, int start, int end, int line, int col) {
		super(NodeType.PYFLOAT, file, start, end, line, col);
		s = s.replaceAll("_", "");
		if (s.equals("inf")) {
			value = Double.POSITIVE_INFINITY;
		} else if (s.equals("-inf")) {
			value = Double.NEGATIVE_INFINITY;
		} else {
			value = Double.parseDouble(s);
		}
	}

	@Override
	public String toString()
	{
		return "(float:" + value + ")";
	}

}
