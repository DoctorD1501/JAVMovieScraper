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


public class DmmComParsingProfile extends SiteParsingProfile implements SpecificProfile {

	final static double dmmMaxRating = 5.00;
	private boolean doGoogleTranslation;
	private boolean scrapeTrailers;
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}
	
	public DmmComParsingProfile()
	{
		super();
		doGoogleTranslation = (scrapingLanguage == Language.ENGLISH);
		scrapeTrailers = true;
	}
	
	public DmmComParsingProfile(Document document) {
		super(document);
		doGoogleTranslation = (scrapingLanguage == Language.ENGLISH);
	}

	/**
	 * Default constructor does not define a document, so be careful not to call
	 * scrape methods without initializing the document first some other way.
	 * This constructor is mostly used for calling createSearchString() and
	 * getSearchResults()
	 */
	public DmmComParsingProfile(boolean doGoogleTranslation) {
		super();
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
		scrapeTrailers = true;
	}

	public DmmComParsingProfile(boolean doGoogleTranslation, boolean scrapeTrailers) {
		super();
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
		this.scrapeTrailers = scrapeTrailers;
	}

	public DmmComParsingProfile(Document document, boolean doGoogleTranslation) {
		super(document);
		this.doGoogleTranslation = doGoogleTranslation;
		if(this.doGoogleTranslation == false)
			setScrapingLanguage(Language.JAPANESE);
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("[property=og:title]").first();
		// run a google translate on the japanese title
		if(doGoogleTranslation)
		{
			return new Title(
					TranslateString.translateStringJapaneseToEnglish(titleElement
							.attr("content").toString()));
		}
		else
		{
			return new Title(titleElement.attr("content").toString());
		}
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		Element titleElement = document.select("[property=og:title]").first();
		// leave the original title as the japanese title
		return new OriginalTitle(titleElement.attr("content").toString());
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select(
				"table.mg-b20 tr td a[href*=article=series/id=]").first();
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
		Element ratingElement = document.select(".d-review__average strong")
				.first();
		if (ratingElement != null)
			return new Rating(dmmMaxRating, ratingElement.text().replace("ç‚¹", ""));
		else
			return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate(){
		Element releaseDateElement = document
				.select("table.mg-b20 tr td:contains(貸出開始日：) + td, table.mg-b20 tr td:contains(発売日：) + td, table.mg-b20 tr td:contains(商品発売日：) + td")
				.first();
		if(releaseDateElement != null)
		{
			String releaseDate = releaseDateElement.text();
			//we want to convert something like 2015/04/25 to 2015-04-25 
			releaseDate = StringUtils.replace(releaseDate, "/", "-");
			return new ReleaseDate(releaseDate);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on DMM
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		Element votesElement = document.select(".d-review__evaluates strong")
				.first();
		if (votesElement != null)
			return new Votes(votesElement.text());
		else
			return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		// TODO Auto-generated method stub
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		
		//dvd mode
		Element plotElement = document.select("p.mg-b20").first();
		if(plotElement == null || document.baseUri().contains("/digital/video"))
		{
		//video rental mode if it didnt find a match using above method
			plotElement = document.select("tbody .mg-b20.lh4").first();
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
				"table.mg-b20 tr td:contains(収録時間：) + td").first();
		if (runtimeElement != null) {
			// get rid of japanese word for minutes and just get the number
			runtime = runtimeElement.text().replaceAll("分", "");
		}
		return new moviescraper.doctord.model.dataitem.Runtime(runtime);

	}
	
	@Override
	public Trailer scrapeTrailer(){
		try {
			//we can return no trailers if scraping trailers is not enabled or the page we are scraping does not have a button to link to the trailer
			Element buttonElement;
			
			if(scrapeTrailers && (buttonElement = document.select("a.d-btn[onclick*=sampleplay]").first()) != null){
				System.out.println("There should be a trailer, searching now...");
				
				// First, scrape the contents of the 'play trailer' button action. It's a small ajax document containing
				// an iframe that hosts the flash video player. Then scrape that iframe contents obtaining trailer information.
				
				String playerPath = buttonElement.attr("onclick").replaceFirst("^.*sampleplay\\('([^']+).*$", "$1");
				playerPath = StringEscapeUtils.unescapeJava(playerPath);
				URL playerURL = new URI(document.location()).resolve(playerPath).toURL();	
				Document playerDocument = Jsoup.parse(playerURL, CONNECTION_TIMEOUT_VALUE);
				URL iframeURL = new URL(playerDocument.select("iframe").first().attr("src"));
				Document iframeDocument = Jsoup.parse(iframeURL, CONNECTION_TIMEOUT_VALUE);
				String flashPlayerScript = iframeDocument.select("script").last().data();
				Pattern pattern = Pattern.compile(".*flashvars.fid\\s*=\\s*\"([^\"]+).*flashvars.bid\\s*=\\s*\"(\\d)(w|s)\".*", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(flashPlayerScript);
				
				if (matcher.matches()){
					String cid = matcher.group(1);
					int bitrates = Integer.parseInt(matcher.group(2));
					String ratio = matcher.group(3);
					String quality = (bitrates & 0b100) != 0 ? "dmb" : (bitrates & 0b010) != 0 ? "dm" : "sm";
					String firstLetterOfCid = cid.substring(0,1);
					String threeLetterCidCode = cid.substring(0,3);
					
					String potentialTrailerURL = String.format("http://cc3001.dmm.com/litevideo/freepv/%1$s/%2$s/%3$s/%3$s_%4$s_%5$s.mp4", 
				 			firstLetterOfCid, threeLetterCidCode, cid, quality, ratio);
					
					if(SiteParsingProfile.fileExistsAtURL(potentialTrailerURL)) {
						System.out.println("Trailer existed at: " + potentialTrailerURL);
						return new Trailer(potentialTrailerURL);
					}
				}
			
				System.err.println("I expected to find a trailer and did not at " + document.location());
			}		
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return Trailer.BLANK_TRAILER;
	}
	
	
	@Override
	public Thumb[] scrapePosters() {
		//don't crop the cover for videoc elements as it is a website release and does not have dvd art
		if(document.baseUri().contains("/digital/videoc"))
			return scrapePostersAndFanart(false, false);
		else return scrapePostersAndFanart(true, false);
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
				"a[href^=http://pics.dmm.com][name=package-image], div#sample-video img[src*=/pics.dmm.com]").first();
		// the extra screenshots for this movie. It's just the thumbnail as the
		// actual url requires javascript to find.
		// We can do some string manipulation on the thumbnail URL to get the
		// full URL, however
		Elements extraArtElementsSmallSize = document.select("div#sample-image-block img.mg-b6");

		ArrayList<Thumb> posters = new ArrayList<Thumb>(
				1 + extraArtElementsSmallSize.size());
		String posterLink = postersElement.attr("href");
		if(posterLink == null || posterLink.length() < 1)
			posterLink = postersElement.attr("src");
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
		if(scrapingExtraFanart)
		{
			// maybe you're someone who doesn't want the movie poster as the cover.
			// Include the extra art in case
			// you want to use one of those
			for (Element item : extraArtElementsSmallSize) {

				// We need to do some string manipulation and put a "jp" before the
				// last dash in the URL to get the full size picture
				String extraArtLinkSmall = item.attr("src");
				int indexOfLastDash = extraArtLinkSmall.lastIndexOf('-');
				String URLpath = extraArtLinkSmall.substring(0, indexOfLastDash)
						+ "jp" + extraArtLinkSmall.substring(indexOfLastDash);
				try {
					if (Thumb.fileExistsAtUrl(URLpath))
						posters.add(new Thumb(URLpath));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
		Element idElement = document.select("td:containsOwn(品番：) ~ td").first();
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
		//DMM sometimes has a letter and underscore then followed by numbers. numbers will be stripped in the next step, so let's strip out the underscore prefix part of the string
		if(idElementText.contains("_"))
		{
			idElementText = idElementText.substring(idElementText.indexOf('_')+1);
		}

		//DMM sometimes includes numbers before the ID, so we're going to strip them out to use
		//the same convention that other sites use for the id number
		idElementText = idElementText.substring(StringUtils.indexOfAnyBut(idElementText,"0123456789"));
		//Dmm has everything in lowercase for this field; most sites use uppercase letters as that follows what shows on the cover so will uppercase the string
		//English locale used for uppercasing just in case user is in some region that messes with the logic of this code...
		idElementText = idElementText.toUpperCase(Locale.ENGLISH);
		//insert the dash between the text and number part
		int firstNumberIndex = StringUtils.indexOfAny(idElementText, "0123456789");
		idElementText = idElementText.substring(0,firstNumberIndex) + "-" + idElementText.substring(firstNumberIndex);
		
		//remove extra zeros in case we get a 5 or 6 digit numerical part 
		//(For example ABC-00123 will become ABC-123)
		Pattern patternID = Pattern.compile("([0-9]*\\D+)(\\d{5,6})");
		Matcher matcher = patternID.matcher(idElementText);
		String groupOne = "";
		String groupTwo = "";
		while (matcher.find()) {
		    groupOne = matcher.group(1);
		    groupTwo = matcher.group(2);
		}
		if(groupOne.length() > 0 && groupTwo.length() > 0)
		{
			groupTwo = String.format("%03d", Integer.parseInt(groupTwo));
			return groupOne + groupTwo; 
		}
		return idElementText;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document
				.select("table.mg-b12 tr td a[href*=article=keyword/id=]");
		ArrayList<Genre> genres = new ArrayList<Genre>(genreElements.size());
		for (Element genreElement : genreElements) {
			// get the link so we can examine the id and do some sanity cleanup
			// and perhaps some better translation that what google has, if we
			// happen to know better
			String href = genreElement.attr("href");
			String genreID = genreElement.attr("href").substring(
					href.indexOf("id=") + 3, href.length() - 1);
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
		case "6529": // "DVD Toaster"
			return false;
		case "6102": // "Sample Video"
			return false;
		case "73160": // "Bluray"
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		// scrape all the actress IDs
		Elements actressIDElements = document
				.select("a[href*=article=actor/id=]");
		ArrayList<Actor> actorList = new ArrayList<Actor>(
				actressIDElements.size());
		
		// No actors have dedicated pages on DMM.com, so just scrape the base names
		Elements nameOnlyActors = document.select("a[href*=article=actor/id=]");
		for(Element currentNameOnlyActor : nameOnlyActors)
		{
			String actorName = currentNameOnlyActor.text().trim();
			//for some reason, they sometimes list the age of the person after their name, so let's get rid of that
			actorName = actorName.replaceFirst("\\([0-9]{2}\\)","");
			if(doGoogleTranslation)
				actorName = TranslateString.translateJapanesePersonNameToRomaji(actorName);
			actorList.add(new Actor(actorName, "", null));
		}

		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directors = new ArrayList<Director>();
		Element directorElement = document.select(
				"table.mg-b20 tr td a[href*=article=director/id=]").first();
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
				"table.mg-b20 tr td a[href*=article=maker/id=]").first();
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
		//System.out.println("fileNameNoExtension in DMM: " + fileNameNoExtension);
		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			//System.out.println("FileNameUrlencode = " + fileNameURLEncoded);
			return "http://www.dmm.com/search/=/searchstr="
			+ fileNameURLEncoded + "/";
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
		Element nextPageLink = searchResultsPage.select("div.list-capt div.list-boxcaptside.list-boxpagenation ul li:not(.terminal) a").last();
		ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
		ArrayList<String> pagesVisited = new ArrayList<String>();
		while(firstPageScraping || nextPageLink != null)
		{
		nextPageLink = searchResultsPage.select("div.list-capt div.list-boxcaptside.list-boxpagenation ul li:not(.terminal) a").last();
		String currentPageURL = searchResultsPage.baseUri();
		String nextPageURL = "";
		if(nextPageLink != null)
			nextPageURL = nextPageLink.attr("href");
		pagesVisited.add(currentPageURL);
		//I can probably combine this into one selector, but it wasn't working when I tried it,
		//so for now I'm making each its own variable and looping through and adding in all the elements seperately
		Elements dvdLinks = searchResultsPage
				.select("p.tmb a[href*=/mono/dvd/");
		Elements rentalElements = searchResultsPage
				.select("p.tmb a[href*=/rental/ppr/");
		Elements digitalElements = searchResultsPage
				.select("p.tmb a[href*=/digital/videoa/], p.tmb a[href*=/digital/videoc/]");
		
		//get /mono/dvd links
		for (int i = 0; i < dvdLinks.size(); i++) {
			String currentLink = dvdLinks.get(i).attr("href");
			Element imageLinkElement = dvdLinks.get(i).select("img").first();
			if(imageLinkElement != null)
			{
				Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("src"));
				searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
			}
			else
			{
				searchResults.add(new SearchResult(currentLink));
			}
		}
		//get /rental/ppr links
		for (int i = 0; i < rentalElements.size(); i++) {
			String currentLink = rentalElements.get(i).attr("href");
			Element imageLinkElement = rentalElements.get(i).select("img").first();
			if(imageLinkElement != null)
			{
				Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("src"));
				searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
			}
			else
			{
				searchResults.add(new SearchResult(currentLink));
			}
		}
		//get /digital/videoa links
		for (int i = 0; i < digitalElements.size(); i++) {
			String currentLink = digitalElements.get(i).attr("href");
			System.out.println("currentLink = " + currentLink);
			Element imageLinkElement = digitalElements.get(i).select("img").first();
			if(imageLinkElement != null)
			{
				Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("src"));
				searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
			}
			else
			{
				searchResults.add(new SearchResult(currentLink));
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
			if(!currentSR.getUrlPath().contains("/mono/dvd/"))
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
		return "dmm.com";
	}

	@Override
	public SiteParsingProfile newInstance() {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
		return new DmmComParsingProfile(!preferences.getScrapeInJapanese());
	}

	@Override
	public String getParserName() {
		return "DMM.com";
	}





}
