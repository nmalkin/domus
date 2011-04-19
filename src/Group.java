import java.awt.Point;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

// Note: a Group is a Singleton
public class Group extends AbstractCollection<House> {
	private static final Group INSTANCE = new Group();
	
	/** the houses that make up this group */
	private Collection<House> _houses;
	
	/** What is our [possibly estimated] lottery number? */
	private int _lotteryNumber;
	
	/** Where on screen am I located? */
	private Point _position;
	
	public static final Group getInstance() {
		return INSTANCE;
	}
	
	private Group() {
		_houses = new LinkedList<House>();
		_position = new Point(0,0);
	}
	
	@Override
	public boolean add(House e) {
		return _houses.add(e);
	}
	
	@Override
	public Iterator<House> iterator() {
		return _houses.iterator();
	}

	@Override
	public int size() {
		return _houses.size();
	}
	
	public Point getPosition() {
		return _position;
	}
	
	public void setPosition(Point p) {
		_position = p;
	}
	
	public int getLotteryNumber() {
		return _lotteryNumber;
	}
	
	public void setLotteryNumber(int number) {
		_lotteryNumber = number;
	}
}
