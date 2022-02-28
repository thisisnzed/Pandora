package org.pandora.master.builder.impl.node;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.pandora.master.utils.FileUtils;

public class SelectorBuilder {

    public StackPane buildSelector(final FileUtils fileUtils, final double x, final double y, final String value) {
        final StackPane stackPane = new StackPane();
        final Rectangle back = new Rectangle(30, 10, Color.RED);
        final Button button = new Button();
        final String styleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: RED;";
        final String styleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #00893d;";
        final boolean[] state = {Boolean.parseBoolean(fileUtils.getValue(value))};
        final double height = back.getHeight();
        stackPane.getChildren().addAll(back, button);
        stackPane.setMinSize(30, 15);
        back.maxWidth(30);
        back.minWidth(30);
        back.maxHeight(10);
        back.minHeight(10);
        back.setArcHeight(height);
        back.setArcWidth(height);
        button.setShape(new Circle(2.0));
        button.setMaxSize(15, 15);
        button.setMinSize(15, 15);
        button.setStyle(state[0] ? styleOn : styleOff);
        back.setFill(state[0] ? Color.valueOf("#80C49E") : Color.valueOf("#eda6bc"));
        stackPane.setAlignment(button, state[0] ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        final EventHandler<Event> click = e -> {
            if (state[0]) {
                button.setStyle(styleOff);
                back.setFill(Color.valueOf("#eda6bc"));
                stackPane.setAlignment(button, Pos.CENTER_LEFT);
                state[0] = false;
            } else {
                button.setStyle(styleOn);
                back.setFill(Color.valueOf("#80C49E"));
                stackPane.setAlignment(button, Pos.CENTER_RIGHT);
                state[0] = true;
            }
            fileUtils.setValue(value, String.valueOf(state[0]));
        };
        button.setFocusTraversable(false);
        stackPane.setOnMouseClicked(click);
        button.setOnMouseClicked(click);
        stackPane.setLayoutX(x);
        stackPane.setLayoutY(y);
        return stackPane;
    }
}
