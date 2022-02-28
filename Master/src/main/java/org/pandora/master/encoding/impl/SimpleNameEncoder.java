package org.pandora.master.encoding.impl;

public class SimpleNameEncoder {

    public String exec(final String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
