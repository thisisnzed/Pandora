package org.pandora.agent.decoding.impl;

public class XORDecoder {

    public String exec(final String str, final String key) {
        final StringBuilder hexToDeci = new StringBuilder();
        for (int i = 0; i < str.length() - 1; i += 2)
            hexToDeci.append((char) Integer.parseInt(str.substring(i, (i + 2)), 16));
        final StringBuilder decrypted = new StringBuilder();
        int keyItr = 0;
        for (int i = 0; i < hexToDeci.length(); i++) {
            int temp = hexToDeci.charAt(i) ^ key.charAt(keyItr);
            decrypted.append((char) temp);
            keyItr++;
            if (keyItr >= key.length()) keyItr = 0;
        }
        return decrypted.toString();
    }
}