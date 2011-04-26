import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class House  extends DraggablePositionableComponent implements Iterable<SubGroup> {
	/** the subgroups that make up this house */
	private Collection<SubGroup> _subgroups;
	
	/** Where would we be okay living? */
	private LocationPreference _locations;
	
	public House() {
		super();
		
		_subgroups = new LinkedList<SubGroup>();
		
		this.setBounds(0,0,1,1);
		
		this.setBackground(Color.BLACK);
		
		this.setVisible(true);
	}
	
	public boolean addSubGroup(SubGroup e) {
		_subgroups.add(e);
		updateSubGroupPositions();
		
		return true;
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
	
	protected void updateSubGroupPositions() {
		// note: positions are absolute,
		// so everything is (also) offset by this subgroup's position
		
		int horizontalOffset = Constants.STANDARD_PADDING;
		for(SubGroup s : _subgroups) {
			s.setPosition(getPosition().x + horizontalOffset, getPosition().y + Constants.STANDARD_PADDING);
			s.updatePeoplePositions();
			
			horizontalOffset += s.getWidth() + Constants.STANDARD_PADDING;
		}
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y); // adjust my position
		
		// adjust the positions of everybody inside me
		for(SubGroup s : _subgroups) {
			s.moveBy(x, y);
		}
	}
	
	@Override
	public int getWidth() {
		int width = Constants.STANDARD_PADDING;
		
		for(SubGroup s : _subgroups) {
			width += s.getWidth() + Constants.STANDARD_PADDING; //TODO: getWidth() may not return correct width
		}
		
		return width;
	}
	
	@Override
	public int getHeight() {
		int height = 0;
		
		for(SubGroup s : _subgroups) {
			height = Math.max(height, s.getHeight());
		}
		
		height += 2 * Constants.STANDARD_PADDING;
		
		return height;
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
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Constants.HOUSE_COLOR);
		g2.fill(new RoundRectangle2D.Double(
				Constants.INSET, Constants.INSET, 
				width - 2 * Constants.INSET, height - 2 * Constants.INSET, 
				10, 10));
		
	}
}
