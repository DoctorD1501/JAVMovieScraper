package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.SwingUtilities;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import moviescraper.doctord.view.GUIMain;

public class BrowseDirectoryAction implements ActionListener {
	private final GUIMain guiMain;
	public BrowseDirectoryAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.guiMain.setChooser(createDirectoryFileChooser());
		// remember our last used directory and start the search there
		if (this.guiMain.getGuiSettings().getLastUsedDirectory().exists())
			this.guiMain.getChooser().setInitialDirectory(this.guiMain.getGuiSettings().getLastUsedDirectory());

		// required so we have access to
		// this variable inside the
		// runnable
		final GUIMain myGuiMain = this.guiMain;

		Platform.runLater(new Runnable() {
			@Override
			// run on javafx thread - required since our file chooser is a
			// javafx thing
			public void run() {
				File returnVal = myGuiMain.getChooser().showDialog(null);
				if (returnVal != null) {

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// run back on swing thread - the rest of the
							// program is swing :)
							myGuiMain.setCurrentlySelectedDirectoryList(returnVal);

							// display a wait cursor while repopulating the list
							// as this can sometimes be slow
							try {
								myGuiMain.getFrmMoviescraper()
										.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								myGuiMain.updateFileListModel(myGuiMain.getCurrentlySelectedDirectoryList(), false);
							} finally {
								myGuiMain.getGuiSettings()
										.setLastUsedDirectory(myGuiMain.getCurrentlySelectedDirectoryList());
								myGuiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
							}
						}
					});
				}
			}
		});

	}
	
	private static DirectoryChooser createDirectoryFileChooser() {
		DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Pick Scraping Directory");
        return fileChooser;
	}
}