package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Shape {
    private int x, y, width, height;
    private boolean isRectangle; // true pour rectangle, false pour cercle
    private Color color;
    private RenderText renderText = null;
    private boolean isOver = false;

    public Shape(int x, int y, int width, int height, boolean isRectangle, Color color, RenderText renderText) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isRectangle = isRectangle;
        this.color = color;
        this.renderText = renderText;
    }

	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		if (isRectangle) {
			g2d.drawRect(x, y, width, height);
		} else {
			g2d.drawOval(x, y, width, height);
		}
	}

	public boolean contains(int px, int py) {
		if (isRectangle) {
			return px >= x && px <= x + width && py >= y && py <= y + height;
		} else {
			int centerX = x + width / 2;
			int centerY = y + height / 2;
			double radius = width / 2.0;
			return Math.pow(px - centerX, 2) + Math.pow(py - centerY, 2) <= Math.pow(radius, 2);
		}
	}

	public void moveTo(int newX, int newY) {
		x = newX;
		y = newY;
	}

	public void resize(int newWidth, int newHeight) {
		width = newWidth;
		height = newHeight;
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

    public boolean isRectangle() {
        return isRectangle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public RenderText getRenderText() {
        return renderText;
    }

    public void setRenderText(RenderText renderText) {
        this.renderText = renderText;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }
}