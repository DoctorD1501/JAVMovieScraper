package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import moviescraper.doctord.view.GUIMain;

public class UpDirectoryAction implements ActionListener {

	/**
	 * 
	 */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public UpDirectoryAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			File parentDirectory = this.guiMain.getCurrentlySelectedDirectoryList().getParentFile();
			if(parentDirectory != null && parentDirectory.exists())
			{
				this.guiMain.setCurrentlySelectedDirectoryList(parentDirectory);
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.guiMain.updateFileListModel(this.guiMain.getCurrentlySelectedDirectoryList(), false);
			}
		}
		finally
		{
			this.guiMain.getGuiSettings().setLastUsedDirectory(this.guiMain.getCurrentlySelectedDirectoryList());
			this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
		}
	}
}