import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * Stores all variable, non-GUI data in the program.
 *
 * @author nmalkin, jswarren
 */
public class State {
	// State is a singleton
	private static final State INSTANCE = new State();
	public static State getInstance() {
		return INSTANCE;
	}
	
	/** the (current) group */
	private Group _group;
	
	/** the most recent results (based on the current group) */
	private Multimap<SubGroup, Room> _results;
	
	/** room lists */
	private List<RoomList> _roomLists;
	
	/** the house that's currently selected on the screen */
	private House _selectedHouse;
	
	/** listener for whether or not the house is changing */
	private ChangeListener _selectedHouseChangeListener;
	
	private State() {
		_group = new Group();
		_results = TreeMultimap.create();
		_roomLists = new LinkedList<RoomList>();
		_selectedHouse = null;
		_selectedHouseChangeListener = null;
	}
	
	public Group getGroup() {
		return _group;
	}
	
	protected void setGroup(Group group) { //TODO: do we want to expose it like this?
		_group = group;
	}
	
	public boolean isSophomoreOnly() {
		return true;
	}
	
	public int getOptimism() {
		//TODO: optimism in state
		int optimism = Constants.PESSIMISTIC;
		return optimism;
	}
	
	public int[] getYears() {
		//TODO: years in state
		int[] years = {2006, 2007, 2008, 2009, 2010, 2011};
		return years;
	}
	
	public void setSelectedHouse(House h) {
		_selectedHouse = h;
		
		if(_selectedHouseChangeListener != null) {
			ChangeEvent e = new ChangeEvent(this);
			_selectedHouseChangeListener.stateChanged(e);
		}
	}
	
	public House getSelectedHouse() {
		return _selectedHouse;
	}
	
	/**
	 * Attaches the given ChangeListener such that
	 * a ChangeEvent is fired whenever the selected house is changed.
	 * 
	 * @param l
	 */
	public void setSelectedHouseChangeListener(ChangeListener l) {
		_selectedHouseChangeListener = l;
	}
	
	public void updateResults() {
		_results.clear();
		int[] years = getYears();
		boolean sophomoreOnly = isSophomoreOnly();
		for (House h : getGroup()) {
			Collection<Dorm> locations = h.getLocationPreference();
			boolean genderNeutral = h.isGenderNeutral();
			for (SubGroup sg : h) {
				RoomList results = Database.getResults(locations, sg.getOccupancy(), years, genderNeutral, sophomoreOnly);
				for (Room r : results) {
					_results.put(sg, r);
				}
			}
		}
	}
	
	public Multimap<SubGroup, Room> getResults() {
		return TreeMultimap.create(_results);
	}
	
	public List<RoomList> getRoomLists() {
		return new LinkedList<RoomList>(_roomLists);
	}
	
	public void addRoomList(RoomList list) {
		if (_roomLists.size() == 0) {
			//TODO enable Cart tab
		}
		_roomLists.add(list);
	}
	
	public void removeRoomList(RoomList list) {
		_roomLists.remove(list);
	}
	
	public void export(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
	
	public void load(String filename) {
		// TODO: consider returning boolean depending on success/failure
	}
}
