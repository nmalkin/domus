import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
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
	private JLabel _button;
	private JPanel _labelsPanel;
	private ProbabilityDisplay _probabilityDisplay;
	private JPanel _infoPanel;
	private boolean _isOpen;
	private boolean _fullWidth;
	private static ImageIcon _buttonIcon = new ImageIcon(Constants.ADD_FILE, "add to list");
	private double _comparisonValue;
	
	private int _listWidth = Constants.RESULTS_LIST_ITEM_WIDTH;
	
	public ResultsListItem(Room room, AccordionList<ResultsListTab, ResultsListItem> list) {
		super();
		
		// set the layout and size of this panel
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		Dimension size = new Dimension(_listWidth, Constants.RESULTS_LIST_ITEM_HEIGHT);
		this.setPreferredSize(size);
		this.setSize(size);
		
		// create a panel for the room name and probability
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setPreferredSize(size);
		panel.setSize(size);
		
		// set initial values
		_room = room;
		_room.addToListItem(this);
		_parentList = list;
		_comparisonValue = _room.getProbability();
		_isOpen = false;
		_fullWidth = true;
		
		// create label for Room name/number
		_label = new JLabel(_room.getDorm().getName() + " " + _room.getNumber());
//		_label.setFont();
		panel.add(Box.createRigidArea(new Dimension(Constants.OPEN_ICON_WIDTH, 0)));
		panel.add(_label);
		
		// create panel for list labels
		_labelsPanel = new JPanel();
		_labelsPanel.setLayout(new BoxLayout(_labelsPanel, BoxLayout.LINE_AXIS));
		_labelsPanel.setBackground(new Color(255, 255, 255, 0));
		panel.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		panel.add(_labelsPanel);
		panel.add(Box.createHorizontalGlue());
		
		// create probability display
		_probabilityDisplay = new ProbabilityDisplay(_room.getProbability());
		panel.add(_probabilityDisplay);
		panel.add(Box.createRigidArea(new Dimension(Constants.PROBABILITY_DISPLAY_RIGHT_SPACING, 0)));
		
		// create button (by default, action and icon are for adding)
		// this can be changed later
		_button = new JLabel(_buttonIcon);
		_button.setPreferredSize(new Dimension(_buttonIcon.getIconWidth(), _buttonIcon.getIconHeight()));
		_button.addMouseListener(new AddListener(this));
		panel.add(_button);
		panel.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		panel.addMouseListener(new SelectedListener());
		this.add(panel);
		
		// make sure the room has the proper list labels
		validateListLabels();
		
		// create a panel to hold the other information (LotteryResults, GAS, possible sq feet?)
		_infoPanel = new JPanel();
		size = new Dimension(_listWidth, 2 * Constants.RESULTS_LIST_ITEM_HEIGHT + 1);
		_infoPanel.setPreferredSize(size);
		_infoPanel.setSize(size);
		_infoPanel.setLayout(new BoxLayout(_infoPanel, BoxLayout.LINE_AXIS));
		JPanel fillerPanel = new JPanel();
		fillerPanel.setLayout(new BoxLayout(fillerPanel, BoxLayout.LINE_AXIS));
		fillerPanel.add(Box.createRigidArea(new Dimension(Constants.OPEN_ICON_WIDTH, 0)));
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
		
		// create a label for each property of this ResultListItem's room
		// create a panel for the ResultsListItem's room's properties
		JPanel propertiesPanel = new JPanel();
		size = new Dimension(0, 2 * Constants.RESULTS_LIST_ITEM_HEIGHT);
		propertiesPanel.setPreferredSize(size);
		propertiesPanel.setSize(size);
		propertiesPanel.setLayout(new BoxLayout(propertiesPanel, BoxLayout.LINE_AXIS));
		if (_room.isGenderNeutral()) {
			JLabel genLabel = new JLabel("G ");
			propertiesPanel.add(genLabel);
			genLabel.setPreferredSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			genLabel.setSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			size = propertiesPanel.getPreferredSize();
			propertiesPanel.setPreferredSize(new Dimension(size.width + genLabel.getPreferredSize().width, size.height));
			propertiesPanel.setSize(new Dimension(size.width + genLabel.getPreferredSize().width, size.height));
		}
		if (_room.hasApartmentRate()) {
			JLabel rateLabel = new JLabel("A ");
			propertiesPanel.add(rateLabel);
			rateLabel.setPreferredSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			rateLabel.setSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			size = propertiesPanel.getPreferredSize();
			propertiesPanel.setPreferredSize(new Dimension(size.width + rateLabel.getPreferredSize().width, size.height));
			propertiesPanel.setSize(new Dimension(size.width + rateLabel.getPreferredSize().width, size.height));
		}
		if (_room.getDorm().isSophomoreOnly()) {
			JLabel sophLabel = new JLabel("S "); 
			propertiesPanel.add(sophLabel);
			sophLabel.setPreferredSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			sophLabel.setSize(new Dimension(5, Constants.RESULTS_LIST_ITEM_HEIGHT));
			size = propertiesPanel.getPreferredSize();
			propertiesPanel.setPreferredSize(new Dimension(size.width + sophLabel.getPreferredSize().width, size.height));
			propertiesPanel.setSize(new Dimension(size.width + sophLabel.getPreferredSize().width, size.height));
		}
		JLabel results = new JLabel("Past Results:"); 
		propertiesPanel.add(results);
		propertiesPanel.setPreferredSize(new Dimension(30, Constants.RESULTS_LIST_ITEM_HEIGHT));
		propertiesPanel.setSize(new Dimension(30, Constants.RESULTS_LIST_ITEM_HEIGHT));
		size = propertiesPanel.getPreferredSize();
		propertiesPanel.setPreferredSize(new Dimension(size.width + results.getPreferredSize().width, size.height));
		propertiesPanel.setSize(new Dimension(size.width + results.getPreferredSize().width, size.height));
		holderPanel.add(propertiesPanel);
		holderPanel.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		
		// create a panel for the past lottery results
		JPanel pastResultsPanel = new JPanel();
		pastResultsPanel.setLayout(new BoxLayout(pastResultsPanel, BoxLayout.LINE_AXIS));
		size = new Dimension(0, 2 * Constants.RESULTS_LIST_ITEM_HEIGHT);
		pastResultsPanel.setPreferredSize(size);
		pastResultsPanel.setSize(size);
		
		// create a panel for each past result
		int count = 0;
		for (LotteryResult lotteryResult : _room.getResults()) {
			JPanel pastResult = new JPanel();
			pastResult.setLayout(new BoxLayout(pastResult, BoxLayout.PAGE_AXIS));
			int left = 1;
			if (count == 0)
				left = 0;
			pastResult.setBorder(BorderFactory.createMatteBorder(0, left, 0, 0, Color.BLACK));
			
			// create a label for the year and for the result
			JLabel year = new JLabel(lotteryResult.getYear() + " ");
			year.setFont(year.getFont().deriveFont(10f));
			year.setPreferredSize(new Dimension(28, Constants.RESULTS_LIST_ITEM_HEIGHT));
			year.setSize(new Dimension(28, Constants.RESULTS_LIST_ITEM_HEIGHT));
			year.setHorizontalAlignment(JLabel.CENTER);
			year.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
			JLabel result = new JLabel(lotteryResult.getLotteryNumber() + " ");
			result.setFont(result.getFont().deriveFont(10f));
			result.setPreferredSize(new Dimension(28, Constants.RESULTS_LIST_ITEM_HEIGHT));
			result.setSize(new Dimension(28, Constants.RESULTS_LIST_ITEM_HEIGHT));
			result.setHorizontalAlignment(JLabel.CENTER);
			
			// add the labels and reset the size
			pastResult.add(year);
			pastResult.add(result);
			int width = Math.max(year.getPreferredSize().width, result.getSize().width);
			size = pastResult.getSize();
			size = new Dimension(width, 2 * Constants.RESULTS_LIST_ITEM_HEIGHT);
			pastResult.setPreferredSize(size);
			pastResult.setSize(size);
//			pastResult.setMinimumSize(size);
			
			// add the panel to the past lottery results panel
			// and resize it
			pastResultsPanel.add(pastResult);
			size = pastResultsPanel.getSize();
			size = new Dimension(size.width + pastResult.getSize().width, 2 * Constants.RESULTS_LIST_ITEM_HEIGHT);
			pastResultsPanel.setPreferredSize(size);
			pastResultsPanel.setSize(size);
			count++;
		}
		pastResultsPanel.add(Box.createHorizontalGlue());
		holderPanel.add(pastResultsPanel);
		holderPanel.add(Box.createHorizontalGlue());
//		holderPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
		
		_infoPanel.add(fillerPanel);
		_infoPanel.add(holderPanel);
		_infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		
		
		// add info panel to ResultsListItem (initally not visible)
		_infoPanel.setVisible(false);
		this.add(_infoPanel);
		
	}
	
	/** Makes sure the list labels for this item are updated */
	public void validateListLabels() {
		_labelsPanel.removeAll();
		for (RoomList rl : _room.getRoomLists()) {
			addListLabel(rl);
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
	
	/** 
	 * Returns the parent list associated with this item.
	 * May be null.
	 */
	public AccordionList<ResultsListTab, ResultsListItem> getParentList() {
		return _parentList;
	}
	
	/** Returns the room associated with this item */
	public Room getRoom() {
		return _room;
	}
	
	public void updateProbability() {
		_comparisonValue = getRoom().getProbability();
		_probabilityDisplay.setProbability(_comparisonValue);
	}

	public void setButtonIcon(ImageIcon icon) {
		_buttonIcon = icon;
		_button.setIcon(_buttonIcon);
	}
	
	public void setButtonMouseListener(MouseListener l) {
		MouseListener[] listeners = _button.getMouseListeners();
		for (int i = 0; i < listeners.length; ++i) {
			MouseListener ml = listeners[i];
			_button.removeMouseListener(ml);
		}
		if (l != null)
			_button.addMouseListener(l);
	}
	
	@Override
	public double getComparisonValue() {
		return _comparisonValue;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void setComparisonValue(double d) { }
	
	/** 
	 * ResultsListItems cannot be open, so instead this
	 * sets the selection state of this item. It bolds
	 * the item's label if it is selected.
	 */
	@Override
	public void setOpen(boolean open) {
		_isOpen = open;
		int height = _infoPanel.getPreferredSize().height;
		if (open) {
			_infoPanel.setVisible(true);
//			_label.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
			this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
//			_infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
		}
		else {
			_infoPanel.setVisible(false);
			_label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));
			this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK));
			height = -height;
		}
		Dimension size = getSize();
		size = new Dimension(size.width, size.height + height);
		this.setPreferredSize(size);
		this.setSize(size);
		if (_parentList != null) {
			ResultsListTab tab = (ResultsListTab) getParent().getParent();
			tab.resizeItem(new Dimension(0, height));
		}
	}
	
	@Override
	public void addItem(AccordionItem item) { }
	
	@Override
	public void removeItem(AccordionItem item) { }
	
	@Override
	public void resizeItem(Dimension d) {
		// determine if component needs to be resized
		boolean resize = (d.width < 0 && _fullWidth) || (d.width > 0 && !_fullWidth);
		
		// resize if necessary
		if (resize) {
			Dimension size = getPreferredSize();
			setPreferredSize(new Dimension(size.width + d.width, size.height));
			setSize(new Dimension(size.width + d.width, size.height));
			_fullWidth = !_fullWidth;
		}
	}
	
	@Override
	@Deprecated
	public int compareTo(AccordionItem o) {
		return _room.compareTo(((ResultsListItem) o).getRoom());
	}
	
	@Override
	public int hashCode() {
		return _room.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ResultsListItem))
			return false;
		ResultsListItem other = (ResultsListItem) o;
		return _room.equals(other.getRoom());
	}
	
	@Override
	public String toString() {
		return _room.getDorm().getName() + " " + _room.getNumber();
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
						_room.addToRoomList(rl);
						rl.add(new ResultsListItem(_room, null));
						ListsTab.getInstance().updateLists();
						exists = true;
						break;
					}
				}
				if (!exists) {
					RoomList list = new RoomList(selected);
					State.getInstance().addRoomList(list);
					_room.addToRoomList(list);
					list.add(new ResultsListItem(_room, null));
					ListsTab.getInstance().updateLists();
				}
			}
			for (ResultsListItem rli : getRoom().getListItems()) {
				rli.validateListLabels();
				if (rli.getParentList() != null) {
					ResultsListTab rlt = (ResultsListTab) rli.getParent().getParent();
					rlt.validateListLabels();
				}
			}
			_prompt.setVisible(false);
			_prompt.dispose();
		}
		
	}
	
	/** Bolds the item when clicked */
	private class SelectedListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			setOpen(!isOpen());
		}
	}

	public void updateWidth(int width) {
		// TODO Auto-generated method stub
		_listWidth = width;
		Dimension size = new Dimension(_listWidth, Constants.RESULTS_LIST_ITEM_HEIGHT);
		this.setPreferredSize(size);
		this.setSize(size);
	}
	
}
