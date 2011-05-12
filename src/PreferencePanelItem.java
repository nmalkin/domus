

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PreferencePanelItem extends JPanel implements AccordionItem {

	private AccordionList<PreferencePanelTab, PreferencePanelItem> _parentList;
	private JPanel _labelsPanel;
	private boolean _isOpen;
	private JLabel _label;
	private boolean _fullWidth;
	private static Font _unselectedFont = new Font("Verdana", Font.PLAIN, 12);
	private ImageIcon _removeIcon = new ImageIcon(Constants.REMOVE_FILE, "remove from list");
	private House _house;
	private Dorm _dorm;
	
	private final int _itemHeight = 30;
	private final int _itemWidth = Constants.PREFERENCE_PANEL_WIDTH;
	
	protected PreferencePanelItem(House house, Dorm dorm, AccordionList<PreferencePanelTab, PreferencePanelItem> list) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		Dimension size = new Dimension(_itemWidth, _itemHeight);
		this.setPreferredSize(size);
		this.setSize(size);
		
		_parentList = list;
		_house = house;
		_dorm = dorm;
		
		_fullWidth = true;
		_isOpen = false;
		
		_label = new JLabel(dorm.getName());
		_label.setFont(_unselectedFont);
		
		this.add(Box.createRigidArea(new Dimension(Constants.OPEN_ICON_WIDTH, 0)));
		this.add(_label);
		this.setBorder(Constants.EMPTY_BORDER);
		
		_labelsPanel = new JPanel();
		_labelsPanel.setLayout(new BoxLayout(_labelsPanel, BoxLayout.LINE_AXIS));
		_labelsPanel.setBackground(new Color(255, 255, 255, 0));
		this.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		this.add(_labelsPanel);
		
		this.add(Box.createHorizontalGlue());
		
		JLabel removeButton = new JLabel(_removeIcon);
		removeButton.addMouseListener(new RemoveListener());
		removeButton.setVisible(false);
		this.add(removeButton);
		this.addMouseListener(new HoverListener());
		
		this.add(Box.createRigidArea(new Dimension(Constants.INSET, 0)));
		this.addMouseListener(new SelectedListener());
		
	}

	public Dorm getDorm () {
		return _dorm;
	}
	
	@Override
	public double getComparisonValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public void addItem(AccordionItem item) {	}
	
	@Override
	public void removeItem(AccordionItem item) {	}

	@Override
	public void setComparisonValue(double index) {
		// TODO Auto-generated method stub
		
	}
	
	/** Listener for removal button display */
	private class HoverListener extends MouseAdapter {
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//show the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 2);
			label.setVisible(true);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			//hide the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 2);
			if (panel.getComponentAt(e.getX(), e.getY()) != label)
				label.setVisible(false);
		}
		
	}
	
	/** Listener for room removal */
	private class RemoveListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JPanel panel = (JPanel) ((JComponent) e.getSource()).getParent();
			JPanel itemsPanel = (JPanel) ((JComponent) e.getSource()).getParent().getParent();
			itemsPanel.remove(panel);
			
			Dimension size = new Dimension(itemsPanel.getWidth(), itemsPanel.getHeight() - Constants.REMOVE_ICON_HEIGHT);
			itemsPanel.setPreferredSize(size);
			itemsPanel.setSize(size);
			
			JPanel tab = (JPanel) itemsPanel.getParent();
			Dimension tabSize = tab.getPreferredSize();
			tab.setPreferredSize(new Dimension(tabSize.width, tabSize.height - Constants.REMOVE_ICON_HEIGHT));
			((JPanel) itemsPanel.getParent()).setSize(size);
				
			itemsPanel.validate();
			_parentList.tabDisplayChanged(-Constants.REMOVE_ICON_HEIGHT);
			_parentList.validate();
			
			LocationPreference l = _house.getLocationPreference();
			l.remove(_dorm);
			_house.setLocationPreference(l);
			
			State.getInstance().setSelectedHouse(null);
			State.getInstance().setSelectedHouse(_house);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			label.setVisible(false);
		}
		
	}
	
	/** Bolds the item when clicked */
	private class SelectedListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			setOpen(!isOpen());
			if (isOpen())
				_parentList.setSelectedItem(PreferencePanelItem.this);
			else
				_parentList.setSelectedItem(null);
		}
	}

	@Override
	public void setOpen(boolean open) {
		if (open) {
			_isOpen = true;
		}
		else {
			_isOpen = false;
		}
	}

	@Override
	@Deprecated
	public int compareTo(AccordionItem o) {
		return _dorm.compareTo(((PreferencePanelItem) o).getDorm());
	}

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
	public boolean isFullWidth() {
		return _fullWidth;
	}

}
