package org.pandora.agent.encoding.impl;

public class XOREncoder {

    public String exec(final String str, final String key) {
        final StringBuilder encrypted = new StringBuilder();
        int keyItr = 0;
        for (int i = 0; i < str.length(); i++) {
            encrypted.append(String.format("%02x", (byte) str.charAt(i) ^ key.charAt(keyItr)));
            keyItr++;
            if (keyItr >= key.length()) keyItr = 0;
        }
        return encrypted.toString();
    }
}