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
	
	/** The average lottery number, calculated depending on defined years of interest. */
	private int _averageResult;
	
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
	
	private Room (Dorm dorm, String number) {
		_dorm = dorm;
		_number = number;
		_results = new LinkedList<LotteryResult>();
		_roomLists = new LinkedList<RoomList>();
		_listItems = new LinkedList<ResultsListItem>();
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
	
	/** Returns the average lottery result for this Room */
	public int getAverageResult() {
		return _averageResult;
	}
	
	/** 
	 * Returns the probability of receiving this room based
	 * on group's lottery number
	 */
	public int getProbability() {
		int lotteryNum = State.getInstance().getGroup().getLotteryNumber();
		
		int sum = 0;
		
		for(LotteryResult res : _results) {
			if(res.getLotteryNumber() > lotteryNum) sum += 1;
		}
		
		if(_results.size() == 0) return 0;
		return sum * 100 / _results.size();
	}

	public String toString() {
		String resultString = "[ ";
		
		for(LotteryResult r: _results) resultString += r.getLotteryNumber() + " ";
		resultString += "]";
		
		return _dorm.getName() + " " + _number + "; " + resultString + "; avg " + _averageResult;
	}
	
	public void addResult(LotteryResult result) {
		_results.add(result);
		updateAverage();
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
	
	public void updateAverage() {
		_averageResult = 0;
		
		for(int i = 0; i < _results.size(); i++) _averageResult += _results.get(i).getLotteryNumber();
		
		_averageResult /= _results.size();
	}

	@Override
	public int compareTo(Room o) {
		return _averageResult < o.getAverageResult() ? -1 : (_averageResult > o.getAverageResult() ? 1 : 0);
	}
}
