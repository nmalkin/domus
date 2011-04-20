import java.util.Collection;
import java.util.Map;

/**
 * Stores all variable, non-GUI data in the program.
 *
 */
public class State {
	public static final State INSTANCE = new State();
	
	/** the (current) group */
	private Group _group;
	
	/** the most recent results (based on the current group) */
	private Map<SubGroup, Collection<Room>> _results; //TODO: redo as multimap
	
	/** room lists */
	//TODO
	
	public State getInstance() {
		return INSTANCE;
	}
	
	private State() {
		_group = Group.getInstance();
	}
	
	// method stubs (TODO)
	
	public Group getGroup() {
		return _group;
	}
	
	public void updateResults() {
		
	}
	
	// public ? getResults() {}
	
	// public ? getRoomLists()
	
	public void export(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
	
	public void load(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
}
