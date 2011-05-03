import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
	private Map<JLabel, Room> _map;
	private ImageIcon _removeIcon = new ImageIcon(Constants.REMOVE_FILE, "remove from list");
	private JList _panel;
	private JScrollPane _scroller;
	private static Font _font = new Font("Verdana", Font.PLAIN, 12);
	private static Insets _insets = new Insets(0, 5, 0, 5);
	
	private final int _listWidth = 350;
	private final int _listHeight = 500;
	private final int _itemWidth = 150;
	private final int _itemHeight = 15;
	
	public ListPanel(RoomList list) {
		super();
		_list = list;
		_map = new HashMap<JLabel, Room>();
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setSize(new Dimension(_listWidth, _listHeight));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBounds(0, 0, _listWidth, _itemHeight);
		JLabel label = new JLabel(_list.getName());
		label.setFont(new Font(_font.getFontName(), Font.BOLD, _font.getSize()));
		panel.add(label);
		JLabel removeLabel = new JLabel(_removeIcon);
		removeLabel.addMouseListener(new RemoveListListener());;
		panel.add(Box.createHorizontalGlue());
		panel.add(removeLabel);
		panel.setBorder(new EmptyBorder(new Insets(_insets.top, _insets.left + 1, _insets.bottom, _insets.right + 1)));
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
	
	public void updateList() {
		_panel.removeAll();
		_panel.setPreferredSize(new Dimension(_itemWidth, 0));
		_panel.setSize(new Dimension(_itemWidth, 0));
		for (Room r : _list) {
			JPanel p = new JPanel();
			p.setPreferredSize(new Dimension(_itemWidth, _itemHeight));
			p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
			JLabel label = new JLabel(r.getDorm() + " " + r.getNumber());
			label.setFont(_font);
			p.add(label);
			p.add(Box.createHorizontalGlue());
			JLabel removeLabel = new JLabel(_removeIcon);
			removeLabel.addMouseListener(new RemoveListener());
			p.add(removeLabel);
			p.setBorder(new EmptyBorder(_insets));
			_map.put(removeLabel, r);
			Dimension size = _panel.getPreferredSize();
			_panel.setPreferredSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.setSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.add(p);
		}
	}
	
	/** Add a room to list */
	public void addRoom(Room r) {
		_list.add(r);
		updateList();
	}
	
	/** Remove a room from list */
	public void removeRoom(Room r) {
		_list.remove(r);
		updateList();
	}
	
	/** Listener for room removal */
	private class RemoveListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			removeRoom(_map.get(label));
		}
		
	}
	
	/** Listener for list removal */
	private class RemoveListListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			State.getInstance().removeRoomList(_list);
			_list = null;
			ListsTab.getInstance().removeList(ListPanel.this);
			ListsTab.getInstance().updateLists();
		}
	}
}
