import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A single results inside a tab of results for a specific subgroup.
 * 
 * @author jswarren
 *
 */
public class ResultsListItem extends JPanel implements AccordionItem {

	private Room _room;
	private JLabel _label;
	private JLabel _addButton;
	private static ImageIcon _addToListIcon = ImageIconLoader.getInstance().createImageIcon("images/add_to_list.png", "add to list");
	private int _index;
	private boolean _isAddedToList;
	private Collection<RoomList> _roomList;
	private Insets _insets;
	
	private final int _itemHeight = 15;
	private final int _itemWidth = 200;
	
	public ResultsListItem(Room room) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		Dimension size = new Dimension(_itemWidth, _itemHeight);
		this.setPreferredSize(size);
		this.setSize(size);
		_room = room;
		_label = new JLabel(_room.getDorm().getName() + " " + _room.getNumber());
		this.add(_label);
		this.add(Box.createHorizontalGlue());
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener());
		this.add(_addButton);	
	}
	
	public void setInsets(Insets insets) {
		_insets = insets;
		this.setBorder(new EmptyBorder(_insets));
	}
	
	@Override
	public int getIndex() {
		return _index;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void setIndex(int index) {
		_index = index;
	}

	@Override
	public void setOpen(boolean open) { }

	@Override
	public void addItem(AccordionItem item) { }
	
	@Override
	public int compareTo(AccordionItem o) {
		return (_index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0));
	}

	private static ImageIcon createImageIcon(String path, String description) {
		URL imgURL = ResultsListTab.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
	private class AddListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO
			JOptionPane.showMessageDialog(ResultsListItem.this, "add to list (TODO)", "Domus", JOptionPane.PLAIN_MESSAGE);
		}
		
	}
	
}
