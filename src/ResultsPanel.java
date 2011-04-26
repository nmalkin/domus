import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class ResultsPanel extends JPanel {
	
	private List<AccordionList<ResultsListTab, ResultsListItem>> _lists;
	private Multimap<SubGroup, Room> _results;
	
	public ResultsPanel() {
		super();
		Dimension size = new Dimension(500, 550);
		this.setPreferredSize(size);
		_lists = new LinkedList<AccordionList<ResultsListTab, ResultsListItem>>();
		_results = State.getInstance().getResults();
		SetMultimap<SubGroup, Dorm> _dormMap = HashMultimap.create();
		for (SubGroup sg : _results.keySet()) {
			for (Room r : _results.get(sg)) {
				_dormMap.put(sg, r.getDorm());
			}
		}
		for (SubGroup sg : _dormMap.keySet()) {
			AccordionList<ResultsListTab, ResultsListItem> list = AccordionList.create();
			list.setPreferredSize(new Dimension(200, 500));
			list.setVisible(true);
			for (Dorm d : _dormMap.get(sg)) {
				ResultsListTab tab = new ResultsListTab(d, sg);
				list.addTab(tab);
				for (Room r : _results.get(sg)) {
					if (r.getDorm() == d) {
						ResultsListItem item = new ResultsListItem(r);
						item.setInsets(tab.getBorderInsets());
						list.addListItem(tab, item);
					}
				}
			}
			_lists.add(list);
			this.add(list);
		}
	}
	
}
