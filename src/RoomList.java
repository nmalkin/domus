import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class RoomList extends AbstractCollection<Room> {
	private Collection<Room> _rooms;
	
	public RoomList() {
		_rooms = new LinkedList<Room>();
	}
	
	public RoomList(Collection<Room> dorms) {
		_rooms = dorms;
	}
	
	@Override
	public boolean add(Room e) {
		return _rooms.add(e);
	}

	@Override
	public Iterator<Room> iterator() {
		return _rooms.iterator();
	}

	@Override
	public int size() {
		return _rooms.size();
	}
}
