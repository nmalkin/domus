

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ResultsTab extends JPanel implements ComponentListener {
	
	private ResultsPanel _resultsPanel;
	private PreferencePanel _preferencePanel;
	private JLabel _noPeopleLabel;
	
	public ResultsTab() {
		this.setLayout(new BorderLayout());
		
		_noPeopleLabel = new JLabel(Constants.NO_PEOPLE_MESSAGE);
		_noPeopleLabel.setPreferredSize(new Dimension(Constants.LISTS_INSTRUCTIONS_WIDTH, Constants.LISTS_INSTRUCTIONS_HEIGHT));
		_noPeopleLabel.setFont(Constants.DOMUS_FONT.deriveFont(12f));
		
		_preferencePanel = new PreferencePanel();
		this.add(_preferencePanel, BorderLayout.LINE_START);
		
		_resultsPanel = new ResultsPanel();
		this.add(_resultsPanel);
		
		this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
		
//		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
//		_resultsPanel.updateHeight(Math.min(size.height - 100, Constants.RESULTS_PANEL_HEIGHT));
//		_preferencePanel.updateHeight(Math.min(size.height - 100, Constants.RESULTS_PANEL_HEIGHT));
		
		// add listeners to Group and State for updating results based on certain user interactions
		State.getInstance().getGroup().addGroupStateChangeListener(new GroupStateChangeListener());
		State.getInstance().addSelectedHouseChangeListener(new SelectedHouseChangeListener());
		State.getInstance().setSelectedProbabilityModelChangeListener(new SelectedProbabilityModelChangeListener());
		State.getInstance().setSelectedYearsChangeListener(new SelectedYearsChangeListener());
	}
	
	/**
	 * Updates the results lists based on the boolean argument. If it's true,
	 * then fetch new results from the database. If it's false, then
	 * just update the probabilities for each result.
	 * 
	 * @param updateEverything, fetch new results if true, update probabilities if false
	 */
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
			if(State.getInstance().getGroup().numberOfPeople() == 0) {
				_resultsPanel.removeAll();
				_resultsPanel.add(_noPeopleLabel);
			}
			else {
				// update results, not just probabilities
				_resultsPanel.remove(_noPeopleLabel);
				updateResults(true);
			}
			
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
//			if (isVisible())
				// update results, not just probabilities
				updateResults(false);
		}
		
	}
	
	/**
	 * Listens for the selected years to change. If the results tab is visible and this
	 * happens, then the results should be updated.
	 * 
	 * @author jswarren
	 */
	private class SelectedYearsChangeListener implements ChangeListener {
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (isVisible())
				// update the results, not just probabilities
				updateResults(true);
		}
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
//		System.out.println(getSize().height);
//		System.out.println(getPreferredSize().height);
//		_resultsPanel.updateHeight(getSize().height);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
