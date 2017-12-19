package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.MovieFilenameFilter;
import moviescraper.doctord.model.preferences.GuiSettings;
import moviescraper.doctord.view.GUIMain;

public class PlayMovieAction implements ActionListener {

	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public PlayMovieAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	/**
	 * Play the selected movie (and any associated movies that are stacked in case the movie file is split into multiple pieces) 
	 * in the user's preferred external media player. If no external media player is known, the program 
	 * will prompt the user to set one before playing the file.
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		for (int movieNumberInList = 0; movieNumberInList < this.guiMain.getCurrentlySelectedMovieFileList().size(); movieNumberInList++) {
			if (this.guiMain.getCurrentlySelectedMovieFileList() != null) {
				File currentMovieFile = this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList);
				if (currentMovieFile.exists()) {
					//Get the movie file from within the directory
					if (currentMovieFile.isDirectory()) {
						File[] movieFilesInFolder = currentMovieFile.listFiles(new MovieFilenameFilter());
						if (movieFilesInFolder.length > 0) {
							//Filter out the trailer files and then sort the list so we play any stacked movies in sequential order
							List<File> fileList = new LinkedList<>(Arrays.asList(movieFilesInFolder));
							fileList = filterFiles(fileList);
							Collections.sort(fileList);

							playItems(fileList, arg0);
						}
					}
					//We are selecting an actual movie file. Get all stacked movie files and play them in alphabetical order. Remove trailer files as well from our playlist
					else {
						List<File> stackedMovieFiles = new LinkedList<>();

						File currentDirectory = this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).getParentFile();
						String currentlySelectedMovieFileWihoutStackSuffix = SiteParsingProfile
								.stripDiscNumber(FilenameUtils.removeExtension(this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).getName()));
						if (currentDirectory != null) {

							for (File currentFile : currentDirectory.listFiles(new MovieFilenameFilter())) {
								String currentFileNameWithoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(currentFile.getName()));
								if (currentFile.isFile() && currentFileNameWithoutStackSuffix.equals(currentlySelectedMovieFileWihoutStackSuffix)) {
									stackedMovieFiles.add(currentFile);
								}
							}
						}

						Collections.sort(stackedMovieFiles);
						stackedMovieFiles = filterFiles(stackedMovieFiles);
						playItems(stackedMovieFiles, arg0);
					}
				}
			}

		}
	}

	private void playItems(List<File> fileList, ActionEvent arg0) {
		String pathToExternalMediaPlayer = GuiSettings.getInstance().getPathToExternalMediaPlayer();
		if (pathToExternalMediaPlayer != null)
			openMediaFilesInExternalProgram(fileList, pathToExternalMediaPlayer);
		//We don't know what program to play the movies in, so let's ask the user for a program
		else {
			Object[] options = { "Yes", "No" };
			int optionPicked = JOptionPane.showOptionDialog(guiMain.getFrmMoviescraper(), "No external media player set. Would you like to set one now?",
					"Configure External Media Player", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (optionPicked == 0) {
				ChooseExternalMediaPlayerAction chooseExternalMediaPlayerAction = new ChooseExternalMediaPlayerAction(guiMain);
				chooseExternalMediaPlayerAction.actionPerformed(arg0);
				pathToExternalMediaPlayer = GuiSettings.getInstance().getPathToExternalMediaPlayer();
				//now that the user has successfully set a path to the media player, let's play it
				if (pathToExternalMediaPlayer != null)
					openMediaFilesInExternalProgram(fileList, pathToExternalMediaPlayer);
			}
		}
	}

	/**
	 * Removes the trailer files from a fileList so that we don't enqueue them in our playlist
	**/
	private List<File> filterFiles(List<File> fileList) {
		int listSize = fileList.size();
		for (int i = listSize - 1; i >= 0; i--) {
			String currentFileBaseName = FilenameUtils.getBaseName(fileList.get(i).toString());
			if (currentFileBaseName.endsWith("-trailer"))
				fileList.remove(i);
		}

		return fileList;
	}

	/**
	 * Opens the files passed in by fileList using the program specified at the path pathToPlayerProgram
	 * @param fileList - list of files to play (multiple files may be passed in so that we can play multipart movie files)
	 * @param pathToPlayerProgram - full system path of the program to use to open the movie files
	 */
	private void openMediaFilesInExternalProgram(List<File> fileList, String pathToPlayerProgram) {
		int i = 0;
		String args = " ";
		String[] cmdarray = new String[fileList.size() + 1];
		cmdarray[i++] = pathToPlayerProgram;
		for (File file : fileList) {
			args += "\"" + file.getAbsolutePath() + "\" ";
			cmdarray[i++] = file.getAbsolutePath();
		}
		try {
			System.out.println("Running command to open External Media Player: \"" + pathToPlayerProgram + "\"" + args);
			Runtime.getRuntime().exec(cmdarray);

		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println("Error while opening external media player: " + e1.getMessage());
			JOptionPane.showMessageDialog(guiMain.getFrmMoviescraper(), e1.getMessage(), "Error opening external media player", JOptionPane.ERROR_MESSAGE);

		}
	}

}
