package Vue;

import java.awt.Cursor;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ImageViewEventListeners {

    private ImageView imageView;
    private boolean isPainting = false;
    private boolean isPickingColor = false;

    public ImageViewEventListeners(ImageView imageView) {
        this.imageView = imageView;

        JMenuItem paintBucketItem = imageView.paintBucketItem;
        paintBucketItem.addActionListener(e -> {
            if (imageView.controller != null) {
                // Change le curseur pour le seau de peinture
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                imageView.setCursor(cursor);
                isPainting = true;
            }
        });

        imageView.imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPainting) {
                    int x = evt.getX();
                    int y = evt.getY();

                    // Ajustement des coordonnées
                    int imageX = x - (imageView.imageLabel.getWidth() - imageView.image.getWidth()) / 2;
                    int imageY = y - (imageView.imageLabel.getHeight() - imageView.image.getHeight()) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < imageView.image.getWidth() && imageY >= 0 && imageY < imageView.image.getHeight()) {
                        if (imageView.controller != null) {
                            imageView.controller.applyPaintBucket(imageX, imageY, imageView.pickedColor, 90);
                        }
                    }

                    isPainting = false;
                    imageView.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        JMenuItem pickColorItem = imageView.pickColorItem;
        pickColorItem.addActionListener(e -> {
            if (imageView.controller != null) {
                // Changer le curseur pour la pipette
                Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
                imageView.setCursor(cursor);
                isPickingColor = true;
            }
        });

        imageView.imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPickingColor) {
                    int x = evt.getX();
                    int y = evt.getY();

                    // Ajustement des coordonnées
                    int imageX = x - (imageView.imageLabel.getWidth() - imageView.image.getWidth()) / 2;
                    int imageY = y - (imageView.imageLabel.getHeight() - imageView.image.getHeight()) / 2;

                    // Vérifiez si les coordonnées ajustées sont dans les limites de l'image
                    if (imageX >= 0 && imageX < imageView.image.getWidth() && imageY >= 0 && imageY < imageView.image.getHeight()) {
                        if (imageView.controller != null) {
                            imageView.controller.pickColor(imageX, imageY);
                        }
                    }

                    isPickingColor = false;
                    imageView.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
}
