// --- MODEL ---
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

class ImageModel {
    private BufferedImage image;
    private String imagePath;

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void crop(Rectangle selection) {
        if (image != null && selection != null) {
            int x = Math.max(0, selection.x);
            int y = Math.max(0, selection.y);
            int width = Math.min(selection.width, image.getWidth() - x);
            int height = Math.min(selection.height, image.getHeight() - y);
            image = image.getSubimage(x, y, width, height);
        }
    }

    public void removeBackground(Color targetColor) {
        if (image != null && targetColor != null) {
            int targetRGB = targetColor.getRGB();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) == targetRGB) {
                        image.setRGB(x, y, 0x00FFFFFF); // Transparent
                    }
                }
            }
        }
    }

    public void adjustBrightness(float factor) {
        if (image != null) {
            RescaleOp op = new RescaleOp(factor, 0, null);
            image = op.filter(image, null);
        }
    }

    public void addText(String text, Font font, Color color, int x, int y) {
        if (image != null && text != null) {
            Graphics2D g2d = image.createGraphics();
            g2d.setFont(font);
            g2d.setColor(color);
            g2d.drawString(text, x, y);
            g2d.dispose();
        }
    }

    public void saveImage(String outputPath) throws IOException {
        if (image != null) {
            ImageIO.write(image, "png", new File(outputPath));
        }
    }

    public void loadImage(String path) throws IOException {
        image = ImageIO.read(new File(path));
        imagePath = path;
    }
}