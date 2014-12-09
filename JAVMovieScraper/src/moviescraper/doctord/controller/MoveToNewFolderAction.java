package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import moviescraper.doctord.Movie;
import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class MoveToNewFolderAction extends AbstractAction {

	/**
	 * 
	 */
	private final GUIMain guiMain;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2250733525782269006L;

	public MoveToNewFolderAction(GUIMain guiMain) {
		this.guiMain = guiMain;
		putValue(NAME, "Move File to New Folder");
		putValue(SHORT_DESCRIPTION, "Move File to New Folder");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String pathSeperator = System.getProperty("file.separator");
		int moviesToMove = guiMain.getCurrentlySelectedMovieFileList().size();
		for(int movieNumberInList = 0; movieNumberInList < moviesToMove; movieNumberInList++)
		{
			try {
				//set the cursor to busy as this could take more than 1 or 2 seconds while files are copied or extrafanart is downloaded from the internet
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (this.guiMain.getCurrentlySelectedMovieFileList() != null
						&& this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).exists() && this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).isFile()) {
					// we can append the movie title to resulting folder name if
					// the movie is scraped, has an ID and generally matches the
					// ID in the filename (assuming the file is only named the
					// ID of the movie)
					String destinationDirectoryPrefix = "";
					if (this.guiMain.movieToWriteToDiskList != null && this.guiMain.movieToWriteToDiskList.size() > 0) {
						
						String possibleID = this.guiMain.movieToWriteToDiskList.get(movieNumberInList).getId().getId()
								.toUpperCase();
						String possibleIDWithoutDash = possibleID.replaceFirst(
								"-", "");
						String fileNameComparingTo = FilenameUtils
								.getBaseName(this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList)
										.getName().toUpperCase());
						if (possibleID.equals(SiteParsingProfile.stripDiscNumber(fileNameComparingTo))
								|| possibleIDWithoutDash
								.equals(SiteParsingProfile.stripDiscNumber(fileNameComparingTo))) {
							destinationDirectoryPrefix = this.guiMain.movieToWriteToDiskList.get(movieNumberInList)
									.getTitle().getTitle() + " - ";
							// replace illegal characters in the movie filename
							// prefix that the OS doesn't allow with blank space
							destinationDirectoryPrefix = destinationDirectoryPrefix
									.replace("^\\.+", "").replaceAll(
											"[\\\\/:*?\"<>|]", "");
						}

					}
					File destDir = new File(
							this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).getParentFile()
							.getCanonicalPath()
							+ pathSeperator
							+ destinationDirectoryPrefix
							+ SiteParsingProfile.stripDiscNumber(FilenameUtils
									.getBaseName(this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList)
											.getName())));
					this.guiMain.clearAllFieldsOfFileDetailPanel();
					//copy over the .actor folder items to the destination folder, but only if the preference is set and the usual sanity checking is done
					if (this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).isFile() && this.guiMain.getCurrentlySelectedActorsFolderList() != null && this.guiMain.getPreferences().getDownloadActorImagesToActorFolderPreference())
					{
						File [] actorFilesToCopy = this.guiMain.actorFolderFiles(movieNumberInList);
						File actorsFolderDestDir = new File(destDir.getPath() + File.separator + ".actors");
						for(File currentFile : actorFilesToCopy)
						{
							FileUtils.copyFileToDirectory(currentFile, actorsFolderDestDir);
						}
					}
					if (this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).exists())
					{
						//In case of stacked movie files (Movies which are split into multiple files such AS CD1, CD2, etc) get the list of all files
						//which are part of this movie's stack
						File currentDirectory = this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).getParentFile();
						String currentlySelectedMovieFileWihoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).getName()));
						if(currentDirectory != null)
						{

							for(File currentFile : currentDirectory.listFiles())
							{
								String currentFileNameWithoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(currentFile.getName()));
								if(currentFile.isFile() && currentFileNameWithoutStackSuffix.equals(currentlySelectedMovieFileWihoutStackSuffix))
								{
									//this should also get the nfo file as a nice side effect
									FileUtils.moveFileToDirectory(currentFile,destDir, true);
								}
							}
						}

					}
					if (this.guiMain.getCurrentlySelectedNfoFileList().get(movieNumberInList).exists())
						FileUtils.moveFileToDirectory(this.guiMain.getCurrentlySelectedNfoFileList().get(movieNumberInList),destDir, true);
					if (this.guiMain.getCurrentlySelectedPosterFileList().get(movieNumberInList).exists()) {
						//if we're going to create folder.jpg file, just grab the poster file we already have and make a copy of it in the new folder
						if(this.guiMain.getPreferences().getCreateFolderJpgEnabledPreference())
						{
							File currentlySelectedFolderJpg = new File(Movie.getFileNameOfFolderJpg(destDir));
							FileUtils.copyFile(this.guiMain.getCurrentlySelectedPosterFileList().get(movieNumberInList), currentlySelectedFolderJpg );
						}
						FileUtils.moveFileToDirectory(this.guiMain.getCurrentlySelectedPosterFileList().get(movieNumberInList), destDir, true);
					}
					if (this.guiMain.getCurrentlySelectedFanartFileList().get(movieNumberInList).exists()) {
						FileUtils.moveFileToDirectory(this.guiMain.getCurrentlySelectedFanartFileList().get(movieNumberInList), destDir, true);
					}

					if(this.guiMain.getCurrentlySelectedTrailerFileList().get(movieNumberInList).exists())
					{
						FileUtils.moveFileToDirectory(this.guiMain.getCurrentlySelectedTrailerFileList().get(movieNumberInList), destDir, true);
					}

					//if we are supposed to write the extrafanart, make sure to write that too

					if(this.guiMain.getPreferences().getExtraFanartScrapingEnabledPreference())
					{
						this.guiMain.writeExtraFanart(destDir, movieNumberInList);
					}



				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
			finally
			{

				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
			}
		}
		// remove all the old references so we aren't tempted to
		// reuse them when updating the GUI
		guiMain.removeOldScrapedMovieReferences();
		guiMain.removeOldSelectedFileReferences();
		this.guiMain.updateFileListModel(this.guiMain.getCurrentlySelectedDirectoryList(), false);
	}

}