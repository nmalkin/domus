import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class ResultsPanel extends JPanel implements Runnable {
	
	private Map<SubGroup, AccordionList<ResultsListTab, ResultsListItem>> _listsMap;
	private Multimap<SubGroup, Room> _results;
	private Map<Dorm, DormAverage> _dormAverages;
	private JScrollPane _scroller;
	private JPanel _resultsPanel;
	private JLabel _leftButton, _rightButton;
	private ImageIcon _leftButtonIcon = new ImageIcon(Constants.LEFT_ARROW, "scroll left");
	private ImageIcon _rightButtonIcon = new ImageIcon(Constants.RIGHT_ARROW, "scroll right");
	private boolean _scrollLeft, _scrollRight, _scrollOnce;
	private int _scrollWidth, _scrollDelay;
	
	public ResultsPanel() {
		super();
		this.setPreferredSize(new Dimension(Constants.RESULTS_PANEL_WIDTH, Constants.RESULTS_PANEL_HEIGHT));
		_resultsPanel = new JPanel();
		_resultsPanel.setPreferredSize(new Dimension(0, Constants.RESULTS_PANEL_HEIGHT));
		_resultsPanel.setSize(new Dimension(0, Constants.RESULTS_PANEL_HEIGHT));
		_resultsPanel.setLayout(new BoxLayout(_resultsPanel, BoxLayout.LINE_AXIS));
		_resultsPanel.setBorder(Constants.EMPTY_BORDER);
		_scroller = new JScrollPane(_resultsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.setPreferredSize(new Dimension(0, Constants.RESULTS_PANEL_HEIGHT + _scroller.getHorizontalScrollBar().getPreferredSize().height));
		_scroller.setViewportBorder(Constants.EMPTY_BORDER);
		_scroller.getHorizontalScrollBar().setBorder(Constants.EMPTY_BORDER);
		_leftButton = new JLabel(_leftButtonIcon);
		_leftButton.setVisible(false);
		_rightButton = new JLabel(_rightButtonIcon);
		_rightButton.setVisible(false);
		MouseListener listener = new ScrollButtonListener();
		_leftButton.addMouseListener(listener);
		_rightButton.addMouseListener(listener);
		this.add(_leftButton);
		this.add(_scroller);
		this.add(_rightButton);
		_listsMap = new HashMap<SubGroup, AccordionList<ResultsListTab, ResultsListItem>>();
	}
	
	public void updateResultsLists(boolean probabilitiesOnly) {
		if (probabilitiesOnly) {
			updateProbabilities();
			return;
		}
		SetMultimap<SubGroup, Dorm> dormMap = TreeMultimap.create(Ordering.natural(), new DormComparator());
		_dormAverages = new HashMap<Dorm, DormAverage>();
		_results = State.getInstance().getResults();
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				DormAverage average = _dormAverages.get(r.getDorm());
				if (average == null)
					average = new DormAverage();
				average.add(r.getProbability());
				_dormAverages.put(r.getDorm(), average);
			}
		}
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				dormMap.put(sg, r.getDorm());
			}
		}
		Collection<SubGroup> oldSubGroups = _listsMap.keySet();
		for (Iterator<SubGroup> iter = oldSubGroups.iterator(); iter.hasNext();) {
			SubGroup sg = iter.next();
			if (!dormMap.containsKey(sg)) {
				if (_listsMap.get(sg) != null)
					_resultsPanel.remove(_listsMap.get(sg));
				iter.remove();
			}
		}
		for (SubGroup sg : dormMap.keySet()) {
			AccordionList<ResultsListTab, ResultsListItem> list = null;
			if ((list = _listsMap.get(sg)) == null) {
				list = AccordionList.create(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_LIST_HEIGHT, Constants.RESULTS_HEADER_HEIGHT);
			}
			list.setHeader(createListHeader(sg));
			Collection<ResultsListTab> tabs = list.getTabs();
			for (Iterator<ResultsListTab> iter = tabs.iterator(); iter.hasNext();) {
				ResultsListTab rlt = iter.next();
				if (!_dormAverages.containsKey(rlt.getDorm())) {
					iter.remove();
					list.removeTab(rlt);
				}
			}
			tabs = list.getTabs();
			for (Dorm d : dormMap.get(sg)) {
				ResultsListTab tab = containsDormTab(tabs, d);
				if (tab != null) {
					intersectResultsWithTab(sg, d, list, tab);
					tab.setComparisonValue(_dormAverages.get(d).getAverage());
				}
				else {
					tab = new ResultsListTab(d, sg, list);
					tab.setComparisonValue(_dormAverages.get(d).getAverage());
					list.addTab(tab);
					for (Room r : _results.get(sg)) {
						if (r.getDorm() == d) {
							addListItem(list, tab, r);
						}
					}
				}
				tab.validateListLabels();
			}
			_listsMap.put(sg, list);
			_resultsPanel.add(list);
		}
		int numLists = _listsMap.values().size();
		int displayLists = Math.min(numLists, Constants.RESULTS_LISTS_DISPLAYED);
		Dimension size = new Dimension(numLists * Constants.RESULTS_LIST_WIDTH + ((numLists - 1) * Constants.RESULTS_PANEL_HORIZONTAL_GAP), Constants.RESULTS_PANEL_HEIGHT);
		_resultsPanel.setPreferredSize(size);
		_resultsPanel.setSize(size);
		size = new Dimension(displayLists * Constants.RESULTS_LIST_WIDTH + ((displayLists -1) * Constants.RESULTS_PANEL_HORIZONTAL_GAP), _scroller.getSize().height);
		_scroller.setPreferredSize(size);
		_scroller.setSize(size);
		boolean buttonsVisible = false;
		if (numLists > Constants.RESULTS_LISTS_DISPLAYED)
			buttonsVisible = true;
		_leftButton.setVisible(buttonsVisible);
		_rightButton.setVisible(buttonsVisible);
	}
	
	private JPanel createListHeader(SubGroup sg) {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_HEADER_HEIGHT));
		panel.setSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_HEADER_HEIGHT));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		String names = "";
		int i = 0;
		ImageIcon man = new ImageIcon(Gender.MALE.getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH));
		ImageIcon woman = new ImageIcon(Gender.FEMALE.getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH));
		int width = Constants.RESULTS_LIST_WIDTH - (man.getIconWidth() * sg.getOccupancy()); 
		for (Person p : sg) {
			ImageIcon icon = man;
			if (p.getGender() == Gender.FEMALE)
				icon = woman;
			JLabel label = new JLabel(icon);
			
			panel.add(label);
			panel.add(Box.createRigidArea(new Dimension(5, 0)));
			if (! p.getName().equals(Constants.NEW_PERSON_DEFAULT_NAME)) {
				label.setToolTipText(p.getName());
				if (i == 0)
					names += p.getName();
				else if (i == sg.getOccupancy() - 1) {
					if (i > 1)
						names += ","; 
					names += " and " + p.getName();
				}
				else
					names += ", " + p.getName();
			}
			++i;
		}
		JLabel name = new JLabel(names);
		name.validate();
		boolean fix = false;
		while (name.getPreferredSize().width > width) {
			String text = name.getText();
			text = text.substring(0, text.length() - 2);
			name.setText(text);
			name.validate();
			fix = true;
		}
		if (fix) {
			String text = name.getText();
			text = text.substring(0, text.length() - 2 - "...".length());
			text += "...";
			name.setText(text);
			name.setToolTipText(names);
		}
		panel.add(name);
		panel.add(Box.createHorizontalGlue());
		return panel;
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
		Collection<ResultsListItem> tabItems = tab.getItems();
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
		tabItems = tab.getItems();
		for (ResultsListItem item : tabItems) {
			Room r = item.getRoom();
			if (!roomResults.contains(r)) {
				list.removeListItem(tab, item);
			}
		}
		for (Room r : roomResults) {
			if (r.getDorm() == tab.getDorm()) {
				if (!tabRooms.contains(r)) {
					addListItem(list, tab, r);
				}
			}
		}
		if (tab.getItems().size() == 0) {
			list.removeTab(tab);
		}
	}
	
	private void addListItem(AccordionList<ResultsListTab, ResultsListItem> list, ResultsListTab tab, Room r) {
		ResultsListItem item = new ResultsListItem(r, list);
		list.addListItem(tab, item);
	}
	
	private void updateProbabilities() {
		for (AccordionList<ResultsListTab, ResultsListItem> list : _listsMap.values()) {
			for (ResultsListTab tab : list.getTabs()) {
				tab.updateProbabilities();
			}
		}
	}
	
	private class DormAverage {
		
		private int _size;
		private double _sum;
		
		public DormAverage() {
			_sum = 0;
			_size = 0;
		}
		
		public void add(double d) {
			_sum += d;
			++_size;
		}
		
		public double getAverage() {
			return (double) (_sum / _size);
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
			if (_scrollOnce && count > (Constants.RESULTS_LIST_WIDTH / _scrollWidth)) {
				break;
			}
			if (_scrollWidth == Constants.RESULTS_PANEL_HORIZONTAL_GAP) {
				break;
			}
			if (count == (Constants.RESULTS_LIST_WIDTH / _scrollWidth)) {
				_scrollWidth = Constants.RESULTS_PANEL_HORIZONTAL_GAP;
			}
			if (_resultsPanel.getSize().width < _scroller.getSize().width) {
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
				if (r.x + r.width + _scrollWidth >= _resultsPanel.getWidth()) {
					view.setViewPosition(new Point(_resultsPanel.getWidth() - r.width, r.y));
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
			if (button == _leftButton) {
				_scrollLeft = true;
				_scrollRight = false;
			}
			else if (button == _rightButton) {
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
