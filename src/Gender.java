

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;

public enum Gender {
    MALE(Constants.MAN_FILE, Constants.MAN_WIDTH, Constants.MAN_HEIGHT), FEMALE(
            Constants.WOMAN_FILE, Constants.WOMAN_WIDTH, Constants.WOMAN_HEIGHT);

    private Image _image;
    private Dimension _dimension;

    Gender(String imageFile, int width, int height) {
        _image = null;

        try {
            _image = javax.imageio.ImageIO.read(getClass().getResource(imageFile));
        } catch (IOException e) {
            // TODO: do anything except silently fail?
        }

        _dimension = new Dimension(width, height);
    }

    /**
     * Returns the image associated with the given gender.
     * 
     * @return the image associated with the given gender.
     * @throws IOException
     *             if the image file could not be opened
     */
    public Image getImage() {
        return _image;
    }

    /**
     * Returns the dimensions of the image associated with the given gender.
     * 
     * @return
     */
    public Dimension getImageDimension() {
        return _dimension;
    }

    public String toString() {
        String r = "";

        switch (this) {
        case MALE:
            r = "M";
            break;
        case FEMALE:
            r = "F";
            break;
        }

        return r;
    }
}
