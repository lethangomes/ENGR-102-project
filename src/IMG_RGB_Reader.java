import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class IMG_RGB_Reader implements ActionListener, MouseListener, MouseMotionListener, WindowListener {

	RGB_Reader_Panel panel = new RGB_Reader_Panel();
	JFrame fileChooser = new JFrame("Enter Image File Name");
	JFrame frame = new JFrame();
	
	JButton compareBoxesB = new JButton("Compare Boxes");
	JButton deleteBoxesB = new JButton("Delete Box");
	JButton changeImageB = new JButton("Change Image");
	JTextArea boxInfoField =new JTextArea("");
	JTextArea colorField = new JTextArea();
	Container east = new Container();
	
	final int NONE = 0;
	final int COMPARE_BOXES = 1;
	final int COMPARE_BOXES2 = 2;
	final int BOX_SECOND_CORNER = 3;
	final int DELETE_BOX = 4;
	final int CHANGING_IMAGE = 5;
	int state = NONE;
	
	int boxStartX = 0;
	int boxStartY = 0;
	
	BufferedImage image;
	String fileName;
	
	DecimalFormat df = new DecimalFormat("###.###");
	
	Box firstBox = null;
	
	
	Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	int screenWidth = (int)size.getWidth();
	int screenHeight = (int)size.getHeight();
	
	int frameWidthOffset = 200;
	int frameHeightOffset = 100;
	
	final JFileChooser fc = new JFileChooser();
	
	public static void main(String[] args) 
	{
		
		new IMG_RGB_Reader();
	}
	
	public IMG_RGB_Reader()
	{
		getFile();
	}
	
	public void makeFrame()
	{
		//creates window for the actual image analysis
		frame.setSize(image.getWidth() + frameWidthOffset, image.getHeight() + frameHeightOffset);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		
		//east layout
		east.setLayout(new GridLayout(5,1));
		compareBoxesB.addActionListener(this);
		compareBoxesB.setBackground(Color.gray);
		deleteBoxesB.addActionListener(this);
		deleteBoxesB.setBackground(Color.gray);
		changeImageB.addActionListener(this);
		changeImageB.setBackground(Color.gray);
		boxInfoField.setEditable(false);
		east.add(compareBoxesB);
		east.add(deleteBoxesB);
		east.add(changeImageB);
		east.add(colorField);
		east.add(boxInfoField);
		frame.add(east, BorderLayout.EAST);
	}

	@Override
	public void mouseReleased(MouseEvent e) { //when mouse released
		switch(state)
		{
		case COMPARE_BOXES: 
			
			//finds box the user clicked on
			firstBox = panel.findBox(e.getX(), e.getY());
			
			//if the user actually clicked on a box it records the box clicked and highlights it
			if(firstBox != null)
			{
				state = COMPARE_BOXES2;
				panel.resetHighlightedBoxes();
				firstBox.setHighlighted(true);
			}
			break;
		case COMPARE_BOXES2:
			
			//finds the box the user clicked on
			Box secondBox = panel.findBox(e.getX(), e.getY());
			
			//if the user actually clicked on a box calculates difference in average color of each box
			if(secondBox !=null && !secondBox.equals(firstBox))
			{
				firstBox.setHighlighted(false);
				
				//calculates difference in red, green, and blue values and the average of those differences
				int redDiff = Math.abs(firstBox.getColor().getRed() - secondBox.getColor().getRed());
				int greenDiff = Math.abs(firstBox.getColor().getGreen() - secondBox.getColor().getGreen());
				int blueDiff = Math.abs(firstBox.getColor().getBlue() - secondBox.getColor().getBlue());
				int avgDiff = (redDiff + greenDiff + blueDiff)/3;
				
				//creates pop-up window displaying results
				JOptionPane.showMessageDialog(frame, "Red difference: " + redDiff+ "\nGreen difference: " + greenDiff + "\nBlue difference: " + blueDiff + "\n\nAverage difference: " + avgDiff);
				state = COMPARE_BOXES;
			}
			break;
		case BOX_SECOND_CORNER:
			//makes sure user actually clicked image
			if(e.getX() <= image.getWidth() && e.getY() <= image.getHeight())
			{
				//creates a new box using mouse coordinates
				panel.addBox(boxStartX, boxStartY, e.getX() , e.getY());
				
				//resets state and turns off selection box
				state = NONE;
				panel.endSelectionBox();
				compareBoxesB.setBackground(Color.gray);
			}
			break;
		case DELETE_BOX:
			
			//finds the box the user clicked on
			Box boxToBeDeleted = panel.findBox(e.getX(), e.getY());
			
			//if the user actually clicked on a box, deletes the box
			if(boxToBeDeleted != null)
			{
				panel.deleteBox(boxToBeDeleted);
				
				//resets state
				state = NONE;
				deleteBoxesB.setBackground(Color.gray);
			}
			break;
		case NONE:
			
			//checks if user clicked on a box
			Box selectedBox = panel.findBox(e.getX(), e.getY());
			if(selectedBox != null)
			{
				//if user clicked a box, displays box information and color in textfield
				Color boxColor = selectedBox.getColor();
				panel.resetHighlightedBoxes();
				selectedBox.setHighlighted(true);
				colorField.setBackground(boxColor);
				boxInfoField.setText("X: " + selectedBox.getX() + 
						"\nY: " + selectedBox.getY() + 
						"\nRed: " + boxColor.getRed() + 
						"\nGreen: " + boxColor.getGreen() +
						"\nBlue: " + boxColor.getBlue() + 
						"\nSD: " + df.format(selectedBox.getColorSD()));
			}
			else
			{
				//if a box was not clicked, sets initial points for new box
				boxStartX = e.getX();
				boxStartY = e.getY();
				
				//checks if user actually clicked image
				if(boxStartX <= image.getWidth() && boxStartY <= image.getHeight())
				{
					state = BOX_SECOND_CORNER;
					
					//starts selection box
					panel.startSelectionBox(e.getX(), e.getY());
				}
				
			}
			break;
			
		}
		
		//redraws frame
		frame.repaint();
	}


	//on button pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(compareBoxesB)) //compare boxes button pressed
		{
			//if button pressed while in the compare boxes state, resets state
			if(state == COMPARE_BOXES || state == COMPARE_BOXES2)
			{
				state = NONE;
				compareBoxesB.setBackground(Color.gray);
				
			}
			else
			{
				//if button pressed while not comparing boxes, sets state to comparing boxes
				state = COMPARE_BOXES;
				compareBoxesB.setBackground(Color.green);
				deleteBoxesB.setBackground(Color.gray);
			}
		}
		if(e.getSource().equals(deleteBoxesB)) // delete box button pressed
		{
			//if button pressed in delete boxes state, resets state
			if(state == DELETE_BOX)
			{
				state = NONE;
				deleteBoxesB.setBackground(Color.gray);
			}
			else
			{
				//if button pressed while not deleting boxes, sets state to delete box
				state = DELETE_BOX;
				deleteBoxesB.setBackground(Color.green);
				compareBoxesB.setBackground(Color.gray);
			}
		}
		if(e.getSource().equals(changeImageB)) //change image button pressed
		{
			//makes file chooser window visible
			
			state = CHANGING_IMAGE;
			
			getFile();
		}
		
	}



	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {//when mouse is moved
		
		//if use is selecting second corner for a new box, update selection box
		if(state == BOX_SECOND_CORNER)
		{
			panel.updateSelectionBox(e.getX(), e.getY());
			frame.repaint();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	//Method to resize BufferedImages. Source: https://stackoverflow.com/questions/9417356/bufferedimage-resize
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	//makes sure state is NONE when filechooser window is closed
	@Override
	public void windowDeactivated(WindowEvent e) {
		state = NONE;
		
	}  
	
	
	public void readFile(File file)
	{
		try {
			//sets image to the file specified by user, then creates window to display image
			 image = ImageIO.read(file);
			 if(image != null)
			 {
				 double aspectRatio = (double)image.getWidth()/image.getHeight();
				 
				 //adjusts image if it's too wide
				 if(image.getWidth() + frameWidthOffset > screenWidth)
				 {
					 image = resize(image, screenWidth - frameWidthOffset, (int)((screenWidth - frameWidthOffset) * (1/aspectRatio)));
				 }
				 
				 //adjusts image if it's too tall
				 if(image.getHeight() + frameHeightOffset > screenHeight)
				 {
					 image = resize(image, (int)((screenHeight - frameHeightOffset) * aspectRatio), screenHeight - frameHeightOffset);
				 }
				 
				 panel.setImage(image);
				 fileChooser.setVisible(false);
				 if(state == NONE)
				 {
					 //creates frame if the frame hasn't been made yet
					 makeFrame();
					 fileChooser.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				 }
				 else if(state == CHANGING_IMAGE)
				 {
					 //updates frame size to match new image and clears all boxes
					 panel.clearBoxes();
					 frame.setSize(image.getWidth() + frameWidthOffset, image.getHeight() + frameHeightOffset);
					 state = NONE;
				 }
			 }
			 else
			 {
				 JOptionPane.showMessageDialog(frame, "Invalid file");
				 getFile();
			 }
		} catch (IOException ee) {
			//if the file was not found, or is not readable notifies user.
			ee.printStackTrace();
			
		}
	}
	
	public void getFile()
	{
		//opens file chooser to get image
		int fileNum = fc.showOpenDialog(fileChooser);
		if(fileNum == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			readFile(file);
		}
		else if(state == NONE)
		{
			frame.dispose();
			System.exit(0);
		}
		else
		{
			state = NONE;
		}
	}
}
