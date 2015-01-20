package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import moviescraper.doctord.GUI.GUIMain;

public class BrowseDirectoryAction implements ActionListener {
	/**
	 * 
	 */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public BrowseDirectoryAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	public void actionPerformed(ActionEvent arg0) {
		this.guiMain.setChooser(new JFileChooser());
		//remember our last used directory and start the search there
		if(this.guiMain.getGuiSettings().getLastUsedDirectory().exists())
			this.guiMain.getChooser().setCurrentDirectory(this.guiMain.getGuiSettings().getLastUsedDirectory());
		this.guiMain.getChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Movies", "avi", "mp4", "wmv", "flv", "mov", "rm", "mkv");
		this.guiMain.getChooser().setFileFilter(filter);
		int returnVal = this.guiMain.getChooser().showOpenDialog(this.guiMain.getFrmMoviescraper());
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			this.guiMain.setCurrentlySelectedDirectoryList(this.guiMain.getChooser().getSelectedFile());

			//display a wait cursor while repopulating the list
			//as this can sometimes be slow
			try{
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.guiMain.updateFileListModel(this.guiMain.getCurrentlySelectedDirectoryList(), false);
			}
			finally
			{
				this.guiMain.getGuiSettings().setLastUsedDirectory(this.guiMain.getCurrentlySelectedDirectoryList());
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
			}


		}
	}
}