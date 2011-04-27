import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class ListPanel extends JPanel {

	private RoomList _list;
	private ImageIcon _removeIcon = ImageIconLoader.getInstance().createImageIcon("images/remove_from_list_black.png", "remove from list");
	private JPanel _panel;
	private JScrollPane _scroller;
	
	private final int _listWidth = 200;
	private final int _listHeight = 500;
	private final int _itemWidth = 150;
	private final int _itemHeight = 15;
	
	public ListPanel(RoomList list) {
		super();
		_list = list;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setSize(new Dimension(_listWidth, _listHeight));
//		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		((FlowLayout) this.getLayout()).setAlignment(FlowLayout.LEADING);
		System.out.println("name: " + _list.getName());
		JLabel label = new JLabel(_list.getName());
		label.setBounds(0, 0, _listWidth, _itemHeight);
		
		//TODO bold
//		label.setFont(BOLD);
		this.add(label);
		_panel = new JPanel();
		_panel.setPreferredSize(new Dimension(_listWidth, 0));
		_panel.setSize(new Dimension(_listWidth, 0));
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.PAGE_AXIS));
		_scroller = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		_scroller.setPreferredSize(new Dimension(_listWidth, 25));
		_scroller.setBounds(0, label.getHeight(), _listWidth, _listHeight - _itemHeight);
//		_scroller.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(_scroller);
		updateList();
	}
	
//	public ListPanel() {
//		super();
//		_list = new RoomList();
//		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
//		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//	}
	
	public void updateList() {
		_panel.removeAll();
		_panel.setPreferredSize(new Dimension(_itemWidth, 0));
		for (Room r : _list) {
			JPanel p = new JPanel();
			p.setPreferredSize(new Dimension(_itemWidth, _itemHeight));
			p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
			p.add(new JLabel(r.getDorm() + " " + r.getNumber()));
			p.add(Box.createHorizontalGlue());
			p.add(new JLabel(_removeIcon));
			Insets insets = new Insets(0, 5, 0, 25);
			p.setBorder(new EmptyBorder(insets));
			Dimension size = _panel.getPreferredSize();
			System.out.println("p " + p.getHeight() + " " + _removeIcon.getIconHeight());
			System.out.println("ps " + p.getPreferredSize().height);
			System.out.println("s " + p.getSize().height);
			_panel.setPreferredSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.setSize(new Dimension(size.width, size.height + _itemHeight));
			_panel.add(p);
		}
		System.out.println(_panel.getHeight() + " ps " + _panel.getPreferredSize().height);
//		revalidate();
	}
	
	public void addRoom(Room r) {
		_list.add(r);
		updateList();
	}
	
	public void removeRoom(Room r) {
		_list.remove(r);
		updateList();
	}
}
