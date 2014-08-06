package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class JavZooParsingProfile extends SiteParsingProfile {

	private static final String siteLanguageToScrape = "en";

	public JavZooParsingProfile(Document doc) {
		super(doc);
	}

	public JavZooParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div div.container div.row-fluid h3").first();
		if(titleElement != null)
		{
			//remove the ID number off beginning of the title, if it exists (and it usually always does on JavLibrary)
				String titleElementText = titleElement.text().trim();
				titleElementText = titleElementText.substring(StringUtils.indexOf(titleElementText," ")).trim();
				//sometimes this still leaves "- " at the start of the title, so we'll want to get rid of that too
				if(titleElementText.startsWith("- "))
				{
					titleElementText = titleElementText.replaceFirst(Pattern.quote("- "), "");
				}
				return new Title(titleElementText);
		}
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		//JavZoo doesn't have the original title when scraping in english
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return new SortTitle("");
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("div.span3.info p:contains(Series:) ~ p a").first();
		if(setElement != null)
		{
			return new Set(setElement.text().trim());
		}
		else return new Set("");
	}

	@Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
		return new Rating(0, "0");
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = document.select("div.span3.info p:contains(Release Date:)").first();
		if(yearElement != null)
		{
			String yearText = yearElement.text().trim();
			try{
				yearText = yearText.substring(14,18);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				//the site didn't have a release date, even though it had a row where it should be, so return an empty year
				return new Year("");
			}
			return new Year(yearText);
		}
		else return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on JavZoo
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		//This type of info doesn't exist on JavZoo
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		//This type of info doesn't exist on JavZoo
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
		//This type of info doesn't exist on JavZoo
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		//This type of info doesn't exist on JavZoo
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("div.span3.info p:contains(Length:)").first();
		if(runtimeElement != null)
		{
			String lengthText = runtimeElement.text().trim();
			lengthText = lengthText.replaceFirst(Pattern.quote("Length: "), "");
			lengthText = lengthText.replaceFirst(Pattern.quote("min"), "");
			if(lengthText.length() > 0)
			{
				return new Runtime(lengthText);
			}
		}
		return new Runtime("");	
	}

	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart(true);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}
	
	private Thumb[] scrapePostersAndFanart(boolean doCrop) {
		Element posterElement = document
				.select("a.bigImage img")
				.first();
		Thumb[] posterThumbs = new Thumb[1];
		if(posterElement != null)
		{
			String posterLink = posterElement.attr("src").trim();
			try{
				if (doCrop)
					//posterThumbs[0] = new Thumb(posterLink, 52.7, 0, 0, 0);
					posterThumbs[0] = new Thumb(posterLink, true);
				else
					posterThumbs[0] = new Thumb(posterLink);
				return posterThumbs;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new Thumb[0];
			}
		}
		else return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select("div.span3.info p:contains(ID:)").first();
		if(idElement != null)
		{
			String idText = idElement.text().trim();
			idText = idText.replaceFirst(Pattern.quote("ID: "), "");
			return new ID(idText);
		}
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document.select(".genre");
		if(genreElements != null)
		{
			ArrayList<Genre> genreList = new ArrayList<Genre>(genreElements.size());
			for(Element currentGenre: genreElements)
			{
				genreList.add(new Genre(currentGenre.text().trim()));
			}
			return genreList;
		}
		return new ArrayList<Genre>();
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("div.row-fluid.star-box li a img");
		if(actorElements != null)
		{
			ArrayList<Actor> actorList = new ArrayList<Actor>(actorElements.size());
			for(Element currentActor : actorElements)
			{
				String actorName = currentActor.attr("title").trim();
				String actorThumbURL = currentActor.attr("src").trim();
				//we want the full resolution thumbnail, so replace the "medium" from the URL to get it
				actorThumbURL = actorThumbURL.replaceFirst(Pattern.quote("/medium/"), "/");
				try {
					//we can add the actor with their thumbnail so long as we aren't using a placeholder image
					if(!actorThumbURL.contains("nowprinting.gif"))
					{
						actorList.add(new Actor(actorName,"",new Thumb(actorThumbURL)));
					}
					else //otherwise add the actor without an image
					{
						actorList.add(new Actor(actorName,"",null));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return actorList;
		}
		return new ArrayList<Actor>();
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		Element directorElement = document.select("div.span3.info p:contains(Director:)").first();
		if(directorElement != null)
		{
			ArrayList<Director> directorList = new ArrayList<Director>(1);
			String directorNameText = directorElement.text().trim();
			directorNameText = directorNameText.replaceFirst(Pattern.quote("Director: "), "");
			directorList.add(new Director(directorNameText, null));
			return directorList;
		}
		else return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("div.span3.info p:contains(Studio:) ~ p a").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text().trim();
			studioText = studioText.replaceFirst(Pattern.quote("Studio: "), "");
			return new Studio(studioText);
		}
		else return new Studio("");
	}

	@Override
	public String createSearchString(File file) {
		String fileNameNoExtension = findIDTagFromFile(file);
		
		//return fileNameNoExtension;
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			String searchTerm = "http://www.javzoo.com/" + siteLanguageToScrape  + "/search/" + fileNameURLEncoded;
			
			return searchTerm;
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		ArrayList<SearchResult> linksList = new ArrayList<SearchResult>();
		try{
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
			{
				Elements divVideoLinksElements = doc.select("div.item:has(a[href*=/movie/])");
				String favoredSearchResultString = null;
				for(Element currentDivVideoLink : divVideoLinksElements)
				{
					Element videoLinksElements = currentDivVideoLink.select("a[href*=/movie/]").first();
					String idFromSearchResult = currentDivVideoLink.select("span").first().text();
					String currentLink = videoLinksElements.attr("href");
					if(currentLink.length() > 1)
					{
						//maybe we can improve search accuracy by putting our suspected best match at the front of the array
						//we do this by examining the ID from the search result and seeing if it was in our initial search string
						if(searchString.contains(idFromSearchResult) || searchString.contains(idFromSearchResult.replaceAll(Pattern.quote("-"),"")))
						{
							favoredSearchResultString = currentLink;
						}
						linksList.add(new SearchResult(currentLink));

					}
				}
				//if we had a favoredSearchResult, remove it from the list and add it back to the front of the list
				if(favoredSearchResultString != null)
				{
					linksList.remove(favoredSearchResultString);
					linksList.add(0, new SearchResult(favoredSearchResultString));
				}
				return linksList.toArray(new SearchResult[linksList.size()]);
			}
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		//no extra fanart is supported on this site, for now
		return new Thumb[0];
	}

}
