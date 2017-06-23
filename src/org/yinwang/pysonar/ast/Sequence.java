package org.yinwang.pysonar.ast;

import java.util.List;


public abstract class Sequence extends Node {

	public List<Node> elts;

	public Sequence(NodeType nodeType, List<Node> elts, String file, int start, int end, int line, int col) {
		super(nodeType, file, start, end, line, col);
		this.elts = elts;
		addChildren(elts);
	}

}
