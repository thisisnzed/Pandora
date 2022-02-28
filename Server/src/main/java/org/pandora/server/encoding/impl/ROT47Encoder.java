package org.pandora.server.encoding.impl;

public class ROT47Encoder {

    public String exec(final String str) {
        int key = 47;
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int temp = (int) str.charAt(i) + key;
            if ((int) str.charAt(i) == 32) stringBuilder.append(" ");
            else {
                if (temp > 126) temp -= 94;
                stringBuilder.append((char) temp);
            }
        }
        return stringBuilder.toString();
    }
}
