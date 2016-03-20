package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

public class TheMovieDatabaseParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {
	
	//include adult results when searching
	private boolean includeAdult = true;
	//change this to false to get non adult movies in the search results
	//that might be useful as a future preference value
	//includeAdult also must be true for adult results to be returned
	private boolean onlyReturnAdultResults = true; 
	//This is XBMC/Kodi's key - hopefully this is OK to use as I'm scraping XBMC data...
	private final String tmdbKey = "f7f51775877e0bb6703520952b3c7840";
	private final String movieImagePathPrefix = "https://image.tmdb.org/t/p/original";
	private final String movieImageThumbnailPathPrefix = "https://image.tmdb.org/t/p/w130";
	private final String movieImageFanartThumbnailPathPrefix = "https://image.tmdb.org/t/p/w300";
	private final String movieImagePosterThumbnailPathPrefix = "https://image.tmdb.org/t/p/w185";
	private JSONObject movieJSON;
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
		return groupNames;
	}
	
	
	private JSONObject getMovieJSON()
	{
		if(movieJSON == null && document != null)
		{
			try {
				movieJSON = getJSONObjectFromURL(document.location());
				return movieJSON;
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
		return movieJSON;
	}
	
	@Override
	public Title scrapeTitle() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				String titleString = pageJSON.getString("title");
				if(titleString != null)
					return new Title(titleString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				String originalTitleString = pageJSON.getString("original_title");
				if(originalTitleString != null)
					return new OriginalTitle(originalTitleString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				JSONObject collectionJSON = pageJSON.getJSONObject("belongs_to_collection");
				if(collectionJSON != null)
				{
					String setName = collectionJSON.getString("name");
					if(setName != null)
						return new Set(setName);
				}
			} catch (JSONException e) {
				//e.printStackTrace();
			}
		}
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				double originalTitleString = pageJSON.getDouble("vote_average");
				return new Rating(10.0,new Double(originalTitleString).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				String releaseDate = pageJSON.getString("release_date");
				if(releaseDate != null && releaseDate.length() > 4)
					return new ReleaseDate(releaseDate);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				int idString = pageJSON.getInt("vote_count");
				if(idString >= 0)
					return new Votes(new Integer(idString).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				String overview = pageJSON.getString("overview");
				if(overview != null)
					return new Plot(overview);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				String taglineString = pageJSON.getString("tagline");
				if(taglineString != null)
					return new Tagline(taglineString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				int runtimeString = pageJSON.getInt("runtime");
				if(runtimeString > 0)
					return new Runtime(new Integer(runtimeString).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return Runtime.BLANK_RUNTIME;
	}
	
	private Thumb[] scrapePostersAndFanart(String arrayName, String previewPrefix)
	{
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				JSONObject imageJSON = pageJSON.getJSONObject("images");
				JSONArray posterArrays = imageJSON.getJSONArray(arrayName);
				if(posterArrays != null)
				{
					Thumb [] thumbArray = new Thumb[posterArrays.length()];
					for(int i = 0; i < posterArrays.length(); i++)
					{
						JSONObject posterOrBackdropObject = posterArrays.getJSONObject(i);
						if(posterOrBackdropObject != null)
						{
							String filePath = posterOrBackdropObject.getString("file_path");
							Thumb thumbToAdd = new Thumb(movieImagePathPrefix + filePath);
							thumbToAdd.setPreviewURL(new URL(previewPrefix + filePath));
							thumbArray[i] = thumbToAdd;
						}
					}
					return thumbArray;
				}
			} catch (JSONException | MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return new Thumb[0];
	}
	
	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart("posters", movieImagePosterThumbnailPathPrefix);
	}


	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart("backdrops", movieImageFanartThumbnailPathPrefix);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		//may need to come back to this later
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				int idString = pageJSON.getInt("id");
				if(idString >= 0)
					return new ID(new Integer(idString).toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		//We want to get genres for sure, but maybe keywords should be <tags>?
		//I'm leaving keywords out for now but may revist them at a later date
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				JSONArray genreJSON = pageJSON.getJSONArray("genres");
				if(genreJSON != null)
				{
					for(int i = 0; i < genreJSON.length(); i++)
					{
						String genreName = genreJSON.getJSONObject(i).getString("name");
						if(genreName != null)
							genreList.add(new Genre(genreName));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
				try {
					JSONObject creditsJSON = pageJSON.getJSONObject("credits");
					if(creditsJSON != null)
					{
						JSONArray castArray = creditsJSON.getJSONArray("cast");
						if(castArray != null)
						{
							for(int i = 0; i < castArray.length(); i++)
							{
								String actorName = castArray.getJSONObject(i).getString("name");
								String actorRole = castArray.getJSONObject(i).getString("character");
								String thumbPath = "";
								try
								{
									thumbPath = castArray.getJSONObject(i).getString("profile_path");
								}
								catch(JSONException e)
								{
									//we don't care if there wasn't a thumbnail path, ignore the error
								}
								if(thumbPath != null && thumbPath.length() > 0)
								{
									try {
										actorList.add(new Actor(actorName, actorRole, new Thumb(movieImagePathPrefix + thumbPath)));
									} catch (MalformedURLException e) {
										actorList.add(new Actor(actorName, actorRole, null));
									}
								}
								else
									actorList.add(new Actor(actorName, actorRole, null));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
				try {
					JSONObject creditsJSON = pageJSON.getJSONObject("credits");
					if(creditsJSON != null)
					{
						JSONArray crewArray = creditsJSON.getJSONArray("crew");
						if(crewArray != null)
						{
							for(int i = 0; i < crewArray.length(); i++)
							{
								String personName = crewArray.getJSONObject(i).getString("name");
								String job = crewArray.getJSONObject(i).getString("job");
								if(job != null && job.equals("Director"))
									directorList.add(new Director(personName, null));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		JSONObject pageJSON = getMovieJSON();
		if(pageJSON != null)
		{
			try {
				JSONArray productionCompaniesJSON = pageJSON.getJSONArray("production_companies");
				if(productionCompaniesJSON != null)
				{
					for(int i = 0; i < productionCompaniesJSON.length(); i++)
					{
						//Just return the first studio for now if we find multiple studios
						String studioName = productionCompaniesJSON.getJSONObject(i).getString("name");
						if(studioName != null)
							return new Studio(studioName);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		URLCodec codec = new URLCodec();
			try {
				String movieName = getMovieNameFromFileWithYear(file);
				String year = getYearFromFileWithYear(file);
				String fileNameURLEncoded = codec.encode(movieName);
				String includeAdultParameter = "";
				if(includeAdult)
					includeAdultParameter = "&include_adult=true";
				if(year != null && year.length() == 4)
				{
					return "http://api.themoviedb.org/3/search/movie?api_key=" + tmdbKey + includeAdultParameter + "&query=" + fileNameURLEncoded + "&year="+year;
				}
				else return "http://api.themoviedb.org/3/search/movie?api_key=" + tmdbKey + includeAdultParameter + "&query=" + fileNameURLEncoded;
			} catch (EncoderException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		if(searchString == null)
			return new SearchResult[0];
		try {
			JSONObject searchResultPageJSON = getJSONObjectFromURL(searchString);
			JSONArray resultsArray = searchResultPageJSON.getJSONArray("results");
			ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
			for(int i = 0; i < resultsArray.length(); i++)
			{
				JSONObject currentSearchResult = resultsArray.getJSONObject(i);
				int movieID = currentSearchResult.getInt("id");
				String movieTitle = currentSearchResult.getString("title");
				String posterPath = movieImageThumbnailPathPrefix + currentSearchResult.get("poster_path");
				boolean isAdultMovie = currentSearchResult.getBoolean("adult");
				if(isAdultMovie || !onlyReturnAdultResults)
				{
					SearchResult searchResultToAdd;
					if(posterPath != null && posterPath.length() > 0)
						searchResultToAdd = new SearchResult(getAPIURLPathFromMovieID(movieID), movieTitle, new Thumb(posterPath));
					else
						searchResultToAdd = new SearchResult(getAPIURLPathFromMovieID(movieID), movieTitle);
					searchResultToAdd.setJSONSearchResult(true);
					searchResults.add(searchResultToAdd);
				}
				
			}
			return searchResults.toArray(new SearchResult[searchResults.size()]);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new SearchResult[0];
	}
	
	private String getAPIURLPathFromMovieID(int movieID){
		return "http://api.themoviedb.org/3/movie/" + movieID + 
				"?api_key=" + tmdbKey + "&append_to_response=images,credits,keywords";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new TheMovieDatabaseParsingProfile();
	}

	@Override
	public String getParserName() {
		return "The Movie Database (TMDb)";
	}

}
