package org.pandora.master.builder.impl.node;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LabelBuilder {

    public Label buildLabel(final String string, final int x, final int y, final String color, final String font, final int size) {
        return this.buildLabel(string, x, y, Color.valueOf(color), font, size);
    }

    public Label buildLabel(final String string, final int x, final int y, final Color color, final String font, final int size) {
        final Label label = new Label(string);
        label.setFont(font.equals("") ? Font.font("Helvetica", size) : Font.font(font, size));
        label.setTextFill(color);
        label.setTranslateX(x);
        label.setTranslateY(y);
        return label;
    }
}
