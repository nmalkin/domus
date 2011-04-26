import java.awt.BorderLayout;

import javax.swing.JPanel;


public class PreferencesTab extends JPanel {
	public PreferencesTab() {
		this.setLayout(new BorderLayout());
		this.add(new Canvas(), BorderLayout.CENTER); // canvas
		this.add(LotteryNumberPanel.getInstance(), BorderLayout.EAST);
		this.add(LocationPreferencePanel.getInstance(), BorderLayout.SOUTH);
	}
}
