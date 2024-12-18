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
  

    public ImageController() {
        this.model = new ImageModel();
        this.views = new ArrayList<>();
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
    


    public void applyPaintBucket(int x, int y, Color color, int tolerance) {
        model.applyPaintBucket(x, y, color, tolerance);
        activeView.updateImage(model.getImage());
    }

    public void rotate(boolean clockwise) {
        model.rotate(clockwise);
        activeView.updateImage(model.getImage());
    }

    public void rotateImageByAngle(int angle) {
        model.rotateByAngle(angle);
        activeView.updateImage(model.getImage());
    }

    public void pickColor(int x, int y) {
        Color color = model.pickColor(x, y);
        activeView.displayPickedColor(color);
    }

    public void flipImage(boolean horizontal) {
        model.flip(horizontal);
        activeView.updateImage(model.getImage());
    }

    public void adjustBrightness(int brighten) {
        model.adjustBrightness(brighten);
        activeView.updateImage(model.getImage());
    }

    public void adjustContrast(int contrast) {
        model.adjustContrast(contrast);
        activeView.updateImage(model.getImage());
    }

    public void addTextToImage(String text, Font font, Color color, int x, int y) {
        model.addText(text, font, color, x, y);
        activeView.updateImage(model.getImage());
    }

    public static void main(String[] args) {
        new ImageController();
    }
}
