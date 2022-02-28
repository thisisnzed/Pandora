package org.pandora.server.utils;

import java.time.Instant;

public class TimeUtils {

    public static String getDate() {
        final java.util.Date date = java.sql.Date.from(Instant.now());
        final StringBuilder stringBuilder = new StringBuilder();
        final int hours = date.getHours();
        final int min = date.getMinutes();
        final int sec = date.getSeconds();
        if (hours < 10) stringBuilder.append("0").append(hours).append(":");
        else stringBuilder.append(hours).append(":");
        if (min < 10) stringBuilder.append("0").append(min).append(":");
        else stringBuilder.append(min).append(":");
        if (sec < 10) stringBuilder.append("0").append(sec);
        else stringBuilder.append(sec);
        return "[" + stringBuilder.toString() + "] ";
    }
}
