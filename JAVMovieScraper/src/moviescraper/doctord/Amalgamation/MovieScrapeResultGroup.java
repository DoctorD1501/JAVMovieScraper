package moviescraper.doctord.Amalgamation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SiteParsingProfile.specific.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.R18ParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.MovieDataItem;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Thumb;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;


/**
 * Collection of all the {@link Movie} objects for a given file that have been scraped along with the 
 * ranked preferences of what sites we prefer to use data from when amalgamating
 *
 */
public class MovieScrapeResultGroup {
	
	List<Movie> scrapedMovieObjectsForFile;
	//This preference applies to all data items in a given movie. If we want a custom ordering per item, we look at the DataItemSourceAmalgationPreference within the data item itself
	DataItemSourceAmalgamationPreference amalgamationPreferenceOrderForEntireMovieGroup; 
	
	/**
	 * Constructor
	 * @param scrapedMovieObjectsForFile - all the movies you wish to amalgamate
	 * @param amalgamationPreferenceOrder - the preference of which field you prefer. Items earlier in the list have a higher preference to being picked when amalgamating,
	 */
	public MovieScrapeResultGroup(List<Movie> scrapedMovieObjectsForFile, DataItemSourceAmalgamationPreference amalgamationPreferenceOrder)
	{
		this.scrapedMovieObjectsForFile = scrapedMovieObjectsForFile;
		this.amalgamationPreferenceOrderForEntireMovieGroup = amalgamationPreferenceOrder;
	}
	
	private ArrayList<?> getPreferredMovieDataItemAsArrayList(Class<?> classOfMovieDataItem) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		ArrayList<?> arrayList;
		arrayList = getPreferredMovieDataItem(classOfMovieDataItem).length > 0 ? (ArrayList<?>) getPreferredMovieDataItem(classOfMovieDataItem)[0] : new ArrayList<>();
		System.out.println("Amalgamated " + classOfMovieDataItem.toString().replace("class moviescraper.doctord.dataitem.", "") + " is " + arrayList);
		return arrayList;
	}
	
	private MovieDataItem getPreferredMovieDataItemAsMovieDataItem(Class<?> classOfMovieDataItem) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		MovieDataItem result = getPreferredMovieDataItem(classOfMovieDataItem).length > 0 ? (MovieDataItem) getPreferredMovieDataItem(classOfMovieDataItem)[0] : null; 
		System.out.println("Amalgamated " + classOfMovieDataItem.toString().replace("class moviescraper.doctord.dataitem.", "") + " is " + result);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Movie amalgamateMovie()
	{
		System.out.println("Amalgamating a movie between " + scrapedMovieObjectsForFile.size() + " Movie objects with preference order = " + amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreferenceOrder());
		try {
			ArrayList<Actor> actors = (ArrayList<Actor>) getPreferredMovieDataItemAsArrayList(Actor.class);
			ArrayList<Director> directors = (ArrayList<Director>) getPreferredMovieDataItemAsArrayList(Director.class);
			ArrayList<Genre> genres = (ArrayList<Genre>) getPreferredMovieDataItemAsArrayList(Genre.class);
			
			Thumb[] fanart = getPreferredArrayMovieDataItem("fanart"); 
			Thumb[] extraFanart = getPreferredArrayMovieDataItem("extraFanart"); 
			Thumb[] posters = getPreferredArrayMovieDataItem("posters"); 
			
			ID id = (ID) getPreferredMovieDataItemAsMovieDataItem(ID.class);
			MPAARating mpaa = (MPAARating) getPreferredMovieDataItemAsMovieDataItem(MPAARating.class);
			OriginalTitle originalTitle = (OriginalTitle) getPreferredMovieDataItemAsMovieDataItem(OriginalTitle.class);
			Outline outline = (Outline) getPreferredMovieDataItemAsMovieDataItem(Outline.class);
			Plot plot = (Plot) getPreferredMovieDataItemAsMovieDataItem(Plot.class);
			Rating rating = (Rating) getPreferredMovieDataItemAsMovieDataItem(Rating.class); 
			Runtime runtime = (Runtime) getPreferredMovieDataItemAsMovieDataItem(Runtime.class); 
			Set set = (Set) getPreferredMovieDataItemAsMovieDataItem(Set.class); 
			SortTitle sortTitle = (SortTitle) getPreferredMovieDataItemAsMovieDataItem(SortTitle.class); 
			Studio studio = (Studio) getPreferredMovieDataItemAsMovieDataItem(Studio.class);
			Tagline tagline = (Tagline) getPreferredMovieDataItemAsMovieDataItem(Tagline.class);
			Title title = (Title) getPreferredMovieDataItemAsMovieDataItem(Title.class);
			Top250 top250 = (Top250) getPreferredMovieDataItemAsMovieDataItem(Top250.class);
			Trailer trailer = (Trailer) getPreferredMovieDataItemAsMovieDataItem(Trailer.class);
			Votes votes = (Votes) getPreferredMovieDataItemAsMovieDataItem(Votes.class);
			Year year = (Year) getPreferredMovieDataItemAsMovieDataItem(Year.class);
			return new Movie(actors, directors, fanart, extraFanart, genres, id, mpaa, originalTitle, outline, plot, posters, rating, runtime, set, sortTitle, studio, tagline, title, top250, trailer, votes, year);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
		}
			return null;
	}
	
	
	private Object[] getPreferredMovieDataItem(Class classOfMovieDataItem) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		Object[] preferredValueOrder = new Object[amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreferenceOrder().size()];
		boolean fieldIsArray = false;
		boolean fieldIsArrayList = false;
		boolean fieldIsMovieDataItem = false;
		//For each movie, find the field that matches the class passed in
		//	for that Field, put it in the index matching the preference order
		for(Movie currentMovie : scrapedMovieObjectsForFile)
		{
			for (Field field : currentMovie.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				String name = field.getName();
				Object value = field.get(currentMovie);
				if(value != null)
				{

					
					//Case for MovieDataItem
					if(value.getClass().equals(classOfMovieDataItem))
					{
						
						//System.out.printf("Field name: %s, Field value: %s%n", name, value);
						Object item = (MovieDataItem) value;
						
						DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup;
						DataItemSourceAmalgamationPreference itemAmalgamationPref = ((MovieDataItem) item).getDataItemAmalgamtionPreference();
						//The data item itself has a preference it wants to amalgamate by, so we will use it instead of the Movie's one
						if(itemAmalgamationPref != null)
						{
							amalgamationPrefToUse = itemAmalgamationPref;
							//reset the array to get the correct size and fill it with null values so that there is room
							if(preferredValueOrder.length != amalgamationPrefToUse.getAmalgamationPreferenceOrder().size())
							{
								preferredValueOrder = new Object[amalgamationPrefToUse.getAmalgamationPreferenceOrder().size()];
							}
						}
						for (int i = 0; i < amalgamationPrefToUse.getAmalgamationPreferenceOrder().size(); i++)
						{
							//System.out.println("arry:" + amalgamationPreferenceOrder.getAmalgamationPreferenceOrder().get(i));
							if(((MovieDataItem) item).getDataItemSource().toString().equals(amalgamationPrefToUse.getAmalgamationPreferenceOrder().get(i).toString()))
							{
								//System.out.println("ds: " + ((MovieDataItem) item).getDataItemSource());
								preferredValueOrder[i] = item;
								fieldIsMovieDataItem = true;
							}
						}
						
						

					}
					//Case for ArrayList
					else if(value.getClass().equals(ArrayList.class))
					{

						ParameterizedType paramType = (ParameterizedType) field.getGenericType();
				    	Class<?> arrayListClass = (Class<?>) paramType.getActualTypeArguments()[0]; 
						//System.out.println("ArrayList of " + arrayListClass + " compared to " + classOfMovieDataItem);
						ArrayList<Object> arrayList = (ArrayList<Object>) value;

				        if(arrayListClass.equals(classOfMovieDataItem))
				        {
				        	
					        //System.out.println("Parameterized type :" + arrayListClass); 
					        //System.out.printf("Field name: %s, Field value: %s%n", name, value);
					        //System.out.println("arr item = " + arrayList);
				        	
				        	//The data item itself has a preference it wants to amalgamate by, so we will use it instead of the Movie's one
							DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup;
							DataItemSourceAmalgamationPreference itemAmalgamationPref = null;
							if(arrayList.size() > 0)
							{
								MovieDataItem firstItem = (MovieDataItem) arrayList.get(0);
								if(firstItem != null){
									itemAmalgamationPref = firstItem.getDataItemAmalgamtionPreference();
								}						
							}
							
							
							if(itemAmalgamationPref != null)
							{
								amalgamationPrefToUse = itemAmalgamationPref;
								//reset the array to get the correct size and fill it with null values so that there is room
								if(preferredValueOrder.length != amalgamationPrefToUse.getAmalgamationPreferenceOrder().size())
								{
									preferredValueOrder = new Object[amalgamationPrefToUse.getAmalgamationPreferenceOrder().size()];
								}
							}
				        	for (int i = 0; i < amalgamationPrefToUse.getAmalgamationPreferenceOrder().size(); i++)
				        	{
				        		if(arrayList.size() > 0)
				        		{
				        			MovieDataItem firstItem = (MovieDataItem) arrayList.get(0);
				        			//System.out.println("arry:" + amalgamationPreferenceOrder.getAmalgamationPreferenceOrder().get(i));
				        			if(firstItem.getDataItemSource().toString().equals(amalgamationPrefToUse.getAmalgamationPreferenceOrder().get(i).toString()))
				        			{
				        				//System.out.println("ds: " + firstItem.getDataItemSource());
				        				preferredValueOrder[i] = arrayList;
				        				fieldIsArrayList = true;
				        			}
				        		}
				        	}
				        }
				      
					}
					//Case for Array
					else if(value.getClass().isArray())
					{
						//need to change isArray to true somehow
						//System.out.println("Array!");
					}
				}
			}

		}
		
		
		
		//MovieDataItem return
		//the first non null item in the list is the highest preferred value
		if(fieldIsMovieDataItem)
		{
			for(int j = 0; j< preferredValueOrder.length; j++)
			{
				if(preferredValueOrder[j] != null)
				{
					MovieDataItem[] returnValue = {(MovieDataItem)preferredValueOrder[j]};
					return returnValue;
				}
			}
		}
		else if(fieldIsArrayList)
		{
			//ArrayList Return
			//the first non null item in the list is the highest preferred value
			for(int i = 0; i< preferredValueOrder.length; i++)
			{
				if(preferredValueOrder[i] != null)
				{
					Object[] returnValue = {(ArrayList)preferredValueOrder[i]};
					return returnValue;
				}
			}
		}
		
		else if(fieldIsArray)
		{
			//do nothing for now - this is handled in another method
			//System.out.println("need to return that array...");
		}


		//nothing found otherwise
		return new Object[0];
	}
	
	
	
	//used for thumb arrays and such
	private Thumb[] getPreferredArrayMovieDataItem(String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ArrayList<Thumb[]> preferredValueOrder = new ArrayList<Thumb[]>(amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreferenceOrder().size());
		for(int i = 0; i < amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreferenceOrder().size(); i++)
		{
			preferredValueOrder.add(null);
		}
		//System.out.println(amalgamationPreferenceOrder.getAmalgamationPreferenceOrder().size());
		//System.out.println("preferredValueOrder size = " + preferredValueOrder.size());
		

		for(Movie currentMovie : scrapedMovieObjectsForFile)
		{
			Field fieldByName = currentMovie.getClass().getDeclaredField(fieldName);
			fieldByName.setAccessible(true);
			Thumb[] value = (Thumb [])fieldByName.get(currentMovie);
			if(value != null)
			{
				DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup;
				//The data item itself has a preference it wants to amalgamate by, so we will use it instead of the Movie's one
				if(value.length > 0 && value[0].getDataItemAmalgamtionPreference() != null)
				{
					amalgamationPrefToUse = value[0].getDataItemAmalgamtionPreference();
					//reset the list to get the correct size and fill it with null values so that there is room
					if(preferredValueOrder.size() != amalgamationPrefToUse.getAmalgamationPreferenceOrder().size())
					{
						preferredValueOrder = new ArrayList<Thumb[]>(amalgamationPrefToUse.getAmalgamationPreferenceOrder().size());
						for(int i = 0; i < amalgamationPrefToUse.getAmalgamationPreferenceOrder().size(); i++)
						{
							preferredValueOrder.add(null);
						}
					}
				}
				for (int i = 0; i < amalgamationPrefToUse.getAmalgamationPreferenceOrder().size(); i++)
				{
					//System.out.println("arry:" + amalgamationPreferenceOrder.getAmalgamationPreferenceOrder().get(i));
					if(value.length > 0 && value[0].getDataItemSource().toString().equals(amalgamationPrefToUse.getAmalgamationPreferenceOrder().get(i).toString()))
					{
						//System.out.println("Adding " + value[0]);
						preferredValueOrder.set(i, value);
						//System.out.println("PreferredValueOrder = " + preferredValueOrder);
					}
				}
			}
		}
		for(int j = 0; j< preferredValueOrder.size(); j++)
		{
			if(preferredValueOrder.get(j) != null)
			{
				Thumb[] returnValue = preferredValueOrder.get(j);
				System.out.println("Amalgamated " + fieldName + " is " + Arrays.toString(returnValue));
				return returnValue;
			}
		}
		//nothing found otherwise
		Thumb[] emptyThumb = new Thumb[0];
		System.out.println("Amalgamated " + fieldName + " is " + Arrays.toString(emptyThumb));
		return emptyThumb;
	}

}
