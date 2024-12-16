// --- VIEW ---
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

class ImageEditorView {
    private JFrame frame;
    private JLabel imageLabel;
    private JToolBar toolBar;

    public ImageEditorView() {
        frame = new JFrame("PixelCraft Studio");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        imageLabel = new JLabel();
        scrollPane.setViewportView(imageLabel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        toolBar = new JToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public void addToolBarButton(String name, ActionListener listener) {
        JButton button = new JButton(name);
        button.addActionListener(listener);
        toolBar.add(button);
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public JFrame getFrame() {
        return frame;
    }
}