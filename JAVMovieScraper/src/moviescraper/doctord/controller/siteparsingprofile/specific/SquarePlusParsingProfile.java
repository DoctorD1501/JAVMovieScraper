package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.LinkedList;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
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

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SquarePlusParsingProfile extends SiteParsingProfile implements SpecificProfile {
	
	private static final SimpleDateFormat squarePlusReleaseDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH); 
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	public SquarePlusParsingProfile(Document document) {
		super(document);
	}

	public SquarePlusParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Title scrapeTitle() {

		Element titleElement = document
				.select("div.product-name.page-title h1")
				.first();
		//remove the ID number off the end of the title, if it exists
		if(titleElement != null)
		{
			String titleElementText = titleElement.text().trim();
			if(titleElementText.contains("("))
			{
				titleElementText = titleElementText.substring(0, StringUtils.lastIndexOf(titleElementText,"("));
			}
			return new Title(titleElementText);
		}
		//this shouldn't really ever happen...
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		//Does not have original japanese title, so don't return anything
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("th.label:containsOwn(Series) ~ td").first();
		if (setElement != null)
			return new Set(setElement.text());
		
		return Set.BLANK_SET;

	}

	@Override
	public Rating scrapeRating() {
		//site doesn't have a rating
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement = document.select("th.label:containsOwn(Release date) ~ td").first();
		if(releaseDateElement != null && releaseDateElement.text().length() > 4)
		{
			String releaseDateText = releaseDateElement.text().trim();
			return new ReleaseDate(releaseDateText, squarePlusReleaseDateFormat);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on ActionJav
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
			return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("th.label:containsOwn(Play Time) ~ td").first();
		if(runtimeElement != null)
			return new Runtime(runtimeElement.text());
		else return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart(true);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}
	
	private Thumb[] scrapePostersAndFanart(boolean doCrop)
	{
		Element boxArtElement = document.select("p.product-image a").first();
		if(boxArtElement != null)
		{
			Thumb poster;
			try {
				poster = new Thumb(boxArtElement.attr("href"), doCrop);
				Thumb[] posters = {poster};
				return posters;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		// It's always XXX content on ActionJav! ;)
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Element idElement = document
				.select("div.page-title h1")
				.first();
		//just get the ID number off the end of the title, if it exists
		if(idElement != null && idElement.text().contains("("))
		{
			String idElementText = idElement.text().trim();
			idElementText = idElementText.substring(StringUtils.lastIndexOf(idElementText,"(")+1, idElementText.length()-1);
			return new ID(idElementText);
		}
		//maybe some titles don't have ID numbers on squareplus or we got some other error
		else return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Element genreElement = document.select("th.label:containsOwn(Genre) ~ td").first();
		if(genreElement != null)
		{
			String [] actorSplitList = genreElement.text().split(",");
			for(String genreToAdd : actorSplitList)
				genreList.add(new Genre(genreToAdd));
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		
		Element featuringElement = document.select("th.label:containsOwn(Featuring) ~ td:not(:containsOwn(Various))").first();
		if(featuringElement != null)
		{
			String [] actorSplitList = featuringElement.text().split(",");
			for(String actorToAdd : actorSplitList)
				actorList.add(new Actor(actorToAdd,"",null));
		}
		
		Element starringElement = document.select("th.label:containsOwn(Starring) ~ td:not(:containsOwn(Various))").first();
		if(starringElement != null)
		{
			String [] actorSplitList = starringElement.text().split(",");
			for(String actorToAdd : actorSplitList)
				actorList.add(new Actor(actorToAdd,"",null));
		}
		return actorList;	
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//SquarePlus doesn't have director information, so just return an empty list
		return new ArrayList<>();
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("th.label:containsOwn(Label) ~ td:not(:containsOwn(Other))").first();
		if(studioElement != null)
			return new Studio(studioElement.text());
		
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String searchId = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return "http://www.squareplus.co.jp/catalogsearch/result/?q=" + searchId;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		if (searchString == null)
			return new SearchResult[0];
				
		Document doc = Jsoup.connect(searchString).timeout(CONNECTION_TIMEOUT_VALUE).get();
		Elements foundMovies = doc.select("ul.products-grid>li");
		String searchId = searchString.replaceAll(".*\\?q=(.*)$", "$1").replace("-", "").toLowerCase();
		LinkedList<SearchResult> searchList = new LinkedList<>();
		
		for(Element movie: foundMovies){
			String urlPath = movie.select("a").first().attr("href");
			String thumb = movie.select("img").first().attr("src");
			String label = movie.select(".product-name,.actresslist").text();
			SearchResult searchResult = new SearchResult(urlPath, label, new Thumb(thumb));
			
			if (urlPath.endsWith("/" + searchId + ".html"))
				searchList.addFirst(searchResult);
			else
				searchList.addLast(searchResult);
		}
		
		// if both DVD and Blue-Ray gets listed, pick the correct one
		if (searchList.size() == 2)
			if (searchList.get(0).getUrlPath().endsWith("/"+searchId+".html"))
				if (searchList.get(1).getUrlPath().endsWith("/9"+searchId+".html"))
					searchList.remove(1);
		
		return searchList.toArray(new SearchResult[searchList.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		Elements extraFanartElements = document.select("div.more-views ul li a");
		if(extraFanartElements != null)
		{
			ArrayList<Thumb> extrafanartThumbList = new ArrayList<>(extraFanartElements.size());
			for(Element extraFanartElement : extraFanartElements)
			{
				Thumb thumbToAdd;
				try {
					thumbToAdd = new Thumb(extraFanartElement.attr("href"));
					extrafanartThumbList.add(thumbToAdd);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			return extrafanartThumbList.toArray(new Thumb[extrafanartThumbList.size()]);
		}
		return new Thumb[0];
	}
	
	@Override
	public String toString(){
		return "SquarePlus";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new SquarePlusParsingProfile();
	}

	@Override
	public String getParserName() {
		return "SquarePlus";
	}

}
