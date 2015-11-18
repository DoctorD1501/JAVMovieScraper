package moviescraper.doctord.view;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
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

import moviescraper.doctord.controller.SelectFileListAction;
import moviescraper.doctord.controller.amalgamation.AllAmalgamationOrderingPreferences;
import moviescraper.doctord.model.IconCache;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.preferences.GuiSettings;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.renderer.FileRenderer;

//madaustrian added
import moviescraper.doctord.model.dataitem.GlobalGenreList;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;

import javax.swing.UIManager;

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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.BoxLayout;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
	private GuiSettings guiSettings;
	private AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences;

	//scraped movies
	private Movie currentlySelectedMovieDMM;
	private Movie currentlySelectedMovieActionJav;
	private Movie currentlySelectedMovieSquarePlus;
	private Movie currentlySelectedMovieJavLibrary;
	private Movie currentlySelectedMovieJavZoo;
	private Movie currentlySelectedMovieCaribbeancomPremium;
	private Movie currentlySelectedMovieData18Movie;
	private Movie currentlySelectedMovieR18;
	public List <Movie> movieToWriteToDiskList;

	//Gui Elements
	JFrame frmMoviescraper;
	protected WindowBlocker frmMovieScraperBlocker;
	private DefaultListModel<File> listModelFiles;

	private JPanel fileListPanel;
	private FileDetailPanel fileDetailPanel;

	private JScrollPane fileListScrollPane;
	private JSplitPane fileListFileDetailSplitPane;
	private JList<File> fileList;
	private JFileChooser chooser;
	
	private MessageConsolePanel messageConsolePanel;
	
	private ProgressMonitor progressMonitor;

	//variables for fileList
	private static int CHAR_DELTA = 1000;
	private String m_key;
	private long m_time;

	//Menus
	JMenuBar menuBar;
	JMenu preferenceMenu;
	private String originalJavLibraryMovieTitleBeforeAmalgamate;

	//Dimensions of various elements
	private static final int defaultMainFrameX = 1024;
	private static final int defaultMainFrameY = 768;

	private final static boolean debugMessages = false;
	private GUIMainButtonPanel buttonPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					//Prevent text area font from looking different than text field font
					UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));
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
	 * Restore amalgamation preferences from what is saved on disk
	 */
	public void reinitializeAmalgamationPreferencesFromFile()
	{
		

		allAmalgamationOrderingPreferences = new AllAmalgamationOrderingPreferences();
				
		allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences.initializeValuesFromPreferenceFile();
		
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		preferences = MoviescraperPreferences.getInstance();
		guiSettings = GuiSettings.getInstance();
		
// madaustrian added
				ReadGenreListfromXML();
//				ReadGenreXMLFile();
		
		reinitializeAmalgamationPreferencesFromFile();
		
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
		IconCache.setIconProvider(getGuiSettings().getUseContentBasedTypeIcons() ? IconCache.IconProviderType.CONTENT
						: IconCache.IconProviderType.SYSTEM);
		
		//Used for icon in the title bar
		frmMoviescraper.setIconImage(GUICommon.getProgramIcon());
		
		//Set up the file list panel - the panel where the user picks what file to scrape
		setUpFileListPanel();
		
		
		//Set up the bottom panel - area for message panel
		messageConsolePanel = new MessageConsolePanel();
		frmMoviescraper.getContentPane().add(messageConsolePanel, BorderLayout.SOUTH);

		buttonPanel = new GUIMainButtonPanel(this);
		frmMoviescraper.getContentPane().add(buttonPanel, BorderLayout.NORTH);
		
		//add in the menu bar
		frmMoviescraper.setJMenuBar(new GUIMainMenuBar(this));

		int gap = 7;
		fileListFileDetailSplitPane.setBorder(BorderFactory.createEmptyBorder());
		fileListFileDetailSplitPane.setDividerSize(gap);
		messageConsolePanel.setBorder(BorderFactory.createEmptyBorder(gap, 0, 0, 0));
		
		// restore gui state
		buttonPanel.setVisible(guiSettings.getShowToolbar());
		messageConsolePanel.setVisible(guiSettings.getShowOutputPanel());
		
	}

	/**
	 * @param upIcon
	 * @param browseDirectoryIcon
	 * @param refreshDirectoryIcon
	 */
	private void setUpFileListPanel() {
		fileListPanel = new JPanel();

		defaultHomeDirectory = getGuiSettings().getLastUsedDirectory();
		setCurrentlySelectedDirectoryList(defaultHomeDirectory);
		
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
								getGuiSettings().setLastUsedDirectory(getCurrentlySelectedDirectoryList());
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
		FileList fl = new FileList();
		fileListScrollPane = fl.getGui(
				showFileListSorted(getCurrentlySelectedDirectoryList()), listModelFiles,
				true);
		fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
		fileListPanel.add(fileListScrollPane);

		fileDetailPanel = new FileDetailPanel(getPreferences(), this);
		JScrollPane fileDetailsScrollPane = new JScrollPane(fileDetailPanel);
		
		fileListFileDetailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileListPanel, fileDetailsScrollPane);
		fileListPanel.setMinimumSize(new Dimension(200,50));
		fileDetailsScrollPane.setMinimumSize(new Dimension(100,50));
		
		frmMoviescraper.getContentPane().add(fileListFileDetailSplitPane, BorderLayout.CENTER);
	}

	public void removeOldScrapedMovieReferences() {
		setCurrentlySelectedMovieDMM(null);
		setCurrentlySelectedMovieActionJav(null);
		setCurrentlySelectedMovieSquarePlus(null);
		setCurrentlySelectedMovieJavLibrary(null);
		setOriginalJavLibraryMovieTitleBeforeAmalgamate(null);
		setCurrentlySelectedMovieJavZoo(null);
		setCurrentlySelectedMovieCaribbeancomPremium(null);
		setCurrentlySelectedMovieData18Movie(null);
		setCurrentlySelectedMovieR18(null);
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
		removeOldScrapedMovieReferences();
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
				getFileList().removeListSelectionListener(getFileList().getListSelectionListeners()[0]);
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
			@Override
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




	

	public void clearAllFieldsOfFileDetailPanel() {
		fileDetailPanel.clearView();
		fileDetailPanel.setTitleEditable(false);
	}

	//Update the File Detail Panel GUI so the user can see what is scraped in
	public void updateAllFieldsOfFileDetailPanel(boolean forceUpdatePoster, boolean newMovieWasSet) {
			fileDetailPanel.updateView(forceUpdatePoster, newMovieWasSet);
	}



	public static SearchResult showOptionPane(SearchResult [] searchResults, String siteName)
	{
		if(searchResults.length > 0)
		{

			SelectionDialog selectionDialog = new SelectionDialog(searchResults, siteName);

			int optionPicked = JOptionPane.showOptionDialog(null, selectionDialog, "Select Movie to Scrape From " + siteName,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);
			if(optionPicked == JOptionPane.CANCEL_OPTION)
				return null;
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
		if(movieToWriteToDiskList != null 
				&& movieToWriteToDiskList.size() > 0 
				&& movieToWriteToDiskList.get(movieNumberInList) != null
				&& movieToWriteToDiskList.get(movieNumberInList).getActors() != null)
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
		if(this.currentlySelectedMovieJavLibrary != null && this.currentlySelectedMovieJavLibrary.getTitle() != null)
			setOriginalJavLibraryMovieTitleBeforeAmalgamate(currentlySelectedMovieJavLibrary.getTitle().getTitle());
		else
		{
			setOriginalJavLibraryMovieTitleBeforeAmalgamate(null);
		}
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

	public GuiSettings getGuiSettings() {
		return guiSettings;
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
	
	public void showMessageConsolePanel(){
		messageConsolePanel.setVisible(true);
		guiSettings.setShowOutputPanel(true);
	}
	
	public void hideMessageConsolePanel(){
		messageConsolePanel.setVisible(false);
		guiSettings.setShowOutputPanel(false);
	}
	
	public void showButtonPanel(){
		buttonPanel.setVisible(true);
		guiSettings.setShowToolbar(true);
	}
	
	public void hideButtonPanel(){
		buttonPanel.setVisible(false);
		guiSettings.setShowToolbar(false);
	}
	
	public Movie getCurrentlySelectedMovieR18() {
		return currentlySelectedMovieR18;
	}

	public void setCurrentlySelectedMovieR18(Movie currentlySelectedMovieR18) {
		this.currentlySelectedMovieR18 = currentlySelectedMovieR18;
	}

	public String getOriginalJavLibraryMovieTitleBeforeAmalgamate() {
		return originalJavLibraryMovieTitleBeforeAmalgamate;
	}

	public void setOriginalJavLibraryMovieTitleBeforeAmalgamate(
			String originalJavLibraryMovieTitleBeforeAmalgamate) {
		this.originalJavLibraryMovieTitleBeforeAmalgamate = originalJavLibraryMovieTitleBeforeAmalgamate;
	}
	
	public boolean showAmalgamationSettingsDialog() {
		AmalgamationSettingsDialog dialog =  new AmalgamationSettingsDialog(this, getAllAmalgamationOrderingPreferences());
		return dialog.show();
	}

	public AllAmalgamationOrderingPreferences getAllAmalgamationOrderingPreferences() {
		//rereading from file in case external program somehow decides to change this file before we get it.
		//also this fixes a bug where canceling a scrape somehow corrupted the variable and caused an error when opening the
		//amalgamation settings dialog
		allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences.initializeValuesFromPreferenceFile();
		return allAmalgamationOrderingPreferences;
	}

	public void setAllAmalgamationOrderingPreferences(
			AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences) {
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
	}

////madaustrian added	
//
	public void ReadGenreListfromXML() {

		File fXmlFile = new File("genrelist.xml");
	
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = null;
		
		if ( fXmlFile.exists()== false){
			// create file
			System.out.println("Creating genrelist.xml since it was not found...");

			  DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			  DocumentBuilder docBuildernew = null;
			try {
				docBuildernew = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			//root elements
			  Document docnew = docBuildernew.newDocument();

			  Element rootElement = docnew.createElement("genrelist");
			  docnew.appendChild(rootElement);

			  // ----------- every node needs this ---------
			  //genrenode elements
			  Element genrenode = docnew.createElement("genre");
			  rootElement.appendChild(genrenode);
			  //set attribute to genrenode element
			  Attr listcolumn = docnew.createAttribute("column");
			  listcolumn.setValue("0");
			  genrenode.setAttributeNode(listcolumn);
			  //text elements
			  Element genretext = docnew.createElement("text");
			  genretext.appendChild(docnew.createTextNode("JAV"));
			  genrenode.appendChild(genretext);
			  // end ----------- every node needs this ---------

			  //write the content into xml file
			  TransformerFactory transformerFactory = TransformerFactory.newInstance();
			  Transformer transformer = null;
			try {
				transformer = transformerFactory.newTransformer();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  DOMSource source = new DOMSource(docnew);

			  StreamResult result =  new StreamResult(new File("genrelist.xml"));
			  try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		try {
			doc = dBuilder.parse(fXmlFile);
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("genre");
		
		GlobalGenreList.NumberOfGenreItems_1 = 0;
		GlobalGenreList.NumberOfGenreItems_2 = 0;
		GlobalGenreList.NumberOfGenreItems_3 = 0;
		GlobalGenreList.NumberOfGenreItems_4 = 0;
		GlobalGenreList.NumberOfGenreItems_5 = 0;
		
		GlobalGenreList.RealNumberOfGenreItems_1 = 0;
		GlobalGenreList.RealNumberOfGenreItems_2 = 0;
		GlobalGenreList.RealNumberOfGenreItems_3 = 0;
		GlobalGenreList.RealNumberOfGenreItems_4 = 0;
		GlobalGenreList.RealNumberOfGenreItems_5 = 0;
		
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
					
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				System.out.println("genre col,text : " + eElement.getAttribute("column") + "," + eElement.getElementsByTagName("text").item(0).getTextContent());
				
				int col = 999;
				
				if (eElement.getAttribute("column") != ""){
					col = Integer.parseInt(eElement.getAttribute("column"));
				}
				
				if ( col == 1){
					GlobalGenreList.NumberOfGenreItems_1++;
				}
				else if ( col == 2){
					GlobalGenreList.NumberOfGenreItems_2++;
				} 
				else if ( col == 3){
					GlobalGenreList.NumberOfGenreItems_3++;
				} 
				else if ( col == 4){
					GlobalGenreList.NumberOfGenreItems_4++;
				}
				else if ( col == 5){
					GlobalGenreList.NumberOfGenreItems_5++;
				} 

			}
		}
		
		GlobalGenreList.GenreList_1 = new String[GlobalGenreList.NumberOfGenreItems_1];
		GlobalGenreList.GenreList_2 = new String[GlobalGenreList.NumberOfGenreItems_2];
		GlobalGenreList.GenreList_3 = new String[GlobalGenreList.NumberOfGenreItems_3];
		GlobalGenreList.GenreList_4 = new String[GlobalGenreList.NumberOfGenreItems_4];
		GlobalGenreList.GenreList_5 = new String[GlobalGenreList.NumberOfGenreItems_5];
		
		GlobalGenreList.NumberOfGenreItems_1 = 0;
		GlobalGenreList.NumberOfGenreItems_2 = 0;
		GlobalGenreList.NumberOfGenreItems_3 = 0;
		GlobalGenreList.NumberOfGenreItems_4 = 0;
		GlobalGenreList.NumberOfGenreItems_5 = 0;
		
		
//create arrays and define text
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
					
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				int col = 999;

				if (eElement.hasAttribute("column")){
					col = Integer.parseInt(eElement.getAttribute("column"));
				}
				
				if ( col == 1){
					GlobalGenreList.GenreList_1[GlobalGenreList.NumberOfGenreItems_1]=eElement.getElementsByTagName("text").item(0).getTextContent();
					GlobalGenreList.NumberOfGenreItems_1++;
				}
				else if ( col == 2){
					GlobalGenreList.GenreList_2[GlobalGenreList.NumberOfGenreItems_2]=eElement.getElementsByTagName("text").item(0).getTextContent();
					GlobalGenreList.NumberOfGenreItems_2++;
				} 
				else if ( col == 3){
					GlobalGenreList.GenreList_3[GlobalGenreList.NumberOfGenreItems_3]=eElement.getElementsByTagName("text").item(0).getTextContent();
					GlobalGenreList.NumberOfGenreItems_3++;
				} 
				else if ( col == 4){
					GlobalGenreList.GenreList_4[GlobalGenreList.NumberOfGenreItems_4]=eElement.getElementsByTagName("text").item(0).getTextContent();
					GlobalGenreList.NumberOfGenreItems_4++;
				}
				else if ( col == 5){
					GlobalGenreList.GenreList_5[GlobalGenreList.NumberOfGenreItems_5]=eElement.getElementsByTagName("text").item(0).getTextContent();
					GlobalGenreList.NumberOfGenreItems_5++;
				} 
			}
		}
		
		System.out.println("NumberOfGenreItems_1 : " + GlobalGenreList.NumberOfGenreItems_1);
		System.out.println("NumberOfGenreItems_2 : " + GlobalGenreList.NumberOfGenreItems_2);
		System.out.println("NumberOfGenreItems_3 : " + GlobalGenreList.NumberOfGenreItems_3);
		System.out.println("NumberOfGenreItems_4 : " + GlobalGenreList.NumberOfGenreItems_4);
		System.out.println("NumberOfGenreItems_5 : " + GlobalGenreList.NumberOfGenreItems_5);
		
		GlobalGenreList.GenreButton_1 = new JCheckBox[GlobalGenreList.NumberOfGenreItems_1];
		GlobalGenreList.GenreButton_2 = new JCheckBox[GlobalGenreList.NumberOfGenreItems_2];
		GlobalGenreList.GenreButton_3 = new JCheckBox[GlobalGenreList.NumberOfGenreItems_3];
		GlobalGenreList.GenreButton_4 = new JCheckBox[GlobalGenreList.NumberOfGenreItems_4];	
		GlobalGenreList.GenreButton_5 = new JCheckBox[GlobalGenreList.NumberOfGenreItems_5];

				
		// ======== Genrelist - copy down for now ====================	
			
	}	
}