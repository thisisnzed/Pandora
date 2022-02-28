package org.pandora.master.builder.impl.node;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class BasicLineBuilder {

    public Line buildSimpleLine(final Color color, final double startX, final double startY, final double endX, final double endY) {
        final Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.setStroke(color);
        return line;
    }
}
