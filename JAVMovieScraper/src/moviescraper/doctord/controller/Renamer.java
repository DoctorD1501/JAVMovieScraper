package moviescraper.doctord.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class Renamer {

	private String fileNameRenameString;
	private String folderNameRenameString;
	private Movie movie;
	private String sanitizer;
	private File oldFile;
	
	private String extension;
	private String filename;
	private String path;
	private static final int maxFileNameLength = 248;
	private static final int extraFlexForFileNameLength = 25; //a folder can't be so long in name that it can't have a reasonable file inside it, so we're giving ourselves some extra flex

	//file tags
	private final static String ID = "<ID>";
	private final static String TITLE = "<TITLE>";
	private final static String ACTORS = "<ACTORS>";
	private final static String YEAR = "<YEAR>";
	private final static String RELEASEDATE = "<RELEASEDATE>";
	private final static String ORIGINALTITLE = "<ORIGINALTITLE>";
	private final static String SET = "<SET>";
	private final static String STUDIO = "<STUDIO>";
	private final static String GENRES = "<GENRES>";
	private final static String period = ".";
	private final static String[] availableRenameTags = {ID, TITLE, ACTORS, GENRES, SET, STUDIO, YEAR, RELEASEDATE, ORIGINALTITLE};
	
	//folder tags
	private final static String BASEDIRECTORY = "<BASEDIRECTORY>";
	private final static String PATHSEPERATOR = "<PATHSEPERATOR>";
	private final static String[] availableFolderRenameTags = {BASEDIRECTORY, PATHSEPERATOR, ID, TITLE, ACTORS, GENRES, SET, STUDIO, YEAR, RELEASEDATE, ORIGINALTITLE};
	
	public Renamer(String fileNameRenameString, String folderNameRenameString, String sanitizer, Movie toRename, File oldFile) {
		this.fileNameRenameString = fileNameRenameString;
		this.folderNameRenameString = folderNameRenameString;
		this.sanitizer = sanitizer;
		this.movie = toRename;
		this.oldFile = oldFile;
	}
	
	/**
	 * Returns the new name given by the constructed renamer string used from the movie scraper preferences and arguments passed into the renamer object
	 * @param isFolderName - if true will remove the extension and enders such as -poster and -trailer from the filename. otherwise leaves them on
	 */
	public String getNewFileName(boolean isFolderName) {
		
		extension = FilenameUtils.getExtension(oldFile.toString());
		if(oldFile.isDirectory())
			extension = "";
		filename = FilenameUtils.getBaseName(oldFile.toString());
		path = FilenameUtils.getFullPath(oldFile.toString());
		path = getRenamedFolderPath(path);
		String dot = ".";
		if(oldFile.isDirectory())
			dot = "";
		String newName = getSanitizedString (replace(fileNameRenameString));
		if(isFolderName)
		{
			newName = path + newName;
		}
		else
		{
			newName = path + newName + getAppendix() + getPosterFanartTrailerEnder() + dot + extension;
		}
		
		//shorten the string if it still doesn't fit
		while((newName.length() + extraFlexForFileNameLength) > maxFileNameLength)
		{
			//newName = path + newName.substring(0,newName.length()-1) + getAppendix() + getPosterFanartTrailerEnder() + dot + extension;
			//Cut the title down by one character and try again
			System.out.println("Potential filename was too long. Cutting letters off title");
			Title newTitle = new Title(movie.getTitle().getTitle().substring(0, movie.getTitle().getTitle().length()-1));
			System.out.println("New truncated title is = " + newTitle.getTitle());
			movie.setTitle(newTitle);
			return getNewFileName(isFolderName);
		}
		
		return newName;
	}
	
	private String getRenamedFolderPath(String path) {
		System.out.println("Old Path: " + path);
		String newPath = replace(folderNameRenameString);
		//Make sure we don't have any double path separators caused by things like an empty field
		String doublePathSeperator = File.separator + File.separator;
		while(newPath.contains(doublePathSeperator))
		{
			newPath = newPath.replace(doublePathSeperator, File.separator);
		}
		System.out.println("New path: " + newPath);
		return newPath;
	}

	private String replace(String target) {
		String movieID = movie.getId().getId();
		String movieTitle = movie.getTitle().getTitle();
		List<Actor> movieActorsList = movie.getActors();
		String movieActors = combineActorList(movieActorsList);
		String movieYear = movie.getYear().getYear();
		String movieReleaseDate = movie.getReleaseDate().getReleaseDate();
		String movieOriginalTitle = movie.getOriginalTitle().getOriginalTitle();
		String movieSet = movie.getSet().getSet();
		String movieStudio = movie.getStudio().getStudio();
		String movieGenres = combineGenreList(movie.getGenres());
		String baseDirectory = oldFile.getParent();
		String pathSeperator = File.separator;
		String newName = target;
				
		//path stuff
		newName = renameReplaceAll(newName, BASEDIRECTORY, baseDirectory);
		newName = renameReplaceAll(newName, PATHSEPERATOR, pathSeperator);
		
		//metadata stuff
		newName = renameReplaceAll(newName, ID, movieID);
		newName = renameReplaceAll(newName, TITLE, movieTitle);
		newName = renameReplaceAll(newName, YEAR, movieYear);
		newName = renameReplaceAll(newName, RELEASEDATE, movieReleaseDate);
		newName = renameReplaceAll(newName, ORIGINALTITLE, movieOriginalTitle);
		newName = renameReplaceAll(newName, SET, movieSet);
		newName = renameReplaceAll(newName, STUDIO, movieStudio);
		newName = renameReplaceAll(newName, GENRES, movieGenres);
		
		//we need to watch out when renaming a file that a large number of actors doesn't create
		//a movie name that is too long
		String potentialNameWithActors =  renameReplaceAll(newName, ACTORS, movieActors);
		if(potentialNameWithActors.length() + path.length() + getAppendix().length() + getPosterFanartTrailerEnder().length() + period.length() + extension.length() < maxFileNameLength )
			newName = potentialNameWithActors;
		else
			newName = renameReplaceAll(newName, ACTORS, "");

		return newName.trim();
	}
	
	private String renameReplaceAll(String replacementString, String tagName, String movieContentOfTag)
	{
		String replacedString = replacementString;
		//Remove's stuff like <RELEASEDATE> when this field is null
		if(movieContentOfTag == null)
		{
			replacedString = replacedString.replace(tagName, "");
		}

		if(replacedString.contains(tagName)){
			replacedString = StringUtils.replace(replacedString, tagName, movieContentOfTag);
		}
		//Get rid of empty parens that are left over from blank field replacements like these : ()
		if(movieContentOfTag == null || movieContentOfTag.trim().equals(""))
		{
			replacedString = replacedString.replaceAll("\\[\\]|\\(\\)", "");
		}
		return replacedString;
	}
	
	private String combineActorList(List<Actor> actors) {
		String actorsString = "";
		for (int i = 0; i < movie.getActors().size(); i++) {
			actorsString += movie.getActors().get(i).getName();
			if (i + 1 < movie.getActors().size())
				actorsString += ", ";
		}
		return actorsString;
	}
	
	public static void rename(File fileToRename, MoviescraperPreferences preferences) throws IOException
	{
		File nfoFile = new File(Movie.getFileNameOfNfo(fileToRename, preferences.getNfoNamedMovieDotNfo()));
		File posterFile = new File(Movie.getFileNameOfPoster(fileToRename, preferences.getNoMovieNameInImageFiles()));
		File fanartFile = new File(Movie.getFileNameOfFanart(fileToRename, preferences.getNoMovieNameInImageFiles()));
		File trailerFile = new File(Movie.getFileNameOfTrailer(fileToRename));
		if(nfoFile != null && nfoFile.exists() && fileToRename.exists())
		{
			Movie movieReadFromNfo = Movie.createMovieFromNfo(nfoFile);
			if(movieReadFromNfo != null && movieReadFromNfo.getTitle() != null)
			{
				Renamer renamer = new Renamer(MoviescraperPreferences.getRenamerString(), MoviescraperPreferences.getRenamerString(), MoviescraperPreferences.getSanitizerForFilename(), movieReadFromNfo, fileToRename);
				
				//Figure out all the new names
			    File newMovieFilename = new File(renamer.getNewFileName(false));
			    
			    renamer.setOldFilename(nfoFile);
			    File newNfoFilename = new File(renamer.getNewFileName(false));
				
				renamer.setOldFilename(posterFile);
				File newPosterFilename = new File(renamer.getNewFileName(false));
				
				renamer.setOldFilename(fanartFile);
				File newFanartFilename = new File(renamer.getNewFileName(false));
				
				renamer.setOldFilename(trailerFile);
				File newTrailerFilename = new File(renamer.getNewFileName(false));
				
				//Do All the Renames
				if(fileToRename.exists())
				{
					System.out.println("Renaming " + fileToRename.getPath() + " to " + newMovieFilename);
					fileToRename.renameTo(newMovieFilename);
				}
				
				if(nfoFile.exists())
				{
					System.out.println("Renaming " + nfoFile.getPath() + " to " + newNfoFilename);
					FileUtils.moveFile(nfoFile, newNfoFilename);
				}
				
				if(posterFile.exists())
				{
					System.out.println("Renaming " + posterFile.getPath() + " to " + newPosterFilename);
					FileUtils.moveFile(posterFile, newPosterFilename);
				}
				
				if(fanartFile.exists())
				{
					System.out.println("Renaming " + fanartFile.getPath() + " to " + newFanartFilename);
					FileUtils.moveFile(fanartFile, newFanartFilename);
				}
				
				if(trailerFile.exists())
				{
					System.out.println("Renaming " + trailerFile.getPath() + " to " + newTrailerFilename);
					FileUtils.moveFile(trailerFile, newTrailerFilename);
				}
				
				//In case of stacked movie files (Movies which are split into multiple files such AS CD1, CD2, etc) get the list of all files
				//which are part of this movie's stack
				File currentDirectory = fileToRename.getParentFile();
				String currentlySelectedMovieFileWihoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(fileToRename.getName()));
				if(currentDirectory != null)
				{

					for(File currentFile : currentDirectory.listFiles())
					{
						String currentFileNameWithoutStackSuffix = SiteParsingProfile.stripDiscNumber(FilenameUtils.removeExtension(currentFile.getName()));
						if(currentFile.isFile() && currentFileNameWithoutStackSuffix.equals(currentlySelectedMovieFileWihoutStackSuffix))
						{
							renamer.setOldFilename(currentFile);
							File newStackedFilename = new File(renamer.getNewFileName(false));
							System.out.println("Renaming " + currentFile.getPath() + " to " + newStackedFilename);
							FileUtils.moveFile(currentFile, newStackedFilename);
						}
					}
				}
			}
		}
		else if(!nfoFile.exists())
		{
			System.err.println("No scraped nfo file found for: " + fileToRename + "  - skipping rename.");
		}
	}
	
	private String combineGenreList(List<Genre> genres) {
		String genresString = "";
		for (int i = 0; i < movie.getGenres().size(); i++) {
			genresString += movie.getGenres().get(i).getGenre();
			if (i + 1 < movie.getGenres().size())
				genresString += ", ";
		}
		return genresString;
	}
	
	private String getAppendix() {
		//TODO: make this method more flexible to check all the possible types of disc names
		//(I already have a method somewhere else in this project which has a good regular expression to use)
		String appendix = "";
		boolean hasAppendix = filename.matches(".*CD\\s?1.*");
		if (hasAppendix)
			appendix = " CD1";
		hasAppendix = filename.matches(".*CD\\s?2.*");
		if (hasAppendix)
			appendix = " CD2";
		hasAppendix = filename.matches(".*CD\\s?3.*");
		if (hasAppendix)
			appendix = " CD3";
		hasAppendix = filename.matches(".*CD\\s?4.*");
		if (hasAppendix)
			appendix = " CD4";
		hasAppendix = filename.matches(".*CD\\s?5.*");
		if (hasAppendix)
			appendix = " CD5";
		return appendix;
	}
	
	private String getPosterFanartTrailerEnder(){
		String fileNameEnder = "";
		boolean hasFileNameEnder = oldFile.getPath().matches(".*-poster[\\.].+");
		if (hasFileNameEnder)
			fileNameEnder = "-poster";
		hasFileNameEnder = oldFile.getPath().matches(".*-trailer[\\.].+");
		if (hasFileNameEnder)
			fileNameEnder = "-trailer";
		hasFileNameEnder = oldFile.getPath().matches(".*-fanart[\\.].+");
		if (hasFileNameEnder)
			fileNameEnder = "-fanart";
		
		return fileNameEnder;
	}
	
	private String getSanitizedString(String fileName) {
		final Pattern ILLEGAL_CHARACTERS = Pattern.compile(sanitizer);
		fileName = ILLEGAL_CHARACTERS.matcher(fileName).replaceAll("").replaceAll("\\s+", " ").trim();
		return fileName;
	}
	
	public static String getAvailableFileTags()
	{
		String tags = "";
		for (String tag : availableRenameTags)
		{
			tags= tags + " " + tag;
		}
		return tags.trim();
	}
	
	public static String getAvailableFolderTags()
	{
		String tags = "";
		for (String tag : availableFolderRenameTags)
		{
			tags= tags + " " + tag;
		}
		return tags.trim();
	}

	public String getOldFilename() {
		return filename;
	}

	public void setOldFilename(File oldFile) {
		this.oldFile = oldFile;
	}
	
}
