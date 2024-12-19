// ImageModel.java (Modèle pour manipuler l'image)

package Model;

import Controller.ImageController;
import Vue.Shape;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class ImageModel {
	private ImageController controller;

	public ImageModel(ImageController controller) {
		this.controller = controller;
	}

	public BufferedImage loadImageFromFile(File file) throws IOException {
		BufferedImage image = readImageFromFile(file);
		return image;
	}

	private BufferedImage readImageFromFile(File file) throws IOException {
		return ImageIO.read(file);
	}

	public void saveImageToFile(BufferedImage image, File file, String format) throws IOException {
		if (image != null) {
			ImageIO.write(image, format, file);
		}
	}

	public Point convertToImageCoordinates(int x, int y, JLabel imageLabel, BufferedImage imageTemp) {
		int offsetX = (imageLabel.getWidth() - imageTemp.getWidth()) / 2;
		int offsetY = (imageLabel.getHeight() - imageTemp.getHeight()) / 2;
		int imageX = x - offsetX;
		int imageY = y - offsetY;
		return new Point(imageX, imageY);
	}

	public Point convertToLabelCoordinates(int x, int y, JLabel imageLabel, BufferedImage imageTemp) {
		int offsetX = (imageLabel.getWidth() - imageTemp.getWidth()) / 2;
		int offsetY = (imageLabel.getHeight() - imageTemp.getHeight()) / 2;
		int labelX = x + offsetX;
		int labelY = y + offsetY;
		return new Point(labelX, labelY);
	}

	public BufferedImage applyPaintBucket(BufferedImage image, int x, int y, Color newColor, int tolerance, Shape shape,
			JLabel imageLabel) {
		if (image == null)
			return null;

		int targetColor = image.getRGB(x, y);

		if (shape == null)
			return floodFill(image, x, y, targetColor, newColor.getRGB(), tolerance);
		else
			return floodFillShape(image, x, y, targetColor, newColor.getRGB(), tolerance, shape, imageLabel);
	}

	public BufferedImage floodFill(BufferedImage image, int x, int y, int targetColor, int newColor, int tolerance) {
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

		return image;
	}

	public BufferedImage floodFillShape(BufferedImage image, int x, int y, int targetColor, int newColor, int tolerance,
			Shape shape, JLabel imageLabel) {
		if (targetColor == newColor)
			return null; // Pas besoin de remplir si la couleur cible est identique

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

			Point tmpPoint = convertToLabelCoordinates(px, py, imageLabel, image);

			if (shape.contains(tmpPoint.x, tmpPoint.y)) {
				image.setRGB(px, py, newColor);
				visited[px][py] = true;

				queue.add(new Point(px + 1, py));
				queue.add(new Point(px - 1, py));
				queue.add(new Point(px, py + 1));
				queue.add(new Point(px, py - 1));
			}
		}

		return image;
	}

    public boolean isWithinTolerance(int color1, int color2, int tolerance) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
        return distance < tolerance;
    }

    public boolean isWithinToleranceC(Color color1, Color color2, int tolerance) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
        return distance <= tolerance;
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

    public BufferedImage rotateImageInverse(BufferedImage source, double angleDegrees) {
        int width = source.getWidth();
        int height = source.getHeight();
    
        double angleRadians = Math.toRadians(angleDegrees);
    
        // Calcul de la nouvelle taille pour contenir l'image entière
        int newWidth = (int) Math.round(Math.abs(width * Math.cos(angleRadians)) + Math.abs(height * Math.sin(angleRadians)));
        int newHeight = (int) Math.round(Math.abs(height * Math.cos(angleRadians)) + Math.abs(width * Math.sin(angleRadians)));
    
        // Créer une image avec transparence pour la nouvelle taille
        BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();
    
        // Activer des rendus de haute qualité
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
        // Remplir le fond avec la transparence
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, newWidth, newHeight);
    
        // Réinitialiser la transparence pour dessiner l'image
        g2d.setComposite(AlphaComposite.SrcOver);
    
        // Centrer l'image dans la nouvelle zone
        g2d.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
    
        // Appliquer la rotation autour du centre de l'image originale
        g2d.rotate(angleRadians, width / 2.0, height / 2.0);
    
        // Dessiner l'image d'origine
        g2d.drawRenderedImage(source, null);
    
        g2d.dispose();
        return result;
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

    public int getTextWidth(String text, Font font) {
        if (text == null || font == null) {
            return 0; // Éviter les NullPointerException
        }
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        FontMetrics metrics = g2d.getFontMetrics(font);
        g2d.dispose();
        return metrics.stringWidth(text);
    }

	// Méthode pour limiter la valeur entre 0 et 255
	private int clamp(int value) {
		return Math.min(Math.max(value, 0), 255);
	}
}
