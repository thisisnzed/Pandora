package org.pandora.master.utils;

public class NumberUtils {

    public static boolean isInteger(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (final NullPointerException | NumberFormatException ignore) {
            return false;
        }
    }
}
