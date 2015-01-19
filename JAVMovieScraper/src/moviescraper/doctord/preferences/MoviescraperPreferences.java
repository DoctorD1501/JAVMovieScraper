package moviescraper.doctord.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

public class MoviescraperPreferences extends Settings {

	//for the property names, it's important (because of reflection) that the values be kept the same as the variable names
	//this lets me be lazy and reuse code for each property setter/getter using a common method :)
	//so don't go renaming these variables without being aware of the consequences!
	protected static final String lastUsedDirectory = "lastUsedDirectory";
	protected static final String writeFanartAndPosters = "writeFanartAndPosters";
	protected static final String overwriteFanartAndPosters = "overwriteFanartAndPosters";
	protected static final String downloadActorImagesToActorFolder = "downloadActorImagesToActorFolder";
	protected static final String extraFanartScrapingEnabled = "extraFanartScrapingEnabled";
	protected static final String createFolderJpg = "createFolderJpg";
	protected static final String noMovieNameInImageFiles = "noMovieNameInImageFiles";
	protected static final String writeTrailerToFile = "writeTrailerToFile";
	protected static final String nfoNamedMovieDotNfo = "nfoNamedMovieDotNfo";
	protected static final String useIAFDForActors = "useIAFDForActors";
	protected static final String sanitizerForFilename = "sanitizerForFilename";
	protected static final String renamerString = "renamerString";
	protected static final String renameMovieFile = "renameMovieFile";
	protected static final String scrapeInJapanese = "scrapeInJapanese";
	protected static final String promptForUserProvidedURLWhenScraping = "promptForUserProvidedURLWhenScraping";
	
	/*
	 * useContentBasedTypeIcons:
	 * Use icons in res/mime instead of system icons. 
	 * Needed for linux as system icons only show two types of icons otherwise (files and folders)
	 * There's no menu option for this preference, but you can manually modify the settings file yourself to enable it
	 * this option is also automatically enabled on linux
	 */
	
	protected static final String useContentBasedTypeIcons = "useContentBasedTypeIcons";

	public MoviescraperPreferences(){
		super();
		
		//initialize default values that must exist in the settings file
		setSanitizerForFilename(getSanitizerForFilename());
		setRenamerString(getRenamerString());
	}

	public File getLastUsedDirectory(){
		String lastUsedDir = getStringValue(lastUsedDirectory, null);
		if(lastUsedDir != null)
		{
			File lastUsedDirFile = new File(lastUsedDir);
			if(lastUsedDirFile.exists())
				return lastUsedDirFile;
			else return new File(System.getProperty("user.home"));
		}
		else return new File(System.getProperty("user.home"));
	}

	public void setLastUsedDirectory(File lastUsedDirectoryFile){
		setStringValue(lastUsedDirectory, lastUsedDirectoryFile.getPath());
	}

	public void setOverWriteFanartAndPostersPreference(boolean preferenceValue){
		setBooleanValue(overwriteFanartAndPosters, preferenceValue);
	}

	public boolean getOverWriteFanartAndPostersPreference()
	{
		return getBooleanValue(overwriteFanartAndPosters, true);
	}

	public void setWriteFanartAndPostersPreference(boolean preferenceValue){
		setBooleanValue(writeFanartAndPosters, preferenceValue);
	}



	public void setDownloadActorImagesToActorFolderPreference(boolean preferenceValue)
	{
		setBooleanValue(downloadActorImagesToActorFolder, preferenceValue);
	}

	public boolean getDownloadActorImagesToActorFolderPreference()
	{
		return getBooleanValue(downloadActorImagesToActorFolder, true);
	}

	public boolean getWriteFanartAndPostersPreference() {
		/*String writeFanartAndPostersPref = getStringValue(writeFanartAndPosters);
		if(writeFanartAndPostersPref != null)
		{
		if(writeFanartAndPostersPref.equals("true"))
			return true;
		else return false;
		}
		else return true; //default value if no preference has been set yet*/
		return getBooleanValue(writeFanartAndPosters, true);
	}

	public boolean getExtraFanartScrapingEnabledPreference() {
		return getBooleanValue(extraFanartScrapingEnabled, false);
	}

	public void setExtraFanartScrapingEnabledPreference(boolean preferenceValue){
		setBooleanValue(extraFanartScrapingEnabled, preferenceValue);
	}

	public void setCreateFolderJpgEnabledPreference(boolean preferenceValue) {
		setBooleanValue(createFolderJpg, preferenceValue);

	}

	public boolean getCreateFolderJpgEnabledPreference() {
		return getBooleanValue(createFolderJpg, false);
	}

	public boolean getNoMovieNameInImageFiles(){
		return getBooleanValue(noMovieNameInImageFiles, false);
	}

	public void setNoMovieNameInImageFiles(boolean preferenceValue){
		setBooleanValue(noMovieNameInImageFiles, preferenceValue);
	}

	public boolean getWriteTrailerToFile(){
		return getBooleanValue(writeTrailerToFile, false);
	}

	public void setWriteTrailerToFile(boolean preferenceValue){
		setBooleanValue(writeTrailerToFile, preferenceValue);
	}

	public boolean getNfoNamedMovieDotNfo(){
		return getBooleanValue(nfoNamedMovieDotNfo, false);
	}

	public void setNfoNamedMovieDotNfo(boolean preferenceValue){
		setBooleanValue(nfoNamedMovieDotNfo, preferenceValue);
	}

	public boolean getUseIAFDForActors() {
		return getBooleanValue(useIAFDForActors, false);
	}

	public void setUseIAFDForActors(boolean preferenceValue) {
		setBooleanValue(useIAFDForActors, preferenceValue);
	}

	public String getSanitizerForFilename() {
		return getStringValue(sanitizerForFilename, "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)");
	}

	public void setSanitizerForFilename(String preferenceValue) {
		setStringValue(sanitizerForFilename, preferenceValue);
	}

	public String getRenamerString() {
		return getStringValue(renamerString, "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]");
	}

	public void setRenamerString(String preferenceValue) {
		setStringValue(renamerString, preferenceValue);
	}

	public boolean getRenameMovieFile() {
		return getBooleanValue(renameMovieFile, false);
	}

	public void setRenameMovieFile(boolean preferenceValue) {
		setBooleanValue(renameMovieFile, preferenceValue);
	}

	public boolean getScrapeInJapanese(){
		return getBooleanValue(scrapeInJapanese, false);
	}

	public void setScrapeInJapanese(boolean preferenceValue){
		setBooleanValue(scrapeInJapanese, preferenceValue);
	}

	public boolean getUseContentBasedTypeIcons() {

		// if we're on linux we want the content based icons as default        
		boolean defaultValue = SystemUtils.IS_OS_LINUX;

		return getBooleanValue(useContentBasedTypeIcons, defaultValue);
	}

	public void setUseContentBasedTypeIcons(boolean preferenceValue) {
		setBooleanValue(useContentBasedTypeIcons, preferenceValue);    
	}
	
	public boolean getPromptForUserProvidedURLWhenScraping(){
		return getBooleanValue(promptForUserProvidedURLWhenScraping, false);
	}

	public void setPromptForUserProvidedURLWhenScraping(boolean preferenceValue){
		setBooleanValue(promptForUserProvidedURLWhenScraping, preferenceValue);
	}


}
