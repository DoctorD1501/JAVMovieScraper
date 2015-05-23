package moviescraper.doctord.Amalgamation;

import java.util.Hashtable;
import java.util.Map;

import moviescraper.doctord.SiteParsingProfile.ActionJavParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.SiteParsingProfile.specific.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavZooParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.R18ParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.SquarePlusParsingProfile;


public class AllAmalgamationOrderingPreferences {
	
	Map<ScraperGroupName, ScraperGroupAmalgamationPreference> allAmalgamationOrderingPreferences;
	
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
	
	public void initializeValuesFromPreferenceFile()
	{
		//Do nothing for now, but I need to do this eventually
		System.out.println("Pretending to initialize the amalgamation values from a preferences file. For now I'm not actually doing this and just initializing some default preferences");
		initializeDefaultPreferences();
	}
	
	public void initializeDefaultPreferences(){
		if(!allAmalgamationOrderingPreferences.containsKey(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP))
		{
			System.out.println("Initializing default jav preferences");
			
			DataItemSourceAmalgamationPreference overallJavOrdering = new DataItemSourceAmalgamationPreference(new R18ParsingProfile(), new JavLibraryParsingProfile(), new JavZooParsingProfile(), new SquarePlusParsingProfile(), new ActionJavParsingProfile(), new DmmParsingProfile());
			ScraperGroupAmalgamationPreference javPreferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, overallJavOrdering);
			allAmalgamationOrderingPreferences.put(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP, javPreferences);
		}
		//define a default ordering for all other types
	}
	


}
