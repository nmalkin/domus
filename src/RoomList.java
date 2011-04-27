import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class RoomList extends AbstractCollection<Room> {
	/** the name by which this RoomList will be referred to */
	private String _name;
	
	/** the rooms that make up this RoomList */
	private Collection<Room> _rooms;
	
	/**
	 * Initializes a RoomList with the given name.
	 */
	public RoomList(String name) {
		_name = name;
		_rooms = new LinkedList<Room>();
	}
	
	/**
	 * Initializes a RoomList with the given name,
	 * with the given Rooms.
	 * @param dorms
	 */
	public RoomList(Collection<Room> rooms, String name) {
		_name = name;
		_rooms = rooms;
	}
	
	/**
	 * Initializes a RoomList without a name.
	 */
	public RoomList() {
		this("");
	}
	
	/**
	 * Initializes a RoomList without a name,
	 * with the given Rooms.
	 * @param dorms
	 */
	public RoomList(Collection<Room> rooms) {
		this(rooms, "");
	}
	
	@Override
	public boolean add(Room e) {
		if (!_rooms.contains(e)) {
			return _rooms.add(e);
		}
		return false;
	}

	@Override
	public Iterator<Room> iterator() {
		return _rooms.iterator();
	}

	@Override
	public int size() {
		return _rooms.size();
	}
	
	public String getName() {
		return _name;
	}
}
