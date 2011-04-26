import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import static java.awt.event.KeyEvent.*;

/**
 * A single results inside a tab of results for a specific subgroup.
 * 
 * @author jswarren
 *
 */
public class ResultsListItem extends JPanel implements AccordionItem {

	private Room _room;
	private JLabel _label;
	private JLabel _addButton;
	private static ImageIcon _addToListIcon = ImageIconLoader.getInstance().createImageIcon("images/add_to_list.png", "add to list");
	private int _index;
	private boolean _isAddedToList;
	private Collection<RoomList> _roomList;
	private Insets _insets;
	
	private final int _itemHeight = 15;
	private final int _itemWidth = 200;
	
	public ResultsListItem(Room room) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		Dimension size = new Dimension(_itemWidth, _itemHeight);
		this.setPreferredSize(size);
		this.setSize(size);
		_room = room;
		_label = new JLabel(_room.getDorm().getName() + " " + _room.getNumber());
		this.add(_label);
		this.add(Box.createHorizontalGlue());
		_addButton = new JLabel(_addToListIcon);
		_addButton.addMouseListener(new AddListener());
		this.add(_addButton);	
	}
	
	public void setInsets(Insets insets) {
		_insets = insets;
		this.setBorder(new EmptyBorder(_insets));
	}
	
	public Room getRoom() {
		return _room;
	}
	
	@Override
	public int getIndex() {
		return _index;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void setIndex(int index) {
		_index = index;
	}

	@Override
	public void setOpen(boolean open) { }

	@Override
	public void addItem(AccordionItem item) { }
	
	@Override
	public int compareTo(AccordionItem o) {
		return (_index < o.getIndex() ? -1 : (_index > o.getIndex() ? 1 : 0));
	}

	private class AddListener extends MouseAdapter {
		
		private String _newList = "Make a new list...";
		private JDialog _prompt;
		private String[] _lists;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			List<RoomList> roomLists = State.getInstance().getRoomLists();
			{
				_lists = null;
				if (roomLists.size() > 0) {
					_lists = new String[roomLists.size()];
					int i = 0;
					for (RoomList rl : roomLists) {
						_lists[i++] = rl.getName();
					}
				}
				_prompt = new JDialog(JOptionPane.getFrameForComponent(ResultsListItem.this), "Domus");
				_prompt.setSize(new Dimension(250, 120));
				JComponent pane = (JComponent) _prompt.getContentPane();
				pane.setSize(new Dimension(250, 120));
				pane.setLayout(new FlowLayout());
				((FlowLayout) pane.getLayout()).setAlignment(FlowLayout.CENTER);
				JLabel label1 = new JLabel("Start typing to choose a list");
				JLabel label2 = new JLabel("or create a new one.");
				label1.setAlignmentX(CENTER_ALIGNMENT);
				label2.setAlignmentX(CENTER_ALIGNMENT);
				pane.add(label1);
				pane.add(label2);
				JComboBox cb = (_lists != null) ? new JComboBox(_lists): new JComboBox();
				cb.setSize(new Dimension(200, 20));
				cb.setEditable(true);
				ListAutodetectionListener listener = new ListAutodetectionListener();
				cb.addActionListener(listener);
				KeyListener[] listeners = cb.getKeyListeners();
				System.out.println(listeners.length);
				for (int i = 0; i < listeners.length; ++i) {
					cb.removeKeyListener(listeners[i]);
				}
				listeners = cb.getKeyListeners();
				System.out.println(listeners.length);
				cb.addKeyListener(listener);
				pane.add(cb);
				_prompt.setLocationRelativeTo(JOptionPane.getFrameForComponent(ResultsListItem.this));
				_prompt.setVisible(true);
//				String selected = (String) JOptionPane.showInputDialog(ResultsListItem.this, "Choose list:", "Domus", JOptionPane.PLAIN_MESSAGE, null, lists, null);
			}
		}
		
		private class ListAutodetectionListener extends KeyAdapter implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String selected = (String) cb.getSelectedItem();
				System.out.println(selected);
				if (selected == null || selected.equals("")) {
					System.out.println("cancel");
				}
				else {
					boolean exists = false;
					for (RoomList rl : State.getInstance().getRoomLists()) {
						if (rl.getName().equals(selected)) {
							rl.add(_room);
							ListsTab.getInstance().updateLists();
							exists = true;
							break;
						}
					}
					if (!exists) {
						RoomList list = new RoomList(selected);
						State.getInstance().addRoomList(list);
						list.add(_room);
						ListsTab.getInstance().updateLists();
					}
				}
				_prompt.setVisible(false);
				_prompt.dispose();
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("pressed " + e);
				JComboBox cb = (JComboBox) e.getSource();
				int selected = cb.getSelectedIndex();
				if (e.getKeyCode() == VK_DOWN) {
					if (cb.getItemCount() > 0) {
						cb.setPopupVisible(true);
					}
					if (selected < cb.getItemCount()) {
						cb.setSelectedIndex(selected + 1);
					}
				}
				if (e.getKeyCode() == VK_UP) {
					if (cb.getItemCount() > 0) {
						cb.setPopupVisible(true);
					}
					if (selected > 0) {
						cb.setSelectedIndex(selected - 1);
					}
				}
				if (e.getKeyCode() == VK_ENTER) {
					cb.setSelectedItem(cb.getItemAt(selected));
					System.out.println("enter");
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println("typed " + e);
			}
			
		}
		
	} // end of AddListener
	
} // end of ResultsListItem
