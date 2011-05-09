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
		State.getInstance().getGroup().setSophomoreStatusChangeListener(new SophomoreStatusChangeListener());
		State.getInstance().getGroup().setLotteryNumberChangeListener(new LotteryNumberChangeListener());
		State.getInstance().addSelectedHouseChangeListener(new SelectedHouseChangeListener());
	}
	
	private void updateResults(boolean probabilitiesOnly) {
		if (!probabilitiesOnly) {
			State.getInstance().updateResults();
		}
		_resultsPanel.updateResultsLists(probabilitiesOnly);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			updateResults(false);
			
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
	private class SophomoreStatusChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (isVisible())
				updateResults(false);
		}
		
	}
	
	/**
	 * Listens for changes in the lottery number. If the results tab is
	 * visible and this happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class LotteryNumberChangeListener implements ChangeListener {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (isVisible())
				updateResults(true);
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
				updateResults(false);
		}
		
	}
}
