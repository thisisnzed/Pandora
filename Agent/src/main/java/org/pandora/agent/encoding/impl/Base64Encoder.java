package org.pandora.agent.encoding.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encoder {

    public String exec(final String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
}