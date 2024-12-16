import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageEditorTool {
    private JFrame frame;
    private JLabel imageLabel;
    private BufferedImage currentImage;
    private String imagePath;

    public ImageEditorTool() {
        // Initialize UI components
        frame = new JFrame("Image Editor Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Main panel with scrolling
        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        imageLabel = new JLabel();
        scrollPane.setViewportView(imageLabel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton openButton = new JButton("Open");
        JButton saveButton = new JButton("Save");
        JButton rotateLeftButton = new JButton("Rotate Left");
        JButton rotateRightButton = new JButton("Rotate Right");
        JButton flipHorizontalButton = new JButton("Flip Horizontal");
        JButton flipVerticalButton = new JButton("Flip Vertical");
        JButton brightnessButton = new JButton("Brighten");
        JButton darkenButton = new JButton("Darken");

        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(rotateLeftButton);
        toolBar.add(rotateRightButton);
        toolBar.add(flipHorizontalButton);
        toolBar.add(flipVerticalButton);
        toolBar.add(brightnessButton);
        toolBar.add(darkenButton);
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Add action listeners
        openButton.addActionListener(e -> openImage());
        saveButton.addActionListener(e -> saveImage());
        rotateLeftButton.addActionListener(e -> rotateImage(-90));
        rotateRightButton.addActionListener(e -> rotateImage(90));
        flipHorizontalButton.addActionListener(e -> flipImage(true));
        flipVerticalButton.addActionListener(e -> flipImage(false));
        brightnessButton.addActionListener(e -> adjustBrightness(20));
        darkenButton.addActionListener(e -> adjustBrightness(-20));

        // Finalize frame setup
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                currentImage = ImageIO.read(file);
                imagePath = file.getAbsolutePath();
                imageLabel.setIcon(new ImageIcon(currentImage));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading image: " + e.getMessage());
            }
        }
    }

    private void saveImage() {
        if (currentImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(currentImage, "png", file);
                    JOptionPane.showMessageDialog(frame, "Image saved successfully.");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Error saving image: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No image to save.");
        }
    }

    private void rotateImage(int degrees) {
        if (currentImage != null) {
            int w = currentImage.getWidth();
            int h = currentImage.getHeight();
            BufferedImage rotatedImage = new BufferedImage(h, w, currentImage.getType());
            Graphics2D g2d = rotatedImage.createGraphics();
            g2d.rotate(Math.toRadians(degrees), w / 2.0, h / 2.0);
            g2d.drawImage(currentImage, 0, 0, null);
            g2d.dispose();
            currentImage = rotatedImage;
            imageLabel.setIcon(new ImageIcon(currentImage));
        } else {
            JOptionPane.showMessageDialog(frame, "No image loaded.");
        }
    }

    private void flipImage(boolean horizontal) {
        if (currentImage != null) {
            int w = currentImage.getWidth();
            int h = currentImage.getHeight();
            BufferedImage flippedImage = new BufferedImage(w, h, currentImage.getType());
            Graphics2D g2d = flippedImage.createGraphics();
            if (horizontal) {
                g2d.drawImage(currentImage, 0, 0, w, -h, null);
            } else {
                g2d.drawImage(currentImage, 0, 0, -w, h, null);
            }
            g2d.dispose();
            currentImage = flippedImage;
            imageLabel.setIcon(new ImageIcon(currentImage));
        } else {
            JOptionPane.showMessageDialog(frame, "No image loaded.");
        }
    }

    private void adjustBrightness(int amount) {
        if (currentImage != null) {
            for (int x = 0; x < currentImage.getWidth(); x++) {
                for (int y = 0; y < currentImage.getHeight(); y++) {
                    Color color = new Color(currentImage.getRGB(x, y));
                    int r = Math.min(255, Math.max(0, color.getRed() + amount));
                    int g = Math.min(255, Math.max(0, color.getGreen() + amount));
                    int b = Math.min(255, Math.max(0, color.getBlue() + amount));
                    currentImage.setRGB(x, y, new Color(r, g, b).getRGB());
                }
            }
            imageLabel.setIcon(new ImageIcon(currentImage));
        } else {
            JOptionPane.showMessageDialog(frame, "No image loaded.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageEditorTool::new);
    }
}
