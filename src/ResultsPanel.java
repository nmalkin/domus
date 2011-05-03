import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class ResultsPanel extends JPanel implements Runnable {
	
	private Map<SubGroup, AccordionList<ResultsListTab, ResultsListItem>> _listsMap;
	private Multimap<SubGroup, Room> _results;
	private Map<Dorm, DormAverage> _dormAverages;
	private JScrollPane _scroller;
	private JPanel _listsPanel;
	private JLabel _leftButton, _rightButton;
	private boolean _scrollLeft, _scrollRight, _scrollOnce;
	private int _scrollWidth, _scrollDelay, _hGap = 5;
	
	private int _listHeight = 550;
	private int _listWidth = 300;
	private int _numListsDisplayed = 3;
	
	public ResultsPanel() {
		super();
		this.setPreferredSize(new Dimension(1000, _listHeight));
		_listsPanel = new JPanel();
		_listsPanel.setPreferredSize(new Dimension(0, _listHeight));
		_listsPanel.setSize(new Dimension(0, _listHeight));
		_listsPanel.setLayout(new BoxLayout(_listsPanel, BoxLayout.LINE_AXIS));
		_listsPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		_scroller = new JScrollPane(_listsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.setPreferredSize(new Dimension(0, _listHeight));
		_scroller.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		_scroller.getHorizontalScrollBar().setBorder(new EmptyBorder(0, 0, 0, 0));
		_leftButton = new JLabel("LEFT");
		_leftButton.setFont(new Font("Verdana", Font.BOLD, 24));
		_rightButton = new JLabel("RIGHT");
		_rightButton.setFont(new Font("Verdana", Font.BOLD, 24));
		MouseListener listener = new ScrollButtonListener();
		_leftButton.addMouseListener(listener);
		_rightButton.addMouseListener(listener);
		this.add(_leftButton);
		this.add(_scroller);
		this.add(_rightButton);
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
		int i = 0;
		for (SubGroup sg : dormMap.keySet()) {
			AccordionList<ResultsListTab, ResultsListItem> list = null;
			if ((list = _listsMap.get(sg)) == null) {
				list = AccordionList.create();
			}
			Collection<ResultsListTab> tabs = list.getTabs();
			for (ResultsListTab rlt : tabs) {
				if (!_dormAverages.containsKey(rlt.getDorm()))
					list.removeTab(rlt);
			}
			for (Dorm d : dormMap.get(sg)) {
				ResultsListTab tab = null;
				if ((tab = containsDormTab(tabs, d)) != null) {
					tab.setComparisonValue((int) _dormAverages.get(d).getAverage());
					intersectResultsWithTab(sg, d, list, tab);
				}
				else {
					tab = new ResultsListTab(d, sg, list);
					tab.setComparisonValue((int) _dormAverages.get(d).getAverage());
					list.addTab(tab);
					for (Room r : _results.get(sg)) {
						if (r.getDorm() == d) {
							addListItem(list, tab, r);
						}
					}
				}
			}
			_listsMap.put(sg, list);
			_listsPanel.add(list);	
		}
		int numLists = _listsMap.values().size();
		int displayLists = Math.min(numLists, _numListsDisplayed);
		Dimension size = new Dimension(numLists * _listWidth + ((numLists - 1) * _hGap), _listHeight);
		_listsPanel.setPreferredSize(size);
		_listsPanel.setSize(size);
		size = new Dimension(displayLists * _listWidth + ((displayLists -1) * _hGap), _listHeight);
		_scroller.setPreferredSize(size);
		_scroller.setSize(size);
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
		if (tabItems != null) {
			for (ResultsListItem rli : tabItems) {
				tabRooms.add(rli.getRoom());
			}
		}
		else {
			for (Room r : roomResults) {
				if (r.getDorm() == tab.getDorm())
					addListItem(list, tab, r);
			}
			return;
		}
		for (Room r : roomResults) {
			if (r.getDorm() == tab.getDorm()) {
				if (!tabRooms.contains(r)) {
					addListItem(list, tab, r);
				}
			}
		}
		for (Room r : tabRooms) {
			if (r.getDorm() == tab.getDorm()) {
				if (!tabRooms.contains(r)) {
					ResultsListItem[] tabs = tabItems.toArray(new ResultsListItem[0]);
					list.removeListItem(tab, tabs[tabRooms.indexOf(r)]);
				}	
			}
		}
		if (list.getItemsFromTab(tab).size() == 0) {
			list.removeTab(tab);
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
	
	@Override
	public void run() {
		// this ensures that if a mouseClicked event fires, the mousePressed
		// and mouseReleased scrolling does not take place
//		if (!_scrollOnce) {
//			try {
//				Thread.sleep(10);
//				if (_scrollOnce)
//					return;
//			}
//			catch (Exception e) {
//				// do nothing
//			}
//		}
		int count = 0;
		while (_scrollLeft || _scrollRight) {
			if (_scrollOnce && count > (_listWidth / _scrollWidth)) {
				break;
			}
			if (_scrollWidth == _hGap) {
				break;
			}
			if (count == (_listWidth / _scrollWidth)) {
				_scrollWidth = _hGap;
			}
			if (_listsPanel.getSize().width < _scroller.getSize().width) {
				_scrollLeft = false;
				_scrollRight = false;
				break;
			}
			JViewport view = _scroller.getViewport();
			if (_scrollRight) {
				Rectangle r = view.getViewRect();
//				if (r.x + r.width >= _listsPanel.getWidth()) {
//					_scrollLeft = false;
//					_scrollRight = false;
//					break;
//				}
				if (r.x + r.width + _scrollWidth >= _listsPanel.getWidth()) {
					view.setViewPosition(new Point(_listsPanel.getWidth() - r.width, r.y));
					_scrollRight = false;
					_scrollLeft = false;
					break;
				}
				else {
					view.setViewPosition(new Point(r.x + _scrollWidth, r.y));
				}
			}
			else if (_scrollLeft) {
				Rectangle r = view.getViewRect();
//				if (r.x <= 0) {
//					_scrollLeft = false;
//					_scrollRight = false;
//					break;
//				}
				if (r.x - _scrollWidth <= 0) {
					view.setViewPosition(new Point(0, r.y));
					_scrollLeft = false;
					_scrollRight = false;
					break;
				}
				else {
					view.setViewPosition(new Point(r.x - _scrollWidth, r.y));
				}
			}
			try {
				Thread.sleep(_scrollDelay);
			}
			catch (Exception e) {
				// do nothing
			}
			++count;
		}
	}
	
	private class ScrollButtonListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel button = (JLabel) e.getSource();
			if (button.getText().equals("LEFT")) {
				_scrollLeft = true;
				_scrollRight = false;
			}
			else if (button.getText().equals("RIGHT")) {
				_scrollRight = true;
				_scrollLeft = false;
			}
			_scrollWidth = 50;
			_scrollDelay = 50;
			_scrollOnce = true;
			Executor exec = Executors.newSingleThreadExecutor();
			exec.execute(ResultsPanel.this);
		}
		
//		@Override
//		public void mousePressed(MouseEvent e) {
//			JLabel button = (JLabel) e.getSource();
//			if (button.getText().equals("LEFT")) {
//				_scrollLeft = true;
//				_scrollRight = false;
//			}
//			else if (button.getText().equals("RIGHT")) {
//				_scrollRight = true;
//				_scrollLeft = false;
//			}
//			_scrollWidth = 25;
//			_scrollDelay = 25;
//			_scrollOnce = false;
//			Executor exec = Executors.newSingleThreadExecutor();
//			exec.execute(ResultsPanel.this);
//		}
//		
//		@Override
//		public void mouseReleased(MouseEvent e) {
//			JLabel button = (JLabel) e.getSource();
//			if (button.getText().equals("LEFT")) {
//				_scrollLeft = false;
//				_scrollRight = false;
//			}
//			else if (button.getText().equals("RIGHT")) {
//				_scrollLeft = false;
//				_scrollRight = false;
//			}
//		}
	}
}
