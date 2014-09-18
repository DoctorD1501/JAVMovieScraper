package moviescraper.doctord.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

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

	public MoviescraperPreferences()
	{
		programPreferences = new Properties();
		try {
			programPreferences.loadFromXML(new FileInputStream(fileNameOfPreferences));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			savePreferences(); //file doesn't exist. this will create the file
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
		/*if(preferenceValue)
			programPreferences.setProperty(overwriteFanartAndPosters, "true");
		else
			programPreferences.setProperty(overwriteFanartAndPosters, "false");
		savePreferences();*/
		setBooleanValue(overwriteFanartAndPosters, preferenceValue);
	}
	
	public boolean getOverWriteFanartAndPostersPreference()
	{
		/*String overwriteFanartAndPostersPref = programPreferences.getProperty(overwriteFanartAndPosters);
		if(overwriteFanartAndPostersPref != null)
		{
		if(overwriteFanartAndPostersPref.equals("true"))
			return true;
		else return false;
		}
		else return true; //default value if no preference has been set yet*/
		return getBooleanValue(overwriteFanartAndPosters, true);
	}
	
	public void setWriteFanartAndPostersPreference(boolean preferenceValue){
		/*if(preferenceValue)
			programPreferences.setProperty(writeFanartAndPosters, "true");
		else
			programPreferences.setProperty(writeFanartAndPosters, "false");
		savePreferences();*/
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
		return programPreferences.getProperty(renamerString, "");
	}
	
	public void setRenamerString(String preferenceValue) {
		programPreferences.setProperty(renamerString, preferenceValue);
	}
	
	public boolean getRenameMovieFile() {
		return getBooleanValue(renameMovieFile, false);
	}
	
	public void setRenameMovieFile(boolean preferenceValue) {
		setBooleanValue(renameMovieFile, preferenceValue);
	}

}
