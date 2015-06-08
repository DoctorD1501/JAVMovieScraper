package moviescraper.doctord.Amalgamation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

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
import moviescraper.doctord.dataitem.ReleaseDate;
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
import moviescraper.doctord.model.Movie;


/**
 * Collection of all the {@link Movie} objects for a given file that have been scraped along with the 
 * ranked preferences of what sites we prefer to use data from when amalgamating
 *
 */
public class MovieScrapeResultGroup {
	
	List<Movie> scrapedMovieObjectsForFile;
	//This preference applies to all data items in a given movie. If we want a custom ordering per item, we look at the DataItemSourceAmalgationPreference within the data item itself
	//DataItemSourceAmalgamationPreference amalgamationPreferenceOrderForEntireMovieGroup; 
	ScraperGroupAmalgamationPreference amalgamationPreferenceOrderForEntireMovieGroup;
	
	/**
	 * Constructor
	 * @param scrapedMovieObjectsForFile - all the movies you wish to amalgamate
	 * @param amalgamationPreferenceOrder - the preference of which field you prefer. Items earlier in the list have a higher preference to being picked when amalgamating,
	 */
	public MovieScrapeResultGroup(List<Movie> scrapedMovieObjectsForFile, ScraperGroupAmalgamationPreference amalgamationPreferenceOrder)
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
		if(result == null)
		{
			//call the default constructor
			Constructor<?>[] ctors = classOfMovieDataItem.getDeclaredConstructors();
			for(Constructor<?> currentConstructor : ctors)
			{
				if(currentConstructor.getParameterTypes().length == 0)
				{
					//We are the default zero param constructor
					return (MovieDataItem)currentConstructor.newInstance();
				}
			}
			throw new NoSuchMethodException("Didn't find default constructor of class = " + classOfMovieDataItem);
		}
			
		System.out.println("Amalgamated " + classOfMovieDataItem.toString().replace("class moviescraper.doctord.dataitem.", "") + " is " + result);
		return result;
	}
	

	
	@SuppressWarnings("unchecked")
	public Movie amalgamateMovie()
	{
		System.out.println("Amalgamating a movie between " + scrapedMovieObjectsForFile.size() + " Movie objects with preference order = " + amalgamationPreferenceOrderForEntireMovieGroup.toString());
		try {
			callAmalgamateActorOnAllTuples(scrapedMovieObjectsForFile);
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
			ReleaseDate releaseDate = (ReleaseDate) getPreferredMovieDataItemAsMovieDataItem(ReleaseDate.class);
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
			
			Movie amalgamatedMovie = new Movie(actors, directors, fanart,
					extraFanart, genres, id, mpaa, originalTitle, outline,
					plot, posters, rating, releaseDate, runtime, set, sortTitle, studio,
					tagline, title, top250, trailer, votes, year);
			//The all titles at this point is just the file name, which is the same for all so we can just use the first one
			if(scrapedMovieObjectsForFile != null  && scrapedMovieObjectsForFile.size() > 0){
				amalgamatedMovie.setFileName(scrapedMovieObjectsForFile.get(0).getFileName());
			}
			
			return amalgamatedMovie;
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
		}
			return null;
	}
	
	/**
	 * Helper method to call amalgamateActor on all possible tuples (where order matters) of our scraped movie list
	 * @param allScrapedMovies - the scraped movie list
	 */
	private void callAmalgamateActorOnAllTuples(List<Movie> allScrapedMovies)
	{
		if(allScrapedMovies != null && allScrapedMovies.size() >= 2)
		{
			for(int i = 0; i < allScrapedMovies.size(); i++){
				for(int j = i + 1; j < allScrapedMovies.size(); j++)
				{
					Movie tupleValue1 = allScrapedMovies.get(i);
					Movie tupleValue2 = allScrapedMovies.get(j);
					amalgamateActor(tupleValue1, tupleValue2);
					amalgamateActor(tupleValue2, tupleValue1);
				}
			}
		}
	}
	
	/**
	 * try to fill in any holes in thumbnails from the sourceMovie by looking through movieToGetExtraInfoFrom and see if it has them
	 * @param sourceMovie - movie that has missing actor images
	 * @param movieToGetExtraInfoFrom - other movie to try to get the missing actor images from
	 * @return
	 */
	private ArrayList<Actor> amalgamateActor(Movie sourceMovie, Movie movieToGetExtraInfoFrom)
	{
		ArrayList<Actor> amalgamatedActorList = new ArrayList<Actor>();
		boolean changeMade = false;
		if(sourceMovie.getActors() != null && movieToGetExtraInfoFrom.getActors() != null)
		{
			for(Actor currentActor : sourceMovie.getActors())
			{
				if(currentActor.getThumb() == null || currentActor.getThumb().getThumbURL().getPath().length() < 1)
				{
					//Found an actor with no thumbnail in sourceMovie
					for(Actor extraMovieActor: movieToGetExtraInfoFrom.getActors())
					{
						//scan through other movie and find actor with same name as the one we are currently on
						if(currentActor.getName().equals(extraMovieActor.getName()) && (extraMovieActor.getThumb() != null) && extraMovieActor.getThumb().getThumbURL().getPath().length() > 1)
						{
							currentActor.setThumb(extraMovieActor.getThumb());
							changeMade = true;
						}
					}
				}
				amalgamatedActorList.add(currentActor);
			}
		}
		if(changeMade)
		{
			return amalgamatedActorList;
		}
		else return sourceMovie.getActors(); // we didn't find any changes needed so just return the source movie's actor list
	}
	
	
	private Object[] getPreferredMovieDataItem(@SuppressWarnings("rawtypes") Class classOfMovieDataItem)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Object[] preferredValueOrder = new Object[amalgamationPreferenceOrderForEntireMovieGroup
				.getOverallAmalgamationPreference()
				.getAmalgamationPreferenceOrder().size()];
		boolean fieldIsArray = false;
		boolean fieldIsArrayList = false;
		boolean fieldIsMovieDataItem = false;
		// For each movie, find the field that matches the class passed in
		// for that Field, put it in the index matching the preference order
		for (Movie currentMovie : scrapedMovieObjectsForFile) {
			for (Field field : currentMovie.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object currentFieldValue = field.get(currentMovie);
				if (currentFieldValue != null) {

					// Case for MovieDataItem
					if (currentFieldValue.getClass().equals(
							classOfMovieDataItem)) {

						Object item = (MovieDataItem) currentFieldValue;

						DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup
								.getAmalgamationPreference(field);

						for (int i = 0; i < amalgamationPrefToUse
								.getAmalgamationPreferenceOrder().size(); i++) {
							if (((MovieDataItem) item)
									.getDataItemSource()
									.toString()
									.equals(amalgamationPrefToUse
											.getAmalgamationPreferenceOrder()
											.get(i).toString()) && ((MovieDataItem) item).isStringValueEmpty()) {
								preferredValueOrder[i] = item;
								fieldIsMovieDataItem = true;
							}
						}

					}
					// Case for ArrayList
					else if (currentFieldValue.getClass().equals(
							ArrayList.class)) {

						ParameterizedType paramType = (ParameterizedType) field
								.getGenericType();
						Class<?> arrayListClass = (Class<?>) paramType
								.getActualTypeArguments()[0];
						@SuppressWarnings("unchecked")
						ArrayList<Object> arrayList = (ArrayList<Object>) currentFieldValue;

						if (arrayListClass.equals(classOfMovieDataItem)) {

							// The data item itself has a preference it wants to
							// amalgamate by, so we will use it instead of the
							// Movie's one
							DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup
									.getAmalgamationPreference(field);

							for (int i = 0; i < amalgamationPrefToUse
									.getAmalgamationPreferenceOrder().size(); i++) {
								if (arrayList.size() > 0) {
									MovieDataItem firstItem = (MovieDataItem) arrayList
											.get(0);
									if (firstItem
											.getDataItemSource()
											.toString()
											.equals(amalgamationPrefToUse
													.getAmalgamationPreferenceOrder()
													.get(i).toString())) {
										preferredValueOrder[i] = arrayList;
										fieldIsArrayList = true;
									}
								}
							}
						}

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
					@SuppressWarnings("rawtypes")
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
		ArrayList<Thumb[]> preferredValueOrder = new ArrayList<Thumb[]>(amalgamationPreferenceOrderForEntireMovieGroup.getOverallAmalgamationPreference().getAmalgamationPreferenceOrder().size());
		for(int i = 0; i < amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreference(null).getAmalgamationPreferenceOrder().size(); i++)
		{
			preferredValueOrder.add(null);
		}
		

		for(Movie currentMovie : scrapedMovieObjectsForFile)
		{
			Field fieldByName = currentMovie.getClass().getDeclaredField(fieldName);
			fieldByName.setAccessible(true);
			Thumb[] value = (Thumb [])fieldByName.get(currentMovie);
			if(value != null)
			{
				DataItemSourceAmalgamationPreference amalgamationPrefToUse = amalgamationPreferenceOrderForEntireMovieGroup.getAmalgamationPreference(fieldByName);
				for (int i = 0; i < amalgamationPrefToUse.getAmalgamationPreferenceOrder().size(); i++)
				{
					if(value.length > 0 && value[0].getDataItemSource().toString().equals(amalgamationPrefToUse.getAmalgamationPreferenceOrder().get(i).toString()))
					{
						preferredValueOrder.set(i, value);
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
