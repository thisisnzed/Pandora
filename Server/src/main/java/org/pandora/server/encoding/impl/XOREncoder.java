package org.pandora.server.encoding.impl;

public class XOREncoder {

    public String exec(String str) {
        final String key = "hXnvZjGKW8Ytr9LuJCgZc8W2qCbZ1WvszynpNkiSkHlS5L4iP66AGFBRUlrygdH9MkzdPpzMEPlfn6kYn";
        final StringBuilder stringBuilder = new StringBuilder();
        int keyItr = 0;
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append(String.format("%02x", (byte) str.charAt(i) ^ key.charAt(keyItr)));
            keyItr++;
            if (keyItr >= key.length()) keyItr = 0;
        }
        return stringBuilder.toString();
    }
}