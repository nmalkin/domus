import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class ResultsTab extends JPanel {
	
		public ResultsTab() {
			this.setLayout(new BorderLayout());
			JPanel sidePanel = new JPanel();
			Dimension size = new Dimension(150, 550);
			sidePanel.setPreferredSize(size);
			this.add(new JPanel(), BorderLayout.LINE_START);
			this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
			this.add(new ResultsPanel(), BorderLayout.CENTER);
		}
		
		public void updateResults() {
			int[] years = State.getInstance().getYears();
			boolean sophomoreOnly = State.getInstance().isSophomoreOnly();
			for (House h : State.getInstance().getGroup()) {
				Collection<Dorm> locations = h.getLocationPreference();
				boolean genderNeutral = h.isGenderNeutral();
				for (SubGroup sg : h) {
					RoomList results = Database.getResults(locations, sg.size(), years, genderNeutral, sophomoreOnly);
					State.getInstance().putResults(sg, results);
				}
			}
		}
		
		@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			updateResults();
		}
}
