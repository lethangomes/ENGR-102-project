import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class IMG_RGB_Reader implements ActionListener, MouseListener, MouseMotionListener {

	RGB_Reader_Panel panel = new RGB_Reader_Panel();
	JFrame fileChooser = new JFrame("Enter Image File Name");
	JFrame frame = new JFrame();
	
	JTextField fileChooserField = new JTextField();
	JButton fileChooserB = new JButton("Enter");
	JButton compareBoxesB = new JButton("Compare Boxes");
	JButton deleteBoxesB = new JButton("Delete Box");
	JTextArea boxInfoField =new JTextArea("");
	JTextArea colorField = new JTextArea();
	Container east = new Container();
	
	final int NONE = 0;
	final int COMPARE_BOXES = 1;
	final int COMPARE_BOXES2 = 2;
	final int BOX_SECOND_CORNER = 3;
	final int DELETE_BOX = 4;
	int state = NONE;
	
	int boxStartX = 0;
	int boxStartY = 0;
	
	BufferedImage image;
	String fileName;
	
	DecimalFormat df = new DecimalFormat("###.###");
	
	Box firstBox = null;
	
	public static void main(String[] args) 
	{
		
		new IMG_RGB_Reader();
	}
	
	public IMG_RGB_Reader()
	{
		//creates window for user to enter image file name
		fileChooser.setLayout(new BorderLayout());
		fileChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileChooser.add(fileChooserField);
		fileChooser.add(fileChooserField, BorderLayout.CENTER);
		fileChooserB.addActionListener(this);
		fileChooser.add(fileChooserB, BorderLayout.EAST);
		fileChooser.setSize(500, 100);
		fileChooser.setVisible(true);
	}
	
	public void makeFrame()
	{
		//creates window for the actual image analysis
		frame.setSize(image.getWidth() + 100, image.getHeight() + 100);
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
		boxInfoField.setEditable(false);
		east.add(compareBoxesB);
		east.add(deleteBoxesB);
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
				JOptionPane.showMessageDialog(frame, "Red difference: " + redDiff+ "\nGreen difference" + greenDiff + "\nBlue difference: " + blueDiff + "\nAverage difference: " + avgDiff);
				state = COMPARE_BOXES;
			}
			break;
		case BOX_SECOND_CORNER:
			
			//creates a new box using mouse coordinates
			panel.addBox(boxStartX, boxStartY, e.getX() , e.getY());
			
			//resets state and turns off selection box
			state = NONE;
			panel.endSelectionBox();
			compareBoxesB.setBackground(Color.gray);
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
				state = BOX_SECOND_CORNER;
				
				//starts selection box
				panel.startSelectionBox(e.getX(), e.getY());
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
		if(e.getSource().equals(fileChooserB)) //file chooser button pressed
		{
			//attempts to find file based on user input
			File file = new File(fileChooserField.getText());
			
			//attempts to read file
			try {
				//sets image to the file specified by user, then creates window to display image
				 image = ImageIO.read(file);
				 panel.setImage(image);
				 fileChooser.setVisible(false);
				 makeFrame();
			} catch (IOException ee) {
				//if the file was not found, or is not readable notifies user.
				ee.printStackTrace();
				JOptionPane.showMessageDialog(frame, "File \"" + fileChooserField.getText()+ "\" not found");
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


}
