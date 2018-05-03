package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.GUIMain;

public class WriteFileDataAction implements ActionListener {

	/**
	 * 
	 */
	private final GUIMain guiMain;
	SwingWorker<Void, String> worker;

	/**
	 * @param guiMain
	 */
	public WriteFileDataAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.guiMain.setMainGUIEnabled(false);
		worker = new SwingWorker<Void, String>() {

			@Override
			protected Void doInBackground() throws Exception {
				for (int movieNumberInList = 0; movieNumberInList < guiMain.getCurrentlySelectedMovieFileList().size(); movieNumberInList++) {
					try {
						//Display a wait cursor since file IO sometimes takes a little bit of time
						guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						// Write the user or automatic selection using amalgamation
						// of different scraping sites
						if (guiMain.movieToWriteToDiskList == null) {
							//I don't think this should happen anymore, just display an error message instead of executing the above code
							System.err.println("Code I thought was not supposed to execute did, WriteFileDataAction, line 59");
						}
						if (guiMain.movieToWriteToDiskList.get(movieNumberInList) == null || !guiMain.movieToWriteToDiskList.get(movieNumberInList).hasValidTitle()) {
							System.out.println("No match for this movie in the array or there was no title filled in; skipping writing");
							continue;
						}
						System.out.println("Writing this movie to file: " + guiMain.movieToWriteToDiskList.get(movieNumberInList).getTitle());
						if (guiMain.movieToWriteToDiskList != null) {
							if (guiMain.getPreferences().getRenameMovieFile()) {
								File oldMovieFile = guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList);
								Movie movie = guiMain.movieToWriteToDiskList.get(movieNumberInList);

								String sanitizerString = MoviescraperPreferences.getSanitizerForFilename();
								String fileRenameString = MoviescraperPreferences.getRenamerString();
								String folderRenameString = MoviescraperPreferences.getFolderRenamerString();
								Renamer renamer = new Renamer(fileRenameString, folderRenameString, sanitizerString, movie, oldMovieFile);
								String newMovieFilename = renamer.getNewFileName(oldMovieFile.isDirectory());
								System.out.println("New Filename : " + newMovieFilename);
								File newMovieFile = new File(newMovieFilename);

								File newMovieDirectoryFile = newMovieFile;
								if (!oldMovieFile.isDirectory()) {
									newMovieDirectoryFile = newMovieFile.getParentFile().getCanonicalFile();
								}
								/*
								 * old method
								boolean renameStatus = oldMovieFile.renameTo(newMovieFile);
								if(!renameStatus)
								{
									System.err.println("There was a problem renaming " + oldMovieFile + " to "+ newMovieFile);
								}*/
								try {
									if (oldMovieFile.isDirectory()) {
										FileUtils.moveDirectory(oldMovieFile, newMovieFile);
									} else if (oldMovieFile.isFile()) {
										/* Faster on network shares to move to directory then rename */
										File oldMovieDirectoryFile = oldMovieFile.getParentFile().getCanonicalFile();
										File oldMovieNameFile = new File(oldMovieFile.getName());
										File newMovieNameFile = new File(newMovieFile.getName());

										System.out.println("==============\n Moving file:");
										System.out.println("   - oldMovieDirectoryFile: " + oldMovieDirectoryFile.getPath());
										System.out.println("   - newMovieDirectoryFile: " + newMovieDirectoryFile.getPath());
										System.out.println("   - oldMovieNameFile: " + oldMovieNameFile.getPath());
										System.out.println("   - newMovieNameFile: " + newMovieNameFile.getPath());
										System.out.println("==============");

										if (!newMovieDirectoryFile.equals(oldMovieDirectoryFile)) {
											// Move to new directory
											FileUtils.moveFileToDirectory(oldMovieFile, newMovieDirectoryFile, true);
										}

										// Create paths based on new directory and rename file
										File newDirOldNameFile = new File(newMovieDirectoryFile, oldMovieNameFile.toString());
										File newDirNewNameFile = new File(newMovieDirectoryFile, newMovieNameFile.toString());

										if(!newDirOldNameFile.equals(newDirNewNameFile)) {
											System.out.println("-----\n Renaming movie file:");
											System.out.println("   - newDirOldNameFile: " + newDirOldNameFile.getPath());
											System.out.println("   - newDirNewNameFile: " + newDirNewNameFile.getPath());
											System.out.println("==============");
											FileUtils.moveFile(newDirOldNameFile, newDirNewNameFile);
										}
									}
								} catch (FileExistsException e) {
									System.out.println("A file or directory already exists at " + newMovieFile + " - skipping overwrite or creation of new folder.");
								}

								guiMain.movieToWriteToDiskList.get(movieNumberInList).writeToFile(
										new File(Movie.getFileNameOfNfo(newMovieDirectoryFile, guiMain.getPreferences().getNfoNamedMovieDotNfo())),
										new File(Movie.getFileNameOfPoster(newMovieDirectoryFile, guiMain.getPreferences().getNoMovieNameInImageFiles())),
										new File(Movie.getFileNameOfFanart(newMovieDirectoryFile, guiMain.getPreferences().getNoMovieNameInImageFiles())),
										new File(Movie.getFileNameOfFolderJpg(newMovieDirectoryFile)), new File(Movie.getFileNameOfExtraFanartFolderName(newMovieDirectoryFile)),
										new File(Movie.getFileNameOfTrailer(newMovieDirectoryFile)), guiMain.getPreferences());
							} else {
								//save without renaming movie
								guiMain.movieToWriteToDiskList.get(movieNumberInList).writeToFile(guiMain.getCurrentlySelectedNfoFileList().get(movieNumberInList),
										guiMain.getCurrentlySelectedPosterFileList().get(movieNumberInList), guiMain.getCurrentlySelectedFanartFileList().get(movieNumberInList),
										guiMain.getCurrentlySelectedFolderJpgFileList().get(movieNumberInList),
										new File(Movie.getFileNameOfExtraFanartFolderName(guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList))),
										new File(Movie.getFileNameOfTrailer(guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList))), guiMain.getPreferences());
							}

							//we can only output extra fanart if we're scraping a folder, because otherwise the extra fanart will get mixed in with other files
							if (guiMain.getPreferences().getExtraFanartScrapingEnabledPreference()
									&& guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).isDirectory()) {
								guiMain.movieToWriteToDiskList.get(movieNumberInList).writeExtraFanart(guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList));
							}
						}
						//now write out the actor images if the user preference is set
						if (guiMain.getPreferences().getDownloadActorImagesToActorFolderPreference() && guiMain.getCurrentlySelectedMovieFileList() != null
								&& guiMain.getCurrentlySelectedDirectoryList() != null) {
							guiMain.movieToWriteToDiskList.get(movieNumberInList).writeActorImagesToFolder(guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList));
						}

						System.out.println("Finished writing a movie file");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
					} finally {
						done();

					}
				}
				return null;
			}

			@Override
			protected void done() {
				guiMain.setMainGUIEnabled(true);
				//out of loop and done writing files, update the gui
				guiMain.updateFileListModel(guiMain.getCurrentlySelectedDirectoryList(), true);
				guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
			}
		}; //end SwingWorker definition
		worker.execute();
	}
}