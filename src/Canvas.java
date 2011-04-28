import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Iterator;

import javax.swing.*;

public class Canvas extends JLayeredPane {
	
	
	public Canvas() {
		this.setLayout(null);
		
		Dimension d = new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
		this.setPreferredSize(d);
		this.setSize(d);
		
		this.setBackground(Constants.CANVAS_COLOR);
		this.setOpaque(true);
		
		// sample data; TODO: remove
		House h = new House();
		SubGroup s = new SubGroup();
		Person p1 = new Person("Sumner", Gender.MALE);
		Person p2 = new Person("Miya", Gender.FEMALE);
		
		h.addSubGroup(s);
		s.addPerson(p1);
		s.addPerson(p2);
		
		this.add(p1, Constants.PERSON_LAYER);
		this.add(p2, Constants.PERSON_LAYER);
		this.add(s, Constants.SUBGROUP_LAYER);
		this.add(h, Constants.HOUSE_LAYER);
		
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
		int parentSubGroups = 0; // tracks how many subgroups this person is currently in
		
		for(Component c : this.getComponents()) { // go through all components
			if(c instanceof SubGroup) { // for each subgroup:
				SubGroup s = (SubGroup) c;
				
				// does this subgroup contain the given person?
				Iterator<Person> it = s.iterator();
				while(it.hasNext()) {
					if(it.next() == person) { // yes!
						parentSubGroups++;
						
						if(intersectionAreaFraction(person.getRectangle(), s.getRectangle()) > Constants.INTERSECTION_FRACTION) { // well, should it?
											// (is the person, graphically, inside this subgroup?)
							// yes!
							// ah, good, we're done then
							return;
						} else {
							// no, not anymore
							// remove it
							it.remove();
							parentSubGroups--;
							
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
				if(intersectionAreaFraction(person.getRectangle(), s.getRectangle()) > Constants.INTERSECTION_FRACTION) {
					// yes. add it.
					s.addPerson(person);
					parentSubGroups++;
				}
			}
		}
		
		// if we removed people, some houses may be empty. check for that
		removeEmptyHouses();
		
		if(parentSubGroups == 0) { // this person isn't in any subgroup
			// we should fix that.
			SubGroup newSubGroup = new SubGroup();
			
			// place the new subgroup wherever the subgroup is right now
			newSubGroup.setPosition(
					person.getPosition().x - Constants.SUBGROUP_PADDING, 
					person.getPosition().y - Constants.SUBGROUP_PADDING);
				/*TODO: our use of the PADDING constant here
				 * is based on knowledge of how the house places internal components.
				 * any way to make it more abstract?
				 */
			
			// add the person to the subgroup
			newSubGroup.addPerson(person);
			
			// display it
			this.add(newSubGroup, Constants.SUBGROUP_LAYER);
			
			// but wait! is this new subgroup in a house?
			dropSubGroupAt(newSubGroup, // checks for that and adds a house if necessary
					newSubGroup.getPosition().x, 
					newSubGroup.getPosition().y);
		}
		
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
		int parentHouses = 0; // tracks how many houses this subgroup is currently in
		
		for(Component c : this.getComponents()) { // go through all components
			if(c instanceof House) { // for each house:
				House h = (House) c;
				
				// does this house contain the given subgroup?
				Iterator<SubGroup> it = h.iterator();
				while(it.hasNext()) {
					if(it.next() == subgroup) { // yes!
						parentHouses++;
						
						if(intersectionAreaFraction(subgroup.getRectangle(), h.getRectangle()) > Constants.INTERSECTION_FRACTION) { // well, should it?
											// (is the person, graphically, inside this subgroup?)
							// yes!
							// ah, good, we're done then
							return;
						} else {
							// no, not anymore
							// remove it
							it.remove();
							parentHouses--;
							
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
				if(intersectionAreaFraction(subgroup.getRectangle(), h.getRectangle()) > Constants.INTERSECTION_FRACTION) {
					// yes. add it.
					h.addSubGroup(subgroup);
					parentHouses++;
				}
			}
		}
		
		if(parentHouses == 0) { // this subgroup isn't in any house
			// we should fix that.
			House newHouse = new House();
			
			// place the new house wherever the subgroup is right now
			newHouse.setPosition(
					subgroup.getPosition().x - Constants.HOUSE_PADDING, 
					subgroup.getPosition().y - Constants.HOUSE_PADDING);
				/*TODO: our use of the PADDING constant here
				 * is based on knowledge of how the house places internal components.
				 * any way to make it more abstract?
				 */
			
			// add the subgroup to the house
			newHouse.addSubGroup(subgroup);
			
			// display it
			this.add(newHouse, Constants.HOUSE_LAYER);
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
//		System.out.println("Is " + 
//				x + " within [" + c.getPosition().x + "," + (c.getPosition().x + c.getWidth()) + "] and " +
//				y + " within [" + c.getPosition().y + "," + (c.getPosition().y + c.getHeight()) + "]?");
		
		return 
			x >= c.getPosition().x &&
			x <= c.getPosition().x + c.getWidth() &&
			y >= c.getPosition().y &&
			y <= c.getPosition().y + c.getHeight();
	}
	
	/**
	 * What fraction of Rectangle A's area does its intersection with Rectangle B take up?
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private double intersectionAreaFraction(Rectangle a, Rectangle b) {
		if(! a.intersects(b)) {
			return 0;
		}
		
		Rectangle intersection = a.intersection(b);
		
		double aArea = a.getWidth() * a.getHeight();
		double intersectionArea = intersection.getWidth() * intersection.getHeight();
		
		if(aArea == 0) {
			return 0;
		} else {
			return intersectionArea / aArea;
		}
	}
	
	/*
	 * FIXME:
	 * sometimes, relatively rarely, when a person is dropped in a new location,
	 * a new house and subgroup are created, but the subgroup is behind the house (not seen),
	 * contrary to the layer hierarchy.
	 */
}
