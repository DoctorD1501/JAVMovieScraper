package moviescraper.doctord.view;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * 
 * Common utility methods used in the various GUI classes. Methods should be static.
 *
 */
public class GUICommon {

	public static Image getProgramIcon() {
		//initialize the icons used in the program
		URL programIconURL = GUICommon.class.getResource("/res/AppIcon.png");

		//Used for icon in the title bar
		Image programIcon = null;
		try {
			programIcon = ImageIO.read(programIconURL);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return programIcon;
	}

}
