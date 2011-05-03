import java.util.HashMap;
import java.util.Map;



public class RoomFactory {

	private static Map<String, Room> _roomMap = new HashMap<String, Room>();
	
	public static Room getRoom(Dorm dorm, String roomNumber) {
		String roomName = dorm.getName() + roomNumber;
		Room room = null;
		if ((room = _roomMap.get(roomName)) == null) {
			room = new Room(dorm, roomNumber);
			_roomMap.put(roomName, room);
		}
		return room;
	}
}
