package moviescraper.doctord.GUI;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import moviescraper.doctord.Movie;
import moviescraper.doctord.controller.BrowseDirectoryAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.RefreshDirectoryAction;
import moviescraper.doctord.preferences.MoviescraperPreferences;

public class GUIMainMenuBar extends JMenuBar{
	
	private JMenu preferenceMenu;
	private MoviescraperPreferences preferences;
	private GUIMain guiMain;
	

	
	public GUIMainMenuBar(GUIMain guiMain)
	{
		this.preferences = guiMain.getPreferences();
		this.guiMain = guiMain;
		initializeMenus();
	}
	
	private void initializeMenus(){
		

		//Set up the preferences menu
		preferenceMenu = new JMenu("Preferences");
		preferenceMenu.setMnemonic(KeyEvent.VK_P);
		preferenceMenu.getAccessibleContext().setAccessibleDescription(
				"Preferences for JAVMovieScraper");


		//Checkbox for writing fanart and poster
		JCheckBoxMenuItem writeFanartAndPosters = new JCheckBoxMenuItem("Write fanart and poster files");
		writeFanartAndPosters.setState(getPreferences().getWriteFanartAndPostersPreference());
		writeFanartAndPosters.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setWriteFanartAndPostersPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setWriteFanartAndPostersPreference(false);

			}
		});
		preferenceMenu.add(writeFanartAndPosters);

		//Checkbox for overwriting fanart and poster
		JCheckBoxMenuItem overwriteFanartAndPosters = new JCheckBoxMenuItem("Overwrite fanart and poster files");
		overwriteFanartAndPosters.setState(getPreferences().getOverWriteFanartAndPostersPreference());
		overwriteFanartAndPosters.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setOverWriteFanartAndPostersPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setOverWriteFanartAndPostersPreference(false);

			}
		});
		preferenceMenu.add(overwriteFanartAndPosters);

		//Checkbox for overwriting writing actors to .actor folder
		JCheckBoxMenuItem writeActorImages = new JCheckBoxMenuItem("Write Actor Images");
		writeActorImages.setState(getPreferences().getDownloadActorImagesToActorFolderPreference());
		writeActorImages.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setDownloadActorImagesToActorFolderPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setDownloadActorImagesToActorFolderPreference(false);

			}
		});
		preferenceMenu.add(writeActorImages);

		//Checkbox for scraping extrafanart		
		JCheckBoxMenuItem scrapeExtraFanart = new JCheckBoxMenuItem("Write Extrafanart When Writing Data to a Directory or Moving File to a Directory");
		scrapeExtraFanart.setState(getPreferences().getExtraFanartScrapingEnabledPreference());
		scrapeExtraFanart.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setExtraFanartScrapingEnabledPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setExtraFanartScrapingEnabledPreference(false);

			}
		});
		preferenceMenu.add(scrapeExtraFanart);

		//Checkbox for also creating folder.jpg	in addition to the poster file jpg
		JCheckBoxMenuItem createFolderJpg = new JCheckBoxMenuItem("Create folder.jpg for each folder");
		createFolderJpg.setState(getPreferences().getCreateFolderJpgEnabledPreference());
		createFolderJpg.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setCreateFolderJpgEnabledPreference(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setCreateFolderJpgEnabledPreference(false);

			}
		});
		preferenceMenu.add(createFolderJpg);

		//Checkbox for using fanart.jpg and poster.jpg, not moviename-fanart.jpg and moviename-poster.jpg
		JCheckBoxMenuItem noMovieNameInImageFiles = new JCheckBoxMenuItem("Save poster and fanart as fanart.jpg and poster.jpg instead of moviename-fanart.jpg and moviename-poster.jpg");
		noMovieNameInImageFiles.setState(getPreferences().getNoMovieNameInImageFiles());
		noMovieNameInImageFiles.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setNoMovieNameInImageFiles(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setNoMovieNameInImageFiles(false);

			}
		});
		preferenceMenu.add(noMovieNameInImageFiles);

		//Checkbox for writing the trailer to file
		JCheckBoxMenuItem writeTrailerToFile = new JCheckBoxMenuItem("Write Trailer To File");
		writeTrailerToFile.setState(getPreferences().getWriteTrailerToFile());
		writeTrailerToFile.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setWriteTrailerToFile(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setWriteTrailerToFile(false);

			}
		});
		preferenceMenu.add(writeTrailerToFile);

		//Checkbox for naming .nfo file movie.nfo instead of using movie name in file
		JCheckBoxMenuItem nfoNamedMovieDotNfo = new JCheckBoxMenuItem(".nfo file named movie.nfo instead of using movie name");
		nfoNamedMovieDotNfo.setState(getPreferences().getNfoNamedMovieDotNfo());
		nfoNamedMovieDotNfo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setNfoNamedMovieDotNfo(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setNfoNamedMovieDotNfo(false);

			}
		});
		preferenceMenu.add(nfoNamedMovieDotNfo);

		//Checkbox for using IAFD Actors instead of Data18
		JCheckBoxMenuItem useIAFDForActors = new JCheckBoxMenuItem("Using IAFD Actors instead of Data18");
		useIAFDForActors.setState(getPreferences().getUseIAFDForActors());
		useIAFDForActors.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setUseIAFDForActors(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setUseIAFDForActors(false);

			}
		});
		preferenceMenu.add(useIAFDForActors);

		//Checkbox for renaming Movie file
		JCheckBoxMenuItem renameMovieFile = new JCheckBoxMenuItem("Rename Movie File");
		renameMovieFile.setState(getPreferences().getRenameMovieFile());
		renameMovieFile.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setRenameMovieFile(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setRenameMovieFile(false);

			}
		});
		preferenceMenu.add(renameMovieFile);

		//Checkbox for scraping JAV files in japanese instead of english when clicking scrape jav
		JCheckBoxMenuItem scrapeInJapanese = new JCheckBoxMenuItem("Scrape JAV Movies in Japanese Instead of English");
		scrapeInJapanese.setState(getPreferences().getScrapeInJapanese());
		scrapeInJapanese.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//save the menu choice off to the preference object (and the disk based settings file)
				if(e.getStateChange() == ItemEvent.SELECTED)
					getPreferences().setScrapeInJapanese(true);
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					getPreferences().setScrapeInJapanese(false);

			}
		});
		preferenceMenu.add(scrapeInJapanese);

		JMenu renameMenu = new JMenu("Rename Settings");

		JMenuItem renameSettings = new JMenuItem("Rename Settings");
		renameSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Movie currentSelectedMovie = null;
				if(guiMain.getMovieToWriteToDiskList().size() > 0)
					currentSelectedMovie = guiMain.getMovieToWriteToDiskList().get(0);
				new RenamerGUI(getPreferences(), currentSelectedMovie);
			}
		});
		renameMenu.add(renameSettings);

		// File menu

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"File actions for JAVMovieScraper");

		//Browse directory file menu
		JMenuItem browseDirectory = new JMenuItem("Browse directory...");
		browseDirectory.setMnemonic(KeyEvent.VK_B);
		browseDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				Event.CTRL_MASK));
		browseDirectory.addActionListener(new BrowseDirectoryAction(guiMain));
		fileMenu.add(browseDirectory);
		
		//Refresh file menu
		JMenuItem refreshDirectory = new JMenuItem("Refresh");
		refreshDirectory.setMnemonic(KeyEvent.VK_R);
		refreshDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Event.CTRL_MASK));
		refreshDirectory.addActionListener(new RefreshDirectoryAction(guiMain));
		fileMenu.add(refreshDirectory);
		
		//Open file menu
		JMenuItem openFile = new JMenuItem("Open");
		openFile.setMnemonic(KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				Event.CTRL_MASK));
		openFile.addActionListener(new OpenFileAction(guiMain));
		fileMenu.add(openFile);

		fileMenu.addSeparator();
		
		//Exit file menu
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				Event.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exit);

		//add the various menus together
		this.add(fileMenu);
		this.add(preferenceMenu);
		this.add(renameMenu);
	}
	
	private MoviescraperPreferences getPreferences(){
		return preferences;
	}

}
