import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.*;


public class SubGroup extends DraggablePositionableComponent implements Iterable<Person> {
	/** the people that make up this sub-group */
	private Collection<Person> _people;
		
	public SubGroup() {
		_people = new LinkedList<Person>();
		
		this.setBounds(0,0,1,1);
		
		this.addMouseListener(new SubGroupDropListener());
		
		this.setBackground(Color.BLACK);
		
		this.setVisible(true);
	}	
	
	public boolean addPerson(Person p) {
		_people.add(p); // add to list of people
		
//		this.add(p); // add to parent JComponent
		
		updatePeoplePositions(); // update everybody's positions
		
		return true;
	}
	
	/**
	 * Returns true if there are no people in this subgroup.
	 * @return
	 */
	public boolean isEmpty() {
		return _people.isEmpty();
	}
	
	public Iterator<Person> iterator() {
		return _people.iterator();
	}
	
	protected void updatePeoplePositions() {
		// note: positions are absolute,
		// so everything is (also) offset by this subgroup's position
		
		int horizontalOffset = Constants.SUBGROUP_PADDING;
		for(Person p : _people) {
			p.setPosition(getPosition().x + horizontalOffset, getPosition().y + Constants.SUBGROUP_PADDING);
			
			horizontalOffset += p.getPreferredSize().width + Constants.SUBGROUP_PADDING;
		}
	}
	
	@Override
	public void moveBy(int x, int y) {
		Point constraint = this.movementConstraint(x, y);
		x = constraint.x;
		y = constraint.y;
		
		super.moveBy(x, y); // adjust my position
		
		// adjust the positions of everybody inside me
		for(Person p : _people) {
			p.moveBy(x, y);
		}
	}
	
	@Override
	public int getWidth() {
		int width = Constants.SUBGROUP_PADDING;
		
		for(Person p : _people) {
			width += p.getWidth() + Constants.SUBGROUP_PADDING;
		}
		
		return width;
	}
	
	@Override
	public int getHeight() {
		int height = 0;
		
		for(Person p : _people) {
			height = Math.max(p.getHeight(), height);
		}
		
		height += 2 * Constants.SUBGROUP_PADDING;
		
		return height;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = getWidth();
		int height = getHeight();
		
		// set dimensions
		Dimension d = new Dimension(width, height);
		this.setPreferredSize(d);
		this.setSize(d);
		
		updatePosition();
		
		// draw box representing the subgroup
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Constants.SUBGROUP_COLOR);
		g2.fill(new RoundRectangle2D.Double(
				Constants.INSET, Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.INSET, 
				10, 10));
	}
	
	private class SubGroupDropListener extends java.awt.event.MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			SubGroup source = (SubGroup) e.getSource();
			int xAbsolute = source.getPosition().x + e.getX();
			int yAbsolute = source.getPosition().y + e.getY();
			
			Canvas canvas = (Canvas) source.getParent();
			canvas.dropSubGroupAt(source, xAbsolute, yAbsolute);
		}
	}
}
