package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class EICBookParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private boolean doGoogleTranslation;
	private boolean scrapeTrailers;
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	public EICBookParsingProfile()
	{
		super();
		doGoogleTranslation = (scrapingLanguage == Language.ENGLISH);
		scrapeTrailers = true;
	}
	
	public EICBookParsingProfile(Document document) {
		super(document);
		doGoogleTranslation = (scrapingLanguage == Language.ENGLISH);
	}

	/**
	 * Default constructor does not define a document, so be careful not to call
	 * scrape methods without initializing the document first some other way.
	 * This constructor is mostly used for calling createSearchString() and
	 * getSearchResults()
	 */
	public EICBookParsingProfile(boolean doGoogleTranslation) {
		super();
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
		scrapeTrailers = true;
	}

	public EICBookParsingProfile(boolean doGoogleTranslation, boolean scrapeTrailers) {
		super();
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
		this.scrapeTrailers = scrapeTrailers;
	}

	public EICBookParsingProfile(Document document, boolean doGoogleTranslation) {
		super(document);
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
	}

	@Override
	public Title scrapeTitle() {
		String whitespace_chars =  ""       /* dummy empty string for homogeneity */
                + "\\u0009" // CHARACTER TABULATION
                + "\\u000A" // LINE FEED (LF)
                + "\\u000B" // LINE TABULATION
                + "\\u000C" // FORM FEED (FF)
                + "\\u000D" // CARRIAGE RETURN (CR)
                + "\\u0020" // SPACE
                + "\\u0085" // NEXT LINE (NEL) 
                + "\\u00A0" // NO-BREAK SPACE
                + "\\u1680" // OGHAM SPACE MARK
                + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
                + "\\u2000" // EN QUAD 
                + "\\u2001" // EM QUAD 
                + "\\u2002" // EN SPACE
                + "\\u2003" // EM SPACE
                + "\\u2004" // THREE-PER-EM SPACE
                + "\\u2005" // FOUR-PER-EM SPACE
                + "\\u2006" // SIX-PER-EM SPACE
                + "\\u2007" // FIGURE SPACE
                + "\\u2008" // PUNCTUATION SPACE
                + "\\u2009" // THIN SPACE
                + "\\u200A" // HAIR SPACE
                + "\\u2028" // LINE SEPARATOR
                + "\\u2029" // PARAGRAPH SEPARATOR
                + "\\u202F" // NARROW NO-BREAK SPACE
                + "\\u205F" // MEDIUM MATHEMATICAL SPACE
                + "\\u3000" // IDEOGRAPHIC SPACE
                ;        
		/* A \s that actually works for Java’s native character set: Unicode */
		String whitespace_charclass = "["  + whitespace_chars + "]";  
		
		Element titleElement = document.select("h1").first();
		// run a google translate on the japanese title
		if(doGoogleTranslation)
		{
			return new Title(
					TranslateString.translateStringJapaneseToEnglish(titleElement
							.text().replaceAll("(" + whitespace_charclass + "Blu-ray版|" + whitespace_charclass + "DVD版)", "")));
		}
		else
		{
			return new Title(titleElement.text().replaceAll("(" + whitespace_charclass + "Blu-ray版|" + whitespace_charclass + "DVD版)", ""));
		}
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		String whitespace_chars =  ""       /* dummy empty string for homogeneity */
                + "\\u0009" // CHARACTER TABULATION
                + "\\u000A" // LINE FEED (LF)
                + "\\u000B" // LINE TABULATION
                + "\\u000C" // FORM FEED (FF)
                + "\\u000D" // CARRIAGE RETURN (CR)
                + "\\u0020" // SPACE
                + "\\u0085" // NEXT LINE (NEL) 
                + "\\u00A0" // NO-BREAK SPACE
                + "\\u1680" // OGHAM SPACE MARK
                + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
                + "\\u2000" // EN QUAD 
                + "\\u2001" // EM QUAD 
                + "\\u2002" // EN SPACE
                + "\\u2003" // EM SPACE
                + "\\u2004" // THREE-PER-EM SPACE
                + "\\u2005" // FOUR-PER-EM SPACE
                + "\\u2006" // SIX-PER-EM SPACE
                + "\\u2007" // FIGURE SPACE
                + "\\u2008" // PUNCTUATION SPACE
                + "\\u2009" // THIN SPACE
                + "\\u200A" // HAIR SPACE
                + "\\u2028" // LINE SEPARATOR
                + "\\u2029" // PARAGRAPH SEPARATOR
                + "\\u202F" // NARROW NO-BREAK SPACE
                + "\\u205F" // MEDIUM MATHEMATICAL SPACE
                + "\\u3000" // IDEOGRAPHIC SPACE
                ;        
		/* A \s that actually works for Java’s native character set: Unicode */
		String whitespace_charclass = "["  + whitespace_chars + "]"; 
		
		Element titleElement = document.select("h1").first();
		// leave the original title as the japanese title
		return new OriginalTitle(titleElement.text().replaceAll("(" + whitespace_charclass + "Blu-ray版|" + whitespace_charclass + "DVD版)", ""));
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("tr:has(th:contains(シリーズ)) td a").first();
		if (setElement == null)
			return Set.BLANK_SET;
		else if (doGoogleTranslation)
		{
			return new Set(
					TranslateString.translateStringJapaneseToEnglish(setElement
							.text()));
		}
		else return new Set(setElement.text());
	}

	@Override
	public Rating scrapeRating() {
		// EIC-Book doesn't have this
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate(){
		Element releaseDateElement = document
				.select("tr:has(th:contains(発売日)) td a")
				.first();
		if(releaseDateElement != null)
		{
			String releaseDate = releaseDateElement.text();
			return new ReleaseDate(releaseDate);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		// EIC-Book doesn't have this
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		// EIC-Book doesn't have this
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		// EIC-Book doesn't have this
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		Element plotElement;
		if (document.select("#player1") != null) { // If the page has a trailer
			plotElement = document.select("#mainCon #dtSpec p:nth-child(4)").first();
		} else { // If it doesn't
			plotElement = document.select("#mainCon #dtSpec p:nth-child(3)").first();
		}
		if(doGoogleTranslation)
		{
			return new Plot(
					TranslateString.translateStringJapaneseToEnglish(plotElement
							.text()));
		}
		else return new Plot(plotElement.text());
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public moviescraper.doctord.model.dataitem.Runtime scrapeRuntime() {
		String runtime = "";
		Element runtimeElement = document.select(
				"tr:has(th:contains(収録時間)) td a").first();
		if (runtimeElement != null) {
			// get rid of japanese word for minutes and just get the number
			runtime = runtimeElement.text().replaceAll("分", "");
		}
		return new moviescraper.doctord.model.dataitem.Runtime(runtime);
	}
	
	@Override
	public Trailer scrapeTrailer(){
		try {
			Element playerElement;
			
			if(scrapeTrailers && (playerElement = document.select(".detailMN #player1").first()) != null){
				System.out.println("There should be a trailer, searching now...");
				
				// The actual .flv should be exposed in an attribute of the player.
				String potentialTrailerURL = playerElement.select("param[value^=flv=]").first().attr("value").replaceAll("(flv=|\\u0026[a-z=:/.-]+)", "");
				System.out.println("Trailer existed at: " + potentialTrailerURL);
				return new Trailer(potentialTrailerURL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Trailer.BLANK_TRAILER;
	}
	
	
	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart(true, false);
	}

	/**
	 * Helper method for scrapePoster() and scapeFanart since this code is
	 * virtually identical
	 * 
	 * @param doCrop
	 *            - if true, will only get the front cover as the initial poster
	 *            element; otherwise it uses the entire dvd case from dmm.com
	 * @return Thumb[] containing all the scraped poster and extraart (if doCrop
	 *         is true) or the cover and back in extraart (if doCrop is false)
	 */
	private Thumb[] scrapePostersAndFanart(boolean doCrop, boolean scrapingExtraFanart) {

		// the movie poster, on this site it usually has both front and back
		// cover joined in one image
		Element postersElement = document.select(
				"#dtTopL a").first();
		if (postersElement == null) {
			postersElement = document.select(
					"#dtTopL img").first();
		}

		ArrayList<Thumb> posters = new ArrayList<>(1);
		String posterLink = postersElement.attr("href").replaceAll("https", "http");
		if(posterLink == null || posterLink.length() < 1)
			posterLink = postersElement.attr("src").replaceAll("https", "http");
		try {
			// for the poster, do a crop of the the right side of the dvd case image 
			//(which includes both cover art and back art)
			// so we only get the cover
			if (doCrop && !scrapingExtraFanart)
				//use javCropCoverRoutine version of the new Thumb constructor to handle the cropping
				posters.add(new Thumb(posterLink, true));
			else if (!scrapingExtraFanart)
				posters.add(new Thumb(posterLink));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return posters.toArray(new Thumb[0]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false, false);
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {	
		Element idElement = document.select("tr:has(th:contains(商品コード)) td").first();
		if(idElement != null)
		{
			String idElementText = idElement.text();
			idElementText = fixUpIDFormatting(idElementText);
			return new ID(idElementText);
		}
		//This page didn't have an ID, so just put in a empty one
		else return ID.BLANK_ID;
	}
	
	public static String fixUpIDFormatting(String idElementText){
		// The ID has superfluous information after it
		int firstSpaceIndex = StringUtils.indexOfAny(idElementText, " ");
		idElementText = idElementText.substring(0,firstSpaceIndex);
		return idElementText;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document
				.select("tr:has(th:contains(ジャンル)) td a");
		ArrayList<Genre> genres = new ArrayList<>(genreElements.size());
		for (Element genreElement : genreElements) {
			// get the link so we can examine the genre name and do some sanity cleanup
			// and perhaps some better translation that what google has, if we
			// happen to know better
			String href = genreElement.attr("href");
			String genreID = genreElement.attr("href").substring(
					href.indexOf("genre_name=") + 11, href.length());
			if (acceptGenreID(genreID)) {
				if(doGoogleTranslation == false)
				{
					genres.add(new Genre(genreElement.text()));
				}
				else
				{
					String potentialBetterTranslation = betterGenreTranslation(
							genreElement.text(), genreID);

					// we didn't know of anything hand picked for genres, just use
					// google translate
					if (potentialBetterTranslation.equals("")) {
						genres.add(new Genre(TranslateString
								.translateStringJapaneseToEnglish(genreElement
										.text())));
					}
					// Cool, we got something we want to use instead for our genre,
					// let's use that
					else {
						genres.add(new Genre(potentialBetterTranslation));
					}
				}
			}
		}
		// System.out.println("genres" + genreElements);
		return genres;
	}

	private String betterGenreTranslation(String text, String genreID) {
		String betterGenreTranslatedString = "";
		switch (genreID) {
			case "いもうと系":
				betterGenreTranslatedString = "Little Sister Body"; // Might want a better translation for this. Perhaps "Young Body" ?
				break;
			case "制服":
				betterGenreTranslatedString = "School Uniform";
				break;
			case "小学生アイドル":
				betterGenreTranslatedString = "Elementary School Idol";
				break;
			case "美少女":
				betterGenreTranslatedString = "Beautiful Girl";
				break;
			case "女子高生":
				betterGenreTranslatedString = "Female High-School Student";
				break;
			case "中学生アイドル":
				betterGenreTranslatedString = "Junior High-School Idol";
				break;
			case "ジュニアアイドル":
				betterGenreTranslatedString = "Junior Idol";
				break;
			case "レオタード":
				betterGenreTranslatedString = "Leotard";
				break;
			case "水着":
				betterGenreTranslatedString = "Swimsuit";
				break;
			case "体操服":
				betterGenreTranslatedString = "Gym Uniform";
				break;
			case "":
				betterGenreTranslatedString = "";
				break;
		}

		return betterGenreTranslatedString;
	}

	private String betterActressTranslation(String text, String actressID) {
		String betterActressTranslatedString = "";
		switch (actressID) {
		}

		return betterActressTranslatedString;
	}

	// Return false on any genres we don't want scraping in. This can later be
	// something the user configures, but for now I'll use it
	// to get rid of weird stuff like DVD toaster
	// the genreID comes from the href to the genre keyword from DMM
	// Example: <a href="/mono/dvd/-/list/=/article=keyword/id=6004/">
	// The genre ID would be 6004 which is passed in as the String
	private boolean acceptGenreID(String genreID) {
		switch (genreID) {
		case "MOOK": // "Mook"
			return false;
		case "Blu-ray（ブルーレイ）": // "Blu-ray"
			return false;
		case "高画質DL対応": // "High Resolution DL"
			return false;
		case "フルHD画質": // "Full HD Quality"
			return false;
		case "セル同発配信/新作配信": // "Latest Release"
			return false;
		case "デビュー作": // "Debut Work", no sure we want to scrape it so removing it for now
			return false;
		case "980円動画": // "Discounted Video"
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		// scrape all the actress IDs
		Elements actressIDElements = document
				.select("tr:has(th:contains(出演者)) td a");
		ArrayList<Actor> actorList = new ArrayList<>(
				actressIDElements.size());
		
		// No actors have dedicated pages on DMM.com, so just scrape the base names
		Elements nameOnlyActors = document.select("a[href*=model_id=]");
		for(Element currentNameOnlyActor : nameOnlyActors)
		{
			String actorName = currentNameOnlyActor.text().trim();
			if(doGoogleTranslation)
				actorName = TranslateString.translateJapanesePersonNameToRomaji(actorName);
			actorList.add(new Actor(actorName, "", null));
		}

		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directors = new ArrayList<>();
		Element directorElement = document.select(
				"tr:has(th:contains(監督・スタッフ)) td a").first();
		if (directorElement != null && directorElement.hasText()) {
			if(doGoogleTranslation)
				directors.add(new Director(TranslateString
					.translateStringJapaneseToEnglish(directorElement.text()),
					null));
			else
				directors.add(new Director(directorElement.text(),null));
		}
		return directors;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select(
				"tr:has(th:contains(メーカー)) td a").first();
		if (studioElement != null)
		{
			if(doGoogleTranslation)		
			return new Studio(
					TranslateString.translateStringJapaneseToEnglish(studioElement
							.text()));
			else return new Studio(studioElement.text());
		}
		else return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		System.out.println("fileNameNoExtension in EIC-Book: " + fileNameNoExtension);
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			return "http://www.eic-book.com/search?q="
			+ fileNameURLEncoded;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * returns a String[] filled in with urls of each of the possible movies
	 * found on the page returned from createSearchString
	 * 
	 * @throws IOException
	 */
	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		boolean firstPageScraping = true;
		Document searchResultsPage = Jsoup.connect(searchString).timeout(CONNECTION_TIMEOUT_VALUE).get();
		Element nextPageLink = searchResultsPage.select(".listPager a:contains(次へ)").last();
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		ArrayList<String> pagesVisited = new ArrayList<>();
		while(firstPageScraping || nextPageLink != null)
		{
		nextPageLink = searchResultsPage.select(".listPager a:contains(次へ)").last();
		String currentPageURL = searchResultsPage.baseUri();
		String nextPageURL = "";
		if(nextPageLink != null)
			nextPageURL = nextPageLink.attr("href");
		pagesVisited.add(currentPageURL);
		//I can probably combine this into one selector, but it wasn't working when I tried it,
		//so for now I'm making each its own variable and looping through and adding in all the elements seperately
		Elements dvdLinks = searchResultsPage
				.select("div.cmnList div.list a[href*=/product/detail/]:has(.PH)");
		
		// Get the link
		for (int i = 0; i < dvdLinks.size(); i++) {
			String currentLink = dvdLinks.get(i).attr("href");
			Element imageLinkElement = dvdLinks.get(i).select("img").first();
			if(imageLinkElement != null)
			{
				Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("src"));
				searchResults.add(new SearchResult("http://www.eic-book.com" + currentLink, "", currentPosterThumbnail));
			}
			else
			{
				searchResults.add(new SearchResult("http://www.eic-book.com" + currentLink));
			}
		}
		firstPageScraping = false;
		//get the next page of search results (if it exists) using the "next page" link, but only if we haven't visited that page before
		//TODO this is really not the cleanest way of doing this - I can probably find some way to make the selector not send me in a loop
		//of pages, but this will work for now
		if(nextPageLink != null && !pagesVisited.contains(nextPageURL))
			searchResultsPage = Jsoup.connect(nextPageURL).get();
		else
			break;
		
		}
		
		return searchResults.toArray(new SearchResult[searchResults.size()]);
	}
	
	public SearchResult[] getSearchResultsWithoutDVDLinks(String dmmSearchString) throws IOException {
		SearchResult[] allSearchResult = getSearchResults(dmmSearchString);
		List<SearchResult> filteredSearchResults = new LinkedList<SearchResult>();
		for(SearchResult currentSR : allSearchResult)
		{
			System.out.println("current SR = " + currentSR.getUrlPath());
			if(!currentSR.getUrlPath().contains("/product/detail/"))
				filteredSearchResults.add(currentSR);
		}
		
		return filteredSearchResults.toArray(new SearchResult[filteredSearchResults.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		if(super.isExtraFanartScrapingEnabled())
			return scrapePostersAndFanart(false, true);
		else return new Thumb[0];
	}
	
	@Override
	public String toString(){
		return "eic-book.com";
	}

	@Override
	public SiteParsingProfile newInstance() {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
		return new EICBookParsingProfile(!preferences.getScrapeInJapanese());
	}

	@Override
	public String getParserName() {
		return "EIC-Book.com";
	}
}