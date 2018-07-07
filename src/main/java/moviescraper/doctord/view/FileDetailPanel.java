package moviescraper.doctord.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import moviescraper.doctord.controller.EditGenresAction;
import moviescraper.doctord.controller.EditTagsAction;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.AbstractFileDetailPanelEditGUI.Operation;
import moviescraper.doctord.view.renderer.ActressListRenderer;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

public class FileDetailPanel extends JPanel {

	private static final String NO_FILE_SELECTED = "No File Selected";

	private static final long serialVersionUID = 7088761619568387476L;

	private JTextField txtFieldMovieTitleText;
	private JTextField txtFieldOriginalTitleText;
	private JTextField txtFieldScrapedYearText;
	private JTextField txtFieldIDCurrentMovie;
	private JTextField txtFieldStudio;
	private JTextField txtFieldMovieSet;
	private JTextArea moviePlotTextField;
	private JList<Actor> actorList;
	private JTextField genreList;
	private JTextField tagList;

	protected Movie currentMovie = getEmptyMovie();
	MoviescraperPreferences preferences;

	private ArtWorkPanel artWorkPanel;
	private final static int DEFAULT_TEXTFIELD_LENGTH = 35;

	public GUIMain guiMain;

	public int currentListIndexOfDisplayedMovie = 0;

	private JTextField txtFieldReleaseDateText;

	private static final int COLUMN_LABEL = 2;
	private static final int COLUMN_FORM_FIELD = 4;
	private static final int COLUMN_ARTWORK_PANEL = 6;

	private static final int ROW_CHANGE_MOVIE_BUTTONS = 2;
	private static final int ROW_ARTWORK_PANEL = 2;
	private static final int ROW_FILE_PATH = 4;
	private static final int ROW_TITLE = 6;
	private static final int ROW_ORIGINAL_TITLE = 8;
	private static final int ROW_YEAR = 10;
	private static final int ROW_RELEASE_DATE = 12;
	private static final int ROW_ID = 14;
	private static final int ROW_STUDIO = 16;
	private static final int ROW_MOVIE_SET = 18;
	private static final int ROW_PLOT = 20;
	private static final int ROW_GENRES = 22;
	private static final int ROW_TAGS = 24;
	private static final int ROW_ACTORS = 26;

	private JButton previousMovieButton;

	private JButton nextMovieButton;

	private JTextField pathTextField;

	private JLabel numberInListSelectedLabel;

	/**
	 * Create the panel.
	 */
	public FileDetailPanel(MoviescraperPreferences preferences, GUIMain gui) {
		this.preferences = preferences;
		this.guiMain = gui;
		JPanel fileDetailsPanel = this;

		FormLayout formLayout = new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, // 1 - empty space
				FormFactory.DEFAULT_COLSPEC, //2 - label for each of the form items
				FormFactory.RELATED_GAP_COLSPEC, //3 - empty space
				ColumnSpec.decode("fill:pref:grow"), // 4 - Form text items
				FormFactory.RELATED_GAP_COLSPEC, //5 - empty space
				FormFactory.DEFAULT_COLSPEC, // 6 - artwork panel
				FormFactory.RELATED_GAP_COLSPEC,//7 - empty space
		}, new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, //1 - empty space
				FormFactory.DEFAULT_ROWSPEC, //2 - navigation buttons and artwork panel
				FormFactory.RELATED_GAP_ROWSPEC, //3  - empty space
				FormFactory.DEFAULT_ROWSPEC, //4 - File Path
				FormFactory.RELATED_GAP_ROWSPEC, //5 - empty space
				FormFactory.DEFAULT_ROWSPEC, //6 - Title
				FormFactory.RELATED_GAP_ROWSPEC, //7 - empty space
				FormFactory.DEFAULT_ROWSPEC, //8 - original title
				FormFactory.RELATED_GAP_ROWSPEC, //9 - empty space
				FormFactory.DEFAULT_ROWSPEC, //10 - Year
				FormFactory.RELATED_GAP_ROWSPEC, //11 - empty space
				FormFactory.DEFAULT_ROWSPEC, //12 - Release Date
				FormFactory.RELATED_GAP_ROWSPEC, //13 - empty space
				FormFactory.DEFAULT_ROWSPEC, //14 - ID
				FormFactory.RELATED_GAP_ROWSPEC, //15 - empty space
				FormFactory.DEFAULT_ROWSPEC, //16 - Studio
				FormFactory.RELATED_GAP_ROWSPEC, //17 - empty space
				FormFactory.DEFAULT_ROWSPEC, //18 - Movie set
				FormFactory.RELATED_GAP_ROWSPEC, //19 - empty space
				FormFactory.DEFAULT_ROWSPEC, //20 - Plot
				FormFactory.RELATED_GAP_ROWSPEC, //21 - empty space
				FormFactory.DEFAULT_ROWSPEC, //22 - genres
				FormFactory.RELATED_GAP_ROWSPEC, //23 - empty space
				FormFactory.DEFAULT_ROWSPEC, //24 - tags
				FormFactory.RELATED_GAP_ROWSPEC, //25 - empty space
				RowSpec.decode("fill:pref:grow"), //26 - actors
				FormFactory.RELATED_GAP_ROWSPEC//27 - empty space
		});

		fileDetailsPanel.setLayout(formLayout);

		//next and previous buttons
		previousMovieButton = new JButton("<< Previous Movie");
		previousMovieButton.addActionListener(e -> handlePreviousMovieSelected());
		numberInListSelectedLabel = new JLabel("0 / 0");
		nextMovieButton = new JButton("Next Movie >>");
		nextMovieButton.addActionListener(e -> handleNextMovieSelected());
		JPanel movieNavigationButtonsPanel = new JPanel();
		movieNavigationButtonsPanel.add(previousMovieButton);
		movieNavigationButtonsPanel.add(numberInListSelectedLabel);
		movieNavigationButtonsPanel.add(nextMovieButton);
		changeEnabledStatusOfPreviousAndNextButtons();
		fileDetailsPanel.add(movieNavigationButtonsPanel, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_CHANGE_MOVIE_BUTTONS));

		//Path
		JLabel lblPath = new JLabel("Path:");
		pathTextField = new JTextField(NO_FILE_SELECTED, DEFAULT_TEXTFIELD_LENGTH);
		pathTextField.setEditable(false);
		updatePathTextField();
		fileDetailsPanel.add(pathTextField, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_FILE_PATH));
		fileDetailsPanel.add(lblPath, getLayoutPositionString(COLUMN_LABEL, ROW_FILE_PATH));

		//Movie title

		JLabel lblTitle = new JLabel("Title:");
		fileDetailsPanel.add(lblTitle, getLayoutPositionString(COLUMN_LABEL, ROW_TITLE));

		txtFieldMovieTitleText = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldMovieTitleText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentMovie != null) {
					String newValue = (String) txtFieldMovieTitleText.getText();
					if (newValue != null && newValue.length() > 0) {
						currentMovie.setTitle(new Title(newValue));
						guiMain.enableFileWrite();
					} else {
						guiMain.disableFileWrite();
					}
				}
			}
		});
		txtFieldMovieTitleText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldMovieTitleText.getText();
				if (newValue != null && newValue.length() > 0) {
					currentMovie.setTitle(new Title(newValue));
					guiMain.enableFileWrite();
				} else {
					guiMain.disableFileWrite();
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldMovieTitleText, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_TITLE));

		JLabel lblOriginalTitle = new JLabel("Original Title:");
		fileDetailsPanel.add(lblOriginalTitle, getLayoutPositionString(COLUMN_LABEL, ROW_ORIGINAL_TITLE));

		txtFieldOriginalTitleText = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldOriginalTitleText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldOriginalTitleText.getText();
					if (newValue != null) {
						currentMovie.setOriginalTitle(new OriginalTitle(newValue));
					}
				}
			}
		});
		txtFieldOriginalTitleText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldOriginalTitleText.getText();
				if (newValue != null) {
					currentMovie.setOriginalTitle(new OriginalTitle(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldOriginalTitleText, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_ORIGINAL_TITLE));

		JLabel lblYear = new JLabel("Year:");
		fileDetailsPanel.add(lblYear, getLayoutPositionString(COLUMN_LABEL, ROW_YEAR));

		txtFieldScrapedYearText = new JTextField("", 4);
		fileDetailsPanel.add(txtFieldScrapedYearText, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_YEAR));
		txtFieldScrapedYearText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldScrapedYearText.getText();
					if (newValue != null) {
						currentMovie.setYear(new Year(newValue));
					}
				}
			}
		});
		txtFieldScrapedYearText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldScrapedYearText.getText();
				if (newValue != null) {
					currentMovie.setYear(new Year(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});

		JLabel lblReleaseDate = new JLabel("Release Date:");
		fileDetailsPanel.add(lblReleaseDate, getLayoutPositionString(COLUMN_LABEL, ROW_RELEASE_DATE));

		txtFieldReleaseDateText = new JTextField("", 12);
		txtFieldReleaseDateText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldReleaseDateText.getText();
					if (newValue != null) {
						currentMovie.setReleaseDate(new ReleaseDate(newValue));
					}
				}
			}
		});
		txtFieldReleaseDateText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldReleaseDateText.getText();
				if (newValue != null) {
					currentMovie.setReleaseDate(new ReleaseDate(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldReleaseDateText, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_RELEASE_DATE));

		JLabel lblID = new JLabel("ID:");
		fileDetailsPanel.add(lblID, getLayoutPositionString(COLUMN_LABEL, ROW_ID));

		txtFieldIDCurrentMovie = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldIDCurrentMovie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldIDCurrentMovie.getText();
					if (newValue != null) {
						currentMovie.setId(new ID(newValue));
					}
				}
			}
		});
		txtFieldIDCurrentMovie.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldIDCurrentMovie.getText();
				if (newValue != null) {
					currentMovie.setId(new ID(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldIDCurrentMovie, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_ID));

		JLabel lblStudio = new JLabel("Studio:");
		txtFieldStudio = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldStudio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldStudio.getText();
					if (newValue != null) {
						currentMovie.setStudio(new Studio(newValue));
					}
				}
			}
		});
		txtFieldStudio.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldStudio.getText();
				if (newValue != null) {
					currentMovie.setStudio(new Studio(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});

		fileDetailsPanel.add(lblStudio, getLayoutPositionString(COLUMN_LABEL, ROW_STUDIO));
		fileDetailsPanel.add(txtFieldStudio, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_STUDIO));

		JLabel lblSet = new JLabel("Movie Set:");
		fileDetailsPanel.add(lblSet, getLayoutPositionString(COLUMN_LABEL, ROW_MOVIE_SET));
		txtFieldMovieSet = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldMovieSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentMovie != null) {
					String newValue = (String) txtFieldMovieSet.getText();
					if (newValue != null) {
						currentMovie.setSet(new Set(newValue));
					}
				}
			}
		});
		txtFieldMovieSet.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldMovieSet.getText();
				if (newValue != null) {
					currentMovie.setSet(new Set(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldMovieSet, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_MOVIE_SET));

		JLabel lblPlot = new JLabel("Plot:");
		fileDetailsPanel.add(lblPlot, getLayoutPositionString(COLUMN_LABEL, ROW_PLOT));

		moviePlotTextField = new JTextArea(3, 35);
		moviePlotTextField.setLineWrap(true);
		moviePlotTextField.setWrapStyleWord(true);
		moviePlotTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) moviePlotTextField.getText();
				if (newValue != null) {
					currentMovie.setPlot(new Plot(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});

		JScrollPane plotPanelScrollPane = new JScrollPane(moviePlotTextField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		fileDetailsPanel.add(plotPanelScrollPane, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_PLOT));

		JLabel lblActors = new JLabel("Actors:");
		fileDetailsPanel.add(lblActors, getLayoutPositionString(COLUMN_LABEL, ROW_ACTORS));

		actorList = new JList<>(new ActorItemListModel());
		actorList.setDragEnabled(true);
		actorList.setDropMode(DropMode.INSERT);
		actorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		actorList.setTransferHandler(new ActorTransferHandler(actorList));

		List<File> currentlySelectedActorsFolderList = new ArrayList<>();
		if (gui != null)
			currentlySelectedActorsFolderList = gui.getCurrentlySelectedActorsFolderList();
		actorList.setCellRenderer(new ActressListRenderer(currentlySelectedActorsFolderList));
		actorList.setComponentPopupMenu(new FileDetailPanelPopup(new FileDetailPanelActorEditor(this)));
		actorList.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				actorList.setSelectedIndex(actorList.locationToIndex(e.getPoint()));
			}

			//double or triple click the actor list to open the editor on the item you clicked
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2 || evt.getClickCount() == 3) {
					// Double-click detected
					FileDetailPanelActorEditor actorEditor = new FileDetailPanelActorEditor(FileDetailPanel.this);
					actorEditor.showGUI(Operation.EDIT);
				}
			}
		});

		JScrollPane actorListScroller = new JScrollPane(actorList);
		actorListScroller.setPreferredSize(new Dimension(250, 250));

		fileDetailsPanel.add(actorListScroller, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_ACTORS));

		JLabel lblGenres = new JLabel("Genres:");
		fileDetailsPanel.add(lblGenres, getLayoutPositionString(COLUMN_LABEL, ROW_GENRES));

		genreList = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		//the user clicks the field to edit it - we don't want them typing directly here
		genreList.addKeyListener(new KeyListenerIgnoreTyping());
		genreList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				EditGenresAction editGenresAction = new EditGenresAction(FileDetailPanel.this);
				editGenresAction.actionPerformed(null);
			}
		});
		fileDetailsPanel.add(genreList, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_GENRES));

		JLabel lblTags = new JLabel("Tags:");
		fileDetailsPanel.add(lblTags, getLayoutPositionString(COLUMN_LABEL, ROW_TAGS));

		tagList = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		//the user clicks the field to edit it - we don't want them typing directly here
		tagList.addKeyListener(new KeyListenerIgnoreTyping());
		tagList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				EditTagsAction editTagsAction = new EditTagsAction(FileDetailPanel.this);
				editTagsAction.actionPerformed(null);
			}
		});
		fileDetailsPanel.add(tagList, getLayoutPositionString(COLUMN_FORM_FIELD, ROW_TAGS));

		artWorkPanel = new ArtWorkPanel(guiMain);
		//frmMoviescraper.getContentPane().add(artworkPanelScrollPane, BorderLayout.EAST);
		String beginRow = "1";
		String rowsToSpan = Integer.toString((formLayout.getRowCount() - 1));
		String rowSpanConstraintString = "," + beginRow + "," + rowsToSpan;
		fileDetailsPanel.add(artWorkPanel, getLayoutPositionString(COLUMN_ARTWORK_PANEL, ROW_ARTWORK_PANEL) + rowSpanConstraintString);

	}

	public void updatePathTextField() {
		int positionOfCurrentMovie = findPositionOfCurrentlySelectedMovie();
		if (positionOfCurrentMovie >= 0 && guiMain.getCurrentlySelectedMovieFileList().size() > positionOfCurrentMovie) {
			File currentlyShowingFile = guiMain.getCurrentlySelectedMovieFileList().get(positionOfCurrentMovie);
			if (currentlyShowingFile != null && currentlyShowingFile.exists()) {
				pathTextField.setText(currentlyShowingFile.getAbsolutePath().toString());
				pathTextField.setCaretPosition(0);
			}
		} else {
			pathTextField.setText("");
		}

	}

	/**
	 * Updates the view by setting the previous selected movie as the movie which is showing
	 */
	private void handlePreviousMovieSelected() {
		int positionOfCurrentMovie = findPositionOfCurrentlySelectedMovie();
		if (positionOfCurrentMovie == -1)
			positionOfCurrentMovie = currentListIndexOfDisplayedMovie;
		int positionOfPreviousMovie = positionOfCurrentMovie - 1;
		if (positionOfCurrentMovie >= 0 && positionOfPreviousMovie < guiMain.movieToWriteToDiskList.size() && positionOfPreviousMovie >= 0) {
			Movie previousMovie = guiMain.movieToWriteToDiskList.get(positionOfPreviousMovie);
			//if (previousMovie != null) {
			currentListIndexOfDisplayedMovie = positionOfPreviousMovie;
			setNewMovie(previousMovie, false, false);
			//}
		}
	}

	/**
	 * Updates the view by setting the next selected movie as the movie which is showing
	 */
	private void handleNextMovieSelected() {

		int positionOfCurrentMovie = findPositionOfCurrentlySelectedMovie();
		if (positionOfCurrentMovie == -1)
			positionOfCurrentMovie = currentListIndexOfDisplayedMovie;
		int positionOfNextMovie = positionOfCurrentMovie + 1;
		if (positionOfCurrentMovie >= 0 && positionOfNextMovie < guiMain.movieToWriteToDiskList.size()) {
			Movie nextMovie = guiMain.movieToWriteToDiskList.get(positionOfNextMovie);
			//if (nextMovie != null) {
			currentListIndexOfDisplayedMovie = positionOfNextMovie;
			setNewMovie(nextMovie, false, false);
			//}
		}
	}

	//returns -1 if not found
	private int findPositionOfCurrentlySelectedMovie() {
		if (guiMain != null && guiMain.movieToWriteToDiskList != null && currentMovie != null) {
			for (int i = 0; i < guiMain.movieToWriteToDiskList.size(); i++) {
				if (guiMain.movieToWriteToDiskList.get(i) != null && guiMain.movieToWriteToDiskList.get(i).equals(currentMovie))
					return i;
			}
		}
		return -1;
	}

	/**
	 * Sets a new movie and updates a view
	 * @param newMovie the movie this fileDetailPanel will show
	 * @param forcePosterUpdate whether to force a redownload from the poster defined in the Thumb url
	 */

	public void setNewMovie(Movie newMovie, boolean forcePosterUpdate) {
		setNewMovie(newMovie, forcePosterUpdate, false);
	}

	/**
	 * Sets a new movie and updates a view
	 * @param newMovie the movie this fileDetailPanel will show
	 * @param forcePosterUpdate whether to force a redownload from the poster defined in the Thumb url
	 * @param modifyWriteToDiskList - whether to modify the gui object's disk list by adding the currently viewed item if the disk list is empty
	 */
	public void setNewMovie(Movie newMovie, boolean forcePosterUpdate, boolean modifyWriteToDiskList) {
		//System.out.println("Setting new movie: " + newMovie);
		if (newMovie != null) {
			setCurrentMovie(newMovie);
			updateView(forcePosterUpdate, modifyWriteToDiskList);
		} else {
			clearView();
			updatePathTextField();
			changeEnabledStatusOfPreviousAndNextButtons();
			updateNumberInListSelectedLabel();
			updateView(true, false);
		}
	}

	public void clearView() {
		artWorkPanel.clearPictures();
		setTitleEditable(false);
		this.currentMovie = getEmptyMovie();
	}

	/**
	 * Updates the view for the current movie
	 * @param forcePosterUpdate - if force a refresh of the poster from the URL by downloading the file. If false, tries to
	 * read from the local file first
	 * @param newMovieWasSet - true if you are setting a new movie. clears the old one and refreshes all fields
	 */
	public void updateView(boolean forcePosterUpdate, boolean newMovieWasSet) {

		List<Movie> movieToWriteToDiskList = guiMain.getMovieToWriteToDiskList();
		//do i need this?
		//currentListIndexOfDisplayedMovie = Math.max(findPositionOfCurrentlySelectedMovie(),0);

		if (newMovieWasSet && movieToWriteToDiskList.size() == 0) {
			movieToWriteToDiskList.add(currentMovie);
		}
		//begin
		if ((movieToWriteToDiskList == null || movieToWriteToDiskList.size() == 0) && !newMovieWasSet) {
			clearView();
		} else if (movieToWriteToDiskList != null && movieToWriteToDiskList.get(currentListIndexOfDisplayedMovie) != null) {
			if (!newMovieWasSet)
				clearView();
			if (!newMovieWasSet)
				this.setCurrentMovie(movieToWriteToDiskList.get(currentListIndexOfDisplayedMovie));

			//All the titles from the various versions scraped of this movie from the different sites
			this.getCurrentMovie().getAllTitles().add(getCurrentMovie().getTitle());
			String fileName = this.getCurrentMovie().getFileName();
			if (fileName != null && fileName.trim().length() > 0)
				this.getCurrentMovie().getAllTitles().add(new Title(fileName));

			if (this.getCurrentMovie().getAllTitles().size() > 0)
				this.setTitleEditable(true);
			//end
		}
		txtFieldMovieTitleText.setText(currentMovie.getTitle().getTitle());
		txtFieldMovieTitleText.setCaretPosition(0);
		txtFieldOriginalTitleText.setText(currentMovie.getOriginalTitle().getOriginalTitle());
		txtFieldOriginalTitleText.setCaretPosition(0);
		txtFieldScrapedYearText.setText(currentMovie.getYear().getYear());
		txtFieldReleaseDateText.setText(currentMovie.getReleaseDate().getReleaseDate());
		txtFieldIDCurrentMovie.setText(currentMovie.getId().getId());
		txtFieldStudio.setText(currentMovie.getStudio().getStudio());
		txtFieldStudio.setCaretPosition(0);
		txtFieldMovieSet.setText(currentMovie.getSet().getSet());
		txtFieldMovieSet.setCaretPosition(0);
		moviePlotTextField.setText(currentMovie.getPlot().getPlot());
		moviePlotTextField.setCaretPosition(0);
		genreList.setText(toGenreListFormat(currentMovie.getGenres()));
		genreList.setCaretPosition(0);
		tagList.setText(toTagListFormat(currentMovie.getTags()));
		tagList.setCaretPosition(0);

		//Actors and Genres are automatically generated
		actorList.updateUI();
		artWorkPanel.updateView(forcePosterUpdate, guiMain);

		if (txtFieldMovieTitleText.getText().length() > 0) {
			guiMain.enableFileWrite();
		} else {
			guiMain.disableFileWrite();
		}
		updatePathTextField();
		changeEnabledStatusOfPreviousAndNextButtons();
		updateNumberInListSelectedLabel();
	}

	private void changeEnabledStatusOfPreviousAndNextButtons() {
		List<Movie> movieList = guiMain.getMovieToWriteToDiskList();

		System.out.println("Movie Size: " + movieList.size());
		//no movies to scroll through
		if (movieList.size() == 0) {
			nextMovieButton.setEnabled(false);
			previousMovieButton.setEnabled(false);
			return;
		}
		if ((currentListIndexOfDisplayedMovie - 1) < 0) {
			previousMovieButton.setEnabled(false);
		} else
			previousMovieButton.setEnabled(true);

		if ((currentListIndexOfDisplayedMovie + 1) >= movieList.size()) {
			nextMovieButton.setEnabled(false);
		} else
			nextMovieButton.setEnabled(true);

	}

	//Updates the label which shows what movie number we are on: e.g. 1/5
	private void updateNumberInListSelectedLabel() {
		int movieNumberIAmOn = currentListIndexOfDisplayedMovie + 1;
		int numberOfMovies = guiMain.getMovieToWriteToDiskList().size();
		numberInListSelectedLabel.setText(movieNumberIAmOn + " / " + numberOfMovies);
	}

	public Movie getCurrentMovie() {
		return currentMovie;
	}

	public Movie getEmptyMovie() {
		return Movie.getEmptyMovie();

	}

	public void setTitleEditable(boolean value) {
		txtFieldMovieTitleText.setEditable(value);
	}

	public ArtWorkPanel getArtWorkPanel() {
		return artWorkPanel;
	}

	public JList<Actor> getActorList() {
		return actorList;
	}

	public void setActorList(JList<Actor> actorList) {
		this.actorList = actorList;
	}

	public JTextField getGenreList() {
		return genreList;
	}

	public void setGenreList(JTextField genreList) {
		this.genreList = genreList;
	}

	public JTextField getTagList() {
		return tagList;
	}

	public void setTagList(JTextField tagList) {
		this.tagList = tagList;
	}

	public void setCurrentMovie(Movie currentMovie) {
		this.currentMovie = currentMovie;
	}

	public void hideArtworkPanel() {
		artWorkPanel.setVisible(false);
	}

	private final class KeyListenerIgnoreTyping implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			e.consume();

		}

		@Override
		public void keyPressed(KeyEvent e) {
			e.consume();

		}

		@Override
		public void keyReleased(KeyEvent e) {
			e.consume();

		}
	}

	public class GenreItemListModel extends DefaultListModel<Genre> {

		private static final long serialVersionUID = 973741706455659871L;

		@Override
		public Genre getElementAt(int index) {
			Genre genre = currentMovie.getGenres().get(index);
			return (genre != null) ? genre : new Genre("");
		}

		@Override
		public int getSize() {
			return currentMovie.getGenres().size();
		}
	}

	class ActorItemListModel extends AbstractListModel<Actor> {

		private static final long serialVersionUID = 276453659002862686L;

		@Override
		public Actor getElementAt(int index) {
			return currentMovie.getActors().get(index);
		}

		public Actor remove(int index) {
			return currentMovie.getActors().remove(index);
		}

		public void add(int index, Actor actor) {
			currentMovie.getActors().add(index, actor);
		}

		public int indexOf(Actor actor) {
			return currentMovie.getActors().indexOf(actor);
		}

		public boolean removeElement(Actor actor) {
			return currentMovie.getActors().remove(actor);
		}

		@Override
		public int getSize() {
			return currentMovie.getActors().size();
		}

	}

	public static String toGenreListFormat(ArrayList<Genre> genres) {
		String genreText = "";
		for (Genre currentGenre : genres) {
			genreText += currentGenre.getGenre();
			genreText += " \\ ";
		}
		if (genreText.endsWith(" \\ ")) {
			genreText = genreText.substring(0, genreText.length() - 3);
		}
		return genreText;
	}

	public static String toTagListFormat(ArrayList<Tag> tags) {
		String tagText = "";
		for (Tag currentTag : tags) {
			tagText += currentTag.getTag();
			tagText += " \\ ";
		}
		if (tagText.endsWith(" \\ ")) {
			tagText = tagText.substring(0, tagText.length() - 3);
		}
		return tagText;
	}

	private String getLayoutPositionString(int columnNumber, int rowNumber) {
		return columnNumber + ", " + rowNumber;
	}

	static class ActorTransferHandler extends TransferHandler {

		private static final DataFlavor DATA_FLAVOUR = new DataFlavor(Actor.class, "Actor");

		private final JList actorList;
		private boolean inDrag;

		ActorTransferHandler(JList actorList) {
			this.actorList = actorList;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			inDrag = true;
			return new Transferable() {
				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[] { DATA_FLAVOUR };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return flavor.equals(DATA_FLAVOUR);
				}

				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					if (flavor != DATA_FLAVOUR) {
						return new UnsupportedFlavorException(flavor);
					}
					return actorList.getSelectedValue();
				}
			};
		}

		@Override
		public boolean canImport(TransferSupport support) {
			if (!inDrag || !support.isDataFlavorSupported(DATA_FLAVOUR)) {
				return false;
			}

			JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
			if (dl.getIndex() == -1) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}

			Transferable transferable = support.getTransferable();
			try {
				Actor draggedActor = (Actor) transferable.getTransferData(DATA_FLAVOUR);

				JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
				ActorItemListModel model = (ActorItemListModel) actorList.getModel();
				int dropIndex = dl.getIndex();
				if (model.indexOf(draggedActor) < dropIndex) {
					dropIndex--;
				}
				model.removeElement(draggedActor);
				model.add(dropIndex, draggedActor);
				return true;
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			super.exportDone(source, data, action);
			inDrag = false;
			this.actorList.repaint();
		}
	}

}
