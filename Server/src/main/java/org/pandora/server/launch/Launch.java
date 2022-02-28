package org.pandora.server.launch;

import org.pandora.server.Server;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Launch {

    public static void main(String[] args) {
        forceUTF();
        new Server(args).start();
    }

    private static void forceUTF() {
        System.setProperty("file.encoding", "UTF-8");
        try {
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (final IllegalAccessException | NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }
}
