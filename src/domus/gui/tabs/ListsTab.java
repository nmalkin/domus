package domus.gui.tabs;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import domus.Constants;
import domus.State;
import domus.data.Group;
import domus.data.RoomList;
import domus.gui.lists.ListPanel;
import domus.gui.lotterynumberpanel.LotteryNumberPanel;

public class ListsTab extends JPanel implements Runnable {

	private static ListsTab _INSTANCE = new ListsTab();

	private JPanel _listsPanel;
	private JScrollPane _scroller;
	private List<ListPanel> _lists;
	private JLabel _leftButton, _rightButton;
	private ImageIcon _leftButtonIcon = new ImageIcon(Constants.LEFT_ARROW, "scroll left");
	private ImageIcon _rightButtonIcon = new ImageIcon(Constants.RIGHT_ARROW, "scroll right");
	private boolean _scrollLeft, _scrollRight, _scrollOnce;
	private int _scrollWidth, _scrollDelay;
	private JLabel _instructionsLabel;
	
	public static ListsTab getInstance() {
		return _INSTANCE;
	}
	
	private ListsTab() {
		super();
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(Constants.LISTS_PANEL_WIDTH, Constants.LISTS_HEIGHT));

		// create a panel for the instructions which will be displayed whenever there are no lists
		_instructionsLabel = new JLabel(Constants.LISTS_INSTRUCTIONS);
		_instructionsLabel.setPreferredSize(new Dimension(Constants.LISTS_INSTRUCTIONS_WIDTH, Constants.LISTS_INSTRUCTIONS_HEIGHT));
		_instructionsLabel.setFont(Constants.DOMUS_FONT.deriveFont(12f));
		
		// create the panel to hold the lists
		_listsPanel = new JPanel();
		_listsPanel.setLayout(new BoxLayout(_listsPanel, BoxLayout.LINE_AXIS));
		_listsPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		
		// get screen size
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		
		// add the instructions initially
		_listsPanel.add(_instructionsLabel);
		_listsPanel.setPreferredSize(new Dimension(_instructionsLabel.getPreferredSize().width, Math.min(size.height - 25, Constants.LISTS_HEIGHT)));
		_listsPanel.setSize(new Dimension(_instructionsLabel.getPreferredSize().width, Math.min(size.height - 25, Constants.LISTS_HEIGHT)));
		
		// create a scroll pane to hold the panel of lists
		_scroller = new JScrollPane(_listsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.setPreferredSize(new Dimension(_listsPanel.getSize().width, Math.min(size.height - 25, Constants.LISTS_HEIGHT)));
		_scroller.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		_scroller.getHorizontalScrollBar().setBorder(new EmptyBorder(0, 0, 0, 0));
		
		// create and add the scroll buttons which will be visible when necessary
		_leftButton = new JLabel(_leftButtonIcon);
		_leftButton.setVisible(false);
		_rightButton = new JLabel(_rightButtonIcon);
		_rightButton.setVisible(false);
		MouseListener listener = new ScrollButtonListener();
		_leftButton.addMouseListener(listener);
		_rightButton.addMouseListener(listener);
		
		// create a panel for the lists
		JPanel mainPanel = new JPanel();
		mainPanel.add(_leftButton);
		mainPanel.add(_scroller);
		mainPanel.add(_rightButton);
		this.add(mainPanel, BorderLayout.CENTER);
		
		// create and add a new LotteryNumberPanel
		this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
		
		// create a list to hold all of the individual lists
		_lists = new LinkedList<ListPanel>();
		_INSTANCE = this;
		
		// add a GroupStateChangeListener to the Group for lotteryNumber differences
		State.getInstance().getGroup().addGroupStateChangeListener(new GroupStateChangeListener());
	}
	
	public void updateLists() {
		int i = 0;
		for (RoomList rl : State.getInstance().getRoomLists()) {
			if (i >= _lists.size()) {
				// remove the instructions if no lists exist yet
				if (i == 0) {
					_listsPanel.removeAll();
					_listsPanel.setPreferredSize(new Dimension(0, Constants.LISTS_HEIGHT));
					_listsPanel.setSize(new Dimension(0, Constants.LISTS_HEIGHT));
					_scroller.setPreferredSize(new Dimension(0, Constants.LISTS_HEIGHT));
					_scroller.setPreferredSize(new Dimension(0, Constants.LISTS_HEIGHT));
				}
				
				// create a new list and add it to the lists panel, as well as a spacing component
				ListPanel lp = new ListPanel(rl);
				_lists.add(lp);
				_listsPanel.add(lp);
				int extra = 0;
				if (i != _lists.size()) {
					_listsPanel.add(Box.createRigidArea(new Dimension(Constants.LISTS_HORIZONTAL_GAP, 0)));
					extra = Constants.LISTS_HORIZONTAL_GAP;
				}
				
				// reset the size of the lists panel
				Dimension size = _listsPanel.getPreferredSize();
				_listsPanel.setPreferredSize(new Dimension(size.width + Constants.LISTS_WIDTH + extra, size.height));
				_listsPanel.setSize(new Dimension(size.width + Constants.LISTS_WIDTH + extra, size.height));
				
				// reset the size of the scroll pane if needed
				if (i < Constants.LISTS_DISPLAYED) {
					size = _scroller.getPreferredSize();
					_scroller.setPreferredSize(new Dimension(size.width + Constants.LISTS_WIDTH + extra, size.height));
					_scroller.setSize(new Dimension(size.width + Constants.LISTS_WIDTH + extra, size.height));
				}
			}
			i++;
		}
		
		// set the button visibility and update all pre-existing lists
		setButtonVisibility();
		for (ListPanel lp : _lists) {
			lp.updateList();
		}
	}
	
	/**
	 * Removes a list from the lists panel 
	 * @param list
	 */
	void removeList(ListPanel list) {
		// if the list doesn't exist, return
		if (!_lists.remove(list))
			return;
		
		// figure out which component the list belongs to
		Component[] comps = _listsPanel.getComponents();
		int index = -1;
		for (int i = 0; i < comps.length; ++i) {
			if (list == comps[i]) {
				index = i;
			}
		}
		
		// if none, return
		if (index == -1) {
			return;
		}
		
		// remove the component containing the list
		_listsPanel.remove(index);
		_listsPanel.remove(index);
		
		// reset the size of the lists panel
		Dimension size = _listsPanel.getPreferredSize();
		int extra = 0;
		if (_lists.size() > 0)
			extra = Constants.LISTS_HORIZONTAL_GAP;
		_listsPanel.setPreferredSize(new Dimension(size.width - Constants.LISTS_WIDTH - extra, size.height));
		_listsPanel.setSize(new Dimension(size.width - Constants.LISTS_WIDTH - extra, size.height));
		
		// reset the size of the scroll pane if necessary
		if (_lists.size() < Constants.LISTS_DISPLAYED) {
			size = _scroller.getPreferredSize();
			_scroller.setPreferredSize(new Dimension(size.width - Constants.LISTS_WIDTH - extra, size.height));
			_scroller.setSize(new Dimension(size.width - Constants.LISTS_WIDTH - extra, size.height));
		}
		
		// add the instructions back to the panel if there are no lists
		if (_lists.size() == 0) {
			_listsPanel.add(_instructionsLabel);
			_listsPanel.setPreferredSize(new Dimension(_instructionsLabel.getPreferredSize().width, Constants.LISTS_HEIGHT));
			_listsPanel.setSize(new Dimension(_instructionsLabel.getPreferredSize().width, Constants.LISTS_HEIGHT));
			_scroller.setPreferredSize(new Dimension(_listsPanel.getSize().width, Constants.LISTS_HEIGHT));
		}
		
		// set the button visibility
		setButtonVisibility();
		validate();
	}
	
	/**
	 * Sets the visibility of the scroll buttons.
	 * They are made visible if there are more than LISTS_DISPLAYED
	 * lists. Otherwise, they are hidden.
	 */
	public void setButtonVisibility() {
		boolean buttonsVisible = false;
		if (_lists.size() > Constants.LISTS_DISPLAYED)
			buttonsVisible = true;
		
		_leftButton.setVisible(buttonsVisible);
		_rightButton.setVisible(buttonsVisible);
	}
	
	/**
	 * Updates the display of each list. This includes
	 * probabilities and list labels.
	 */
	public void updateListsDisplay() {
	    for (ListPanel lp : _lists) {
            lp.updateDisplay();
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
			if (_scrollOnce && count > (Constants.LISTS_WIDTH / _scrollWidth)) {
				break;
			}
			if (_scrollWidth == Constants.LISTS_HORIZONTAL_GAP) {
				break;
			}
			if (count == (Constants.LISTS_WIDTH / _scrollWidth)) {
				_scrollWidth = Constants.LISTS_HORIZONTAL_GAP;
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
			exec.execute(ListsTab.this);
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
//			exec.execute(ListsTab.this);
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
	
	/**
	 * Listens for changes in the sophomoreOnly status. If the results tab is
	 * visible and this happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class GroupStateChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (!((Group.GroupChangeEvent) e).getUpdateType())
				updateListsDisplay();
		}
		
	}
	
}
