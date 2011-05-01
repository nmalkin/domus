import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JPanel;


public class ResultsTab extends JPanel {
	
	
		public ResultsTab() {
			this.setLayout(new BorderLayout());
			JPanel sidePanel = new JPanel();
			Dimension size = new Dimension(150, 550);
			sidePanel.setPreferredSize(size);
			this.add(new JPanel(), BorderLayout.LINE_START);
			this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
		}
		
		public void updateResults() {
			for (Component c : this.getComponents()) {
				if (c instanceof ResultsPanel) {
					this.remove(c);
				}
			}
			State.getInstance().clearResults();
			int[] years = State.getInstance().getYears();
			boolean sophomoreOnly = State.getInstance().isSophomoreOnly();
			for (House h : State.getInstance().getGroup()) {
				Collection<Dorm> locations = h.getLocationPreference();
				boolean genderNeutral = h.isGenderNeutral();
				for (SubGroup sg : h) {
					RoomList results = Database.getResults(locations, sg.getOccupancy(), years, genderNeutral, sophomoreOnly);
					State.getInstance().putResults(sg, results);
				}
			}
			this.add(new ResultsPanel());
		}
		
		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			updateResults();
		}
}
