package moviescraper.doctord.preferences;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class MoviescraperPreferences extends Settings {

	enum Key implements Settings.Key {
		lastUsedDirectory,
		writeFanartAndPosters,
		overwriteFanartAndPosters,
		downloadActorImagesToActorFolder,
		extraFanartScrapingEnabled,
		createFolderJpg,
		noMovieNameInImageFiles,
		writeTrailerToFile,
		nfoNamedMovieDotNfo,
		useIAFDForActors,
		sanitizerForFilename, 
		renamerString,
		renameMovieFile,
		scrapeInJapanese,
		promptForUserProvidedURLWhenScraping,
		useContentBasedTypeIcons,
		;

		@Override
		public String getKey() {
			return toString();
		}
	}
	
	public MoviescraperPreferences(){
		super();
		
		//initialize default values that must exist in the settings file
		setSanitizerForFilename(getSanitizerForFilename());
		setRenamerString(getRenamerString());
	}

	public File getLastUsedDirectory(){
		String lastUsedDir =  getStringValue(Key.lastUsedDirectory, null);

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
		setStringValue(Key.lastUsedDirectory, lastUsedDirectoryFile.getPath());
	}

	public void setOverWriteFanartAndPostersPreference(boolean preferenceValue){
		setBooleanValue(Key.overwriteFanartAndPosters, preferenceValue);
	}

	public boolean getOverWriteFanartAndPostersPreference()
	{
		return getBooleanValue(Key.overwriteFanartAndPosters, true);
	}

	public void setWriteFanartAndPostersPreference(boolean preferenceValue){
		setBooleanValue(Key.writeFanartAndPosters, preferenceValue);
	}



	public void setDownloadActorImagesToActorFolderPreference(boolean preferenceValue)
	{
		setBooleanValue(Key.downloadActorImagesToActorFolder, preferenceValue);
	}

	public boolean getDownloadActorImagesToActorFolderPreference()
	{
		return getBooleanValue(Key.downloadActorImagesToActorFolder, true);
	}

	public boolean getWriteFanartAndPostersPreference() {
		return getBooleanValue(Key.writeFanartAndPosters, true);
	}

	public boolean getExtraFanartScrapingEnabledPreference() {
		return getBooleanValue(Key.extraFanartScrapingEnabled, false);
	}

	public void setExtraFanartScrapingEnabledPreference(boolean preferenceValue){
		setBooleanValue(Key.extraFanartScrapingEnabled, preferenceValue);
	}

	public void setCreateFolderJpgEnabledPreference(boolean preferenceValue) {
		setBooleanValue(Key.createFolderJpg, preferenceValue);

	}

	public boolean getCreateFolderJpgEnabledPreference() {
		return getBooleanValue(Key.createFolderJpg, false);
	}

	public boolean getNoMovieNameInImageFiles(){
		return getBooleanValue(Key.noMovieNameInImageFiles, false);
	}

	public void setNoMovieNameInImageFiles(boolean preferenceValue){
		setBooleanValue(Key.noMovieNameInImageFiles, preferenceValue);
	}

	public boolean getWriteTrailerToFile(){
		return getBooleanValue(Key.writeTrailerToFile, false);
	}

	public void setWriteTrailerToFile(boolean preferenceValue){
		setBooleanValue(Key.writeTrailerToFile, preferenceValue);
	}

	public boolean getNfoNamedMovieDotNfo(){
		return getBooleanValue(Key.nfoNamedMovieDotNfo, false);
	}

	public void setNfoNamedMovieDotNfo(boolean preferenceValue){
		setBooleanValue(Key.nfoNamedMovieDotNfo, preferenceValue);
	}

	public boolean getUseIAFDForActors() {
		return getBooleanValue(Key.useIAFDForActors, false);
	}

	public void setUseIAFDForActors(boolean preferenceValue) {
		setBooleanValue(Key.useIAFDForActors, preferenceValue);
	}

	public String getSanitizerForFilename() {
		return getStringValue(Key.sanitizerForFilename, "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)");
	}

	public void setSanitizerForFilename(String preferenceValue) {
		setStringValue(Key.sanitizerForFilename, preferenceValue);
	}

	public String getRenamerString() {
		return getStringValue(Key.renamerString, "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]");
	}

	public void setRenamerString(String preferenceValue) {
		setStringValue(Key.renamerString, preferenceValue);
	}

	public boolean getRenameMovieFile() {
		return getBooleanValue(Key.renameMovieFile, false);
	}

	public void setRenameMovieFile(boolean preferenceValue) {
		setBooleanValue(Key.renameMovieFile, preferenceValue);
	}

	public boolean getScrapeInJapanese(){
		return getBooleanValue(Key.scrapeInJapanese, false);
	}

	public void setScrapeInJapanese(boolean preferenceValue){
		setBooleanValue(Key.scrapeInJapanese, preferenceValue);
	}

	public boolean getUseContentBasedTypeIcons() {
	/*    
     * Use icons in res/mime instead of system icons. 
     * Needed for linux as system icons only show two types of icons otherwise (files and folders)
     * There's no menu option for this preference, but you can manually modify the settings file yourself to enable it
     * this option is also automatically enabled on linux
     */

		// if we're on linux we want the content based icons as default        
		boolean defaultValue = SystemUtils.IS_OS_LINUX;

		return getBooleanValue(Key.useContentBasedTypeIcons, defaultValue);
	}

	public void setUseContentBasedTypeIcons(boolean preferenceValue) {
		setBooleanValue(Key.useContentBasedTypeIcons, preferenceValue);    
	}
	
	public boolean getPromptForUserProvidedURLWhenScraping(){
		return getBooleanValue(Key.promptForUserProvidedURLWhenScraping, false);
	}

	public void setPromptForUserProvidedURLWhenScraping(boolean preferenceValue){
		setBooleanValue(Key.promptForUserProvidedURLWhenScraping, preferenceValue);
	}


}
