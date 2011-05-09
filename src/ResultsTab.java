import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ResultsTab extends JPanel {
	
	private ResultsPanel _resultsPanel;
	private PreferencePanel _preferencePanel;
	
	public ResultsTab() {
		this.setLayout(new BorderLayout());
		
		_preferencePanel = new PreferencePanel();
		this.add(_preferencePanel, BorderLayout.LINE_START);
		
		_resultsPanel = new ResultsPanel();
		this.add(_resultsPanel);
		
		this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
		State.getInstance().getGroup().addGroupStateChangeListener(new GroupStateChangeListener());
		State.getInstance().addSelectedHouseChangeListener(new SelectedHouseChangeListener());
		State.getInstance().setSelectedProbabilityModelChangeListener(new SelectedProbabilityModelChangeListener());
	}
	
	private void updateResults(boolean updateEverything) {
		if (updateEverything) {
			State.getInstance().updateResults();
		}
		_resultsPanel.updateResultsLists(updateEverything);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			// update results, not just probabilities
			updateResults(true);
			
			this.remove(_preferencePanel);
			_preferencePanel = new PreferencePanel();
			this.add(_preferencePanel, BorderLayout.LINE_START);
		}
	}
	
	/**
	 * Listens for changes in the sophomoreOnly status. If the results tab is
	 * visible and this happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class GroupStateChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (!((Group.GroupChangeEvent) e).getUpdateType())
				updateResults(((Group.GroupChangeEvent) e).getUpdateType());
			
			else if (isVisible())
				// update what should be updated based on the GroupChangeEvent updateType
				updateResults(((Group.GroupChangeEvent) e).getUpdateType());
		}
		
	}
	
	/** 
	 * Listens for the selected house to change. If the results tab is
	 * visible and this happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class SelectedHouseChangeListener implements ChangeListener {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (isVisible())
				// update results, not just probabilities
				updateResults(true);
		}
		
	}
	
	/** 
	 * Listens for the selected probability model to change. If the results tab is
	 * visible and this happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class SelectedProbabilityModelChangeListener implements ChangeListener {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (isVisible())
				// update results, not just probabilities
				updateResults(true);
		}
		
	}
}
