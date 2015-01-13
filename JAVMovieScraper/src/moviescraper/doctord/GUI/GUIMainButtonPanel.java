package moviescraper.doctord.GUI;

import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import moviescraper.doctord.controller.FileNameCleanupAction;
import moviescraper.doctord.controller.MoveToNewFolderAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.ScrapeMovieAction;
import moviescraper.doctord.controller.ScrapeMovieActionAutomatic;
import moviescraper.doctord.controller.ScrapeMovieActionData18Movie;
import moviescraper.doctord.controller.ScrapeMovieActionData18WebContent;
import moviescraper.doctord.controller.WriteFileDataAction;

public class GUIMainButtonPanel extends JPanel {

	public GUIMainButtonPanel(GUIMain guiMain)
	{
		//used in the write to file button
		ImageIcon saveIcon = guiMain.initializeImageIcon("SaveButton");

		//used in the scrape data18 buttons
		ImageIcon data18Icon = guiMain.initializeImageIcon("Data18");

		//used for scraping japanese movies
		ImageIcon japanIcon = guiMain.initializeImageIcon("Japan");

		//open the file icon
		ImageIcon openIcon = guiMain.initializeImageIcon("Open");

		//move to new folder icon
		ImageIcon moveToFolderIcon = guiMain.initializeImageIcon("FileFolder");
		
		//Fix file name icon
		ImageIcon fixFileNameIcon = guiMain.initializeImageIcon("FixFileName");
		
		
		JToolBar specificParserToolbar = new JToolBar("Specific");
		JToolBar scrapeButtons = new JToolBar("Scrape");
		JToolBar fileOperationsButtons = new JToolBar("File");

		JComponent parserPanel = new SpecificParserPanel(guiMain);
		parserPanel.setLayout(new BoxLayout(parserPanel, BoxLayout.X_AXIS));
		//parserPanel.setPreferredSize(new Dimension(200,50));
		specificParserToolbar.add(parserPanel);

		JButton btnScrapeSelectMovieJAV = new JButton("Scrape JAV (Manual)");
		btnScrapeSelectMovieJAV.setAction(new ScrapeMovieAction(guiMain));
		btnScrapeSelectMovieJAV.setIcon(japanIcon);
		scrapeButtons.add(btnScrapeSelectMovieJAV);
		
		JButton btnScrapeSelectMovieJAVAutomatic = new JButton("Scrape JAV (Automatic)");
		btnScrapeSelectMovieJAVAutomatic.setAction(new ScrapeMovieActionAutomatic(guiMain));
		btnScrapeSelectMovieJAVAutomatic.setIcon(japanIcon);
		scrapeButtons.add(btnScrapeSelectMovieJAVAutomatic);

		JButton btnScrapeSelectMovieData18Movie = new JButton("Scrape Data18 Movie");
		btnScrapeSelectMovieData18Movie.setAction(new ScrapeMovieActionData18Movie(guiMain));
		btnScrapeSelectMovieData18Movie.setIcon(data18Icon);
		scrapeButtons.add(btnScrapeSelectMovieData18Movie);

		JButton btnScrapeSelectMovieData18WebContent = new JButton("Scrape Data18 Web Content");
		btnScrapeSelectMovieData18WebContent.setAction(new ScrapeMovieActionData18WebContent(guiMain));
		btnScrapeSelectMovieData18WebContent.setIcon(data18Icon);
		scrapeButtons.add(btnScrapeSelectMovieData18WebContent);

		JButton btnWriteFileData = new JButton("Write File Data");
		btnWriteFileData.setToolTipText("Write out the .nfo file to disk");
		btnWriteFileData.setIcon(saveIcon);
		btnWriteFileData.addActionListener(new WriteFileDataAction(guiMain));
		fileOperationsButtons.add(btnWriteFileData);

		JButton btnMoveFileToFolder = new JButton();
		btnMoveFileToFolder.setAction(new MoveToNewFolderAction(guiMain));
		btnMoveFileToFolder.setToolTipText("Create a folder for the file and put the file and any associated files in that new folder.");
		btnMoveFileToFolder.setIcon(moveToFolderIcon);
		fileOperationsButtons.add(btnMoveFileToFolder);

		JButton openCurrentlySelectedFileButton = new JButton("Open File");
		openCurrentlySelectedFileButton.setToolTipText("Open the currently selected file with the system default program for it");
		openCurrentlySelectedFileButton.addActionListener(new OpenFileAction(guiMain));
		openCurrentlySelectedFileButton.setIcon(openIcon);
		fileOperationsButtons.add(openCurrentlySelectedFileButton);
		
		JButton fileNameCleanupButton = new JButton("Clean Up File Name");
		fileNameCleanupButton
				.setToolTipText("Attempts to rename a file of a web content release before scraping so that it is more likely to find a match. I'm still working on adding more site abbreviations, so this feature is experimental for now.");
		fileNameCleanupButton.setIcon(fixFileNameIcon);
		fileNameCleanupButton.addActionListener(new FileNameCleanupAction(guiMain));
		fileOperationsButtons.add(fileNameCleanupButton);
		
		JPanel toolbarPanel = this;
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
		toolbarPanel.add(scrapeButtons);
		toolbarPanel.add(fileOperationsButtons);
		toolbarPanel.add(specificParserToolbar);
	}
	
}
