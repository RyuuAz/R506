package ViewLouis.FramePrincipal;

// Importations nécessaires
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.FlatSearchIcon;

// Classe pour la zone de travail (JPanel)
class MainPanel extends JPanel {
    public MainPanel() {
        // Configuration du panneau principal
        setBackground(Color.LIGHT_GRAY);
        setLayout(new BorderLayout());

        // Création d'un panneau pour contenir la barre de menu et la barre d'outils
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Empiler verticalement les éléments

        // Création de la barre de menu
        JMenuBar menuBar = createMenuBar();
        topPanel.add(menuBar);

        // Création de la barre d'outils
        JToolBar toolBar = createToolBar();
        topPanel.add(toolBar);

        // Ajouter le panneau contenant la barre de menu et la barre d'outils dans le nord
        add(topPanel, BorderLayout.NORTH);

        // Exemple d'image avec barre de défilement
        JLabel imageLabel = new JLabel("Zone de travail", JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        imageLabel.setForeground(Color.DARK_GRAY);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Fichier");

        JMenuItem openItem = new JMenuItem("Ouvrir");
        openItem.addActionListener(e -> onOpen());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Enregistrer");
        saveItem.addActionListener(e -> onSave());
        fileMenu.add(saveItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    // Méthodes pour gérer les actions du menu
    private void onOpen() {
        JFileChooser fileChooser = new JFileChooser();

        // Créer un filtre de fichiers pour les formats d'image
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
            "Images", "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp");

        // Appliquer le filtre au file chooser
        fileChooser.setFileFilter(imageFilter);

        // Afficher la boîte de dialogue pour ouvrir un fichier
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Fichier ouvert : " + fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onSave() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Fichier enregistré : " + fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Création de la barre d'outils avec des icônes FlatLaf
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Barre d'outils");
        toolBar.setOrientation(JToolBar.HORIZONTAL); // Définir l'orientation en horizontal

        // Bouton pour le "Saut de peinture" avec une icône
        JButton paintJumpButton = new JButton("Saut de peinture");
        paintJumpButton.setIcon(new FlatSearchIcon()); // Utilisation d'une icône de FlatLaf
        paintJumpButton.addActionListener(e -> onPaintJump());
        toolBar.add(paintJumpButton);

        // Bouton pour la "Pipette" avec une icône (remplacé par une autre icône)
        JButton pipetteButton = new JButton("Pipette");
        pipetteButton.setIcon(new FlatSearchIcon()); // Utilisation d'une autre icône FlatLaf
        pipetteButton.addActionListener(e -> onPipette());
        toolBar.add(pipetteButton);

        // Séparateur
        toolBar.addSeparator();

        // Bouton pour "Rotation 90°" avec une icône
        JButton rotate90Button = new JButton("Rotation 90°");
        //rotate90Button.setIcon(); // Icône de rotation de FlatLaf
        rotate90Button.addActionListener(e -> onRotate(90));
        toolBar.add(rotate90Button);

        // Bouton pour "Rotation 180°" avec une icône
        JButton rotate180Button = new JButton("Rotation 180°");
        //rotate180Button.setIcon(new FlatRotateLeftIcon()); // Icône de rotation gauche de FlatLaf
        rotate180Button.addActionListener(e -> onRotate(180));
        toolBar.add(rotate180Button);

        // Bouton pour "Rotation 270°" avec une icône
        JButton rotate270Button = new JButton("Rotation 270°");
        //rotate270Button.setIcon(new FlatRotateRightIcon()); // Icône de rotation droite de FlatLaf
        rotate270Button.addActionListener(e -> onRotate(270));
        toolBar.add(rotate270Button);

        return toolBar;
    }

    // Actions associées aux boutons de la barre d'outils
    private void onPaintJump() {
        // Action "Saut de peinture"
        JOptionPane.showMessageDialog(this, "Action : Saut de peinture");
    }

    private void onPipette() {
        // Action "Pipette"
        JOptionPane.showMessageDialog(this, "Action : Pipette");
    }

    private void onRotate(int angle) {
        // Action de rotation (exemple simple pour afficher l'angle)
        JOptionPane.showMessageDialog(this, "Rotation de " + angle + "°");
    }
}