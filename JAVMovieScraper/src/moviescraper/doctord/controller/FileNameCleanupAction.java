package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.ReleaseRenamer.WebReleaseRenamer;

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
			WebReleaseRenamer renamer = new WebReleaseRenamer();
			for(File currentFile : filesSelected)
			{
				System.out.println("currentFile = " + currentFile);
				System.out.println("currentFileRenamer = " + renamer.getCleanName(currentFile.getName()));
				System.out.println("I want to rename to " + renamer.newFileName(currentFile));
				File newFileName = renamer.newFileName(currentFile);
				boolean renameStatus = currentFile.renameTo(newFileName);
				if(renameStatus != true)
					System.err.println("Rename failed!");
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
