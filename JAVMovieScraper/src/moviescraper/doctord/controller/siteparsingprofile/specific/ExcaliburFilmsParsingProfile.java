package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
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

public class ExcaliburFilmsParsingProfile extends SiteParsingProfile implements SpecificProfile {
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
		return groupNames;
	}
	
	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("title").first();
		if(titleElement != null)
		{
			String titleText = titleElement.text();
			titleText = titleText.replaceFirst("Adult DVD", "");
			titleText = titleText.replaceFirst("Blu-Ray", "");
			return new Title(titleText);
		}
		return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return new OriginalTitle(scrapeTitle().getTitle());
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		//Excalibur doesn't have set info
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		//Excalibur doesn't have rating info
		return Rating.BLANK_RATING;
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement = document.select("font:containsOwn(Released:) + font").first();
		if(releaseDateElement != null)
		{
			ReleaseDate releaseDate = new ReleaseDate(releaseDateElement.text(),new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH));
			return releaseDate;
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public Top250 scrapeTop250() {
		//Excalibur doesn't have this info
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		//Excalibur doesn't have this info
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		//Excalibur doesn't have this info
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		Element plotElement = document.select("a:has(font b:containsOwn(Description:)) + font").first();
		if(plotElement != null)
		{
			String plotElementText = plotElement.text().trim();
			//They like to include their plot descriptions within quotes, so we can remove those quotes
			if(plotElementText.startsWith("\"") && plotElementText.endsWith("\"") && plotElementText.length() > 2)
			{
				plotElementText = plotElementText.substring(1, plotElementText.length() -1);
			}
			return new Plot(plotElementText);
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		//Excalibur doesn't have this information
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("font:containsOwn(Run Time:) + font").first();
		if(runtimeElement != null)
		{
			String runtimeText = runtimeElement.text().replace(" min.", "");
			return new Runtime(runtimeText);
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		String movieID = scrapeID().getId();
		String thumbPath = getPosterPathFromIDString(movieID);
		if(thumbPath == null)
			return new Thumb[0];
		try {
			Thumb posterThumb = new Thumb(thumbPath);
			Thumb[] thumbsToReturn = {posterThumb};
			return thumbsToReturn;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new Thumb[0];
		}
	}
	
	private String getPosterPathFromIDString(String movieID)
	{
		if(movieID == null)
			return null;
		return "http://images.excaliburfilms.com/DVD/reviews/imagesBB020609/largemoviepic/dvd_" + movieID + ".jpg";
	}
	
	private String getPosterPreviewPathFromIDString(String movieID)
	{
		if(movieID == null)
			return null;
		return "http://images.excaliburfilms.com/dvd/dvdicon2/dvd_" + movieID + ".jpg";
	}
	
	

	@Override
	public Thumb[] scrapeFanart() {
		//No Fanart on this site
		return new Thumb[0];
	}

	/**
	 * We return the back cover as the extrafanart for Excalibur Films
	 */
	@Override
	public Thumb[] scrapeExtraFanart() {
		String movieID = scrapeID().getId();
		String thumbPath = "http://images.excaliburfilms.com/DVD/reviews/imagesBB020609/largemoviepic/dvd_" + movieID + "-b.jpg";
		try {
			Thumb posterThumb = new Thumb(thumbPath);
			Thumb[] thumbsToReturn = {posterThumb};
			return thumbsToReturn;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new Thumb[0];
		}
	}

	@Override
	public MPAARating scrapeMPAA() {
		Element mpaaRatingElement = document.select("font:containsOwn(Rated:) + font a").first();
		if(mpaaRatingElement != null)
		{
			String mpaaRatingText = mpaaRatingElement.text();
			return new MPAARating(mpaaRatingText);
		}
		return MPAARating.BLANK_RATING;
	}

	@Override
	public ID scrapeID() {
		String id = getIDStringFromDocumentLocation(document);
		if(id != null)
		{
			return new ID(id);
		}
		return ID.BLANK_ID;
	}
	
	private String getIDStringFromDocumentLocation(Document doc)
	{
		if (doc != null) {
			String id = doc.location();
			if (id.contains("/") && id.contains("_") && id.contains(".htm")) {
				id = id.substring(id.lastIndexOf('/') + 1, Math.min(id.indexOf('_'), id.length()));
				return id;
			}
		}
		return null;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Element genreElement = document.select("font:containsOwn(Fetish:) + a").first();
		if(genreElement != null)
		{
			String genreText = genreElement.text();
			if(genreText.length() > 0 && !genreText.equals("BluRay"))
			{
				genreList.add(new Genre(genreText));
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Element firstActorList = document.select("font:containsOwn(Starring:) + font").first();
		Elements actorListElements = firstActorList.select("a");
		for(Element currentActor : actorListElements)
		{
			String actorName = currentActor.text();
			String pageName = currentActor.attr("href");
			Thumb actorThumb = getThumbForPersonPageUrl(pageName);
			if(actorThumb != null)
			{
				Actor currentActorToAdd = new Actor(actorName, "", actorThumb);
				actorList.add(currentActorToAdd);
			}
			else
			{
				Actor currentActorToAdd = new Actor(actorName, "", null);
				if(actorName.trim().length() > 0)
					actorList.add(currentActorToAdd);
			}
		}
		//get no image actors
		String firstActorListText = firstActorList.ownText();
		if(firstActorListText.length() > 0)
		{
			String currentActorTextSplitByComma[] = firstActorListText.trim().split(",");
			for(String currentNoThumbActor: currentActorTextSplitByComma)
			{
				String actorName = currentNoThumbActor.trim();
				//last actor in the list has a period since the list is in sentence form, so we want to get rid of that
				if(actorName.endsWith("."))
					actorName = actorName.substring(0, actorName.length()-1);
				//we already have some of the actors if they were added in the thumb version, so check before adding them again
				boolean hadThisActorAlready = false;
				for(Actor existingActor: actorList)
				{
					if(existingActor.getName().equals(actorName))
						hadThisActorAlready = true;
				}
				if(!hadThisActorAlready && actorName.trim().length() > 0)
					actorList.add(new Actor(actorName, "", null));
			}
		}
		return actorList;
	}
	
	private Thumb getThumbForPersonPageUrl(String personPageUrl) {
		String actorFromPageName = personPageUrl.substring(personPageUrl.lastIndexOf("/"), personPageUrl.length())
				.replace(".htm", "");
		String actorThumbURL = "http://Images.ExcaliburFilms.com/pornlist/starpicsAA020309" + actorFromPageName
				+ ".jpg";
		Thumb actorThumb = null;
		try {
			actorThumb = new Thumb(actorThumbURL);
		} catch (MalformedURLException e) {
			return null;
		}
		return actorThumb;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		Element directorElement = document.select("font:containsOwn(Director:) + a").first();
		if(directorElement != null)
		{
			String directorName = directorElement.text();
			String directorPageURL = directorElement.attr("href");
			Thumb directorThumb = null;
			if(directorPageURL != null)
			{
				directorThumb = getThumbForPersonPageUrl(directorPageURL);
			}
			Director directorToAdd = new Director(directorName, directorThumb);
			directorList.add(directorToAdd);
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("font:containsOwn(By:) + a").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text();
			return new Studio(studioText);
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		String fileBaseName;
		if (file.isFile())
			fileBaseName = FilenameUtils.getBaseName(Movie.getUnstackedMovieName(file));
		else
			fileBaseName = file.getName();
		String[] splitBySpace = fileBaseName.split(" ");
		if (splitBySpace.length > 1) {
			// check if last word in filename contains a year like (2012) or [2012]
			// we want to remove this from our search because it freaks out the search on excalibur films and gives no results
			if (splitBySpace[splitBySpace.length - 1].matches("[\\(\\[]\\d{4}[\\)\\]]")) {
				fileBaseName = fileBaseName.replaceFirst("[\\(\\[]\\d{4}[\\)\\]]", "").trim();
			}
		}
		URLCodec codec = new URLCodec();
		try {
			fileBaseName = codec.encode(fileBaseName);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		fileBaseName = "http://www.excaliburfilms.com/search/adultSearch.htm?searchString=" + fileBaseName
				+ "&Case=ExcalMovies&Search=AdultDVDMovies&SearchFor=Title.x";
		return fileBaseName;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		Document doc = Jsoup.connect(searchString).timeout(CONNECTION_TIMEOUT_VALUE).get();
		boolean onSearchResultsPage = doc.location().contains("adultSearch.htm");
		//found the movie without a search results page
		if(doc.location() != null && !onSearchResultsPage)
		{
			String idOfPage = getIDStringFromDocumentLocation(doc);
			String posterPath = getPosterPreviewPathFromIDString(idOfPage);
			String label = doc.select("title").first().text();
			Thumb previewImage = new Thumb(posterPath);
			//SearchResult directResult = new SearchResult(doc.location());
			SearchResult result = null;
			if(posterPath != null)
				 result = new SearchResult(doc.location(), label, previewImage);
			else
				result = new SearchResult(doc.location(), label, null);

			SearchResult[] directResultArray = {result};
			return directResultArray;
		}
		Elements foundMovies = doc.select("table[width=690]:contains(Wish List) tr tbody:has(img)");
		LinkedList<SearchResult> searchList = new LinkedList<SearchResult>();
		
		for(Element movie: foundMovies){
			String urlPath = movie.select("a").first().attr("href");
			String thumb = movie.select("img").first().attr("src");
			String label = movie.select("img").first().attr("alt");
			SearchResult searchResult = new SearchResult(urlPath, label, new Thumb(thumb));
			if(!searchList.contains(searchResult))
				searchList.add(searchResult);
		}
		return searchList.toArray(new SearchResult[searchList.size()]);
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new ExcaliburFilmsParsingProfile();
	}

	@Override
	public String getParserName() {
		return "Excalibur Films";
	}

}
