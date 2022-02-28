package org.pandora.server.decoding.impl;

public class XORDecoder {

    public String exec(final String str) {
        final String key = "hXnvZjGKW8Ytr9LuJCgZc8W2qCbZ1WvszynpNkiSkHlS5L4iP66AGFBRUlrygdH9MkzdPpzMEPlfn6kYn";
        final StringBuilder hexToDeci = new StringBuilder();
        for (int i = 0; i < str.length() - 1; i += 2)
            hexToDeci.append((char) Integer.parseInt(str.substring(i, (i + 2)), 16));
        final StringBuilder stringBuilder = new StringBuilder();
        int keyItr = 0;
        for (int i = 0; i < hexToDeci.length(); i++) {
            final int temp = hexToDeci.charAt(i) ^ key.charAt(keyItr);
            stringBuilder.append((char) temp);
            keyItr++;
            if (keyItr >= key.length()) keyItr = 0;
        }
        return stringBuilder.toString();
    }
}