import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

/**
 * A ChildCheckBox is a JCheckBox with a special behavior:
 * when it is unselected, the parent checkbox will also be unselected;
 * when it is selected, if all of the parent's children are selected,
 * the parent checkbox will also be selected.
 * 
 * To accomodate this behavior, the ChildCheckBox holds a reference
 * to its ParentCheckBox.
 * 
 * Note that for this behavior to work, you must either use one of the custom constructors,
 * or manually set the ItemListener to this object.
 * 
 * @author nmalkin
 *
 */
public class ChildCheckBox extends JCheckBox implements ItemListener {
	private ParentCheckBox _parent = null;
	
	public ChildCheckBox(String text) {
		super(text);
		super.addItemListener(this);
	}
	
	public ChildCheckBox(String text, boolean selected) {
		super(text, selected);
		super.addItemListener(this);
	}
	
	/**
	 * Sets this box's parent checkbox.
	 * 
	 * @param parent
	 */
	public void setParent(ParentCheckBox parent) {
		_parent = parent;
	}
	
	/**
	 * When this checkbox is unselected, the parent checkbox will also be unselected;
	 * when it is selected, if all of the parent's children are selected,
	 * the parent checkbox will also be selected.
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if(_parent != null) {
			if(this.isSelected()) {
				if(_parent.allChildrenSelected()) {
					_parent.setSelected(true);
				}
			} else {
				_parent.setSelected(false);
			}
		}
	}

}
