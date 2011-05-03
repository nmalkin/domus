import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class SubGroup extends CanvasComponent implements Iterable<Person>, Comparable<SubGroup> {
	/** the people that make up this subgroup */
	private Collection<Person> _people;
	
	/** the house that this subgroup is contained in */
	private House _house;
	
	/** the index of this subgroup, used for comparing in results */
	private int _index;
	
	/** used for comparing subgroups, strictly increasing */
	private static int _subGroupCount = 0;
		
	public SubGroup() {
		_people = new LinkedList<Person>();
		_house = null;
		_index = _subGroupCount++;
		
		this.setBounds(0,0,1,1);
		
		this.addMouseListener(new SubGroupDropListener());
		
		this.setBackground(Color.BLACK);
		
		this.setVisible(true);
	}	
	
	public boolean addPerson(Person p) {
		_people.add(p); // add to list of people
		
//		this.add(p); // add to parent JComponent
		
		// remove him from his old subgroup
		if(p.getSubGroup() != null) {
			p.getSubGroup().removePerson(p);
		}
		p.setSubGroup(this);
		
		updatePeoplePositions(); // update everybody's positions
		
		return true;
	}
	
	/**
	 * Removes the given person from this house.
	 * 
	 * @param p
	 * @return true if the person was found and removed, or false if the person wasn't found
	 */
	public boolean removePerson(Person p) {
		Iterator<Person> it = _people.iterator();
		while(it.hasNext()) {
			Person current = it.next();
			if(current == p) {
				current.setSubGroup(null);
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public void setHouse(House h) {
		_house = h;
	}
	
	public House getHouse() {
		return _house;
	}
	
	public int getOccupancy() {
		return _people.size();
	}
	
	/** Used for sorting in results display */
	public int getIndex() {
		return _index;
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
	
	/**
	 * Returns the appropriate color for this subgroup based on its location.
	 * 
	 * If the subgroup is over the trash icon, it will return a transparent version of its color. 
	 * 
	 * @return
	 */
	private Color getColor() {
		if(Canvas.overTrashIcon(this) || // if hovering over trash, draw this transparently 
			_house != null && Canvas.overTrashIcon(_house)) // if my house is over the trash, also draw transparently
		{ 
			return Constants.SUBGROUP_COLOR_TRANSPARENT;
		} else {
			return Constants.SUBGROUP_COLOR;
		}
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
		
		// select color based on location (over trash can or not?)
		g2.setPaint(getColor());
		
		g2.fill(new RoundRectangle2D.Double(
				Constants.INSET, Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.INSET, 
				10, 10));
	}
	
	@Override
	public String toString() {
		String s = "";
		Iterator<Person> people = _people.iterator();
		if (people.hasNext()) {
			s += people.next().getName();
		}
		return s;
	}

	/**
	 * Compares SubGroups based on creation order:
	 * a SubGroup that was created earlier is "smaller" than a SubGroup that was created later.
	 */
	@Override
	public int compareTo(SubGroup o) {
		return _index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0);
	}
	
	private class SubGroupDropListener extends java.awt.event.MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			SubGroup source = (SubGroup) e.getSource();
			
			Canvas canvas = (Canvas) source.getParent();
			canvas.dropSubGroup(source);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			House myHouse = ((SubGroup) e.getSource()).getHouse();
			State.getInstance().setSelectedHouse(myHouse);
			getParent().repaint();
		}
	}
}
