// ImageModel.java (Modèle pour manipuler l'image)

package Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


import javax.imageio.ImageIO;
import javax.swing.JLabel;

import Controller.ImageController;
import Vue.Shape;

public class ImageModel {
    private BufferedImage image;
    private ImageController controller;

    public ImageModel(ImageController controller) {
        this.controller = controller;
        this.image = image;
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


    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Point convertToImageCoordinates(int x, int y, JLabel imageLabel, BufferedImage imageTemp) {
        double scaleX = (double) image.getWidth() / imageLabel.getWidth();
        double scaleY = (double) image.getHeight() / imageLabel.getHeight();
    
        return new Point((int) (x * scaleX), (int) (y * scaleY));
    }


    public void applyPaintBucket(int x, int y, Color newColor, int tolerance, Shape shape) {
        if (image == null)
            return;

        int targetColor = image.getRGB(x, y);
        if (shape == null)
            floodFill(x, y, targetColor, newColor.getRGB(), tolerance);
        else
            floodFillShape(x, y, targetColor, newColor.getRGB(), tolerance, shape);
    }

    public void floodFill(int x, int y, int targetColor, int newColor, int tolerance) {
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int px = p.x;
            int py = p.y;

            if (px < 0 || py < 0 || px >= image.getWidth() || py >= image.getHeight())
                continue;
            if (visited[px][py])
                continue;

            int currentColor = image.getRGB(px, py);
            if (!isWithinTolerance(targetColor, currentColor, tolerance))
                continue;

            image.setRGB(px, py, newColor);
            visited[px][py] = true;

            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }

    public void floodFillShape(int x, int y, int targetColor, int newColor, int tolerance, Shape shape) {
        if (targetColor == newColor) return;  // Pas besoin de remplir si la couleur cible est identique
    
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int px = p.x;
            int py = p.y;

            if (px < 0 || py < 0 || px >= image.getWidth() || py >= image.getHeight())
                continue;
            if (visited[px][py])
                continue;

            int currentColor = image.getRGB(px, py);
            if (!isWithinTolerance(targetColor, currentColor, tolerance))
                continue;

            if (shape.contains(px, py)) {
                image.setRGB(px, py, newColor);
                visited[px][py] = true;

                queue.add(new Point(px + 1, py));
                queue.add(new Point(px - 1, py));
                queue.add(new Point(px, py + 1));
                queue.add(new Point(px, py - 1));
            }
        }
    }

    private boolean isWithinTolerance(int color1, int color2, int tolerance) {
        int r1 = (color1 / 256) / 256;
        int g1 = (color1 / 256) % 256;
        int b1 = color1 % 256;

        int r2 = (color2 / 256) / 256;
        int g2 = (color2 / 256) % 256;
        int b2 = color2 % 256;

        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2)) < tolerance;
    }

    // Appliquer une rotation
    public BufferedImage rotateImage(BufferedImage source, boolean clockwise) {
        // Translation et rotation selon le sens de rotation
        if (clockwise) {
            // Rotation horaire : tourner et déplacer l'image
            return rotateImageInverse(source, 90);

        } else {
            // Rotation antihoraire : tourner et déplacer l'image
            return rotateImageInverse(source, -90);
        }
    }

    public Color pickColor(int x, int y) {
        if (image == null || x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return null; // Coordonnées invalides ou image absente
        }
        int rgb = image.getRGB(x, y);
        return new Color(rgb);
    }

    public BufferedImage rotateImageInverse(BufferedImage source, double angleDegrees)
	{
		int width = source.getWidth();
		int height = source.getHeight();
		
		int i0 = width / 2;
		int j0 = height / 2;

		double angleRadians = Math.toRadians(angleDegrees);
		double cosAngle = Math.cos(angleRadians);
		double sinAngle = Math.sin(angleRadians);

		BufferedImage destination = new BufferedImage(width, height, source.getType());

		for (int iPrime = 0; iPrime < width; iPrime++)
		{
			for (int jPrime = 0; jPrime < height; jPrime++)
			{
				int xPrime = iPrime - i0;
				int yPrime = jPrime - j0;

				int x = (int) Math.round(xPrime * cosAngle - yPrime * sinAngle);
				int y = (int) Math.round(xPrime * sinAngle + yPrime * cosAngle);

				int i = x + i0;
				int j = y + j0;

				if (i >= 0 && i < width && j >= 0 && j < height)
					destination.setRGB(iPrime, jPrime, source.getRGB(i, j));
			}
		}
		
		return destination;
	}

    public BufferedImage flipImage(BufferedImage image, boolean horizontal) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int xNew = horizontal ? width - x - 1 : x;
                int yNew = horizontal ? y : height - y - 1;
                flippedImage.setRGB(xNew, yNew, image.getRGB(x, y));
            }
        }

        return flippedImage;
    }

    // Ajuster la luminosité pixel par pixel
    public BufferedImage adjustBrightness(BufferedImage image, int value) {
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
        return image;
    }

    // Ajuster le contraste pixel par pixel
    public BufferedImage adjustContrast(BufferedImage image, int value) {
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
        return image;
    }

    // Méthode pour limiter la valeur entre 0 et 255
    private int clamp(int value) {
        return Math.min(Math.max(value, 0), 255);
    }

    public void addText(String text, Font font, Color color, int x, int y) {
        if (image == null)
            return;
        Graphics2D g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text, x, y);
        g2d.dispose();
    }
}
