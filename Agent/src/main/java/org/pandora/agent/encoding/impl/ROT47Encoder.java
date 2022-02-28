package org.pandora.agent.encoding.impl;

public class ROT47Encoder {

    public String exec(final String str) {
        int key = 47;
        final StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            int temp = (int) str.charAt(i) + key;
            if ((int) str.charAt(i) == 32) encrypted.append(" ");
            else {
                if (temp > 126) temp -= 94;
                encrypted.append((char) temp);
            }
        }
        return encrypted.toString();
    }
}
