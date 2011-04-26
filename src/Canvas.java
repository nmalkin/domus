import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.*;

public class Canvas extends JPanel {
	
	
	public Canvas() {
		this.setLayout(null);
		
		Dimension d = new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
		this.setPreferredSize(d);
		this.setSize(d);
		
		this.setBackground(Constants.CANVAS_COLOR);
		
		// sample data; TODO: remove
		House h = new House();
		SubGroup s = new SubGroup();
		Person p1 = new Person("Sumner", Gender.MALE);
		Person p2 = new Person("Miya", Gender.FEMALE);
		
		h.addSubGroup(s);
		s.addPerson(p1);
		s.addPerson(p2);
		
		this.add(p1);
		this.add(p2);
		this.add(s);
		this.add(h);
		
		/*
		 * NOTE:
		 * For some reason I have yet to understand,
		 * the order in which you add components to the JPanel matters.
		 * If you don't add them in the order [Person, SubGroup, House],
		 * then the lower-level components won't be individually draggable.
		 */
	}
	
	/**
	 * Performs a drop of the given person at the given location.
	 * 
	 * This entails checking what subgroup the person was dropped inside:
	 * if it's the subgroup it was already in, nothing's changed;
	 * if it's a new subgroup, it is added there and removed from the old one.
	 * 		TODO: strange behavior possible with overlapping subgroups
	 * 
	 * @param person
	 * @param x
	 * @param y
	 */
	public void dropPersonAt(Person person, int x, int y) {
		for(Component c : this.getComponents()) { // go through all components
			if(c instanceof SubGroup) { // for each subgroup:
				SubGroup s = (SubGroup) c;
				
				// does this subgroup contain the given person?
				Iterator<Person> it = s.iterator();
				while(it.hasNext()) {
					if(it.next() == person) { // yes!
						if(isDraggablePositionableComponentAt(s, x, y)) { // well, should it?
											// (is the person, graphically, inside this subgroup?)
							// yes!
							// ah, good, we're done then
							return;
						} else {
							// no, not anymore
							// remove it
							it.remove();
							
							// are there any people left in this subgroup?
							if(s.isEmpty()) { // no
								this.remove(s); // remove it from view
							} else { // yes
								s.updatePeoplePositions(); // update their positions
							}
						}
					}
				}
				
				// at this point, this subgroup definitely doesn't contain the given person
				// should it?
				if(isDraggablePositionableComponentAt(s, x, y)) {
					// yes. add it.
					s.addPerson(person);
				}
			}
		}
		
		// if we removed people, some houses may be empty. check for that
		removeEmptyHouses();
		
		// with the addition/removal, the canvas could have changed. let's repaint it.
		this.repaint();
	}
	
	/**
	 * Performs a drop of the given person at the given location.
	 * 
	 * This entails checking what subgroup the person was dropped inside:
	 * if it's the subgroup it was already in, nothing's changed;
	 * if it's a new subgroup, it is added there and removed from the old one.
	 * 		TODO: strange behavior possible with overlapping subgroups
	 * 
	 * @param subgroup
	 * @param x
	 * @param y
	 */
	public void dropSubGroupAt(SubGroup subgroup, int x, int y) {
		for(Component c : this.getComponents()) { // go through all components
			if(c instanceof House) { // for each house:
				House h = (House) c;
				
				// does this house contain the given subgroup?
				Iterator<SubGroup> it = h.iterator();
				while(it.hasNext()) {
					if(it.next() == subgroup) { // yes!
						if(isDraggablePositionableComponentAt(h, x, y)) { // well, should it?
											// (is the person, graphically, inside this subgroup?)
							// yes!
							// ah, good, we're done then
							return;
						} else {
							// no, not anymore
							// remove it
							it.remove();
							
							// are there any subgroups left in this house?
							if(h.isEmpty()) { // no
								this.remove(h); // remove it from view
							} else { // yes
								h.updateSubGroupPositions(); // update their positions
							}
						}
					}
				}
				
				// at this point, this subgroup definitely doesn't contain the given person
				// should it?
				if(isDraggablePositionableComponentAt(h, x, y)) {
					// yes. add it.
					h.addSubGroup(subgroup);
				}
			}
		}
		
		// with the addition/removal, the canvas could have changed. let's repaint it.
		this.repaint();
	}
	
	/**
	 * Removes any houses on the canvas that do not have any people inside them.
	 */
	private void removeEmptyHouses() {
		for(Component c : this.getComponents()) {
			if(c instanceof House) {
				House h = (House) c;
				
				h.removeEmptySubGroups();
				
				if(h.isEmpty()) {
					this.remove(h);
				}
			}
		}
	}
	
	private boolean isDraggablePositionableComponentAt(DraggablePositionableComponent c, int x, int y) {
		return 
			x >= c.getPosition().x &&
			x <= c.getPosition().x + c.getWidth() &&
			y >= c.getPosition().y &&
			y <= c.getPosition().y + c.getHeight();
	}
}
