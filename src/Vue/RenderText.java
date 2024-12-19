package Vue;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;

public class RenderText {

	private int x, y, size;
	private String text;
	private Color color;
	private Font font = new Font("Arial", Font.PLAIN, 20);
	private TexturePaint texturePaint = null;

	public RenderText(int x, int y, String text, Font font, Color color, int size) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.font = font;
		this.size = size;
	}

	public void setTexture(TexturePaint texturePaint) {
		this.texturePaint = texturePaint;
	}

	public void draw(Graphics2D g2d) {
		if (texturePaint != null) {
			// Si une texture est définie, on l'utilise pour dessiner le texte
			g2d.setPaint(texturePaint);
		} else {
			// Sinon, on utilise la couleur normale
			g2d.setColor(color);
		}
		g2d.setFont(font);

		// Utilisation de TextLayout pour gérer les différentes tailles et positions de
		// texte avec précision
		TextLayout layout = new TextLayout(text, font, g2d.getFontRenderContext());
		layout.draw(g2d, x, y);
	}

	public void draw(Graphics2D g2d, int x, int y) {
		if (texturePaint != null) {
			// Si une texture est définie, on l'utilise pour dessiner le texte
			g2d.setPaint(texturePaint);
		} else {
			// Sinon, on utilise la couleur normale
			g2d.setColor(color);
		}
		g2d.setFont(font);

		// Utilisation de TextLayout pour gérer les différentes tailles et positions de
		// texte avec précision
		TextLayout layout = new TextLayout(text, font, g2d.getFontRenderContext());
		layout.draw(g2d, x, y);
	}

	public boolean contains(int px, int py) {
		// On peut définir une zone d'interaction (par exemple, les bords du texte)
		return px >= x - 50 && px <= x + size + 50 && py >= y - 50 && py <= y + 50;
	}

	public void moveTo(int newX, int newY) {
		x = newX;
		y = newY;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}