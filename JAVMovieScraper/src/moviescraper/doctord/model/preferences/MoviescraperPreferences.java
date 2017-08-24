package moviescraper.doctord.model.preferences;

import moviescraper.doctord.view.FavoriteGenrePickerPanel;

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
		considerUserSelectionOneURLWhenScraping, //Consider all selected items to be one 'movie'.  To keep from being prompted for each CD/Scene
		isFirstWordOfFileID, //Usually the scraper expects the last word of the file to be the ID. This option if enabled will instead look at the first word. 
		appendIDToStartOfTitle, //Scraped ID will be put as the first word of the title if enabled. Useful for people who like to keep releases from the same company alphabetically together.
		useFilenameAsTitle, //Filename will be writen to the title field of the nfo file instead of using the scraped result
		selectArtManuallyWhenScraping, //Confirmation dialog to allow user to select art will be shown. If false, art is still picked, but it will be automatically chosen.
		selectSearchResultManuallyWhenScraping, //Confirmation dialog to allow user to pick which search result they want to use will be shown.
		confirmCleanUpFileNameNameBeforeRenaming, // Show a dialog asking the user to confirm the rename of a file each time using the File Name Cleanup feature
		frequentlyUsedGenres, //Used in genre editing to store user's list of frequently used genres to aid in quickly adding genres to a movie
		frequentlyUsedTags, //Used in tag editing to store user's list of frequently used tags to aid in quickly adding tags to a movie
		writeThumbTagsForPosterAndFanartToNfo //Whether to write the <thumb> tag into the nfo
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


	public void setOverWriteFanartAndPostersPreference(Boolean preferenceValue){
		setBooleanValue(Key.overwriteFanartAndPosters, preferenceValue);
	}

	public Boolean getOverWriteFanartAndPostersPreference()
	{
		return getBooleanValue(Key.overwriteFanartAndPosters, Boolean.TRUE);
	}

	public void setWriteFanartAndPostersPreference(Boolean preferenceValue){
		setBooleanValue(Key.writeFanartAndPosters, preferenceValue);
	}



	public void setDownloadActorImagesToActorFolderPreference(Boolean preferenceValue)
	{
		setBooleanValue(Key.downloadActorImagesToActorFolder, preferenceValue);
	}

	public Boolean getDownloadActorImagesToActorFolderPreference()
	{
		return getBooleanValue(Key.downloadActorImagesToActorFolder, true);
	}

	public Boolean getWriteFanartAndPostersPreference() {
		return getBooleanValue(Key.writeFanartAndPosters, true);
	}

	public Boolean getExtraFanartScrapingEnabledPreference() {
		return getBooleanValue(Key.extraFanartScrapingEnabled, false);
	}

	public void setExtraFanartScrapingEnabledPreference(Boolean preferenceValue){
		setBooleanValue(Key.extraFanartScrapingEnabled, preferenceValue);
	}

	public void setCreateFolderJpgEnabledPreference(Boolean preferenceValue) {
		setBooleanValue(Key.createFolderJpg, preferenceValue);

	}

	public Boolean getCreateFolderJpgEnabledPreference() {
		return getBooleanValue(Key.createFolderJpg, false);
	}

	public Boolean getNoMovieNameInImageFiles(){
		return getBooleanValue(Key.noMovieNameInImageFiles, Boolean.FALSE);
	}

	public void setNoMovieNameInImageFiles(Boolean preferenceValue){
		setBooleanValue(Key.noMovieNameInImageFiles, preferenceValue);
	}

	public Boolean getWriteTrailerToFile(){
		return getBooleanValue(Key.writeTrailerToFile, Boolean.FALSE);
	}

	public void setWriteTrailerToFile(Boolean preferenceValue){
		setBooleanValue(Key.writeTrailerToFile, preferenceValue);
	}

	public Boolean getNfoNamedMovieDotNfo(){
		return getBooleanValue(Key.nfoNamedMovieDotNfo, Boolean.FALSE);
	}

	public void setNfoNamedMovieDotNfo(Boolean preferenceValue){
		setBooleanValue(Key.nfoNamedMovieDotNfo, preferenceValue);
	}

	public Boolean getUseIAFDForActors() {
		return getBooleanValue(Key.useIAFDForActors, Boolean.FALSE);
	}

	public void setUseIAFDForActors(Boolean preferenceValue) {
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

	public Boolean getRenameMovieFile() {
		return getBooleanValue(Key.renameMovieFile, Boolean.FALSE);
	}

	public void setRenameMovieFile(Boolean preferenceValue) {
		setBooleanValue(Key.renameMovieFile, preferenceValue);
	}

	public Boolean getScrapeInJapanese(){
		return getBooleanValue(Key.scrapeInJapanese, Boolean.FALSE);
	}

	public void setScrapeInJapanese(Boolean preferenceValue){
		setBooleanValue(Key.scrapeInJapanese, preferenceValue);
	}
	
	public Boolean getPromptForUserProvidedURLWhenScraping(){
		return getBooleanValue(Key.promptForUserProvidedURLWhenScraping, Boolean.FALSE);
	}

	public void setPromptForUserProvidedURLWhenScraping(Boolean preferenceValue){
		setBooleanValue(Key.promptForUserProvidedURLWhenScraping, preferenceValue);
	}

        public Boolean getConsiderUserSelectionOneURLWhenScraping(){
		return getBooleanValue(Key.considerUserSelectionOneURLWhenScraping, Boolean.FALSE);
	}

	public void setConsiderUserSelectionOneURLWhenScraping(Boolean preferenceValue){
		setBooleanValue(Key.considerUserSelectionOneURLWhenScraping, preferenceValue);
	}
	
	public Boolean getIsFirstWordOfFileID(){
		return getBooleanValue(Key.isFirstWordOfFileID, Boolean.FALSE);
	}
	
	public void setIsFirstWordOfFileID(Boolean preferenceValue){
		setBooleanValue(Key.isFirstWordOfFileID, preferenceValue);
	}
	
	public Boolean getAppendIDToStartOfTitle(){
		return getBooleanValue(Key.appendIDToStartOfTitle, Boolean.FALSE);
	}
	
	public void setAppendIDToStartOfTitle(Boolean preferenceValue){
		setBooleanValue(Key.appendIDToStartOfTitle, preferenceValue);
	}
	
	public Boolean getUseFileNameAsTitle(){
		return getBooleanValue(Key.useFilenameAsTitle, Boolean.FALSE);
	}
	
	public void setUseFileNameAsTitle(Boolean preferenceValue){
		setBooleanValue(Key.useFilenameAsTitle, preferenceValue);
	}
	
	public Boolean getSelectArtManuallyWhenScraping()
	{
		return getBooleanValue(Key.selectArtManuallyWhenScraping, Boolean.TRUE);
	}
	
	public void setSelectArtManuallyWhenScraping(Boolean preferenceValue)
	{
		setBooleanValue(Key.selectArtManuallyWhenScraping, preferenceValue);
	}
	
	public Boolean getSelectSearchResultManuallyWhenScraping()
	{
		return getBooleanValue(Key.selectSearchResultManuallyWhenScraping, Boolean.FALSE);
	}
	
	public void setSelectSearchResultManuallyWhenScraping(Boolean preferenceValue)
	{
		setBooleanValue(Key.selectSearchResultManuallyWhenScraping, preferenceValue);
	}
	
	public Boolean getConfirmCleanUpFileNameNameBeforeRenaming()
	{
		return getBooleanValue(Key.confirmCleanUpFileNameNameBeforeRenaming, Boolean.TRUE);
	}
	
	public void setConfirmCleanUpFileNameNameBeforeRenaming(Boolean preferenceValue)
	{
		setBooleanValue(Key.confirmCleanUpFileNameNameBeforeRenaming, preferenceValue);
	}
	
	public String getfrequentlyUsedGenres()
	{
		return getStringValue(Key.frequentlyUsedGenres, "Adult" + FavoriteGenrePickerPanel.listSeperator +  "JAV");
	}
	
	public void setFrequentlyUsedGenres(String value)
	{
		setStringValue(Key.frequentlyUsedGenres, value);
	}
	
	public String getfrequentlyUsedTags()
	{
		return getStringValue(Key.frequentlyUsedTags, "Feature");
	}
	
	public void setFrequentlyUsedTags(String value)
	{
		setStringValue(Key.frequentlyUsedTags, value);
	}
	
	public Boolean getWriteThumbTagsForPosterAndFanartToNfo()
	{
		return getBooleanValue(Key.writeThumbTagsForPosterAndFanartToNfo, true);
	}
	
	public void setWriteThumbTagsForPosterAndFanartToNfo(Boolean preferenceValue)
	{
		setBooleanValue(Key.writeThumbTagsForPosterAndFanartToNfo, preferenceValue);
	}
}
