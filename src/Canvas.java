import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.*;

public class Canvas extends JLayeredPane {
	private Image _trashImage;
	
	public Canvas() {
		this.setLayout(null);
		
		Dimension d = new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT);
		this.setPreferredSize(d);
		this.setSize(d);
		
		this.setBackground(Constants.CANVAS_COLOR);
		this.setOpaque(true);
		
		AddPersonListener listener = new AddPersonListener();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		
		// load trash icon
		try {
			_trashImage = javax.imageio.ImageIO.read(new java.io.File(Constants.TRASH_FILE));
		} catch(java.io.IOException e) {
			//TODO: yell, or break silently?
		}
		
		// sample data; TODO: remove
//		House h = new House();
//		SubGroup s = new SubGroup();
//		Person p1 = new Person("Sumner", Gender.MALE);
//		Person p2 = new Person("Miya", Gender.FEMALE);
//		
//		State.getInstance().getGroup().add(h);
//		h.addSubGroup(s);
//		s.addPerson(p1);
//		s.addPerson(p2);
//		
//		h.setPosition(200,150);
//		h.updateSubGroupPositions();
//		
//		this.add(p1, Constants.PERSON_LAYER);
//		this.add(p2, Constants.PERSON_LAYER);
//		this.add(s, Constants.SUBGROUP_LAYER);
//		this.add(h, Constants.HOUSE_LAYER);
	}
	
	private boolean intersecting(DraggablePositionableComponent a, DraggablePositionableComponent b) {
		return GraphicsSupport.intersectionAreaFraction(
				a.getRectangle(), 
				b.getRectangle()) > Constants.INTERSECTION_FRACTION;
	}
	
	/**
	 * Performs a drop of the given person at the given location.
	 * 
	 * This entails checking what subgroup the person was dropped inside:
	 * if it's the subgroup it was already in, nothing's changed;
	 * if it's a new subgroup, it is added there and removed from the old one.
	 * 
	 * @param person
	 */
	public void dropPerson(Person person) {
		if(tryRemove(person)) { // if the person is over the trash, remove them
			repaint();
			return;
		}
		
		SubGroup newSubGroup = null;
		
		// if this person can be put in any existing subgroup, add him to it
		outer:
		for(House h : State.getInstance().getGroup()) {
			if(intersecting(person, h)) { // person is inside this house
				for(SubGroup s : h) {
					if(intersecting(person, s)) { // person is inside this subgroup 
						newSubGroup = s;
						break outer;
					}
				}
			}
		}
		
		// remove this person from his/her old subgroup
		SubGroup currentSubGroup = person.getSubGroup();
		
		if(currentSubGroup != null) {
			if(currentSubGroup == newSubGroup) { // ...unless he's staying in the same subgroup
				currentSubGroup.updatePeoplePositions(); // move the person pack to his/her spot
				return; // otherwise, no changes are needed
			}
			
			currentSubGroup.removePerson(person);
			if(currentSubGroup.isEmpty()) {
				removeIfEmpty(currentSubGroup); // (may require cleaning up subgroup/house, if they have been left empty)
			} else {
				currentSubGroup.updatePeoplePositions();
			}	
		}
		
		if(newSubGroup == null) { // no subgroup was found for this person. create a new one.
			newSubGroup = new SubGroup();
			
			// place the new subgroup wherever the person is right now
			newSubGroup.setPosition(
					person.getPosition().x - Constants.SUBGROUP_PADDING, 
					person.getPosition().y - Constants.SUBGROUP_PADDING);
			
			// add the person to the subgroup
			newSubGroup.addPerson(person);
			
			// place the subgroup into a house
			placeSubGroup(newSubGroup);
			
			// display it
			this.add(newSubGroup, Constants.SUBGROUP_LAYER);
		} else {
			// add the person to the (already-existing) subgroup
			newSubGroup.addPerson(person);
		}
		
		newSubGroup.updatePeoplePositions();
		newSubGroup.getHouse().updateSubGroupPositions();
		
		State.getInstance().setSelectedHouse(newSubGroup.getHouse());
		
		this.repaint();
	}
	
	/**
	 * Performs a drop of the given subgroup at its current location.
	 * 
	 * @param subgroup
	 */
	public void dropSubGroup(SubGroup subgroup) {
		if(tryRemove(subgroup)) { // if the subgroup is over the trash, remove them
			repaint();
			return;
		}
		
		placeSubGroup(subgroup);
	}
	
	public void placeSubGroup(SubGroup subgroup) {
		House newHouse = null;
		
		for(House h : State.getInstance().getGroup()) {
			if(intersecting(subgroup, h)) { // subgroup is inside this house
				// is the subgroup intersecting any existing subgroups?
				for(SubGroup s : h) {
					if(	subgroup != s &&
						intersecting(subgroup, s)) 
					{ // subgroup is inside this subgroup 
						mergeSubGroups(subgroup, s);
						h.updateSubGroupPositions();
						
						State.getInstance().setSelectedHouse(h);
						
						this.repaint();
						return;
					}
				}
				
				// subgroup is inside house, but not intersecting any existing subgroups
				// add it to the house.
				newHouse = h;
				break;
			}
		}
		
		House currentHouse = subgroup.getHouse();
		
		if(newHouse == null) {
			// subgroup isn't in any house. create one for it.
			newHouse = new House();
			
			// save the new house to State
			State.getInstance().getGroup().add(newHouse);
			
			// set the position of the new house to wherever the subgroup is right now
			newHouse.setPosition(
					subgroup.getPosition().x - Constants.HOUSE_PADDING, 
					subgroup.getPosition().y - Constants.HOUSE_PADDING);
			
			// display it
			this.add(newHouse, Constants.HOUSE_LAYER);
		} else if(newHouse == currentHouse) {
			currentHouse.updateSubGroupPositions(); // move subgroup to its default location in house
			this.repaint();
			return;
		}
		
		if(currentHouse != null) {
			currentHouse.removeSubGroup(subgroup);
			currentHouse.updateSubGroupPositions();
			
			removeIfEmpty(currentHouse);
		}
		
		newHouse.addSubGroup(subgroup);
		newHouse.updateSubGroupPositions();
		
		State.getInstance().setSelectedHouse(newHouse);
		
		// verify house status on the canvas (merge/move if necessary)
		placeHouse(newHouse);
		
		this.repaint();
	}
	
	/**
	 * Performs a drop of the given house at its current location.
	 * 
	 * Specifically,
	 * Checks if it is over the trash can (if it is, it is removed).
	 * Places the house on the canvas (@see {@link #placeHouse(House)})
	 * 
	 * @param house
	 */
	public void dropHouse(House house) {
		if(tryRemove(house)) { // if the house is over the trash, remove them
			repaint();
			return;
		}
		
		placeHouse(house);
	}
	
	/**
	 * Establishes the location/status of a house on the canvas.
	 * 
	 * Specifically,
	 * Checks if the house is over the sidebar (if it is, moves it off).
	 * Checks if it is intersecting any house (if it is, they are merged).
	 * 
	 * @param house
	 */
	protected void placeHouse(House house) {
		if(house.getPosition().x < Constants.SIDEBAR_WIDTH) {
			house.setPosition(Constants.SIDEBAR_WIDTH, house.getPosition().y);
			house.updateSubGroupPositions();
		}
		
		for(House h : State.getInstance().getGroup()) {
			if(house != h &&
			   intersecting(house, h)) 
			{ // the houses are intersecting
				// merge them!
				// get all the subgroups in the current house
					//see TODO in mergeSubGroups for why we do this
				java.util.Collection<SubGroup> tmp = new java.util.LinkedList<SubGroup>();
				for(SubGroup s : house) {
					tmp.add(s);
				}
				
				// add them to the new house
				for(SubGroup s : tmp) {
					h.addSubGroup(s); // this also removes them from the current house
				}
				
				// add the old house's location preferences the new house
				// (the new house's location preferences are the union of the two old ones)
				LocationPreference newHousePreference = h.getLocationPreference();
				for(Dorm d : house.getLocationPreference()) {
					newHousePreference.add(d);
				}
				
				// remove the dropped house
				removeIfEmpty(house);
				
				// update the new house
				h.updateSubGroupPositions();
				
				State.getInstance().setSelectedHouse(h);
				
				this.repaint();
				
				return;
			}
		}
	}
	
	/**
	 * Merges subgroup a into subgroup b.
	 * 
	 * @param a
	 * @param b
	 */
	private void mergeSubGroups(SubGroup a, SubGroup b) {
		// move over the people from this subgroup to the new one
		/* unfortunately, we can't just do:
		 * 	for(Person p : a) { b.addPerson(p); }
		 * because this creates a ConcurrentModificationException.
		 * So the workaround is to copy the people to a temporary container,
		 * and delete from there.
		 * This is hacky. It would be nice to avoid this. (TODO)
		 */
		java.util.Collection<Person> tmp = new java.util.LinkedList<Person>();
		for(Person p : a) {
			tmp.add(p);
		}
		
		for(Person p : tmp) {
			b.addPerson(p);
			// the addPerson code also removes the current person from his/her current subgroup (a)
		}
		
		// remove the old subgroup from its current house
		removeIfEmpty(a);
	}
	
	/**
	 * Removes the given SubGroup from its House, and from view, if it is empty.
	 * If the container House becomes empty, it is removed as well.
	 * 
	 * @param subgroup
	 */
	private void removeIfEmpty(SubGroup subgroup) {
		if(subgroup.isEmpty()) {
			this.remove(subgroup); // remove from view
			
			House currentHouse = subgroup.getHouse();
			if(currentHouse != null) {
				currentHouse.removeSubGroup(subgroup); // remove from house
				
				removeIfEmpty(currentHouse);
			}
		}
	}
	
	/**
	 * Removes the given House from view and from State if it is empty.
	 * 
	 * @param house
	 */
	private void removeIfEmpty(House house) {
		if(house.isEmpty()) {
			this.remove(house); // remove from view
			State.getInstance().getGroup().remove(house); // remove from group
			
			// make sure the house is not listed as selected anymore
			if(State.getInstance().getSelectedHouse() == house) {
				State.getInstance().setSelectedHouse(null);
			}
		}
	}
	
	@Deprecated
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
	 * Removes a person from the program if he/she is over the trash can.
	 * 
	 * @param p the person to (possibly) be removed
	 * @return true if the person was removed, false if the person was not removed
	 */
	private boolean tryRemove(Person p) {
		if(overTrashIcon(p)) {
			SubGroup currentParent = p.getSubGroup();
			
			// remove from view
			this.remove(p);
			
			// remove from subgroup
			if(currentParent != null) {
				currentParent.removePerson(p);
				currentParent.updatePeoplePositions();
				removeIfEmpty(currentParent);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a subgroup from the program if it is over the trash can.
	 * 
	 * @param s the subgroup to (possibly) be removed
	 * @return true if the subgroup was removed, false if the subgroup was not removed
	 */
	private boolean tryRemove(SubGroup s) {
		if(overTrashIcon(s)) {
			// remove the people from this subgroup
			Iterator<Person> it = s.iterator();
			while(it.hasNext()) {
				Person currentPerson = it.next();
				currentPerson.setSubGroup(null);
				this.remove(currentPerson);
				it.remove();
			}
			
			// remove from view
			this.remove(s);
			
			// remove from house
			removeIfEmpty(s);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a house from the program if it is over the trash can.
	 * 
	 * @param h the house to (possibly) be removed
	 * @return true if the house was removed, false if the house was not removed
	 */
	private boolean tryRemove(House h) {
		if(overTrashIcon(h)) {
			// remove the subgroups from this house
			Iterator<SubGroup> it = h.iterator();
			while(it.hasNext()) {
				SubGroup currentSubGroup = it.next();
				
				// remove the people from this subgroup
				Iterator<Person> it2 = currentSubGroup.iterator();
				while(it2.hasNext()) {
					Person currentPerson = it2.next();
					currentPerson.setSubGroup(null);
					this.remove(currentPerson);
					it2.remove();
				}
				
				currentSubGroup.setHouse(null);
				this.remove(currentSubGroup);
				it.remove();
			}
			
			removeIfEmpty(h);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean overTrashIcon(DraggablePositionableComponent c) {
		return GraphicsSupport.intersectionAreaFraction(c.getRectangle(), getTrashRectangle()) 
		> Constants.INTERSECTION_FRACTION;
	}
	
	/**
	 * Returns a Rectangle mask for the trash image.
	 * 
	 * @return
	 */
	private static Rectangle getTrashRectangle() {
		return new Rectangle(
				Constants.TRASH_X_POSITION, 
				Constants.TRASH_Y_POSITION, 
				Constants.TRASH_WIDTH, 
				Constants.TRASH_HEIGHT);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// sidebar
		g.setColor(Color.BLACK);
		g.drawLine(Constants.SIDEBAR_WIDTH, 0, Constants.SIDEBAR_WIDTH, this.getHeight());
		g.setColor(Constants.SIDEBAR_COLOR);
		g.fillRect(0, 0, Constants.SIDEBAR_WIDTH, this.getHeight());
		
		
		// new guy
		String text = "New Guy";
		int textWidth = g.getFontMetrics().stringWidth(text);
		int textPosition = (0 + Constants.SIDEBAR_WIDTH) / 2 - textWidth / 2; 
		g.drawString(text, textPosition, Constants.NEW_MALE_Y_POSITION - 20);
		g.drawImage(Gender.MALE.getImage(), Constants.NEW_MALE_X_POSITION, Constants.NEW_MALE_Y_POSITION, null);
		
		// new girl
		text = "New Girl";
		textWidth = g.getFontMetrics().stringWidth(text);
		textPosition = (0 + Constants.SIDEBAR_WIDTH) / 2 - textWidth / 2;
		g.drawString(text, textPosition, Constants.NEW_FEMALE_Y_POSITION - 20);
		g.drawImage(Gender.FEMALE.getImage(), Constants.NEW_FEMALE_X_POSITION, Constants.NEW_FEMALE_Y_POSITION, null);
		
		// trash can
		g.drawImage(_trashImage, Constants.TRASH_X_POSITION, Constants.TRASH_Y_POSITION, null);
	}
	
	private class AddPersonListener extends MouseAdapter {
		private Person _currentlyDragging = null;
		private java.awt.Point _lastMousePosition = new java.awt.Point(0,0);
		
		/**
		 * Given a position on the Canvas, returns null if the position is not over any "New Person" button;
		 * otherwise, returns the gender of that new person.
		 * 
		 * @param x
		 * @param y
		 * @return
		 */
		private Gender overNewPersonIcon(int x, int y) {
			if(	x >= Constants.NEW_MALE_X_POSITION &&
				x <= Constants.NEW_MALE_X_POSITION + Gender.MALE.getImageDimension().width &&
				y >= Constants.NEW_MALE_Y_POSITION &&
				y <= Constants.NEW_MALE_Y_POSITION + Gender.MALE.getImageDimension().height) 
			{
				return Gender.MALE;
			} else if(
				x >= Constants.NEW_FEMALE_X_POSITION &&
				x <= Constants.NEW_FEMALE_X_POSITION + Gender.FEMALE.getImageDimension().width &&
				y >= Constants.NEW_FEMALE_Y_POSITION &&
				y <= Constants.NEW_FEMALE_Y_POSITION + Gender.FEMALE.getImageDimension().height) 
			{
				return Gender.FEMALE;
			} else {
				return null;
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			Gender newPersonGender = overNewPersonIcon(e.getX(), e.getY());
			if(newPersonGender != null) {
				Person newPerson = new Person("A Person", newPersonGender);
				
				switch(newPersonGender) {
				case MALE:
					newPerson.setPosition(Constants.NEW_MALE_X_POSITION, Constants.NEW_MALE_Y_POSITION);
					break;
				case FEMALE:
					newPerson.setPosition(Constants.NEW_FEMALE_X_POSITION, Constants.NEW_FEMALE_Y_POSITION);
					break;
				}
				
				add(newPerson, Constants.PERSON_LAYER);
				
				_currentlyDragging = newPerson;
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(_currentlyDragging != null) {
				dropPerson(_currentlyDragging);
				_currentlyDragging = null;
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(_currentlyDragging != null) {
				int xOffset = e.getX() - _lastMousePosition.x;
				int yOffset = e.getY() - _lastMousePosition.y;
				
				_currentlyDragging.moveBy(xOffset, yOffset);
			}
			
			_lastMousePosition.x = e.getX();
			_lastMousePosition.y = e.getY();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			_lastMousePosition.x = e.getX();
			_lastMousePosition.y = e.getY();
		}
	}
}
