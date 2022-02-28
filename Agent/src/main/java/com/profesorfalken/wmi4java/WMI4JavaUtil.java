// =============================================== //
// Recompile disabled. Please run Recaf with a JDK //
// =============================================== //

// Decompiled with: FernFlower
// Class Version: 5
package com.profesorfalken.wmi4java;

import java.util.Iterator;

public final class WMI4JavaUtil {
    public static String join(String delimiter, Iterable<?> parts) {
        StringBuilder joinedString = new StringBuilder();
        Iterator var3 = parts.iterator();

        while(var3.hasNext()) {
            Object part = var3.next();
            joinedString.append(part);
            joinedString.append(delimiter);
        }

        joinedString.delete(joinedString.length() - delimiter.length(), joinedString.length());
        return joinedString.toString();
    }
}
 