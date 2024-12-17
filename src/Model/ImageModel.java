// ImageModel.java (Modèle pour manipuler l'image)

package Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Controller.ImageController;


public class ImageModel {
    private BufferedImage image;
    private ImageController controller;

    public void setController(ImageController controller) {
        this.controller = controller;
    }

    public void loadImageFromFile(File file) throws IOException {
        image = ImageIO.read(file);
    }

    public void saveImageToFile(File file, String format) throws IOException {
        if (image != null) {
            ImageIO.write(image, format, file);
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void applyPaintBucket(int x, int y, Color newColor, int tolerance) {
        if (image == null) return;
        
        int targetColor = image.getRGB(x, y);
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        floodFill(x, y, targetColor, newColor.getRGB(), tolerance, visited);
    }

    private void floodFill(int x, int y, int targetColor, int newColor, int tolerance, boolean[][] visited) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return;
        if (visited[x][y]) return;
        visited[x][y] = true;

        int currentColor = image.getRGB(x, y);
        if (!isWithinTolerance(targetColor, currentColor, tolerance)) return;

        image.setRGB(x, y, newColor);

        floodFill(x + 1, y, targetColor, newColor, tolerance, visited);
        floodFill(x - 1, y, targetColor, newColor, tolerance, visited);
        floodFill(x, y + 1, targetColor, newColor, tolerance, visited);
        floodFill(x, y - 1, targetColor, newColor, tolerance, visited);
    }

    private boolean isWithinTolerance(int color1, int color2, int tolerance) {
        int r1 = (color1 / 256) / 256;
		int g1 = (color1 / 256) % 256;
		int b1 = color1 % 256;

		int r2 = (color2 / 256) / 256;
		int g2 = (color2 / 256) % 256;
		int b2 = color2 % 256;

		return Math.sqrt(Math.pow(r1-r2, 2) + Math.pow(g1-g2, 2) + Math.pow(b1-b2, 2)) < tolerance;
    }

    // Appliquer une rotation
    public void rotate(boolean clockwise) {
        int w = image.getWidth();
        int h = image.getHeight();

        // Créer une nouvelle image avec des dimensions inversées pour 90° de rotation
        BufferedImage rotatedImage = new BufferedImage(h, w, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();

        // Translation et rotation selon le sens de rotation
        if (clockwise) {
            // Rotation horaire : tourner et déplacer l'image
            g2d.translate(h, 0);
            g2d.rotate(Math.toRadians(90));
        } else {
            // Rotation antihoraire : tourner et déplacer l'image
            g2d.translate(0, w);
            g2d.rotate(Math.toRadians(-90));
        }

        // Dessiner l'image d'origine sur le contexte graphique tourné
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Mettre à jour l'image après rotation
        this.image = rotatedImage;
    }

    public Color pickColor(int x, int y) {
        if (image == null || x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return null; // Coordonnées invalides ou image absente
        }
        int rgb = image.getRGB(x, y);
        return new Color(rgb);
    }


    public void rotateByAngle(int angle) {
        if (image == null) return;
    
        int width = image.getWidth();
        int height = image.getHeight();
    
        // Calcul d'une nouvelle taille pour éviter la coupe
        double radians = Math.toRadians(angle);
        int newWidth = (int) Math.round(Math.abs(width * Math.cos(radians)) + Math.abs(height * Math.sin(radians)));
        int newHeight = (int) Math.round(Math.abs(height * Math.cos(radians)) + Math.abs(width * Math.sin(radians)));
    
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
    
        // Translation et rotation
        g2d.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        g2d.rotate(radians, width / 2.0, height / 2.0);
        g2d.drawImage(image, 0, 0, null);
    
        g2d.dispose();
        image = rotatedImage;
    }
    

    public void flip(boolean horizontal) {
        if (image == null) return;
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = flippedImage.createGraphics();
        if (horizontal) {
            g2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
        } else {
            g2d.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
        }
        g2d.dispose();
        image = flippedImage;
    }

    // Ajuster la luminosité pixel par pixel
    public void adjustBrightness(int value) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y));

                // Récupérer les valeurs des composants RGB
                int red = c.getRed() + value;
                int green = c.getGreen() + value;
                int blue = c.getBlue() + value;

                // Limiter les valeurs pour éviter un dépassement des bornes
                red = clamp(red);
                green = clamp(green);
                blue = clamp(blue);

                // Appliquer la nouvelle couleur
                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
    }

    // Ajuster le contraste pixel par pixel
    public void adjustContrast(int value) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y));

                // Récupérer les valeurs des composants RGB
                int red = (int) (c.getRed() + value / 100.0 * (c.getRed() - 127));
                int green = (int) (c.getGreen() + value / 100.0 * (c.getGreen() - 127));
                int blue = (int) (c.getBlue() + value / 100.0 * (c.getBlue() - 127));

                // Limiter les valeurs pour éviter un dépassement des bornes
                red = clamp(red);
                green = clamp(green);
                blue = clamp(blue);

                // Appliquer la nouvelle couleur
                image.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }
    }

    // Méthode pour limiter la valeur entre 0 et 255
    private int clamp(int value) {
        return Math.min(Math.max(value, 0), 255);
    }
    

    public void addText(String text, Font font, Color color, int x, int y) {
        if (image == null) return;
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, x, y);
        g2d.dispose();
    }
}
