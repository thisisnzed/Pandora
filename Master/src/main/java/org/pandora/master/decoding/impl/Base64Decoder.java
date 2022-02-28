package org.pandora.master.decoding.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Decoder {

    public String exec(final String str) {
        return new String(Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)));
    }
}