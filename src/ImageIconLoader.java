import javax.swing.ImageIcon;
import java.net.URL;

public class ImageIconLoader {
	
	private static ImageIconLoader _INSTANCE = new ImageIconLoader();
	
	private ImageIconLoader() {}
	
	public static ImageIconLoader getInstance() {
		return _INSTANCE;
	}
	
	public ImageIcon createImageIcon(String path, String description) {
		// TODO fix this after package heirarchy is decided
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}
	
}
