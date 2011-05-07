import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.*;
import org.dom4j.io.*;

/**
 * Class for reading and writing from the Domus XML file format.
 * 
 * @author nmalkin
 *
 */
public class DomusXML {
	/**
	 * Given a filename, writes an XML file representing the State to this file.
	 * 
	 * @param filename
	 * @throws IOException if there was a problem writing to the file
	 */
	public static void writeXML(File file) throws IOException {
		Document document = makeXMLDocument();
		
		writeDocument(document, file);
	}
	
	/**
	 * Given a filename, writes an HTML document, representing a transform of the State XML file, to this file.
	 * 
	 * @param filename
	 * @throws IOException if there was a problem writing to the file
	 * @throws TransformerException if there was a problem applying the XSL transformation
	 */
	public static void writeHTML(File file) throws IOException, TransformerException {
		Document document = makeXMLDocument();
		Document htmlDocument = styleDocument(document, Constants.XML_TRANFORM_FILE);
		
		writeDocument(htmlDocument, file);
	}
	
	/**
	 * Given a document and a file, write the document to the file.
	 * 
	 * @param document
	 * @param file
	 * @throws IOException if there was a problem writing to the file
	 */
	private static void writeDocument(Document document, File file) throws IOException {
		FileWriter outStream = new FileWriter(file); // set up the output stream
		XMLWriter fileWriter = new XMLWriter(outStream, OutputFormat.createPrettyPrint()); // output using "pretty" formatting
		fileWriter.write(document); // write!
		outStream.flush();
	}
	
	/**
	 * Creates an XML document representing the State.
	 * 
	 * @return
	 */
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
			
			house.addAttribute("x", h.getPosition().x + "");
			house.addAttribute("y", h.getPosition().y + "");
			
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
	
	/**
	 * Given a filename, reads in the provided XML document, and copies the settings into State.
	 * 
	 * @param filename
	 * @throws IOException if there was a problem reading from file
	 * @throws DocumentException if there was a problem with the structure of the document
	 */
	public static void readXML(File file) throws IOException, DocumentException {
		// to prevent problematic inputs from corrupting the State,
		// we will create a new Group and new RoomLists for the data we're reading in,
		// and then copy them to State at the end, when we've ascertained the lack of issues
		Group newGroup = new Group();
		java.util.List<RoomList> lists = new LinkedList<RoomList>();
		
		// read document
		SAXReader reader = new SAXReader();
		Document document = reader.read(new FileReader(file));
		
		// parse document
		Element root = document.getRootElement();
		if(root.getQName().getName() != "domus") {
			throw new DocumentException("root element expected to be \"domus\"");
		} else if(getIntAttribute(root, "version") != 1) {
			throw new DocumentException("unrecognized file format version number");
		}
		
		try {
			Element input = root.element("input");
			Element group = input.element("group");
			
			for(Object o : group.elements("house")) {
				Element house = (Element) o;
				
				House newHouse = new House();
				
				// get constituents
				Element subgroups = house.element("subgroups");
				for(Object o2 : subgroups.elements("subgroup")) {
					Element subgroup = (Element) o2;
					
					SubGroup newSubGroup = new SubGroup();
					
					for(Object o3 : subgroup.elements("person")) {
						Element person = (Element) o3;
						
						String name = person.attributeValue("name");
						
						String gender = person.attributeValue("gender");
						Gender g;
						if(gender.equals("male")) {
							g = Gender.MALE;
						} else if(gender.equals("female")) {
							g = Gender.FEMALE;
						} else {
							throw new DocumentException("unrecognized string for gender");
						}
						
						Person newPerson = new Person(name, g);
						newSubGroup.addPerson(newPerson);
					}
					
					newHouse.addSubGroup(newSubGroup);
				}
				
				// get location preferences
				Element preferences = house.element("preferences");
				LocationPreference lp = new LocationPreference();
				
				Element buildings = preferences.element("buildings");
				for(Object o4 : buildings.elements("building")) {
					Element building = (Element) o4;
					
					Dorm dorm = Database.getDorm(building.attributeValue("name"));
					if(dorm == null) {
						throw new DocumentException("unrecognized dorm name");
					}
					
					lp.add(dorm);
				}
				
				newHouse.setLocationPreference(lp);
				
				// get position
				int x = getIntAttribute(house, "x");
				int y = getIntAttribute(house, "y");
				newHouse.setPosition(x, y);
				newHouse.updateSubGroupPositions();
				
				newGroup.add(newHouse);
			}
			
			Element choices = root.element("choices");
			for(Object o5 : choices.elements("list")) {
				Element list = (Element) o5;
				
				String name = list.attributeValue("name");
				int color = getIntAttribute(list, "color");
				
				RoomList rl = new RoomList(name);
				rl.setColor(new java.awt.Color(color));
				
				for(Object o6 : list.elements("room")) {
					Element room = (Element) o6;
					
					Dorm dorm = Database.getDorm(room.attributeValue("building"));
					if(dorm == null) {
						throw new DocumentException("unrecognized dorm name");
					}
					
					String roomNumber = room.attributeValue("number");
					
					Room newRoom = Room.getRoom(dorm, roomNumber);
					rl.add(newRoom);
					newRoom.addToRoomList(rl); //TODO: is it really necessary to call both?
				}
				
				lists.add(rl);
			}
		} catch(NullPointerException e) {
			e.printStackTrace();
			throw new DocumentException("missing required attribute");
			// but which one? it would be nice to know. (TODO)
		}
		
		State.getInstance().setGroup(newGroup);
		for(RoomList rl : lists) {
			State.getInstance().addRoomList(rl);
		}
	}
	
	/**
	 * Returns the int value of the attribute of the Element identified by the given name.
	 *  
	 * @param element the element whose attribute you're looking for
	 * @param attributeName the name of the desired attribute
	 * @return the int contained in the attribute
	 * @throws DocumentException if the input is not a valid int
	 */
	protected static int getIntAttribute(Element element, String attributeName) throws DocumentException {
		if(element == null) throw new IllegalArgumentException("null is not a valid element");
		
		Attribute attribute = element.attribute(attributeName);
		
		if(attribute == null) { // non-existing attribute
			throw new DocumentException("element " + element.getName() + " missing attribute " + attributeName);
		}
		
		int i;
		try {
			i = Integer.parseInt(attribute.getStringValue());
		} catch(NumberFormatException e) {
			// oh no! it's not a double!
			throw new DocumentException("expected attribute " + attributeName + " to be a number. it wasn't");
		}
		return i;
	}
	
	/**
	 * Given a dom4j Document, styles it according to the XSL stylesheet in the specified file.
	 * 
	 * Code directly from http://dom4j.sourceforge.net/dom4j-1.6.1/guide.html
	 * ("Styling a Document with XSLT")
	 * 
	 * @param document
	 * @param stylesheet
	 * @return
	 * @throws TransformerException
	 */
	private static Document styleDocument(Document document, String stylesheet) throws TransformerException {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer( 
            new StreamSource( stylesheet ) 
        );

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();
        transformer.transform( source, result );

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
}
