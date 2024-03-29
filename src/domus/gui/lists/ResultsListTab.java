package domus.gui.lists;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import domus.Constants;
import domus.State;
import domus.data.Dorm;
import domus.data.Room;
import domus.data.RoomList;
import domus.gui.accordionlist.AccordionItem;
import domus.gui.accordionlist.AccordionList;
import domus.gui.canvas.SubGroup;
import domus.gui.tabs.ListsTab;

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
	private JPanel _itemsPanel;
	private JPanel _labelsPanel;
	private ProbabilityDisplay _probabilityDisplay;
	private Dorm _dorm;
	private double _comparisonValue;
	private boolean _isOpen;
	private boolean _fullWidth;
	private static ImageIcon _openIcon = new ImageIcon(Constants.OPEN_FILE, "open results list");
	private static ImageIcon _closedIcon = new ImageIcon(Constants.CLOSED_FILE, "closed results list");
	private static ImageIcon _addToListIcon = new ImageIcon(Constants.ADD_FILE, "add to list");
	
	ResultsListTab(Dorm dorm, SubGroup sg, AccordionList<ResultsListTab, ResultsListItem> list) {
		super();
		_dorm = dorm;
		_parentList = list;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_LIST_TAB_HEIGHT));
		this.setSize(new Dimension(Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_LIST_TAB_HEIGHT));
		_isOpen = false;
		_fullWidth = true;
		
		//Set up expandable tab
		_tab = new JPanel();
		_tab.setLayout(new BoxLayout(_tab, BoxLayout.LINE_AXIS));
		_tab.setBounds(0, 0, Constants.RESULTS_LIST_WIDTH, Constants.RESULTS_LIST_TAB_HEIGHT);
		_tab.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		
		//Set up label on tab (with expansion icon)
		_label = new JLabel(_dorm.getName());
		_label.setFont(Constants.DOMUS_FONT.deriveFont(12f));
		_label.setIcon(_closedIcon);
		_tab.addMouseListener(new ExpandListener());
		_tab.add(_label);
		
		//Set up panel for list labels on tab
		_labelsPanel = new JPanel();
		_labelsPanel.setLayout(new BoxLayout(_labelsPanel, BoxLayout.LINE_AXIS));
		_labelsPanel.setBackground(new Color(255, 255, 255, 0));
		_tab.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		_tab.add(_labelsPanel);
		
		_tab.add(Box.createHorizontalGlue());
		
		// add probability display (average probability for this dorm)
		_probabilityDisplay = new ProbabilityDisplay(0);
		_tab.add(_probabilityDisplay);
		_tab.add(Box.createRigidArea(new Dimension(Constants.PROBABILITY_DISPLAY_RIGHT_SPACING, 0)));
		
		//Set up addToListButton (actually a blank label with an icon)
		JLabel addButton = new JLabel(_addToListIcon);
		addButton.addMouseListener(new AddListener(this));
		_tab.add(addButton);
		_tab.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		this.add(_tab);
		
		//Set up scroll pane which will contain list of individual results
		_itemsPanel = new JPanel();
		_itemsPanel.setPreferredSize(new Dimension(Constants.RESULTS_LIST_WIDTH, 1));
		_itemsPanel.setSize(new Dimension(Constants.RESULTS_LIST_WIDTH, 1));
		_itemsPanel.setLocation(0, Constants.RESULTS_LIST_TAB_HEIGHT);
		_itemsPanel.setLayout(new BoxLayout(_itemsPanel, BoxLayout.PAGE_AXIS));
		_itemsPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		this.add(_itemsPanel);
	}
	
	/** Returns the dorm this tab represents */
	public Dorm getDorm() {
		return _dorm;
	}
	
	/** Returns the items contained in this tab */
	public Collection<ResultsListItem> getItems() {
		Collection<ResultsListItem> items = new LinkedList<ResultsListItem>();
		for (Component c : _itemsPanel.getComponents()) {
			items.add((ResultsListItem) c);
		}
		return items;
	}
	
	/** Makes sure the list labels for this item are updated */
	public void validateListLabels() {
		_labelsPanel.removeAll();
		for (RoomList rl : State.getInstance().getRoomLists()) {
			boolean addLabel = true;
			for (Component c : _itemsPanel.getComponents()) {
				ResultsListItem rli = (ResultsListItem) c;
				rli.validateListLabels();
				Room r = rli.getRoom();
				if (!r.getRoomLists().contains(rl))
					addLabel = false;
			}
			if (addLabel) {
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

	public void updateProbabilities() {
		double probabilitySum = 0, itemCount = 0, probabilityValue;
		
		for (Component c : _itemsPanel.getComponents()) {
			ResultsListItem listItem = (ResultsListItem) c;
			listItem.updateProbability();
			
			probabilityValue = listItem.getComparisonValue(); // getComparisonValue returns the probability for this room
			if(probabilityValue != Constants.PROBABILITY_NO_DATA) {
				probabilitySum += probabilityValue;
				itemCount++;
			}
		}
		
		// update this tab's probability display
		if(itemCount == 0) itemCount = 1; // avoid divide-by-zero error
		_probabilityDisplay.setProbability(probabilitySum / itemCount);
	}
	
	public boolean isFullWidth() {
		return _fullWidth;
	}
	
	/** 
	 * Resizes the list. Called when the visibility
	 * of the vertical scrollbar on the parent components
	 * changes.
	 */
	@Override
	public void resizeItem(Dimension d) {
		//determine if components need to be resized
		boolean resize = (d.width < 0 && _fullWidth) || (d.width > 0 && !_fullWidth) || getHeight() != d.height;
		
		if (resize) {
		    //resize this component
		    Dimension size = this.getSize();
			this.setPreferredSize(new Dimension(size.width + d.width, size.height + d.height));
			this.setSize(new Dimension(size.width + d.width, size.height + d.height));
		
		//resize tab
			size = _tab.getSize();
			_tab.setPreferredSize(new Dimension(size.width + d.width, size.height));
			_tab.setSize(new Dimension(size.width + d.width, size.height));

		//resize itemsPanel
			size = _itemsPanel.getSize();
			_itemsPanel.setPreferredSize(new Dimension(size.width + d.width, size.height + d.height));
			_itemsPanel.setSize(new Dimension(size.width + d.width, size.height + d.height));
			if (d.width != 0)
			    _fullWidth = !_fullWidth;
//			int bottom = 1;
//			if (!_fullWidth && this == _parentList.getLastTab())
//				bottom = 0;
//			System.out.println(bottom);
//			_itemsPanel.setBorder(BorderFactory.createMatteBorder(0, 1, bottom, 1, Color.BLACK));
		}
		
		//resize items
		for (Component c : _itemsPanel.getComponents()) {
			AccordionItem item = (AccordionItem) c;
			item.resizeItem(d);
		}
	}

	@Override
	public double getComparisonValue() {
		return _comparisonValue;
	}
	
	@Override
	public void setComparisonValue(double d) {
		_comparisonValue = d;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void setOpen(boolean open) {
		_isOpen = open;
		int height = _itemsPanel.getPreferredSize().height;
		if (!_isOpen)
			height = -height;
		_parentList.tabDisplayChanged(height);
		int bottom = 1;
		if (_isOpen)
			bottom = 0;
		_tab.setBorder(BorderFactory.createMatteBorder(0, 1, bottom, 1, Color.BLACK));
	}

	@Override
	public void addItem(AccordionItem item) {
		_itemsPanel.add((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height + ((JComponent) item).getSize().height);
		_itemsPanel.setPreferredSize(size);
		_itemsPanel.setSize(size);
	}
	
	@Override
	public void removeItem(AccordionItem item) {
		_itemsPanel.remove((JComponent) item);
		Dimension size = _itemsPanel.getPreferredSize();
		size = new Dimension(size.width, size.height - ((JComponent) item).getSize().height);
		_itemsPanel.setPreferredSize(size);
		_itemsPanel.setSize(size);
	}
	
	/**
	 * Compares two tabs based on the average probability of their results.
	 * If two tabs have the same average probability, their dorm names are compared lexicographically.
	 * 
	 * @return a number less than 0 if this tab has a lower average probability,
	 * or if the two probabilities are equal but this tab's dorm comes first lexicographically
	 */
	@Override
	@Deprecated
	public int compareTo(AccordionItem o) {	
		double ave1 = this.getComparisonValue();
		double ave2 = o.getComparisonValue();
		if (ave1 < ave2)
			return -1;
		if (ave1 > ave2)
			return 1;
		
		// if the averages are equal, compare dorms
		return getDorm().compareTo(((ResultsListTab) o).getDorm());
	}
	
	@Override
	public String toString() {
		return _dorm.getName();
	}
	
	private class ExpandListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			boolean open = !isOpen();
			if (open) {
				_label.setIcon(_openIcon);
				_itemsPanel.setVisible(true);
				setPreferredSize(new Dimension(getWidth(), Constants.RESULTS_LIST_TAB_HEIGHT + _itemsPanel.getPreferredSize().height));
				revalidate();
				setOpen(open);
			}
			else {
				setOpen(open);
				_label.setIcon(_closedIcon);
				_itemsPanel.setVisible(false);
				setPreferredSize(new Dimension(getWidth(), Constants.RESULTS_LIST_TAB_HEIGHT));
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
							rli.getRoom().addToRoomList(rl);
							rl.add(new ResultsListItem(rli.getRoom(), null));
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
						rli.getRoom().addToRoomList(list);
						list.add(new ResultsListItem(rli.getRoom(), null));
					}
					ListsTab.getInstance().updateLists();
				}
			}
			for (Component c : _itemsPanel.getComponents()) {
				ResultsListItem listItem = (ResultsListItem) c;
				for (ResultsListItem rli : listItem.getRoom().getListItems())
					rli.validateListLabels();
				validateListLabels();
			}
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}

}
