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

public class Person extends DraggablePositionableComponent {
	/** What is my name? */
	private String _name;
	
	/** What is my gender? */
	private Gender _gender;
	
	public Person(String name, Gender gender) {
		super();
		
		_name = name;
		_gender = gender;

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
	
	@Override
	public int getWidth() {
		return _gender.getImageDimension().width;
	}
	
	@Override
	public int getHeight() {
		return _gender.getImageDimension().height;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draw image
		g.drawImage(_gender.getImage(), 0, 0, null);
	}
	
	@Override
	public String toString() {
		return getName() + "(" + this.getGender().toString() + ")";
	}
}
