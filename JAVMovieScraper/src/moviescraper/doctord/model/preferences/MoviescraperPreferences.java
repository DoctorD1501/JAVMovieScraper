package moviescraper.doctord.model.preferences;


public class MoviescraperPreferences extends Settings {
	
	private static MoviescraperPreferences INSTANCE;
	
	enum Key implements Settings.Key {
		writeFanartAndPosters, //fanart and poster files will be downloaded and then written to disk when writing the movie's metadata.
		overwriteFanartAndPosters, //overwrites existing fanart and poster files when writing the metadata to disk
		downloadActorImagesToActorFolder, //creates .actor thumbnail files when writing the metadata
		extraFanartScrapingEnabled, //will attempt to scrape and write extrafanart
		createFolderJpg, //Folder.jpg will be created when writing the file. This is a copy of the movie's poster file. Used in windows to show a thumbnail of the folder in Windows Explorer.
		noMovieNameInImageFiles, //fanart and poster will be called fanart.jpg and poster.jpg instead of also containing with the movie's name within the file
		writeTrailerToFile, //Download the trailer file from the internet and write it to a file when writing the rest of the metadata.
		nfoNamedMovieDotNfo, //.nfo file written out will always be called "movie.nfo"
		useIAFDForActors, //No longer used. Replaced by Amalgamation settings.
		sanitizerForFilename, //Used to help remove illegal characters when renaming the file. For the most part, the user does not need to change this.
		renamerString, //Renamer string set in the renamer configuration gui to apply a renamer rule to the file's name
		folderRenamerString, ////Renamer string set in the renamer configuration gui to apply a renamer rule to the file's folder name
		renameMovieFile, //File will be renamed according to renamer rules when writing the movie file's metadata out to disk.
		scrapeInJapanese, //For sites that support it, downloaded info will be in Japanese instead of English
		promptForUserProvidedURLWhenScraping, //Prompt user to manually provide their own url when scraping a file. Useful if search just can't find a file, but the user knows what to use anyways. Not intended to be left on all the time.
		isFirstWordOfFileID, //Usually the scraper expects the last word of the file to be the ID. This option if enabled will instead look at the first word. 
		appendIDToStartOfTitle, //Scraped ID will be put as the first word of the title if enabled. Useful for people who like to keep releases from the same company alphabetically together.
		useFilenameAsTitle, //Filename will be writen to the title field of the nfo file instead of using the scraped result
		selectArtManuallyWhenScraping, //Confirmation dialog to allow user to select art will be shown. If false, art is still picked, but it will be automatically chosen.
		selectSearchResultManuallyWhenScraping, //Confirmation dialog to allow user to pick which search result they want to use will be shown.
		confirmCleanUpFileNameNameBeforeRenaming // Show a dialog asking the user to confirm the rename of a file each time using the File Name Cleanup feature
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
	
	public static String getFolderRenamerString() {
		return getStringValue(Key.folderRenamerString, "<BASEDIRECTORY><PATHSEPERATOR>");
	}
	
	public void setFolderRenamerString(String preferenceValue) {
		setStringValue(Key.folderRenamerString, preferenceValue);
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
	
	public boolean getUseFileNameAsTitle(){
		return getBooleanValue(Key.useFilenameAsTitle, false);
	}
	
	public void setUseFileNameAsTitle(boolean preferenceValue){
		setBooleanValue(Key.useFilenameAsTitle, preferenceValue);
	}
	
	public boolean getSelectArtManuallyWhenScraping()
	{
		return getBooleanValue(Key.selectArtManuallyWhenScraping, true);
	}
	
	public void setSelectArtManuallyWhenScraping(boolean preferenceValue)
	{
		setBooleanValue(Key.selectArtManuallyWhenScraping, preferenceValue);
	}
	
	public boolean getSelectSearchResultManuallyWhenScraping()
	{
		return getBooleanValue(Key.selectSearchResultManuallyWhenScraping, false);
	}
	
	public void setSelectSearchResultManuallyWhenScraping(boolean preferenceValue)
	{
		setBooleanValue(Key.selectSearchResultManuallyWhenScraping, preferenceValue);
	}
	
	public boolean getConfirmCleanUpFileNameNameBeforeRenaming()
	{
		return getBooleanValue(Key.confirmCleanUpFileNameNameBeforeRenaming, true);
	}
	
	public void setConfirmCleanUpFileNameNameBeforeRenaming(boolean preferenceValue)
	{
		setBooleanValue(Key.confirmCleanUpFileNameNameBeforeRenaming, preferenceValue);
	}
}
