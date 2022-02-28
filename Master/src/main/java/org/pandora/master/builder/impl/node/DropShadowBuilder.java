package org.pandora.master.builder.impl.node;

import javafx.scene.effect.DropShadow;

public class DropShadowBuilder {

    public DropShadow buildDropShadow(final double width, final double height, final double offsetX, final double offsetY, final double radius) {
        final DropShadow dropShadow = new DropShadow();
        dropShadow.setWidth(width);
        dropShadow.setHeight(height);
        dropShadow.setOffsetX(offsetX);
        dropShadow.setOffsetY(offsetY);
        dropShadow.setRadius(radius);
        return dropShadow;
    }
}
