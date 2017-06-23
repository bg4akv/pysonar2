package org.yinwang.pysonar.ast;

import java.util.List;


public class With extends Node {

	public List<Withitem> items;
	public Block body;
	public boolean isAsync = false;

	public With(List<Withitem> items, Block body, String file, boolean isAsync, int start, int end, int line, int col) {
		super(NodeType.WITH, file, start, end, line, col);
		this.items = items;
		this.body = body;
		this.isAsync = isAsync;
		addChildren(items);
		addChildren(body);
	}

	@Override
	public String toString()
	{
		return "<With:" + items + ":" + body + ">";
	}

}
