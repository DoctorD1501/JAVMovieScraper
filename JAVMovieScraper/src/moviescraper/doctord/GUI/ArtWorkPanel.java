package moviescraper.doctord.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import moviescraper.doctord.Movie;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

	public void updateView(boolean forceUpdatePoster, GUIMain gui) {
		boolean posterFileUpdateOccured = false;
		boolean fanartFileUpdateOccured = false;
		if(!forceUpdatePoster)
		{
			// try to get the poster from a local file, if it exists
			//Maybe there is a file in the directory just called folder.jpg
			File potentialOtherPosterJpg = new File(Movie.getFileNameOfPoster(gui.getCurrentlySelectedMovieFileList().get(0), true));
			File potentialOtherFanartJpg = new File(Movie.getFileNameOfFanart(gui.getCurrentlySelectedMovieFileList().get(0), true));
			//the poster would be called moviename-poster.jpg
			File standardPosterJpg = new File(Movie.getFileNameOfPoster(gui.getCurrentlySelectedMovieFileList().get(0), false));
			File standardFanartJpg = new File(Movie.getFileNameOfFanart(gui.getCurrentlySelectedMovieFileList().get(0), false));
			if (gui.getCurrentlySelectedPosterFileList().get(0).exists()) {
				try {
					BufferedImage img = ImageIO.read(gui.getCurrentlySelectedPosterFileList().get(0));
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						this.setNewPoster(scaledImage);
						posterFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(gui.getCurrentlySelectedPosterFileList().get(0).exists())
			{
				try {
					//System.out.println("found the standard fanart");
					BufferedImage img = ImageIO.read(gui.getCurrentlySelectedFanartFileList().get(0));
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage);
						fanartFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//well we didn't find a poster file we were expecting, try to see if there is any file named poster.jpg in there
			if(gui.getCurrentlySelectedMovieFileList().get(0).isDirectory() && potentialOtherPosterJpg.exists() && !posterFileUpdateOccured)
			{
				try {
					//System.out.println("Reading in poster from other" + potentialOtherPosterJpg);
					BufferedImage img = ImageIO.read(potentialOtherPosterJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						this.setNewPoster(scaledImage);
						posterFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//well we didn't find a poster file we were expecting, try to see if there is any file named fanart.jpg in there
			if(gui.getCurrentlySelectedMovieFileList().get(0).isDirectory() && potentialOtherFanartJpg.exists() && !fanartFileUpdateOccured)
			{
				try {
					//System.out.println("Reading in fanart from other" + potentialOtherFanartJpg);
					BufferedImage img = ImageIO.read(potentialOtherFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage);
						fanartFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//just in case, also check to see if one called moviename-poster.jpg is there, even if we were expecting a poster.jpg due to the preference we set
			if(standardPosterJpg.exists() && !posterFileUpdateOccured)
			{
				try {
					//System.out.println("Reading in poster from moviename-poster" + potentialOtherPosterJpg);
					this.setNewPoster(new ImageIcon(
							standardPosterJpg.getCanonicalPath()));
					posterFileUpdateOccured = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//just in case, also check to see if one called moviename-fanart.jpg is there, even if we were expecting a poster.jpg due to the preference we set
			if(standardFanartJpg.exists() && !fanartFileUpdateOccured)
			{
				try {
					//System.out.println("Reading in fanart from moviename-fanart" + potentialOtherPosterJpg);
					BufferedImage img = ImageIO.read(standardFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage);
						fanartFileUpdateOccured = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// otherwise read it from the URL specified by the object since we couldn't find any local file
		if (gui.movieToWriteToDiskList.size() > 0 && gui.movieToWriteToDiskList.get(0) != null && gui.movieToWriteToDiskList.get(0).hasPoster() && !posterFileUpdateOccured) {
			try {
				Image posterImage = gui.movieToWriteToDiskList.get(0).getPosters()[0]
						.getThumbImage();
				ImageIcon newPosterIcon = new ImageIcon(posterImage);
				BufferedImage img = (BufferedImage) newPosterIcon.getImage();
				BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
				this.setNewPoster(scaledImage);
				posterFileUpdateOccured = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}

		//try to read the fanart from the url since we couldn't find any local file
		if (gui.movieToWriteToDiskList.size() > 0 && gui.movieToWriteToDiskList.get(0) != null && gui.movieToWriteToDiskList.get(0).hasFanart() && !fanartFileUpdateOccured) {
			System.out.println("Reading in the fanart from the url");
			try {
				Image fanartImage = gui.movieToWriteToDiskList.get(0).getFanart()[0]
						.getThumbImage();
				ImageIcon newFanartIcon = new ImageIcon(fanartImage);
				BufferedImage img = (BufferedImage) newFanartIcon.getImage();
				BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
				this.setNewFanart(scaledImage);
				fanartImage = scaledImage;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

		
	}


