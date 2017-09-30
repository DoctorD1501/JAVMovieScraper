package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import moviescraper.doctord.view.GUIMain;

public class RefreshDirectoryAction implements ActionListener {

	/**
	    *
	    */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public RefreshDirectoryAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			File currentDirectory = this.guiMain.getCurrentlySelectedDirectoryList();
			if (currentDirectory != null && currentDirectory.exists()) {
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.guiMain.updateFileListModel(currentDirectory, true);
			}
		} finally {
			this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
		}
	}
}
