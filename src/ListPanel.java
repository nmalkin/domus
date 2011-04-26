import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ListPanel extends JPanel {

	private RoomList _list;
	private ImageIcon _removeIcon = ImageIconLoader.getInstance().createImageIcon("images/remove_from_list.png", "remove from list");
	private int _index;
	
	private final int _listWidth = 200;
	private final int _listHeight = 500;
	private final int _itemHeight = 25;
	
	public ListPanel(RoomList list, int index) {
		super();
		_list = list;
		_index = index;
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		updateList();
	}
	
	public ListPanel(int index) {
		super();
		_list = new RoomList();
		_index = index;
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public void updateList() {
		this.removeAll();
		this.add(new JLabel(_list.getName()));
		for (Room r : _list) {
			JPanel p = new JPanel();
			p.setPreferredSize(new Dimension(_listWidth, _itemHeight));
			p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
			p.add(new JLabel(r.getDorm() + " " + r.getNumber()));
			p.add(Box.createHorizontalGlue());
			p.add(new JLabel(_removeIcon));
			this.add(p);
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
}
