// --- CONTROLLER ---
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.RescaleOp;


class ImageEditorController {
    private ImageModel model;
    private ImageEditorView view;

    public ImageEditorController(ImageModel model, ImageEditorView view) {
        this.model = model;
        this.view = view;

        view.addToolBarButton("Charger Image", e -> loadImage());
        view.addToolBarButton("Sauvegarder Image", e -> saveImage());
        view.addToolBarButton("Recadrer", e -> cropImage());
        view.addToolBarButton("Supprimer Arrière-plan", e -> removeBackground());
        view.addToolBarButton("Ajouter Texte", e -> addText());
        view.addToolBarButton("Ajuster Luminosité", e -> toggleBrightnessSlider());

        view.getBrightnessSlider().addChangeListener(e -> adjustBrightness());
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(view.getFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                model.loadImage(fileChooser.getSelectedFile().getAbsolutePath());
                view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
                JOptionPane.showMessageDialog(view.getFrame(), "Image chargée avec succès !");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(view.getFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                model.saveImage(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(view.getFrame(), "Image sauvegardée avec succès !");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view.getFrame(), "Erreur lors de la sauvegarde de l'image : " + e.getMessage());
            }
        }
    }

    private void cropImage() {
        // Exemple de rectangle pour recadrer (à remplacer par la logique de sélection réelle)
        Rectangle selection = new Rectangle(50, 50, 200, 200);
        model.crop(selection);
        view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
        JOptionPane.showMessageDialog(view.getFrame(), "Image recadrée avec succès !");
    }

    private void removeBackground() {
        Color targetColor = JColorChooser.showDialog(view.getFrame(), "Choisissez la couleur d'arrière-plan", Color.WHITE);
        if (targetColor != null) {
            model.removeBackground(targetColor);
            view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
            JOptionPane.showMessageDialog(view.getFrame(), "Arrière-plan supprimé avec succès !");
        }
    }

    private void addText() {
        String text = JOptionPane.showInputDialog(view.getFrame(), "Entrez votre texte :");
        if (text != null && !text.isEmpty()) {
            model.addText(text, new Font("Arial", Font.BOLD, 24), Color.BLUE, 50, 50);
            view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
            JOptionPane.showMessageDialog(view.getFrame(), "Texte ajouté avec succès !");
        }
    }

    private void toggleBrightnessSlider() {
        boolean isVisible = view.getSliderPanel().isVisible();
        view.getSliderPanel().setVisible(!isVisible);
    }

    private void adjustBrightness() {
        int sliderValue = view.getBrightnessSlider().getValue();
        float factor = sliderValue / 100.0f;
        model.adjustBrightness(factor);
        view.getImageLabel().setIcon(new ImageIcon(model.getImage()));
    }
}