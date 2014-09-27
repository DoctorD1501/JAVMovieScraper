package moviescraper.doctord.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class ArtWorkPanel extends JPanel {

	private static final long serialVersionUID = 9061046066424044803L;
	
	private ArtWorkPanel artworkPanel;

	private Image posterImage;
	private Image fanartImage;
	private JLabel lblPosterIcon;
	private JLabel lblFanartIcon;
	
	private static final int posterSizeX = 379;
	private static final int posterSizeY = 536;
	private static int fanartSizeX = 85;
	private static int fanartSizeY = 85;
	
	public ArtWorkPanel() {
		artworkPanel = this;
		artworkPanel.setLayout(new BoxLayout(artworkPanel, BoxLayout.PAGE_AXIS));
		artworkPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//set up the poster
		lblPosterIcon = new JLabel("");
		
		//Dimension posterSize = new Dimension(posterSizeX, posterSizeY);
		// posterImage is initially a transparent poster size rectangle
		posterImage = createEmptyImage(posterSizeX, posterSizeY);
		
		ImageIcon posterIcon = new ImageIcon(posterImage);
		lblPosterIcon.setIcon(posterIcon);
		//lblPosterIcon.setSize(posterSize);
		//lblPosterIcon.setMaximumSize(posterSize);
		//lblPosterIcon.setMinimumSize(posterSize);
		//lblPosterIcon.setPreferredSize(posterSize);
		lblPosterIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPosterIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		//set up the fanart
		lblFanartIcon = new JLabel("");
		//Dimension fanartSize = new Dimension(fanartSizeX, fanartSizeY);
		// fanartImage is intiailly a transparent rectangle
		fanartImage = createEmptyImage(fanartSizeX, fanartSizeY);
		
		ImageIcon fanartIcon = new ImageIcon(fanartImage);
		lblFanartIcon.setIcon(fanartIcon);
		//lblFanartIcon.setMaximumSize(fanartSize);
		//lblFanartIcon.setMinimumSize(fanartSize);
		//lblFanartIcon.setPreferredSize(fanartSize);
		lblFanartIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblFanartIcon.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		
		
		artworkPanel.add(lblPosterIcon);
		artworkPanel.add(lblFanartIcon);
	}
	
	private Image createEmptyImage(int x, int y) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(x, y, Transparency.TRANSLUCENT);
	}
	
	public void clearPictures() {
		clearFanart();
		clearPoster();
	}
	
	public void clearFanart() {
		lblFanartIcon.setIcon( new ImageIcon(createEmptyImage(fanartSizeX, fanartSizeY)) );
	}
	
	public void clearPoster() {
		lblPosterIcon.setIcon( new ImageIcon(createEmptyImage(posterSizeX, posterSizeY)) );
	}

	public void setNewFanart(Image fanart) {
		lblFanartIcon.setIcon( new ImageIcon(fanart) );
	}
	
	public void setNewFanart(ImageIcon fanart) {
		lblFanartIcon.setIcon( fanart );
	}

	public void setNewPoster(Image poster) {
		lblPosterIcon.setIcon( new ImageIcon(poster) );
	}
	
	public void setNewPoster(ImageIcon poster) {
		lblPosterIcon.setIcon( poster );
	}
	
	public static BufferedImage resizeToFanart(Image image) {
		return resizeToFanart( (BufferedImage) new ImageIcon(image).getImage() );
	}
	
	public static BufferedImage resizeToFanart(BufferedImage image) {
		return resizePicture(image, fanartSizeX, fanartSizeY);
	}
	
	public static BufferedImage resizeToPoster(Image image) {
		return resizeToPoster( (BufferedImage) new ImageIcon(image).getImage() );
	}
	
	public static BufferedImage resizeToPoster(BufferedImage image) {
		return resizePicture(image, posterSizeX, posterSizeY);
	}
	
	public static BufferedImage resizePicture(BufferedImage image, int newWidth, int newHeigth) {
		return Scalr.resize(image, Method.QUALITY, newWidth, newHeigth, Scalr.OP_ANTIALIAS);
	}

}
