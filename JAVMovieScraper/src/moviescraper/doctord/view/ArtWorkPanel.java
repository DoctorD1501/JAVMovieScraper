package moviescraper.doctord.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.view.CustomComponents.AsyncImageComponent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;



public class ArtWorkPanel extends JPanel implements ComponentListener {




	private static final long serialVersionUID = 9061046066424044803L;

	//private ArtWorkPanel artworkPanel;

	private AsyncImageComponent lblPosterIcon;
	private AsyncImageComponent lblFanartIcon;
	private static final String artworkTooltip = "Double click to change image.";

	private Image posterImage;
	private Image fanartImage;

	//initial max sizes = values can change when window is resized
	private static int maximumPosterSizeX = 400;
	private static int maximumPosterSizeY = 500;
	private static int maximumFanartSizeX = 400;
	private static int maximumFanartSizeY = 135;
	private Dimension oldSize;

	private boolean updatingPosterAndFanartSizes = false;

	private GUIMain guiMain;





	public ArtWorkPanel(GUIMain guiMain) {
		//artworkPanel.setMinimumSize(new Dimension(379,675));
		this.guiMain = guiMain;
		//this.setPreferredSize(new Dimension(maximumPosterSizeX + 100, (maximumPosterSizeY + maximumFanartSizeY) * 2));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addComponentListener(this);

		//set up the poster
		lblPosterIcon = new AsyncImageComponent(null, false, null, false, true, false);
		lblPosterIcon.setPreferredSize(new Dimension(maximumPosterSizeX, maximumPosterSizeY));
		lblPosterIcon.setToolTipText(artworkTooltip);
		

		//Dimension posterSize = new Dimension(posterSizeX, posterSizeY);
		// posterImage is initially a transparent poster size rectangle

		posterImage = createEmptyImage(maximumPosterSizeX, maximumPosterSizeY);
		//ImageIcon posterIcon = new ImageIcon(posterImage);
		//lblPosterIcon.setIcon(posterIcon, posterImage);
		lblPosterIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPosterIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
		lblPosterIcon.addMouseListener(new MouseListenerShowArtPicker(false));

		//set up the fanart
		lblFanartIcon = new AsyncImageComponent(null, false, null, false, true, false);
		lblFanartIcon.setPreferredSize(new Dimension(maximumFanartSizeX, maximumFanartSizeY));
		lblFanartIcon.setToolTipText(artworkTooltip);
		// fanartImage is initially a transparent rectangle
		fanartImage = createEmptyImage(maximumFanartSizeX, maximumFanartSizeY);
		//ImageIcon fanartIcon = new ImageIcon(fanartImage);
		//lblFanartIcon.setIcon(fanartIcon);
		lblFanartIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblFanartIcon.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblFanartIcon.addMouseListener(new MouseListenerShowArtPicker(true));


		add(lblPosterIcon);
		//add a little bit of space between the poster and the fanart
		add(Box.createRigidArea(new Dimension(0,5)));
		add(lblFanartIcon);

		oldSize = getBounds().getSize();
	}

	private Image createEmptyImage(int x, int y) {
		int xToUse = Math.max(x, 1);
		int yToUse = Math.max(y, y);
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(xToUse, yToUse, Transparency.TRANSLUCENT);
	}

	public void clearPictures() {
		clearFanart();
		clearPoster();
	}

	public void clearFanart() {
		//lblFanartIcon.setIcon( new ImageIcon(createEmptyImage(maximumFanartSizeX, maximumFanartSizeY)) );
		lblFanartIcon.clear();
	}

	public void clearPoster() {
		//lblPosterIcon.setIcon( new ImageIcon(createEmptyImage(maximumPosterSizeX, maximumPosterSizeY)) );
		lblPosterIcon.clear();
		//lblPosterIcon.setIcon( null );
	}

	public void setNewFanart(Image fanart, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			fanartImage = fanart;
		}
		//lblFanartIcon.setIcon(new ImageIcon(fanart));
	}

	public void setNewFanart(ImageIcon fanart, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			fanartImage = fanart.getImage();
		}
		//lblFanartIcon.setIcon(fanart);
	}

	public void setNewPoster(Image poster, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			posterImage = poster;
		}
		//lblPosterIcon.setIcon(new ImageIcon(poster));
	}

	public void setNewPoster(ImageIcon poster, boolean updateSourceImages) {
		if(updateSourceImages)
		{
			posterImage = poster.getImage();
		}
		//lblPosterIcon.setIcon(poster);
	}

	private void updatePosterAndFanartSizes(){
		updatingPosterAndFanartSizes = true;
		BufferedImage bufferedFanartImg = Thumb.convertToBufferedImage(fanartImage);
		if(bufferedFanartImg != null)
		{
			//BufferedImage fanartScaledImage = ArtWorkPanel.resizeToFanart(lblFanartIcon.get);
			//this.setNewFanart(fanartScaledImage, false);
			Dimension newSize = calculateDimensionFit(lblFanartIcon.getWidth(), lblFanartIcon.getHeight(), maximumPosterSizeX, maximumFanartSizeY);
			lblFanartIcon.setPreferredSize(newSize);
			lblFanartIcon.repaint();
		}

		//BufferedImage posterImg = Thumb.convertToBufferedImage(posterImage);
		//BufferedImage posterScaledImage = ArtWorkPanel.resizeToPoster(posterImg);
		//this.setNewPoster(posterScaledImage, false);

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
		if(image != null && newWidth > 0 && newHeight > 0)
			return Scalr.resize(image, Method.QUALITY, newWidth, newHeight, Scalr.OP_ANTIALIAS);
		else return image;
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
					/*BufferedImage img = ImageIO.read(gui.getCurrentlySelectedPosterFileList().get(0));
					if(img != null)
					{
						//we're doing a resize so store off the original img so if we resize again we don't lose quality
						posterImage = img;
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						this.setNewPoster(scaledImage, false);
						posterFileUpdateOccured = true;
					}
					*/
					lblPosterIcon.setIcon(new Thumb(gui.getCurrentlySelectedPosterFileList().get(0)), new Dimension(maximumPosterSizeX, maximumPosterSizeY));
					posterFileUpdateOccured = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(gui.getCurrentlySelectedFanartFileList().get(0).exists())
			{
				try {
					/*BufferedImage img = ImageIO.read(gui.getCurrentlySelectedFanartFileList().get(0));
					if(img != null)
					{
						//we're doing a resize so store off the original img so if we resize again we don't lose quality
						fanartImage = img;
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						if(scaledImage != null)
						{
							this.setNewFanart(scaledImage, false);
							fanartFileUpdateOccured = true;
						}
					}*/
					lblFanartIcon.setIcon(new Thumb(gui.getCurrentlySelectedFanartFileList().get(0)), new Dimension(maximumFanartSizeX, maximumFanartSizeY));
					fanartFileUpdateOccured = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//well we didn't find a poster file we were expecting, try to see if there is any file named poster.jpg in there
			if(gui.getCurrentlySelectedMovieFileList().get(0).isDirectory() && potentialOtherPosterJpg.exists() && !posterFileUpdateOccured)
			{
				try {
					/*BufferedImage img = ImageIO.read(potentialOtherPosterJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
						//this.setNewPoster(scaledImage, true);
						posterFileUpdateOccured = true;
					}*/
					lblPosterIcon.setIcon(new Thumb(potentialOtherPosterJpg), new Dimension(maximumPosterSizeX, maximumPosterSizeY));
					posterFileUpdateOccured = true;

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//well we didn't find a fanart file we were expecting, try to see if there is any file named fanart.jpg in there
			if(gui.getCurrentlySelectedMovieFileList().get(0).isDirectory() && potentialOtherFanartJpg.exists() && !fanartFileUpdateOccured)
			{
				try {
					/*BufferedImage img = ImageIO.read(potentialOtherFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage, true);
						fanartFileUpdateOccured = true;
					}*/
					lblFanartIcon.setIcon(new Thumb(potentialOtherFanartJpg), new Dimension(maximumFanartSizeX, maximumFanartSizeY));
					fanartFileUpdateOccured = true;

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//just in case, also check to see if one called moviename-poster.jpg is there, even if we were expecting a poster.jpg due to the preference we set
			if(standardPosterJpg.exists() && !posterFileUpdateOccured)
			{
				try {
					/*this.setNewPoster(new ImageIcon(
							standardPosterJpg.getCanonicalPath()), true);*/
					lblPosterIcon.setIcon(new Thumb(standardPosterJpg), new Dimension(maximumPosterSizeX, maximumPosterSizeY));
					posterFileUpdateOccured = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			//just in case, also check to see if one called moviename-fanart.jpg is there, even if we were expecting a poster.jpg due to the preference we set
			if(standardFanartJpg.exists() && !fanartFileUpdateOccured)
			{
				try {
					/*BufferedImage img = ImageIO.read(standardFanartJpg);
					if(img != null)
					{
						BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
						this.setNewFanart(scaledImage, true);
						fanartFileUpdateOccured = true;
					}*/
					lblFanartIcon.setIcon(new Thumb(standardFanartJpg), new Dimension(maximumFanartSizeX, maximumFanartSizeY));
					fanartFileUpdateOccured = true;
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
					/*Image posterImage = gui.getFileDetailPanel().currentMovie.getPosters()[0]
							.getThumbImage();
					ImageIcon newPosterIcon = new ImageIcon(posterImage);
					BufferedImage img = (BufferedImage) newPosterIcon.getImage();
					BufferedImage scaledImage = ArtWorkPanel.resizeToPoster(img);
					this.setNewPoster(scaledImage, true);*/
					lblPosterIcon.setIcon(gui.getFileDetailPanel().currentMovie.getPosters()[0], new Dimension(maximumPosterSizeX, maximumPosterSizeY));
					posterFileUpdateOccured = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}

		//try to read the fanart from the url since we couldn't find any local file
		if (gui.movieToWriteToDiskList.size() > 0 && gui.movieToWriteToDiskList.get(0) != null && gui.movieToWriteToDiskList.get(0).hasFanart() && !fanartFileUpdateOccured) {
			if(gui.getFileDetailPanel().currentMovie.getFanart().length > 0)
			{
				/*fanartImage = gui.getFileDetailPanel().currentMovie.getFanart()[0]
						.getThumbImage();
				ImageIcon newFanartIcon = new ImageIcon(fanartImage);
				BufferedImage img = (BufferedImage) newFanartIcon.getImage();
				BufferedImage scaledImage = ArtWorkPanel.resizeToFanart(img);
				fanartImage = img;
				this.setNewFanart(scaledImage, false);
				*/
				lblFanartIcon.setIcon(gui.getFileDetailPanel().currentMovie.getFanart()[0], new Dimension(maximumFanartSizeX, maximumFanartSizeY));
				fanartFileUpdateOccured = true;
				
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

	private final class MouseListenerShowArtPicker implements MouseListener {

		/**
		 * Set this to true in the constructor if this is supposed to be used for fanart picker. 
		 * Could have done some kind of subclass thing, but this was just easier.
		 */
		boolean forFanartInsteadOfPosters;
		private static final String posterPickerDialogName = "Pick New Primary Poster";
		private static final String fanartPickerDialogName = "Pick New Primary Fanart";

		public MouseListenerShowArtPicker(boolean forFanartInsteadOfPosters) {
			this.forFanartInsteadOfPosters = forFanartInsteadOfPosters;
		}

		private String getDialogName()
		{
			if(forFanartInsteadOfPosters)
				return fanartPickerDialogName;
			else return posterPickerDialogName;
		}

		private Thumb[] getCorrectArtArray(Movie movie)
		{
			if(forFanartInsteadOfPosters)
				return movie.getFanart();
			else return movie.getPosters();
		}

		@Override
		public void mouseClicked(MouseEvent event) {

			if (event.getClickCount() == 2) {
				Movie currentMovie = guiMain.getFileDetailPanel().getCurrentMovie();
				if(currentMovie != null)
				{
					Thumb [] artToPick = getCorrectArtArray(currentMovie);

					if (artToPick != null
							&& currentMovie.getPosters().length > 1) {
						Thumb artFromUserSelection = ScrapeAmalgamatedProgressDialog.showArtPicker(artToPick,
								getDialogName(),!forFanartInsteadOfPosters);
						if(forFanartInsteadOfPosters)
							currentMovie.moveExistingFanartToFront(artFromUserSelection);
						else currentMovie.moveExistingPosterToFront(artFromUserSelection);
						guiMain.getFileDetailPanel().updateView(true, false);
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}


}

