import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageEditorTool {
    public static void main(String[] args) {
        ImageModel model = new ImageModel();
        ImageEditorView view = new ImageEditorView();
        new ImageEditorController(model, view);
    }
}