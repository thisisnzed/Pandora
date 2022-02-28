package org.pandora.master.builder.impl.node;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleBuilder {

    public Rectangle buildRectangle(final double x, final double y, final double width, final double height, final double arcHeight, final double arcWidth, final Color fill, final double strokeWidth, final Color stroke, final DropShadow dropShadow) {
        final Rectangle rectangle = new Rectangle();
        rectangle.setX(x);
        rectangle.setY(y);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setArcHeight(arcHeight);
        rectangle.setArcWidth(arcWidth);
        rectangle.setFill(fill);
        rectangle.setStrokeWidth(strokeWidth);
        rectangle.setStroke(stroke);
        if (dropShadow != null) rectangle.setEffect(dropShadow);
        return rectangle;
    }
}
