package org.pandora.master.builder.impl.node;

import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

public class ScrollPaneBuilder {

    public ScrollPane buildScrollPane(final boolean fitToWidth, final Text content, final int x, final int y, final int width, final int height) {
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        if (content != null) scrollPane.setContent(content);
        scrollPane.setLayoutX(x);
        scrollPane.setLayoutY(y);
        scrollPane.setPrefWidth(width);
        scrollPane.setPrefHeight(height);
        scrollPane.setFocusTraversable(false);
        return scrollPane;
    }
}
