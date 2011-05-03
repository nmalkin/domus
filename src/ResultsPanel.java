import java.awt.Dimension;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.JPanel;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class ResultsPanel extends JPanel {
	
	private Map<SubGroup, AccordionList<ResultsListTab, ResultsListItem>> _listsMap;
	private Multimap<SubGroup, Room> _results;
	private Map<Dorm, DormAverage> _dormAverages;
	
	public ResultsPanel() {
		super();
		Dimension size = new Dimension(500, 550);
		this.setPreferredSize(size);
		_listsMap = new HashMap<SubGroup, AccordionList<ResultsListTab, ResultsListItem>>();
		updateResultsLists();
	}
	
	public void updateResultsLists() {
		SetMultimap<SubGroup, Dorm> dormMap = TreeMultimap.create(Ordering.natural(), new DormComparator());
		_dormAverages = new HashMap<Dorm, DormAverage>();
		_results = State.getInstance().getResults();
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
				dormMap.put(sg, r.getDorm());
			}
		}
		for (SubGroup sg : dormMap.keySet()) {
			AccordionList<ResultsListTab, ResultsListItem> list = null;
			if ((list = _listsMap.get(sg)) == null) {
				list = AccordionList.create();
			}
			Collection<ResultsListTab> tabs = list.getTabs();
			for (Dorm d : dormMap.get(sg)) {
				ResultsListTab tab = null;
				if ((tab = containsDormTab(tabs, d)) != null)
					intersectResultsWithTab(sg, d, list, tab);
				else {
					System.out.println("new tab");
					tab = new ResultsListTab(d, sg, list);
					list.addTab(tab);
					for (Room r : _results.get(sg)) {
						if (r.getDorm() == d) {
							addListItem(list, tab, r);
						}
					}
				}
			}
			_listsMap.put(sg, list);
			this.add(list);
		}
	}
	
	private ResultsListTab containsDormTab(Collection<ResultsListTab> tabs, Dorm d) {
		ResultsListTab tab = null;
		for (ResultsListTab rlt : tabs) {
			if (rlt.getDorm().equals(d)) {
				tab = rlt;
			}
		}
		return tab;
	}
	
	private void intersectResultsWithTab(SubGroup sg, Dorm d, AccordionList<ResultsListTab, ResultsListItem> list, ResultsListTab tab) {
		Collection<Room> roomResults = _results.get(sg);
		SortedSet<ResultsListItem> tabItems = (SortedSet<ResultsListItem>) list.getItemsFromTab(tab);
		List<Room> tabRooms = new LinkedList<Room>();
		System.out.println("intersect " + "rR: " + roomResults.size() + " tR: " + tabRooms.size());
		if (tabItems != null) {
			for (ResultsListItem rli : tabItems) {
				tabRooms.add(rli.getRoom());
			}
		}
		else {
			for (Room r : roomResults) {
				addListItem(list, tab, r);
			}
			return;
		}
		for (Room r : roomResults) {
			if (!tabRooms.contains(r)) {
				addListItem(list, tab, r);
			}
		}
		for (Room r : tabRooms) {
			if (!tabRooms.contains(r)) {
				ResultsListItem[] tabs = tabItems.toArray(new ResultsListItem[0]);
				list.removeListItem(tab, tabs[tabRooms.indexOf(r)]);
			}
		}
	}
	
	private void addListItem(AccordionList<ResultsListTab, ResultsListItem> list, ResultsListTab tab, Room r) {
		ResultsListItem item = new ResultsListItem(r, list);
		item.setInsets(tab.getBorderInsets());
		list.addListItem(tab, item);
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
