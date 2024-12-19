package Vue;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TextInput extends JPanel {
    private final JTextField textField;
    private final JComboBox<String> fontComboBox;
    private final JSpinner fontSizeSpinner;
    private final JCheckBox boldCheckBox;
    private final JCheckBox italicCheckBox;
    private final JButton colorButton;
    private final JPanel colorPreview;
    private Color selectedColor = Color.BLACK;
    private final JCheckBox textureCheckBox;
    private final JButton textureButton;
    private final JPanel texturePreview;
    private final BufferedImage[] textureImage = new BufferedImage[1];

    public TextInput() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Champ de texte
        textField = new JTextField(20);
        add(new JLabel("Entrez le texte de votre choix :"));
        add(textField);

        // Sélecteur de police
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontComboBox = new JComboBox<>(fonts);
        add(new JLabel("Choisissez la police :"));
        add(fontComboBox);

        // Sélecteur de taille de police
        SpinnerModel sizeModel = new SpinnerNumberModel(20, 1, 100, 1);
        fontSizeSpinner = new JSpinner(sizeModel);
        add(new JLabel("Choisissez la taille de la police :"));
        add(fontSizeSpinner);

        // Options de style : Gras et Italique
        boldCheckBox = new JCheckBox("Gras");
        italicCheckBox = new JCheckBox("Italique");
        add(new JLabel("Options de style :"));
        add(boldCheckBox);
        add(italicCheckBox);

        // Sélecteur de couleur
        colorButton = new JButton("Choisir la couleur");
        add(new JLabel("Choisissez la couleur :"));

        // Aperçu de la couleur choisie
        colorPreview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (textureCheckBox.isSelected()) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(selectedColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        colorPreview.setPreferredSize(new Dimension(100, 100));
        colorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(colorPreview);
        add(colorButton);

        // Case à cocher pour activer/désactiver la texture
        textureCheckBox = new JCheckBox("Activer la texture");
        textureCheckBox.setSelected(false);

        // Listener pour sélectionner une couleur
        colorButton.addActionListener(e -> {
            if (!textureCheckBox.isSelected()) {
                Color color = JColorChooser.showDialog(this, "Sélectionner une couleur", selectedColor);
                if (color != null) {
                    selectedColor = color;
                    colorPreview.repaint(); // Mise à jour de l'aperçu
                }
            }
        });

        add(textureCheckBox);

        // Bouton pour importer une texture
        textureButton = new JButton("Importer une texture");
        textureButton.setEnabled(false);
        add(new JLabel("Ou choisissez une texture :"));

        // Aperçu de la texture choisie
        texturePreview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (textureImage[0] != null) {
                    g.drawImage(textureImage[0], 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        };
        texturePreview.setPreferredSize(new Dimension(100, 100));
        texturePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(texturePreview);
        add(textureButton);

        // Listener pour sélectionner une texture
        textureButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    textureImage[0] = ImageIO.read(fileChooser.getSelectedFile());
                    texturePreview.repaint();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la texture.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Activer/Désactiver la texture
        textureCheckBox.addActionListener(e -> {
            boolean isTextureEnabled = textureCheckBox.isSelected();
            textureButton.setEnabled(isTextureEnabled);
            texturePreview.setVisible(isTextureEnabled);
            colorButton.setEnabled(!isTextureEnabled);
            colorPreview.repaint(); // Mise à jour de l'aperçu de la couleur
        });
    }

    public String getTextInput() {
        return textField.getText();
    }

    public Font getSelectedFont() {
        String selectedFont = (String) fontComboBox.getSelectedItem();
        int fontSize = (Integer) fontSizeSpinner.getValue();
    
        // Récupérer le style (Gras, Italique, ou les deux)
        int style = Font.PLAIN;
        if (boldCheckBox.isSelected()) {
            style |= Font.BOLD;
        }
        if (italicCheckBox.isSelected()) {
            style |= Font.ITALIC;
        }
    
        try {
            // Vérifier si la police sélectionnée supporte le style
            return new Font(selectedFont, style, fontSize);
        } catch (Exception e) {
            // Fallback si le style n'est pas supporté
            return new Font("Serif", style, fontSize);
        }
    }
    

    public Color getSelectedColor() {
        return selectedColor;
    }

    public BufferedImage getSelectedTexture() {
        return textureImage[0];
    }
}
