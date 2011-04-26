import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class ResultsTab extends JPanel {
	
		public ResultsTab() {
			this.setLayout(new BorderLayout());
			JPanel sidePanel = new JPanel();
			Dimension size = new Dimension(150, 500);
			sidePanel.setPreferredSize(size);
			this.add(new JPanel(), BorderLayout.LINE_START);
			this.add(new LotteryNumberPanel(), BorderLayout.LINE_END);
			this.add(new ResultsPanel(), BorderLayout.CENTER);
		}
		
}
