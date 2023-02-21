import java.awt.Color;

public class Box {
	int x;
	int y;
	int width;
	int height;
	double colorSD;
	boolean highlighted = false;
	
	Color color;
	
	public Box(int newX, int newY, int newWidth, int newHeight, Color newColor, double newColorSD)
	{
		x = newX;
		y = newY;
		width =newWidth;
		height = newHeight;
		color = newColor;
		colorSD = newColorSD;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getColorSD() {
		return colorSD;
	}
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}


}
