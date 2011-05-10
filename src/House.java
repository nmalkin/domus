import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class House extends CanvasComponent implements Iterable<SubGroup>, Comparable<House> {
	/** the subgroups that make up this house */
	private Collection<SubGroup> _subgroups;
	
	/** Where would we be okay living? */
	private LocationPreference _locations;
	
	/** the index of this house, used for comparing in results */
	private int _index;
	
	/** used for comparing house, strictly increasing */
	private static int _houseCount = 0;
	
	/** listener for changes in location preferences */
	private ChangeListener _locationPreferenceChangeListener;
	
	private int _width = 0, _height = 0;
	
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
		
		if (_locationPreferenceChangeListener != null) {
			ChangeEvent e = new ChangeEvent(this);
			_locationPreferenceChangeListener.stateChanged(e);
		}
	}
	
	/**
	 * Attaches the given ChangeListener such that
	 * a ChangeEvent is fired whenever the selected house is changed.
	 * 
	 * @param l
	 */
	public void setLocationPreferenceChangeListener(ChangeListener l) {
		_locationPreferenceChangeListener = l;
	}
	
	/** Used for sorting in results display */
	public int getIndex() {
		return _index;
	}
	
	/**
	 * Returns the total number of people in this house.
	 * 
	 * @return
	 */
	public int numberOfPeople() {
		int count = 0;
		
		for(SubGroup s : _subgroups) {
			count += s.getOccupancy();
		}
		
		return count;
	}
	
	protected void updateSubGroupPositions() {
		// note: positions are absolute,
		// so everything is (also) offset by this house's position
		
		_width = 0;
		_height = 0;
		
		int horizontalOffset = Constants.HOUSE_PADDING;
		int verticalOffset = Constants.HOUSE_PADDING + Constants.HOUSE_ROOF_HEIGHT;
		
		int xPosition;
		
		for(SubGroup s : _subgroups) {
			xPosition = this.getPosition().x + horizontalOffset;
			
			// make sure the position where we're placing this subgroup is not off the screen
			if(xPosition + s.getWidth() + Constants.HOUSE_PADDING > Canvas.getInstance().getWidth() &&
					horizontalOffset > Constants.HOUSE_PADDING) // do not allow wrapping on the first subgroup in each row
			{
				_width = horizontalOffset;
				
				horizontalOffset = Constants.HOUSE_PADDING;
				verticalOffset += s.getHeight() + Constants.HOUSE_PADDING;
					// NOTE: we are assuming here that all SubGroups have the same height as the current one
				
				xPosition = this.getPosition().x + horizontalOffset;
			}
			
			// set subgroup's position
			s.setPosition(xPosition, getPosition().y + verticalOffset);
			s.updatePeoplePositions();
			
			horizontalOffset += s.getWidth() + Constants.HOUSE_PADDING; // offset the position of the next subgroup
			
			_height = Math.max(_height, verticalOffset + s.getHeight() + Constants.HOUSE_PADDING); // update the height if necessary
		}
		
		_width = Math.max(_width, horizontalOffset);
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
		return _width;
	}
	
	@Override
	public int getHeight() {
		return _height + Constants.HOUSE_ROOF_HEIGHT;
	}
	
	@Override
	public boolean contains(int x, int y) {
		int width = getWidth();
		int height = getHeight();
		
		Rectangle rectangle = new Rectangle(
				Constants.INSET, Constants.HOUSE_ROOF_HEIGHT + Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.HOUSE_ROOF_HEIGHT  - 2 * Constants.INSET);
		
		int[] xPoints = {Constants.INSET, width / 2, width - Constants.INSET};
		int[] yPoints = {Constants.HOUSE_ROOF_HEIGHT + Constants.INSET, Constants.INSET, Constants.HOUSE_ROOF_HEIGHT + Constants.INSET};
		Polygon roof = new Polygon(xPoints, yPoints, 3);
		
		return rectangle.contains(x, y) || roof.contains(x, y);
	}
	
	@Override
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}
	
	/**
	 * Returns the appropriate color for this house based on its location.
	 * 
	 * If the house is over the trash icon, it will return a transparent version of its color. 
	 * 
	 * @return
	 */
	private Color getColor() {
		if(this.overTrashIcon()) { // if hovering over trash, draw this transparently
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
		if(Canvas.getInstance().overTrashIcon(this)) { // if hovering over trash, draw this transparently
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
		java.awt.Shape houseBox = new Rectangle(
				Constants.INSET, Constants.HOUSE_ROOF_HEIGHT + Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.HOUSE_ROOF_HEIGHT  - 2 * Constants.INSET);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// select color based on location (over trash can or not?)
		g2.setPaint(getColor());
		
		g2.fill(houseBox);
		
		// draw roof on house
		int[] xPoints = {Constants.INSET, width / 2, width - Constants.INSET};
		int[] yPoints = {Constants.HOUSE_ROOF_HEIGHT + Constants.INSET, Constants.INSET, Constants.HOUSE_ROOF_HEIGHT + Constants.INSET};
		g.fillPolygon(xPoints, yPoints, 3);
		
		// if selected, draw border
		if(this == State.getInstance().getSelectedHouse()) {
//			g2.setPaint(getBorderColor());
//			g2.setStroke(new java.awt.BasicStroke(Constants.SELECTED_HOUSE_BORDER_WIDTH));
//			g2.draw(houseBox);
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
