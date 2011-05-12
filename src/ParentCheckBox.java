

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JCheckBox;

/**
 * A ParentCheckBox is a JCheckBox with a special behavior: when it is selected
 * or unselected, all of its child checkboxes are also selected/unselected.
 * 
 * To accomodate this behavior, the ParentCheckBox holds a reference to all of
 * its child checkboxes. (Note that though there is a ChildCheckBox class, it
 * exists for a different (reciprocal) behavior; therefore, ParentCheckBox's
 * child checkboxes need not be ChildCheckBoxes.)
 * 
 * Note that for this behavior to work, you must either use one of the custom
 * constructors, or manually set the ItemListener to this object.
 * 
 * @author nmalkin
 * 
 */
public class ParentCheckBox extends JCheckBox implements ItemListener {
    private Collection<JCheckBox> _children = new LinkedList<JCheckBox>();

    public ParentCheckBox(String text) {
        super(text);
        super.addItemListener(this);
    }

    public ParentCheckBox(String text, boolean selected) {
        super(text, selected);
        super.addItemListener(this);
    }

    /**
     * Registers a child with this ParentCheckBox.
     * 
     * @param child
     */
    public void addChild(JCheckBox child) {
        _children.add(child);
    }

    /**
     * When this checkbox is selected or unselected, all of its child checkboxes
     * are also selected/unselected.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (this.hasFocus()) {
            for (JCheckBox child : _children) {
                child.setSelected(this.isSelected());
            }
        }
    }

    /**
     * Returns true if all child checkboxes are selected.
     * 
     * @return
     */
    public boolean allChildrenSelected() {
        boolean allSelected = true;

        for (JCheckBox child : _children) {
            allSelected &= child.isSelected();
        }

        return allSelected;
    }

}
