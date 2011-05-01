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
	
	/** the house that's currently selected on the screen */
	private House _selectedHouse;
	
	public static State getInstance() {
		return INSTANCE;
	}
	
	private State() {
		_group = new Group();
		_results = LinkedListMultimap.create();
		_roomLists = new LinkedList<RoomList>();
		_selectedHouse = null;
	}
	
	public Group getGroup() {
		return _group;
	}
	
	public void setSelectedHouse(House h) {
		_selectedHouse = h;
	}
	
	public House getSelectedHouse() {
		return _selectedHouse;
	}
	
	public void updateResults() {
		// TODO
		System.out.println("updateResults");
	}
	
	public Multimap<SubGroup, Room> getResults() {
		return LinkedListMultimap.create(_results);
	}
	
	public List<RoomList> getRoomLists() {
		return new LinkedList<RoomList>(_roomLists);
	}
	
	public void export(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
	
	public void load(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
}
