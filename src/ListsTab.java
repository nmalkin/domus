import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
	
	private int _listWidth = 350;
	private int _listHeight = 525;
	private int _numDisplayLists = 3;
	private int _hGap = 5;
	
	public static ListsTab getInstance() {
		return _INSTANCE;
	}
	
	private ListsTab() {
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
		_lists = new LinkedList<ListPanel>();
		_INSTANCE = this;
	}
	
	public void updateLists() {
		int i = 0;
		for (RoomList rl : State.getInstance().getRoomLists()) {
			if (i >= _lists.size()) {
				ListPanel lp = new ListPanel(rl);
				_lists.add(lp);
				_listsPanel.add(lp);
				int extra = 0;
				if (i != _lists.size()) {
					_listsPanel.add(Box.createRigidArea(new Dimension(_hGap, 0)));
					extra = _hGap;
				}
				Dimension size = _listsPanel.getPreferredSize();
				_listsPanel.setPreferredSize(new Dimension(size.width + _listWidth + extra, size.height));
				_listsPanel.setSize(new Dimension(size.width + _listWidth + extra, size.height));
				if (i < _numDisplayLists) {
					size = _scroller.getPreferredSize();
					_scroller.setPreferredSize(new Dimension(size.width + _listWidth + extra, size.height));
					_scroller.setSize(new Dimension(size.width + _listWidth + extra, size.height));
				}
			}
			i++;
		}
		setButtonVisibility();
		for (ListPanel lp : _lists) {
			lp.updateList();
		}
	}
	
	public void removeList(ListPanel list) { 
		_lists.remove(list);
		Component[] comps = _listsPanel.getComponents();
		int index = -1;
		for (int i = 0; i < comps.length; ++i) {
			if (list == comps[i]) {
				index = i;
			}
		}
		if (index == -1) {
			return;
		}
		_listsPanel.remove(index);
		_listsPanel.remove(index);
		Dimension size = _listsPanel.getPreferredSize();
		int extra = 0;
		if (_lists.size() > 0)
			extra = _hGap;
		_listsPanel.setPreferredSize(new Dimension(size.width - _listWidth - extra, size.height));
		_listsPanel.setSize(new Dimension(size.width - _listWidth - extra, size.height));
		if (_lists.size() < _numDisplayLists) {
			size = _scroller.getPreferredSize();
			_scroller.setPreferredSize(new Dimension(size.width - _listWidth - extra, size.height));
			_scroller.setSize(new Dimension(size.width - _listWidth - extra, size.height));
		}
		setButtonVisibility();
		validate();
	}
	
	public void setButtonVisibility() {
		boolean buttonsVisible = false;
		if (_lists.size() > _numDisplayLists)
			buttonsVisible = true;
		_leftButton.setVisible(buttonsVisible);
		_rightButton.setVisible(buttonsVisible);
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
}
