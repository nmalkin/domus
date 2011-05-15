package domus.gui.lists;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import domus.State;
import domus.data.RoomList;
import domus.gui.accordionlist.AccordionItem;

public class AddListener extends MouseAdapter {

    private AccordionItem _container;
    private JDialog _prompt;
    private JComboBox _cb;

    public AddListener(AccordionItem c) {
        _container = c;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        List<RoomList> roomLists = State.getInstance().getRoomLists();
        String[] lists = null;
        if (roomLists.size() > 0) {
            lists = new String[roomLists.size()];
            int i = 0;
            for (RoomList rl : roomLists) {
                lists[i++] = rl.getName();
            }
        }
        _prompt = new JDialog(JOptionPane.getFrameForComponent((JComponent) e
                .getSource()), "Domus", true);
        _prompt.setSize(new Dimension(250, 120));
        JComponent pane = (JComponent) _prompt.getContentPane();
        pane.setSize(new Dimension(250, 120));
        pane.setLayout(new FlowLayout());
        ((FlowLayout) pane.getLayout()).setAlignment(FlowLayout.CENTER);
        JLabel label1 = new JLabel("Start typing to choose a list");
        JLabel label2 = new JLabel("or create a new one.");
        label1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        label2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane.add(label1);
        pane.add(label2);
        _cb = (lists != null) ? new JComboBox(lists) : new JComboBox();
        _cb.setSize(new Dimension(200, 20));
        _cb.setEditable(true);
        _cb.setSelectedIndex(-1);
        _cb.putClientProperty("JComboBox.isTableCellEditor", true);
        if (_container.getClass().getSimpleName().equals("ResultsListTab")) {
            _cb
                    .addActionListener(((ResultsListTab) _container).new RoomListSelectionListener(
                            _prompt));
        } else {
            _cb
                    .addActionListener(((ResultsListItem) _container).new RoomListSelectionListener(
                            _prompt));
        }
        pane.add(_cb);
        _prompt.setLocationRelativeTo(JOptionPane
                .getFrameForComponent((JComponent) e.getSource()));
        _prompt.setVisible(true);
    }

}