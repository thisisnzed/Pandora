package org.pandora.master.builder.impl.node;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextBuilder {

    public Text buildText(final String string, final int x, final int y, final Color color, final String font, final int size) {
        final Text text = new Text(string);
        text.setFill(color);
        text.setTranslateX(x);
        text.setTranslateY(y);
        text.setFont(font.equals("") ? Font.font("Helvetica", size) : Font.font(font, size));
        text.setText(string);
        text.setFocusTraversable(false);
        return text;
    }
}
