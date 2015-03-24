package moviescraper.doctord.GUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import moviescraper.doctord.Movie;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;



public class ArtWorkPanel extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 9061046066424044803L;

	private ArtWorkPanel artworkPanel;

	private JLabel lblPosterIcon;
	private JLabel lblFanartIcon;

	private Image posterImage;
	private Image fanartImage;

	//initial max sizes = values can change when window is resized
	private static int maximumPosterSizeX = 379;
	private static int maximumPosterSizeY = 500;
	private static int maximumFanartSizeX = 379;
	private static int maximumFanartSizeY = 135;
	private Dimension oldSize;

	private boolean updatingPosterAndFanartSizes = false;




	public ArtWorkPanel() {
		artworkPanel = this;
		//artworkPanel.setMinimumSize(new Dimension(379,675));
		artworkPanel.setLayout(new BoxLayout(artworkPanel, BoxLayout.Y_AXIS));
		artworkPanel.addComponentListener(this);

		//set up the poster
		lblPosterIcon = new JLabel("");

		//Dimension posterSize = new Dimension(posterSizeX, posterSizeY);
		// posterImage is initially a transparent poster size rectangle

		posterImage = createEmptyImage(maximumPosterSizeX, maximumPosterSizeY);
		ImageIcon posterIcon = new ImageIcon(posterImage);
		lblPosterIcon.setIcon(posterIcon);
		lblPosterIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPosterIcon.setAlignmentY(Component.CENTER_ALIGNMENT);

		//set up the fanart
		lblFanartIcon = new JLabel("");
		// fanartImage is initially a transparent rectangle
		fanartImage = createEmptyImage(maximumFanartSizeX, maximumFanartSizeY);
		ImageIcon fanartIcon = new ImageIcon(fanartImage);
		lblFanartIcon.setIcon(fanartIcon);
		lblFanartIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblFanartIcon.setAlignmentY(Component.BOTTOM_ALIGNMENT);


		artworkPanel.add(lblPosterIcon);
		//add a little bit of space between the poster and the fanart
		artworkPanel.add(Box.createRigidArea(new Dimension(0,5)));
		artworkPanel.add(lblFanartIcon);

		oldSize = artworkPanel.getBounds().getSize();
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
		lblFanartIcon.setIcon( new ImageIcon(createEmptyImage(maximumFanartSizeX, maximumFanartSizeY)) );
	}

	public void clearPoster() {
		lblPosterIcon.setIcon( new ImageIcon(createEmptyImage(maximumPosterSizeX, maximumPosterSizeY)) );
	}

	public void setNewFanart(Image fanart, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			fanartImage = fanart;
		}
		lblFanartIcon.setIcon(new ImageIcon(fanart));
	}

	public void setNewFanart(ImageIcon fanart, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			fanartImage = fanart.getImage();
		}
		lblFanartIcon.setIcon(fanart);
	}

	public void setNewPoster(Image poster, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			posterImage = poster;
		}
		lblPosterIcon.setIcon(new ImageIcon(poster));
	}

	public void setNewPoster(ImageIcon poster, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			posterImage = poster.getImage();
		}
		lblPosterIcon.setIcon(poster);
	}

	private void updatePosterAndFanartSizes(){
		updatingPosterAndFanartSizes = true;
		BufferedImage fanartImg = (BufferedImage)(fanartImage);
		BufferedImage fanartScaledImage = ArtWorkPanel.resizeToFanart(fanartImg);
		this.setNewFanart(fanartScaledImage, false);

		BufferedImage posterImg = (BufferedImage)(posterImage);
		BufferedImage posterScaledImage = ArtWorkPanel.resizeToPoster(posterImg);
		this.setNewPoster(posterScaledImage, false);

	}

	public static BufferedImage resizeToFanart(BufferedImage image) {
		Dimension dimensionFit = calculateDimensionFit(image.getWidth(), image.getHeight(), 
				maximumFanartSizeX, maximumFanartSizeY);
		return resizePicture(image, dimensionFit.width, dimensionFit.height);
	}

	/**
	 * Calculate the max size we can resize an image while fitting within maxWidth and maxHeight
	 * and still maintaining the aspect ratio
	 * @param imageWidth - the width of the image to resize
	 * @param imageHeight - the height of the image to resize
	 * @param maxWidth - the maximum width the image can be
	 * @param maxHeight - the maximum height the image can be
	 * @return A Dimension object with the calculated width and heights set on it
	 */
	private static Dimension calculateDimensionFit(int imageWidth, int imageHeight, 
			int maxWidth, int maxHeight)
	{
		double aspectRatio = Math.min((double) maxWidth / (double) imageWidth,
				(double) maxHeight / (double) imageHeight);
		return new Dimension((int)(imageWidth * aspectRatio), (int)(imageHeight * aspectRatio));
	}

	public static BufferedImage resizeToPoster(BufferedImage image) {
		Dimension dimensionFit = calculateDimensionFit(image.getWidth(), image.getHeight(), 
				maximumPosterSizeX, maximumPosterSizeY);
		return resizePicture(image, dimensionFit.width, dimensionFit.height);
	}

	public static BufferedImage resizePicture(BufferedImage image, int newWidth, int newHeight) {
		return Scalr.resize(image, Method.QUALITY, newWidth, newHeight, Scalr.OP_ANTIALIAS);
	}

	public void updateView(boolean forceUpdatePoster, GUIMain gui) {
		boolean posterFileUpdateOccured = false;
		boolean fanartFileUpdateOccured = false;
		if(!forceUpdatePoster && gui.getCurrentlySelectedMovieFileList().size() > 0)
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
						//we're doing a resize so store off the original img so if we resize again we don't lose quality
						posterImage = img;
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						this.setNewPoster(scaledImage, false);
						posterFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(gui.getCurrentlySelectedFanartFileList().get(0).exists())
			{
				try {
					BufferedImage img = ImageIO.read(gui.getCurrentlySelectedFanartFileList().get(0));
					if(img != null)
					{
						//we're doing a resize so store off the original img so if we resize again we don't lose quality
						fanartImage = img;
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage, false);
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
					BufferedImage img = ImageIO.read(potentialOtherPosterJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						this.setNewPoster(scaledImage, true);
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
					BufferedImage img = ImageIO.read(potentialOtherFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage, true);
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
					this.setNewPoster(new ImageIcon(
							standardPosterJpg.getCanonicalPath()), true);
					posterFileUpdateOccured = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//just in case, also check to see if one called moviename-fanart.jpg is there, even if we were expecting a poster.jpg due to the preference we set
			if(standardFanartJpg.exists() && !fanartFileUpdateOccured)
			{
				try {
					BufferedImage img = ImageIO.read(standardFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage, true);
						fanartFileUpdateOccured = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// otherwise read it from the URL specified by the object since we couldn't find any local file
		if (gui.movieToWriteToDiskList.size() > 0 && gui.movieToWriteToDiskList.get(0) != null && gui.movieToWriteToDiskList.get(0).hasPoster() && !posterFileUpdateOccured) {
			try {
				if(gui.getFileDetailPanel().currentMovie.getPosters().length > 0 )
				{
					Image posterImage = gui.getFileDetailPanel().currentMovie.getPosters()[0]
							.getThumbImage();
					ImageIcon newPosterIcon = new ImageIcon(posterImage);
					BufferedImage img = (BufferedImage) newPosterIcon.getImage();
					BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
					this.setNewPoster(scaledImage, true);
					posterFileUpdateOccured = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}

		//try to read the fanart from the url since we couldn't find any local file
		if (gui.movieToWriteToDiskList.size() > 0 && gui.movieToWriteToDiskList.get(0) != null && gui.movieToWriteToDiskList.get(0).hasFanart() && !fanartFileUpdateOccured) {
			try {
				if(gui.getFileDetailPanel().currentMovie.getFanart().length > 0)
				{
					Image fanartImage = gui.getFileDetailPanel().currentMovie.getFanart()[0]
							.getThumbImage();
					ImageIcon newFanartIcon = new ImageIcon(fanartImage);
					BufferedImage img = (BufferedImage) newFanartIcon.getImage();
					BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
					this.setNewFanart(scaledImage, true);
					fanartImage = scaledImage;
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentResized(ComponentEvent e) {

		Dimension newSize = e.getComponent().getBounds().getSize();
		double oldSizeToNewSizeScaleWidth = (double) newSize.width / (double) oldSize.width; 
		//double oldSizeToNewSizeScaleHeight = (double) newSize.height / (double) oldSize.height;
		if(oldSize.width != 0 && oldSize.height != 0 && !updatingPosterAndFanartSizes )
		{
			maximumPosterSizeX *= oldSizeToNewSizeScaleWidth;
			//maximumPosterSizeY *= oldSizeToNewSizeScaleHeight;
			maximumFanartSizeX = maximumPosterSizeX;
			maximumFanartSizeY = newSize.height - maximumPosterSizeY - 25;

			updatePosterAndFanartSizes();
		}
		oldSize = newSize;
		updatingPosterAndFanartSizes = false;
	}


	@Override
	public void componentShown(ComponentEvent e) {}


}

