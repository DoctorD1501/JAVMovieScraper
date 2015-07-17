package moviescraper.doctord.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import moviescraper.doctord.controller.xmlserialization.XbmcXmlMovieBean;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.view.GUIMain;

public class SelectFileListAction implements ListSelectionListener {
	/**
	 * 
	 */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public SelectFileListAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

			if (this.guiMain.getFileList().getSelectedIndex() == -1) {
				// No selection
				// Clear out old selection references
				this.guiMain.removeOldSelectedFileReferences();

			} else {
				//totally new selection
				//if(guiMain.getFileList().getSelectedValuesList().size() == 1)
					this.guiMain.removeOldSelectedFileReferences();

				
				for(File currentSelectedFile : this.guiMain.getFileList().getSelectedValuesList())
				{
					this.guiMain.getCurrentlySelectedNfoFileList().add(new File(Movie
							.getFileNameOfNfo(currentSelectedFile, this.guiMain.getPreferences().getNfoNamedMovieDotNfo())));
					this.guiMain.getCurrentlySelectedPosterFileList().add(new File(Movie
							.getFileNameOfPoster(currentSelectedFile, this.guiMain.getPreferences().getNoMovieNameInImageFiles())));
					this.guiMain.getCurrentlySelectedFolderJpgFileList().add(new File(Movie
							.getFileNameOfFolderJpg(currentSelectedFile)));
					this.guiMain.getCurrentlySelectedFanartFileList().add(new File(Movie
							.getFileNameOfFanart(currentSelectedFile, this.guiMain.getPreferences().getNoMovieNameInImageFiles())));
					this.guiMain.getCurrentlySelectedTrailerFileList().add(new File(Movie.getFileNameOfTrailer(currentSelectedFile)));
				}

				this.guiMain.debugWriter("nfos after selection: " + this.guiMain.getCurrentlySelectedNfoFileList());
				this.guiMain.debugWriter("posters after selection: " + this.guiMain.getCurrentlySelectedPosterFileList());
				this.guiMain.debugWriter("folderjpgs after selection: " + this.guiMain.getCurrentlySelectedFolderJpgFileList());
				this.guiMain.debugWriter("fanartfiles after selection: " + this.guiMain.getCurrentlySelectedFanartFileList());
				this.guiMain.debugWriter("trailer after selection: " + this.guiMain.getCurrentlySelectedTrailerFileList());


				this.guiMain.setCurrentlySelectedMovieFileList(this.guiMain.getFileList().getSelectedValuesList());



				this.guiMain.updateActorsFolder();

				// clean up old scraped movie results from previous selection
				//this.guiMain.removeOldScrapedMovieReferences();

				//this method is rather ineffecient - it has to reread the entire list's nfo every time
				//instead of just reading ones it doesn't already have!
				boolean readInAnInfo = false;
				for(File currentSelectedFile : this.guiMain.getFileList().getSelectedValuesList())
				{
					File potentialNfoFile = new File(Movie.getFileNameOfNfo(currentSelectedFile, this.guiMain.getPreferences().getNfoNamedMovieDotNfo()));
					List<File> nfoList = guiMain.getCurrentlySelectedNfoFileList();
					int potentialIndex = nfoList.indexOf(potentialNfoFile);
					if(potentialIndex != -1)
					{
						if (nfoList.get(potentialIndex).exists()) {
							readMovieFromNfoFile(guiMain.getCurrentlySelectedNfoFileList().get(potentialIndex));
							readInAnInfo = true;
						}
					}

				}
				if(!readInAnInfo)
				{
					this.guiMain.updateAllFieldsOfFileDetailPanel(false, false);
				}
				this.guiMain.debugWriter("currentlySelectedMovieFileList: " + guiMain.getCurrentlySelectedMovieFileList());
				this.guiMain.debugWriter("movieToWriteToDiskList: " + guiMain.movieToWriteToDiskList.size());
				

			}
		}
	}
	
	protected void readMovieFromNfoFile(File nfoFile) {
		FileInputStream fisTargetFile = null;
		try {
			fisTargetFile = new FileInputStream(nfoFile);
			String targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
			//Sometimes there's some junk before the prolog tag. Do a workaround to remove that junk.
			//This really isn't the cleanest way to do this, but it'll work for now
			//check first to make sure the string even contains <?xml so we don't loop through an invalid file needlessly
			if(targetFileStr.contains("<?xml"))
			{
				while(targetFileStr.length() > 0 && !targetFileStr.startsWith("<?xml"))
				{
					if(targetFileStr.length() > 1)
					{
						targetFileStr = targetFileStr.substring(1,targetFileStr.length());
					}
					else break;
				}
			}
			XbmcXmlMovieBean xmlMovieBean = XbmcXmlMovieBean.makeFromXML(targetFileStr);
			if(xmlMovieBean != null)
			{
				Movie movieFromNfo = xmlMovieBean.toMovie();
				guiMain.movieToWriteToDiskList.add(movieFromNfo);
				if (guiMain.getCurrentlySelectedPosterFileList().get(0).exists()) {
					//we don't want to resize this poster later
					Thumb[] currentPosters = guiMain.movieToWriteToDiskList.get(0).getPosters();
					Thumb fileFromDisk;
					if(currentPosters.length > 0 && currentPosters[0] != null && currentPosters[0].getThumbURL() != null)
						fileFromDisk = new Thumb(guiMain.getCurrentlySelectedPosterFileList().get(0), currentPosters[0].getThumbURL().toString());
					else
					{
						fileFromDisk = new Thumb(guiMain.getCurrentlySelectedPosterFileList().get(0));
						currentPosters = new Thumb[1];
					}
					currentPosters[0] = fileFromDisk;
				}

				// The poster read from the URL is not resized. We used to do a resize here when this was only a jav scraper, but for now i've turned this off
				else if (guiMain.movieToWriteToDiskList.get(0).hasPoster()) {
					//Thumb[] currentPosters = movieToWriteToDiskList.get(0).getPosters();
					//this was the old method before I wrote in method from pythoncovercrop. it is no longer used
					/*currentPosters[0] = new Thumb(currentPosters[0].getThumbURL()
						.toString(), 52.7, 0, 0, 0);*/
					//for now don't resize
					//currentPosters[0] = new Thumb(currentPosters[0].getThumbURL().toString(), true);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);

		}
		finally
		{
			try {
				fisTargetFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				guiMain.updateAllFieldsOfFileDetailPanel(false, false);
			}
		}
	}
}