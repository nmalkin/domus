import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

public class Person extends JComponent implements Draggable {
	/** What is my name? */
	private String _name;
	
	/** What is my gender? */
	private Gender _gender;
	
	/** Where on the canvas am I located? */
	private Point _position;
	
	public Person(String name, Gender gender) {
		_name = name;
		_gender = gender;
		_position = new Point(0,0);

		this.setPreferredSize(_gender.getImageDimension());
		updatePosition();
		this.setToolTipText(_name);
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK)); //TODO: remove
		
		this.setVisible(true);
	}
	
	// getters and setters
	public String getName() {
		return _name;
	}
	
	public Gender getGender() {
		return _gender;
	}
	
	public Point getPosition() { 
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
		updatePosition();
	}
	
	public void setPosition(int x, int y) {
		_position.x = x;
		_position.y = y;
		
		updatePosition();
	}
	
	/**
	 * Updates the object's position on screen by (re-)setting its bounds.
	 * 
	 * @see java.awt.Component#setBounds(int, int, int, int)
	 */
	private void updatePosition() {
		this.setBounds(_position.x, _position.y, this.getPreferredSize().width, this.getPreferredSize().height);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
				
		// draw image
		g.drawImage(_gender.getImage(), 0, 0, null);
	}
	
	@Override
	public void moveBy(int x, int y) {
		_position.x += x;
		_position.y += y;
		
		updatePosition();
	}
	
	@Override
	public String toString() {
		return getName() + "(" + this.getGender().toString() + ")";
	}
}
