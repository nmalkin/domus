import java.awt.Dimension;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class ResultsPanel extends JPanel {
	
	private List<AccordionList<ResultsListTab, ResultsListItem>> _lists;
	private Multimap<SubGroup, Room> _results;
	private Map<Dorm, DormAverage> _dormAverages;
	
	public ResultsPanel() {
		super();
		Dimension size = new Dimension(500, 550);
		this.setPreferredSize(size);
		_lists = new LinkedList<AccordionList<ResultsListTab, ResultsListItem>>();
		_results = State.getInstance().getResults();
		SetMultimap<SubGroup, Dorm> _dormMap = TreeMultimap.create(Ordering.natural(), new DormComparator());
		_dormAverages = new HashMap<Dorm, DormAverage>();
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				DormAverage average = null;
				if (_dormAverages.get(r.getDorm()) != null)
					average = _dormAverages.get(r.getDorm());
				else
					average = new DormAverage();
				average.add(r.getAverageResult());
				_dormAverages.put(r.getDorm(), average);
			}
		}
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				_dormMap.put(sg, r.getDorm());
			}
		}
		for (SubGroup sg : _dormMap.keySet()) {
			AccordionList<ResultsListTab, ResultsListItem> list = AccordionList.create();
			for (Dorm d : _dormMap.get(sg)) {
				ResultsListTab tab = new ResultsListTab(d, sg, list);
				list.addTab(tab);
				for (Room r : _results.get(sg)) {
					if (r.getDorm() == d) {
						ResultsListItem item = new ResultsListItem(r, list);
						item.setInsets(tab.getBorderInsets());
						list.addListItem(tab, item);
					}
				}
			}
			_lists.add(list);
			this.add(list);
		}
	}
	
	private class DormAverage {
		
		private int _size;
		private int _sum;
		
		public DormAverage() {
			_sum = 0;
			_size = 0;
		}
		
		public void add(int value) {
			_sum += value;
			++_size;
		}
		
		public double getAverage() {
			return (double) _sum / _size;
		}
	}
	
	private class DormComparator implements Comparator<Dorm> {

		@Override
		public int compare(Dorm o1, Dorm o2) {
			double ave1 = _dormAverages.get(o1).getAverage();
			double ave2 = _dormAverages.get(o2).getAverage();
			return ave1 < ave2 ? -1 : (ave1 > ave2 ? 1 : 0); 
		}
		
	}
}
