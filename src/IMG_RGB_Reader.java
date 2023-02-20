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
	JFrame fileChooser = new JFrame();
	JFrame frame = new JFrame();
	
	JTextField fileChooserField = new JTextField("");
	JButton fileChooserB = new JButton("Enter");
	JButton placeBoxB = new JButton("Place Box");
	JTextArea boxInfoField =new JTextArea("");
	JTextArea colorField = new JTextArea();
	Container east = new Container();
	
	final int NONE = 0;
	final int BOX_FIRST_CORNER = 1;
	final int BOX_SECOND_CORNER = 2;
	int state = NONE;
	
	int boxStartX = 0;
	int boxStartY = 0;
	
	BufferedImage image;
	String fileName;
	
	DecimalFormat df = new DecimalFormat("###.###");
	
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
		fileChooser.setSize(300, 100);
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
		placeBoxB.addActionListener(this);
		placeBoxB.setBackground(Color.gray);
		boxInfoField.setEditable(false);
		east.add(placeBoxB);
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
		case BOX_FIRST_CORNER:
			boxStartX = e.getX();
			boxStartY = e.getY();
			state = BOX_SECOND_CORNER;
			panel.startSelectionBox(e.getX(), e.getY());
			break;
		case BOX_SECOND_CORNER:
			
			panel.addBox(boxStartX, boxStartY, e.getX() , e.getY());
			state = NONE;
			panel.endSelectionBox();
			placeBoxB.setBackground(Color.gray);
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
		if(e.getSource().equals(placeBoxB))
		{
			state = BOX_FIRST_CORNER;
			panel.endSelectionBox();
			placeBoxB.setBackground(Color.green);
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
