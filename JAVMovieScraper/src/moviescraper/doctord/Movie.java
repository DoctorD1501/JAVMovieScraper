package moviescraper.doctord;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import moviescraper.doctord.SiteParsingProfile.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.dataitem.*;
import moviescraper.doctord.dataitem.Runtime;

public class Movie {

	private ArrayList<Actor> actors;
	private ArrayList<Director> directors;
	private Thumb[] fanart;
	private ArrayList<Genre> genres;
	private ID id;
	private MPAARating mpaa;
	private OriginalTitle originalTitle;
	private Outline outline;
	private Plot plot;
	private Thumb[] posters;
	private Rating rating;
	private Runtime runtime;
	private Set set;
	private SortTitle sortTitle;
	private Studio studio;

	private Tagline tagline;

	private Title title;

	private Top250 top250;

	private Votes votes;

	private Year year;
	
	private final static int connectionTimeout = 10000; //10 seconds
	private final static int  readTimeout = 10000; //10 seconds
	
	public Movie(ArrayList<Actor> actors, ArrayList<Director> directors,
			Thumb[] fanart, ArrayList<Genre> genres, ID id, MPAARating mpaa,
			OriginalTitle originalTitle, Outline outline, Plot plot,
			Thumb[] posters, Rating rating, Runtime runtime, Set set,
			SortTitle sortTitle, Studio studio, Tagline tagline, Title title,
			Top250 top250, Votes votes, Year year) {
		super();
		this.actors = actors;
		this.directors = directors;
		this.fanart = fanart;
		this.genres = genres;
		this.id = id;
		this.mpaa = mpaa;
		this.originalTitle = originalTitle;
		this.outline = outline;
		this.plot = plot;
		this.posters = posters;
		this.rating = rating;
		this.runtime = runtime;
		this.set = set;
		this.sortTitle = sortTitle;
		this.studio = studio;
		this.tagline = tagline;
		this.title = title;
		this.top250 = top250;
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
		votes = siteToScrapeFrom.scrapeVotes();
		outline = siteToScrapeFrom.scrapeOutline();
		plot = siteToScrapeFrom.scrapePlot();
		tagline = siteToScrapeFrom.scrapeTagline();
		studio = siteToScrapeFrom.scrapeStudio();
		runtime = siteToScrapeFrom.scrapeRuntime();
		posters = siteToScrapeFrom.scrapePosters();
		fanart = siteToScrapeFrom.scrapeFanart();
		mpaa = siteToScrapeFrom.scrapeMPAA();
		id = siteToScrapeFrom.scrapeID();
		actors = siteToScrapeFrom.scrapeActors();
		genres = siteToScrapeFrom.scrapeGenres();
		directors = siteToScrapeFrom.scrapeDirectors();
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
		return "Movie [title=" + title + ", originalTitle=" + originalTitle
				+ ", sortTitle=" + sortTitle + ", set=" + set + ", rating="
				+ rating + ", year=" + year + ", top250=" + top250 + ", votes="
				+ votes + ", outline=" + outline + ", plot=" + plot
				+ ", tagline=" + tagline + ", studio=" + studio + ", runtime="
				+ runtime + ", posters=" + Arrays.toString(posters)
				+ ", fanart=" + Arrays.toString(fanart) + ", mpaa=" + mpaa
				+ ", id=" + id + ", genres=" + genres + ", actors=" + actors
				+ ", directors=" + directors + "]";
	}

	public String toXML() {
		return title.toXML();
	}

	public void writeToFile(File nfofile, File posterFile, File fanartFile, boolean writePoster, boolean writeFanart, boolean writePosterIfAlreadyExists, boolean writeFanartIfAlreadyExists) throws IOException {
		// Output the movie to XML using XStream and a proxy class to
		// translate things to a format that xbmc expects

		String xml = new XbmcXmlMovieBean(this).toXML();
		// add the xml header since xstream doesn't do this
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
				+ "\n" + xml;
		System.out.println("Xml I am writing to file: \n" + xml);

		FileUtils.writeStringToFile(nfofile, xml,
				org.apache.commons.lang3.CharEncoding.UTF_8);
		
		Thumb posterToSaveToDisk = posters[0];
		Thumb fanartToSaveToDisk = fanart[0];
		
		
		// save the first poster out
		// maybe we did some clipping, so we're going to have to reencode it
		if (this.getPosters().length > 0 && writePoster && ((posterFile.exists() == writePosterIfAlreadyExists) || (!posterFile.exists())))
		{
			//System.out.println("writing poster to " + posterFile);
			if(posterToSaveToDisk.isModified())
			{
				//reencode the jpg since we probably did a resize
				Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter writer = (ImageWriter)iter.next();
				// instantiate an ImageWriteParam object with default compression options
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(1);   // an float between 0 and 1
				// 1 specifies minimum compression and maximum quality
				
				//FileUtils.deleteQuietly(posterFile);
				FileImageOutputStream output = new FileImageOutputStream(posterFile);
				writer.setOutput(output);
				IIOImage image = new IIOImage((RenderedImage) posterToSaveToDisk.getThumbImage(), null, null);
				
				writer.write(null, image, iwp);
				writer.dispose();
				output.close();
				/*ImageIO.write((RenderedImage) posterToSaveToDisk.thumbImage,
						"jpg", posterFile);*/
			}
			else
				FileUtils.copyURLToFile(posterToSaveToDisk.getThumbURL(), fanartFile, connectionTimeout, readTimeout);
		}
		
		// save the first fanart out
		// we didn't modify it so we can write it directly from the URL
		if (this.getFanart().length > 0 && writeFanart && ((fanartFile.exists() == writeFanartIfAlreadyExists) || !fanartFile.exists()))
		{
			FileUtils.copyURLToFile(fanartToSaveToDisk.getThumbURL(), fanartFile, connectionTimeout, readTimeout);
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
	        return string.substring(0, pos)
	             + replacement
	             + string.substring(pos + toReplace.length(), string.length());
	    } else {
	        return string;
	    }
	}
	
	//returns the movie file path without anything like CD1, Disc A, etc and also gets rid of the file extension
	//Example: MyMovie ABC-123 CD1.avi returns MyMovie ABC-123
	//Example2: MyMovie ABC-123.avi returns MyMovie ABC-123
	public static String getUnstackedMovieName(File file)
	{
		String fileName = file.toString();
		fileName = replaceLast(fileName, file.getName(), SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(file.getName())));
		return fileName;
	}
	
	public static String getFileNameOfNfo(File file)
	{
		return getTargetFilePath(file, ".nfo");
	}

	public static String getFileNameOfPoster(File file) {
		return getTargetFilePath(file, "-poster.jpg");
	}
	
	public static String getFileNameOfFanart(File file) {
		return getTargetFilePath(file, "-fanart.jpg");
	}
	
	private static String getLastWordOfFile(File file)
	{
		String [] fileNameParts = file.getName().split("\\s+");
		String lastWord = fileNameParts[fileNameParts .length-1];
		return lastWord;
		
	}
	
	private static String getTargetFilePath(File file, String extension)
	{
		if(!file.isDirectory())
		{
			String nfoName = getUnstackedMovieName(file) + extension;
			return nfoName;
		}
		//look in the directory for an nfo file, otherwise we will make one based on the last word (JAVID of the folder name)
		else
		{
			final String extensionFromParameter = extension;
			//getting the nfo files in this directory, if any
			File [] directoryContents = file.listFiles(new FilenameFilter() {
			    public boolean accept(File directory, String fileName) {
			        return fileName.endsWith(extensionFromParameter);
			    }
			});
			//if there are 1 or more files, it's not really in spec, so just return the first one
			if (directoryContents.length > 0)
				return directoryContents[0].getPath();
			else
			{
				//no file found in directory, so we will be setting the target to create one in that directory
				System.out.println("Last Word of File" + getLastWordOfFile(file));
				//file.getAbsolutePath()
				return new File(file.getAbsolutePath() + "\\" + getLastWordOfFile(file) + extension).getPath();
			}
		}
	}
	
	/*private String [] searchResultsHelperForScrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom)
	{
		String [] searchResults = siteToParseFrom.getSearchResults(searchString);
		int levDistanceOfCurrentMatch = 999999; // just some super high number
		String idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile);
		
		//loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
		for (int i = 0; i < searchResults.length; i++)
		{
			String urltoMatch = searchResults[i].toLowerCase();
			String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
			//System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
			if (urltoMatch.contains(idFromMovieFileToMatch))
			{
				//let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
				//and update the variables accordingly so we know what our best match so far is
				int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
				if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)
				{
					levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
					searchResultNumberToUse = i;
				}
			}
		}
		return searchResults;
	}*/
	
	public static Movie scrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom, String urlToScrapeFromDMM, boolean useURLtoScrapeFrom) throws IOException{
		String searchString = siteToParseFrom.createSearchString(movieFile);
		SearchResult [] searchResults = null;
		int searchResultNumberToUse = 0;
		//no URL was passed in so we gotta figure it ourselves
		if(!useURLtoScrapeFrom)
		{
		searchResults = siteToParseFrom.getSearchResults(searchString);
		int levDistanceOfCurrentMatch = 999999; // just some super high number
		String idFromMovieFile = SiteParsingProfile.findIDTagFromFile(movieFile);
		
		//loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
		for (int i = 0; i < searchResults.length; i++)
		{
			String urltoMatch = searchResults[i].getUrlPath().toLowerCase();
			String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
			//System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
			if (urltoMatch.contains(idFromMovieFileToMatch))
			{
				//let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
				//and update the variables accordingly so we know what our best match so far is
				int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
				if (candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)
				{
					levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
					searchResultNumberToUse = i;
				}
			}
		}
		}
		//just use the URL to parse from the parameter
		else if(useURLtoScrapeFrom)
		{
			searchResults = new SearchResult[1];
			if(siteToParseFrom instanceof DmmParsingProfile)
				searchResults[0] = new SearchResult(urlToScrapeFromDMM);
			else if(siteToParseFrom instanceof JavLibraryParsingProfile)
				searchResults[0] = new SearchResult(((JavLibraryParsingProfile) siteToParseFrom).getOverrideURLJavLibrary());
		}
		if (searchResults != null && searchResults.length > 0)
		{
			//System.out.println("Scraping this webpage for movie: " + searchResults[searchResultNumberToUse]);
			//for now just set the movie to the first thing found unless we found a link which had something close to the ID
			Document searchMatch = Jsoup.connect(searchResults[searchResultNumberToUse].getUrlPath()).timeout(0).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
			siteToParseFrom.setDocument(searchMatch);
			siteToParseFrom.setOverrideURLDMM(urlToScrapeFromDMM);
			return new Movie(siteToParseFrom);
		}
		else return null; //TODO return some kind of default movie
	}

	public boolean hasAtLeastOneActorThumbnail() {
		for(Actor currentActor : actors)
		{
			if(currentActor.getThumb() != null && currentActor.getThumb().getThumbURL() != null && !currentActor.getThumb().getThumbURL().equals(""))
			{
				return true;
			}
		}
		return false;
	}

}
