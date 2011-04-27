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
	private boolean _scrollLeft, _scrollRight;
	
	public static ListsTab getInstance() {
		return _INSTANCE;
	}
	
	private ListsTab() {
		super();
		this.setPreferredSize(new Dimension(1000, 550));
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		_listsPanel = new JPanel();
		_listsPanel.setPreferredSize(new Dimension(20, 550));
		_listsPanel.setSize(new Dimension(20, 550));
		_listsPanel.setLayout(new BoxLayout(_listsPanel, BoxLayout.LINE_AXIS));
		_listsPanel.setBorder(new EmptyBorder(new Insets(0, 10, 0, 5)));
		_scroller = new JScrollPane(_listsPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.setPreferredSize(new Dimension(835, 550));
		_leftButton = new JLabel("LEFT");
		_leftButton.setFont(new Font("Verdana", Font.BOLD, 24));
		_rightButton = new JLabel("RIGHT");
		_rightButton.setFont(new Font("Verdana", Font.BOLD, 24));
		MouseListener listener = new ScrollButtonListener();
		_leftButton.addMouseListener(listener);
		_rightButton.addMouseListener(listener);
		this.add(_leftButton);
		this.add(Box.createHorizontalGlue());
		this.add(_scroller);
		this.add(Box.createHorizontalGlue());
		this.add(_rightButton);
		_lists = new LinkedList<ListPanel>();
		updateLists();
		_INSTANCE = this;
	}
	
	public void updateLists() {
		int i = 0;
		for (RoomList rl : State.getInstance().getRoomLists()) {
			if (i >= _lists.size()) {
				ListPanel lp = new ListPanel(rl);
				_lists.add(lp);
				_listsPanel.add(lp);
				_listsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
				Dimension size = _listsPanel.getPreferredSize();
				_listsPanel.setPreferredSize(new Dimension(size.width + 200 + 5, size.height));
				_listsPanel.setSize(new Dimension(size.width + 200 + 5, size.height));
			}
			i++;
		}
		for (ListPanel lp : _lists) {
			lp.updateList();
		}
	}
	
	@Override
	public void run() {
		while (_scrollLeft || _scrollRight) {
			System.out.println("p " + _listsPanel.getSize().width);
			System.out.println("s " + _scroller.getSize().width);
			if (_listsPanel.getSize().width < _scroller.getSize().width) {
				_scrollLeft = false;
				_scrollRight = false;
				break;
			}
			JViewport view = _scroller.getViewport();
			if (_scrollRight) {
				System.out.println("right");
				Rectangle r = view.getViewRect();
				if (r.x + r.width >= _listsPanel.getWidth()) {
					_scrollLeft = false;
					_scrollRight = false;
					break;
				}
				if (r.x + r.width + 25 >= _listsPanel.getWidth()) {
					view.setViewPosition(new Point(_listsPanel.getWidth() - r.width, r.y));
					_scrollRight = false;
					_scrollLeft = false;
				}
				else {
					view.setViewPosition(new Point(r.x + 25, r.y));
				}
				try {
					Thread.sleep(25);
				}
				catch (Exception e) {
					// do nothing
				}
			}
			else if (_scrollLeft) {
				System.out.println("left");
				Rectangle r = view.getViewRect();
				if (r.x <= 0) {
					_scrollLeft = false;
					_scrollRight = false;
					break;
				}
				if (r.x - 25 <= 0) {
					view.setViewPosition(new Point(0, r.y));
					_scrollLeft = false;
					_scrollRight = false;
					break;
				}
				else {
					view.setViewPosition(new Point(r.x - 25, r.y));
				}
				try {
					Thread.sleep(25);
				}
				catch (Exception e) {
					// do nothing
				}
			}
		}
	}
	
	private class ScrollButtonListener extends MouseAdapter {
		
//		@Override
//		public void mouseClicked(MouseEvent e) {
//			JLabel button = (JLabel) e.getSource();
//			if (button.getText().equals("LEFT")) {
//				System.out.println("Scroll left if possible");
//			}
//			else if (button.getText().equals("RIGHT")) {
//				System.out.println("Scroll right if possible");
//			}
//		}
//		
		@Override
		public void mousePressed(MouseEvent e) {
			JLabel button = (JLabel) e.getSource();
			if (button.getText().equals("LEFT")) {
				System.out.println("p left");
				_scrollLeft = true;
				_scrollRight = false;
			}
			else if (button.getText().equals("RIGHT")) {
				System.out.println("p right");
				_scrollRight = true;
				_scrollLeft = false;
			}
			Executor exec = Executors.newSingleThreadExecutor();
			exec.execute(ListsTab.this);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			JLabel button = (JLabel) e.getSource();
			if (button.getText().equals("LEFT")) {
				System.out.println("r left");
				_scrollLeft = false;
				_scrollRight = false;
			}
			else if (button.getText().equals("RIGHT")) {
				System.out.println("r right");
				_scrollLeft = false;
				_scrollRight = false;
			}
			Executor exec = Executors.newSingleThreadExecutor();
			exec.execute(ListsTab.this);
		}
	}
}
