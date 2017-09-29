package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.languagetranslation.JapaneseCharacter;
import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.languagetranslation.TranslateString;
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

public class JavBusParsingProfile extends SiteParsingProfile implements SpecificProfile {
	
	public static final String urlLanguageEnglish = "en";
	public static final String urlLanguageJapanese = "ja";
	//JavBus divides movies into two categories - censored and uncensored.
	//All censored movies need cropping of their poster
	private boolean isCensoredSearch = true;
	private Document japaneseDocument;
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	private void initializeJapaneseDocument() {
		if(japaneseDocument == null)
		{
			String urlOfCurrentPage = document.location();
			if(urlOfCurrentPage != null && urlOfCurrentPage.contains("/en/"))
			{
				//the genres are only available on the japanese version of the page
				urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://www.javbus.com/en/"), "http://www.javbus.com/ja/");
				if(urlOfCurrentPage.length() > 1)
				{
						try {
							japaneseDocument = Jsoup.connect(urlOfCurrentPage).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
			else if(document != null)
				japaneseDocument = document;		
		}
	}
	
	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("title").first();
		if(titleElement != null)
		{
			String titleText = titleElement.text();
			titleText = titleText.replace("- JavBus", "");
			//Remove the ID from the front of the title
			if(titleText.contains(" "))
				titleText = titleText.substring(titleText.indexOf(" "),titleText.length());
			//Translate the element using google translate if needed
			if(scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(titleText))
				titleText = TranslateString.translateStringJapaneseToEnglish(titleText);				
			return new Title(titleText);
		}
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		initializeJapaneseDocument();
		if(japaneseDocument != null)
		{
			Element titleElement = japaneseDocument.select("title").first();
			if(titleElement != null)
			{
				String titleText = titleElement.text();
				titleText = titleText.replace("- JavBus", "");
				//Remove the ID from the front of the title
				if(titleText.contains(" "))
					titleText = titleText.substring(titleText.indexOf(" "),titleText.length());
				return new OriginalTitle(titleText);
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
		String seriesWord = (scrapingLanguage == Language.ENGLISH) ? "Series:" : "シリーズ:";
		Element setElement = document.select("span.header:containsOwn(" + seriesWord + ") ~ a").first();
		if(setElement != null && setElement.text().length() > 0)
		{
			String setText = setElement.text();
			if(scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(setText))
			{
				setText = TranslateString.translateStringJapaneseToEnglish(setText);
			}
			return new Set(setText);
		}
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		String releaseDateWord = (scrapingLanguage == Language.ENGLISH) ? "Release Date:" : "発売日:";
		Element releaseDateElement = document.select("p:contains(" + releaseDateWord + ")").first();
		if(releaseDateElement != null && releaseDateElement.ownText().trim().length() > 4)
		{
			String releaseDateText = releaseDateElement.ownText().trim();
			return new ReleaseDate(releaseDateText);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
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
		String lengthWord = (scrapingLanguage == Language.ENGLISH) ? "Length:" : "収録時間:";
		Element lengthElement = document.select("p:contains(" + lengthWord + ")").first();
		if(lengthElement != null && lengthElement.ownText().trim().length() >= 0)
		{
			//Getting rid of the word "min" in both Japanese and English
			String runtimeText = lengthElement.ownText().trim().replace("min","");
			runtimeText = runtimeText.replace("分", "");
			return new Runtime(runtimeText);
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
	
	private Thumb[] scrapePostersAndFanart(boolean isPosterScrape)
	{
		Element posterElement = document.select("a.bigImage").first();
		if(posterElement != null)
		{
			try {
				Thumb posterImage = new Thumb(posterElement.attr("href"), (isCensoredSearch && isPosterScrape));
				Thumb[] posterArray = {posterImage};
				return posterArray;
			} catch (IOException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
		}
		else return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		Elements extraFanartElements = document.select("div.sample-box ul li a");
		if(extraFanartElements != null && extraFanartElements.size() > 0)
		{
			Thumb[] extraFanart = new Thumb[extraFanartElements.size()];
			int i = 0;
			for(Element extraFanartElement : extraFanartElements)
			{
				String href = extraFanartElement.attr("href");
				try {
					extraFanart[i] = new Thumb(href);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			return extraFanart;
		}
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select(".movie .info span + span").first();
		if(idElement != null)
			return new ID(idElement.text());
		else return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Elements genreElements = document.select("span.genre a[href*=/genre/");
		if(genreElements != null)
		{
			for(Element genreElement : genreElements)
			{
				String genreText = genreElement.text();
				if(genreElement.text().length() > 0)
				{
					//some genre elements are untranslated, even on the english site, so we need to do it ourselves
					if(scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(genreText))
					{
						genreText = TranslateString.translateStringJapaneseToEnglish(genreText);
					}
					genreList.add(new Genre(WordUtils.capitalize(genreText)));
				}
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>();
		Elements actorElements = document.select("div.star-box li a img");
		if(actorElements != null)
		{
			for(Element currentActor: actorElements)
			{
				String actorName = currentActor.attr("title");
				//Sometimes for whatever reason the english page still has the name in japanaese, so I will translate it myself
				if(scrapingLanguage == Language.ENGLISH && JapaneseCharacter.containsJapaneseLetter(actorName))
					actorName = TranslateString.translateJapanesePersonNameToRomaji(actorName);
				String actorImage = currentActor.attr("src");
				if(actorImage != null && !actorImage.contains("printing.gif") && fileExistsAtURL(actorImage))
				{
					try {
						actorList.add(new Actor(actorName, null, new Thumb(actorImage)));
					} catch (MalformedURLException e) {
						e.printStackTrace();
						actorList.add(new Actor(actorName, null, null));
					}
				}
				else
				{
					actorList.add(new Actor(actorName, null, null));
				}
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		String directorWord = (scrapingLanguage == Language.ENGLISH) ? "Director:" : "監督:";
		Element directorElement = document.select("span.header:containsOwn(" + directorWord + ") ~ a").first();
		if(directorElement != null && directorElement.text().length() > 0)
		{
			directorList.add(new Director(directorElement.text(), null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		String studioWord = (scrapingLanguage == Language.ENGLISH) ? "Studio:" : "メーカー:";
		Element studioElement = document.select("span.header:containsOwn(" + studioWord + ") ~ a").first();
		if(studioElement != null && studioElement.text().length() > 0)
		{
			return new Studio(studioElement.text());
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			String searchTerm = "http://www.javbus.com/" + getUrlLanguageToUse() + "/search/" + fileNameURLEncoded;
			return searchTerm;
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getUrlLanguageToUse()
	{
		String urlLanguageToUse = (scrapingLanguage == Language.ENGLISH) ? urlLanguageEnglish : urlLanguageJapanese;
		return urlLanguageToUse;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		ArrayList<SearchResult> linksList = new ArrayList<>();
		try{
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements videoLinksElements = doc.select("div.item");
			if(videoLinksElements == null || videoLinksElements.size() == 0)
			{
				searchString = searchString.replace("/search/", "/uncensored/search/");
				isCensoredSearch = false;
			}
			doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			videoLinksElements = doc.select("div.item");
			if(videoLinksElements != null)
			{
				for(Element videoLink : videoLinksElements)
				{
					String currentLink = videoLink.select("a").attr("href");
					String currentLinkLabel = videoLink.select("a").text().trim();
					String currentLinkImage = videoLink.select("img").attr("src");
					if(currentLink.length() > 1)
					{
						linksList.add(new SearchResult(currentLink,currentLinkLabel,new Thumb(currentLinkImage)));
					}
				}
			}
			return linksList.toArray(new SearchResult[linksList.size()]);
		}

		catch (IOException e) {
			e.printStackTrace();
			return new SearchResult[0];
		}
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new JavBusParsingProfile();
	}

	@Override
	public String getParserName() {
		return "JavBus";
	}

}
