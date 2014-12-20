package moviescraper.doctord.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

public class MoviescraperPreferences {

	Properties programPreferences;
	private static final String fileNameOfPreferences = "settings.xml";
	//for the property names, it's important (because of reflection) that the values be kept the same as the variable names
	//this lets me be lazy and reuse code for each property setter/getter using a common method :)
	//so don't go renaming these variables without being aware of the consequences!
	private static final String lastUsedDirectory = "lastUsedDirectory";
	private static final String writeFanartAndPosters = "writeFanartAndPosters";
	private static final String overwriteFanartAndPosters = "overwriteFanartAndPosters";
	private static final String downloadActorImagesToActorFolder = "downloadActorImagesToActorFolder";
	private static final String extraFanartScrapingEnabled = "extraFanartScrapingEnabled";
	private static final String createFolderJpg = "createFolderJpg";
	private static final String noMovieNameInImageFiles = "noMovieNameInImageFiles";
	private static final String writeTrailerToFile = "writeTrailerToFile";
	private static final String nfoNamedMovieDotNfo = "nfoNamedMovieDotNfo";
	private static final String useIAFDForActors = "useIAFDForActors";
	private static final String sanitizerForFilename = "sanitizerForFilename";
	private static final String renamerString = "renamerString";
	private static final String renameMovieFile = "renameMovieFile";
	private static final String scrapeInJapanese = "scrapeInJapanese";
	
	/*
	 * useContentBasedTypeIcons:
	 * Use icons in res/mime instead of system icons. 
	 * Needed for linux as system icons only show two types of icons otherwise (files and folders)
	 * There's no menu option for this preference, but you can manually modify the settings file yourself to enable it
	 * this option is also automatically enabled on linux
	 */
	
	private static final String useContentBasedTypeIcons = "useContentBasedTypeIcons";

	public MoviescraperPreferences()
	{
		programPreferences = new Properties();
		try {
			programPreferences.loadFromXML(new FileInputStream(fileNameOfPreferences));
			//initialize default values that must exist in the settings file
			setSanitizerForFilename(getSanitizerForFilename());
			setRenamerString(getRenamerString());
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			savePreferences(); //file doesn't exist. this will create the file
			//initialize default values that must exist in the settings file
			setSanitizerForFilename(getSanitizerForFilename());
			setRenamerString(getRenamerString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void savePreferences(){
		try {
			programPreferences.storeToXML(new FileOutputStream(fileNameOfPreferences), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File getLastUsedDirectory(){
		String lastUsedDir = programPreferences.getProperty(lastUsedDirectory);
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
		programPreferences.setProperty(lastUsedDirectory, lastUsedDirectoryFile.getPath());
		savePreferences();
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



	private void setBooleanValue(String preferenceName, boolean preferenceValue) {
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			if(preferenceValue)
				programPreferences.setProperty((String)fieldToUse.get(new String()), "true");
			else
				programPreferences.setProperty((String)fieldToUse.get(new String()), "false");
			savePreferences();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean getBooleanValue(String preferenceName, boolean defaultValue)
	{
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			String fieldValue = (String)fieldToUse.get(new String());
			String preferenceValue = programPreferences.getProperty(fieldValue);
			if(preferenceValue == null)
				return defaultValue;
			if(preferenceValue.equals("true"))
				return true;
			else if(preferenceValue.equals("false"))
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultValue;
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
		/*String writeFanartAndPostersPref = programPreferences.getProperty(writeFanartAndPosters);
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
		return programPreferences.getProperty(sanitizerForFilename, "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)");
	}

	public void setSanitizerForFilename(String preferenceValue) {
		programPreferences.setProperty(sanitizerForFilename, preferenceValue);
	}

	public String getRenamerString() {
		return programPreferences.getProperty(renamerString, "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]");
	}

	public void setRenamerString(String preferenceValue) {
		programPreferences.setProperty(renamerString, preferenceValue);
		savePreferences();
	}

	public boolean getRenameMovieFile() {
		return getBooleanValue(renameMovieFile, false);
	}

	public void setRenameMovieFile(boolean preferenceValue) {
		setBooleanValue(renameMovieFile, preferenceValue);
		savePreferences();
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
	
	public String toString(){
		return programPreferences.toString();
	}

}
