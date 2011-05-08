import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Room implements Comparable<Room> {
	/* factory methods for room */
	private static Map<String, Room> _roomMap = new HashMap<String, Room>();
	
	public static Room getRoom(Dorm dorm, String roomNumber) {
		String roomName = dorm.getName() + roomNumber;
		Room room = null;
		if ((room = _roomMap.get(roomName)) == null) {
			room = new Room(dorm, roomNumber);
			_roomMap.put(roomName, room);
		}
		return room;
	}
	
	/** What dorm is this room in? */
	private Dorm _dorm;
	
	/** The name/number of this room. */
	private String _number;
	
	/** This room's history in the lottery. */
	private List<LotteryResult> _results;
	
	/** 
	 * The RoomLists that this room is a part of.
	 * Used for displaying list labels on results page.
	 */
	private List<RoomList> _roomLists;
	
	/**
	 * The ResultsListItems that this room is a part of.
	 * Used for updating list labels on results tab.
	 */
	private List<ResultsListItem> _listItems;
	
	/** regressions coefficients
	 * 
	 * we compute the probability of obtaining this room 
	 * by using (externally calculated) regressions coefficients
	 * and plugging values into the logistic function
	 */
	private double _b0_coefficient, _b1_coefficient;
	
	
	private Room (Dorm dorm, String number) {
		_dorm = dorm;
		_number = number;
		_results = new LinkedList<LotteryResult>();
		_roomLists = new LinkedList<RoomList>();
		_listItems = new LinkedList<ResultsListItem>();
		
		_b0_coefficient = 0;
		_b1_coefficient = 0;
	}
	
	/** Returns the Dorm to which this Room belongs */
	public Dorm getDorm() {
		return _dorm;
	}
	
	/** Returns the number/name of this room */
	public String getNumber() {
		return _number;
	}
	
	/** Returns the past lottery results for this Room */
	public List<LotteryResult> getResults() {
		return new LinkedList<LotteryResult>(_results);
	}
	
	/** Returns the average lottery result for this Room. */
	@Deprecated
	public int getAverageResult() {
		int averageResult = 0;
		
		for(int i = 0; i < _results.size(); i++) averageResult += _results.get(i).getLotteryNumber();
		
		averageResult /= _results.size();
		
		return averageResult;
	}
	
	/** 
	 * Returns the probability of receiving this room based on group's lottery number.
	 * 
	 * To compute the probability, we use the coefficients from the logistic regression
	 * (set using setCoefficients) and plug the lottery number into the logistic function:
	 * 1/(1+e^-(b0 + b1*x))
	 * where x is the lottery number
	 */
	public double getProbability() {
		double probability = 1.0 / (1.0 + Math.exp(-1 * (_b0_coefficient + _b1_coefficient * State.getInstance().getGroup().getLotteryNumber())));
		System.out.println(probability);
		return probability;
	}
	
	public void setCoefficients(double b0, double b1) {
		_b0_coefficient = b0;
		_b1_coefficient = b1;
	}

	public String toString() {
		String resultString = "[ ";
		
		for(LotteryResult r: _results) resultString += r.getLotteryNumber() + " ";
		resultString += "]";
		
		return _dorm.getName() + " " + _number + "; " + resultString + ";";
	}
	
	public void addResult(LotteryResult result) {
		_results.add(result);
	}
	
	public Collection<RoomList> getRoomLists() {
		return _roomLists;
	}
	
	public void addToRoomList(RoomList list) {
		if (!_roomLists.contains(list))
			_roomLists.add(list);
	}
	
	public void removeFromRoomList(RoomList list) {
		if (_roomLists.contains(list))
			_roomLists.remove(list);
	}
	
	public Collection<ResultsListItem> getListItems() {
		return _listItems;
	}
	
	public void addToListItem(ResultsListItem item) {
		if (!_listItems.contains(item))
			_listItems.add(item);
	}
	
	public void removeFromListItem(ResultsListItem item) {
		if (_listItems.contains(item))
			_listItems.remove(item);
	}

	@Override
	public int compareTo(Room o) {
		double myProbability = getProbability();
		double theirProbability = o.getProbability();
		return myProbability < theirProbability ? -1 : (myProbability > theirProbability ? 1 : 0);
	}
}
