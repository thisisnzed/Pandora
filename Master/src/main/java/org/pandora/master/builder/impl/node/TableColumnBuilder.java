package org.pandora.master.builder.impl.node;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableColumnBuilder {

    public TableColumn<?, ?> buildTableColumn(final String columnName, final double width, final String dataValue) {
        final TableColumn<?, ?> tableColumn = new TableColumn<>(columnName);
        tableColumn.setEditable(false);
        tableColumn.setSortable(false);
        tableColumn.setMinWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(dataValue));
        return tableColumn;
    }
}
