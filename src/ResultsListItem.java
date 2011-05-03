import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

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

	private AccordionList<ResultsListTab, ResultsListItem> _parentList;
	private Room _room;
	private JLabel _label;
	private JLabel _probLabel;
	private JLabel _addButton;
	private JPanel _labelsPanel;
	private boolean _labelsValidated;
	private static ImageIcon _addToListIcon = new ImageIcon(Constants.ADD_FILE, "add to list");
	private int _index;
	private int _numLists;
	private Insets _insets;
	private static Font _unselectedFont = new Font("Verdana", Font.PLAIN, 12);
	private static Font _selectedFont = new Font("Verdana", Font.BOLD, 12);
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
		_labelsValidated = false;
		_label = new JLabel(_room.getDorm().getName() + " " + _room.getNumber());
		_label.setFont(_unselectedFont);
		this.add(_label);
		_labelsPanel = new JPanel();
		_labelsPanel.setLayout(new BoxLayout(_labelsPanel, BoxLayout.LINE_AXIS));
		_labelsPanel.setBackground(new Color(255, 255, 255, 0));
		validateListLabels();
		this.add(Box.createRigidArea(new Dimension(5, 0)));
		this.add(_labelsPanel);
		this.add(Box.createHorizontalGlue());
		_probLabel = new JLabel("[" + _room.getProbability() + "%]");
		_probLabel.setFont(_unselectedFont);
		this.add(_probLabel);
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener(this));
		this.add(_addButton);
		this.addMouseListener(new SelectedListener());
		_unselectedBackgroundColor = this.getBackground();
		_selectedBackgroundColor = _unselectedBackgroundColor.darker();
	}
	
	/** Makes sure the list labels for this item are updated */
	private void validateListLabels() {
		_labelsValidated = true;
		_labelsPanel.removeAll();
		if (_room.getRoomLists() != null) {
			for (RoomList rl : _room.getRoomLists()) {
				addListLabel(rl);
			}
		}
		for (ResultsListItem rli : _room.getListItems()) {
			if (!rli.equals(this) && !rli.hasValidatedLabels())
				rli.validateListLabels();
		}
	}
	
	/** Adds a list label for this item */
	private void addListLabel(RoomList list) {
		if (list.getColor() == null) {
			list.setColor(null);
			System.out.println(list.getColor());
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
	}
	
	/** Sets the insets for this item to match those of the tab */
	public void setInsets(Insets insets) {
		_insets = insets;
		this.setBorder(new EmptyBorder(_insets));
	}
	
	/** Returns the room associated with this item */
	public Room getRoom() {
		return _room;
	}
	
	/**
	 * Returns whether or not the list labels for this item
	 * have been updated.
	 */
	public boolean hasValidatedLabels() {
		return _labelsValidated;
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
	
	/** 
	 * ResultsListItems cannot be open, so instead this
	 * sets the selection state of this item. It bolds
	 * the item's label if it is selected.
	 */
	@Override
	public void setOpen(boolean open) {
		if (open) {
			_label.setFont(_selectedFont);
			_probLabel.setFont(_selectedFont);
			this.setBackground(_selectedBackgroundColor);
		}
		else {
			_label.setFont(_unselectedFont);
			_probLabel.setFont(_unselectedFont);
			this.setBackground(_unselectedBackgroundColor);
		}
	}
	
	@Override
	public void addItem(AccordionItem item) { }
	
	@Override
	public void removeItem(AccordionItem item) { }
	
	@Override
	public int compareTo(AccordionItem o) {
		return _index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0);
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
						ListsTab.getInstance().updateLists();
						exists = true;
						break;
					}
				}
				if (!exists) {
					RoomList list = new RoomList(selected);
					State.getInstance().addRoomList(list);
					list.add(_room);
					++_numLists;
					ListsTab.getInstance().updateLists();
				}
			}
			_labelsValidated = false;
			validateListLabels();
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}
	
	/** Bolds the item when clicked */
	private class SelectedListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!isOpen()) {
				setOpen(true);
				_parentList.setSelectedItem(ResultsListItem.this);
			}
		}
	}
	
} // end of ResultsListItem
