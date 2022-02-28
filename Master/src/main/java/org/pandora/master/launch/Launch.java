package org.pandora.master.launch;

import javafx.application.Application;
import org.pandora.master.Master;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Launch {

    public static void main(String[] args) {
        forceUTF();
        Application.launch(Master.class, args);
    }

    private static void forceUTF() {
        System.setProperty("file.encoding", "UTF-8");
        try {
            final Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (final IllegalAccessException | NoSuchFieldException exception) {
            exception.printStackTrace();
        }
    }
}
