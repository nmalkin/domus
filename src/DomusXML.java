import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * Class for reading and writing from the Domus XML file format.
 * 
 * @author nmalkin
 *
 */
public class DomusXML {
	public static void writeXML(String filename) throws IOException {
		Document document = makeXMLDocument();
		
		FileWriter outStream = new FileWriter(filename); // set up the output stream
		XMLWriter fileWriter = new XMLWriter(outStream, OutputFormat.createPrettyPrint()); // output using "pretty" formatting
		fileWriter.write(document); // write!
		outStream.flush();
	}
	
	public static Document makeXMLDocument() {
		Document document = DocumentHelper.createDocument();
		
		Element root = DocumentHelper.createElement("domus");
		document.setRootElement(root);
		root.addAttribute("version", Constants.FILE_FORMAT_VERSION + "");
		
		Element input = DocumentHelper.createElement("input");
		root.add(input);
		
		Element group = DocumentHelper.createElement("group");
		input.add(group);
		
		group.addAttribute("lottery_number", State.getInstance().getGroup().getLotteryNumber() + "");
		
		Element house;
		for(House h : State.getInstance().getGroup()) {
			house = DocumentHelper.createElement("house");
			group.add(house);
			
			Element subgroups = DocumentHelper.createElement("subgroups");
			house.add(subgroups);
			
			Element subgroup;
			for(SubGroup s : h) {
				subgroup = DocumentHelper.createElement("subgroup");
				subgroups.add(subgroup);
				
				Element person;
				for(Person p : s) {
					person = DocumentHelper.createElement("person");
					subgroup.add(person);
					
					person.addAttribute("name", p.getName());
					
					String gender = "";
					switch(p.getGender()) {
					case MALE:
						gender = "male";
						break;
					case FEMALE:
						gender = "female";
						break;
					}
					person.addAttribute("gender", gender);
				}
			}
			
			Element preferences = DocumentHelper.createElement("preferences");
			house.add(preferences);
			
			Element buildings = DocumentHelper.createElement("buildings");
			preferences.add(buildings);
			
			Element building;
			for(Dorm d : h.getLocationPreference()) {
				building = DocumentHelper.createElement("building");
				buildings.add(building);
				
				building.addAttribute("name", d.getName());
			}
		}
		
		Element choices = DocumentHelper.createElement("choices");
		root.add(choices);
		
		Element list;
		for(RoomList l : State.getInstance().getRoomLists()) {
			list = DocumentHelper.createElement("list");
			choices.add(list);
			
			list.addAttribute("name", l.getName());
			list.addAttribute("color", l.getColor().getRGB() + "");
			
			Element room;
			for(Room r : l) {
				room = DocumentHelper.createElement("room");
				list.add(room);
				
				room.addAttribute("building", r.getDorm().getName());
				room.addAttribute("number", r.getNumber());
				//room.addAttribute("order", "?");//TODO: how do we get this?
			}
		}
		
		return document;
	}
}
