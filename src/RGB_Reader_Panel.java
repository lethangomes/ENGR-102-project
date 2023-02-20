import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class RGB_Reader_Panel extends JPanel {
	ArrayList<Box> boxes = new ArrayList<Box>();
	BufferedImage image = null;
	
	boolean selectionBoxActive = false;
	int selectionBoxX1;
	int selectionBoxY1;
	int selectionBoxX2;
	int selectionBoxY2;
	
	public RGB_Reader_Panel()
	{
		super();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
		
		//draws boxes
		for(int i = 0; i < boxes.size(); i++)
		{
			Box currentBox = boxes.get(i);
			Color boxColor = currentBox.getColor();
			g.drawRect(currentBox.getX(), currentBox.getY(), currentBox.getWidth(), currentBox.getHeight());
			
			g.setColor(Color.white);
			g.drawRect(currentBox.getX() + 1, currentBox.getY() + 1, currentBox.getWidth() - 2, currentBox.getHeight() - 2);
			g.fillRect(currentBox.getX(), currentBox.getY() - 20, 120, 20);
			
			g.setColor(new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), 100));
			g.fillRect(currentBox.getX() + 2, currentBox.getY() + 2, currentBox.getWidth() - 4, currentBox.getHeight() - 4);
			
			g.setColor(Color.black);
			g.drawString("RGB: " + boxColor.getRed() + ", " + boxColor.getGreen() + ", " + boxColor.getBlue(),currentBox.getX() , currentBox.getY() - 5);
		}
		
		if(selectionBoxActive)
		{
			int[] selectionBoxParameters = createBoxParameters(selectionBoxX1, selectionBoxY1, selectionBoxX2, selectionBoxY2);
			g.drawRect(selectionBoxParameters[0], selectionBoxParameters[1], selectionBoxParameters[2], selectionBoxParameters[3]);
			
			g.setColor(Color.white);
			g.drawRect(selectionBoxParameters[0] + 1, selectionBoxParameters[1] + 1, selectionBoxParameters[2] - 2, selectionBoxParameters[3] - 2);
		}
		
	}

	public void addBox(int x1, int y1, int x2, int y2) {
		int[] boxValues = createBoxParameters(x1, y1, x2, y2);
		int startX = boxValues[0];
		int startY = boxValues[1];
		int boxWidth = boxValues[2];
		int boxHeight = boxValues[3];
		
		int[] rgbValues = image.getRGB(startX, startY, boxWidth, boxHeight, null, 0, boxWidth);
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		int redSD = 0;
		int greenSD = 0;
		int blueSD = 0;
		
		for(int i = 0; i < rgbValues.length; i++)
		{
			Color color = new Color(rgbValues[i]);
			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
		}
		red /= rgbValues.length;
		green /= rgbValues.length;
		blue /= rgbValues.length;
		
		for(int i = 0; i < rgbValues.length; i++)
		{
			Color color = new Color(rgbValues[i]);
			redSD += Math.pow(color.getRed() - red, 2);
			greenSD += Math.pow(color.getGreen() - green, 2);
			blueSD += Math.pow(color.getBlue() - blue, 2);
		}
		
		
		System.out.println("Red: " + red + ", Green: " +  green + ", Blue: " + blue);
		
		boxes.add(new Box(startX, 
				startY, 
				boxWidth, 
				boxHeight, 
				new Color(red, green, blue),
				Math.sqrt(((redSD/(rgbValues.length -1)) +  (greenSD/(rgbValues.length -1)) + (blueSD/(rgbValues.length -1)))/3)));
	}
	
	public void startSelectionBox(int x, int y)
	{
		selectionBoxActive = true;
		selectionBoxX1 = x;
		selectionBoxY1 = y;
		selectionBoxX2 = x;
		selectionBoxY2 = y;
	}
	
	public void updateSelectionBox(int x, int y)
	{
		selectionBoxX2 = x;
		selectionBoxY2 = y;
	}
	
	public void endSelectionBox()
	{
		selectionBoxActive = false;
	}
	
	public int[] createBoxParameters(int x1, int y1, int x2, int y2)
	{
		int leftmostX;
		int rightmostX;
		int topY;
		int bottomY;
		
		if(x1 > x2)
		{
			leftmostX = x2;
			rightmostX = x1;
		}
		else
		{
			leftmostX = x1;
			rightmostX = x2;
		}
		
		if(y1 > y2)
		{
			topY = y2;
			bottomY = y1;
		}
		else
		{
			topY = y1;
			bottomY = y2;
		}
		
		int[] parameters = {leftmostX, topY, rightmostX - leftmostX , bottomY - topY};
		
		return parameters;
	}
	
	public Box findBox(int x, int y)
	{
		for (int i = 0; i < boxes.size(); i++) 
		{
			Box currentBox = boxes.get(i);
			if(currentBox.getX() < x &&
					currentBox.getX() + currentBox.getWidth() > x &&
					currentBox.getY() < y &&
					currentBox.getY() + currentBox.getHeight() > y)
			{
				return currentBox;
			}
		}
		
		return null;
	}
	
	public void setImage(BufferedImage newImage)
	{
		image = newImage;
	}
	

}
