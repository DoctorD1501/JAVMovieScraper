package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import moviescraper.doctord.controller.releaserenamer.WebReleaseRenamer;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.GUIMain;

public class FileNameCleanupAction implements ActionListener {

	GUIMain guiMain;

	public FileNameCleanupAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<File> filesSelected = guiMain.getCurrentlySelectedMovieFileList();
		System.out.println("Files selected = " + filesSelected);
		try {
			WebReleaseRenamer webReleaseRenamer = new WebReleaseRenamer();
			for(File currentFile : filesSelected)
			{
				System.out.println("currentFile = " + currentFile);
				System.out.println("currentFileRenamer = " + webReleaseRenamer.getCleanName(currentFile.getName()));
				System.out.println("I want to rename to " + webReleaseRenamer.newFileName(currentFile));
				File newFileName = webReleaseRenamer.newFileName(currentFile);
				if(currentFile.getName().equals(newFileName.getName()))
				{
					System.out.println("New file and old file are the same. No rename necessary.");
				}
				else if(guiMain != null && MoviescraperPreferences.getInstance().getConfirmCleanUpFileNameNameBeforeRenaming())
				{
					int optionPicked = JOptionPane.showConfirmDialog(
							guiMain.getFrmMoviescraper(),
							"<html>Rename <b>" + currentFile + "</b><br> to <b>" + newFileName + "</b> ?</html>",
							"Confirm Rename",
							JOptionPane.YES_NO_OPTION);

					if(optionPicked == JOptionPane.YES_OPTION)
					{
						boolean renameStatus = currentFile.renameTo(newFileName);
						if(renameStatus != true)
							System.err.println("Rename failed! Perhaps a file name already exists with that name?");
					}
				}
				else
				{
					boolean renameStatus = currentFile.renameTo(newFileName);
					if(renameStatus != true)
						System.err.println("Rename failed! Perhaps a file name already exists with that name?");
				}	
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			guiMain.updateFileListModel(guiMain.getCurrentlySelectedDirectoryList(), false);
		}


	}

}
