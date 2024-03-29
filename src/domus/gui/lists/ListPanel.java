package domus.gui.lists;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import domus.Constants;
import domus.State;
import domus.data.RoomList;
import domus.gui.tabs.ListsTab;

public class ListPanel extends JPanel {

	private RoomList _list;
	private static ImageIcon _removeIcon = new ImageIcon(Constants.REMOVE_FILE, "remove from list");
	private JList _panel;
    private static JList _sourceList;
	private JScrollPane _scroller;
	private static boolean _controlDown = false;
	
	private final int _listWidth = Constants.LISTS_WIDTH;
	private final int _listHeight = Constants.LISTS_HEIGHT;
	private final int _itemWidth = Constants.LISTS_ITEM_WIDTH;
	private final int _itemHeight = Constants.LISTS_ITEM_HEIGHT;
	
	public ListPanel(RoomList list) {
		super();
		_list = list;
		this.setLayout(null);
		this.setPreferredSize(new Dimension(_listWidth, _listHeight));
		this.setSize(new Dimension(_listWidth, _listHeight));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBounds(0, 0, _listWidth, _itemHeight);
		JLabel label = new JLabel(_list.getName());
		label.setFont(Constants.DOMUS_FONT.deriveFont(12f).deriveFont(Font.BOLD));
		if (_list.getColor() == null) {
			_list.setColor(null);
		}
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		g.setColor(_list.getColor());
		g.fillRect(0, 0, 10, 10);
		ImageIcon icon = new ImageIcon(image);
		label.setIcon(icon);
		label.setHorizontalTextPosition(JLabel.LEADING);
		label.setIconTextGap(10);
		panel.add(label);
		JLabel removeLabel = new JLabel(_removeIcon);
		removeLabel.addMouseListener(new RemoveListListener());
		panel.add(Box.createHorizontalGlue());
		removeLabel.setVisible(false);
		panel.addMouseListener(new HoverListener());
		removeLabel.addMouseListener(new RemoveListListener());;
		panel.add(Box.createHorizontalGlue());
		panel.add(removeLabel);
		panel.setBorder(new EmptyBorder(0, Constants.INSET + 1, 0, Constants.INSET + 1));
		this.add(panel);
	    DefaultListModel listModel = new DefaultListModel();
		_panel = new JList(listModel);
		_panel.setPreferredSize(new Dimension(_listWidth, 0));
		_panel.setSize(new Dimension(_listWidth, 0));
		_panel.setLayout(new BoxLayout(_panel, BoxLayout.PAGE_AXIS));
		Color c = this.getBackground();
		_panel.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));
		_panel.setDragEnabled(true);
		_panel.setDropMode(DropMode.INSERT);
		_panel.setTransferHandler(new ListTransferHandler());
		List<MouseListener> listeners = new LinkedList<MouseListener>();
		for (MouseListener l : _panel.getMouseListeners()) {
		    listeners.add(l);
		}
		for (MouseListener l : listeners) {
		    _panel.removeMouseListener(l);
		}
		_panel.addMouseListener(new ListClickListener());
		for (MouseListener l : listeners) {
		    _panel.addMouseListener(l);
		}
		_panel.setSelectionModel(new ToggleListSelectionModel());
		_scroller = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_scroller.setBounds(0, panel.getHeight(), _listWidth, _listHeight - _itemHeight);
		this.add(_scroller);
		updateList();
	}
	
	/**
	 * Updates the list by removing all components and
	 * then adding all existing ones.
	 */
	public void updateList() {
	    DefaultListModel listModel = (DefaultListModel) _panel.getModel();
	    listModel.removeAllElements();
		for (ResultsListItem item : _list) {
			item.setButtonIcon(_removeIcon);
			item.updateWidth(_itemWidth);
			if (listModel.isEmpty()) {
			    _panel.setCellRenderer(item);
			}
			listModel.addElement(item);
			item.updateProbability();
			item.validateListLabels();
		}
		
	}
	
	/** 
	 * Updates the probabilities of all components in the
	 * list model.
	 */
	public void updateDisplay() {
	    DefaultListModel listModel = (DefaultListModel) _panel.getModel();
	    for (int i = 0; i < listModel.size(); ++i) {
	        ResultsListItem item = (ResultsListItem) listModel.getElementAt(i);
	        item.updateProbability();
	        item.validateListLabels();
	        listModel.setElementAt(item, i);
	    }
	}
	
	/** Removes a ResultsListItem from list */
	public void removeResultsListItem(ResultsListItem item) {
		DefaultListModel listModel = (DefaultListModel) _panel.getModel();
		listModel.removeElement(item);
		_list.remove(item);
		item.getRoom().removeFromRoomList(_list);
		item.getRoom().removeFromListItem(item);
		for (ResultsListItem rli : item.getRoom().getListItems()) {
			rli.validateListLabels();
		}
		ListsTab.getInstance().updateListsDisplay();
	}
	
	/** Listener for removal button display */
	private class HoverListener extends MouseAdapter {
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//show the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 1);
			label.setVisible(true);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			//hide the remove button label
			JPanel panel = (JPanel) e.getSource();
			JLabel label = (JLabel) panel.getComponent(panel.getComponentCount() - 1);
			if (panel.getComponentAt(e.getX(), e.getY()) != label)
				label.setVisible(false);
		}
		
	}
	
	/** Listener for list removal */
	private class RemoveListListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if (_list != null) {
				ResultsListItem[] items = _list.toArray(new ResultsListItem[0]);
				for (int i = 0; i < items.length; ++i) {
					removeResultsListItem(items[i]);
				}
				State.getInstance().removeRoomList(_list);
				_list.clear();
				_list = null;
				ListsTab.getInstance().removeList(ListPanel.this);
				ListsTab.getInstance().updateLists();
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			JLabel label = (JLabel) e.getSource();
			label.setVisible(false);
		}
		
	}
	
	/** TransferHandler for List drag and drop */
	private class ListTransferHandler extends TransferHandler {
	    
	    private int[] indices = null;
	    private int lastIndex;
	    
	    @Override
	    public boolean canImport(TransferHandler.TransferSupport info) {
	        // Check for proper flavor
	        try {
	            if (!info.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ResultsListItem.class.getName() + "\""))) {
	                return false;
	            }
	        }
	        catch (ClassNotFoundException e) {
	            System.err.println("Class not found during drag and drop action. " + e.getMessage());
	            e.printStackTrace();
	        }
	        
	        if (info.getUserDropAction() == TransferHandler.COPY) {
	            info.setDropAction(TransferHandler.MOVE);
	        }
	        
	        if (info.getComponent() != _sourceList) {
    	        Transferable trans = info.getTransferable();
                if (trans == null)
                    return false;
                
                List<ResultsListItem> items = null;
                try {
                    items = (List<ResultsListItem>) trans.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ResultsListItem.class.getName() + "\""));
                }
                catch (Exception e) {
                    System.out.println("Error during drag and drop action. " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
                
                for (ResultsListItem item : items) {
                    if (!_list.contains(item))
                        info.setDropAction(TransferHandler.COPY);
                }
                
                if (info.getDropAction() != TransferHandler.COPY)
                    return false;
	        }

	        return true;
	    }
	    
	    @Override
	    protected Transferable createTransferable(JComponent c) {
	        JList list = (JList) c;
	        _sourceList = list;
	        indices = list.getSelectedIndices();
	        Object[] values = list.getSelectedValues();
	        
	        return new ResultsListItemTransferable(values);
	    }
	    
	    @Override
	    public int getSourceActions(JComponent c) {
	        return TransferHandler.COPY_OR_MOVE;
	    }
	    
	    @Override
	    public boolean importData(TransferHandler.TransferSupport info) {
	        if (!info.isDrop()) {
	            return false;
	        }
	        
	        JList list = (JList) info.getComponent();
	        DefaultListModel listModel = (DefaultListModel) list.getModel();
	        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
	        int index = dl.getIndex();
	        if (!dl.isInsert()) {
	            return false;
	        }
	        
	        Transferable trans = info.getTransferable();
	        if (trans == null)
	            return false;
	        
	        List<ResultsListItem> items = null;
	        try {
	            items = (List<ResultsListItem>) trans.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ResultsListItem.class.getName() + "\""));
	        }
	        catch (Exception e) {
	            System.err.println("Error during drag and drop action. " + e.getMessage());
	            e.printStackTrace();
	            return false;
	        }
	        
	        for (ResultsListItem item : items) {
	            // TODO: fix this so it's not just graphical and 
	            // is represented in the RoomLists as well (ordering)
	            if (!_list.contains(item)) {
	                _list.add(item);
	                item.getRoom().addToRoomList(_list);
	            }
	            item.validateListLabels();
	            for (ResultsListItem rli : item.getRoom().getListItems()) {
	                    rli.validateListLabels();
	            }
	            if (info.getDropAction() == TransferHandler.MOVE || (info.getDropAction() == TransferHandler.COPY && !listModel.contains(item)))
	                listModel.add(index++, item);    
	        }
	        
	        lastIndex = index;
	        
	        return true;
	    }
	    
	    @Override
	    protected void exportDone(JComponent c, Transferable data, int action) {
	        JList source = (JList) c;
	        DefaultListModel listModel = (DefaultListModel) source.getModel();
	        
	        boolean success = false;
	        if (action == TransferHandler.MOVE) {
	            for (int i = indices.length - 1; i >= 0; --i) {
	                int index = indices[i];
	                if (lastIndex <= index) {
	                    index += indices.length;
	                    --lastIndex;
	                }
	                listModel.remove(index);
	                success = true;
	            }
	        }
	        if (action == TransferHandler.COPY) {
	            for (int i = indices.length - 1; i >= 0; --i) {
	                int index = indices[i];
	                if (index >= listModel.getSize())
	                    continue;
	                ResultsListItem item = (ResultsListItem) listModel.getElementAt(index);
	                item.validateListLabels();
	                listModel.setElementAt(item, index);
	                success = true;
	            }
	        }
	        
	        if (success)
	            _sourceList.getSelectionModel().clearSelection();
	        
	        lastIndex = 0;
	        indices = null;
	        _sourceList = null;
	    }
	    
	    /** Transferable representing a group of ResultsListItems */
	    private class ResultsListItemTransferable implements Transferable {

	        private List<ResultsListItem> _items;
	        
	        public ResultsListItemTransferable(Object[] values) {
	            _items = new ArrayList<ResultsListItem>();
	            for (Object o : values) {
	                ResultsListItem newItem = new ResultsListItem(((ResultsListItem) o).getRoom(), null);
	                newItem.setButtonIcon(_removeIcon);
	                newItem.updateWidth(_itemWidth);
	                newItem.updateProbability();
	                newItem.validateListLabels();
	                _items.add(newItem);
	            }
	        }
	        
            @Override
            public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException, IOException {
                return _items;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] flavors = new DataFlavor[1];
                try {
                    flavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ResultsListItem.class.getName() + "\"");
                }
                catch (ClassNotFoundException e) {
                    System.err.println("Error during drag and drop action. " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                if (!flavor.getRepresentationClass().getName().equals(ResultsListItem.class.getName()))
                    return false;
                return true;
            }
            
            @Override
            public String toString() {
                return _items.toString();
            }
            
	    }
	    
	}
	
	/** 
	 * Listens for clicks on list items. Used for removal and
	 * opening/closing the info panel.
	 * 
	 * @author jswarren
	 */
	private class ListClickListener extends MouseAdapter {
	    
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        int index = _panel.locationToIndex(e.getPoint());
	        if (index < 0) {
	            return;
	        }
	        DefaultListModel listModel = (DefaultListModel) _panel.getModel();
	        ResultsListItem item = (ResultsListItem) listModel.getElementAt(index);
	        JLabel button = item.getButton();
	        int height = 0;
	        for (int i = 0; i < index; ++i) {
	            ResultsListItem rli = (ResultsListItem) listModel.get(i);
                height += rli.getPreferredSize().height; 
	        }
	        Point click = new Point(e.getX() - button.getX(), e.getY() - button.getY() - height);
	        if (button.contains(click)) {
	            removeResultsListItem(item);
	        }
	        else {
	            if (!e.isControlDown()) {
    	            item.setOpen(!item.isOpen());
    	            listModel.setElementAt(item, index);
	            }
	        }
	    }
	    
	}
	
	/**
	 * Used to toggle selection for list items, insted of needing to
	 * select something else to unselect an item.
	 * 
	 * @author jswarren
	 */
	private class ToggleListSelectionModel extends DefaultListSelectionModel {
	    
	    boolean gestureStarted = false;
	    	    
	    @Override
	    public void setSelectionInterval(int index0, int index1) {
	        if (!_controlDown)
	            return;
	        
	        if (isSelectedIndex(index0) && !gestureStarted) {
	            super.removeSelectionInterval(index0, index1);
	        }
	        else {
	            super.setSelectionInterval(index0, index1);
	        }
	        
	    }
	    
	    @Override
	    public void setValueIsAdjusting(boolean isAdjusting) {
	        gestureStarted = isAdjusting;
	    }
	    
	}
	
}
