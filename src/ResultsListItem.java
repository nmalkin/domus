import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
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
	private static ImageIcon _addToListIcon = ImageIconLoader.getInstance().createImageIcon("images/add_to_list_new.png", "add to list");
	private int _index;
	private boolean _isAddedToList;
	private Collection<RoomList> _roomList;
	private Insets _insets;
	private static Font _font = new Font("Verdana", Font.PLAIN, 12);
	
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
		_label.setFont(_font);
		this.add(_label);
		this.add(Box.createHorizontalGlue());
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener(this));
		this.add(_addButton);	
	}
	
	public void setInsets(Insets insets) {
		_insets = insets;
		this.setBorder(new EmptyBorder(_insets));
	}
	
	public Room getRoom() {
		return _room;
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
		
	public class RoomListSelectionListener implements ActionListener {
		
		private JDialog _prompt;
		
		public RoomListSelectionListener(JDialog prompt) {
			_prompt = prompt;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String selected = (String) cb.getSelectedItem();
			if (selected == null || selected.equals("")) {
				System.out.println("cancel");
			}
			else {
				boolean exists = false;
				for (RoomList rl : State.getInstance().getRoomLists()) {
					if (rl.getName().equals(selected)) {
						rl.add(_room);
						ListsTab.getInstance().updateLists();
						exists = true;
						break;
					}
				}
				if (!exists) {
					RoomList list = new RoomList(selected);
					State.getInstance().addRoomList(list);
					list.add(_room);
					ListsTab.getInstance().updateLists();
				}
			}
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}
	
} // end of ResultsListItem
