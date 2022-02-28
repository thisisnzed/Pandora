package org.pandora.master.builder.impl.node;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class ChoiceBoxBuilder {

    public ChoiceBox<?> buildChoiceBox(final ObservableList<?> observableList, final double x, final double y, final double width, final double height) {
        final ChoiceBox<?> choiceBox = new ChoiceBox<>(observableList);
        choiceBox.setTranslateX(x);
        choiceBox.setTranslateY(y);
        choiceBox.setPrefSize(width, height);
        choiceBox.setFocusTraversable(false);
        return choiceBox;
    }
}
