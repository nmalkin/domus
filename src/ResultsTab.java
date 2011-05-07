import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;


public class ResultsTab extends JPanel {
	
	private ResultsPanel _resultsPanel;
	
	public ResultsTab() {
		this.setLayout(new BorderLayout());
		
		this.add(new ResultsPreferencePanel(), BorderLayout.LINE_START);
		this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
		_resultsPanel = new ResultsPanel();
		this.add(_resultsPanel);
	}
	
	public void updateResults() {
		State.getInstance().updateResults();
		_resultsPanel.updateResultsLists();
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			updateResults();
	}
}
