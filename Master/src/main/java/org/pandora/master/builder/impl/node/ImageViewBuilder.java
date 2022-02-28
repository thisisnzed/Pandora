package org.pandora.master.builder.impl.node;

import javafx.scene.image.ImageView;

public class ImageViewBuilder {

    public ImageView buildImageView(final String location, final int x, final int y, final int width, final int height) {
        ImageView imageView = location == null ? new ImageView() : new ImageView(location);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setX(x);
        imageView.setY(y);
        return imageView;
    }
}
