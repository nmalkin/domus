import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.border.MatteBorder;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class ResultsPanel extends JPanel implements Runnable {

	private Map<SubGroup, AccordionList<ResultsListTab, ResultsListItem>> _listsMap;
	private Multimap<SubGroup, Room> _results;
	private JScrollPane _scroller;
	private JPanel _resultsPanel;
	private JLabel _leftButton, _rightButton;
	private ImageIcon _leftButtonIcon = new ImageIcon(Constants.LEFT_ARROW, "scroll left");
	private ImageIcon _rightButtonIcon = new ImageIcon(Constants.RIGHT_ARROW, "scroll right");
	private boolean _scrollLeft, _scrollRight, _scrollOnce;
	private int _scrollWidth, _scrollDelay;
	
	private int _listWidth, _listHeight;

	public ResultsPanel() {
		super();
		_listWidth = Constants.RESULTS_LIST_WIDTH;
		_listHeight = Constants.RESULTS_PANEL_HEIGHT;
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		_resultsPanel = new JPanel();
		_resultsPanel.setPreferredSize(new Dimension(0, _listHeight));
		_resultsPanel.setSize(new Dimension(0, _listHeight));
		_resultsPanel.setLayout(new BoxLayout(_resultsPanel, BoxLayout.LINE_AXIS));
		_resultsPanel.setBorder(Constants.EMPTY_BORDER);
		_scroller = new JScrollPane(_resultsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.setPreferredSize(new Dimension(0, _listHeight + _scroller.getHorizontalScrollBar().getPreferredSize().height));
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
	
	public void updateHeight(int height) {
		_listHeight = height;
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		_resultsPanel.setPreferredSize(new Dimension(0, _listHeight));
		_resultsPanel.setSize(new Dimension(0, _listHeight));
		_scroller.setPreferredSize(new Dimension(0, _listHeight + _scroller.getHorizontalScrollBar().getPreferredSize().height));
		for (AccordionList<ResultsListTab, ResultsListItem> list : _listsMap.values()) {
			list.updateHeight(height);
		}
	}

	public void updateResultsLists(boolean updateEverything) {
		// if only updating probability displays, do so and return
		if (!updateEverything) {
			updateProbabilities();
			return;
		}

		// set up some variables
		SetMultimap<SubGroup, Dorm> dormMap = TreeMultimap.create(Ordering.natural(), new DormComparator());

		// get the results from State
		_results = State.getInstance().getResults();

		// create a multimap from SubGroup to Dorms
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				dormMap.put(sg, r.getDorm());
			}
		}

		// Remove any AccordionLists which shouldn't exist any more
		// (because the corresponding SubGroup was deleted)
		Collection<SubGroup> oldSubGroups = _listsMap.keySet();
		for (Iterator<SubGroup> iter = oldSubGroups.iterator(); iter.hasNext();) {
			SubGroup sg = iter.next();
			if (!dormMap.containsKey(sg)) {
				if (_listsMap.get(sg) != null)
					_resultsPanel.remove(_listsMap.get(sg));
				iter.remove();
			}
		}

		for(House h: State.getInstance().getGroup()) {
			for (SubGroup sg : h) {
				// if there is not an existing AccordionList for this SubGroup, create a new one
				AccordionList<ResultsListTab, ResultsListItem> list = null;
				if ((list = _listsMap.get(sg)) == null) {
					list = AccordionList.create(Constants.RESULTS_LIST_WIDTH, _listHeight - Constants.RESULTS_HEADER_HEIGHT, Constants.RESULTS_HEADER_HEIGHT);
					_listsMap.put(sg, list);
					_resultsPanel.add(list);
				}
				
				JPanel headerTop = createListHeader(sg);
				
				if(h.getLocationPreference().size() != 0) {

					if(dormMap.keySet().contains(sg)) {
						// set the header of the AccordionList (Person icons and names)
						list.setHeader(headerTop);
						
						// get the old ResultsListTabs from the AccordionList (could be empty if new list)
						Collection<ResultsListTab> tabs = list.getTabs();

						// remove any ResultsListTabs which don't have a corresponding Dorm for this SubGroup 
						for (Iterator<ResultsListTab> iter = tabs.iterator(); iter.hasNext();) {
							ResultsListTab rlt = iter.next();
							if (!dormMap.get(sg).contains(rlt.getDorm())) {
								iter.remove();
								list.removeTab(rlt);
							}
						}

						for (Dorm d : dormMap.get(sg)) {
							// if there is an existing ResultsListTab for this Dorm, update it
							// otherwise, create a new one
							ResultsListTab tab = containsDormTab(tabs, d);
							if (tab != null) {
								intersectResultsWithTab(sg, d, list, tab);
							}
							else {
								// create a new ResultsListTab and add all the rooms to it
								tab = new ResultsListTab(d, sg, list);
								list.addTab(tab);
								for (Room r : _results.get(sg)) {
									if (r.getDorm() == d) {
										addListItem(list, tab, r);
									}
								}
							}

							// make sure tabs display the average of their components' probabilities
							tab.updateProbabilities();

							// make sure the ResultsListTab is displaying all of the list labels it should be
							tab.validateListLabels();
						}	
					}
					else {
						JLabel message = new JLabel("Sorry! We found no available results.");
						message.setHorizontalAlignment(JLabel.CENTER);
						message.setBorder(new MatteBorder(1,1,0,1,Color.BLACK));
						JPanel header = new JPanel(new BorderLayout());
						header.add(headerTop, BorderLayout.NORTH);
						header.add(message, BorderLayout.SOUTH);
						
						list.setHeader(header);
					}
				}
				else {
					
					JLabel message = new JLabel("Select some preferences first!");
					message.setHorizontalAlignment(JLabel.CENTER);
					message.setBorder(new MatteBorder(1,1,0,1,Color.BLACK));
					JPanel header = new JPanel(new BorderLayout());
					header.add(headerTop, BorderLayout.NORTH);
					header.add(message, BorderLayout.SOUTH);
					
					list.setHeader(header);
				}
			}
		}

		// make sure that size of the panel is updated properly
		int numLists = _listsMap.values().size();
		int displayLists = Math.min(numLists, Constants.RESULTS_LISTS_DISPLAYED);
		Dimension size = new Dimension(numLists * Constants.RESULTS_LIST_WIDTH + ((numLists - 1) * Constants.RESULTS_PANEL_HORIZONTAL_GAP), _listHeight);
		_resultsPanel.setPreferredSize(size);
		_resultsPanel.setSize(size);

		// that goes for the scroll pane as well
		size = new Dimension(displayLists * Constants.RESULTS_LIST_WIDTH + ((displayLists -1) * Constants.RESULTS_PANEL_HORIZONTAL_GAP), _scroller.getSize().height);
		_scroller.setPreferredSize(size);
		_scroller.setSize(size);

		// show or hide the scroll buttons as necessary
		boolean buttonsVisible = false;
		if (numLists > Constants.RESULTS_LISTS_DISPLAYED)
			buttonsVisible = true;
		_leftButton.setVisible(buttonsVisible);
		_rightButton.setVisible(buttonsVisible);
	}

	/**
	 * Creates a header for the AccordionList. The header
	 * consists of a Person icon for each person in the SubGroup,
	 * as well as the names of the Person objects, if they have
	 * been named by the user.
	 * 
	 * @param sg, the SubGroup for which to create a header
	 * @return the header, a JPanel
	 */
	private JPanel createListHeader(SubGroup sg) {
		// create a new JPanel, and set its size appropriately
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_HEADER_HEIGHT));
		panel.setSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_HEADER_HEIGHT));
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		String names = "";
		int i = 0;

		// create an ImageIcon for both the man and the woman Person objects
		// We can use a single instance for all labels
		// created by scaling the images from the Canvas
		ImageIcon man = new ImageIcon(Gender.MALE.getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH));
		ImageIcon woman = new ImageIcon(Gender.FEMALE.getImage().getScaledInstance(-1, 30, Image.SCALE_SMOOTH));

		//determine how much width is left on the header after the icons are added
		int width = Constants.RESULTS_LIST_WIDTH - (man.getIconWidth() * sg.getOccupancy());

		for (Person p : sg) {
			// add the correct icon for each Person
			ImageIcon icon = man;
			if (p.getGender() == Gender.FEMALE)
				icon = woman;
			JLabel label = new JLabel(icon);
			panel.add(label);
			panel.add(Box.createRigidArea(new Dimension(5, 0)));

			// add the Person's name to the label if they have been named
			// Make sure to use correct grammar, and of course,
			// an Oxford comma if necessary
			if (! p.getName().equals(Constants.NEW_PERSON_DEFAULT_NAME)) {
				//set the tooltip for the icon if the Person has been named
				label.setToolTipText(p.getName());
				if (i == 0)
					names += p.getName();
				else if (i == sg.getOccupancy() - 1) {
					if (i > 1)
						names += ",";
					if (!names.equals(""))
						names += " and ";
					names += p.getName();
				}
				else
					names += ", " + p.getName();
			}
			++i;
		}

		// create a new label for the names
		JLabel nameLabel = new JLabel(names);
		nameLabel.validate();
		boolean fix = false;

		// if the label is too wide for the header, shrink it down
		while (nameLabel.getPreferredSize().width > width) {
			String text = nameLabel.getText();
			text = text.substring(0, text.length() - 2);
			nameLabel.setText(text);
			nameLabel.validate();
			fix = true;
		}

		// if it was too wide, shrink it a little more and add "..." to the end
		if (fix) {
			String text = nameLabel.getText();
			text = text.substring(0, text.length() - 2 - "...".length());
			text += "...";
			nameLabel.setText(text);
		}

		// if the names label is not empty, set the tooltip in case it is too long
		if (nameLabel.getText().equals(""))
			nameLabel.setToolTipText(names);

		// add the names label and return the header 
		panel.add(nameLabel);
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

	/**
	 * Performs an intersection on the results already in the ResultsListTab, and
	 * those returned from the database for the corresponding Drom.
	 * 
	 * @param sg, the SubGroup to which the Dorm and ResultsListTab being merged belong
	 * @param d, the Dorm to merge with the ResultsListTab
	 * @param list, the AccordionList the ResultsListTab belongs too
	 * @param tab, the ResultsListTab to merge with the Dorm
	 */
	private void intersectResultsWithTab(SubGroup sg, Dorm d, AccordionList<ResultsListTab, ResultsListItem> list, ResultsListTab tab) {
		// get the results for the SubGroup
		Collection<Room> roomResults = _results.get(sg);

		// get the itmes already present in the ResultsListTab
		// add the corresponding Rooms to a list
		Collection<ResultsListItem> tabItems = tab.getItems();
		List<Room> tabRooms = new LinkedList<Room>();
		if (tabItems != null) {
			for (ResultsListItem rli : tabItems) {
				tabRooms.add(rli.getRoom());
			}
		}
		// if the ResultsListTab is empty, add all of the Rooms in the results to it
		// then return
		else {
			for (Room r : roomResults) {
				if (r.getDorm() == tab.getDorm())
					addListItem(list, tab, r);
			}
			return;
		}

		// for each ResultsListItem already in the ResultsListTab
		// remove it if its room is not in the returned results
		for (ResultsListItem item : tabItems) {
			Room r = item.getRoom();
			if (!roomResults.contains(r)) {
				list.removeListItem(tab, item);
			}
		}

		// for each Room in the returned results, add it to the
		// ResultsListTab if it's not already there
		for (Room r : roomResults) {
			if (r.getDorm() == tab.getDorm()) {
				if (!tabRooms.contains(r)) {
					addListItem(list, tab, r);
				}
			}
		}

		// if the ResultsListTab is now empty, remove it
		if (tab.getItems().size() == 0) {
			list.removeTab(tab);
		}
	}

	/**
	 * Adds a Room (via a ResultsListItem) to the ResultsListTab.
	 * 
	 * @param list, the AccordionList to which to add the ResultsListItem
	 * @param tab, the ResultsListTab to which to add the ResultsListItem
	 * @param r, the Room to add to the ResultsListTab
	 */
	private void addListItem(AccordionList<ResultsListTab, ResultsListItem> list, ResultsListTab tab, Room r) {
		ResultsListItem item = new ResultsListItem(r, list);
		list.addListItem(tab, item);
	}

	/**
	 * Updates the probabilities for each AccordionList
	 */
	private void updateProbabilities() {
		for (AccordionList<ResultsListTab, ResultsListItem> list : _listsMap.values()) {
			for (ResultsListTab tab : list.getTabs()) {
				tab.updateProbabilities();
			}
		}
	}

	/**
	 * Compares Dorms by CampusArea (alphabetically) and then
	 * lexicographically inside of that.
	 * 
	 * @author jswarren
	 */
	private class DormComparator implements Comparator<Dorm> {

		Map<Dorm, CampusArea> _campusAreas;

		public DormComparator() {
			_campusAreas = new HashMap<Dorm, CampusArea>();
			for (CampusArea ca : Database.getCampusAreas()) {
				for (Dorm d : ca)
					_campusAreas.put(d, ca);
			}
		}

		@Override
		public int compare(Dorm o1, Dorm o2) {
			// TODO Auto-generated method stub
			CampusArea ca1 = _campusAreas.get(o1);
			CampusArea ca2 = _campusAreas.get(o2);

			int result = ca1.compareTo(ca2);
			if (result < 0)
				return -1;
			if (result > 1)
				return 1;
			return o1.compareTo(o2);
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

	/**
	 * Listens for mouseClicks on the scroll buttons (if they are
	 * visible) and scrolls the panel appropriately.
	 * 
	 * @author jswarren
	 */
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
