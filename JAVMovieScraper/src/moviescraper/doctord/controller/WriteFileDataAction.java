package moviescraper.doctord.controller;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import moviescraper.doctord.Movie;
import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.model.Renamer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class WriteFileDataAction implements ActionListener {


	/**
	 * 
	 */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public WriteFileDataAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	public void actionPerformed(ActionEvent arg0) {
		for(int movieNumberInList = 0; movieNumberInList < this.guiMain.getCurrentlySelectedMovieFileList().size(); movieNumberInList++)
		{
			try {
				//Display a wait cursor since file IO sometimes takes a little bit of time
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Write the user or automatic selection using amalgamation
				// of different scraping sites
				if(this.guiMain.movieToWriteToDiskList == null)
				{
					/*Movie amalgamationAutoPickMovie = this.guiMain.amalgamateJAVMovie(
							this.guiMain.getCurrentlySelectedMovieDMM(),
							this.guiMain.getCurrentlySelectedMovieActionJav(),
							this.guiMain.getCurrentlySelectedMovieSquarePlus(),
							this.guiMain.getCurrentlySelectedMovieJavLibrary(),
							this.guiMain.getCurrentlySelectedMovieJavZoo(), movieNumberInList);

					this.guiMain.movieToWriteToDiskList.add(amalgamationAutoPickMovie);*/
					//I don't think this should happen anymore, just display an error message instead of executing the above code
					System.err.println("Code I thought was not supposed to execute did, WriteFileDataAction, line 56");
				}
				if(this.guiMain.movieToWriteToDiskList.get(movieNumberInList) == null)
				{
					System.out.println("No match for this movie in the array, skipping writing");
					continue;
				}
				System.out.println("Writing this movie to file: "
						+ this.guiMain.movieToWriteToDiskList);
				if(this.guiMain.movieToWriteToDiskList != null)
				{
					if ( this.guiMain.getPreferences().getRenameMovieFile() ) {
						////TODO New Filewrite
						System.out.println(this.guiMain.getCurrentlySelectedMovieFileList().get(0));
						File oldMovieFile = this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList);
						Movie movie = this.guiMain.movieToWriteToDiskList.get(movieNumberInList);

						String sanitizerString = this.guiMain.getPreferences().getSanitizerForFilename();
						String renameString = this.guiMain.getPreferences().getRenamerString(); 
						Renamer renamer = new Renamer(renameString, sanitizerString, movie, oldMovieFile);
						String newMovieFilename = renamer.getNewFileName();
						System.out.println( "New Filename : " + newMovieFilename );
						File newMovieFile = new File(newMovieFilename);
						oldMovieFile.renameTo(newMovieFile);

						this.guiMain.movieToWriteToDiskList.get(movieNumberInList).writeToFile(
								new File( Movie.getFileNameOfNfo(newMovieFile, this.guiMain.getPreferences().getNfoNamedMovieDotNfo()) ),
								new File( Movie.getFileNameOfPoster(newMovieFile, this.guiMain.getPreferences().getNoMovieNameInImageFiles()) ),
								new File( Movie.getFileNameOfFanart(newMovieFile, this.guiMain.getPreferences().getNoMovieNameInImageFiles())),
								new File( Movie.getFileNameOfFolderJpg(newMovieFile) ),
								this.guiMain.getPreferences());							
					} else {
						//save without renaming movie
						this.guiMain.movieToWriteToDiskList.get(movieNumberInList).writeToFile(
								this.guiMain.getCurrentlySelectedNfoFileList().get(movieNumberInList),
								this.guiMain.getCurrentlySelectedPosterFileList().get(movieNumberInList),
								this.guiMain.getCurrentlySelectedFanartFileList().get(movieNumberInList),
								this.guiMain.getCurrentlySelectedFolderJpgFileList().get(movieNumberInList),
								this.guiMain.getPreferences());
					}

					//we can only output extra fanart if we're scraping a folder, because otherwise the extra fanart will get mixed in with other files
					if(this.guiMain.getPreferences().getExtraFanartScrapingEnabledPreference() && this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList).isDirectory() && this.guiMain.getCurrentlySelectedExtraFanartFolderList() != null)
					{
						this.guiMain.writeExtraFanart(null, movieNumberInList);
					}
				}
				//now write out the actor images if the user preference is set
				if(this.guiMain.getPreferences().getDownloadActorImagesToActorFolderPreference() && this.guiMain.getCurrentlySelectedMovieFileList() != null && this.guiMain.getCurrentlySelectedDirectoryList() != null)
				{
					this.guiMain.updateActorsFolder();
					//Don't create an empty .actors folder with no actors underneath it
					if(this.guiMain.movieToWriteToDiskList.get(movieNumberInList).hasAtLeastOneActorThumbnail() && this.guiMain.getCurrentlySelectedActorsFolderList() != null)
					{
						//File actorsFolder = new File(currentlySelectedMovieFile.getPath() + File.seperator + ".actors");
						FileUtils.forceMkdir(this.guiMain.getCurrentlySelectedActorsFolderList().get(movieNumberInList));
						//on windows this new folder should have the hidden attribute; on unix it is already "hidden" by having a . in front of the name
						Path path = this.guiMain.getCurrentlySelectedActorsFolderList().get(movieNumberInList).toPath();
						Boolean hidden = (Boolean) Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS);
						if (hidden != null && !hidden) {
							Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
						}

						for(Actor currentActor : this.guiMain.movieToWriteToDiskList.get(movieNumberInList).getActors())
						{
							String currentActorToFileName = currentActor.getName().replace(' ', '_');
							File fileNameToWrite = new File(this.guiMain.getCurrentlySelectedActorsFolderList().get(movieNumberInList).getPath() + File.separator + currentActorToFileName + ".jpg");
							currentActor.writeImageToFile(fileNameToWrite);
						}

					}

				}

				//write out the trailer to disk, if the preference for it is enabled
				Trailer trailerToWrite = this.guiMain.movieToWriteToDiskList.get(movieNumberInList).getTrailer();
				if(this.guiMain.getPreferences().getWriteTrailerToFile() && trailerToWrite != null && trailerToWrite.getTrailer().length() > 0)
				{
					//don't rewrite a file if it already exists since a trailer requires downloading from the web. this is a slow operation!
					if(!this.guiMain.getCurrentlySelectedTrailerFileList().get(movieNumberInList).exists())
					{
						System.out.println("Starting write of " + trailerToWrite.getTrailer() + " into file " + this.guiMain.getCurrentlySelectedTrailerFileList().get(movieNumberInList));
						trailerToWrite.writeTrailerToFile(this.guiMain.getCurrentlySelectedTrailerFileList().get(movieNumberInList));
					}
				}
				System.out.println("Finished writing a movie file");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
				JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			}
			finally{
				this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
			}
		}
		//out of loop and done writing files, update the gui
		this.guiMain.updateFileListModel(this.guiMain.getCurrentlySelectedDirectoryList(), true);
	}
}