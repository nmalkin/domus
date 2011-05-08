import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class Person extends CanvasComponent {
	/** What is my name? */
	private String _name;
	
	/** What is my gender? */
	private Gender _gender;
	
	/** What is the subgroup that I'm contained in? */
	private SubGroup _subgroup;
	
	public Person(String name, Gender gender) {
		super();
		
		_name = name;
		_gender = gender;
		_subgroup = null;

		this.setPreferredSize(_gender.getImageDimension());
		updatePosition();
		
		this.setToolTipText(_name);
		
		this.addMouseListener(new PersonDropListener());
		
		//this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		this.setVisible(true);
	}
	
	// getters and setters
	public String getName() {
		return _name;
	}
	
	public Gender getGender() {
		return _gender;
	}
	
	public void setSubGroup(SubGroup s) {
		_subgroup = s;
	}
	
	public SubGroup getSubGroup() {
		return _subgroup;
	}
	
	@Override
	public void moveBy(int x, int y) {
		Point constraint = this.movementConstraint(x, y);
		x = constraint.x;
		y = constraint.y;
		
		super.moveBy(x, y);
	}
	
	@Override
	public int getWidth() {
		return _gender.getImageDimension().width;
	}
	
	@Override
	public int getHeight() {
		return _gender.getImageDimension().height;
	}
	
	/**
	 * Returns the appropriate alpha value for this person based on its location.
	 * 
	 * If the person is over the trash can, it will return an alpha value corresponding to transparency.
	 * 
	 * @return
	 */
	private float getAlphaValue() {
		if(this.overTrashIcon() || // if hovering over trash, draw this transparently
			_subgroup != null && _subgroup.overTrashIcon() || // if my subgroup is over the trash, also draw transparently
			_subgroup != null && _subgroup.getHouse() != null && _subgroup.getHouse().overTrashIcon())  // same for house
		{ 
			return Constants.TRASH_OVERLAY_ALPHA_FRACTION;
		} else {
			return 1.0f;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// draw image
		float alpha = getAlphaValue();
		if(alpha < 1) {
			float[] scales = { 1f, 1f, 1f, alpha };
			float[] offsets = new float[4];
			java.awt.image.RescaleOp rop = new java.awt.image.RescaleOp(scales, offsets, null);
		
			java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
			g2.drawImage((java.awt.image.BufferedImage) _gender.getImage(), rop, 0, 0);
		} else {
			g.drawImage(_gender.getImage(), 0, 0, null);
		}
	}
	
	@Override
	public String toString() {
		return getName() + "(" + this.getGender().toString() + ")";
	}
	
	private class PersonDropListener extends java.awt.event.MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			Person source = (Person) e.getSource();
			Canvas canvas = (Canvas) source.getParent();
			canvas.dropPerson(source);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				String newName = (String) JOptionPane.showInputDialog(
						(java.awt.Component) e.getSource(), 
						"Enter a name:", 
						"Domus", 
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						null,
						_name);
				
				if(newName != null) {
					_name = newName;
					setToolTipText(_name);
				}
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			SubGroup mySubGroup = ((Person) e.getSource()).getSubGroup();
			House myHouse = null;
			if(mySubGroup != null) {
				myHouse = mySubGroup.getHouse();
			}
			
			State.getInstance().setSelectedHouse(myHouse);
			getParent().repaint();
		}
	}
}
