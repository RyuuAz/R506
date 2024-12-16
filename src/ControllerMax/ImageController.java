package ControllerMax;

import java.io.File;

import javax.swing.JOptionPane;

import ModeleMax.ImageModel;
import VueMax.ImageView;

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
        if (image == null) return;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage rotatedImage = new BufferedImage(height, width, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        if (clockwise) {
            g2d.rotate(Math.toRadians(90), height / 2.0, height / 2.0);
            g2d.drawImage(image, 0, -height, null);
        } else {
            g2d.rotate(Math.toRadians(-90), width / 2.0, width / 2.0);
            g2d.drawImage(image, -width, 0, null);
        }
        g2d.dispose();
        image = rotatedImage;
    }

    public void rotateImageByAngle(int angle) {
        model.rotateByAngle(angle);
        view.updateImage(model.getImage());
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
