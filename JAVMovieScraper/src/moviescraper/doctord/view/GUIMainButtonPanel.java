package moviescraper.doctord.view;

import moviescraper.doctord.controller.*;
import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedAction;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class GUIMainButtonPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int iconSizeX = 16;
	private static final int iconSizeY = 16;
	
	private GUIMain guiMain;
	private Box box;
	
	private JButton btnWriteFileData;
	
	public GUIMainButtonPanel(GUIMain guiMain)
	{
		this.guiMain = guiMain;
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		box = Box.createHorizontalBox();
		add(box);
				
		initializeButtons();
	}
	
	public static ImageIcon initializeImageIcon(String iconName) {
		return initializeResourceIcon("/res/" + iconName + "Icon.png");
	}
	
	private static ImageIcon initializeResourceIcon(String resourceName) {
		try {
			URL url = GUIMain.class.getResource(resourceName);
			if(url != null)
			{
			BufferedImage iconBufferedImage = ImageIO.read(url);
			if(iconBufferedImage != null)
			{
				iconBufferedImage = Scalr.resize(iconBufferedImage, Method.QUALITY, iconSizeX, iconSizeY, Scalr.OP_ANTIALIAS);
				return new ImageIcon(iconBufferedImage);
			}
			else return new ImageIcon();
			}
			return new ImageIcon();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
		
	private void tweakLookAndFeel(JToolBar toolbar) {
		
		// tweak the Metal look and feel
		if (UIManager.getLookAndFeel().getID() == "Metal") {

			// paint button borders on mouseover only

			MouseListener hoverListener = new MouseListener() {
				@Override
				public void mouseExited(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorderPainted(false);
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					((JButton)arg0.getSource()).setBorderPainted(true);
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) { }
				@Override
				public void mouseReleased(MouseEvent arg0) { }				
				@Override
				public void mousePressed(MouseEvent arg0) { }
			};
			
			for(Component comp : toolbar.getComponents()) {
				if (comp instanceof JButton) {
					JButton button = (JButton)comp;
					button.setBorderPainted(false);
					button.addMouseListener(hoverListener);
				}
			}

			// this will paint the whole background using a plain color 
			// instead of using a gradient for borders and separators
			toolbar.setBackground(new Color(toolbar.getBackground().getRGB()));
			toolbar.setBorderPainted(false);
		}
		// tweak the GTK look and feel
		else if (UIManager.getLookAndFeel().getID() == "GTK"){
			// keep from drawing a bottom toolbar border even with null or empty borders
			toolbar.setBorder(BorderFactory.createLineBorder(getBackground()));
		}
	}
		
	private void add(JToolBar toolbar) {
		for(Component comp : toolbar.getComponents()) {
			if (comp instanceof JButton) {
				JButton button = (JButton)comp;
				button.setFocusable(false);
			}
		}
		
		toolbar.addSeparator();
		toolbar.setFloatable(false);
		toolbar.setFocusable(false);
		
		tweakLookAndFeel(toolbar);
				
		box.add(toolbar);
	}
	
	private Action findScraperAction(MenuElement scrapeMenu, String scraperKey) {
		
		if (scrapeMenu instanceof JMenuItem){
			JMenuItem menuItem = (JMenuItem)scrapeMenu;
			Action action = menuItem.getAction();
			if (action != null){
				String key = (String)action.getValue(ScrapeAmalgamatedAction.SCRAPE_KEY);
				if (key != null && key.equals(scraperKey)){
					return action;
				}
			}
		}
		
		for(MenuElement childMenu: scrapeMenu.getSubElements()){
			Action childAction = findScraperAction(childMenu, scraperKey);
			if (childAction != null){
				return childAction;
			}
		}
		
		return null;
	}
	
	private void initializeButtons()
	{
		initializeDirectoryButtons();
		initializeScrapeButtons();
		initializeFileButtons();
	}

	private void initializeDirectoryButtons() {
		JToolBar directoryOperationsButtons = new JToolBar("Directory");
		
		//Button to go up a directory for the current directory
		JButton btnUpDirectory = new JButton();
		btnUpDirectory.addActionListener(new UpDirectoryAction(guiMain));
		btnUpDirectory.setIcon(initializeImageIcon("Up"));
		btnUpDirectory.setToolTipText("Go to parent directory");		
		
		//Button to bring up a file chooser so the user can browse and pick what directory they want to view
		JButton btnBrowseDirectory = new JButton("Browse");
		btnBrowseDirectory.addActionListener(new BrowseDirectoryAction(guiMain));
		btnBrowseDirectory.setIcon(initializeImageIcon("BrowseDirectory"));
		btnBrowseDirectory.setToolTipText("Browse directory");
		
		JButton btnRefreshDirectory = new JButton();
		btnRefreshDirectory.addActionListener(new RefreshDirectoryAction(guiMain));
		btnRefreshDirectory.setIcon(initializeImageIcon("Refresh"));
		btnRefreshDirectory.setToolTipText("Refresh current directory");
		
		directoryOperationsButtons.add(btnBrowseDirectory);
		directoryOperationsButtons.add(btnUpDirectory);
		directoryOperationsButtons.add(btnRefreshDirectory);
		
		add(directoryOperationsButtons);
	}

	private void initializeFileButtons()
	{
		JToolBar fileOperationsButtons = new JToolBar("File");
		
		btnWriteFileData = new JButton("Write File Data");
		btnWriteFileData.setEnabled(false); //this becomes enabled later when an actual movie is available to write out
		btnWriteFileData.addActionListener(new WriteFileDataAction(guiMain));
		btnWriteFileData.setToolTipText("Write out the .nfo file to disk. The movie must have a title for this to be enabled.");
		btnWriteFileData.setIcon(initializeImageIcon("SaveButton"));
		
		JButton btnMoveFileToFolder = new JButton();
		btnMoveFileToFolder.setAction(new MoveToNewFolderAction(guiMain));
		btnMoveFileToFolder.setToolTipText("Create a folder for the file and put the file and any associated files in that new folder.");
		btnMoveFileToFolder.setIcon(initializeImageIcon("FileFolder"));
		
		JButton openCurrentlySelectedFileButton = new JButton("Open File");
		openCurrentlySelectedFileButton.addActionListener(new OpenFileAction(guiMain));
		openCurrentlySelectedFileButton.setToolTipText("Open the currently selected file with the system default program for it");
		openCurrentlySelectedFileButton.setIcon(initializeImageIcon("Open"));
		
		JButton playCurrentlySelectedMovieButton = new JButton("Play Movie");
		playCurrentlySelectedMovieButton.addActionListener(new PlayMovieAction(guiMain));
		playCurrentlySelectedMovieButton.setToolTipText("<html>Play the currently selected movie file using the external movie player defined in the settings menu.<br>If the movie contains stacked files, all files will be added to the external movie player's playlist in alphabetical order.<br>Trailer files will be automatically excluded from the playlist.</html>");
		playCurrentlySelectedMovieButton.setIcon(initializeImageIcon("Play"));
		
		JButton fileNameCleanupButton = new JButton("Clean Up File Name");
		fileNameCleanupButton.addActionListener(new FileNameCleanupAction(guiMain));
		fileNameCleanupButton.setToolTipText("Attempts to rename a file of a web content release before scraping so that it is more likely to find a match. I'm still working on adding more site abbreviations, so this feature is experimental for now.");
		fileNameCleanupButton.setIcon(initializeImageIcon("FixFileName"));
		
		fileOperationsButtons.add(openCurrentlySelectedFileButton);
		fileOperationsButtons.add(playCurrentlySelectedMovieButton);
		fileOperationsButtons.add(btnWriteFileData);
		fileOperationsButtons.add(btnMoveFileToFolder);
		fileOperationsButtons.add(fileNameCleanupButton);
	
		add(fileOperationsButtons);
	}
	
	private void initializeScrapeButtons()
	{
		JToolBar scrapeButtons = new JToolBar("Scrape");

		ImageIcon arrowIcon = initializeImageIcon("Arrow");
		ImageIcon japanIcon = initializeImageIcon("Japan");
		//ImageIcon data18Icon = initializeImageIcon("Data18");
		ImageIcon appIcon = initializeImageIcon("App");
		
		final JButton scrapeButton = new JButton();
		final JButton arrowButton =  new JButton(arrowIcon);
		final JPopupMenu scrapeMenu = new JPopupMenu();
	
		arrowButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrapeMenu.show(arrowButton, 0, arrowButton.getHeight());
			}
		});

		ActionListener scrapeActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Action action = ((JMenuItem)e.getSource()).getAction(); 
				scrapeButton.setAction(action);
				String scraperKey = (String)action.getValue(ScrapeAmalgamatedAction.SCRAPE_KEY);
				guiMain.getGuiSettings().setLastUsedScraper(scraperKey);
			}
		};
		
		Action scrapeAdultDVDAmalgamatedAction = new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP));
		Action scrapeJAVAmalgamatedAction = new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP));
		
		scrapeAdultDVDAmalgamatedAction.putValue(Action.SMALL_ICON, appIcon);
		scrapeJAVAmalgamatedAction.putValue(Action.SMALL_ICON, japanIcon);

		
		scrapeMenu.add(scrapeAdultDVDAmalgamatedAction).addActionListener(scrapeActionListener);
		scrapeMenu.add(scrapeJAVAmalgamatedAction).addActionListener(scrapeActionListener);
		
		JMenu specificMenu = new JMenu("Specific Scrape");
		scrapeMenu.add(specificMenu);
		
		for(SiteParsingProfileItem item: SpecificProfileFactory.getAll()){
			JMenuItem menuItem = new JMenuItem();
			SiteParsingProfile profile = item.getParser();
			ImageIcon icon = profile.getProfileIcon();
			Action scrapeAction = new ScrapeAmalgamatedAction(guiMain, profile);
			String siteName = item.toString();
			scrapeAction.putValue(Action.SMALL_ICON, icon);
			menuItem.setAction(scrapeAction);
			menuItem.setText(siteName);
			menuItem.addActionListener(scrapeActionListener);
			specificMenu.add(menuItem);
		}
		
		String lastUsedScraper = guiMain.getGuiSettings().getLastUsedScraper();
		Action scrapeAction = scrapeJAVAmalgamatedAction;
		
		if (lastUsedScraper != null){
			Action lastScrapeAction = findScraperAction(scrapeMenu, lastUsedScraper);
			if (lastScrapeAction != null)
				scrapeAction = lastScrapeAction;
		}
		
		scrapeButton.setAction(scrapeAction);
		
		scrapeButtons.add(scrapeButton);
		scrapeButtons.add(arrowButton);
		
		add(scrapeButtons);
	}
	
	public void disableWriteFile() {
		if (btnWriteFileData != null) {
			btnWriteFileData.setEnabled(false);
		}
	}
	
	public void enableWriteFile() {
		if (btnWriteFileData != null) {
			btnWriteFileData.setEnabled(true);
		}
	}
}
