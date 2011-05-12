

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReader {
    BufferedReader in;

    public CSVReader(String file) {
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("file " + file + " not found");
        }
    }

    public ArrayList<String[]> read() {
        ArrayList<String[]> rooms = new ArrayList<String[]>();
        try {
            String line = in.readLine();
            String semester = "";

            while (line != null) {
                if (line.substring(0, 2).equals("\"(")) {
                    semester = line.replace("\"(Beginning of Semester Level ",
                            "");
                    semester = semester.replace(" Groups)\",,,,", "");
                } else if (!line.substring(0, 1).equals("\"")
                        && !line.substring(0, 1).equals(",")) {
                    line = line.replaceAll("\"", "");
                    String[] params = line.split(",");

                    String[] room = new String[5];

                    room[0] = params[0].trim();
                    room[1] = params[1].trim();

                    if (!params[1].contains("No Show")
                            && !params[1].contains("Pass")
                            && !params[1].contains("Drop")) {
                        if (room[1].contains("Hegeman")) {
                            room[1] += " " + params[2].substring(0, 1);
                            params[2] = params[2].substring(1, params[2]
                                    .length());
                        }

                        room[2] = params[2].trim();
                        room[3] = params[3].trim();
                    }

                    room[4] = semester;

                    rooms.add(room);
                }

                line = in.readLine();
            }
        } catch (IOException e) {
            System.out.println("ERROR: error reading file");
        }

        return rooms;
    }

}
