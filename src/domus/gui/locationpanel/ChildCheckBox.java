package domus.gui.locationpanel;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JCheckBox;

/**
 * A ChildCheckBox is a JCheckBox with a special behavior: when it is
 * unselected, the parent checkboxes will also be unselected; when it is
 * selected, if all of the parent's children are selected, the parent checkbox
 * will also be selected.
 * 
 * To accommodate this behavior, the ChildCheckBox holds a reference to its
 * ParentCheckBox.
 * 
 * Note that for this behavior to work, you must either use one of the custom
 * constructors, or manually set the ItemListener to this object.
 * 
 * @author nmalkin
 * 
 */
public class ChildCheckBox extends JCheckBox implements ItemListener {
    
    private Collection<ParentCheckBox> _parents;

    public ChildCheckBox(String text) {
        super(text);
        super.addItemListener(this);

        _parents = new LinkedList<ParentCheckBox>();
    }

    public ChildCheckBox(String text, boolean selected) {
        super(text, selected);
        super.addItemListener(this);

        _parents = new LinkedList<ParentCheckBox>();
    }

    /**
     * Sets this box's parent checkbox.
     * 
     * @param parent
     */
    public void addParent(ParentCheckBox parent) {
        _parents.add(parent);
    }

    /**
     * When this checkbox is unselected, the parent checkbox will also be
     * unselected; when it is selected, if all of the parent's children are
     * selected, the parent checkbox will also be selected.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        for (ParentCheckBox parent : _parents) {
            if (this.isSelected()) {
                if (parent.allChildrenSelected()) {
                    parent.setSelected(true);
                }
            } else {
                parent.setSelected(false);
            }
        }
    }

}
