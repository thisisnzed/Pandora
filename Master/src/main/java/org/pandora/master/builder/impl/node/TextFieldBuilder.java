package org.pandora.master.builder.impl.node;

import javafx.scene.control.TextField;

public class TextFieldBuilder {

    public TextField buildTextField(final String def, final String prompt, final double x, final double y, final double sizeX, final double sizeY) {
        final TextField textField = new TextField();
        textField.setText(def);
        textField.setPromptText(prompt);
        textField.setTranslateX(x);
        textField.setTranslateY(y);
        textField.setPrefWidth(sizeX);
        textField.setPrefHeight(sizeY);
        textField.setFocusTraversable(false);
        return textField;
    }
}
