package domus.gui.tabs;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import domus.gui.canvas.Canvas;
import domus.gui.locationpanel.LocationPreferencePanel;
import domus.gui.lotterynumberpanel.LotteryNumberPanel;

public class PreferencesTab extends JPanel {
    public PreferencesTab() {
        this.setLayout(new BorderLayout());
        this.add(Canvas.getInstance(), BorderLayout.CENTER);
        this.add(LocationPreferencePanel.getInstance(), BorderLayout.SOUTH);
        this.add(new LotteryNumberPanel(), BorderLayout.EAST);
    }
}
