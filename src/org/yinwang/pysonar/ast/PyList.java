package org.yinwang.pysonar.ast;

import java.util.List;


public class PyList extends Sequence {

	public PyList(List<Node> elts, String file, int start, int end, int line, int col) {
		super(NodeType.PYLIST, elts, file, start, end, line, col);
	}

	@Override
	public String toString()
	{
		return "<List:" + start + ":" + elts + ">";
	}

}
