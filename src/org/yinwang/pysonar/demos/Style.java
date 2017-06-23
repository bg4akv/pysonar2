package org.yinwang.pysonar.demos;

import org.yinwang.pysonar.$;

import java.util.List;


/**
 * Represents a simple style run for purposes of source highlighting.
 */
public class Style implements Comparable<Style> {

	public enum Type {
		KEYWORD,
		COMMENT,
		STRING,
		DOC_STRING,
		IDENTIFIER,
		BUILTIN,
		NUMBER,
		CONSTANT, // ALL_CAPS identifier
		FUNCTION, // function name
		PARAMETER, // function parameter
		LOCAL, // local variable
		DECORATOR, // function decorator
		CLASS, // class name
		ATTRIBUTE, // object attribute
		LINK, // hyperlink
		ANCHOR, // name anchor
		DELIMITER,
		TYPE_NAME, // reference to a type (e.g. function or class name)

		ERROR,
		WARNING,
		INFO
	}

	public Type type;
	public int start;
	public int end;

	public String message; // optional hover text

	public String url; // internal or external link

	public String id; // for hover highlight
	public List<String> highlight; // for hover highlight

	public Style(Type type, int start, int end) {
		this.type = type;
		this.start = start;
		this.end = end;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Style)) {
			return false;
		}
		Style other = (Style) o;
		return other.type == type
				&& other.start == start
				&& other.end == end
				&& $.same(other.message, message)
				&& $.same(other.url, url);
	}

	@Override
	public int compareTo(Style other)
	{
		if (this.equals(other)) {
			return 0;
		} else if (start < other.start) {
			return -1;
		} else if (start > other.start) {
			return 1;
		} else {
			return this.hashCode() - other.hashCode();
		}
	}

	@Override
	public String toString()
	{
		return "[" + type + " start=" + start + " end=" + end + "]";
	}
}
