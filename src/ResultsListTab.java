import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * An expandable tab in a results list for a specific subgroup.
 * 
 * @author jswarren
 *
 */
public class ResultsListTab extends JPanel implements AccordionItem {
	
	private JPanel _tab;
	private JLabel _label;
	private JLabel _addButton;
	private JPanel _itemsPanel;
	private JScrollPane _itemsScroller;
	private Dorm _dorm;
	private SubGroup _subGroup;
	private int _index;
	private boolean _isOpen;
	private static ImageIcon _openIcon = ImageIconLoader.getInstance().createImageIcon("images/open_results_tab_new.png", "open results list");
	private static ImageIcon _closedIcon = ImageIconLoader.getInstance().createImageIcon("images/closed_results_tab_new.png", "closed results list");
	private static ImageIcon _addToListIcon = ImageIconLoader.getInstance().createImageIcon("images/add_to_list_new.png", "add to list");
	
	private final int _tabHeight = 25;
	private final int _listWidth = 200;
	private final int _scrollPaneHeight = 100;
	private final int _scrollBarWidth = 20;
	
	ResultsListTab(Dorm dorm, SubGroup sg) {
		super();
		_dorm = dorm;
		_subGroup = sg;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _tabHeight));
		this.setSize(new Dimension(_listWidth, _tabHeight));
		_isOpen = false;
		
		//Set up expandable tab
		_tab = new JPanel();
		_tab.setLayout(new BoxLayout(_tab, BoxLayout.LINE_AXIS));
		_tab.setBounds(0, 0, _listWidth, _tabHeight);
		
		//Set up label on tag (with expansion icon)
		_label = new JLabel();
		_label.setText(_dorm.getName());
		_label.setIcon(_closedIcon);
		_tab.addMouseListener(new ExpandListener());
		_tab.add(_label);
		
		//Set up addToListButton (actually a blank label with an icon)
		_tab.add(Box.createHorizontalGlue());
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener());
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
		_itemsScroller = new JScrollPane(_itemsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_itemsScroller.setBounds(0, _tabHeight, _listWidth, _scrollPaneHeight);
		_itemsScroller.setVisible(false);
		this.add(_itemsScroller);
	}
	
	public Insets getBorderInsets() {
		Insets i = _tab.getBorder().getBorderInsets(_tab);
		i.left += _openIcon.getIconWidth() + _label.getIconTextGap() - 1;
		i.right += _scrollBarWidth + 1;
		i.top = 0;
		i.bottom = 0;
		return i;
	}

	@Override
	public int getIndex() {
		return _index;
	}
	
	@Override
	public void setIndex(int index) {
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
	public int compareTo(AccordionItem o) {
		return (_index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0));
	}
	
	private class ExpandListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			_isOpen = !_isOpen;
			if (_isOpen) {
				_label.setIcon(_openIcon);
				_itemsScroller.setVisible(true);
				setPreferredSize(new Dimension(getWidth(), _tabHeight + _scrollPaneHeight));
				revalidate();
			}
			else {
				_label.setIcon(_closedIcon);
				_itemsScroller.setVisible(false);
				setPreferredSize(new Dimension(getWidth(), _tabHeight));
				revalidate();
			}
		}
		
	}
	
	private class AddListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO
			JOptionPane.showMessageDialog(ResultsListTab.this, "add to list (TODO)", "Domus", JOptionPane.PLAIN_MESSAGE);
		}
		
	}
	
}
