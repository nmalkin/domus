import java.awt.Point;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class House extends AbstractCollection<SubGroup> {
	/** the subgroups that make up this house */
	private Collection<SubGroup> _subgroups;
	
	/** Where would we be okay living? */
	private LocationPreference _locations;
	
	/** Where on screen am I located? */
	private Point _position;
	
	public House() {
		_subgroups = new LinkedList<SubGroup>();
		_position = new Point(0,0);
	}
	
	@Override
	public boolean add(SubGroup e) {
		return _subgroups.add(e);
	}
	
	@Override
	public Iterator<SubGroup> iterator() {
		return _subgroups.iterator();
	}

	@Override
	public int size() {
		return _subgroups.size();
	}
	
	public Point getPosition() {
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
	}
	
	public LocationPreference getLocationPreference() {
		return _locations;
	}
	
	public void setLocationPreference(LocationPreference l) {
		_locations = l;
	}
	
	public boolean isGenderNeutral() {
		return true;
	}
}
