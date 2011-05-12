

import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class FontLoader {
    public static Font load(String path) {
        /*
         * Font font = null;
         * 
         * try { InputStream is = FontLoader.class.getResourceAsStream(path);
         * font = Font.createFont(Font.TRUETYPE_FONT, is); } catch (Exception
         * ex) { ex.printStackTrace();
         * System.err.println("font not loaded. Using serif font."); font = new
         * Font("serif", Font.PLAIN, 24); } return font;
         */

        Font font = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(path));
            font = Font.createFont(Font.TRUETYPE_FONT, is);
            // ttfReal = ttfBase.deriveFont(Font.PLAIN, 24);
        } catch (Exception ex) {
            System.err.println("font could not be loaded. using default font.");
            font = new Font("serif", Font.PLAIN, 12);
        }

        return font;
    }
}
