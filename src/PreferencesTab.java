

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PreferencesTab extends JPanel {
    public PreferencesTab() {
        this.setLayout(new BorderLayout());
        this.add(Canvas.getInstance(), BorderLayout.CENTER);
        this.add(LocationPreferencePanel.getInstance(), BorderLayout.SOUTH);
        this.add(new LotteryNumberPanel(), BorderLayout.EAST);
    }
}
