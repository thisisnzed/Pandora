package org.pandora.master.builder.impl.node;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

public class TableViewBuilder {

    public TableView<?> buildTableView(final String placeholderText, final Color placeholderColor, final int placeholderSize, final double height, final double width, final double x, final double y, final String id) {
        final Label placeholder = new Label(placeholderText);
        placeholder.setTextFill(placeholderColor);
        placeholder.setStyle("-fx-font-weight: bold;\n-fx-font-size: " + placeholderSize + "px;");
        final TableView<?> tableView = new TableView<>();
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setPrefHeight(height);
        tableView.setPrefWidth(width);
        tableView.setTranslateY(y);
        tableView.setTranslateX(x);
        tableView.setId(id);
        tableView.setPlaceholder(placeholder);
        return tableView;
    }
}
