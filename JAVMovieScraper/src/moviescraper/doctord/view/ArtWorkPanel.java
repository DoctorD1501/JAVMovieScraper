package moviescraper.doctord.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
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

	private AsyncImageComponent lblPosterIcon;
	private AsyncImageComponent lblFanartIcon;
	private static final String artworkTooltip = "Double click to change image.";

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
		lblPosterIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPosterIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
		lblPosterIcon.addMouseListener(new MouseListenerShowArtPicker(false));

		//set up the fanart
		lblFanartIcon = new AsyncImageComponent(null, false, null, false, true, false);
		lblFanartIcon.setPreferredSize(new Dimension(maximumFanartSizeX, maximumFanartSizeY));
		lblFanartIcon.setToolTipText(artworkTooltip);
		lblFanartIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblFanartIcon.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblFanartIcon.addMouseListener(new MouseListenerShowArtPicker(true));


		add(lblPosterIcon);
		//add a little bit of space between the poster and the fanart
		add(Box.createRigidArea(new Dimension(0,5)));
		add(lblFanartIcon);

		oldSize = getBounds().getSize();
	}

	public void clearPictures() {
		clearFanart();
		clearPoster();
	}

	public void clearFanart() {
		lblFanartIcon.clear();
	}

	public void clearPoster() {
		lblPosterIcon.clear();
	}

	private void updatePosterAndFanartSizes(){
		updatingPosterAndFanartSizes = true;
		if(lblFanartIcon != null)
		{
			Dimension newSize = calculateDimensionFit(lblFanartIcon.getWidth(), lblFanartIcon.getHeight(), maximumPosterSizeX, maximumFanartSizeY);
			lblFanartIcon.setPreferredSize(newSize);
			lblFanartIcon.repaint();
		}
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
					lblPosterIcon.setIcon(new Thumb(gui.getCurrentlySelectedPosterFileList().get(0)), new Dimension(maximumPosterSizeX, maximumPosterSizeY));
					posterFileUpdateOccured = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(gui.getCurrentlySelectedFanartFileList().get(0).exists())
			{
				try {
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

