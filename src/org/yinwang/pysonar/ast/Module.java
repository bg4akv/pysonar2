package org.yinwang.pysonar.ast;

import org.yinwang.pysonar.$;


public class Module extends Node {

	public Block body;

	public Module(Block body, String file, int start, int end, int line, int col) {
		super(NodeType.MODULE, file, start, end, line, col);
		name = $.moduleName(file);
		this.body = body;
		addChildren(this.body);
	}

	@Override
	public String toString()
	{
		return "(module:" + file + ")";
	}

}
