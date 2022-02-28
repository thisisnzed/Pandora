package org.pandora.master.builder.impl.node;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ButtonBuilder {

    public Button buildButton(final ImageView imageView, final String string, final int x, final int y, final String color, final String font, final int size, final int width, final int height, final String styleId) {
        return this.buildButton(imageView, string, x, y, Color.valueOf(color), font, size, width, height, styleId);
    }

    public Button buildButton(final ImageView imageView, final String string, final int x, final int y, final Color color, final String font, final int size, final int width, final int height, final String styleId) {
        final Button button;
        if (imageView != null) button = new Button(string, imageView);
        else button = new Button(string);
        button.setFont(Font.font(font, size));
        button.setTextFill(color);
        button.setFocusTraversable(false);
        button.setPrefSize(width, height);
        button.setTranslateX(x);
        button.setTranslateY(y);
        button.setId(styleId);
        return button;
    }
}
