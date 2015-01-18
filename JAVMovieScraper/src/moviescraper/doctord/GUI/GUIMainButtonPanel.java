package moviescraper.doctord.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.SiteParsingProfile.SpecificProfileFactory;
import moviescraper.doctord.controller.BrowseDirectoryAction;
import moviescraper.doctord.controller.FileNameCleanupAction;
import moviescraper.doctord.controller.MoveToNewFolderAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.RefreshDirectoryAction;
import moviescraper.doctord.controller.ScrapeMovieAction;
import moviescraper.doctord.controller.ScrapeMovieActionAutomatic;
import moviescraper.doctord.controller.ScrapeMovieActionData18Movie;
import moviescraper.doctord.controller.ScrapeMovieActionData18WebContent;
import moviescraper.doctord.controller.ScrapeSpecificAction;
import moviescraper.doctord.controller.UpDirectoryAction;
import moviescraper.doctord.controller.WriteFileDataAction;

public class GUIMainButtonPanel extends JPanel {
	
	private static final int iconSizeX = 16;
	private static final int iconSizeY = 16;
	
	private GUIMain guiMain;
	
	public GUIMainButtonPanel(GUIMain guiMain)
	{
		this.guiMain = guiMain;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		initializeButtons();
	}
	
	private ImageIcon initializeImageIcon(String iconName) {
		return initializeResourceIcon("/res/" + iconName + "Icon.png");
	}
	
	private ImageIcon initializeProfileIcon(SiteParsingProfile profile)	{
		String profileName = profile.getClass().getSimpleName();
		String siteName = profileName.replace("ParsingProfile", "");
		return initializeResourceIcon("/res/sites/" + siteName + ".png");
	}
	
	private ImageIcon initializeResourceIcon(String resourceName) {
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
	
	private void add(JToolBar toolbar) {
		for(Component comp : toolbar.getComponents()) {
			if (comp instanceof JButton) {
				JButton button = (JButton)comp;
				button.setBorderPainted(false);
				button.setFocusable(false);
			}
		}
		
		toolbar.addSeparator();
		toolbar.setFloatable(false);
		toolbar.setFocusable(false);
		toolbar.setBorderPainted(false);
		
		// Workaround for the Metal look and feel:
		// this will paint the whole background using a plain color 
		// instead of using a gradient for borders and separators
		
		if (UIManager.getLookAndFeel().getID() == "Metal")
			toolbar.setBackground(new Color(toolbar.getBackground().getRGB()));
				
		super.add(toolbar);
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
		
		JButton btnWriteFileData = new JButton("Write File Data");
		btnWriteFileData.addActionListener(new WriteFileDataAction(guiMain));
		btnWriteFileData.setToolTipText("Write out the .nfo file to disk");
		btnWriteFileData.setIcon(initializeImageIcon("SaveButton"));
		
		JButton btnMoveFileToFolder = new JButton();
		btnMoveFileToFolder.setAction(new MoveToNewFolderAction(guiMain));
		btnMoveFileToFolder.setToolTipText("Create a folder for the file and put the file and any associated files in that new folder.");
		btnMoveFileToFolder.setIcon(initializeImageIcon("FileFolder"));
		
		JButton openCurrentlySelectedFileButton = new JButton("Open File");
		openCurrentlySelectedFileButton.addActionListener(new OpenFileAction(guiMain));
		openCurrentlySelectedFileButton.setToolTipText("Open the currently selected file with the system default program for it");
		openCurrentlySelectedFileButton.setIcon(initializeImageIcon("Open"));
		
		JButton fileNameCleanupButton = new JButton("Clean Up File Name");
		fileNameCleanupButton.addActionListener(new FileNameCleanupAction(guiMain));
		fileNameCleanupButton.setToolTipText("Attempts to rename a file of a web content release before scraping so that it is more likely to find a match. I'm still working on adding more site abbreviations, so this feature is experimental for now.");
		fileNameCleanupButton.setIcon(initializeImageIcon("FixFileName"));
		
		fileOperationsButtons.add(openCurrentlySelectedFileButton);
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
		ImageIcon data18Icon = initializeImageIcon("Data18");
		
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
				scrapeButton.setAction(action);;
			}
		};
		
		Action scrapeJavAction = new ScrapeMovieAction(guiMain);
		Action scrapeJavAutoAction = new ScrapeMovieActionAutomatic(guiMain);
		Action scrapeData18MovieAction = new ScrapeMovieActionData18Movie(guiMain);
		Action scrapeData18WebContentAction = new ScrapeMovieActionData18WebContent(guiMain);
		
		scrapeJavAction.putValue(Action.SMALL_ICON, japanIcon);	
		scrapeJavAutoAction.putValue(Action.SMALL_ICON, japanIcon);	
		scrapeData18MovieAction.putValue(Action.SMALL_ICON, data18Icon);
		scrapeData18WebContentAction.putValue(Action.SMALL_ICON, data18Icon);

		scrapeMenu.add(scrapeJavAction).addActionListener(scrapeActionListener);
		scrapeMenu.add(scrapeJavAutoAction).addActionListener(scrapeActionListener);
		scrapeMenu.add(scrapeData18MovieAction).addActionListener(scrapeActionListener);
		scrapeMenu.add(scrapeData18WebContentAction).addActionListener(scrapeActionListener);
		
		JMenu specificMenu = new JMenu("Specific Scrape");
		scrapeMenu.add(specificMenu);
		
		for(SiteParsingProfileItem item: SpecificProfileFactory.getAll()){
			JMenuItem menuItem = new JMenuItem();
			SiteParsingProfile profile = item.getParser();
			ImageIcon icon = initializeProfileIcon(profile);
			Action scrapeAction = new ScrapeSpecificAction(guiMain, profile);
			String siteName = item.toString();
			scrapeAction.putValue(Action.NAME, "Scrape " + siteName);
			scrapeAction.putValue(Action.SMALL_ICON, icon);
			menuItem.setAction(scrapeAction);
			menuItem.setText(siteName);
			menuItem.addActionListener(scrapeActionListener);
			specificMenu.add(menuItem);
		}
		
		scrapeButton.setAction(scrapeJavAutoAction);
		
		scrapeButtons.add(scrapeButton);
		scrapeButtons.add(arrowButton);
		
		add(scrapeButtons);
	}	
}
