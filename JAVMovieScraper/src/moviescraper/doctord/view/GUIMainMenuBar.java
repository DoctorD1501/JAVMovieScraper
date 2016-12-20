package moviescraper.doctord.view;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import moviescraper.doctord.controller.BrowseDirectoryAction;
import moviescraper.doctord.controller.BrowseUriAction;
import moviescraper.doctord.controller.ChooseExternalMediaPlayerAction;
import moviescraper.doctord.controller.ChooseFavoriteGenresAction;
import moviescraper.doctord.controller.ChooseFavoriteTagsAction;
import moviescraper.doctord.controller.FileNameCleanupAction;
import moviescraper.doctord.controller.MoveToNewFolderAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.PlayMovieAction;
import moviescraper.doctord.controller.RefreshDirectoryAction;
import moviescraper.doctord.controller.SelectAmalgamationSettingsAction;
import moviescraper.doctord.controller.WriteFileDataAction;
import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedAction;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class GUIMainMenuBar extends JMenuBar{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MoviescraperPreferences preferences;
	private GUIMain guiMain;
	
	private JMenuItem writeFileMenuItem;

	
	public GUIMainMenuBar(GUIMain guiMain)
	{
		this.preferences = guiMain.getPreferences();
		this.guiMain = guiMain;
		initializeMenus();
	}
	
	/**
	 * Allows you to create a new JCheckBoxMenuItem using Lambda expressions. The preferenceSetterFunction function will be called to change the value when the 
	 * menu item is checked and the initial value will be determined by the value returned by preferenceGetterFunction.
	 * @param checkboxTitle - Text of menu item to create
	 * @param preferenceSetterFunction - setter function called when checkbox item clicked
	 * @param preferenceGetterFunction - function to return initial value of the checkbox
	 * @return
	 */
	private JCheckBoxMenuItem createCheckBoxMenuItem(String checkboxTitle, Consumer<Boolean> preferenceSetterFunction, 
			Supplier<Boolean> preferenceGetterFunction)
	{
		JCheckBoxMenuItem checkBoxMenuItemToCreate = new JCheckBoxMenuItem(checkboxTitle);
		checkBoxMenuItemToCreate.setState(preferenceGetterFunction.get());
		checkBoxMenuItemToCreate.addItemListener(e -> {
			if(e.getStateChange() == ItemEvent.SELECTED) {
				preferenceSetterFunction.accept(true);
			}
			else if(e.getStateChange() == ItemEvent.DESELECTED) {
				preferenceSetterFunction.accept(false);
			}
			
		});
		return checkBoxMenuItemToCreate;
	}
	
	private void initializePreferencesMenu(){
		

		//Set up the preferences menu
		JMenu preferenceMenu = new JMenu("Preferences");
		preferenceMenu.setMnemonic(KeyEvent.VK_P);
		preferenceMenu.getAccessibleContext().setAccessibleDescription(
				"Preferences for JAVMovieScraper");
		createAllCheckBoxMenusForPreferencesMenu(preferenceMenu);	
		add(preferenceMenu);

	}
	
	private void createAllCheckBoxMenusForPreferencesMenu(JMenu preferenceMenu) {
		
		if(preferenceMenu != null) {
			preferenceMenu.add(createFileCreationMenu());
			preferenceMenu.add(createTranslationMenu());
			preferenceMenu.add(createScrapingSearchOptionsMenu());
			preferenceMenu.add(createScrapingDialogsToShowMenu());
			preferenceMenu.add(createModifiyScrapedResultsByMenu());
			preferenceMenu.add(createRenamingMenu());
			preferenceMenu.add(createCleanUpFileNameMenu());
		}
	}
	
	
	private JMenu createCleanUpFileNameMenu() {
		JMenu submenu = new JMenu("Clean Up File Name");
		
		//Checkbox for whether the user needs to manually confirm the results of each clean up file operation	
		JCheckBoxMenuItem confirmNameForFileNameCleanup = createCheckBoxMenuItem(
				"Confirm New Name Each Time for \"Clean Up File Name\"", 
				b -> getPreferences().setConfirmCleanUpFileNameNameBeforeRenaming(b), 
				() -> getPreferences().getConfirmCleanUpFileNameNameBeforeRenaming());
		submenu.add(confirmNameForFileNameCleanup);
		
		return submenu;
	}

	private JMenu createRenamingMenu() {
		JMenu submenu = new JMenu("Renaming");
		
		//Checkbox for renaming Movie file
		JCheckBoxMenuItem renameMovieFile = createCheckBoxMenuItem(
				"Enable Renaming When Writing File Data", 
				b -> getPreferences().setRenameMovieFile(b), 
				() -> getPreferences().getRenameMovieFile());
		submenu.add(renameMovieFile);
		
		return submenu;
	}

	private JMenu createModifiyScrapedResultsByMenu() {
		JMenu submenu = new JMenu("Modify Scraped Results By");
		
		//Checkbox for option to append the ID to start of the title field
		JCheckBoxMenuItem appendIDToStartOfTitle = createCheckBoxMenuItem(
				"Appending ID to Start of Title Field When Scraping", 
				b -> getPreferences().setAppendIDToStartOfTitle(b), 
				() -> getPreferences().getAppendIDToStartOfTitle());
		submenu.add(appendIDToStartOfTitle);
		
		//Checkbox for option to use file name as the scraped title every time
		JCheckBoxMenuItem useFilenameAsScrapedMovieTitle = createCheckBoxMenuItem(
				"Title Field is Always Set as Filename", 
				b -> getPreferences().setUseFileNameAsTitle(b), 
				() -> getPreferences().getUseFileNameAsTitle());
		submenu.add(useFilenameAsScrapedMovieTitle);
		
		return submenu;
	}

	private JMenu createScrapingDialogsToShowMenu() {
		JMenu submenu = new JMenu("Scraping Dialogs to Show");
		
		//Checkbox for scraping dialog box allowing the user to override the URL used when scraping
		JCheckBoxMenuItem promptForUserProvidedURL = createCheckBoxMenuItem(
				"Provide URL Manually", 
				b -> getPreferences().setPromptForUserProvidedURLWhenScraping(b), 
				() -> getPreferences().getPromptForUserProvidedURLWhenScraping());
		submenu.add(promptForUserProvidedURL);
                
		//Checkbox for scraping dialog box allowing the user to override the URL used when scraping
		JCheckBoxMenuItem considerUserSelectionOneURL = createCheckBoxMenuItem(
				"Consider All Selections one Item", 
				b -> getPreferences().setConsiderUserSelectionOneURLWhenScraping(b), 
				() -> getPreferences().getConsiderUserSelectionOneURLWhenScraping());
		submenu.add(considerUserSelectionOneURL);                
		
		//Checkbox for whether the user needs to manually select the art while scraping
		JCheckBoxMenuItem selectArtManuallyWhenScraping = createCheckBoxMenuItem(
				"Select Art Manually", 
				b -> getPreferences().setSelectArtManuallyWhenScraping(b), 
				() -> getPreferences().getSelectArtManuallyWhenScraping());
		submenu.add(selectArtManuallyWhenScraping);
		
		//Checkbox for whether the user needs to manually select the search result when scraping	
		JCheckBoxMenuItem selectSearchResultManuallyWhenScraping = createCheckBoxMenuItem(
				"Select Search Results Manually", 
				b -> getPreferences().setSelectSearchResultManuallyWhenScraping(b), 
				() -> getPreferences().getSelectSearchResultManuallyWhenScraping());
		submenu.add(selectSearchResultManuallyWhenScraping);
		
		return submenu;
	}

	private JMenu createScrapingSearchOptionsMenu() {
		JMenu submenu = new JMenu("Scraping Search Options");
		
		//Checkbox for option if the ID is just considered the first word in the file
		JCheckBoxMenuItem isFirstWordOfFileID = createCheckBoxMenuItem(
				"Use First Word of File for ID Instead of Last", 
				b -> getPreferences().setIsFirstWordOfFileID(b), 
				() -> getPreferences().getIsFirstWordOfFileID());
		submenu.add(isFirstWordOfFileID);
		
		
		return submenu;
	}

	private JMenu createTranslationMenu() {
		JMenu submenu = new JMenu("Translation");
		
		//Checkbox for scraping JAV files in japanese instead of english when clicking scrape jav
		JCheckBoxMenuItem scrapeInJapanese = createCheckBoxMenuItem(
				"Scrape JAV Movies in Japanese Instead of English", 
				b -> getPreferences().setScrapeInJapanese(b), 
				() -> getPreferences().getScrapeInJapanese());
		submenu.add(scrapeInJapanese);
		
		return submenu;
	}

	private JMenu createFileCreationMenu() {
		JMenu submenu = new JMenu("File Creation");

		//Checkbox for writing fanart and poster
		JCheckBoxMenuItem writeFanartAndPosters = createCheckBoxMenuItem(
				"Write Fanart and Poster Images", 
				b -> getPreferences().setWriteFanartAndPostersPreference(b), 
				() -> getPreferences().getWriteFanartAndPostersPreference());
		submenu.add(writeFanartAndPosters);
		
		//Checkbox for overwriting writing actors to .actor folder	
		JCheckBoxMenuItem writeActorImages = createCheckBoxMenuItem(
				"Write Actor Images", 
				b -> getPreferences().setDownloadActorImagesToActorFolderPreference(b), 
				() -> getPreferences().getDownloadActorImagesToActorFolderPreference());
		submenu.add(writeActorImages);
		
		//Checkbox for scraping extrafanart		
		JCheckBoxMenuItem scrapeExtraFanart = createCheckBoxMenuItem(
				"Write Extrafanart When Writing Data to a Directory or Moving File to a Directory", 
				b -> getPreferences().setExtraFanartScrapingEnabledPreference(b), 
				() -> getPreferences().getExtraFanartScrapingEnabledPreference());
		submenu.add(scrapeExtraFanart);
		
		//Checkbox for also creating folder.jpg	in addition to the poster file jpg	
		JCheckBoxMenuItem createFolderJpg = createCheckBoxMenuItem(
				"Create folder.jpg for Each Folder", 
				b -> getPreferences().setCreateFolderJpgEnabledPreference(b), 
				() -> getPreferences().getCreateFolderJpgEnabledPreference());
		submenu.add(createFolderJpg);
		
		//Checkbox for writing the trailer to file
		JCheckBoxMenuItem writeTrailerToFile = createCheckBoxMenuItem(
				"Download and Write Trailer To File (Warning: Slow)", 
				b -> getPreferences().setWriteTrailerToFile(b), 
				() -> getPreferences().getWriteTrailerToFile());
		submenu.add(writeTrailerToFile);

		//Checkbox for overwriting fanart and poster
		JCheckBoxMenuItem overwriteFanartAndPosters = createCheckBoxMenuItem(
				"Overwrite Fanart, Poster, and folder.jpg Files", 
				b -> getPreferences().setOverWriteFanartAndPostersPreference(b), 
				() -> getPreferences().getOverWriteFanartAndPostersPreference());
		submenu.add(overwriteFanartAndPosters);


		//Checkbox for using fanart.jpg and poster.jpg, not moviename-fanart.jpg and moviename-poster.jpg
		JCheckBoxMenuItem noMovieNameInImageFiles = createCheckBoxMenuItem(
				"Save poster and fanart as fanart.jpg and poster.jpg instead of moviename-fanart.jpg and moviename-poster.jpg", 
				b -> getPreferences().setNoMovieNameInImageFiles(b), 
				() -> getPreferences().getNoMovieNameInImageFiles());
		submenu.add(noMovieNameInImageFiles);

		//Checkbox for naming .nfo file movie.nfo instead of using movie name in file
		JCheckBoxMenuItem nfoNamedMovieDotNfo = createCheckBoxMenuItem(
				".nfo file named movie.nfo instead of using movie name", 
				b -> getPreferences().setNfoNamedMovieDotNfo(b), 
				() -> getPreferences().getNfoNamedMovieDotNfo());
		submenu.add(nfoNamedMovieDotNfo);
		
		//Checkbox for whether to write the <thumb> tags into the nfo file
		JCheckBoxMenuItem writeThumbTagsForPosterAndFanartToNfo = createCheckBoxMenuItem(
				"Write <thumb> tags for poster and fanart into .nfo file", 
				b -> getPreferences().setWriteThumbTagsForPosterAndFanartToNfo(b), 
				() -> getPreferences().getWriteThumbTagsForPosterAndFanartToNfo());
		submenu.add(writeThumbTagsForPosterAndFanartToNfo);

		return submenu;
	}

	private void initializeSettingsMenu() {

		JMenu settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic(KeyEvent.VK_S);

		// This is a scraping preference but fits better under the Settings menu
		JMenuItem scrapersMenuItem = new JMenuItem("Amalgamation Settings...");
		scrapersMenuItem.addActionListener(new SelectAmalgamationSettingsAction(guiMain));
		settingsMenu.add(scrapersMenuItem);
		
		JMenuItem renameSettings = new JMenuItem("Rename Settings...");
		renameSettings.addActionListener(new ActionListener() {
			@SuppressWarnings("unused") //simply calling the new for RenamerGUI below will show the dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				Movie currentSelectedMovie = null;
				File currentlySelectedFile = null;
				if(guiMain.getMovieToWriteToDiskList().size() > 0)
					currentSelectedMovie = guiMain.getMovieToWriteToDiskList().get(0);
				if(guiMain.getCurrentlySelectedMovieFileList().size() > 0)
					currentlySelectedFile = guiMain.getCurrentlySelectedMovieFileList().get(0);
				new RenamerGUI(getPreferences(), currentSelectedMovie, currentlySelectedFile);
			}
		});
		settingsMenu.add(renameSettings);
		
		JMenuItem favoriteGenresMenuItem = new JMenuItem("Favorite Genres...");
		favoriteGenresMenuItem.addActionListener(new ChooseFavoriteGenresAction(guiMain));
		settingsMenu.add(favoriteGenresMenuItem);
		
		JMenuItem favoriteTagsMenuItem = new JMenuItem("Favorite Tags...");
		favoriteTagsMenuItem.addActionListener(new ChooseFavoriteTagsAction(guiMain));
		settingsMenu.add(favoriteTagsMenuItem);
		
		JMenuItem externalMediaPlayerPickerMenu = new JMenuItem("Pick External Media Player...");
		externalMediaPlayerPickerMenu.addActionListener(new ChooseExternalMediaPlayerAction(guiMain));
		settingsMenu.add(externalMediaPlayerPickerMenu);
		
		add(settingsMenu);
	}
	
	private void initializeFileMenu() {
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
		
		fileMenu.addSeparator();
		
		//Open file menu
		JMenuItem openFile = new JMenuItem("Open File");
		openFile.setMnemonic(KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				Event.CTRL_MASK));
		openFile.addActionListener(new OpenFileAction(guiMain));
		fileMenu.add(openFile);
		
		JMenuItem playMovie = new JMenuItem("Play Movie");
		playMovie.setMnemonic(KeyEvent.VK_P);
		playMovie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				Event.CTRL_MASK));
		playMovie.addActionListener(new PlayMovieAction(guiMain));
		fileMenu.add(playMovie);

		writeFileMenuItem = new JMenuItem("Write File Data");
		writeFileMenuItem.setToolTipText("Write out the .nfo file to disk. The movie must have a title for this to be enabled.");
		writeFileMenuItem.setEnabled(false); //this becomes enabled later when there is an actual movie to write
		writeFileMenuItem.setMnemonic(KeyEvent.VK_W);
		writeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				Event.CTRL_MASK));
		writeFileMenuItem.addActionListener(new WriteFileDataAction(guiMain));
		fileMenu.add(writeFileMenuItem);
		
		JMenuItem moveFile = new JMenuItem("Move File to New Folder");
		moveFile.setMnemonic(KeyEvent.VK_M);
		moveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				Event.CTRL_MASK));
		moveFile.addActionListener(new MoveToNewFolderAction(guiMain));
		fileMenu.add(moveFile);
		
		JMenuItem cleanFile = new JMenuItem("Clean up File Name");
		cleanFile.setMnemonic(KeyEvent.VK_C);
		cleanFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				Event.CTRL_MASK));
		cleanFile.addActionListener(new FileNameCleanupAction(guiMain));
		fileMenu.add(cleanFile);
		
		
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
		
		add(fileMenu);
	}
	
	private void initializeViewMenu() {
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		JMenuItem consoleInSeperateWindowMenuItem = new JMenuItem("View Output In New Window");
		consoleInSeperateWindowMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageConsoleGUI.showWindow();
			}
		});
		
		
		JCheckBoxMenuItem consolePanelMenuItem = new JCheckBoxMenuItem("Show Output Panel");
		consolePanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		consolePanelMenuItem.setState(guiMain.getGuiSettings().getShowOutputPanel());
		consolePanelMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					guiMain.showMessageConsolePanel();
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					guiMain.hideMessageConsolePanel();	
			}
		});
		
		JCheckBoxMenuItem buttonPanelMenuItem = new JCheckBoxMenuItem("Show Tool Bar");
		buttonPanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		buttonPanelMenuItem.setState(guiMain.getGuiSettings().getShowToolbar());
		buttonPanelMenuItem.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED)
					guiMain.showButtonPanel();
				else if(e.getStateChange() == ItemEvent.DESELECTED)
					guiMain.hideButtonPanel();	
				
			}
		});
		
		viewMenu.add(buttonPanelMenuItem);
		viewMenu.add(consolePanelMenuItem);
		viewMenu.add(consoleInSeperateWindowMenuItem);
		
		add(viewMenu);
	}
	
	private void initializeScrapeMenu() {
		JMenu scrapeMenu = new JMenu("Scrape");
		scrapeMenu.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem scrapeAdultDVDAmalgamated = new JMenuItem(new ScrapeAmalgamatedAction(guiMain, 
				guiMain.getAllAmalgamationOrderingPreferences()
				.getScraperGroupAmalgamationPreference(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)));
		scrapeAdultDVDAmalgamated.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
		scrapeAdultDVDAmalgamated.setIcon(GUIMainButtonPanel.initializeImageIcon("App"));
		
		JMenuItem scrapeJAVAmalgamated = new JMenuItem(new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)));
		scrapeJAVAmalgamated.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		scrapeJAVAmalgamated.setIcon(GUIMainButtonPanel.initializeImageIcon("Japan"));
		
		JMenuItem scrapeIVAmalgamated = new JMenuItem(new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.IV_SCRAPER_GROUP)));
		scrapeIVAmalgamated.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		scrapeIVAmalgamated.setIcon(GUIMainButtonPanel.initializeImageIcon("Japan"));
		
		scrapeMenu.add(scrapeAdultDVDAmalgamated);
		scrapeMenu.add(scrapeJAVAmalgamated);
		scrapeMenu.add(scrapeIVAmalgamated);
		
		JMenu specificMenu = new JMenu("Specific Scrape");
		scrapeMenu.add(specificMenu);
		
		int i = 0;
		
		for(SiteParsingProfileItem item: SpecificProfileFactory.getAll()){
			JMenuItem menuItem = new JMenuItem(item.toString());
			Icon menuItemIcon = item.getParser().getProfileIcon();
			if(menuItemIcon != null)
				menuItem.setIcon(menuItemIcon);
			
			if (++i < 10){
				menuItem.setAccelerator(KeyStroke.getKeyStroke(Character.forDigit(i,  10), Event.CTRL_MASK));
			}
			else if(i < 20){
				if(i==10)
					++i;
				menuItem.setAccelerator(KeyStroke.getKeyStroke(Character.forDigit(i%10,  10), Event.CTRL_MASK | Event.SHIFT_MASK));
			}
			menuItem.addActionListener(new ScrapeAmalgamatedAction(guiMain, item.getParser()));
			specificMenu.add(menuItem);
		}
		
		add(scrapeMenu);
	}
	
	private void initializeHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem website = new JMenuItem("Visit website");
		website.addActionListener(new BrowseUriAction(BrowseUriAction.MainWebsiteUri));
		
		JMenuItem reportBug = new JMenuItem("Report bug");
		reportBug.addActionListener(new BrowseUriAction(BrowseUriAction.ReportBugUri));
		
		JMenuItem about = new JMenuItem("About...");
		about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new AboutDialog(guiMain.getFrmMoviescraper()).setVisible(true);
			}
		});
		
		helpMenu.add(website);
		helpMenu.add(reportBug);
		helpMenu.addSeparator();
		helpMenu.add(about);
		
		add(helpMenu);
	}
	
	private void initializeMenus() {
		//add the various menus together
		initializeFileMenu();
		initializeScrapeMenu();
		initializePreferencesMenu();
		initializeSettingsMenu();
		initializeViewMenu();
		initializeHelpMenu();
	}
	
	private MoviescraperPreferences getPreferences(){
		return preferences;
	}
	
	
	public void disableWriteFile() {
		if (writeFileMenuItem != null) {
			writeFileMenuItem.setEnabled(false);
		}
	}
	
	public void enableWriteFile() {
		if (writeFileMenuItem != null) {
			writeFileMenuItem.setEnabled(true);
		}
	}

}
