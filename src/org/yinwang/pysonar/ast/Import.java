package org.yinwang.pysonar.ast;

import java.util.List;


public class Import extends Node {

	public List<Alias> names;

	public Import(List<Alias> names, String file, int start, int end, int line, int col) {
		super(NodeType.IMPORT, file, start, end, line, col);
		this.names = names;
		addChildren(names);
	}

	@Override
	public String toString()
	{
		return "<Import:" + names + ">";
	}

}
