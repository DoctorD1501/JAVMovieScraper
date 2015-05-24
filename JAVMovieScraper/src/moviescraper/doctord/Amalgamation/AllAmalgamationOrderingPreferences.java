package moviescraper.doctord.Amalgamation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.SiteParsingProfile.specific.ActionJavParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavZooParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.R18ParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.SquarePlusParsingProfile;


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
		return allAmalgamationOrderingPreferences.get(scraperGroupName);
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
		}
		return this;
	}
	
	public void saveToPreferencesFile() throws FileNotFoundException
	{
		FileOutputStream outputStream = new FileOutputStream(settingsFileName);
		JsonWriter jw = new JsonWriter(outputStream);
		jw.write(this);
		jw.close();
		System.out.println("Saved amalgamation preferences to " + settingsFileName);
	}
	
	public void initializeDefaultPreferences(boolean saveToDisk){
		if(!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP))
		{
			System.out.println("Initializing default jav preferences");
			
			//JAV Preferences
			
			DataItemSourceAmalgamationPreference overallJavOrdering = new DataItemSourceAmalgamationPreference(
					new R18ParsingProfile(), new JavLibraryParsingProfile(),
					new JavZooParsingProfile(), new SquarePlusParsingProfile(),
					new ActionJavParsingProfile(), new DmmParsingProfile());
			
			
			ScraperGroupAmalgamationPreference javPreferences = new ScraperGroupAmalgamationPreference(
					ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP,
					overallJavOrdering);
			
			//Specific preferences for each field of JAV
			try {
				// DMM, JavLibrary, JavZoo have japanese title, only DMM is scraped at the moment
				DataItemSourceAmalgamationPreference bestContentForOriginalTitle = new DataItemSourceAmalgamationPreference(
						new DmmParsingProfile(), new JavLibraryParsingProfile(), new JavZooParsingProfile());
				javPreferences.setCustomOrderingForField("originalTitle", bestContentForOriginalTitle);
				
				
				// R18 has the absolute best title information. Pick any english
				// site first, fallback to machine translated DMM
				DataItemSourceAmalgamationPreference bestContentForID = new DataItemSourceAmalgamationPreference(
						new DmmParsingProfile(), new R18ParsingProfile(),
						new JavLibraryParsingProfile(),
						new ActionJavParsingProfile(),
						new SquarePlusParsingProfile(),
						new JavZooParsingProfile());
				javPreferences
						.setCustomOrderingForField("id", bestContentForID);
				
				// R18 has the absolute best title information. Pick any english
				// site first, fallback to machine translated DMM
				DataItemSourceAmalgamationPreference bestContentForTitle = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new JavLibraryParsingProfile(),
						new ActionJavParsingProfile(), new SquarePlusParsingProfile(),
						new JavZooParsingProfile(), new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("title", bestContentForTitle);

				// R18 has the best plot data for english. Set the plot from
				// ActionJav only if R18 didn't have one already
				DataItemSourceAmalgamationPreference bestContentForPlot = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new ActionJavParsingProfile(),
						new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("plot", bestContentForPlot);

				// R18 has the best set data for english, JavZoo is OK
				DataItemSourceAmalgamationPreference bestContentForSet = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new JavZooParsingProfile(),
						new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("set", bestContentForSet);

				// R18 has the best studio data for english
				DataItemSourceAmalgamationPreference bestContentForStudio = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new JavLibraryParsingProfile(),
						new ActionJavParsingProfile(), new JavZooParsingProfile(),
						new SquarePlusParsingProfile(), new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("studio", bestContentForStudio);

				// R18 has the best genre data for english, fallback to machine
				// translated DMM data
				DataItemSourceAmalgamationPreference bestContentForGenres = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new JavLibraryParsingProfile(),
						new JavZooParsingProfile(), new SquarePlusParsingProfile(),
						new ActionJavParsingProfile(), new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("genres", bestContentForGenres);

				// Get ActionJav actors if both JavLib and R18 didn't have any.
				DataItemSourceAmalgamationPreference bestContentForActorsAndDirectors = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new JavLibraryParsingProfile(),
						new JavZooParsingProfile(), new ActionJavParsingProfile(),
						new DmmParsingProfile(), new SquarePlusParsingProfile());
				javPreferences.setCustomOrderingForField("actors", bestContentForActorsAndDirectors);
				javPreferences.setCustomOrderingForField("directors", bestContentForActorsAndDirectors);
				

				// DMM always has the best fanart and posters and extraFanart
				DataItemSourceAmalgamationPreference bestContentForPosterAndFanart = new DataItemSourceAmalgamationPreference(
						new DmmParsingProfile(), new R18ParsingProfile(),
						new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
						new SquarePlusParsingProfile(), new JavZooParsingProfile());
				javPreferences.setCustomOrderingForField("posters", bestContentForPosterAndFanart);
				javPreferences.setCustomOrderingForField("fanart", bestContentForPosterAndFanart);
				javPreferences.setCustomOrderingForField("extraFanart", bestContentForPosterAndFanart);


				// Both DMM and R18 have the same trailer from their respective
				// sites
				DataItemSourceAmalgamationPreference bestContentForTrailer = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("trailer", bestContentForTrailer);

				// Only DMM and JavLibrary has ratings
				DataItemSourceAmalgamationPreference bestContentForRating = new DataItemSourceAmalgamationPreference(
						new JavLibraryParsingProfile(), new DmmParsingProfile());
				javPreferences.setCustomOrderingForField("rating", bestContentForRating);


				// Non localized data: release date, runtime...
				DataItemSourceAmalgamationPreference bestContentForDateAndTime = new DataItemSourceAmalgamationPreference(
						new R18ParsingProfile(), new DmmParsingProfile(),
						new JavLibraryParsingProfile(), new ActionJavParsingProfile(),
						new SquarePlusParsingProfile(), new JavZooParsingProfile());
				javPreferences.setCustomOrderingForField("year", bestContentForDateAndTime);
				javPreferences.setCustomOrderingForField("runtime", bestContentForDateAndTime);
				
				allAmalgamationOrderingPreferences
						.put(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP,
								javPreferences);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
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
