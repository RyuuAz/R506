package Vue;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RenderText {
	
	private int x, y,size;
	private String text;
	private Color color;
	private Font font = new Font("Arial", Font.PLAIN, 12);

	public RenderText(int x, int y, String text,Font font ,Color color, int size) {
		this.x = x;
		this.y = y;
		this.text = text;
		this.color = color;
		this.font = font;
		this.size = size;
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.drawString(text, x, y);
	}

	public boolean contains(int px, int py) {
		return px >= x-50 && px <= x +size  + 50 && py >= y - 50 && py <= y+50;
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


}
