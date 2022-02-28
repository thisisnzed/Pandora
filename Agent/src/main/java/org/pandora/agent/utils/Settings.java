package org.pandora.agent.utils;

public class Settings {

    public static final String VERSION;
    public static final String HOST;
    public static final String PORT;
    public static final boolean AUTOSTART;
    public static final boolean OTHERFILE;
    public static final String FILEURI;
    public static final String APPLAUNCHER;
    public static final String APPID;

   static {
        VERSION = "1.0.0.3";
        APPID = "base:appid";
        APPLAUNCHER = "base:applauncher";
        FILEURI = "base:fileuri";
        OTHERFILE = Boolean.parseBoolean("base:otherfile");
        AUTOSTART = Boolean.parseBoolean("base:autostart");
        PORT = "base:port";
        HOST = "base:hostname";
    }
}
