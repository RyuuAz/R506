package Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import Model.ImageModel;
import Vue.ImageView;
import Vue.Shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Point;

public class ImageController {
    private ImageModel model;
    private List<ImageView> views;
    private ImageView activeView; // Vue active
 // Liste des fenêtres ouvertes
    private BufferedImage clipboard; // Pour gérer le copier/coller
    private Shape shape;
    private BufferedImage image;

    public ImageController() {
        this.views = new ArrayList<>();
        this.model = new ImageModel(this);
        openNewView(null); // Ouvrir une première fenêtre sans image
    }

    public void openNewView(BufferedImage image) {
        ImageView newView = new ImageView(this); // Crée une nouvelle vue
        if (image != null) {
            newView.updateImage(image); // Charge l'image si elle est fournie
        }
        views.add(newView); // Ajoute la vue à la liste
        setActiveView(newView);
    }

    public void openImage(File file) {
        try {
            if (activeView != null) {
                activeView.dispose(); // Ferme la fenêtre active
            }
            model.loadImageFromFile(file);
            openNewView(model.getImage()); // Crée une nouvelle fenêtre avec l'image
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to open image: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveImage(File file) {
        try {
            String format = "png"; // Format par défaut
            model.saveImageToFile(file, format);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to save image: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void copyImage(BufferedImage image,Shape shape) {
        clipboard = image; // Sauvegarde l'image dans le presse-papiers
        this.shape=shape;
    }

    public void pasteImage(ImageView targetView) {
        if (clipboard != null) {
            targetView.addShape(this.shape);
            targetView.pasteImage(clipboard);// Colle l'image dans la vue cible
        } else {
            JOptionPane.showMessageDialog(null, "Clipboard is empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Shape getShape() {
        return shape;
    }

    public List<ImageView> getViews() {
        return views;
    }

    public void setActiveView(ImageView view) {
        this.activeView = view;
        model.setImage(view.getImage());
    }

    public void removeView(ImageView view) {
        views.remove(view);
        if (views.isEmpty()) {
            System.exit(0);// Ouvre une nouvelle fenêtre si toutes les fenêtres sont fermées
        }
    }
    

    public Point convertToImageCoordinates(int x, int y) {
        return model.convertToImageCoordinates(x, y, activeView.getImageLabel(), activeView.getImageTemp());
    }

    public void applyPaintBucket(int x, int y, Color color, int tolerance, Shape shape) {
        model.applyPaintBucket(x, y, color, tolerance, shape);
        activeView.updateImage(model.getImage());
    }

    public BufferedImage rotate(BufferedImage image, boolean clockwise) {
        return model.rotateImage(image, clockwise);
    }

    public BufferedImage rotateImageByAngle(BufferedImage image, int angle) {
        return model.rotateImageInverse(image, angle);
    }

    public void pickColor(int x, int y) {
        Color color = model.pickColor(x, y);
        activeView.displayPickedColor(color);
    }

    public BufferedImage flipImage(BufferedImage image, boolean horizontal) {
        return model.flipImage(image, horizontal);
    }

    public BufferedImage adjustBrightness(BufferedImage image, int brighten) {
        return model.adjustBrightness(image, brighten);
    }

    public BufferedImage adjustContrast(BufferedImage image, int contrast) {
        return model.adjustContrast(image, contrast);
    }

    public void addTextToImage(String text, Font font, Color color, int x, int y) {
        model.addText(text, font, color, x, y);
        activeView.updateImage(model.getImage());
    }

    public static void main(String[] args) {
        new ImageController();
    }
}
