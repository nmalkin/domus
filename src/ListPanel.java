import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class ListPanel extends JPanel {

	private RoomList _list;
	private ImageIcon _removeIcon = new ImageIcon(Constants.REMOVE_FILE, "remove from list");
	private JList _panel;
	private JScrollPane _scroller;
	private static Font _font = new Font("Verdana", Font.PLAIN, 12);
	
	private final int _listWidth = 350;
	private final int _listHeight = 500;
	private final int _itemWidth = 150;
	private final int _itemHeight = 15;
	
	public ListPanel(RoomList list) {
		super();
		_list = list;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setSize(new Dimension(_listWidth, _listHeight));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBounds(0, 0, _listWidth, _itemHeight);
		JLabel label = new JLabel(_list.getName());
		label.setFont(new Font(_font.getFontName(), Font.BOLD, _font.getSize()));
		if (_list.getColor() == null) {
			_list.setColor(null);
		}
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(_list.getColor());
		g.fillRect(0, 0, 10, 10);
		ImageIcon icon = new ImageIcon(image);
		label.setIcon(icon);
		label.setHorizontalTextPosition(JLabel.LEADING);
		label.setIconTextGap(10);
		panel.add(label);
		JLabel removeLabel = new JLabel(_removeIcon);
		removeLabel.addMouseListener(new RemoveListListener());
		panel.add(Box.createHorizontalGlue());
		removeLabel.setVisible(false);
		panel.addMouseListener(new HoverListener());
		removeLabel.addMouseListener(new RemoveListListener());;
		panel.add(Box.createHorizontalGlue());
		panel.add(removeLabel);
		panel.setBorder(new EmptyBorder(0, Constants.INSET + 1, 0, Constants.INSET + 1));
		this.add(panel);
		_panel = new JList();
		_panel.setPreferredSize(new Dimension(_listWidth, 0));
		_panel.setSize(new Dimension(_listWidth, 0));
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.PAGE_AXIS));
		Color c = this.getBackground();
		_panel.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
		_scroller = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setBounds(0, panel.getHeight(), _listWidth, _listHeight - _itemHeight);
		this.add(_scroller);
		updateList();
	}
	
	/**
	 * Updates the list by removing all components and
	 * then adding all existing ones.
	 */
	public void updateList() {
		_panel.removeAll();
		_panel.setPreferredSize(new Dimension(_itemWidth, 0));
		_panel.setSize(new Dimension(_itemWidth, 0));
//		MouseListener hoverListener = new HoverListener();
		for (ResultsListItem r : _list) {
			r.setButtonIcon(_removeIcon);
			r.setButtonMouseListener(new RemoveListener());
//			removeLabel.setVisible(false);
//			removeLabel.addMouseListener(new RemoveListener());
//			r.addMouseListener(hoverListener);
//			_roomMap.put(removeLabel, r);
			Dimension size = _panel.getPreferredSize();
			_panel.setPreferredSize(new Dimension(size.width, size.height + r.getSize().height));
			_panel.setSize(new Dimension(size.width, size.height + r.getSize().height));
			_panel.add(r);
			r.updateProbability();
		}
		
	}
	
	/** Removes a ResultsListItem from list */
	public void removeResultsListItem(ResultsListItem r) {
		_list.remove(r);
		r.getRoom().removeFromRoomList(_list);
		r.getRoom().removeFromListItem(r);
		for (ResultsListItem rli : r.getRoom().getListItems()) {
			rli.validateListLabels();
		}
		updateList();
	}
	
	/** Listener for removal button display */
	private class HoverListener extends MouseAdapter {
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//show the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 1);
			label.setVisible(true);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			//hide the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 1);
			if (panel.getComponentAt(e.getX(), e.getY()) != label)
				label.setVisible(false);
		}
		
	}
	
	
	/** Listener for ResultsListItem removal */
	private class RemoveListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			ResultsListItem item = (ResultsListItem) label.getParent();
			removeResultsListItem(item);
		}

	}
	
	/** Listener for list removal */
	private class RemoveListListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			ResultsListItem[] items = _list.toArray(new ResultsListItem[0]);
			for (int i = 0; i < items.length; ++i) {
				removeResultsListItem(items[i]);
			}
			State.getInstance().removeRoomList(_list);
			_list.clear();
			_list = null;
			ListsTab.getInstance().removeList(ListPanel.this);
			ListsTab.getInstance().updateLists();
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			label.setVisible(false);
		}
		
	}
	
}
