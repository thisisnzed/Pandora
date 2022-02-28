package org.pandora.server.decoding.impl;

public class SimpleNameDecoder {

    public String exec(final String str) {
        return new StringBuffer(str).reverse().toString();
    }
}
