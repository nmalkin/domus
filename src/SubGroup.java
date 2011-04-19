import java.awt.Point;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class SubGroup extends AbstractCollection<Person> {
	/** the people that make up this sub-group */
	private Collection<Person> _people;
	
	/** Where on screen am I located? */
	private Point _position;
	
	public SubGroup() {
		_people = new LinkedList<Person>();
		_position = new Point(0,0);
	}
	
	@Override
	public boolean add(Person e) {
		return _people.add(e);
	}
	
	@Override
	public Iterator<Person> iterator() {
		return _people.iterator();
	}

	@Override
	public int size() {
		return _people.size();
	}
	
	public Point getPosition() {
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
	}

}
