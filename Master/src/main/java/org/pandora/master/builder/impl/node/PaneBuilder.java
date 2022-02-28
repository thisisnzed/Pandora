package org.pandora.master.builder.impl.node;

import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;

public class PaneBuilder {

    public Pane buildPane(final int x, final int y, final int width, final int height, final Background background) {
        final Pane pane = new Pane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        if (background != null) pane.setBackground(background);
        return pane;
    }
}
