package org.pandora.master.builder.impl.node;

import javafx.scene.control.CheckBox;

public class CheckBoxBuilder {

    public CheckBox buildCheckBox(final double x, final double y) {
        final CheckBox checkBox = new CheckBox();
        checkBox.setTranslateX(x);
        checkBox.setTranslateY(y);
        checkBox.setFocusTraversable(false);
        return checkBox;
    }
}
