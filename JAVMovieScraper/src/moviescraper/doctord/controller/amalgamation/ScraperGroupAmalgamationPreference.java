package moviescraper.doctord.controller.amalgamation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.model.Movie;

/**
 * A ScraperGroupAmalgamtionPreference is the preferred order of scrapers to use when amalgamating data
 * plus an optional list of fields that have their own ordering of scrapers to use just for that field
 *
 */
public class ScraperGroupAmalgamationPreference {
	
	ScraperGroupName scraperGroupName;
	DataItemSourceAmalgamationPreference overallOrdering;
	Map<String, DataItemSourceAmalgamationPreference> customAmalgamationOrderPerField;
	

	
	public ScraperGroupAmalgamationPreference(
			ScraperGroupName scraperGroupName,
			DataItemSourceAmalgamationPreference overallOrdering) {
		this.scraperGroupName = scraperGroupName;
		this.overallOrdering = overallOrdering;
	}
	
	/**
	 * Get the specific ordering for this field, or the overall ordering if there wasn't a specific ordering found
	 * @param field
	 * @return
	 */
	public DataItemSourceAmalgamationPreference getAmalgamationPreference(Field field)
	{
		if(field != null && customAmalgamationOrderPerField != null && customAmalgamationOrderPerField.containsKey(field.getName()))
		{
			return customAmalgamationOrderPerField.get(field.getName());
		}
		else {
			return overallOrdering;
		}
	}
	
	/**
	 * Get the specific ordering for this field, or null if there isn't one set
	 * @param field - field to look up the ordering on
	 */
	public DataItemSourceAmalgamationPreference getSpecificAmalgamationPreference(Field field)
	{
		if(field != null && customAmalgamationOrderPerField != null && customAmalgamationOrderPerField.containsKey(field.getName()))
		{
			return customAmalgamationOrderPerField.get(field.getName());
		}
		return null;
	}
	
	public DataItemSourceAmalgamationPreference getOverallAmalgamationPreference()
	{
		return overallOrdering;
	}
	
	
	public void setCustomOrderingForField(Field field, DataItemSourceAmalgamationPreference newValue)
	{
		if (customAmalgamationOrderPerField == null) {
			customAmalgamationOrderPerField = new Hashtable<String, DataItemSourceAmalgamationPreference>(
					Movie.class.getDeclaredFields().length);
		}
		customAmalgamationOrderPerField.put(field.getName(), newValue);
	}
	
	public void setCustomOrderingForField(String fieldName, DataItemSourceAmalgamationPreference newValue) throws NoSuchFieldException, SecurityException
	{
		setCustomOrderingForField(Movie.class.getDeclaredField(fieldName), newValue);
	}
	
	public void removeCustomOrderingForField(Field field)
	{
		customAmalgamationOrderPerField.remove(field.getName());
	}
	
	public static List<Field> getMoviefieldNames()
	{
		LinkedList<Field> fieldNames = new LinkedList<Field>();
		Movie currentMovie = Movie.getEmptyMovie();
		String[] disallowedFieldNames = {"readTimeout","connectionTimeout","preferredFanartToWriteToDisk","allTitles", "fileName"};
		ArrayList<String> disallowedFieldNamesArrayList = new ArrayList<String>(Arrays.asList(disallowedFieldNames));
		for (Field field : currentMovie.getClass().getDeclaredFields()) {
			String fieldName = field.getName();
			if(!disallowedFieldNamesArrayList.contains(fieldName))
			{
				
				fieldNames.add(field);
			}
		}
		
		return fieldNames;
	}
	


	
	@Override
	public String toString() {
		return "ScraperGroupAmalgamationPreference [scraperGroupName = "
				+ scraperGroupName.toString() + " overallOrdering = "
				+ overallOrdering.toString() + " customAmalgamationPerField "
				+ customAmalgamationOrderPerField + "]";
	}

}
