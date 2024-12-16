package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import Controller.ImageController;
import Modele.ImageModel;

public class ImageView extends JFrame {
    private JLabel imageLabel;
    private ImageController controller;
    private ImageModel model;
    public ImageView() {
        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton openButton = new JButton("Open");
        JButton saveButton = new JButton("Save");
        JButton paintBucketButton = new JButton("Paint Bucket");
        JButton rotateButtonLeft = new JButton("Rotate left");
        JButton rotatebuttonRight = new JButton("Rotate right");
        JButton rotateCustomButton = new JButton("Rotate Custom");
        JButton flipHorizontalButton = new JButton("Flip Horizontal");
        JButton flipVerticalButton = new JButton("Flip Vertical");
        JButton brightenButtonPlus = new JButton("ec +");
        JButton brightenButtonMoin = new JButton("ec -");
        JButton darkenButtonPlus = new JButton("as +");
        JButton darkenButtonMoin = new JButton("as -");

        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(paintBucketButton);
        toolBar.add(rotateButtonLeft);
        toolBar.add(rotatebuttonRight);
        toolBar.add(rotateCustomButton);
        toolBar.add(flipHorizontalButton);
        toolBar.add(flipVerticalButton);
        toolBar.add(brightenButtonPlus);
        toolBar.add(new JLabel("Brightness:"));
        toolBar.add(brightenButtonMoin);
        toolBar.add(darkenButtonPlus);
        toolBar.add(new JLabel("Contrast:"));
        toolBar.add(darkenButtonMoin);


        add(toolBar, BorderLayout.NORTH);

        openButton.addActionListener(this::handleOpenImage);
        saveButton.addActionListener(this::handleSaveImage);

        paintBucketButton.addActionListener(e -> {
            if (controller != null) {
                controller.applyPaintBucket(10, 10, Color.RED, 50); // Exemple
            }
        });

        rotateButtonLeft.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(true);
            }
        });

        rotatebuttonRight.addActionListener(e -> {
            if (controller != null) {
                controller.rotate(false);
            }
        });

        rotateCustomButton.addActionListener(e -> {
            if (controller != null) {
                String angleStr = JOptionPane.showInputDialog(this, "Enter rotation angle (degrees):", "Rotate Custom", JOptionPane.PLAIN_MESSAGE);
                try {
                    int angle = Integer.parseInt(angleStr);
                    controller.rotateImageByAngle(angle);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input! Please enter a numeric value.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flipHorizontalButton.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(true);
            }
        });

        flipVerticalButton.addActionListener(e -> {
            if (controller != null) {
                controller.flipImage(false);
            }
        });

        brightenButtonPlus.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(10);
            }
        });

        brightenButtonMoin.addActionListener(e -> {
            if (controller != null) {
                controller.adjustBrightness(-10);
            }
        });

        darkenButtonPlus.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(10);
            }
        });

        darkenButtonMoin.addActionListener(e -> {
            if (controller != null) {
                controller.adjustContrast(-10);
            }
        });

    }

    private void handleOpenImage(ActionEvent e) {
        if (controller != null) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                controller.openImage(fileChooser.getSelectedFile());
            }
        }
    }

    private void handleSaveImage(ActionEvent e) {
        if (controller != null) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                controller.saveImage(fileChooser.getSelectedFile());
            }
        }
    }

    public void setController(ImageController controller) {
        this.controller = controller;
    }

    public void setModel(ImageModel model) {
        this.model = model;
    }

    public void updateImage(BufferedImage image) {
        imageLabel.setIcon(new ImageIcon(image));
    }

    public void init() {
        setVisible(true);
    }
}
