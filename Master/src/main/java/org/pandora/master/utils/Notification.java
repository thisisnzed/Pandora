package org.pandora.master.utils;

import org.pandora.master.Master;

import java.awt.*;

public class Notification {

    private final FileUtils fileUtils;
    private final Master master;

    public Notification(final Master master) {
        this.fileUtils = master.getFileUtils();
        this.master = master;
    }

    public void tryToNotify(final String title, final String message) {
        if (SystemTray.isSupported() && (this.master.startApp + 10000L) <= System.currentTimeMillis() && ((message.contains(" disconnect") && Boolean.parseBoolean(this.fileUtils.getValue("logoutNotification"))) || (message.contains(" connect") && Boolean.parseBoolean(this.fileUtils.getValue("loginNotification")))))
            this.notify(title, message);
    }

    public void notify(final String title, final String message) {
        final SystemTray tray = SystemTray.getSystemTray();
        final Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        final TrayIcon trayIcon = new TrayIcon(image, "Pandora");
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (final AWTException exception) {
            exception.printStackTrace();
        }
        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }
}