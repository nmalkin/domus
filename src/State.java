import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Stores all variable, non-GUI data in the program.
 *
 * @author nmalkin, jswarren
 */
public class State {
	public static final State INSTANCE = new State();
	
	/** the (current) group */
	private Group _group;
	
	/** the most recent results (based on the current group) */
	private Multimap<SubGroup, Room> _results;
	
	/** room lists */
	private List<RoomList> _roomLists;
	
	public static State getInstance() {
		return INSTANCE;
	}
	
	private State() {
		_group = new Group();
		SubGroup a = new SubGroup();
		a.add(new Person("Nate", Gender.MALE));
		SubGroup b = new SubGroup();
		b.add(new Person("Miya", Gender.FEMALE));
		House h = new House();
		h.add(a);
		h.add(b);
		_group.add(h);
		_results = LinkedListMultimap.create();
		_roomLists = new LinkedList<RoomList>();
	}
	
	public Group getGroup() {
		return _group;
	}
	
	public boolean isSophomoreOnly() {
		return false;
	}
	
	public int[] getYears() {
		int[] years = {2006, 2007, 2008, 2009, 2010, 2011};
		return years;
	}
	
	public void updateResults() {
		// TODO
		System.out.println("updateResults");
	}
	
	public Multimap<SubGroup, Room> getResults() {
		return LinkedListMultimap.create(_results);
	}
	
	public void putResults(SubGroup sg, RoomList rl) {
		for (Room r : rl) {
			_results.put(sg, r);
		}
	}
	
	public List<RoomList> getRoomLists() {
		return new LinkedList<RoomList>(_roomLists);
	}
	
	public void addRoomList(RoomList list) {
		_roomLists.add(list);
	}
	
	public void export(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
	
	public void load(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
}
