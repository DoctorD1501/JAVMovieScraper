package moviescraper.doctord.controller.amalgamation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import com.cedarsoftware.util.io.JsonIoException;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import moviescraper.doctord.controller.siteparsingprofile.IAFDParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.specific.ActionJavParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.DmmParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.ExcaliburFilmsParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavZooParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.R18ParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.SquarePlusParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;


public class AllAmalgamationOrderingPreferences {
	
	Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;
	private static final String settingsFileName = "AmalgamationSettings.json";
	
	public AllAmalgamationOrderingPreferences(){
		allAmalgamationOrderingPreferences = new Hashtable<ScraperGroupName, ScraperGroupAmalgamationPreference>();
	}

	
	@Override
	public String toString(){
		return allAmalgamationOrderingPreferences.toString();
	}
	
	public ScraperGroupAmalgamationPreference getScraperGroupAmalgamationPreference(ScraperGroupName scraperGroupName)
	{
		//make an attempt to reinitialize things if we added a new type of scraping group 
		//and our existing preferences didn't contain that type
		if(!allAmalgamationOrderingPreferences.containsKey(scraperGroupName))
		{
			initializeDefaultPreference(scraperGroupName);
		}
		return allAmalgamationOrderingPreferences.get(scraperGroupName);
	}
	
	//TODO: Good candidate to do this in a more object oriented way
	private void initializeDefaultPreference(ScraperGroupName scraperGroupName) {
		if(scraperGroupName == ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)
			initializeAmericanAdultDVDScraperGroupDefaultPreferences();
		if(scraperGroupName == ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)
			initializeJAVCensoredGroupDefaultPreferences();
	}


	public void putScraperGroupAmalgamationPreference(ScraperGroupName scraperGroupName, ScraperGroupAmalgamationPreference pref)
	{
		allAmalgamationOrderingPreferences.put(scraperGroupName, pref);
	}
	
	public AllAmalgamationOrderingPreferences initializeValuesFromPreferenceFile()
	{
		
		File inputFile = new File(settingsFileName);
		if(!inputFile.exists())
		{
			boolean saveToDisk = true;
			initializeDefaultPreferences(saveToDisk);
			System.out.println("No file existed for amalgamation preferences. Used default preferences.");
			return this;
		}
		else
		{
			try {
				InputStream inputFromFile = new FileInputStream(settingsFileName);
				JsonReader jr = new JsonReader(inputFromFile);
				AllAmalgamationOrderingPreferences jsonObject = (AllAmalgamationOrderingPreferences) jr.readObject();
				jr.close();
				System.out.println("Read in amalgamation preferences from " + settingsFileName);
				return jsonObject;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (JsonIoException e)
			{
				System.out.println("Preference file is not compatible between versions - reinitializing preference file");
				initializeDefaultPreferences(true);
			}
		}
		return this;
	}
	
	public void saveToPreferencesFile() throws FileNotFoundException
	{
		FileOutputStream outputStream = new FileOutputStream(settingsFileName);
		JsonWriter jw = new JsonWriter(outputStream);
		jw.write(this);
		jw.close();
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved amalgamation preferences to " + settingsFileName);
	}
	
	public void initializeDefaultPreferences(boolean saveToDisk){
		if(!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP))
		{
			initializeJAVCensoredGroupDefaultPreferences();
		}
		if(!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP))
		{
			initializeAmericanAdultDVDScraperGroupDefaultPreferences();
		}
		//TODO: define a default ordering for all other scraper types
		
		if (saveToDisk) {
			// Save our preferences to disk
			try {
				saveToPreferencesFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initializeAmericanAdultDVDScraperGroupDefaultPreferences() {
		System.out.println("Initializing default american adult dvd preferences");
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(
				new TheMovieDatabaseParsingProfile(), new Data18MovieParsingProfile(), new ExcaliburFilmsParsingProfile(), new IAFDParsingProfile());
		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(
				ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP,
				overallOrdering);
		
		allAmalgamationOrderingPreferences
		.put(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP,
				preferences);
	}


	private void initializeJAVCensoredGroupDefaultPreferences()
	{
		System.out.println("Initializing default jav preferences");
		
		//JAV Preferences
		
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(
				new R18ParsingProfile(), new JavLibraryParsingProfile(),
				new JavZooParsingProfile(), new SquarePlusParsingProfile(),
				new ActionJavParsingProfile(), new DmmParsingProfile());
		
		
		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(
				ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP,
				overallOrdering);
		
		//Specific preferences for each field of JAV
		try {
			// DMM, JavLibrary, JavZoo have japanese title, only DMM is scraped at the moment
			DataItemSourceAmalgamationPreference bestContentForOriginalTitle = new DataItemSourceAmalgamationPreference(
					new DmmParsingProfile(), new JavLibraryParsingProfile(), new JavZooParsingProfile());
			preferences.setCustomOrderingForField("originalTitle", bestContentForOriginalTitle);
			
			
			// R18 has the absolute best title information. Pick any english
			// site first, fallback to machine translated DMM
			DataItemSourceAmalgamationPreference bestContentForID = new DataItemSourceAmalgamationPreference(
					new DmmParsingProfile(), new R18ParsingProfile(),
					new JavLibraryParsingProfile(),
					new ActionJavParsingProfile(),
					new SquarePlusParsingProfile(),
					new JavZooParsingProfile());
			preferences
					.setCustomOrderingForField("id", bestContentForID);
			
			// R18 has the absolute best title information. Pick any english
			// site first, fallback to machine translated DMM
			DataItemSourceAmalgamationPreference bestContentForTitle = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavLibraryParsingProfile(),
					new ActionJavParsingProfile(), new SquarePlusParsingProfile(),
					new JavZooParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("title", bestContentForTitle);

			// R18 has the best plot data for english. Set the plot from
			// ActionJav only if R18 didn't have one already
			DataItemSourceAmalgamationPreference bestContentForPlot = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new ActionJavParsingProfile(),
					new DmmParsingProfile());
			preferences.setCustomOrderingForField("plot", bestContentForPlot);

			// R18 has the best set data for english, JavZoo is OK
			DataItemSourceAmalgamationPreference bestContentForSet = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavZooParsingProfile(),
					new DmmParsingProfile());
			preferences.setCustomOrderingForField("set", bestContentForSet);

			// R18 has the best studio data for english
			DataItemSourceAmalgamationPreference bestContentForStudio = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavLibraryParsingProfile(),
					new ActionJavParsingProfile(), new JavZooParsingProfile(),
					new SquarePlusParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("studio", bestContentForStudio);

			// R18 has the best genre data for english, fallback to machine
			// translated DMM data
			DataItemSourceAmalgamationPreference bestContentForGenres = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavLibraryParsingProfile(),
					new JavZooParsingProfile(), new SquarePlusParsingProfile(),
					new ActionJavParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("genres", bestContentForGenres);

			// Get ActionJav actors if both JavLib and R18 didn't have any.
			DataItemSourceAmalgamationPreference bestContentForActorsAndDirectors = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavLibraryParsingProfile(),
					new JavZooParsingProfile(), new ActionJavParsingProfile(),
					new DmmParsingProfile(), new SquarePlusParsingProfile());
			preferences.setCustomOrderingForField("actors", bestContentForActorsAndDirectors);
			preferences.setCustomOrderingForField("directors", bestContentForActorsAndDirectors);
			

			// DMM always has the best fanart and posters and extraFanart
			DataItemSourceAmalgamationPreference bestContentForPosterAndFanart = new DataItemSourceAmalgamationPreference(
					new DmmParsingProfile(), new R18ParsingProfile(),
					new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
					new SquarePlusParsingProfile(), new JavZooParsingProfile());
			preferences.setCustomOrderingForField("posters", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("fanart", bestContentForPosterAndFanart);
			preferences.setCustomOrderingForField("extraFanart", bestContentForPosterAndFanart);


			// Both DMM and R18 have the same trailer from their respective
			// sites
			DataItemSourceAmalgamationPreference bestContentForTrailer = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("trailer", bestContentForTrailer);

			// Only DMM and JavLibrary has ratings
			DataItemSourceAmalgamationPreference bestContentForRating = new DataItemSourceAmalgamationPreference(
					new JavLibraryParsingProfile(), new DmmParsingProfile());
			preferences.setCustomOrderingForField("rating", bestContentForRating);


			// Non localized data: year, release date, runtime...
			DataItemSourceAmalgamationPreference bestContentForDateAndTime = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new DmmParsingProfile(),
					new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
					new SquarePlusParsingProfile(), new JavZooParsingProfile());
			preferences.setCustomOrderingForField("year", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("releaseDate", bestContentForDateAndTime);
			preferences.setCustomOrderingForField("runtime", bestContentForDateAndTime);
			
			allAmalgamationOrderingPreferences
					.put(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP,
							preferences);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void reinitializeDefaultPreferences(){
		allAmalgamationOrderingPreferences.clear();
		boolean saveToDisk = false;
		initializeDefaultPreferences(saveToDisk);
	}


	public Map<ScraperGroupName, ScraperGroupAmalgamationPreference> getAllAmalgamationOrderingPreferences() {
		return allAmalgamationOrderingPreferences;
	}


	public void setAllAmalgamationOrderingPreferences(
			Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences) {
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
	}
	


}
