package moviescraper.doctord.model;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import moviescraper.doctord.Movie;
import moviescraper.doctord.dataitem.Actor;

public class Renamer {

	private String renameString;
	private Movie movie;
	private String sanitizer;
	private File oldFile;
	
	private String extension;
	private String filename;
	private String path;

	private final static Pattern ID = Pattern.compile("%.?ID.?%");
	private final static Pattern TITLE = Pattern.compile("%.?TITLE.?%");
	private final static Pattern ACTORS = Pattern.compile("%.?ACTORS.?%");
	private final static Pattern YEAR = Pattern.compile("%.?YEAR.?%");

	public Renamer(String renameString, String sanitizer, Movie toRename, File oldFile) {
		this.renameString = renameString;
		this.sanitizer = sanitizer;
		this.movie = toRename;
		this.oldFile = oldFile;
	}
	
	public String getNewFileName() {
		extension = FilenameUtils.getExtension(oldFile.toString());
		filename = FilenameUtils.getBaseName(oldFile.toString());
		path = FilenameUtils.getFullPath(oldFile.toString());

		String newName = getSanitizedString (replace());
		newName = path + newName + getAppendix() + "." + extension;
		
		return newName;
	}
	
	private String replace() {
		String movieID = movie.getId().getId();
		String movieTitle = movie.getTitle().getTitle();
		List<Actor> movieActorsList = movie.getActors();
		String movieActors = combineList(movieActorsList);
		String movieYear = movie.getYear().getYear();
		
		String newName = renameString;
		
		movieTitle = substring( movieTitle, 60);
		movieActors = substring( movieActors, 60);
		
		newName = replace(ID, newName, movieID);
		newName = replace(TITLE, newName, movieTitle);
		newName = replace(ACTORS, newName, movieActors);
		newName = replace(YEAR, newName, movieYear);
		
		return newName;
	}
	
	private String combineList(List<Actor> actors) {
		String actorsString = "";
		for (int i = 0; i < movie.getActors().size(); i++) {
			actorsString += movie.getActors().get(i).getName();
			if (i + 1 < movie.getActors().size())
				actorsString += ", ";
		}
		return actorsString;
	}
	
	private String getAppendix() {
		String appendix = "";
		boolean hasAppendix = filename.matches(".*CD\\s?1.*");
		if (hasAppendix)
			appendix = "-cd1";
		hasAppendix = filename.matches(".*CD\\s?2.*");
		if (hasAppendix)
			appendix = "-cd2";
		hasAppendix = filename.matches(".*CD\\s?3.*");
		if (hasAppendix)
			appendix = "-cd3";
		
		return appendix;
	}
	
	private String getSanitizedString(String fileName) {
		final Pattern ILLEGAL_CHARACTERS = Pattern.compile(sanitizer);
		fileName = ILLEGAL_CHARACTERS.matcher(fileName).replaceAll("").replaceAll("\\s+", " ").trim();
		return fileName;
	}
	
	private String getMatch(Pattern pattern, String toMatch) {
		Matcher matcher = pattern.matcher(toMatch);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}
	
	private String replace(Pattern pattern, String string, String replacement) {
		String match = getMatch(pattern, string);
		String prefix = getPrefix(match, pattern.pattern());
		String suffix = getSuffix(match, pattern.pattern());
		String rep = "";
		if (replacement == null || replacement.isEmpty()) {
			rep = replacement;
		} else {
			rep = prefix + replacement + suffix;
		}
				
		String result = string.replace(match, rep);
		return result;
	}
	
	private String getPrefix(String foundPattern, String pattern) {
		if (foundPattern.length() > 0) {
			String s = foundPattern.substring(1, 2);
			String b = pattern.substring(3,4);
			if (!s.equals( b ))
				return s;
		}
		return "";
	}
	
	private String getSuffix(String foundPattern, String pattern) {
		int length = foundPattern.length();
		if (foundPattern.length() > 0) {
			String s = foundPattern.substring(length - 2,length - 1);
			int patternLength = pattern.length();
			String b = pattern.substring(patternLength-4,patternLength-3);
			if (!s.equals( b ))
				return s;
		}
		return "";
	}
	
	private String substring(String string, int maxLength) {
		if (string.length() > maxLength)
			return string.substring(0, maxLength);
		return string;
	}
	
}
