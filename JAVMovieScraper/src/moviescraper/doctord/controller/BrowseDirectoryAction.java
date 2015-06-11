package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import moviescraper.doctord.view.GUIMain;

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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.guiMain.setChooser(new JFileChooser());
		//remember our last used directory and start the search there
		if(this.guiMain.getGuiSettings().getLastUsedDirectory().exists())
			this.guiMain.getChooser().setCurrentDirectory(this.guiMain.getGuiSettings().getLastUsedDirectory());
		this.guiMain.getChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.guiMain.getChooser().setAcceptAllFileFilterUsed(false);
		this.guiMain.getChooser().setFileFilter( new FileFilter(){

	            @Override
	            public boolean accept(File f) {
	                return f.isDirectory();
	            }

	            @Override
	            public String getDescription() {
	                return "Directories only";
	            }
	        });
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