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
		frame.setSize(image.getWidth() + 100, image.getHeight() + 100);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		
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
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(state)
		{
		case COMPARE_BOXES:
			firstBox = panel.findBox(e.getX(), e.getY());
			if(firstBox != null)
			{
				state = COMPARE_BOXES2;
				firstBox.setHighlighted(true);
			}
			break;
		case COMPARE_BOXES2:
			Box secondBox = panel.findBox(e.getX(), e.getY());
			if(secondBox !=null && !secondBox.equals(firstBox))
			{
				firstBox.setHighlighted(false);
				int redDiff = Math.abs(firstBox.getColor().getRed() - secondBox.getColor().getRed());
				int greenDiff = Math.abs(firstBox.getColor().getGreen() - secondBox.getColor().getGreen());
				int blueDiff = Math.abs(firstBox.getColor().getBlue() - secondBox.getColor().getBlue());
				int avgDiff = (redDiff + greenDiff + blueDiff)/3;
				
				JOptionPane.showMessageDialog(frame, "Red difference: " + redDiff+ "\nGreen difference" + greenDiff + "\nBlue difference: " + blueDiff + "\nAverage difference: " + avgDiff);
				state = COMPARE_BOXES;
			}
			break;
		case BOX_SECOND_CORNER:
			
			panel.addBox(boxStartX, boxStartY, e.getX() , e.getY());
			state = NONE;
			panel.endSelectionBox();
			compareBoxesB.setBackground(Color.gray);
			break;
		case DELETE_BOX:
			Box boxToBeDeleted = panel.findBox(e.getX(), e.getY());
			if(boxToBeDeleted != null)
			{
				panel.deleteBox(boxToBeDeleted);
				state = NONE;
				deleteBoxesB.setBackground(Color.gray);
				frame.repaint();
			}
			break;
		case NONE:
			Box selectedBox = panel.findBox(e.getX(), e.getY());
			if(selectedBox != null)
			{
				Color boxColor = selectedBox.getColor();
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

				panel.endSelectionBox();
				boxStartX = e.getX();
				boxStartY = e.getY();
				state = BOX_SECOND_CORNER;
				panel.startSelectionBox(e.getX(), e.getY());
			}
			break;
			
		}
		frame.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(compareBoxesB))
		{
			if(state == COMPARE_BOXES || state == COMPARE_BOXES2)
			{
				state = NONE;
				compareBoxesB.setBackground(Color.gray);
			}
			else
			{
				state = COMPARE_BOXES;
				compareBoxesB.setBackground(Color.green);
			}
		}
		if(e.getSource().equals(fileChooserB))
		{
			File file = new File(fileChooserField.getText());
			try {
				 image = ImageIO.read(file);
				 panel.setImage(image);
				 fileChooser.setVisible(false);
				 makeFrame();
			} catch (IOException ee) {
				ee.printStackTrace();
				JOptionPane.showMessageDialog(frame, "File \"" + fileChooserField.getText()+ "\" not found");
			}
		}
		if(e.getSource().equals(deleteBoxesB))
		{
			if(state == DELETE_BOX)
			{
				state = NONE;
				deleteBoxesB.setBackground(Color.gray);
			}
			else
			{
				state = DELETE_BOX;
				deleteBoxesB.setBackground(Color.green);
			}
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if(state == BOX_SECOND_CORNER)
		{
			panel.updateSelectionBox(e.getX(), e.getY());
			frame.repaint();
			//System.out.println("hey");
		}
		
	}

}
