package org.pandora.server.utils;

public class StringUtils {

    public static String capitalize(final String str) {
        final int strLen = str.length();
        if (strLen == 0) return str;
        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) return str;
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }
}
