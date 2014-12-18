package moviescraper.doctord.GUI;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

import javax.swing.JList;

import moviescraper.doctord.IconCache;
import moviescraper.doctord.Movie;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.GUI.renderer.FileRenderer;
import moviescraper.doctord.controller.BrowseDirectoryAction;
import moviescraper.doctord.controller.FileNameCleanupAction;
import moviescraper.doctord.controller.MoveToNewFolderAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.RefreshDirectoryAction;
import moviescraper.doctord.controller.ScrapeMovieAction;
import moviescraper.doctord.controller.ScrapeMovieActionAutomatic;
import moviescraper.doctord.controller.ScrapeMovieActionData18Movie;
import moviescraper.doctord.controller.ScrapeMovieActionData18WebContent;
import moviescraper.doctord.controller.SelectFileListAction;
import moviescraper.doctord.controller.UpDirectoryAction;
import moviescraper.doctord.controller.WriteFileDataAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.preferences.MoviescraperPreferences;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JButton;

import java.awt.SystemColor;

import javax.swing.UIManager;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.event.ListSelectionListener;

public class GUIMain {

	//Objects Used to Keep Track of Program State
	private List<File> currentlySelectedNfoFileList;
	private List<File> currentlySelectedPosterFileList;
	private List<File> currentlySelectedFolderJpgFileList;
	private List<File> currentlySelectedFanartFileList;
	private List<File> currentlySelectedTrailerFileList;
	private List<File> currentlySelectedMovieFileList;
	private List<File> currentlySelectedActorsFolderList;
	private File currentlySelectedDirectoryList;
	private File defaultHomeDirectory;
	private MoviescraperPreferences preferences;

	//scraped movies
	private Movie currentlySelectedMovieDMM;
	private Movie currentlySelectedMovieActionJav;
	private Movie currentlySelectedMovieSquarePlus;
	private Movie currentlySelectedMovieJavLibrary;
	private Movie currentlySelectedMovieJavZoo;
	private Movie currentlySelectedMovieCaribbeancomPremium;
	private Movie currentlySelectedMovieData18Movie;
	public List <Movie> movieToWriteToDiskList;

	//Gui Elements
	JFrame frmMoviescraper;
	protected WindowBlocker frmMovieScraperBlocker;
	private final Action moveToNewFolder = new MoveToNewFolderAction(this);
	private DefaultListModel<File> listModelFiles;

	private JPanel fileListPanel;
	private FileDetailPanel fileDetailPanel;

	private JScrollPane fileListScrollPane;
	private JSplitPane fileListFileDetailSplitPane;
	private JList<File> fileList;
	private JFileChooser chooser;


	private ProgressMonitor progressMonitor;

	//variables for fileList
	private static int CHAR_DELTA = 1000;
	private String m_key;
	private long m_time;

	//Menus
	JMenuBar menuBar;
	JMenu preferenceMenu;

	//Dimensions of various elements
	private static final int iconSizeX = 16;
	private static final int iconSizeY = 16;
	private static final int defaultMainFrameX = 1024;
	private static final int defaultMainFrameY = 768;

	private final static boolean debugMessages = false;

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

	public void debugWriter(String message)
	{
		if(debugMessages)
			System.out.println(message);
	}



	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		setPreferences(new MoviescraperPreferences());
		setCurrentlySelectedNfoFileList(new ArrayList<File>());
		setCurrentlySelectedMovieFileList(new ArrayList<File>());
		setCurrentlySelectedPosterFileList(new ArrayList<File>());
		setCurrentlySelectedFolderJpgFileList(new ArrayList<File>());
		setCurrentlySelectedFanartFileList(new ArrayList<File>());
		setCurrentlySelectedTrailerFileList(new ArrayList<File>());
		currentlySelectedActorsFolderList = new ArrayList<File>();
		movieToWriteToDiskList = new ArrayList<Movie>();
		frmMoviescraper = new JFrame();
		frmMovieScraperBlocker = new WindowBlocker();
		//set up the window that sits above the frame and can block input to this frame if needed while a dialog is open
		frmMoviescraper.setGlassPane(frmMovieScraperBlocker);
		frmMoviescraper.setBackground(SystemColor.window);
		frmMoviescraper.setPreferredSize(new Dimension(defaultMainFrameX, defaultMainFrameY));
		frmMoviescraper.setTitle("JAVMovieScraper");
		frmMoviescraper.setBounds(100, 100, defaultMainFrameX, defaultMainFrameY);
		frmMoviescraper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//create tree view icon provider
		IconCache.setIconProvider(getPreferences().getUseContentBasedTypeIcons() ? IconCache.IconProviderType.CONTENT
						: IconCache.IconProviderType.SYSTEM);

		//initialize the icons used in the program
		URL programIconURL = frmMoviescraper.getClass().getResource("/res/AppIcon.png");
		URL saveButtonIconURL = frmMoviescraper.getClass().getResource("/res/SaveButtonIcon.png");
		URL data18IconURL = frmMoviescraper.getClass().getResource("/res/Data18Icon.png");
		URL japanIconURL = frmMoviescraper.getClass().getResource("/res/JapanIcon.png");
		URL openIconURL = frmMoviescraper.getClass().getResource("/res/OpenIcon.png");
		URL fileFolderIconURL = frmMoviescraper.getClass().getResource("/res/FileFolderIcon.png");
		URL upIconURL = frmMoviescraper.getClass().getResource("/res/UpIcon.png");
		URL browseIconURL = frmMoviescraper.getClass().getResource("/res/BrowseDirectoryIcon.png");
		URL refreshIconURL = frmMoviescraper.getClass().getResource("/res/RefreshIcon.png");
		URL fixFileNameIconURL = frmMoviescraper.getClass().getResource("/res/FixFileNameIcon.png");

		//Used for icon in the title bar
		Image programIcon = null;
		try {
			programIcon = ImageIO.read(programIconURL);
			if(programIcon != null)
				frmMoviescraper.setIconImage(programIcon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//used in the write to file button
		ImageIcon saveIcon = initializeImageIcon(saveButtonIconURL);

		//used in the scrape data18 buttons
		ImageIcon data18Icon = initializeImageIcon(data18IconURL);

		//used for scraping japanese movies
		ImageIcon japanIcon = initializeImageIcon(japanIconURL);

		//open the file icon
		ImageIcon openIcon = initializeImageIcon(openIconURL);

		//move to new folder icon
		ImageIcon moveToFolderIcon = initializeImageIcon(fileFolderIconURL);

		//up one folder icon
		ImageIcon upIcon = initializeImageIcon(upIconURL);

		//browse directory icon
		ImageIcon browseDirectoryIcon = initializeImageIcon(browseIconURL);
		
		//refresh directory icon
		ImageIcon refreshDirectoryIcon = initializeImageIcon(refreshIconURL); 
		
		//Fix file name icon
		ImageIcon fixFileNameIcon = initializeImageIcon(fixFileNameIconURL);
		
		//Set up the file list panel - the panel where the user picks what file to scrape
		setUpFileListPanel(upIcon, browseDirectoryIcon, refreshDirectoryIcon);

		JPanel southPanel = new JPanel();
		southPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		frmMoviescraper.getContentPane().add(southPanel, BorderLayout.SOUTH);

		JComponent parserPanel = new SpecificParserPanel(this);
		parserPanel.setPreferredSize(new Dimension(200,50));
		southPanel.add(parserPanel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.PAGE_AXIS));
		JPanel scrapeButtons = new JPanel();
		JPanel fileOperationsButtons = new JPanel();
		southPanel.add(buttonsPanel);

		JButton btnScrapeSelectMovieJAV = new JButton("Scrape JAV");
		btnScrapeSelectMovieJAV.setAction(new ScrapeMovieAction(this));
		btnScrapeSelectMovieJAV.setIcon(japanIcon);
		scrapeButtons.add(btnScrapeSelectMovieJAV);

		JButton btnScrapeSelectMovieJAVAutomatic = new JButton("Scrape JAV (Automatic)");
		btnScrapeSelectMovieJAVAutomatic.setAction(new ScrapeMovieActionAutomatic(this));
		btnScrapeSelectMovieJAVAutomatic.setIcon(japanIcon);
		scrapeButtons.add(btnScrapeSelectMovieJAVAutomatic);

		JButton btnScrapeSelectMovieData18Movie = new JButton("Scrape Data18 Movie");
		btnScrapeSelectMovieData18Movie.setAction(new ScrapeMovieActionData18Movie(this));
		if(data18Icon != null)
			btnScrapeSelectMovieData18Movie.setIcon(data18Icon);
		scrapeButtons.add(btnScrapeSelectMovieData18Movie);

		JButton btnScrapeSelectMovieData18WebContent = new JButton("Scrape Data18 Web Content");
		btnScrapeSelectMovieData18WebContent.setAction(new ScrapeMovieActionData18WebContent(this));
		if(data18Icon != null)
			btnScrapeSelectMovieData18WebContent.setIcon(data18Icon);
		scrapeButtons.add(btnScrapeSelectMovieData18WebContent);

		JButton btnWriteFileData = new JButton("Write File Data");
		btnWriteFileData.setToolTipText("Write out the .nfo file to disk");
		if(saveIcon != null)
			btnWriteFileData.setIcon(saveIcon);
		btnWriteFileData.addActionListener(new WriteFileDataAction(this));
		fileOperationsButtons.add(btnWriteFileData);

		JButton btnMoveFileToFolder = new JButton("Move file to folder");
		btnMoveFileToFolder.setAction(moveToNewFolder);
		btnMoveFileToFolder.setToolTipText("Create a folder for the file and put the file and any associated files in that new folder.");
		btnMoveFileToFolder.setIcon(moveToFolderIcon);
		fileOperationsButtons.add(btnMoveFileToFolder);

		JButton openCurrentlySelectedFileButton = new JButton(
				"Open File");
		openCurrentlySelectedFileButton.setToolTipText("Open the currently selected file with the system default program for it");
		openCurrentlySelectedFileButton.addActionListener(new OpenFileAction(this));
		openCurrentlySelectedFileButton.setIcon(openIcon);
		fileOperationsButtons.add(openCurrentlySelectedFileButton);
		
		JButton fileNameCleanupButton = new JButton("File Name Cleanup (Experimental Feature)");
		fileNameCleanupButton
				.setToolTipText("Attempts to rename a file of a web content release before scraping so that it is more likely to find a match. I'm still working on adding more site abbreviations, so this feature is experimental for now.");
		fileNameCleanupButton.setIcon(fixFileNameIcon);
		fileNameCleanupButton.addActionListener(new FileNameCleanupAction(this));
		fileOperationsButtons.add(fileNameCleanupButton);

		buttonsPanel.add(scrapeButtons);
		buttonsPanel.add(fileOperationsButtons);
		
		//add in the menu bar
		frmMoviescraper.setJMenuBar(new GUIMainMenuBar(this));
	}

	/**
	 * @param upIcon
	 * @param browseDirectoryIcon
	 * @param refreshDirectoryIcon
	 */
	private void setUpFileListPanel(ImageIcon upIcon,
			ImageIcon browseDirectoryIcon, ImageIcon refreshDirectoryIcon) {
		fileListPanel = new JPanel();

		defaultHomeDirectory = getPreferences().getLastUsedDirectory();
		setCurrentlySelectedDirectoryList(defaultHomeDirectory);
		FileList fl = new FileList();

		listModelFiles = new DefaultListModel<File>();
		setFileList(new JList<File>(listModelFiles));

		//add in a keyListener so that you can start typing letters in the list and it will take you to that item in the list
		//if you type the second letter within CHAR_DELTA amount of time that will count as the Nth letter of the search
		//instead of the first
		getFileList().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//do nothing until the key is released

			}

			@Override
			public void keyReleased(KeyEvent e) {
				char ch = e.getKeyChar();

				// ignore searches for non alpha-numeric characters
				if (!Character.isLetterOrDigit(ch)) {
					return;
				}

				// reset string if too much time has elapsed
				if (m_time + CHAR_DELTA < System.currentTimeMillis()) {
					m_key = "";
				}

				m_time = System.currentTimeMillis();
				m_key += Character.toLowerCase(ch);

				// Iterate through items in the list until a matching prefix is found.
				// This technique is fine for small lists, however, doing a linear
				// search over a very large list with additional string manipulation
				// (eg: toLowerCase) within the tight loop would be quite slow.
				// In that case, pre-processing the case-conversions, and storing the
				// strings in a more search-efficient data structure such as a Trie
				// or a Ternary Search Tree would lead to much faster find.
				for (int i = 0; i < getFileList().getModel().getSize(); i++) {
					String str = getFileList().getModel().getElementAt(i).getName()
							.toString().toLowerCase();
					if (str.startsWith(m_key)) {
						getFileList().setSelectedIndex(i); // change selected item in list
						getFileList().ensureIndexIsVisible(i); // change listbox
						// scroll-position
						break;
					}

				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				//do nothing until the key is released
			}
		});

		//add mouse listener for double click
		getFileList().addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getClickCount() >= 2){
					@SuppressWarnings("unchecked")
					JList<File> theList = (JList<File>) e.getSource();
					try {
						File doubleClickedFile  = theList.getSelectedValue();
						if(doubleClickedFile != null && doubleClickedFile.exists() && doubleClickedFile.isDirectory())
						{
							try{
								setCurrentlySelectedDirectoryList(doubleClickedFile);
								frmMoviescraper.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								updateFileListModel(getCurrentlySelectedDirectoryList(), false);
							}
							finally
							{
								getPreferences().setLastUsedDirectory(getCurrentlySelectedDirectoryList());
								frmMoviescraper.setCursor(Cursor.getDefaultCursor());
							}
						}
						else
						{
							Desktop.getDesktop().open(theList.getSelectedValue());
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		getFileList().addListSelectionListener(new SelectFileListAction(this));
		fileListScrollPane = fl.getGui(
				showFileListSorted(getCurrentlySelectedDirectoryList()), listModelFiles,
				true);
		fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
		fileListPanel.add(fileListScrollPane);
		fileListPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		//set up buttons in the file panel

		JPanel fileListPanelButtonsPanel = new JPanel();
		fileListPanelButtonsPanel.setLayout( new BoxLayout(fileListPanelButtonsPanel, BoxLayout.X_AXIS));
		fileListPanelButtonsPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		fileListPanelButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		//Button to go up a directory for the current directory
		JButton btnUpDirectory = new JButton();
		btnUpDirectory.addActionListener(new UpDirectoryAction(this));
		btnUpDirectory.setIcon(upIcon);
		btnUpDirectory.setAlignmentX(Component.CENTER_ALIGNMENT);

		//Button to bring up a file chooser so the user can browse and pick what directory they want to view
		JButton btnBrowseDirectory = new JButton("Browse");
		btnBrowseDirectory.addActionListener(new BrowseDirectoryAction(this));
		btnBrowseDirectory.setIcon(browseDirectoryIcon);
		btnBrowseDirectory.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Button to refresh the current directory
		JButton btnRefreshDirectory = new JButton();
		btnRefreshDirectory.addActionListener(new RefreshDirectoryAction(this));
		btnRefreshDirectory.setIcon(refreshDirectoryIcon);
		btnRefreshDirectory.setAlignmentX(Component.CENTER_ALIGNMENT);

		fileListPanelButtonsPanel.add(btnRefreshDirectory);

		fileListPanelButtonsPanel.add(btnUpDirectory);
		fileListPanelButtonsPanel.add(btnBrowseDirectory);
		fileListPanel.add(fileListPanelButtonsPanel);
		

		fileDetailPanel = new FileDetailPanel(getPreferences(), this);
		JScrollPane fileDetailsScrollPane = new JScrollPane(fileDetailPanel);
		
		fileListFileDetailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileListPanel, fileDetailsScrollPane);
		fileListPanel.setMinimumSize(new Dimension(200,50));
		fileDetailsScrollPane.setMinimumSize(new Dimension(100,50));
		frmMoviescraper.getContentPane().add(fileListFileDetailSplitPane, BorderLayout.CENTER);
	}

	private ImageIcon initializeImageIcon(URL url){
		try {
			BufferedImage iconBufferedImage = ImageIO.read(url);
			if(iconBufferedImage != null)
			{
				iconBufferedImage = Scalr.resize(iconBufferedImage, Method.QUALITY, iconSizeX, iconSizeY, Scalr.OP_ANTIALIAS);
				return new ImageIcon(iconBufferedImage);
			}
			else return new ImageIcon();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}

	public void removeOldScrapedMovieReferences() {
		setCurrentlySelectedMovieDMM(null);
		setCurrentlySelectedMovieActionJav(null);
		setCurrentlySelectedMovieSquarePlus(null);
		setCurrentlySelectedMovieJavLibrary(null);
		setCurrentlySelectedMovieJavZoo(null);
		setCurrentlySelectedMovieCaribbeancomPremium(null);
		setCurrentlySelectedMovieData18Movie(null);
		if(movieToWriteToDiskList != null)
			movieToWriteToDiskList.clear();
	}
	public void removeOldSelectedFileReferences(){
		getCurrentlySelectedNfoFileList().clear();
		getCurrentlySelectedMovieFileList().clear();
		getCurrentlySelectedActorsFolderList().clear();
		getCurrentlySelectedPosterFileList().clear();
		getCurrentlySelectedFolderJpgFileList().clear();
		getCurrentlySelectedFanartFileList().clear();
		getCurrentlySelectedTrailerFileList().clear();
		getMovieToWriteToDiskList().clear();
	}

	public void updateFileListModel(File currentlySelectedDirectory, boolean keepSelectionsAndReferences) {
		try{
			getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			File [] filesToList = showFileListSorted(currentlySelectedDirectory);
			List<File> selectValuesListBeforeUpdate = getFileList().getSelectedValuesList();

			//We don't want to fire the listeners events when reselecting the items because this 
			//will cause us additional IO that is not needed as the program rereads the nfo.
			//To avoid this, we can save out the old listener, remove it, select the items and then add it back
			ListSelectionListener[] fileListSelectionListener = null;
			if(keepSelectionsAndReferences)
			{
				fileListSelectionListener = getFileList().getListSelectionListeners();
				getFileList().removeListSelectionListener(getFileList().getListSelectionListeners()[0]);;
			}
			listModelFiles.removeAllElements();
			for (File file : filesToList) {
				listModelFiles.addElement(file);
			}
			if(!keepSelectionsAndReferences)
			{
				removeOldScrapedMovieReferences();
				removeOldSelectedFileReferences();
			}
			//select the old values we had before we updated the list
			for(File currentValueToSelect : selectValuesListBeforeUpdate)
			{
				getFileList().setSelectedValue(currentValueToSelect, false);
			}
			if(keepSelectionsAndReferences && fileListSelectionListener != null)
			{
				getFileList().addListSelectionListener(fileListSelectionListener[0]);
			}
		}
		finally
		{
			getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
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


	

	public void clearAllFieldsOfFileDetailPanel() {
		fileDetailPanel.clearView();
		fileDetailPanel.setTitleEditable(false);
	}

	//Update the File Detail Panel GUI so the user can see what is scraped in
	public void updateAllFieldsOfFileDetailPanel(boolean forceUpdatePoster) {
			fileDetailPanel.updateView(forceUpdatePoster, false);
	}



	public static SearchResult showOptionPane(SearchResult [] searchResults, String siteName)
	{
		if(searchResults.length > 0)
		{

			SelectionDialog selectionDialog = new SelectionDialog(searchResults, siteName);

			JOptionPane.showOptionDialog(null, selectionDialog, "Select Movie to Scrape From " + siteName,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);
			return selectionDialog.getSelectedValue();
		}
		else return null;
	}

	class FileList {

		public JScrollPane getGui(File[] all,
				DefaultListModel<File> listModelFiles, boolean vertical) {

			//Gotta clear out the old list before we can populate it with new stuff
			getFileList().removeAll();

			for (File file : all) {
				listModelFiles.addElement(file);
			}
			// ..then use a renderer
			getFileList().setCellRenderer(new FileRenderer(!vertical));

			if (!vertical) {
				getFileList().setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
				getFileList().setVisibleRowCount(-1);
			} else {
				getFileList().setVisibleRowCount(9);
			}
			return new JScrollPane(getFileList());
		}
	}

	public void updateActorsFolder() {
		for(int movieNumberInList = 0; movieNumberInList < getCurrentlySelectedMovieFileList().size(); movieNumberInList++)
		{
			if(getCurrentlySelectedMovieFileList().get(movieNumberInList).isDirectory())
			{
				currentlySelectedActorsFolderList.add(new File(getCurrentlySelectedMovieFileList().get(movieNumberInList).getPath() + File.separator + ".actors"));
			}
			else if(getCurrentlySelectedMovieFileList().get(movieNumberInList).isFile())
			{
				currentlySelectedActorsFolderList.add(new File(getCurrentlySelectedDirectoryList().getPath() + File.separator + ".actors"));
			}
		}
	}

	public void setMainGUIEnabled(boolean value) {
		if(value)
			frmMovieScraperBlocker.unBlock();
		else if(!value)
			frmMovieScraperBlocker.block();
	}


	public File[] actorFolderFiles(int movieNumberInList) {
		ArrayList<File> actorFiles = new ArrayList<File>();
		System.out.println("actorfolderfiles " +  movieToWriteToDiskList);
		if(movieToWriteToDiskList != null && movieToWriteToDiskList.size() > 0 && movieToWriteToDiskList.get(movieNumberInList).getActors() != null)
		{
			if(currentlySelectedActorsFolderList != null && currentlySelectedActorsFolderList.get(movieNumberInList).isDirectory())
			{
				for (Actor currentActor : movieToWriteToDiskList.get(movieNumberInList).getActors())
				{
					String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
					File [] listFiles = currentlySelectedActorsFolderList.get(movieNumberInList).listFiles();
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



	public FileDetailPanel getFileDetailPanel() {
		return fileDetailPanel;
	}

	public List<File> getCurrentFile() {
		if (getCurrentlySelectedMovieFileList().size() > 0)
			return getCurrentlySelectedMovieFileList();
		return null;
	}



	public JFrame getFrmMoviescraper() {
		return frmMoviescraper;
	}

	public List<File> getCurrentlySelectedActorsFolderList() {
		return currentlySelectedActorsFolderList;
	}

	public void setCurrentlySelectedActorsFolderList(
			List<File> currentlySelectedActorsFolderList) {
		this.currentlySelectedActorsFolderList = currentlySelectedActorsFolderList;
	}

	public List<Movie> getMovieToWriteToDiskList() {
		return movieToWriteToDiskList;
	}

	public void setMovieToWriteToDiskList(List<Movie> movieToWriteToDiskList) {
		this.movieToWriteToDiskList = movieToWriteToDiskList;
	}

	public List<File> getCurrentlySelectedMovieFileList() {
		return currentlySelectedMovieFileList;
	}

	public void setCurrentlySelectedMovieFileList(
			List<File> currentlySelectedMovieFileList) {
		this.currentlySelectedMovieFileList = currentlySelectedMovieFileList;
	}

	public List<File> getCurrentlySelectedPosterFileList() {
		return currentlySelectedPosterFileList;
	}

	public void setCurrentlySelectedPosterFileList(
			List<File> currentlySelectedPosterFileList) {
		this.currentlySelectedPosterFileList = currentlySelectedPosterFileList;
	}

	public List<File> getCurrentlySelectedFanartFileList() {
		return currentlySelectedFanartFileList;
	}

	public void setCurrentlySelectedFanartFileList(
			List<File> currentlySelectedFanartFileList) {
		this.currentlySelectedFanartFileList = currentlySelectedFanartFileList;
	}

	public Movie getCurrentlySelectedMovieDMM() {
		return currentlySelectedMovieDMM;
	}

	public void setCurrentlySelectedMovieDMM(Movie currentlySelectedMovieDMM) {
		this.currentlySelectedMovieDMM = currentlySelectedMovieDMM;
	}

	public Movie getCurrentlySelectedMovieJavLibrary() {
		return currentlySelectedMovieJavLibrary;
	}

	public void setCurrentlySelectedMovieJavLibrary(
			Movie currentlySelectedMovieJavLibrary) {
		this.currentlySelectedMovieJavLibrary = currentlySelectedMovieJavLibrary;
	}

	public Movie getCurrentlySelectedMovieSquarePlus() {
		return currentlySelectedMovieSquarePlus;
	}

	public void setCurrentlySelectedMovieSquarePlus(
			Movie currentlySelectedMovieSquarePlus) {
		this.currentlySelectedMovieSquarePlus = currentlySelectedMovieSquarePlus;
	}

	public Movie getCurrentlySelectedMovieActionJav() {
		return currentlySelectedMovieActionJav;
	}

	public void setCurrentlySelectedMovieActionJav(
			Movie currentlySelectedMovieActionJav) {
		this.currentlySelectedMovieActionJav = currentlySelectedMovieActionJav;
	}

	public Movie getCurrentlySelectedMovieJavZoo() {
		return currentlySelectedMovieJavZoo;
	}

	public void setCurrentlySelectedMovieJavZoo(
			Movie currentlySelectedMovieJavZoo) {
		this.currentlySelectedMovieJavZoo = currentlySelectedMovieJavZoo;
	}

	public JFileChooser getChooser() {
		return chooser;
	}

	public void setChooser(JFileChooser chooser) {
		this.chooser = chooser;
	}

	public MoviescraperPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(MoviescraperPreferences preferences) {
		this.preferences = preferences;
	}

	public File getCurrentlySelectedDirectoryList() {
		return currentlySelectedDirectoryList;
	}

	public void setCurrentlySelectedDirectoryList(
			File currentlySelectedDirectoryList) {
		this.currentlySelectedDirectoryList = currentlySelectedDirectoryList;
	}

	public List<File> getCurrentlySelectedNfoFileList() {
		return currentlySelectedNfoFileList;
	}

	public void setCurrentlySelectedNfoFileList(
			List<File> currentlySelectedNfoFileList) {
		this.currentlySelectedNfoFileList = currentlySelectedNfoFileList;
	}

	public List<File> getCurrentlySelectedFolderJpgFileList() {
		return currentlySelectedFolderJpgFileList;
	}

	public void setCurrentlySelectedFolderJpgFileList(
			List<File> currentlySelectedFolderJpgFileList) {
		this.currentlySelectedFolderJpgFileList = currentlySelectedFolderJpgFileList;
	}

	public List<File> getCurrentlySelectedTrailerFileList() {
		return currentlySelectedTrailerFileList;
	}

	public void setCurrentlySelectedTrailerFileList(
			List<File> currentlySelectedTrailerFileList) {
		this.currentlySelectedTrailerFileList = currentlySelectedTrailerFileList;
	}


	public JList<File> getFileList() {
		return fileList;
	}

	public void setFileList(JList<File> fileList) {
		this.fileList = fileList;
	}

	public ProgressMonitor getProgressMonitor() {
		if (progressMonitor == null)
			progressMonitor = new ProgressMonitor(frmMoviescraper);
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public Movie getCurrentlySelectedMovieCaribbeancomPremium() {
		return currentlySelectedMovieCaribbeancomPremium;
	}

	public void setCurrentlySelectedMovieCaribbeancomPremium(
			Movie currentlySelectedMovieCaribbeancomPremium) {
		this.currentlySelectedMovieCaribbeancomPremium = currentlySelectedMovieCaribbeancomPremium;
	}

	public Movie getCurrentlySelectedMovieData18Movie() {
		return currentlySelectedMovieData18Movie;
	}

	public void setCurrentlySelectedMovieData18Movie(
			Movie currentlySelectedMovieData18Movie) {
		this.currentlySelectedMovieData18Movie = currentlySelectedMovieData18Movie;
	}
}