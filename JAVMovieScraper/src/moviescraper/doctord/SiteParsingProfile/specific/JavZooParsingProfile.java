package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.JapaneseCharacter;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.TranslateString;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
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
import moviescraper.doctord.dataitem.Thumb;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class JavZooParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private static final String siteLanguageToScrape = "en";
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	public JavZooParsingProfile(Document doc) {
		super(doc);
	}
	
	
	
	public JavZooParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div.container h3").first();
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
				
				//sometimes title is not translated to english
				if (document.location().contains("/en/"))
					if (JapaneseCharacter.containsJapaneseLetter(titleElementText))
						return new Title(TranslateString.translateStringJapaneseToEnglish(titleElementText));
				
				
				return new Title(titleElementText);
		}
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		try {
			Element titleElement = document.select("div.container h3").first();
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
					
					//sometimes title is not translated on the english site
					if (JapaneseCharacter.containsJapaneseLetter(titleElementText))
						return new OriginalTitle(titleElementText);
					
					// scrape japanese site for original text
					String japaneseUrl = document.location().replaceFirst(Pattern.quote("/en/"), "/ja/");
					if (japaneseUrl.equals(document.location()))
						return new OriginalTitle(titleElementText);
						
					Document japaneseDoc = Jsoup.connect(japaneseUrl).timeout(CONNECTION_TIMEOUT_VALUE).get();		
					JavZooParsingProfile spp = new JavZooParsingProfile(japaneseDoc);
					return spp.scrapeOriginalTitle();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		Element setElement = document.select("div.container p:contains(Series:) ~ p a").first();
		if(setElement != null)
		{
			return new Set(setElement.text().trim());
		}
		else return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = document.select("div.container p:contains(Release Date:)").first();
		if(yearElement != null)
		{
			String yearText = yearElement.text().trim();
			try{
				yearText = yearText.substring(14,18);
			}
			catch(StringIndexOutOfBoundsException e)
			{
				//the site didn't have a release date, even though it had a row where it should be, so return an empty year
				return Year.BLANK_YEAR;
			}
			return new Year(yearText);
		}
		else return Year.BLANK_YEAR;
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on JavZoo
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		//This type of info doesn't exist on JavZoo
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		//This type of info doesn't exist on JavZoo
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		//This type of info doesn't exist on JavZoo
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		//This type of info doesn't exist on JavZoo
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("div.container p:contains(Length:)").first();
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
		return Runtime.BLANK_RUNTIME;	
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
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select("div.container p:contains(ID:)").first();
		if(idElement != null)
		{
			String idText = idElement.text().trim();
			idText = idText.replaceFirst(Pattern.quote("ID: "), "");
			return new ID(idText);
		}
		else return ID.BLANK_ID;
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
		Elements actorElements = document.select("div#avatar-waterfall a.avatar-box");
		System.out.println(actorElements);
		if(actorElements != null)
		{
			ArrayList<Actor> actorList = new ArrayList<Actor>(actorElements.size());
			for(Element currentActor : actorElements)
			{
				String actorName = currentActor.select("span").first().text().trim();
				String actorThumbURL = currentActor.select("img").first().attr("src");
				//we want the full resolution thumbnail, so replace the "medium" from the URL to get it
				//actorThumbURL = actorThumbURL.replaceFirst(Pattern.quote("/medium/"), "/");
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
		Element directorElement = document.select("div.row.movie p:contains(Director:)").first();
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
		Element studioElement = document.select("div.row.movie p:contains(Studio:) ~ p a").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text().trim();
			studioText = studioText.replaceFirst(Pattern.quote("Studio: "), "");
			return new Studio(studioText);
		}
		else return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		
		//return fileNameNoExtension;
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			String searchTerm = "http://www.javdog.com/" + siteLanguageToScrape  + "/search/" + fileNameURLEncoded;
			
			return searchTerm;
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		LinkedList<SearchResult> linksList = new LinkedList<SearchResult>();
		try{
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			{
				Elements divVideoLinksElements = doc.select("div.item:has(a[href*=/movie/])");

				for(Element currentDivVideoLink : divVideoLinksElements)
				{
					Element videoLinksElements = currentDivVideoLink.select("a[href*=/movie/]").last();
					String idFromSearchResult = currentDivVideoLink.select("span").first().text();
					String currentLink = videoLinksElements.attr("href");
					String currentLabel = idFromSearchResult + " " + videoLinksElements.text();
					String currentThumb = currentDivVideoLink.select("img").first().attr("src");
					
					if(currentLink.length() > 1)
					{
						SearchResult searchResult = new SearchResult(currentLink, currentLabel, new Thumb(currentThumb));
						
						//maybe we can improve search accuracy by putting our suspected best match at the front of the array
						//we do this by examining the ID from the search result and seeing if it was in our initial search string
						if(searchString.contains(idFromSearchResult) || searchString.contains(idFromSearchResult.replaceAll(Pattern.quote("-"),"")))
							linksList.addFirst(searchResult);
						else
							linksList.addLast(searchResult);
					}
				}

				return linksList.toArray(new SearchResult[linksList.size()]);
			}
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new SearchResult[0];
			}
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> imageList = new ArrayList<Thumb>();

		Elements sampleBoxImageLinks = document.select("div.sample-box li a[href]");
		if (sampleBoxImageLinks != null) {
			for(Element link: sampleBoxImageLinks)
				try {
					imageList.add(new Thumb(link.attr("href")));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
		}
		
		return imageList.toArray(new Thumb[imageList.size()]);
	}
	
	public String toString(){
		return "JavZoo";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new JavZooParsingProfile();
	}

	@Override
	public String getParserName() {
		return "JavZoo";
	}

}
