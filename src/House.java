import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class House extends CanvasComponent implements Iterable<SubGroup>, Comparable<House> {
	/** the subgroups that make up this house */
	private Collection<SubGroup> _subgroups;
	
	/** Where would we be okay living? */
	private LocationPreference _locations;
	
	/** the index of this house, used for comparing in results */
	private int _index;
	
	/** used for comparing house, strictly increasing */
	private static int _houseCount = 0;
	
	public House() {
		super();
		
		_subgroups = new LinkedList<SubGroup>();
		_locations = new LocationPreference();
		_index = _houseCount++;
		
		this.setBounds(0,0,1,1);
		
		this.setBackground(Color.BLACK);
		
		this.addMouseListener(new HouseMouseListener());
		
		this.setVisible(true);
	}
	
	public boolean addSubGroup(SubGroup s) {
		_subgroups.add(s);
		
		// remove it from its old house
		if(s.getHouse() != null) {
			s.getHouse().removeSubGroup(s);
		}
		s.setHouse(this);
		
		updateSubGroupPositions();
		
		return true;
	}
	
	/**
	 * Removes the given subgroup from this house.
	 * 
	 * @param s
	 * @return true if the subgroup was found and removed, or false if the subgroup wasn't found
	 */
	public boolean removeSubGroup(SubGroup s) {
		Iterator<SubGroup> it = _subgroups.iterator();
		while(it.hasNext()) {
			SubGroup current = it.next();
			if(current == s) {
				current.setHouse(null);
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	public int numberOfSubGroups() {
		return _subgroups.size();
	}
	
	public boolean isGenderNeutral() {
		//TODO: implement
		return true;
	}
	
	/**
	 * Returns true if there are no subgroups in this house.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return _subgroups.isEmpty();
	}
	
	@Override
	public Iterator<SubGroup> iterator() {
		return _subgroups.iterator();
	}
	
	public LocationPreference getLocationPreference() {
		return _locations;
	}
	
	public void setLocationPreference(LocationPreference l) {
		_locations = l;
	}
	
	/** Used for sorting in results display */
	public int getIndex() {
		return _index;
	}
	
	protected void updateSubGroupPositions() {
		// note: positions are absolute,
		// so everything is (also) offset by this subgroup's position
		
		int horizontalOffset = Constants.HOUSE_PADDING;
		for(SubGroup s : _subgroups) {
			s.setPosition(getPosition().x + horizontalOffset, getPosition().y + Constants.HOUSE_PADDING);
			s.updatePeoplePositions();
			
			horizontalOffset += s.getWidth() + Constants.HOUSE_PADDING;
		}
	}
	
	@Override
	public void moveBy(int x, int y) {
		Point constraint = this.movementConstraint(x, y);
		x = constraint.x;
		y = constraint.y;
		super.moveBy(x, y); // adjust my position
		
		// adjust the positions of everybody inside me
		for(SubGroup s : _subgroups) {
			s.moveBy(x, y);
		}
	}
	
	@Override
	public int getWidth() {
		int width = Constants.HOUSE_PADDING;
		
		for(SubGroup s : _subgroups) {
			width += s.getWidth() + Constants.HOUSE_PADDING; //TODO: getWidth() may not return correct width
		}
		
		return width;
	}
	
	@Override
	public int getHeight() {
		int height = 0;
		
		for(SubGroup s : _subgroups) {
			height = Math.max(height, s.getHeight());
		}
		
		height += 2 * Constants.HOUSE_PADDING;
		
		return height;
	}
	
	/**
	 * Returns the appropriate color for this house based on its location.
	 * 
	 * If the house is over the trash icon, it will return a transparent version of its color. 
	 * 
	 * @return
	 */
	private Color getColor() {
		if(Canvas.overTrashIcon(this)) { // if hovering over trash, draw this transparently
			return Constants.HOUSE_COLOR_TRANSPARENT;
		} else {
			return Constants.HOUSE_COLOR;
		}
	}
	
	/**
	 * Returns the appropriate color for this house's based on its location.
	 * 
	 * If the house is over the trash icon, it will return a transparent version of the border color. 
	 * 
	 * @return
	 */
	private Color getBorderColor() {
		if(Canvas.overTrashIcon(this)) { // if hovering over trash, draw this transparently
			return Constants.SELECTED_HOUSE_BORDER_COLOR_TRANSPARENT;
		} else {
			return Constants.SELECTED_HOUSE_BORDER_COLOR;
		}
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = getWidth();
		int height = getHeight();
		
		
		// set dimensions
		Dimension d = new Dimension(width + 5, height + 5);
		this.setPreferredSize(d);
		this.setSize(d);
		
		updatePosition(); // updates component dimensions (in addition to position)
		
		// draw box representing the subgroup
		java.awt.Shape houseBox = new RoundRectangle2D.Double(
				Constants.INSET, Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.INSET, 
				10, 10);
		Graphics2D g2 = (Graphics2D) g;
		
		// select color based on location (over trash can or not?)
		g2.setPaint(getColor());
		
		g2.fill(houseBox);
		
		// if selected, draw border
		if(this == State.getInstance().getSelectedHouse()) {
			g2.setPaint(getBorderColor());
			g2.setStroke(new java.awt.BasicStroke(Constants.SELECTED_HOUSE_BORDER_WIDTH));
			g2.draw(houseBox);
		}
	}
	
	/**
	 * Compares Houses based on creation order:
	 * a House that was created earlier is "smaller" than a House that was created later.
	 */
	@Override
	public int compareTo(House o) {
		return _index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0);
	}
	
	private class HouseMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			State.getInstance().setSelectedHouse((House) e.getSource());
			getParent().repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			House source = (House) e.getSource();
			
			Canvas canvas = (Canvas) source.getParent();
			canvas.dropHouse(source);
		}
	}
}
