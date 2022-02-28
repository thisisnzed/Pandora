package org.pandora.agent.utils;

public class BooleanUtils {

    public static boolean isBoolean(final String str) {
        return str.equals("true") || str.equals("false");
    }
}
