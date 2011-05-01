import java.util.LinkedList;
import java.util.List;


public class Room {
	/** What dorm is this room in? */
	private Dorm _dorm;
	
	/** The name/number of this room. */
	private String _number;
	
	/** This room's history in the lottery. */
	private List<LotteryResult> _results;
	
	/** The average lottery number, calculated depending on defined years of interest. */
	private int _averageResult;
	
	public Room (Dorm dorm, String number) {
		_dorm = dorm;
		_number = number;
		_results = new LinkedList<LotteryResult>();
	}
	
	public void print() {
		String resultString = "[ ";
		
		for(LotteryResult r: _results) resultString += r.getLotteryNumber() + " ";
		resultString += "]";
		
		System.out.println(_dorm.getName() + " " + _number + "; " + resultString + "; avg " + _averageResult);
	}
	
	public void addResult(LotteryResult result) {
		_results.add(result);
		updateAverage();
	}
	
	public void updateAverage() {
		_averageResult = 0;
		
		for(int i = 0; i < _results.size(); i++) _averageResult += _results.get(i).getLotteryNumber();
		
		_averageResult /= _results.size();
	}
	
	public int getAverage() {
		return _averageResult;
	}
}