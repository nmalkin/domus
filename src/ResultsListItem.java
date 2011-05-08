import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A single result inside a tab of results for a specific subgroup.
 * 
 * @author jswarren
 *
 */
public class ResultsListItem extends JPanel implements AccordionItem {

	private AccordionList<ResultsListTab, ResultsListItem> _parentList;
	private Room _room;
	private JLabel _label;
	private JLabel _probLabel;
	private JLabel _addButton;
	private JPanel _labelsPanel;
	private boolean _isOpen;
	private static ImageIcon _addToListIcon = new ImageIcon(Constants.ADD_FILE, "add to list");
	private int _index;
	private int _numLists;
	private static Font _unselectedFont = new Font("Verdana", Font.PLAIN, 12);
	private static Color _unselectedBackgroundColor;
	private static Color _selectedBackgroundColor;
	
	private final int _itemHeight = 15;
	private final int _itemWidth = 350;
	
	public ResultsListItem(Room room, AccordionList<ResultsListTab, ResultsListItem> list) {
		super();
		_parentList = list;
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		Dimension size = new Dimension(_itemWidth, _itemHeight);
		this.setPreferredSize(size);
		this.setSize(size);
		_numLists = 0;
		_room = room;
		_room.addToListItem(this);
		_index = _room.getAverageResult();
		_isOpen = false;
		_label = new JLabel(_room.getDorm().getName() + " " + _room.getNumber());
		_label.setFont(_unselectedFont);
		this.add(Box.createRigidArea(new Dimension(Constants.OPEN_ICON_WIDTH - 1, 0)));
		this.add(_label);
		_labelsPanel = new JPanel();
		_labelsPanel.setLayout(new BoxLayout(_labelsPanel, BoxLayout.LINE_AXIS));
		_labelsPanel.setBackground(new Color(255, 255, 255, 0));
		this.add(Box.createRigidArea(new Dimension(5, 0)));
		this.add(_labelsPanel);
		this.add(Box.createHorizontalGlue());
		String probabilityString = "" + (int) (_room.getProbability() * 100);
		_probLabel = new JLabel("[" + probabilityString + "%]");
		_probLabel.setFont(_unselectedFont);
		this.add(_probLabel);
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener(this));
		this.add(_addButton);
		this.add(Box.createRigidArea(new Dimension(Constants.INSET + 1, 0)));
		this.addMouseListener(new SelectedListener());
		_unselectedBackgroundColor = this.getBackground();
		_selectedBackgroundColor = _unselectedBackgroundColor.darker();
		validateListLabels();
	}
	
	/** Makes sure the list labels for this item are updated */
	public void validateListLabels() {
		_labelsPanel.removeAll();
		if (_room.getRoomLists() != null) {
			for (RoomList rl : _room.getRoomLists()) {
				addListLabel(rl);
			}
		}
	}
	
	/** Adds a list label for this item */
	private void addListLabel(RoomList list) {
		if (list.getColor() == null) {
			list.setColor(null);
		}
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(list.getColor());
		g.fillRect(0, 0, 10, 10);
		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);
		_labelsPanel.add(label);
		_labelsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		validate();
	}
	
	/** Returns the room associated with this item */
	public Room getRoom() {
		return _room;
	}
	
	@Override
	public int getComparisonValue() {
		return _index;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void setComparisonValue(int index) { }
	
	/** 
	 * ResultsListItems cannot be open, so instead this
	 * sets the selection state of this item. It bolds
	 * the item's label if it is selected.
	 */
	@Override
	public void setOpen(boolean open) {
		if (open) {
			this.setBackground(_selectedBackgroundColor);
			_isOpen = true;
		}
		else {
			this.setBackground(_unselectedBackgroundColor);
			_isOpen = false;
		}
	}
	
	@Override
	public void addItem(AccordionItem item) { }
	
	@Override
	public void removeItem(AccordionItem item) { }
	
	@Override
	public void resizeItem(Dimension d) { }
	
	@Override
	public int compareTo(AccordionItem o) {
		return _index < o.getComparisonValue() ? -1 : (_index > o.getComparisonValue() ? 1 : 0);
	}
	
	/** Handles adding this item to a specific user created list */
	public class RoomListSelectionListener implements ActionListener {
		
		private JDialog _prompt;
		
		public RoomListSelectionListener(JDialog prompt) {
			_prompt = prompt;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String selected = (String) cb.getSelectedItem();
			if (selected != null && !selected.equals("")) {
				boolean exists = false;
				for (RoomList rl : State.getInstance().getRoomLists()) {
					if (rl.getName().equals(selected)) {
						rl.add(_room);
						_room.addToRoomList(rl);
						ListsTab.getInstance().updateLists();
						exists = true;
						break;
					}
				}
				if (!exists) {
					RoomList list = new RoomList(selected);
					State.getInstance().addRoomList(list);
					list.add(_room);
					_room.addToRoomList(list);
					++_numLists;
					ListsTab.getInstance().updateLists();
				}
			}
			for (ResultsListItem rli : getRoom().getListItems())
				rli.validateListLabels();
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}
	
	/** Bolds the item when clicked */
	private class SelectedListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			setOpen(!isOpen());
			if (isOpen())
				_parentList.setSelectedItem(ResultsListItem.this);
			else
				_parentList.setSelectedItem(null);
		}
	}
	
} // end of ResultsListItem
