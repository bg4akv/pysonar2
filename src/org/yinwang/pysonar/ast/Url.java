package org.yinwang.pysonar.ast;



/**
 * virtual-AST node used to represent virtual source locations for builtins
 * as external urls.
 */
public class Url extends Node {

    public String url;

    public Url(String url) {
        this.url = url;
    }

    
    @Override
    public String toString() {
        return "<Url:\"" + url + "\">";
    }

}
