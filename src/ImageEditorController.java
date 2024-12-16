// --- CONTROLLER ---
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

class ImageEditorController {
    private ImageModel model;
    private ImageEditorView view;

    public ImageEditorController(ImageModel model, ImageEditorView view) {
        this.model = model;
        this.view = view;

        view.addToolBarButton("Load Image", e -> loadImage());
        view.addToolBarButton("Save Image", e -> saveImage());
        view.addToolBarButton("Crop", e -> cropImage());
        view.addToolBarButton("Remove Background", e -> removeBackground());
        view.addToolBarButton("Add Text", e -> addText());
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(view.getFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                model.loadImage(fileChooser.getSelectedFile().getAbsolutePath());
                view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
                JOptionPane.showMessageDialog(view.getFrame(), "Image loaded successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Error loading image: " + e.getMessage());
            }
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(view.getFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                model.saveImage(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(view.getFrame(), "Image saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Error saving image: " + e.getMessage());
            }
        }
    }

    private void cropImage() {
        // Example rectangle for cropping (replace with actual selection logic)
        Rectangle selection = new Rectangle(50, 50, 200, 200);
        model.crop(selection);
        view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
        JOptionPane.showMessageDialog(view.getFrame(), "Image cropped successfully!");
    }

    private void removeBackground() {
        Color targetColor = JColorChooser.showDialog(view.getFrame(), "Choose Background Color", Color.WHITE);
        if (targetColor != null) {
            model.removeBackground(targetColor);
            view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
            JOptionPane.showMessageDialog(view.getFrame(), "Background removed successfully!");
        }
    }

    private void addText() {
        String text = JOptionPane.showInputDialog(view.getFrame(), "Enter your text:");
        if (text != null && !text.isEmpty()) {
            model.addText(text, new Font("Arial", Font.BOLD, 24), Color.BLUE, 50, 50);
            view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
            JOptionPane.showMessageDialog(view.getFrame(), "Text added successfully!");
        }
    }
}