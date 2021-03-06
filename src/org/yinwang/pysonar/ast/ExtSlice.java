package org.yinwang.pysonar.ast;

import java.util.List;


public class ExtSlice extends Node {

	public List<Node> dims;

	public ExtSlice(List<Node> dims, String file, int start, int end, int line, int col) {
		super(NodeType.EXTSLICE, file, start, end, line, col);
		this.dims = dims;
		addChildren(dims);
	}

	@Override
	public String toString()
	{
		return "<ExtSlice:" + dims + ">";
	}

}
