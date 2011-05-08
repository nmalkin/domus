import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PreferencePanelItem extends JPanel implements AccordionItem{

	private JPanel _labelsPanel;
	private ImageIcon _removeIcon = new ImageIcon(Constants.REMOVE_FILE, "remove from list");
	
	private final int _itemHeight = 30;
	private final int _itemWidth = Constants.PREFERENCE_PANEL_WIDTH;
	
	protected PreferencePanelItem(Dorm dorm) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		JLabel dormName = new JLabel(dorm.getName());
		JLabel removeLabel = new JLabel(_removeIcon);
		
		_labelsPanel = new JPanel();
		_labelsPanel.setPreferredSize(new Dimension(_itemWidth, _itemHeight));
		_labelsPanel.add(dormName);
		_labelsPanel.add(removeLabel);
		
		
		this.add(_labelsPanel);
		this.add(Box.createHorizontalGlue());
	}
	
	@Override
	public void addItem(AccordionItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getComparisonValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeItem(AccordionItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComparisonValue(int index) {
		// TODO Auto-generated method stub
		
	}
	
	/** Listener for room removal */
	/*private class RemoveListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			removeRoom(_map.get(label));
		}
		
	}*/

	@Override
	public void setOpen(boolean open) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(AccordionItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
