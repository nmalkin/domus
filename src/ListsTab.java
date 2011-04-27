import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;


public class ListsTab extends JPanel {

	private static ListsTab _INSTANCE = new ListsTab();
	
	private List<ListPanel> _lists;
	
	public static ListsTab getInstance() {
		return _INSTANCE;
	}
	
	private ListsTab() {
		super();
		this.setPreferredSize(new Dimension(1000, 550));
		_lists = new LinkedList<ListPanel>();
		updateLists();
		_INSTANCE = this;
	}
	
	public void updateLists() {
		int i = 0;
		for (RoomList rl : State.getInstance().getRoomLists()) {
			if (i >= _lists.size()) {
				ListPanel lp = new ListPanel(rl);
				_lists.add(lp);
				this.add(lp);
			}
			i++;
		}
		for (ListPanel lp : _lists) {
			lp.updateList();
		}
	}
}
