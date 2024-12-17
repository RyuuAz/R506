package Controller;

import java.io.File;

import javax.swing.JOptionPane;

import Model.ImageModel;
import Vue.ImageView;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;


public class ImageController {
    private ImageModel model;
    private ImageView view;
    private BufferedImage image;

    public ImageController(ImageModel model, ImageView view) {
        this.model = model;
        this.view = view;
    }

    public void openImage(File file) {
        try {
            model.loadImageFromFile(file);
            view.updateImage(model.getImage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to open image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveImage(File file) {
        try {
            String format = "png"; // Par défaut, on choisit PNG
            model.saveImageToFile(file, format);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Failed to save image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void applyPaintBucket(int x, int y, Color color, int tolerance) {
        model.applyPaintBucket(x, y, color, tolerance);
        view.updateImage(model.getImage());
    }

    public void rotate(boolean clockwise) {
        model.rotate(clockwise);
        view.updateImage(model.getImage());
    }

    public void rotateImageByAngle(int angle) {
        model.rotateByAngle(angle);
        view.updateImage(model.getImage());
    }

    public void pickColor(int x, int y) {
        Color color = model.pickColor(x, y);
        view.displayPickedColor(color);
    }
    

    public void flipImage(boolean horizontal) {
        model.flip(horizontal);
        view.updateImage(model.getImage());
    }

    public void adjustBrightness(int brighten) {
        model.adjustBrightness(brighten);
        view.updateImage(model.getImage());
    }

    public void adjustContrast(int contrast) {
        model.adjustContrast(contrast);
        view.updateImage(model.getImage());
    }

    public void addTextToImage(String text, Font font, Color color, int x, int y) {
        model.addText(text, font, color, x, y);
        view.updateImage(model.getImage());
    }

    public static void main(String[] args) {
        ImageModel model = new ImageModel();
        ImageView view = new ImageView();
        ImageController controller = new ImageController(model, view);

        view.setController(controller);
        view.setModel(model);

        view.setVisible(true);
    }
}
