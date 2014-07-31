package moviescraper.doctord.GUI;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.JList;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.XbmcXmlMovieBean;
import moviescraper.doctord.SiteParsingProfile.ActionJavParsingProfile;
import moviescraper.doctord.SiteParsingProfile.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavZooParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SquarePlusParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;
import moviescraper.doctord.preferences.MoviescraperPreferences;

import javax.swing.JLabel;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JButton;

import java.awt.SystemColor;

import javax.swing.UIManager;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.Action;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class GUIMain {

	private JFrame frmMoviescraper;
	private final Action moveToNewFolder = new MoveToNewFolderAction();
	private File currentlySelectedNfoFile;
	private File currentlySelectedPosterFile;
	private File currentlySelectedFanartFile;
	private File currentlySelectedDirectory;
	private File currentlySelectedMovieFile;
	private File actorsFolder;
	private File[] filesToList;
	private Movie currentlySelectedMovieDMM;
	private Movie currentlySelectedMovieActionJav;
	private Movie currentlySelectedMovieSquarePlus;
	private Movie currentlySelectedMovieJavLibrary;
	private Movie currentlySelectedMovieJavZoo;
	private Movie movieToWriteToDisk;

	private JComboBox<String> comboBoxMovieTitleText;
	private DefaultListModel<String> listModelActorsSite1;
	private JList<String> actorListSite1;
	private DefaultListModel<String> listModelGenresSite1;
	private DefaultListModel<File> listModelFiles;
	private JList<String> genreListSite1;
	private JScrollPane fileListScrollPane;
	private JList<File> fileList;
	private Image posterImage;
	private JPanel artworkPanel;
	private JLabel lblPosterIcon;
	JLabel lblYearGoesHere;
	private File defaultHomeDirectory;
	private JLabel lblOriginalTitleTextSite1;
	private JFileChooser chooser;
	private JLabel lblIDCurrentMovie;
	private JTextField moviePlotTextField;
	private JTextField txtFieldMovieSet;
	private MoviescraperPreferences preferences;
	
	//Menus
	JMenuBar menuBar;
	JMenu preferenceMenu;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					GUIMain window = new GUIMain();
					System.out.println("Gui Initialized");
					window.frmMoviescraper.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIMain() {
		initialize();
	}
	

	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		preferences = new MoviescraperPreferences();
		currentlySelectedNfoFile = new File("");
		currentlySelectedPosterFile = new File(
				"");
		currentlySelectedFanartFile = new File(
				"");
		actorsFolder = new File("");
		frmMoviescraper = new JFrame();
		frmMoviescraper.setBackground(SystemColor.window);
		frmMoviescraper.setPreferredSize(new Dimension(1024, 768));
		frmMoviescraper.setTitle("JAVMovieScraper");
		frmMoviescraper.setBounds(100, 100, 1024, 768);
		frmMoviescraper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel FileListPanel = new JPanel();
		FileListPanel.setPreferredSize(new Dimension(200, 10));
		frmMoviescraper.getContentPane().add(FileListPanel, BorderLayout.WEST);

		defaultHomeDirectory = preferences.getLastUsedDirectory();
		currentlySelectedDirectory = defaultHomeDirectory;
		FileList fl = new FileList();

		listModelFiles = new DefaultListModel<File>();
		fileList = new JList<File>(listModelFiles);
		fileList.addListSelectionListener(new SelectFileListAction());
		fileListScrollPane = fl.getGui(
				showFileListSorted(currentlySelectedDirectory), listModelFiles,
				true);
		FileListPanel.add(fileListScrollPane);
		JButton btnOpenDirectory = new JButton("Open Directory");
		btnOpenDirectory.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		btnOpenDirectory.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnOpenDirectory.addActionListener(new OpenDirectoryAction());
		FileListPanel.setLayout(new BoxLayout(FileListPanel, BoxLayout.Y_AXIS));
		FileListPanel.add(btnOpenDirectory);

		JPanel FileDetailsPanel = new JPanel();
		JScrollPane FileDetailsScrollPane = new JScrollPane(FileDetailsPanel);
		FileDetailsPanel.setForeground(Color.GRAY);
		frmMoviescraper.getContentPane().add(FileDetailsScrollPane,
				BorderLayout.CENTER);
		FileDetailsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));



		JLabel lblTitle = new JLabel("Title:");
		FileDetailsPanel.add(lblTitle, "2, 4");



		String [] initialTitleComboBox = {"Not Scraped Yet"};
		
		//using this workaround for JComboBox constructor for problem with generics in WindowBuilder as per this stackoverflow thread: https://stackoverflow.com/questions/8845139/jcombobox-warning-preventing-opening-the-design-page-in-eclipse
		comboBoxMovieTitleText = new JComboBox<String>();
		comboBoxMovieTitleText.setModel(new DefaultComboBoxModel<String>(initialTitleComboBox));
		comboBoxMovieTitleText.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(movieToWriteToDisk != null)
	            {
	            	String newValue = (String) comboBoxMovieTitleText.getSelectedItem();
	            	if(newValue != null)
	            	{
	            		movieToWriteToDisk.setTitle(new Title(newValue));
	            	}
	            }
	        }
	    });
		comboBoxMovieTitleText.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) comboBoxMovieTitleText.getSelectedItem();
            	if(newValue != null)
            	{
            		movieToWriteToDisk.setTitle(new Title(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		
		FileDetailsPanel.add(comboBoxMovieTitleText, "6, 4");



		JLabel lblOriginalTitle = new JLabel("Original Title:");
		FileDetailsPanel.add(lblOriginalTitle, "2, 6");



		lblOriginalTitleTextSite1 = new JLabel("Not Scraped Yet");
		FileDetailsPanel.add(lblOriginalTitleTextSite1, "6, 6");


		JLabel lblYear = new JLabel("Year:");
		FileDetailsPanel.add(lblYear, "2, 8");
		

		lblYearGoesHere = new JLabel("Not Scraped Yet");
		FileDetailsPanel.add(lblYearGoesHere, "6, 8");
		
		JLabel lblID = new JLabel("ID:");
		FileDetailsPanel.add(lblID, "2, 10");
		
		lblIDCurrentMovie = new JLabel("Not Scraped Yet");
		FileDetailsPanel.add(lblIDCurrentMovie,"6, 10");
		
		JLabel lblSet = new JLabel("Movie Set:");
		FileDetailsPanel.add(lblSet,"2, 12");
		txtFieldMovieSet = new JTextField("");
		txtFieldMovieSet.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(movieToWriteToDisk != null)
	            {
	            	String newValue = (String) txtFieldMovieSet.getText();
	            	if(newValue != null)
	            	{
	            		movieToWriteToDisk.setSet(new Set(newValue));
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
            	if(newValue != null)
            	{
            		movieToWriteToDisk.setSet(new Set(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		FileDetailsPanel.add(txtFieldMovieSet,"6,12");
		
		JLabel lblPlot = new JLabel("Plot:");
		FileDetailsPanel.add(lblPlot, "2,14");
		

		moviePlotTextField = new JTextField(35);
		moviePlotTextField.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(movieToWriteToDisk != null)
	            {
	            	String newValue = (String) moviePlotTextField.getText();
	            	if(newValue != null)
	            	{
	            		movieToWriteToDisk.setPlot(new Plot(newValue));
	            	}
	            }
	        }
	    });
		moviePlotTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) moviePlotTextField.getText();
            	if(newValue != null)
            	{
            		movieToWriteToDisk.setPlot(new Plot(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});

		JScrollBar moviePlotScroller = new JScrollBar(JScrollBar.HORIZONTAL);
		JPanel plotPanel = new JPanel();
		plotPanel.setLayout(new BoxLayout(plotPanel, BoxLayout.Y_AXIS));
		 BoundedRangeModel brm = moviePlotTextField.getHorizontalVisibility();
		 moviePlotScroller.setModel(brm);
		 plotPanel.add(moviePlotTextField);
		 plotPanel.add(moviePlotScroller);

		FileDetailsPanel.add(plotPanel,"6,14");
		

		JLabel lblActors = new JLabel("Actors:");
		FileDetailsPanel.add(lblActors, "2, 16");

		listModelActorsSite1 = new DefaultListModel<String>();
		actorListSite1 = new JList<String>(listModelActorsSite1);
		actorListSite1.setCellRenderer(new ActressListRenderer());


		JScrollPane actorListScroller = new JScrollPane(actorListSite1);
		actorListScroller.setPreferredSize(new Dimension(250, 300));
		actorListSite1.setSize(new Dimension(250, 300));
		FileDetailsPanel.add(actorListScroller, "6, 16");

		JLabel lblGenres = new JLabel("Genres:");
		FileDetailsPanel.add(lblGenres, "2, 18");

		listModelGenresSite1 = new DefaultListModel<String>();
		genreListSite1 = new JList<String>(listModelGenresSite1);
		JScrollPane listScrollerGenres = new JScrollPane(genreListSite1);

		genreListSite1.setSize(new Dimension(200, 200));
		FileDetailsPanel.add(listScrollerGenres, "6, 18");

		artworkPanel = new JPanel();
		JScrollPane artworkPanelScrollPane = new JScrollPane(artworkPanel);
		lblPosterIcon = new JLabel("");

		Dimension posterSize = new Dimension(379, 536);
		// posterImage is initially a transparent poster size rectangle
		posterImage = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(379, 536, Transparency.TRANSLUCENT);
		ImageIcon posterIcon = new ImageIcon(posterImage);
		lblPosterIcon.setIcon(posterIcon);
		lblPosterIcon.setSize(new Dimension(379, 536));
		lblPosterIcon.setMaximumSize(posterSize);
		lblPosterIcon.setMinimumSize(posterSize);
		lblPosterIcon.setPreferredSize(posterSize);

		artworkPanel.add(lblPosterIcon);
		frmMoviescraper.getContentPane().add(artworkPanelScrollPane, BorderLayout.EAST);

		JPanel buttonsPanel = new JPanel();
		frmMoviescraper.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		JButton btnScrapeSelectMovie = new JButton("Scrape Selected Movie");
		btnScrapeSelectMovie.setAction(new ScrapeMovieAction());
		buttonsPanel.add(btnScrapeSelectMovie);
		
		JButton btnScrapeSelectMovieAutomatic = new JButton("Scrape Movie (Automatic Mode)");
		btnScrapeSelectMovieAutomatic.setAction(new ScrapeMovieActionAutomatic());
		buttonsPanel.add(btnScrapeSelectMovieAutomatic);
		

		JButton btnMoveFileToFolder = new JButton("Move file to folder");
		btnMoveFileToFolder.setAction(moveToNewFolder);
		buttonsPanel.add(btnMoveFileToFolder);

		JButton btnWriteFileData = new JButton("Write File Data");
		btnWriteFileData.addActionListener(new WriteFileDataAction());
		buttonsPanel.add(btnWriteFileData);

		JButton openCurrentlySelectedFileButton = new JButton(
				"Open Currently Selected File");
		openCurrentlySelectedFileButton.addActionListener(new OpenFileAction());
		buttonsPanel.add(openCurrentlySelectedFileButton);
		initializeMenus();
	}

	private void initializeMenus(){
		menuBar = new JMenuBar();
		
		//Set up the preferences menu
		preferenceMenu = new JMenu("Preferences");
		preferenceMenu.setMnemonic(KeyEvent.VK_P);
		preferenceMenu.getAccessibleContext().setAccessibleDescription(
                "Preferences for JAVMovieScraper");
		
		
		//Checkbox for writing fanart and poster
		JCheckBoxMenuItem writeFanartAndPosters = new JCheckBoxMenuItem("Write fanart and poster files");
		writeFanartAndPosters.setState(preferences.getWriteFanartAndPostersPreference());
		writeFanartAndPosters.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					preferences.setWriteFanartAndPostersPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					preferences.setWriteFanartAndPostersPreference(false);
				
			}
		});
		preferenceMenu.add(writeFanartAndPosters);
		
		//Checkbox for overwriting fanart and poster
		JCheckBoxMenuItem overwriteFanartAndPosters = new JCheckBoxMenuItem("Overwrite fanart and poster files");
		overwriteFanartAndPosters.setState(preferences.getOverWriteFanartAndPostersPreference());
		overwriteFanartAndPosters.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					preferences.setOverWriteFanartAndPostersPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					preferences.setOverWriteFanartAndPostersPreference(false);
				
			}
		});
		preferenceMenu.add(overwriteFanartAndPosters);
		
		//Checkbox for overwriting writing actors to .actor folder
				JCheckBoxMenuItem writeActorImages = new JCheckBoxMenuItem("Write Actor Images");
				writeActorImages.setState(preferences.getDownloadActorImagesToActorFolderPreference());
				writeActorImages.addItemListener(new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent e) {
						//save the menu choice off to the preference object (and the disk based settings file)
						if(e.getStateChange() == ItemEvent.SELECTED)
							preferences.setDownloadActorImagesToActorFolderPreference(true);
						else if(e.getStateChange() == ItemEvent.DESELECTED)
							preferences.setDownloadActorImagesToActorFolderPreference(false);
						
					}
				});
				preferenceMenu.add(writeActorImages);
		
		//add the various menus together
		menuBar.add(preferenceMenu);
		frmMoviescraper.setJMenuBar(menuBar);
	}
	protected void removeOldScrapedMovieReferences() {
		currentlySelectedMovieDMM = null;
		currentlySelectedMovieActionJav = null;
		currentlySelectedMovieSquarePlus = null;
		currentlySelectedMovieJavLibrary = null;
		currentlySelectedMovieJavZoo = null;

	}

	private void updateFileListModel(File currentlySelectedDirectory) {
		filesToList = showFileListSorted(currentlySelectedDirectory);
		listModelFiles.removeAllElements();
		for (File file : filesToList) {
			listModelFiles.addElement(file);
		}

	}

	private File[] showFileListSorted(File currentlySelectedDirectory) {

		File[] sortedList = currentlySelectedDirectory.listFiles();
		//Make a comparator so we get alphabetic order, with all directories first, then all the files (Like Windows Explorer)
		Comparator<File> comp = new Comparator<File>() {
			public int compare(File file1, File file2) {
				// Directory before non-directory
				if (file1.isDirectory() && !file2.isDirectory()) {

					return -1;
				}
				// Non-directory after directory
				else if (!file1.isDirectory() && file2.isDirectory()) {

					return 1;
				}
				// Alphabetic order otherwise
				else {

					return file1.compareTo(file2);
				}
			}
		};
		Arrays.sort(sortedList, comp);
		return sortedList;
	}
	
	//try to fill in any holes in thumbnails from the sourceMovie by looking through movieToGetExtraInfoFrom and see if has them
	//TODO: debug this
	private ArrayList<Actor> amalgamateActor(Movie sourceMovie, Movie movieToGetExtraInfoFrom)
	{
		ArrayList<Actor> amalgamatedActorList = new ArrayList<Actor>();
		boolean changeMade = false;
		if(sourceMovie.getActors() != null && movieToGetExtraInfoFrom.getActors() != null)
		{
			for(Actor currentActor : sourceMovie.getActors())
			{
				if(currentActor.getThumb() == null || currentActor.getThumb().getThumbURL().getPath().length() < 1)
				{
					//Found an actor with no thumbnail in sourceMovie
					for(Actor extraMovieActor: movieToGetExtraInfoFrom.getActors())
					{
						//scan through other movie and find actor with same name as the one we are currently on
						if(currentActor.getName().equals(extraMovieActor.getName()) && (extraMovieActor.getThumb() != null) && extraMovieActor.getThumb().getThumbURL().getPath().length() > 1)
						{
							currentActor = extraMovieActor;
							changeMade = true;
						}
					}
				}
				amalgamatedActorList.add(currentActor);
			}
		}
		if(changeMade)
		{
			return amalgamatedActorList;
		}
		else return sourceMovie.getActors(); // we didn't find any changes needed so just return the source movie's actor list
	}
	// Look through the fields in the various scraped movies and try to
	// automatically guess what the best data is and construct a Movie based on
	// that
	protected Movie amalgamateMovie(Movie currentlySelectedMovieDMM,
			Movie currentlySelectedMovieActionJav,
			Movie currentlySelectedMovieSquarePlus,
			Movie currentlySelectedMovieJavLibrary,
			Movie currentlySelectedMovieJavZoo) {

		if (currentlySelectedMovieDMM == null
				&& currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null
				&& currentlySelectedMovieJavLibrary == null)
			return null;
		// the case when i'm reading in a movie from a nfo file
		else if (movieToWriteToDisk != null
				&& currentlySelectedMovieDMM == null
				&& currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null
				&& currentlySelectedMovieJavLibrary == null) {
			return movieToWriteToDisk;
		} else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null
				&& (currentlySelectedMovieActionJav != null || currentlySelectedMovieSquarePlus != null || currentlySelectedMovieJavZoo != null)) {
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
					.setOriginalTitle(currentlySelectedMovieDMM
							.getOriginalTitle());
			currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieDMM.getSet());
			// grabbing the things from ActionJav which tend to be high quality
			// info
			if (currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getPlot() != null
					&& currentlySelectedMovieActionJav.getPlot().getPlot()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setPlot(currentlySelectedMovieActionJav.getPlot());
			if (currentlySelectedMovieSquarePlus != null
					&& currentlySelectedMovieSquarePlus.getTitle() != null
					&& currentlySelectedMovieSquarePlus.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieSquarePlus.getTitle());
			if (currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getTitle() != null
					&& currentlySelectedMovieActionJav.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieActionJav.getTitle());
			if (currentlySelectedMovieJavLibrary.getActors().size() == 0
					&& currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getActors().size() > 0)
				currentlySelectedMovieJavLibrary
						.setActors(currentlySelectedMovieActionJav.getActors());
			currentlySelectedMovieJavLibrary.setFanart(currentlySelectedMovieDMM.getFanart());
			currentlySelectedMovieJavLibrary.setPosters(currentlySelectedMovieDMM.getPosters());
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			return currentlySelectedMovieJavLibrary;
		}

		else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null
				&& currentlySelectedMovieActionJav != null) {
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
					.setOriginalTitle(currentlySelectedMovieDMM
							.getOriginalTitle());
			currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieDMM.getSet());
			// grabbing the things from ActionJav which tend to be high quality
			// info
			if (currentlySelectedMovieActionJav.getPlot() != null
					&& currentlySelectedMovieActionJav.getPlot().getPlot()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setPlot(currentlySelectedMovieActionJav.getPlot());
			if (currentlySelectedMovieActionJav.getTitle() != null
					&& currentlySelectedMovieActionJav.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieActionJav.getTitle());
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			return currentlySelectedMovieJavLibrary;
		}

		else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null) {
			//System.out.println("Return Jav Lib movie with DMM Plot");
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
					.setOriginalTitle(currentlySelectedMovieDMM
							.getOriginalTitle());
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			return currentlySelectedMovieJavLibrary;
		}
		// DMM was not found but JavLibrary was? This shouldn't really happen
		// too often...
		else if (currentlySelectedMovieJavLibrary != null) {
			//System.out.println("Return Jav Lib movie");
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			return currentlySelectedMovieJavLibrary;
		}

		// Nothing on either squareplus or actionjav or JavLibrary, so just
		// return the DMM info
		else if (currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null) {
			// System.out.println("Adding in id number to title (DMM Only Case): "
			// + currentlySelectedMovieDMM.getId());
			// currentlySelectedMovieDMM.setTitle(new
			// Title(currentlySelectedMovieDMM.getTitle().getTitle() + " (" +
			// currentlySelectedMovieDMM.getId() + ")"));
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieDMM.setSet(currentlySelectedMovieJavZoo.getSet());
			return currentlySelectedMovieDMM;
		}
		// ActionJav found and SquarePlus not
		else if (currentlySelectedMovieActionJav != null
				&& currentlySelectedMovieSquarePlus == null) {
			//System.out.println("ActionJav found and SquarePlus not");
			ArrayList<Actor> actorsToUse = (currentlySelectedMovieActionJav
					.getActors().size() > 0 && currentlySelectedMovieActionJav
					.getActors().size() >= currentlySelectedMovieDMM
					.getActors().size()) ? currentlySelectedMovieActionJav
					.getActors() : currentlySelectedMovieDMM.getActors();
			ArrayList<Director> directorsToUse = (currentlySelectedMovieActionJav
					.getDirectors().size() > 0) ? currentlySelectedMovieActionJav
					.getDirectors() : currentlySelectedMovieDMM.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			ArrayList<Genre> genresToUse = (currentlySelectedMovieActionJav
					.getGenres().size() > 1) ? currentlySelectedMovieActionJav
					.getGenres() : currentlySelectedMovieDMM.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = (currentlySelectedMovieActionJav.getPlot()
					.getPlot().length() > 1) ? currentlySelectedMovieActionJav
					.getPlot() : currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Title titleToUse = (currentlySelectedMovieActionJav.getTitle()
					.getTitle().length() > 1) ? currentlySelectedMovieActionJav
					.getTitle() : currentlySelectedMovieDMM.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = (currentlySelectedMovieActionJav
					.getRuntime().getRuntime().length() > 1) ? currentlySelectedMovieActionJav
					.getRuntime() : currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = (currentlySelectedMovieActionJav.getStudio()
					.getStudio().length() > 1) ? currentlySelectedMovieActionJav
					.getStudio() : currentlySelectedMovieDMM.getStudio();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, genresToUse, idsToUse, mpaaToUse,
					originalTitleToUse, outlineToUse, plotToUse, postersToUse,
					ratingToUse, runtimeToUse, setToUse, sortTitleToUse,
					studioToUse, taglineToUse, titleToUse, top250ToUse,
					votesToUse, yearToUse);
			return amalgamatedMovie;
		}
		// Squareplus found something, actionjav did not
		else if (currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus != null) {
			//System.out.println("Squareplus found something, actionjav did not");
			ArrayList<Actor> actorsToUse = currentlySelectedMovieDMM
					.getActors();
			ArrayList<Director> directorsToUse = currentlySelectedMovieDMM
					.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			ArrayList<Genre> genresToUse = currentlySelectedMovieDMM
					.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Title titleToUse = currentlySelectedMovieSquarePlus.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = currentlySelectedMovieDMM.getStudio();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, genresToUse, idsToUse, mpaaToUse,
					originalTitleToUse, outlineToUse, plotToUse, postersToUse,
					ratingToUse, runtimeToUse, setToUse, sortTitleToUse,
					studioToUse, taglineToUse, titleToUse, top250ToUse,
					votesToUse, yearToUse);
			return amalgamatedMovie;
		} else // amalgamate from all 3 sources
		{
			//System.out.println("amalgamate from all 3 sources");
			ArrayList<Actor> actorsToUse = (currentlySelectedMovieActionJav
					.getActors().size() > 0 && currentlySelectedMovieActionJav
					.getActors().size() >= currentlySelectedMovieDMM
					.getActors().size()) ? currentlySelectedMovieActionJav
					.getActors() : currentlySelectedMovieDMM.getActors();
			ArrayList<Director> directorsToUse = (currentlySelectedMovieActionJav
					.getDirectors().size() > 0) ? currentlySelectedMovieActionJav
					.getDirectors() : currentlySelectedMovieDMM.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			ArrayList<Genre> genresToUse = (currentlySelectedMovieActionJav
					.getGenres().size() > 1) ? currentlySelectedMovieActionJav
					.getGenres() : currentlySelectedMovieDMM.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = (currentlySelectedMovieActionJav.getPlot()
					.getPlot().length() > 1) ? currentlySelectedMovieActionJav
					.getPlot() : currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Title titleToUse = currentlySelectedMovieActionJav.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = (currentlySelectedMovieActionJav
					.getRuntime().getRuntime().length() > 1) ? currentlySelectedMovieActionJav
					.getRuntime() : currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = (currentlySelectedMovieActionJav.getStudio()
					.getStudio().length() > 1) ? currentlySelectedMovieActionJav
					.getStudio() : currentlySelectedMovieDMM.getStudio();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, genresToUse, idsToUse, mpaaToUse,
					originalTitleToUse, outlineToUse, plotToUse, postersToUse,
					ratingToUse, runtimeToUse, setToUse, sortTitleToUse,
					studioToUse, taglineToUse, titleToUse, top250ToUse,
					votesToUse, yearToUse);
			return amalgamatedMovie;
		}

	}

	protected void readMovieFromNfoFile(File nfoFile) {
		FileInputStream fisTargetFile;
		try {
			fisTargetFile = new FileInputStream(nfoFile);
			String targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
			//Sometimes there's some junk before the prolog tag. Do a workaround to remove that junk.
			//This really isn't the cleanest way to do this, but it'll work for now
			//check first to make sure the string even contains <?xml so we don't loop through an invalid file needlessly
			if(targetFileStr.contains("<?xml"))
			{
				while(targetFileStr.length() > 0 && !targetFileStr.startsWith("<?xml"))
				{
					if(targetFileStr.length() > 1)
					{
					targetFileStr = targetFileStr.substring(1,targetFileStr.length());
					}
					else break;
				}
			}
			movieToWriteToDisk = XbmcXmlMovieBean.makeFromXML(targetFileStr)
					.toMovie();
			fisTargetFile.close();
			if (currentlySelectedPosterFile.exists()) {
				// do nothing for now
			}

			// The poster read from the URL is not resized. Let's do a resize
			// now.
			else if (movieToWriteToDisk.hasPoster()) {
				Thumb[] currentPosters = movieToWriteToDisk.getPosters();
				currentPosters[0] = new Thumb(currentPosters[0].getThumbURL()
						.toString(), 52.7, 0, 0, 0);
			}
			updateAllFieldsOfSite1Movie();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			
		}
	}

	protected void clearAllFieldsOfSite1Movie() {
		comboBoxMovieTitleText.removeAllItems();
		comboBoxMovieTitleText.setEditable(false);
		lblOriginalTitleTextSite1.setText("");
		lblYearGoesHere.setText("");
		lblIDCurrentMovie.setText("");
		listModelGenresSite1.removeAllElements();
		listModelActorsSite1.removeAllElements();
		lblPosterIcon.setIcon(null);
		moviePlotTextField.setText("");
		txtFieldMovieSet.setText("");

	}

	protected void updateAllFieldsOfSite1Movie() {
		if (movieToWriteToDisk == null) {
			clearAllFieldsOfSite1Movie();
		} else if (movieToWriteToDisk != null) {
			clearAllFieldsOfSite1Movie();

			if(movieToWriteToDisk != null)
				comboBoxMovieTitleText.addItem(movieToWriteToDisk.getTitle().getTitle());
			if(currentlySelectedMovieDMM != null)
				comboBoxMovieTitleText.addItem(currentlySelectedMovieDMM.getTitle().getTitle());
			if(currentlySelectedMovieJavLibrary != null)
				comboBoxMovieTitleText.addItem(currentlySelectedMovieJavLibrary.getTitle().getTitle());
			if(currentlySelectedMovieSquarePlus != null)
				comboBoxMovieTitleText.addItem(currentlySelectedMovieSquarePlus.getTitle().getTitle());
			if(currentlySelectedMovieActionJav != null)
				comboBoxMovieTitleText.addItem(currentlySelectedMovieActionJav.getTitle().getTitle());
			if(currentlySelectedMovieJavZoo != null)
				comboBoxMovieTitleText.addItem(currentlySelectedMovieJavZoo.getTitle().getTitle());
			if(comboBoxMovieTitleText.getItemCount() > 0)
				comboBoxMovieTitleText.setEditable(true);
			lblOriginalTitleTextSite1.setText(movieToWriteToDisk
					.getOriginalTitle().getOriginalTitle());
			if(movieToWriteToDisk.getId() != null)
				lblIDCurrentMovie.setText(movieToWriteToDisk.getId().getId());
			if(movieToWriteToDisk.getYear() != null)
			lblYearGoesHere.setText(movieToWriteToDisk.getYear().getYear());
			if(movieToWriteToDisk.getPlot() != null)
				moviePlotTextField.setText(movieToWriteToDisk.getPlot().getPlot());
			if(movieToWriteToDisk.getSet() != null)
				txtFieldMovieSet.setText(movieToWriteToDisk.getSet().getSet());
			// clear out any old genres
			listModelGenresSite1.removeAllElements();
			for (Genre genre : movieToWriteToDisk.getGenres()) {
				listModelGenresSite1.addElement(genre.getGenre());
			}
			listModelActorsSite1.removeAllElements();
			for (Actor actor : movieToWriteToDisk.getActors()) {
				listModelActorsSite1.addElement(actor.getName());

			}

			// TODO Maybe sort the genres after adding them all

			// try to get the poster from a local file, if it exists
			if (currentlySelectedPosterFile.exists()) {
				try {
					lblPosterIcon.setIcon(new ImageIcon(
							currentlySelectedPosterFile.getCanonicalPath()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// otherwise read it from the URL specified by the object
			else if (movieToWriteToDisk.hasPoster()) {
				try {
					posterImage = movieToWriteToDisk.getPosters()[0]
							.getThumbImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
				}
				lblPosterIcon.setIcon(new ImageIcon(posterImage));
			}
		}
	}



	private class OpenFileAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if (currentlySelectedMovieFile != null) {
				try {
					Desktop.getDesktop().open(currentlySelectedMovieFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
				}
			}

		}
	}

	private class WriteFileDataAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			try {
				// Write the user or automatic selection using amalgamation
				// of different scraping sites
				if(movieToWriteToDisk == null)
				{
					Movie amalgamationAutoPickMovie = amalgamateMovie(
							currentlySelectedMovieDMM,
							currentlySelectedMovieActionJav,
							currentlySelectedMovieSquarePlus,
							currentlySelectedMovieJavLibrary,
							currentlySelectedMovieJavZoo);
					
					movieToWriteToDisk = amalgamationAutoPickMovie;
				}
				System.out.println("Writing this movie to file: "
						+ movieToWriteToDisk);
				if(movieToWriteToDisk != null)
				{
					movieToWriteToDisk.writeToFile(
							currentlySelectedNfoFile,
							currentlySelectedPosterFile,
							currentlySelectedFanartFile, preferences.getWriteFanartAndPostersPreference(), preferences.getWriteFanartAndPostersPreference(), preferences.getOverWriteFanartAndPostersPreference(), preferences.getOverWriteFanartAndPostersPreference());
					//we're outputting new files to the current visible directory, so we'll want to update GUI with the fact that they are there
					if(!currentlySelectedMovieFile.isDirectory())
					{
						int selectedIndex = fileList.getSelectedIndex();
						if(!listModelFiles.contains(currentlySelectedNfoFile))
							listModelFiles.add(selectedIndex + 1,
								currentlySelectedNfoFile);
						if(!listModelFiles.contains(currentlySelectedFanartFile))
							listModelFiles.add(selectedIndex + 2,
								currentlySelectedFanartFile);
						if(!listModelFiles.contains(currentlySelectedPosterFile))
							listModelFiles.add(selectedIndex + 3,
								currentlySelectedPosterFile);
					}
				}
				//now write out the actor images if the user preference is set
				if(preferences.getDownloadActorImagesToActorFolderPreference() && currentlySelectedMovieFile != null && currentlySelectedDirectory != null)
				{
					updateActorsFolder();
/*					actorsFolder = null;
					if(currentlySelectedMovieFile.isDirectory())
					{
						actorsFolder = new File(currentlySelectedMovieFile.getPath() + "\\.actors");
					}
					else if(currentlySelectedMovieFile.isFile())
					{
						actorsFolder = new File(currentlySelectedDirectory.getPath() + "\\.actors");
					}*/
					//Don't create an empty .actors folder with no actors underneath it
					if(movieToWriteToDisk.hasAtLeastOneActorThumbnail() && actorsFolder != null)
					{
						//File actorsFolder = new File(currentlySelectedMovieFile.getPath() + "\\.actors");
						FileUtils.forceMkdir(actorsFolder);
						//on windows this new folder should have the hidden attribute; on unix it is already "hidden" by having a . in front of the name
						Path path = actorsFolder.toPath();
						Boolean hidden = (Boolean) Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS);
						if (hidden != null && !hidden) {
							Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
						}
						
						for(Actor currentActor : movieToWriteToDisk.getActors())
						{
							String currentActorToFileName = currentActor.getName().replace(' ', '_');
							File fileNameToWrite = new File(actorsFolder.getPath() + "\\" + currentActorToFileName + ".jpg");
							currentActor.writeImageToFile(fileNameToWrite);
						}
						
					}
					
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class OpenDirectoryAction implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Movies", "avi", "mp4", "wmv", "flv", "mov", "rm");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(frmMoviescraper);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				currentlySelectedDirectory = chooser.getSelectedFile();
				
				//display a wait cursor while repopulating the list
				//as this can sometimes be slow
				try{
					frmMoviescraper.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					updateFileListModel(currentlySelectedDirectory);
				}
				finally
				{
					preferences.setLastUsedDirectory(currentlySelectedDirectory);
					frmMoviescraper.setCursor(Cursor.getDefaultCursor());
				}
				

			}
		}
	}

	private class SelectFileListAction implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {

				if (fileList.getSelectedIndex() == -1) {
					// No selection
					// Clear out old selections
					currentlySelectedNfoFile = null;
					currentlySelectedPosterFile = null;
					currentlySelectedFanartFile = null;
					currentlySelectedMovieFile = null;
					actorsFolder = null;
					// System.out.println("Selection nothing");

				} else {
					// Item is selected
					File selectedValue = fileList.getSelectedValue();
					currentlySelectedNfoFile = new File(Movie
							.getFileNameOfNfo(selectedValue));
					currentlySelectedPosterFile = new File(Movie
							.getFileNameOfPoster(selectedValue));
					currentlySelectedFanartFile = new File(Movie
							.getFileNameOfFanart(selectedValue));
					currentlySelectedMovieFile = selectedValue;
					updateActorsFolder();
					
					// clean up old scraped movie results from previous
					// selection
					removeOldScrapedMovieReferences();

					if (currentlySelectedNfoFile.exists()) {
						readMovieFromNfoFile(currentlySelectedNfoFile);
					}
				}
			}
		}
	}

	private class MoveToNewFolderAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2250733525782269006L;

		public MoveToNewFolderAction() {
			putValue(NAME, "MoveToNewFolder");
			putValue(SHORT_DESCRIPTION, "Move Selected Movie to New Folder");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String pathSeperator = System.getProperty("file.separator");
			try {
				if (currentlySelectedMovieFile != null
						&& currentlySelectedMovieFile.exists() && currentlySelectedMovieFile.isFile()) {
					// we can append the movie title to resulting folder name if
					// the movie is scraped, has an ID and generally matches the
					// ID in the filename (assuming the file is only named the
					// ID of the movie)
					String destinationDirectoryPrefix = "";
					if (movieToWriteToDisk != null) {
						String possibleID = movieToWriteToDisk.getId().getId()
								.toUpperCase();
						String possibleIDWithoutDash = possibleID.replaceFirst(
								"-", "");
						String fileNameComparingTo = FilenameUtils
								.getBaseName(currentlySelectedMovieFile
										.getName().toUpperCase());
						if (possibleID.equals(SiteParsingProfile.stripDiscNumber(fileNameComparingTo))
								|| possibleIDWithoutDash
										.equals(SiteParsingProfile.stripDiscNumber(fileNameComparingTo))) {
							destinationDirectoryPrefix = movieToWriteToDisk
									.getTitle().getTitle() + " - ";
							// replace illegal characters in the movie filename
							// prefix that the OS doesn't allow with blank space
							destinationDirectoryPrefix = destinationDirectoryPrefix
									.replace("^\\.+", "").replaceAll(
											"[\\\\/:*?\"<>|]", "");
						}

					}
					File destDir = new File(
							currentlySelectedMovieFile.getParentFile()
									.getCanonicalPath()
									+ pathSeperator
									+ destinationDirectoryPrefix
									+ SiteParsingProfile.stripDiscNumber(FilenameUtils
											.getBaseName(currentlySelectedMovieFile
													.getName())));
					clearAllFieldsOfSite1Movie();
					//copy over the .actor folder items to the destination folder, but only if the preference is set and the usual sanity checking is done
					if (currentlySelectedMovieFile.isFile() && actorsFolder != null && preferences.getDownloadActorImagesToActorFolderPreference())
					{
						File [] actorFilesToCopy = actorFolderFiles();
						File actorsFolderDestDir = new File(destDir.getPath() + "\\.actors");
						for(File currentFile : actorFilesToCopy)
						{
							FileUtils.copyFileToDirectory(currentFile, actorsFolderDestDir);
						}
					}
					if (currentlySelectedMovieFile.exists())
					{
						//In case of stacked movie files (Movies which are split into multiple files such AS CD1, CD2, etc) get the list of all files
						//which are part of this movie's stack
						File currentDirectory = currentlySelectedMovieFile.getParentFile();
						String currentlySelectedMovieFileWihoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(currentlySelectedMovieFile.getName()));
						if(currentDirectory != null)
						{

							for(File currentFile : currentDirectory.listFiles())
							{
								String currentFileNameWithoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(currentFile.getName()));
								if(currentFile.isFile() && currentFileNameWithoutStackSuffix.equals(currentlySelectedMovieFileWihoutStackSuffix))
								{
									//this should also get the nfo file as a nice side effect
									FileUtils.moveFileToDirectory(currentFile,destDir, true);
								}
							}
						}

					}
					if (currentlySelectedNfoFile.exists())
						FileUtils.moveFileToDirectory(currentlySelectedNfoFile,destDir, true);
					if (currentlySelectedPosterFile.exists()) {
						// we're doing copy and delete instead of move due to
						// occasional system lock bug I'm still working on
						FileUtils.moveFileToDirectory(currentlySelectedPosterFile, destDir, true);
					}
					if (currentlySelectedFanartFile.exists()) {
						FileUtils.moveFileToDirectory(currentlySelectedFanartFile, destDir, true);
					}
					

					// remove all the old references so we aren't tempted to
					// reuse them
					currentlySelectedNfoFile = null;
					currentlySelectedPosterFile = null;
					currentlySelectedFanartFile = null;
					currentlySelectedMovieFile = null;
					movieToWriteToDisk = null;
					actorsFolder = null;
					updateFileListModel(currentlySelectedDirectory);
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	
	private class ScrapeMovieAction extends AbstractAction {
		/**
		 * 
		 */
		String overrideURLDMM;
		String overrideURLJavLibrary;
		private static final long serialVersionUID = 1L;
		boolean promptUserForURLWhenScraping; //do we stop to ask the user to pick a URL when scraping
		
		public SearchResult showOptionPane(SearchResult [] searchResults, String siteName)
		{
			if(searchResults.length > 0)
			{
				JLabel [] options = new JLabel[searchResults.length];
				for(int i = 0; i < searchResults.length; i++)
				{
					JLabel currentLabel = new JLabel();
					currentLabel.setText(searchResults[i].getUrlPath());
					Thumb currentThumb = searchResults[i].getPreviewImage();
					if(currentThumb.getThumbURL() != null)
					{
						currentLabel.setIcon(currentThumb.getImageIconThumbImage());
					}
					options[i] = currentLabel;
				}
				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				JList<SearchResult> labelList = new JList<SearchResult>(searchResults);
				labelList.setCellRenderer(new SearchResultsRenderer());
				labelList.setVisible(true);
				JScrollPane pane = new JScrollPane(labelList);
				panel.add(pane, BorderLayout.CENTER);
				//JButton okButton = new JButton("OK");
				//panel.add(okButton,BorderLayout.SOUTH);
				panel.setPreferredSize(new Dimension(500,400));
				
				final JDialog bwin = new JDialog();
		         bwin.addWindowFocusListener(new WindowFocusListener()
		         {
		             @Override
		             public void windowLostFocus(WindowEvent e)
		             {
		               bwin.setVisible(false);
		               bwin.dispose();
		             }

		             @Override
		             public void windowGainedFocus(WindowEvent e)
		             {
		             }
		         }); 
		         
		         //bwin.setUndecorated(true);
		         bwin.add(panel);
		         bwin.pack();
				
				int result = JOptionPane.showOptionDialog(null, panel, "Select Movie to Scrape From " + siteName,
		                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
		                null, null, null);
				if(result == JOptionPane.OK_OPTION)
				{
					SearchResult optionPickedFromPanel = labelList.getSelectedValue();
					return optionPickedFromPanel;
				}
				else return null;
				/*SearchResult optionPicked = (SearchResult)JOptionPane.showInputDialog(null,
					    "Pick URL From List Below:",
					    "Scraping With Manual URL",
					    JOptionPane.PLAIN_MESSAGE,
					    null,     //do not use a custom Icon
					    searchResults,  //the titles of buttons
					    searchResults[0]); //default button title
					    
				return optionPicked;*/
			}
			else return null;
		}
		
		
		public ScrapeMovieAction() {
			putValue(NAME, "Scrape");
			putValue(SHORT_DESCRIPTION, "Scrape Selected Movie");
			overrideURLDMM = "";
			overrideURLJavLibrary = "";
			promptUserForURLWhenScraping = true;
		}

		public void actionPerformed(ActionEvent e) {
			//set the cursor to busy as this may take a while
			
			// We don't want to block the UI while waiting for a time consuming
			// scrape, so make new threads for each scraping query
			
			// clear out all old values of the scraped movie
			removeOldScrapedMovieReferences();


			if(promptUserForURLWhenScraping)
			{
				//bring up some dialog boxes so the user can choose what URL to use for each site
				try {
					DmmParsingProfile dmmPP = new DmmParsingProfile();
					JavLibraryParsingProfile jlPP = new JavLibraryParsingProfile();
					String searchStringDMM = dmmPP.createSearchString(currentlySelectedMovieFile);
					SearchResult [] searchResultsDMM = dmmPP.getSearchResults(searchStringDMM);
					String searchStringJL = jlPP.createSearchString(currentlySelectedMovieFile);
					if(searchResultsDMM != null && searchResultsDMM.length > 0)
					{
						SearchResult searchResultFromUser = this.showOptionPane(searchResultsDMM, "dmm.co.jp");
						if(searchResultFromUser == null)
							return;
						overrideURLDMM = searchResultFromUser.getUrlPath();

					}
					SearchResult [] searchResultsJavLibStrings = jlPP.getSearchResults(searchStringJL);
					if(searchResultsJavLibStrings != null && searchResultsJavLibStrings.length > 0)
					{
						SearchResult searchResultFromUser = this.showOptionPane(searchResultsJavLibStrings, "javlibrary.com");
						if(searchResultFromUser == null)
							return;
						overrideURLJavLibrary = searchResultFromUser.getUrlPath();				
					}
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			// Scape dmm.co.jp for currently selected movie
			Thread scrapeQueryDMMThread = new Thread() {
				public void run() {
					try {
						currentlySelectedMovieDMM = Movie.scrapeMovie(
								currentlySelectedMovieFile,
								new DmmParsingProfile(), overrideURLDMM, promptUserForURLWhenScraping);

						System.out.println("DMM scrape results: "
								+ currentlySelectedMovieDMM);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			// Scrape ActionJav.com for currently selected movie
			Thread scrapeQueryActionJavThread = new Thread() {
				public void run() {
					try {
						currentlySelectedMovieActionJav = Movie.scrapeMovie(
								currentlySelectedMovieFile,
								new ActionJavParsingProfile(), overrideURLDMM, false);

						System.out.println("Action jav scrape results: "
								+ currentlySelectedMovieActionJav);

					} catch (IOException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					}
				}
			};

			// Scrape SquarePlus.co.jp for currently selected movie
			Thread scrapeQuerySquarePlusThread = new Thread() {
				public void run() {
					try {
						currentlySelectedMovieSquarePlus = Movie.scrapeMovie(
								currentlySelectedMovieFile,
								new SquarePlusParsingProfile(), overrideURLDMM, false);

						System.out.println("SquarePlus scrape results: "
								+ currentlySelectedMovieSquarePlus);

					} catch (IOException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					}
				}
			};

			// Scrape JavLibrary for currently selected movie
			Thread scrapeQueryJavLibraryThread = new Thread() {
				public void run() {
					try {
						JavLibraryParsingProfile jlParsingProfile = new JavLibraryParsingProfile();
						jlParsingProfile.setOverrideURLJavLibrary(overrideURLJavLibrary);
						currentlySelectedMovieJavLibrary = Movie.scrapeMovie(
								currentlySelectedMovieFile,
								jlParsingProfile, overrideURLDMM, promptUserForURLWhenScraping);

						System.out.println("JavLibrary scrape results: "
								+ currentlySelectedMovieJavLibrary);

					} catch (IOException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			
			Thread scrapeQueryJavZooThread = new Thread() {
				public void run() {
					try {
						currentlySelectedMovieJavZoo = Movie.scrapeMovie(
								currentlySelectedMovieFile,
								new JavZooParsingProfile(), overrideURLDMM, false);

						System.out.println("JavZoo scrape results: "
								+ currentlySelectedMovieJavZoo);

					} catch (IOException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			
			
			try
			{
			// Run all the threads in parallel, put busy cursor on as this could take a while
			frmMoviescraper.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			scrapeQueryDMMThread.start();
			scrapeQueryActionJavThread.start();
			scrapeQuerySquarePlusThread.start();
			scrapeQueryJavLibraryThread.start();
			scrapeQueryJavZooThread.start();


			// wait for them to finish before updating gui

				scrapeQueryJavLibraryThread.join();
				scrapeQueryDMMThread.join();
				scrapeQueryActionJavThread.join();
				scrapeQuerySquarePlusThread.join();
				scrapeQueryJavZooThread.join();
				movieToWriteToDisk = amalgamateMovie(currentlySelectedMovieDMM,
						currentlySelectedMovieActionJav,
						currentlySelectedMovieSquarePlus,
						currentlySelectedMovieJavLibrary,
						currentlySelectedMovieJavZoo);
				if(movieToWriteToDisk == null)
				{
					System.out.println("No movie result found");
					JOptionPane.showMessageDialog(frmMoviescraper, "Could not find any movies that match the selected file while scraping.", "No Movies Found", JOptionPane.ERROR_MESSAGE, null);
				}
				//Let's clear out the actorsFolder so we can get new images from the scraped results instead of relying on whatever is there locally
				actorsFolder = null;
				updateAllFieldsOfSite1Movie();

			} catch (InterruptedException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
			finally
			{
				frmMoviescraper.setCursor(Cursor.getDefaultCursor());
			}

	}
	}
	private class ScrapeMovieActionAutomatic extends ScrapeMovieAction
	{
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ScrapeMovieActionAutomatic()
		 {
			super();
			putValue(NAME, "Scrape (Automatic)");
			putValue(SHORT_DESCRIPTION, "Scrape Selected Movie (Automatic)");
			promptUserForURLWhenScraping = false;
		 }
		public void actionPerformed(ActionEvent e){
			super.actionPerformed(e);
		}
	}
	class FileList {

		public JScrollPane getGui(File[] all,
				DefaultListModel<File> listModelFiles, boolean vertical) {

			//Gotta clear out the old list before we can populate it with new stuff
			fileList.removeAll();

			for (File file : all) {
				listModelFiles.addElement(file);
			}
			// ..then use a renderer
			fileList.setCellRenderer(new FileRenderer(!vertical));

			if (!vertical) {
				fileList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
				fileList.setVisibleRowCount(-1);
			} else {
				fileList.setVisibleRowCount(9);
			}
			return new JScrollPane(fileList);
		}
	}

	class FileRenderer extends DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private boolean pad;
		private Border padBorder = new EmptyBorder(3, 3, 3, 3);

		FileRenderer(boolean pad) {
			this.pad = pad;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			Component c = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
			JLabel l = (JLabel) c;
			File f = (File) value;
			l.setText(f.getName());
			l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));
			if (pad) {
				l.setBorder(padBorder);
			}

			return l;
		}
	}

	public class ActressListRenderer extends DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Font font = new Font("helvitica", Font.BOLD, 12);

		public ActressListRenderer() {
			setBorder(new EmptyBorder(1, 1, 1, 1));
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			label.setIcon(getImageIconForLabelName());
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setFont(font);
			label.setBorder(BorderFactory.createLineBorder(Color.black));
			if (index % 2 == 0) {
				label.setBackground(SystemColor.controlHighlight);
			} else {
				label.setBackground(SystemColor.controlLtHighlight);
			}
			return label;
		}
		
		//TODO: I should probably re-implement this to use Maps instead of arrays
		//TODO: Store the files from .actor in a cache somewhere
		private ImageIcon getImageIconForLabelName() {
			if (movieToWriteToDisk != null) {
				for (Actor currentActor : movieToWriteToDisk.getActors()) {
					if (this.getText().equals(currentActor.getName())) {
						if (currentActor.getThumb() != null)
						{
							//see if we can find a local copy in the .actors folder before trying to download
							if(actorsFolder != null && actorsFolder.isDirectory())
							{
								String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
								File [] listFiles = actorsFolder.listFiles();
								for(File currentFile : listFiles)
								{
									if(currentFile.isFile() && FilenameUtils.removeExtension(currentFile.getName()).equals(currentActorNameAsPotentialFileName)){										
										return new ImageIcon(currentFile.getPath());
									}
								}
							}

							else 
							{
								return currentActor.getThumb().getImageIconThumbImage();
							}
						}
						else
							return new ImageIcon();
					}
				}
			}
			return new ImageIcon();
		}
	}

	public void updateActorsFolder() {
		actorsFolder = null;
		if(currentlySelectedMovieFile.isDirectory())
		{
			actorsFolder = new File(currentlySelectedMovieFile.getPath() + "\\.actors");
		}
		else if(currentlySelectedMovieFile.isFile())
		{
			actorsFolder = new File(currentlySelectedDirectory.getPath() + "\\.actors");
		}
	}

	public File[] actorFolderFiles() {
		ArrayList<File> actorFiles = new ArrayList<File>();
		if(movieToWriteToDisk != null && movieToWriteToDisk.getActors() != null)
		{
			if(actorsFolder != null && actorsFolder.isDirectory())
			{
				for (Actor currentActor : movieToWriteToDisk.getActors())
				{
					String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
					File [] listFiles = actorsFolder.listFiles();
					for(File currentFile : listFiles)
					{
						if(currentFile.isFile() && FilenameUtils.removeExtension(currentFile.getName()).equals(currentActorNameAsPotentialFileName)){										
							actorFiles.add(currentFile);
						}
					}
				}
			}
		}
		return actorFiles.toArray(new File[actorFiles.size()]);
	}

}

