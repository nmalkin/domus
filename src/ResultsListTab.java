import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An expandable tab in a results list for a specific subgroup.
 * 
 * @author jswarren
 *
 */
public class ResultsListTab extends JPanel implements AccordionItem {
	
	private AccordionList<ResultsListTab, ResultsListItem> _parentList;
	private JPanel _tab;
	private JLabel _label;
	private JLabel _addButton;
	private JPanel _itemsPanel;
	private JScrollPane _scroller;
	private Dorm _dorm;
	private SubGroup _subGroup;
	private int _index;
	private boolean _isOpen;
	private static ImageIcon _openIcon = new ImageIcon(Constants.OPEN_FILE, "open results list");
	private static ImageIcon _closedIcon = new ImageIcon(Constants.CLOSED_FILE, "closed results list");
	private static ImageIcon _addToListIcon = new ImageIcon(Constants.ADD_FILE, "add to list");
	private static Font _font = new Font("Verdana", Font.PLAIN, 12);
	
	//constants
	private final int _tabHeight = 25;
	private final int _listWidth = 350;
	private final int _scrollPaneHeight = 100;
	private final int _scrollBarWidth = 20;
	
	ResultsListTab(Dorm dorm, SubGroup sg, AccordionList<ResultsListTab, ResultsListItem> list) {
		super();
		_dorm = dorm;
		_subGroup = sg;
		_parentList = list;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _tabHeight));
		this.setSize(new Dimension(_listWidth, _tabHeight));
		_isOpen = false;
		
		//Set up expandable tab
		_tab = new JPanel();
		_tab.setLayout(new BoxLayout(_tab, BoxLayout.LINE_AXIS));
		_tab.setBounds(0, 0, _listWidth, _tabHeight);
		
		//Set up label on tag (with expansion icon)
		_label = new JLabel(_dorm.getName());
		_label.setFont(_font);
		_label.setIcon(_closedIcon);
		_tab.addMouseListener(new ExpandListener());
		_tab.add(_label);
		
		//Set up addToListButton (actually a blank label with an icon)
		_tab.add(Box.createHorizontalGlue());
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener(this));
		_tab.add(_addButton);
		_tab.add(Box.createRigidArea(new Dimension(_scrollBarWidth, 0)));
		_tab.setBorder(LineBorder.createBlackLineBorder());
		this.add(_tab);
		
		//Set up scroll pane which will contain list of individual results
		_itemsPanel = new JPanel();
		_itemsPanel.setPreferredSize(new Dimension(_listWidth, 0));
		_itemsPanel.setSize(new Dimension(_listWidth, 0));
		_itemsPanel.setVisible(true);
		_itemsPanel.setLayout(new BoxLayout(_itemsPanel, BoxLayout.PAGE_AXIS));
		_scroller = new JScrollPane(_itemsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setBounds(0, _tabHeight, _listWidth, _scrollPaneHeight);
		_scroller.setVisible(false);
		this.add(_scroller);
	}
	
	public Insets getBorderInsets() {
		Insets i = _tab.getBorder().getBorderInsets(_tab);
		i.left += _openIcon.getIconWidth() + _label.getIconTextGap() - 1;
		i.right += _scrollBarWidth + 1;
		i.top = 0;
		i.bottom = 0;
		return i;
	}
	
	public Dorm getDorm() {
		return _dorm;
	}

	@Override
	public int getComparisonValue() {
		return _index;
	}
	
	@Override
	public void setComparisonValue(int index) {
		_index = index;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void setOpen(boolean open) {
		_isOpen = open;
	}

	@Override
	public void addItem(AccordionItem item) {
		_itemsPanel.add((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height + ((JComponent) item).getHeight());
		_itemsPanel.setPreferredSize(size);
		_itemsPanel.setSize(size);
	}
	
	@Override
	public void removeItem(AccordionItem item) {
		_itemsPanel.remove((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height - ((JComponent) item).getHeight());
		_itemsPanel.setPreferredSize(size);
		_itemsPanel.setSize(size);		
	}

	@Override
	public int compareTo(AccordionItem o) {
		return _index < o.getComparisonValue() ? -1 : (_index > o.getComparisonValue() ? 1 : 0);
	}
	
	@Override
	public String toString() {
		return _dorm.getName();
	}
	
	private class ExpandListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			_isOpen = !_isOpen;
			if (_isOpen) {
				_label.setIcon(_openIcon);
				_scroller.setVisible(true);
				setPreferredSize(new Dimension(getWidth(), _tabHeight + _scrollPaneHeight));
				revalidate();
			}
			else {
				_label.setIcon(_closedIcon);
				_scroller.setVisible(false);
				setPreferredSize(new Dimension(getWidth(), _tabHeight));
				revalidate();
			}
		}
		
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
						for (Component c : _itemsPanel.getComponents()) {
							ResultsListItem rli = (ResultsListItem) c;
							rl.add(rli.getRoom());
							rli.getRoom().addToRoomList(rl);
						}
						ListsTab.getInstance().updateLists();
						exists = true;
						break;
					}
				}
				if (!exists) {
					RoomList list = new RoomList(selected);
					State.getInstance().addRoomList(list);
					for (Component c : _itemsPanel.getComponents()) {
						ResultsListItem rli = (ResultsListItem) c;
						list.add(rli.getRoom());
						rli.getRoom().addToRoomList(list);
					}
					ListsTab.getInstance().updateLists();
				}
			}
			for (Component c : _itemsPanel.getComponents()) {
				ResultsListItem listItem = (ResultsListItem) c;
				for (ResultsListItem rli : listItem.getRoom().getListItems())
					rli.validateListLabels();
			}
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}
	
}
