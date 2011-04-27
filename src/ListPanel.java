import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class ListPanel extends JPanel {

	private RoomList _list;
	private Map<JLabel, Room> _map;
	private ImageIcon _removeIcon = ImageIconLoader.getInstance().createImageIcon("images/remove_from_list_black.png", "remove from list");
	private JPanel _panel;
	private JScrollPane _scroller;
	private static Font _font = new Font("Verdana", Font.PLAIN, 12);
	
	private final int _listWidth = 200;
	private final int _listHeight = 525;
	private final int _itemWidth = 150;
	private final int _itemHeight = 15;
	
	public ListPanel(RoomList list) {
		super();
		_list = list;
		_map = new HashMap<JLabel, Room>();
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setSize(new Dimension(_listWidth, _listHeight));
		JLabel label = new JLabel(_list.getName());
		label.setBounds(0, 0, _listWidth, _itemHeight);
		
		//TODO bold
		label.setFont(new Font(_font.getFontName(), Font.BOLD, _font.getSize()));
		this.add(label);
		_panel = new JPanel();
		_panel.setPreferredSize(new Dimension(_listWidth, 0));
		_panel.setSize(new Dimension(_listWidth, 0));
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.PAGE_AXIS));
		_scroller = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setBounds(0, label.getHeight(), _listWidth, _listHeight - _itemHeight);
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
			Insets insets = new Insets(0, 5, 0, 5);
			p.setBorder(new EmptyBorder(insets));
			_map.put(removeLabel, r);
			Dimension size = _panel.getPreferredSize();
			_panel.setPreferredSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.setSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.add(p);
		}
	}
	
	public void addRoom(Room r) {
		_list.add(r);
		updateList();
	}
	
	public void removeRoom(Room r) {
		_list.remove(r);
		updateList();
	}
	
	private class RemoveListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			removeRoom(_map.get(label));
		}
		
	}
}
