package moviescraper.doctord.preferences;

import org.apache.commons.lang3.StringUtils;


public class MoviescraperPreferences extends Settings {
	
	private static MoviescraperPreferences INSTANCE;
	
	enum Key implements Settings.Key {
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
		isFirstWordOfFileID,
		appendIDToStartOfTitle,
		selectedScrapers,
		;

		@Override
		public String getKey() {
			return "Preferences:" + toString();
		}
	}
	
	private MoviescraperPreferences(){
		
		//initialize default values that must exist in the settings file

	}
	
	public static synchronized MoviescraperPreferences getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new MoviescraperPreferences();
			INSTANCE.setSanitizerForFilename(getSanitizerForFilename());
			INSTANCE.setRenamerString(getRenamerString());
		}
		return INSTANCE;
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

	public static String getSanitizerForFilename() {
		return getStringValue(Key.sanitizerForFilename, "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)");
	}

	public void setSanitizerForFilename(String preferenceValue) {
		setStringValue(Key.sanitizerForFilename, preferenceValue);
	}

	public static String getRenamerString() {
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
	
	public boolean getPromptForUserProvidedURLWhenScraping(){
		return getBooleanValue(Key.promptForUserProvidedURLWhenScraping, false);
	}

	public void setPromptForUserProvidedURLWhenScraping(boolean preferenceValue){
		setBooleanValue(Key.promptForUserProvidedURLWhenScraping, preferenceValue);
	}
	
	public boolean getIsFirstWordOfFileID(){
		return getBooleanValue(Key.isFirstWordOfFileID, false);
	}
	
	public void setIsFirstWordOfFileID(boolean preferenceValue){
		setBooleanValue(Key.isFirstWordOfFileID, preferenceValue);
	}
	
	public boolean getAppendIDToStartOfTitle(){
		return getBooleanValue(Key.appendIDToStartOfTitle, false);
	}
	
	public void setAppendIDToStartOfTitle(boolean preferenceValue){
		setBooleanValue(Key.appendIDToStartOfTitle, preferenceValue);
	}

	public String[] getSelectedScrapers(){
		String[] defaultValue = { "DMM.co.jp","ActionJav","SquarePlus","JavLibrary", "JavZoo", "R18.com" };
		
		String preferenceValue = getStringValue(Key.selectedScrapers, null);
		
		if (preferenceValue == null)
			return defaultValue;
		
		return preferenceValue.split(";");
	}
	
	public void setSelectedScrapers(String[] preferenceValue){
		String value = StringUtils.join(preferenceValue, ";");
		setStringValue(Key.selectedScrapers, value);
	}

}
