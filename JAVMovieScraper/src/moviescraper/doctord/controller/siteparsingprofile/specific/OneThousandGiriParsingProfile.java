package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class OneThousandGiriParsingProfile extends SiteParsingProfile implements
		SpecificProfile {

	private String idSearchedForFromFileName;
	Document japaneseDocument;
	
	/**
	 * loads up the japanese version of this page into japaneseDocument
	 */
	private void initializeJapaneseDocument()
	{
		if(document != null && japaneseDocument == null && scrapingLanguage.equals(Language.ENGLISH))
		{
			String url = document.baseUri().replaceFirst("http://en", "http://www");
			System.out.println("url = " + url);
			japaneseDocument = SiteParsingProfile.downloadDocumentFromURLString(url);
		}
		else if(document != null && japaneseDocument == null)
			japaneseDocument = document;
	}
	
	@Override
	public Title scrapeTitle() {
		if(pageIsValidMoviePage())
		{
			Element titleElement = document.select("head title").first();
			if(scrapingLanguage.equals(Language.ENGLISH) && titleElement != null)
			{
				return new Title(WordUtils.capitalize(TranslateString
						.translateStringJapaneseToEnglish(titleElement.text()
								.replace("1000giri.com | ", ""))));
			}

			else if(scrapingLanguage.equals(Language.JAPANESE))
			{
				initializeJapaneseDocument();
				return new Title(getJapaneseTitleText(japaneseDocument));
			}
		}
		return new Title("");
	}
	
	/**
	 * Helper method to get the japanese title. Used for both the original title
	 * and the title if the scraper is scraping the page in Japanese
	 */
	private String getJapaneseTitleText(Document japaneseDocument)
	{
		Element titleElement = japaneseDocument.select("head title").first();
		if(titleElement != null)
		{
			String titleElementText = titleElement.text();
			if(titleElementText.contains("| "))
			{
				try{
					titleElementText = titleElementText.substring(titleElementText.indexOf("| ")+2);
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					e.printStackTrace();
				}
			}
			return titleElementText;
		}
		return "";
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		initializeJapaneseDocument();
		return new OriginalTitle(getJapaneseTitleText(japaneseDocument));
		
	}

	@Override
	public SortTitle scrapeSortTitle() {
		//This is something the user sets themselves
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		//No set information for this website
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		//No rating information on this website
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate()
	{
		initializeJapaneseDocument();
		Element releaseDateElement = japaneseDocument.select("table.detail tbody tr th:contains(配信日) + td").first();
		if(releaseDateElement != null && releaseDateElement.text().length() > 4)
		{
			return new ReleaseDate(releaseDateElement.text().trim());
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		//No Top250 on this website
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		//No Votes on this website
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		//No outline on this website
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		initializeJapaneseDocument();
		Element plotElement = japaneseDocument.select("table.detail tbody tr td p").last();
		if(plotElement != null && plotElement.text().length() > 0)
		{
			String plotText = plotElement.text();
			if(scrapingLanguage.equals(Language.ENGLISH))
				plotText = TranslateString.translateStringJapaneseToEnglish(plotText);
			return new Plot(plotText);
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		//No Tagline info on this site
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		//no Runtime info on this site
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		if (pageIsValidMoviePage()) {
			Thumb swfPoster;
			try {
				swfPoster = new Thumb(baseSiteUrl() + "gallery/"
						+ idSearchedForFromFileName + "/images/swf_f.jpg");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
			Thumb[] posterArray = { swfPoster };
			return posterArray;
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		if (pageIsValidMoviePage())
			return new ID(idSearchedForFromFileName);
		else return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		initializeJapaneseDocument();
		Elements genreElements;
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		if(getScrapingLanguage().equals(Language.ENGLISH))
			genreElements = document.select("table.detail tbody tr th:contains(Type) + td a, table.detail tbody tr th:contains(Genres) + td a");
		else
			genreElements = japaneseDocument.select("table.detail tbody tr th:contains(タイプ) + td a, table.detail tbody tr th:contains(ジャンル) + td a");
		if(genreElements != null)
		{
			for(Element genre : genreElements)
			{
				if(genre.text().length() > 0)
				{
					if(!genre.text().equals("Exclusive video"))
					{
						Genre genreToAdd = new Genre(genre.text());
						genreList.add(genreToAdd);
					}
				}
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		initializeJapaneseDocument();
		Elements actorElements;
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		if(getScrapingLanguage().equals(Language.ENGLISH))
			actorElements = document.select("table.detail tbody tr th:contains(Name) + td a");
		else
			actorElements = japaneseDocument.select("table.detail tbody tr th:contains(�??�?) + td a");
		if(actorElements != null)
		{
			for(Element actor : actorElements)
			{
				if(actor.text().length() > 0)
				{
					Actor actorToAdd = new Actor(actor.text(),"",null);
					actorList.add(actorToAdd);
				}
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//No Director info on this site
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		return new Studio("1000giri");
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		idSearchedForFromFileName = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return idSearchedForFromFileName;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		if(idSearchedForFromFileName == null)
			idSearchedForFromFileName = searchString;
		if(searchString != null && searchString.length() > 0)
		{
			SearchResult [] searchResultArray = 
				{new SearchResult(baseSiteUrl() + "moviepages/" + searchString + "/index.html", 
						searchString, 
						new Thumb(baseSiteUrl() + "gallery/" + searchString + "/images/index_s.jpg"))};
			//if no page for this ID, don't try to keep scraping by returning a URL to a page that doesn't exist
			if(fileExistsAtURL(searchResultArray[0].getUrlPath()))
				return searchResultArray;
			
		}
		return new SearchResult[0];
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new OneThousandGiriParsingProfile();
	}

	@Override
	public String getParserName() {
		return "1000giri";
	}
	
	private String baseSiteUrl()
	{
		if(this.scrapingLanguage.equals(Language.ENGLISH))
			return "http://en.1000giri.net/";
		else return "http://www.1000giri.net/";			
	}
	
	/**
	 * Used to make sure when we are scraping our document that we have not been
	 * redirected due to a 404 when scraping from our URL
	 * @return true if we are on a valid page to scrape from, false otherwise
	 */
	private boolean pageIsValidMoviePage()
	{
		if(document.baseUri().contains(idSearchedForFromFileName))
			return true;
		else return false;
	}
	
}
