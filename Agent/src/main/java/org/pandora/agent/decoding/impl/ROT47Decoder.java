package org.pandora.agent.decoding.impl;

public class ROT47Decoder {

    public String exec(final String str) {
        final int key = 47;
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int temp = (int) str.charAt(i) - key;
            if ((int) str.charAt(i) == 32) stringBuilder.append(" ");
            else {
                if (temp < 32) temp += 94;
                stringBuilder.append((char) temp);
            }
        }
        return stringBuilder.toString();
    }
}
