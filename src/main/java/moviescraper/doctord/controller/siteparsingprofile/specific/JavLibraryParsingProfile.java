package moviescraper.doctord.controller.siteparsingprofile.specific;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.languagetranslation.Language;
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
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class JavLibraryParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private String siteLanguageToScrape;
	
	public static final String englishLanguageCode = "en";
	public static final String japaneseLanguageCode = "ja";
	public static final String taiwaneseLanguageCode = "tw";
	public static final String chineseLanguageCode = "cn";
	private static final boolean reverseAsianNameInEnglish = true;
	private String overrideURLJavLibrary;
	
	private static final SimpleDateFormat javLibraryReleaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	public String getOverrideURLJavLibrary() {
		return overrideURLJavLibrary;
	}

	public void setOverrideURLJavLibrary(String overrideURLJavLibrary) {
		this.overrideURLJavLibrary = overrideURLJavLibrary;
	}

	public JavLibraryParsingProfile(Document document) {
		super(document);
		siteLanguageToScrape = determineLanguageToUse();
	}

	public JavLibraryParsingProfile() {
		siteLanguageToScrape = determineLanguageToUse();
	}
	
	private String determineLanguageToUse() {
		return MoviescraperPreferences.getInstance().getScrapeInJapanese() ? "ja" : "en";
	}
	
	public JavLibraryParsingProfile(Document document, String siteLanguageToScrape)
	{
		super(document);
		this.siteLanguageToScrape = siteLanguageToScrape;
	}
	
	public JavLibraryParsingProfile(String siteLanguageToScrape) {
		this.siteLanguageToScrape = siteLanguageToScrape;
	}

	@Override
	public Title scrapeTitle() {

		Element titleElement = document
				.select("h3.post-title.text a")
				.first();
		//remove the ID number off beginning of the title, if it exists (and it usually always does on JavLibrary)
		if(titleElement != null)
		{
			String titleElementText = titleElement.text().trim();
			titleElementText = titleElementText.substring(StringUtils.indexOf(titleElementText," ")).trim();
			//sometimes this still leaves "- " at the start of the title, so we'll want to get rid of that too
			if(titleElementText.startsWith("- "))
			{
				titleElementText = titleElementText.replaceFirst(Pattern.quote("- "), "");
			}
			return new Title(titleElementText);
		}
		//this shouldn't really ever happen...
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {

		if (siteLanguageToScrape.equals(japaneseLanguageCode))
			return new OriginalTitle(scrapeTitle().getTitle());
		
		try {
			String japaneseUrl = document.location().replace("javlibrary.com/" + siteLanguageToScrape + "/", "javlibrary.com/" + japaneseLanguageCode + "/");			
			Document japaneseDoc = Jsoup.connect(japaneseUrl).userAgent(getRandomUserAgent()).timeout(CONNECTION_TIMEOUT_VALUE).get();
			JavLibraryParsingProfile profile = new JavLibraryParsingProfile(japaneseDoc, japaneseLanguageCode);
			return profile.scrapeOriginalTitle();
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
		// Site doesn't have any set information
		return Set.BLANK_SET;

	}

	@Override
	public Rating scrapeRating() {
		//JavLibrary uses a decimal value out of 10 for its rating
		Element ratingElement = document
				.select("span.score")
				.first();
		if(ratingElement != null)
		{
			String ratingText = ratingElement.text();
			//Found a match, get rid of surrounding parenthesis and use this as the rating
			if(ratingText.contains("("))
			{
				ratingText = ratingText.substring(1,ratingText.length()-1).trim();
			}
			return new Rating(10,ratingText);
		}
		else return Rating.BLANK_RATING; //No rating found on the page
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element dateElement = document
				.select("div#video_date tr td.header + td.text")
				.first();
		String dateText = dateElement.text();
		//The dateText is in format YYYY-MM-DD
		if(dateText.length() > 0)
		{
			dateText = dateText.trim();
			return new ReleaseDate(dateText, javLibraryReleaseDateFormat);
		}
		else return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on JavLibrary
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
		Element lengthElement = document
				.select("div#video_length tr td.header + td span.text")
				.first();
		String lengthText = lengthElement.text();
		if(lengthText.length() > 0)
		{
			return new moviescraper.doctord.model.dataitem.Runtime(lengthText);
		}
		else return new moviescraper.doctord.model.dataitem.Runtime("");
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
				.select("img#video_jacket_img")
				.first();
		Thumb[] posterThumbs = new Thumb[1];
		if(posterElement != null)
		{
			String posterLink = "https:" + posterElement.attr("src").trim();
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
		Element idElement = document
				.select("div#video_id tr td.header + td.text")
				.first();
		String idText = idElement.text();
		if(idText.length() > 0)
		{
			return new ID(idText);
		}
		else return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document
				.select(".genre");
		ArrayList<Genre> genreList = new ArrayList<>(genreElements.size());
		for (Element genreElement : genreElements)
		{
			String currentGenreText = genreElement.text().trim();
			//Sometimes javlibrary has junk genres like video sample. It's not really a genre, so get rid of it!
			if(acceptGenreText(currentGenreText))
				genreList.add(new Genre(currentGenreText));
		}
		return genreList;
	}
	
	private boolean acceptGenreText(String genreText){
		switch(genreText)
		{
		case "Video Sample":
			return false;
		case "Blu-ray":
			return false;
		case "With Gifts":
			return false;
		default:
			break;
		}
			
		return true;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements castElements = document
				.select("span.cast");
		ArrayList<Actor> actorList = new ArrayList<>(castElements.size());
		for (Element castElement : castElements) {
			String actressName = castElement.select("span.star a").text().trim();
			Elements aliasElements = castElement.select("span.alias");
			String [] aliasNames = new String[aliasElements.size()];
			int i = 0; //index of loop iteration
			for(Element aliasElement : aliasElements)
			{
				String currentAlias = aliasElement.text().trim();
				//we might need to reverse the alias name from lastname, firstname to firstname lastname, if we're scraping in english and
				//we specify in options
				if(reverseAsianNameInEnglish && siteLanguageToScrape == englishLanguageCode && currentAlias.contains(" "))
					currentAlias = StringUtils.reverseDelimited(currentAlias, ' ');
				aliasNames[i] = currentAlias;
				i++;
			}
			//String aliasName = castElement.select("span.alias").text().trim();
			
			//JavLibrary has asian names in Lastname, first format. Reverse it, if we specify it with the option to do so
			//but only do this if we're scraping in english
			if(reverseAsianNameInEnglish && (siteLanguageToScrape == englishLanguageCode || scrapingLanguage == Language.ENGLISH) && actressName.contains(" "))
			{
				actressName = StringUtils.reverseDelimited(actressName, ' ');
				
			}
			/*if(reverseAsianNameInEnglish && siteLanguageToScrape == englishLanguageCode && aliasName.contains(" "))
			aliasName = StringUtils.reverseDelimited(aliasName, ' ');
			
			if(aliasName.length() > 0)
				actressName += " (" + aliasName + ")";*/
			if(aliasNames.length > 0)
			{
				for(int j = 0; j < aliasNames.length; j++)
				{
					actressName = actressName + " (" + aliasNames[j] + ")";
				}
			}
			actorList.add(new Actor(actressName,"",null));
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		Elements directorElements = document
				.select(".director a");
		ArrayList<Director> directorList = new ArrayList<>(directorElements.size());
		for (Element currentDirectorElement : directorElements)
		{
			String currentDirectorName = currentDirectorElement.text().trim();
			directorList.add(new Director(currentDirectorName,null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document
				.select(".maker a")
				.first();
		if(studioElement != null)
		{
			return new Studio(studioElement.text().trim());
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
			String searchTerm = "http://www.javlibrary.com/" + siteLanguageToScrape + "/vl_searchbyid.php?keyword=" + fileNameURLEncoded;
			
			return searchTerm;
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		

	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		
		ArrayList<SearchResult> linksList = new ArrayList<>();
		String websiteURLBegin = "http://www.javlibrary.com/" + siteLanguageToScrape;
		try{
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		//The search found the page directly
		if(doc.baseUri().contains("/?v="))
		{
			String linkTitle = doc.title().replaceAll(Pattern.quote(" - JAVLibrary"), "");
			Element posterElement = doc
					.select("img#video_jacket_img")
					.first();
			//the page does not have the small version on it, but by replacing the last character of the string with an t, we will get the tiny preview
			if(posterElement != null)
			{
				String posterURLSmall = "https:" + posterElement.attr("src");
				posterURLSmall = posterURLSmall.substring(0, posterURLSmall.lastIndexOf('l')) + "t.jpg";
				linksList.add(new SearchResult(doc.baseUri(), linkTitle, new Thumb(posterURLSmall)));
			}
			else 
			{
				linksList.add(new SearchResult(doc.baseUri(), linkTitle));
			}
			//System.out.println("Added " + doc.baseUri());
			
			return linksList.toArray(new SearchResult[linksList.size()]);
		}
		else
		{
			//The search didn't find an exact match and took us to the search results page
			//We're filtering out anything that does not exactly match the id from the search query
			
			String searchId = new URLCodec().decode(searchString.replaceAll(".*\\?keyword=(.*)$", "$1")).toUpperCase();
			Elements videoLinksElements = doc.select("div.video:has(div.id:matchesOwn(^"+Pattern.quote(searchId)+"$))");
			
			for(Element videoLink : videoLinksElements)
			{
				String currentLink = videoLink.select("a").attr("href");
				String currentLinkLabel = videoLink.select("a").attr("title").trim();
				String currentLinkImage = "https:" + videoLink.select("img").attr("src");
				if(currentLink.length() > 1)
				{
					String fullLink = websiteURLBegin + currentLink.substring(1);
					linksList.add(new SearchResult(fullLink,currentLinkLabel,new Thumb(currentLinkImage)));
					//System.out.println("Added " + fullLink);
				}
			}
			return linksList.toArray(new SearchResult[linksList.size()]);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SearchResult[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		//No extra Fanart on this site is supported, for now
		return new Thumb[0];
	}
	
	@Override
	public String toString(){
		return "JavLibrary";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new JavLibraryParsingProfile();
	}

	@Override
	public String getParserName() {
		return "JAVLibrary";
	}


}

