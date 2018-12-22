package moviescraper.doctord.model;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import moviescraper.doctord.controller.FileDownloaderUtilities;
import moviescraper.doctord.controller.siteparsingprofile.SecurityPassthrough;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18WebContentParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.DmmParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.IAFDParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.controller.xmlserialization.KodiXmlMovieBean;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class Movie {

	/*
	 * Be careful if you decide you want to change the field names in this class (especially the arrays)
	 * because reflection is used in the movie amalgamation routine to get these fields by name, so you will need to
	 * update the references in the reflective call with the new name as well.
	 */
	private ArrayList<Actor> actors;
	private ArrayList<Director> directors;
	private Thumb[] fanart;
	private Thumb[] extraFanart;
	private Thumb preferredFanartToWriteToDisk;
	private ArrayList<Genre> genres;
	private ID id;
	private MPAARating mpaa;
	private OriginalTitle originalTitle;
	private Outline outline;
	private Plot plot;
	private Thumb[] posters;
	private Rating rating;
	private ReleaseDate releaseDate;
	private Runtime runtime;
	private Set set;
	private SortTitle sortTitle;
	private Studio studio;

	private Tagline tagline;
	private ArrayList<Tag> tags;

	private Title title;

	private List<Title> allTitles = new ArrayList<>(); //this is currently not used for much; it used to allow the user to select from one of each title in a drop down box on the file detail panel, but now that amalgamation is here, that feature is not needed as much. It may make sense to put in a generic way to handle selecting between data item sources from amalgamation on a per item basis in the file detail panel, however

	private Top250 top250;

	private Trailer trailer;

	private Votes votes;

	private Year year;

	private String fileName;

	public Movie(ArrayList<Actor> actors, ArrayList<Director> directors, Thumb[] fanart, Thumb[] extraFanart, ArrayList<Genre> genres, ArrayList<Tag> tags, ID id, MPAARating mpaa,
	        OriginalTitle originalTitle, Outline outline, Plot plot, Thumb[] posters, Rating rating, ReleaseDate releaseDate, Runtime runtime, Set set, SortTitle sortTitle, Studio studio,
	        Tagline tagline, Title title, Top250 top250, Trailer trailer, Votes votes, Year year) {
		super();
		this.actors = actors;
		this.directors = directors;
		this.fanart = fanart;
		this.extraFanart = extraFanart;
		this.genres = genres;
		this.tags = tags;
		this.id = id;
		this.mpaa = mpaa;
		this.originalTitle = originalTitle;
		this.outline = outline;
		this.plot = plot;
		this.posters = posters;
		this.rating = rating;
		this.releaseDate = releaseDate;
		this.runtime = runtime;
		this.set = set;
		this.sortTitle = sortTitle;
		this.studio = studio;
		this.tagline = tagline;
		this.title = title;
		this.top250 = top250;
		this.trailer = trailer;
		this.votes = votes;
		this.year = year;
	}

	public Movie(SiteParsingProfile siteToScrapeFrom) {
		title = siteToScrapeFrom.scrapeTitle();

		originalTitle = siteToScrapeFrom.scrapeOriginalTitle();
		sortTitle = siteToScrapeFrom.scrapeSortTitle();
		set = siteToScrapeFrom.scrapeSet();
		rating = siteToScrapeFrom.scrapeRating();
		year = siteToScrapeFrom.scrapeYear();
		top250 = siteToScrapeFrom.scrapeTop250();
		trailer = siteToScrapeFrom.scrapeTrailer();
		votes = siteToScrapeFrom.scrapeVotes();
		outline = siteToScrapeFrom.scrapeOutline();
		plot = siteToScrapeFrom.scrapePlot();
		tagline = siteToScrapeFrom.scrapeTagline();
		studio = siteToScrapeFrom.scrapeStudio();
		releaseDate = siteToScrapeFrom.scrapeReleaseDate();
		runtime = siteToScrapeFrom.scrapeRuntime();
		posters = siteToScrapeFrom.scrapePosters();
		fanart = siteToScrapeFrom.scrapeFanart();
		extraFanart = siteToScrapeFrom.scrapeExtraFanart();
		mpaa = siteToScrapeFrom.scrapeMPAA();
		id = siteToScrapeFrom.scrapeID();
		actors = siteToScrapeFrom.scrapeActors();
		genres = siteToScrapeFrom.scrapeGenres();
		tags = siteToScrapeFrom.scrapeTags();
		directors = siteToScrapeFrom.scrapeDirectors();

		setAllDataItemSources(siteToScrapeFrom);

		String fileNameOfScrapedMovie = siteToScrapeFrom.getFileNameOfScrapedMovie();
		if (fileNameOfScrapedMovie != null && fileNameOfScrapedMovie.trim().length() > 0) {
			fileName = fileNameOfScrapedMovie;
		}

		MoviescraperPreferences scraperPreferences = MoviescraperPreferences.getInstance();
		if (scraperPreferences.getUseFileNameAsTitle() && fileName != null && fileName.length() > 0) {
			title = new Title(fileName);
			title.setDataItemSource(new DefaultDataItemSource());
		}

		appendIDToStartOfTitle();

	}

	/**
	 * @param siteToScrapeFrom
	 */
	private void setAllDataItemSources(SiteParsingProfile siteToScrapeFrom) {
		originalTitle.setDataItemSource(siteToScrapeFrom);
		title.setDataItemSource(siteToScrapeFrom);
		sortTitle.setDataItemSource(siteToScrapeFrom);
		set.setDataItemSource(siteToScrapeFrom);
		rating.setDataItemSource(siteToScrapeFrom);
		year.setDataItemSource(siteToScrapeFrom);
		top250.setDataItemSource(siteToScrapeFrom);
		trailer.setDataItemSource(siteToScrapeFrom);
		votes.setDataItemSource(siteToScrapeFrom);
		outline.setDataItemSource(siteToScrapeFrom);
		plot.setDataItemSource(siteToScrapeFrom);
		tagline.setDataItemSource(siteToScrapeFrom);
		studio.setDataItemSource(siteToScrapeFrom);
		releaseDate.setDataItemSource(siteToScrapeFrom);
		runtime.setDataItemSource(siteToScrapeFrom);
		setDataItemSourceOnThumbs(posters, siteToScrapeFrom);
		setDataItemSourceOnThumbs(fanart, siteToScrapeFrom);
		setDataItemSourceOnThumbs(extraFanart, siteToScrapeFrom);
		mpaa.setDataItemSource(siteToScrapeFrom);
		id.setDataItemSource(siteToScrapeFrom);

		for (Actor currentActor : actors)
			currentActor.setDataItemSource(siteToScrapeFrom);

		for (Genre currentGenre : genres)
			currentGenre.setDataItemSource(siteToScrapeFrom);

		for (Tag currentTag : tags) {
			currentTag.setDataItemSource(siteToScrapeFrom);
		}

		for (Director currentDirector : directors)
			currentDirector.setDataItemSource(siteToScrapeFrom);
	}

	/**
	 * If the appropriate preference is set, add the ID number to the end of the title field
	 */
	private void appendIDToStartOfTitle() {
		if (MoviescraperPreferences.getInstance().getAppendIDToStartOfTitle() && id != null && id.getId() != null && id.getId().trim().length() > 0 && hasValidTitle()) {
			title.setTitle(id.getId() + " - " + title.getTitle());
		}
	}

	private void setDataItemSourceOnThumbs(Thumb[] thumbs, DataItemSource dataItemSource) {
		for (Thumb thumb : thumbs) {
			thumb.setDataItemSource(dataItemSource);
		}
	}

	/**
	 * Create a movie by reading in a values from a nfo file created by previously scraping the movie and then writing the metadata out to the file
	 * 
	 * @param nfoFile
	 * @throws IOException
	 */
	public static Movie createMovieFromNfo(File nfoFile) throws IOException {
		Movie movieFromNfo = null;
		try (FileInputStream fisTargetFile = new FileInputStream(nfoFile);) {

			String targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
			//Sometimes there's some junk before the prolog tag. Do a workaround to remove that junk.
			//This really isn't the cleanest way to do this, but it'll work for now
			//check first to make sure the string even contains <?xml so we don't loop through an invalid file needlessly
			if (targetFileStr.contains("<?xml")) {
				while (targetFileStr.length() > 0 && !targetFileStr.startsWith("<?xml")) {
					if (targetFileStr.length() > 1) {
						targetFileStr = targetFileStr.substring(1, targetFileStr.length());
					} else
						break;
				}
			}
			KodiXmlMovieBean xmlMovieBean = KodiXmlMovieBean.makeFromXML(targetFileStr);
			if (xmlMovieBean != null) {

				movieFromNfo = xmlMovieBean.toMovie();
			}
			return movieFromNfo;
		}
	}

	public ArrayList<Actor> getActors() {
		return actors;
	}

	public ArrayList<Director> getDirectors() {
		return directors;
	}

	public Thumb[] getFanart() {
		return fanart;
	}

	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public ID getId() {
		return id;
	}

	public MPAARating getMpaa() {
		return mpaa;
	}

	public OriginalTitle getOriginalTitle() {
		return originalTitle;
	}

	public Outline getOutline() {
		return outline;
	}

	public Plot getPlot() {
		return plot;
	}

	public Thumb[] getPosters() {
		return posters;
	}

	public Rating getRating() {
		return rating;
	}

	public Runtime getRuntime() {
		return runtime;
	}

	public Set getSet() {
		return set;
	}

	public SortTitle getSortTitle() {
		return sortTitle;
	}

	public Studio getStudio() {
		return studio;
	}

	public Tagline getTagline() {
		return tagline;
	}

	public Title getTitle() {
		return title;
	}

	public Top250 getTop250() {
		return top250;
	}

	public Votes getVotes() {
		return votes;
	}

	public Year getYear() {
		return year;
	}

	public void setActors(ArrayList<Actor> actors) {
		this.actors = actors;
	}

	public void setDirectors(ArrayList<Director> directors) {
		this.directors = directors;
	}

	public void setFanart(Thumb[] fanart) {
		this.fanart = fanart;
	}

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public void setMpaa(MPAARating mpaa) {
		this.mpaa = mpaa;
	}

	public void setOriginalTitle(OriginalTitle originalTitle) {
		this.originalTitle = originalTitle;
	}

	public void setOutline(Outline outline) {
		this.outline = outline;
	}

	public void setPlot(Plot plot) {
		this.plot = plot;
	}

	public void setPosters(Thumb[] posters) {
		this.posters = posters;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public void setRuntime(Runtime runtime) {
		this.runtime = runtime;
	}

	public void setSet(Set set) {
		this.set = set;
	}

	public void setSortTitle(SortTitle sortTitle) {
		this.sortTitle = sortTitle;
	}

	public void setStudio(Studio studio) {
		this.studio = studio;
	}

	public void setTagline(Tagline tagline) {
		this.tagline = tagline;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public void setTop250(Top250 top250) {
		this.top250 = top250;
	}

	public void setVotes(Votes votes) {
		this.votes = votes;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", originalTitle=" + originalTitle + ", sortTitle=" + sortTitle + ", set=" + set + ", rating=" + rating + ", year=" + year + ", top250=" + top250
		        + ", trailer = " + trailer + ", votes=" + votes + ", outline=" + outline + ", plot=" + plot + ", tagline=" + tagline + ", studio=" + studio + "releaseDate=" + releaseDate
		        + ", runtime=" + runtime + ", posters=" + Arrays.toString(posters) + ", fanart=" + Arrays.toString(fanart) + ", extrafanart = " + Arrays.toString(extraFanart) + ", mpaa=" + mpaa
		        + ", id=" + id + ", genres=" + genres + ", tags=" + tags + ", actors=" + actors + ", directors=" + directors + "]";
	}

	public String toXML() {
		return title.toXML();
	}

	public void writeExtraFanart(File directoryMovieIsIn) throws IOException {
		if (directoryMovieIsIn != null && directoryMovieIsIn.exists() && directoryMovieIsIn.isDirectory() && getExtraFanart().length > 0) {
			File extraFanartFolder = new File(directoryMovieIsIn.getPath() + File.separator + "extrafanart");
			FileUtils.forceMkdir(extraFanartFolder);
			int currentExtraFanartNumber = 1;
			for (Thumb currentExtraFanart : this.getExtraFanart()) {
				File fileNameToWrite = new File(extraFanartFolder.getPath() + File.separator + "fanart" + currentExtraFanartNumber + ".jpg");

				//no need to overwrite perfectly good extra fanart since this stuff doesn't change. this will also save time when rescraping since extra IO isn't done.
				if (!fileNameToWrite.exists()) {
					System.out.println("Writing extrafanart to " + fileNameToWrite);
					currentExtraFanart.writeImageToFile(fileNameToWrite);
				}
				currentExtraFanartNumber++;
			}
		}
	}

	public void writeToFile(File nfoFile, File posterFile, File fanartFile, File currentlySelectedFolderJpgFile, File targetFolderForExtraFanartFolderAndActorFolder, File trailerFile,
	        MoviescraperPreferences preferences) throws IOException {
		// Output the movie to XML using XStream and a proxy class to
		// translate things to a format that Kodi expects

		//ID only appended if preference set and not already at the start of the title
		if (!title.getTitle().startsWith(id.getId())) {
			appendIDToStartOfTitle();
		}

		String xml = new KodiXmlMovieBean(this).toXML();
		// add the xml header since xstream doesn't do this
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" + "\n" + xml;
		//System.out.println("Xml I am writing to file: \n" + xml);

		if (nfoFile != null && xml.length() > 0)
			nfoFile.delete();
		FileUtils.writeStringToFile(nfoFile, xml, org.apache.commons.lang3.CharEncoding.UTF_8);

		Thumb posterToSaveToDisk = null;
		if (posters != null && posters.length > 0)
			posterToSaveToDisk = posters[0];

		boolean writePoster = preferences.getWriteFanartAndPostersPreference();
		boolean writeFanart = preferences.getWriteFanartAndPostersPreference();
		boolean writePosterIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference();
		boolean writeFanartIfAlreadyExists = preferences.getOverWriteFanartAndPostersPreference();
		boolean createFolderJpgEnabledPreference = preferences.getCreateFolderJpgEnabledPreference();

		// save the first poster out
		// maybe we did some clipping, so we're going to have to reencode it
		if (this.getPosters().length > 0 && (writePoster || createFolderJpgEnabledPreference)
		        && ((posterFile.exists() == writePosterIfAlreadyExists) || (!posterFile.exists() || (createFolderJpgEnabledPreference)))) {
			if (posterToSaveToDisk != null && (posterToSaveToDisk.isModified() || createFolderJpgEnabledPreference || !posterFile.exists() || writePosterIfAlreadyExists)) {
				//reencode the jpg since we probably did a resize
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter writer = (ImageWriter) iter.next();
				// instantiate an ImageWriteParam object with default compression options
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(1); // an float between 0 and 1
				// 1 specifies minimum compression and maximum quality
				IIOImage image = new IIOImage((RenderedImage) posterToSaveToDisk.getThumbImage(), null, null);

				if (writePoster && posterToSaveToDisk.isModified()) {
					System.out.println("Writing poster to " + posterFile);
					try (FileImageOutputStream posterFileOutput = new FileImageOutputStream(posterFile);) {
						writer.setOutput(posterFileOutput);
						writer.write(null, image, iwp);
					}
				}
				//write out the poster file without reencoding it and resizing it
				else if ((!posterFile.exists() || writePosterIfAlreadyExists) && posterToSaveToDisk.getThumbURL() != null) {
					System.out.println("Writing poster file from nfo: " + posterFile);
					FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL(), posterFile, posterToSaveToDisk.getReferrerURL());
				}
				if (createFolderJpgEnabledPreference && currentlySelectedFolderJpgFile != null) {
					if (!posterToSaveToDisk.isModified() && (!currentlySelectedFolderJpgFile.exists() || (currentlySelectedFolderJpgFile.exists() && writePosterIfAlreadyExists))) {
						System.out.println("Writing folder.jpg (no changes) to " + currentlySelectedFolderJpgFile);
						FileDownloaderUtilities.writeURLToFile(posterToSaveToDisk.getThumbURL(), currentlySelectedFolderJpgFile, posterToSaveToDisk.getReferrerURL());
					} else {
						if (!currentlySelectedFolderJpgFile.exists() || (currentlySelectedFolderJpgFile.exists() && writePosterIfAlreadyExists)) {
							System.out.println("Writing folder to " + currentlySelectedFolderJpgFile);
							try (FileImageOutputStream folderFileOutput = new FileImageOutputStream(currentlySelectedFolderJpgFile);) {
								writer.setOutput(folderFileOutput);
								writer.write(null, image, iwp);
							}
						} else {
							System.out.println("Skipping overwrite of folder.jpg due to preference setting");
						}
					}
				}
				writer.dispose();
			}
		}

		// save the first fanart out
		// we didn't modify it so we can write it directly from the URL
		if (this.getFanart().length > 0 && writeFanart && ((fanartFile.exists() == writeFanartIfAlreadyExists) || !fanartFile.exists())) {
			if (fanart != null && fanart.length > 0) {
				Thumb fanartToSaveToDisk;
				if (preferredFanartToWriteToDisk != null)
					fanartToSaveToDisk = preferredFanartToWriteToDisk;
				else
					fanartToSaveToDisk = fanart[0];
				System.out.println("saving out first fanart to " + fanartFile);

				//can save ourself redownloading the image if it's already in memory, but we dont want to reencode the image, so only do this if it's modified
				if (fanartToSaveToDisk.getImageIconThumbImage() != null && fanartToSaveToDisk.isModified()) {
					try {
						ImageIO.write(fanartToSaveToDisk.toBufferedImage(), "jpg", fanartFile);
					} catch (IOException e) {
						System.err.println("Failed to write fanart due to io error");
						e.printStackTrace();
					}
				}
				//download the url and save it out to disk
				else
					FileDownloaderUtilities.writeURLToFile(fanartToSaveToDisk.getThumbURL(), fanartFile, fanartToSaveToDisk.getReferrerURL());
			}
		}

		//write out the extrafanart, if the preference for it is set
		if (targetFolderForExtraFanartFolderAndActorFolder != null && preferences.getExtraFanartScrapingEnabledPreference()) {
			System.out.println("Starting write of extra fanart into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeExtraFanart(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write the .actor images, if the preference for it is set
		if (preferences.getDownloadActorImagesToActorFolderPreference() && targetFolderForExtraFanartFolderAndActorFolder != null) {
			System.out.println("Writing .actor images into " + targetFolderForExtraFanartFolderAndActorFolder);
			writeActorImagesToFolder(targetFolderForExtraFanartFolderAndActorFolder);
		}

		//write out the trailer, if the preference for it is set
		Trailer trailerToWrite = getTrailer();
		if (preferences.getWriteTrailerToFile() && trailerToWrite != null && trailerToWrite.getTrailer().length() > 0) {
			trailerToWrite.writeTrailerToFile(trailerFile);
		}
	}

	public void writeActorImagesToFolder(File targetFolder) throws IOException {
		File actorFolder = null;
		if (targetFolder.isDirectory()) {
			actorFolder = new File(targetFolder + File.separator + ".actors");
		} else if (targetFolder.isFile()) {
			actorFolder = new File(targetFolder.getParent() + File.separator + ".actors");
		}
		//Don't create an empty .actors folder with no actors underneath it
		if (this.hasAtLeastOneActorThumbnail() && actorFolder != null) {
			FileUtils.forceMkdir(actorFolder);
			//on windows this new folder should have the hidden attribute; on unix it is already "hidden" by having a . in front of the name
			Path path = actorFolder.toPath();
			//if statement needed for Linux checking .actors hidden flag when .actors is a symlink
			if (!Files.isHidden(path)) {
				Boolean hidden = (Boolean) Files.getAttribute(path, "dos:hidden", LinkOption.NOFOLLOW_LINKS);
				if (hidden != null && !hidden) {
					try {
						Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
					} catch (AccessDeniedException e) {
						System.err.println("I was not allowed to make .actors folder hidden. This is not a big deal - continuing with write of actor files...");
					}
				}
			}

			for (Actor currentActor : this.getActors()) {
				String currentActorToFileName = currentActor.getName().replace(' ', '_');
				File fileNameToWrite = new File(actorFolder.getPath() + File.separator + currentActorToFileName + ".jpg");
				currentActor.writeImageToFile(fileNameToWrite);
				//reload from disk instead of cache since the cache is now pointing to the wrong image and the disk has the correct newly edited one
				if (currentActor.isThumbEdited())
					ImageCache.removeImageFromCache(fileNameToWrite.toURI().toURL(), false);
			}

		}
	}

	public boolean hasPoster() {
		if (this.posters.length > 0)
			return true;
		else
			return false;
	}

	private static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}

	//returns the movie file path without anything like CD1, Disc A, etc and also gets rid of the file extension
	//Example: MyMovie ABC-123 CD1.avi returns MyMovie ABC-123
	//Example2: MyMovie ABC-123.avi returns MyMovie ABC-123
	public static String getUnstackedMovieName(File file) {
		String fileName = file.toString();
		fileName = replaceLast(fileName, file.getName(), SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(file.getName())));
		return fileName;
	}

	public static String getFileNameOfNfo(File file, Boolean nfoNamedMovieDotNfo) {
		if (nfoNamedMovieDotNfo) {
			return file.getPath() + File.separator + "movie.nfo";
		} else
			return getTargetFilePath(file, ".nfo");
	}

	public static String getFileNameOfPoster(File file, boolean getNoMovieNameInImageFiles) {
		if (getNoMovieNameInImageFiles) {
			if (file.isDirectory()) {
				return file.getPath() + File.separator + "poster.jpg";
			} else {
				return file.getParent() + File.separator + "poster.jpg";
			}
		} else
			return getTargetFilePath(file, "-poster.jpg");
	}

	public static String getFileNameOfFolderJpg(File selectedValue) {

		if (selectedValue.isDirectory()) {
			return selectedValue.getPath() + File.separator + "folder.jpg";
		} else
			return selectedValue.getParent() + File.separator + "folder.jpg";
	}

	public static String getFileNameOfExtraFanartFolderName(File selectedValue) {
		if (selectedValue != null && selectedValue.isDirectory()) {
			return selectedValue.getPath();
		} else if (selectedValue != null && selectedValue.isFile()) {
			return selectedValue.getParent();
		} else
			return null;
	}

	public static String getFileNameOfTrailer(File selectedValue) {
		//sometimes the trailer has a different extension 
		//than the movie so we will try to brute force a find by trying all movie name extensions
		for (String extension : MovieFilenameFilter.acceptedMovieExtensions) {
			String potentialTrailer = tryToFindActualTrailerHelper(selectedValue, "." + extension);
			if (potentialTrailer != null)
				return potentialTrailer;
		}
		return getTargetFilePath(selectedValue, "-trailer.mp4");
	}

	/**
	 * Checks for the given file a trailer file exists for it for the given file name extension
	 * 
	 * @param selectedValue - base file name of movie or nfo
	 * @param extension - the file name extension we are checking
	 * @return - the path to the file if it found the trailer, otherwise null
	 */
	private static String tryToFindActualTrailerHelper(File selectedValue, String extension) {
		String potentialPath = getTargetFilePath(selectedValue, "-trailer" + extension);
		File trailerCandidate = new File(potentialPath);
		if (trailerCandidate.exists())
			return potentialPath;
		return null;
	}

	public static String getFileNameOfFanart(File file, boolean getNoMovieNameInImageFiles) {
		if (getNoMovieNameInImageFiles) {
			if (file.isDirectory()) {
				return file.getPath() + File.separator + "fanart.jpg";
			} else {
				return file.getParent() + File.separator + "fanart.jpg";
			}
		} else
			return getTargetFilePath(file, "-fanart.jpg");
	}

	private static String getTargetFilePath(File file, String extension) {
		if (!file.isDirectory()) {
			String nfoName = getUnstackedMovieName(file) + extension;
			return nfoName;
		}
		//look in the directory for an nfo file, otherwise we will make one based on the last word (JAVID of the folder name)
		else {
			final String extensionFromParameter = extension;
			//getting the nfo files in this directory, if any
			File[] directoryContents = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File directory, String fileName) {
					return fileName.endsWith(extensionFromParameter);
				}
			});
			//if there are 1 or more files, it's not really in spec, so just return the first one
			if (directoryContents.length > 0) {
				return directoryContents[0].getPath();
			} else {
				//no file found in directory, so we will be setting the target to create one in that directory
				File[] directoryContentsOfAllFiles = file.listFiles(new MovieFilenameFilter());
				if (directoryContentsOfAllFiles.length > 0) {
					//check to see if there's at least one file in the directory that is a movie and go by naming based off the first file found
					for (File currentFile : directoryContentsOfAllFiles) {
						if (currentFile.isFile()) {
							String targetFileName = getUnstackedMovieName(currentFile) + extension;
							//System.out.println("returning " + targetFileName);
							return targetFileName;
						}
					}
				}
				//Use the folder name as the basis for the filename created
				return new File(file.getAbsolutePath() + File.separator + file.getName() + extension).getPath();
			}
		}
	}

	/*
	 * private String [] searchResultsHelperForScrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom)
	 * {
	 * String [] searchResults = siteToParseFrom.getSearchResults(searchString);
	 * int levDistanceOfCurrentMatch = 999999; // just some super high number
	 * String idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile);
	 * 
	 * //loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
	 * for (int i = 0; i < searchResults.length; i++)
	 * {
	 * String urltoMatch = searchResults[i].toLowerCase();
	 * String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
	 * //System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
	 * if (urltoMatch.contains(idFromMovieFileToMatch))
	 * {
	 * //let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
	 * //and update the variables accordingly so we know what our best match so far is
	 * int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
	 * if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)
	 * {
	 * levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
	 * searchResultNumberToUse = i;
	 * }
	 * }
	 * }
	 * return searchResults;
	 * }
	 */

	//Version that allows us to update the GUI while scraping
	public static Movie scrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom, String urlToScrapeFromDMM, boolean useURLtoScrapeFrom) throws IOException {

		//If the user manually canceled the results on this scraper in a dialog box, just return a null movie
		if (siteToParseFrom.getDiscardResults())
			return null;
		String searchString = siteToParseFrom.createSearchString(movieFile);
		SearchResult[] searchResults = null;
		int searchResultNumberToUse = 0;
		//no URL was passed in so we gotta figure it ourselves
		if (!useURLtoScrapeFrom) {
			searchResults = siteToParseFrom.getSearchResults(searchString);
			int levDistanceOfCurrentMatch = 999999; // just some super high number
			String idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile, siteToParseFrom.isFirstWordOfFileIsID());

			//loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
			for (int i = 0; i < searchResults.length; i++) {
				String urltoMatch = searchResults[i].getUrlPath().toLowerCase();
				String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
				//System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
				if (urltoMatch.contains(idFromMovieFileToMatch)) {
					//let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
					//and update the variables accordingly so we know what our best match so far is
					int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
					if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch) {
						levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
						searchResultNumberToUse = i;
					}
				}
			}
		}
		//just use the URL to parse from the parameter
		else if (useURLtoScrapeFrom) {
			searchResults = new SearchResult[1];

			if (siteToParseFrom instanceof DmmParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);
			else if (siteToParseFrom instanceof Data18MovieParsingProfile || siteToParseFrom instanceof Data18WebContentParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);
			else if (siteToParseFrom instanceof JavLibraryParsingProfile)
				searchResults[0] = new SearchResult(((JavLibraryParsingProfile) siteToParseFrom).getOverrideURLJavLibrary());
			else if (siteToParseFrom instanceof IAFDParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);

			//override any of the above if we have specifically set an override url
			if (siteToParseFrom.getOverridenSearchResult() != null) {
				searchResults[0] = siteToParseFrom.getOverridenSearchResult();
				searchResultNumberToUse = 0;
			}

		}
		if (searchResults != null && searchResults.length > 0 && searchResults[searchResultNumberToUse].getUrlPath().length() > 0) {
			System.out.println("Scraping this webpage for movie: " + searchResults[searchResultNumberToUse].getUrlPath());
			//for now just set the movie to the first thing found unless we found a link which had something close to the ID
			SearchResult searchResultToUse = searchResults[searchResultNumberToUse];
			Document searchMatch = siteToParseFrom.downloadDocument(searchResultToUse);
			//Handle any captchas etc that prevent us from getting our result
			if (searchMatch != null && SecurityPassthrough.class.isAssignableFrom(siteToParseFrom.getClass())) {
				SecurityPassthrough siteParsingProfileSecurityPassthrough = (SecurityPassthrough) siteToParseFrom;
				if (siteParsingProfileSecurityPassthrough.requiresSecurityPassthrough(searchMatch)) {
					searchMatch = siteParsingProfileSecurityPassthrough.runSecurityPassthrough(searchMatch, searchResultToUse);
				}
			}
			siteToParseFrom.setDocument(searchMatch);
			siteToParseFrom.setOverrideURLDMM(urlToScrapeFromDMM);

			Movie scrapedMovie = new Movie(siteToParseFrom);
			return scrapedMovie;
		} else //no movie match found
		{
			return null;
		}
	}

	public boolean hasAtLeastOneActorThumbnail() {
		for (Actor currentActor : actors) {
			if (currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null && !currentActor.getThumb().getThumbURL().equals("")) {
				return true;
			}
		}
		return false;
	}

	public Thumb[] getExtraFanart() {
		return extraFanart;
	}

	public void setExtraFanart(Thumb[] extraFanart) {
		this.extraFanart = extraFanart;
	}

	public Trailer getTrailer() {
		return trailer;
	}

	public void setTrailer(Trailer trailer) {
		this.trailer = trailer;
	}

	public boolean hasFanart() {
		if (this.fanart.length > 0)
			return true;
		else
			return false;
	}

	public List<Title> getAllTitles() {
		return allTitles;
	}

	public void setAllTitles(List<Title> allTitles) {
		this.allTitles = allTitles;
	}

	public static Movie getEmptyMovie() {
		ArrayList<Actor> actors = new ArrayList<>();
		ArrayList<Director> directors = new ArrayList<>();
		ArrayList<Genre> genres = new ArrayList<>();
		ArrayList<Tag> tags = new ArrayList<>();

		Thumb[] fanart = new Thumb[0];
		Thumb[] extraFanart = new Thumb[0];
		Thumb[] posters = new Thumb[0];

		ID id = new ID("");
		MPAARating mpaa = new MPAARating("");
		OriginalTitle originalTitle = OriginalTitle.BLANK_ORIGINALTITLE;
		Outline outline = Outline.BLANK_OUTLINE;
		Plot plot = Plot.BLANK_PLOT;
		Rating rating = Rating.BLANK_RATING;
		ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
		Runtime runtime = Runtime.BLANK_RUNTIME;
		Set set = Set.BLANK_SET;
		SortTitle sortTitle = SortTitle.BLANK_SORTTITLE;
		Studio studio = Studio.BLANK_STUDIO;
		Tagline tagline = Tagline.BLANK_TAGLINE;
		Title title = new Title("");
		Top250 top250 = Top250.BLANK_TOP250;
		Trailer trailer = new Trailer(null);
		Votes votes = Votes.BLANK_VOTES;
		Year year = Year.BLANK_YEAR;

		return new Movie(actors, directors, fanart, extraFanart, genres, tags, id, mpaa, originalTitle, outline, plot, posters, rating, releaseDate, runtime, set, sortTitle, studio, tagline, title,
		        top250, trailer, votes, year);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ReleaseDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(ReleaseDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	/**
	 * remove the item from the picked from the existing poster list and put it at
	 * the front of the list. if the movie does not contain the poster, no change will be made
	 * 
	 * @param posterToGoToFront - poster to put in front
	 */
	public void moveExistingPosterToFront(Thumb posterToGoToFront) {
		if (posterToGoToFront != null) {

			ArrayList<Thumb> existingPosters = new ArrayList<>(Arrays.asList(getPosters()));
			boolean didListContainPoster = existingPosters.remove(posterToGoToFront);
			if (didListContainPoster) {
				existingPosters.add(0, posterToGoToFront);
				Thumb[] posterArray = new Thumb[existingPosters.size()];
				setPosters(existingPosters.toArray(posterArray));
			}
		}
	}

	/**
	 * remove the item from the picked from the existing fanart list and put it at
	 * the front of the list. if the movie does not contain the fanart, no change will be made
	 * 
	 * @param fanartToGoToFront - fanart to put in front
	 */
	public void moveExistingFanartToFront(Thumb fanartToGoToFront) {
		if (fanartToGoToFront != null) {

			ArrayList<Thumb> existingFanarts = new ArrayList<>(Arrays.asList(getFanart()));
			boolean didListContainPoster = existingFanarts.remove(fanartToGoToFront);
			if (didListContainPoster) {
				existingFanarts.add(0, fanartToGoToFront);
				Thumb[] fanartArray = new Thumb[existingFanarts.size()];
				setFanart(existingFanarts.toArray(fanartArray));
			}
		}
	}

	/**
	 * @return true if the movie has a non-null, non-zero length title, false otherwise
	 */
	public boolean hasValidTitle() {
		return (title != null && title.getTitle() != null && title.getTitle().length() > 0);
	}

}
