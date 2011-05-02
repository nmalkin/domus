import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class Canvas extends JLayeredPane {
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
		
		/*
		 * NOTE:
		 * For some reason I have yet to understand,
		 * the order in which you add components to the JPanel matters.
		 * If you don't add them in the order [Person, SubGroup, House],
		 * then the lower-level components won't be individually draggable.
		 */
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
			dropSubGroup(newSubGroup);
			
			// display it
			this.add(newSubGroup, Constants.SUBGROUP_LAYER);
		} else {
			// add the person to the (already-existing) subgroup
			newSubGroup.addPerson(person);
		}
		
		newSubGroup.updatePeoplePositions();
		
		State.getInstance().setSelectedHouse(newSubGroup.getHouse());
		
		this.repaint();
	}
	
	/**
	 * Performs a drop of the given subgroup at the given location.
	 * 
	 * @param subgroup
	 */
	public void dropSubGroup(SubGroup subgroup) {
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
			
			// place the new house wherever the subgroup is right now
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
			
			if(currentHouse.isEmpty()) {
				this.remove(currentHouse); // remove from view
				State.getInstance().getGroup().remove(currentHouse); // remove from group
			}
		}
		
		newHouse.addSubGroup(subgroup);
		newHouse.updateSubGroupPositions();
		
		State.getInstance().setSelectedHouse(newHouse);
		
		this.repaint();
	}
	
	/**
	 * Performs a drop of the given house at the given location
	 * by checking if it is intersecting with any houses.
	 * 
	 * @param house
	 */
	public void dropHouse(House house) {
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
				
				// remove the dropped house
				this.remove(house); // remove from view
				State.getInstance().getGroup().remove(house); // remove from group
				
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
	 * @param currentSubGroup
	 */
	private void removeIfEmpty(SubGroup currentSubGroup) {
		if(currentSubGroup.isEmpty()) {
			this.remove(currentSubGroup); // remove from view
			
			House currentHouse = currentSubGroup.getHouse();
			if(currentHouse != null) {
				currentHouse.removeSubGroup(currentSubGroup); // remove from house
				
				if(currentHouse.isEmpty()) {
					this.remove(currentHouse); // remove from view
					State.getInstance().getGroup().remove(currentHouse); // remove from group
				}	
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// separator
		g.drawLine(Constants.SEPARATOR_X_POSITION, 0, Constants.SEPARATOR_X_POSITION, this.getHeight());
		
		// new guy
		String text = "New Guy";
		int textWidth = g.getFontMetrics().stringWidth(text);
		int textPosition = (0 + Constants.SEPARATOR_X_POSITION) / 2 - textWidth / 2; 
		g.drawString(text, textPosition, Constants.NEW_MALE_Y_POSITION - 20);
		g.drawImage(Gender.MALE.getImage(), Constants.NEW_MALE_X_POSITION, Constants.NEW_MALE_Y_POSITION, null);
		
		text = "New Girl";
		textWidth = g.getFontMetrics().stringWidth(text);
		textPosition = (0 + Constants.SEPARATOR_X_POSITION) / 2 - textWidth / 2;
		g.drawString(text, textPosition, Constants.NEW_FEMALE_Y_POSITION - 20);
		g.drawImage(Gender.FEMALE.getImage(), Constants.NEW_FEMALE_X_POSITION, Constants.NEW_FEMALE_Y_POSITION, null);
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
