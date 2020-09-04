package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moviescraper.doctord.controller.languagetranslation.JapaneseCharacter;
import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.languagetranslation.TranslateString;
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

import moviescraper.doctord.scraper.UserAgent;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DmmParsingProfile extends SiteParsingProfile implements SpecificProfile {

	final static double dmmMaxRating = 5.00;
	private boolean doEnglishVersion;
	private boolean scrapeTrailers;

	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
	}

	public DmmParsingProfile() {
		super();
		doEnglishVersion = (scrapingLanguage == Language.ENGLISH);

		// we can skip trailer scraping if user disables write trailer preference
		scrapeTrailers = MoviescraperPreferences.getInstance().getWriteTrailerToFile();
	}

	public DmmParsingProfile(Document document) {
		super(document);
		doEnglishVersion = (scrapingLanguage == Language.ENGLISH);
	}

	/**
	 * Default constructor does not define a document, so be careful not to call
	 * scrape methods without initializing the document first some other way.
	 * This constructor is mostly used for calling createSearchString() and
	 * getSearchResults()
	 */
	public DmmParsingProfile(boolean doGoogleTranslation) {
		super();
		this.doEnglishVersion = doGoogleTranslation;
		if (this.doEnglishVersion == false)
			setScrapingLanguage(Language.JAPANESE);

		// we can skip trailer scraping if user disables write trailer preference
		scrapeTrailers = MoviescraperPreferences.getInstance().getWriteTrailerToFile();
	}

	public DmmParsingProfile(boolean doGoogleTranslation, boolean scrapeTrailers) {
		super();
		this.doEnglishVersion = doGoogleTranslation;
		if (this.doEnglishVersion == false)
			setScrapingLanguage(Language.JAPANESE);
		this.scrapeTrailers = scrapeTrailers;
	}

	public DmmParsingProfile(Document document, boolean doGoogleTranslation) {
		super(document);
		this.doEnglishVersion = doGoogleTranslation;
		if (this.doEnglishVersion == false)
			setScrapingLanguage(Language.JAPANESE);
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("[property=og:title]").first();
		;
		String title = titleElement.attr("content").toString();
		return new Title(title);
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		if (doEnglishVersion) {
			//English website does not have original Japanese title
			return OriginalTitle.BLANK_ORIGINALTITLE;
		}

		Element titleElement = document.select("[property=og:title]").first();
		return new OriginalTitle(titleElement.attr("content").toString());
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("table.mg-b20 tr td a[href*=article=series/id=]").first();
		;

		if (setElement == null)
			return Set.BLANK_SET;
		else
			return new Set(setElement.text());
	}

	@Override
	public Rating scrapeRating() {
		Element ratingElement = document.select(".d-review__average strong").first();
		if (ratingElement != null)
			return new Rating(dmmMaxRating, ratingElement.text().replace("点", ""));
		else
			return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement;
		if (doEnglishVersion) {
			releaseDateElement = document.select("table.mg-b20 tr td:contains(A sale date:) + td").first();
		} else {
			releaseDateElement = document.select("table.mg-b20 tr td:contains(貸出開始日：) + td, table.mg-b20 tr td:contains(発売日：) + td, table.mg-b20 tr td:contains(商品発売日：) + td").first();
		}
		if (releaseDateElement != null) {
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
		Element votesElement = document.select(".d-review__evaluates strong").first();
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
		if (plotElement == null || document.baseUri().contains("/digital/video")) {
			//video rental mode if it didnt find a match using above method
			plotElement = document.select("tbody .mg-b20.lh4").first();
		}

		String plot = plotElement.text();

		//remove special sale messages that occur after first "star" character
		plot = plot.split("★", 2)[0];
		return new Plot(plot);
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public moviescraper.doctord.model.dataitem.Runtime scrapeRuntime() {
		String runtime = "";
		Element runtimeElement = document.select("table.mg-b20 tr td:contains(収録時間：) + td").first();
		if (runtimeElement != null) {
			// get rid of japanese word for minutes and just get the number
			runtime = runtimeElement.text().replaceAll("分", "");
		}
		return new moviescraper.doctord.model.dataitem.Runtime(runtime);
	}

	@Override
	public Trailer scrapeTrailer() {
		try {
			//we can return no trailers if scraping trailers is not enabled or the page we are scraping does not have a button to link to the trailer
			Element buttonElement;

			if (scrapeTrailers && (buttonElement = document.select("a.d-btn[onclick*=sampleplay]").first()) != null) {
				System.out.println("There should be a trailer, searching now...");

				// First, scrape the contents of the 'play trailer' button action. It's a small ajax document containing
				// an iframe that hosts the flash video player. Then scrape that iframe contents obtaining trailer information.

				String playerPath = buttonElement.attr("onclick").replaceFirst("^.*sampleplay\\('([^']+).*$", "$1");
				playerPath = StringEscapeUtils.unescapeJava(playerPath);
				URL playerURL = new URI(document.location()).resolve(playerPath).toURL();
				Document playerDocument = Jsoup.parse(playerURL, CONNECTION_TIMEOUT_VALUE);
				URL iframeURL = new URL(playerDocument.select("iframe").first().attr("abs:src"));
				Document iframeDocument = Jsoup.parse(iframeURL, CONNECTION_TIMEOUT_VALUE);
				String flashPlayerScript = iframeDocument.select("script").last().data();
				Pattern pattern = Pattern.compile(".*flashvars.fid\\s*=\\s*\"([^\"]+).*flashvars.bid\\s*=\\s*\"(\\d)(w|s)\".*", Pattern.DOTALL);
				Matcher matcher = pattern.matcher(flashPlayerScript);

				if (matcher.matches()) {
					String cid = matcher.group(1);
					int bitrates = Integer.parseInt(matcher.group(2));
					String ratio = matcher.group(3);
					String quality = (bitrates & 0b100) != 0 ? "dmb" : (bitrates & 0b010) != 0 ? "dm" : "sm";
					String firstLetterOfCid = cid.substring(0, 1);
					String threeLetterCidCode = cid.substring(0, 3);

					String potentialTrailerURL = String.format("https://cc3001.dmm.co.jp/litevideo/freepv/%1$s/%2$s/%3$s/%3$s_%4$s_%5$s.mp4", firstLetterOfCid, threeLetterCidCode, cid, quality,
					        ratio);

					if (SiteParsingProfile.fileExistsAtURL(potentialTrailerURL)) {
						System.out.println("Trailer existed at: " + potentialTrailerURL);
						return new Trailer(potentialTrailerURL);
					}
				}

				System.err.println("I expected to find a trailer and did not at " + document.location());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Trailer.BLANK_TRAILER;
	}

	@Override
	public Thumb[] scrapePosters() {
		//don't crop the cover for videoc elements as it is a website release and does not have dvd art
		if (document.baseUri().contains("/digital/videoc"))
			return scrapePostersAndFanart(false, false);
		else
			return scrapePostersAndFanart(true, false);
	}

	/**
	 * Helper method for scrapePoster() and scapeFanart since this code is
	 * virtually identical
	 *
	 * @param doCrop
	 * - if true, will only get the front cover as the initial poster
	 * element; otherwise it uses the entire dvd case from DMM.co.jp
	 * @return Thumb[] containing all the scraped poster and extraart (if doCrop
	 * is true) or the cover and back in extraart (if doCrop is false)
	 */
	private Thumb[] scrapePostersAndFanart(boolean doCrop, boolean scrapingExtraFanart) {

		// the movie poster, on this site it usually has both front and back
		// cover joined in one image
		Element postersElement = document.select("a[name=package-image], div#sample-video img[src*=/pics.dmm.co.jp]").first();
		// the extra screenshots for this movie. It's just the thumbnail as the
		// actual url requires javascript to find.
		// We can do some string manipulation on the thumbnail URL to get the
		// full URL, however
		Elements extraArtElementsSmallSize = document.select("div#sample-image-block img.mg-b6");

		ArrayList<Thumb> posters = new ArrayList<>(1 + extraArtElementsSmallSize.size());
		String posterLink = postersElement.attr("abs:href");
		if (posterLink == null || posterLink.length() < 1)
			posterLink = postersElement.attr("abs:src");
		try {
			// for the poster, do a crop of the the right side of the dvd case image (which includes both cover art and back art) so we only get the cover
			if (doCrop && !scrapingExtraFanart)
				//use javCropCoverRoutine version of the new Thumb constructor to handle the cropping
				posters.add(new Thumb(posterLink, true));
			else if (!scrapingExtraFanart)
				posters.add(new Thumb(posterLink));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (scrapingExtraFanart) {
			// maybe you're someone who doesn't want the movie poster as the cover.
			// Include the extra art in case
			// you want to use one of those
			for (Element item : extraArtElementsSmallSize) {

				// We need to do some string manipulation and put a "jp" before the
				// last dash in the URL to get the full size picture
				String extraArtLinkSmall = item.attr("abs:src");
				int indexOfLastDash = extraArtLinkSmall.lastIndexOf('-');
				String URLpath = extraArtLinkSmall.substring(0, indexOfLastDash) + "jp" + extraArtLinkSmall.substring(indexOfLastDash);
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
		Element idElement;
		if (doEnglishVersion) {
			idElement = document.select("td:containsOwn(Movie Number:) ~ td").first();
		} else {
			idElement = document.select("td:containsOwn(品番：) ~ td").first();
		}
		if (idElement != null) {
			String idElementText = idElement.text();
			idElementText = fixUpIDFormatting(idElementText);
			return new ID(idElementText);
		} else {
			//This page didn't have an ID, so just put in a empty one
			return ID.BLANK_ID;
		}
	}

	public static String fixUpIDFormatting(String idElementText) {
		//DMM sometimes has a letter and underscore then followed by numbers. numbers will be stripped in the next step, so let's strip out the underscore prefix part of the string
		if (idElementText.contains("_")) {
			idElementText = idElementText.substring(idElementText.indexOf('_') + 1);
		}

		//DMM sometimes includes numbers before the ID, so we're going to strip them out to use
		//the same convention that other sites use for the id number
		idElementText = idElementText.substring(StringUtils.indexOfAnyBut(idElementText, "0123456789"));
		//Dmm has everything in lowercase for this field; most sites use uppercase letters as that follows what shows on the cover so will uppercase the string
		//English locale used for uppercasing just in case user is in some region that messes with the logic of this code...
		idElementText = idElementText.toUpperCase(Locale.ENGLISH);
		//insert the dash between the text and number part
		int firstNumberIndex = StringUtils.indexOfAny(idElementText, "0123456789");
		idElementText = idElementText.substring(0, firstNumberIndex) + "-" + idElementText.substring(firstNumberIndex);

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
		if (groupOne.length() > 0 && groupTwo.length() > 0) {
			groupTwo = String.format("%03d", Integer.parseInt(groupTwo));
			return groupOne + groupTwo;
		}
		return idElementText;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document.select("table.mg-b12 tr td a[href*=article=keyword/id=]");

		ArrayList<Genre> genres = new ArrayList<>(genreElements.size());
		for (Element genreElement : genreElements) {
			// get the link so we can examine the id and do some sanity cleanup
			// and perhaps some better translation that what google has, if we
			// happen to know better
			String href = genreElement.attr("abs:href");
			String genreID = genreElement.attr("abs:href").substring(href.indexOf("id=") + 3, href.length() - 1);
			if (acceptGenreID(genreID)) {
				if (!doEnglishVersion) {
					genres.add(new Genre(genreElement.text()));
				} else {
					String potentialBetterTranslation = betterGenreTranslation(genreElement.text(), genreID);

					if (potentialBetterTranslation.equals("")) {
						// use genre found on site
						genres.add(new Genre(genreElement.text()));
					} else {
						// use our genre name
						genres.add(new Genre(potentialBetterTranslation));
					}
				}
			}
		}
		return genres;
	}

	private String betterGenreTranslation(String text, String genreID) {
		String betterGenreTranslatedString = "";
		switch (genreID) {
			case "5001":
				betterGenreTranslatedString = "Creampie";
				break;
			case "5002":
				betterGenreTranslatedString = "Fellatio";
				break;
			case "1013":
				betterGenreTranslatedString = "Nurse";
				break;
			default:
				break;
		}

		return betterGenreTranslatedString;
	}

	private String betterActressTranslation(String text, String actressID) {
		String betterActressTranslatedString = "";
		switch (actressID) {
			case "17802":
				betterActressTranslatedString = "Tsubomi";
				break;
			case "27815":
				betterActressTranslatedString = "Sakura Aida";
				break;
			case "1014395":
				betterActressTranslatedString = "Yuria Ashina";
				break;
			case "1001819":
				betterActressTranslatedString = "Emiri Himeno";
				break;
			case "1006261":
				betterActressTranslatedString = "Uta Kohaku";
				break;
			case "101792":
				betterActressTranslatedString = "Nico Nohara";
				break;
			case "1015472":
				betterActressTranslatedString = "Tia";
				break;
			case "1016186":
				betterActressTranslatedString = "Yuko Shiraki";
				break;
			case "1009910":
				betterActressTranslatedString = "Hana Nonoka";
				break;
			case "1016458":
				betterActressTranslatedString = "Eve Hoshino";
				break;
			case "1019676":
				betterActressTranslatedString = "Rie Tachikawa";
				break;
			case "1017201":
				betterActressTranslatedString = "Meisa Chibana";
				break;
			case "1018387":
				betterActressTranslatedString = "Nami Itoshino";
				break;
			case "1014108":
				betterActressTranslatedString = "Juria Tachibana";
				break;
			case "1016575":
				betterActressTranslatedString = "Chika Kitano";
				break;
			case "24489":
				betterActressTranslatedString = "Chichi Asada";
				break;
			case "20631":
				betterActressTranslatedString = "Mitsuki An";
				break;
			default:
				break;

		}
		if (betterActressTranslatedString.equals("")) {
			return text;
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
			case "6529": // "DVD Toaster" WTF is this? Nuke it!
				return false;
			case "6102": // "Sample Video" This is not a genre!
				return false;
			default:
				break;
		}
		return true;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {

		boolean doDmmActressScraping = MoviescraperPreferences.getInstance().getScrapeDmmActressPref();
		if (!doDmmActressScraping) {
			System.out.println("DMM Scraper: Skipping actress scraping. (see Scraper's Setting)");
			return (new ArrayList<>());
		}

		// scrape all the actress IDs
		Elements actressIDElements = document.select("span#performer a[href*=article=actress/id=]");

		if (actressIDElements.size() < 1) {
			System.out.println("DMM Scraper: No actress found.");
			return (new ArrayList<>());
		}

		//setup cookies and user agent for Jsoup
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put("age_check_done", "1");

		String actressPageURL;
		if (doEnglishVersion) {
			actressPageURL = "https://actress.dmm.co.jp/en/-/detail/=/actress_id=";

			// set cookies for EN version
			cookies.put("ckcy", "2");
			cookies.put("cklg", "en");
		} else {
			actressPageURL = "https://actress.dmm.co.jp/-/detail/=/actress_id=";
		}
		ArrayList<Actor> actorList = new ArrayList<>(actressIDElements.size());

		//there maybe multiple actress. let's process each actress.
		for (Element actressIDLink : actressIDElements) {
			String actressName = actressIDLink.text();
			String actressIDHref = actressIDLink.attr("abs:href");
			String actressID = actressIDHref.substring(actressIDHref.indexOf("id=") + 3, actressIDHref.length() - 1);

			System.out.println("DMM Scraper: getting actresses from " + actressPageURL + actressID + "/");
			try {
				Document actressPage = Jsoup.connect(actressPageURL + actressID + "/")
				        //.header("Cache-Control", "no-store").header("Connection", "close")
				        .cookies(cookies).userAgent(UserAgent.getUserAgent(0)).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).post();

				Element actressThumbnailElement = actressPage.select("tr.area-av30.top td img").first();
				String actressThumbnailPath = actressThumbnailElement.attr("abs:src");

				if (doEnglishVersion) {
					actressName = betterActressTranslation(actressName, actressID);
				}

				//Sometimes DMM lists a fake under the Name "Main". It's weird and it's not a real person, so just ignore it.
				if (!actressName.equals("Main")) {
					if (!actressThumbnailPath.contains("nowprinting.gif")) {
						actorList.add(new Actor(actressName, "", new Thumb(actressThumbnailPath)));
					} else {
						actorList.add(new Actor(actressName, "", null));
					}
				}
			} catch (SocketTimeoutException e) {
				System.err.println("DMM Scraper: Cannot download from " + actressPageURL.toString() + ": Socket timed out: " + e.getLocalizedMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Get actors that are just a "Name" and have no page of their own (common on some web releases)
		//TODO Z Refactor "name only actors" to *not* use English translator service. Need examples of these.
		Elements nameOnlyActors = document.select("table.mg-b20 tr td:contains(�??�?：) + td");
		for (Element currentNameOnlyActor : nameOnlyActors) {
			String actorName = currentNameOnlyActor.text().trim();
			//for some reason, they sometimes list the age of the person after their name, so let's get rid of that
			actorName = actorName.replaceFirst("\\([0-9]{2}\\)", "");
			if (doEnglishVersion)
				actorName = TranslateString.translateJapanesePersonNameToRomaji(actorName);
			actorList.add(new Actor(actorName, "", null));
		}

		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directors = new ArrayList<>();
		Element directorElement = document.select("table.mg-b20 tr td a[href*=article=director/id=]").first();

		if (directorElement != null && directorElement.hasText()) {
			directors.add(new Director(directorElement.text(), null));
			System.out.println("DMM Scraper: Directors --> " + directorElement.text());
		} else {
			System.out.println("DMM Scraper: No director found.");
		}
		return directors;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement;

		if (doEnglishVersion) {
			studioElement = document.select("td:containsOwn(Studios:) ~ td").first();
		} else {
			studioElement = document.select("td:containsOwn(メーカー：) ~ td").first();
		}

		if (studioElement != null) {
			return new Studio(studioElement.text());
		} else
			return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());

		URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);

			String searchString;
			if (doEnglishVersion) {
				searchString = "https://www.dmm.co.jp/en/search/=/searchstr=" + fileNameURLEncoded + "/";
			} else {
				searchString = "https://www.dmm.co.jp/search/=/searchstr=" + fileNameURLEncoded + "/";
			}
			System.out.println("DMM Scraper: Search string --> " + searchString);
			return searchString;
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

		Document searchResultsPage = Jsoup.connect(searchString)
		        //.header("Cache-Control", "no-store").header("Connection", "close")
		        .userAgent(UserAgent.getUserAgent(0)).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).post();

		//did we get the no-result page?
		Element noResult = searchResultsPage.select("div.d-rst.whole.search-noresult").first();
		if (noResult != null) {
			System.out.println("DMM Scraper: No Result --> " + noResult.select("p.red").first().text());
			return null;
		}

		Element nextPageLink = searchResultsPage.select("div.list-capt div.list-boxcaptside.list-boxpagenation ul li:not(.terminal) a").last();
		ArrayList<SearchResult> searchResults = new ArrayList<>();
		ArrayList<String> pagesVisited = new ArrayList<>();
		while (firstPageScraping || nextPageLink != null) {
			nextPageLink = searchResultsPage.select("div.list-capt div.list-boxcaptside.list-boxpagenation ul li:not(.terminal) a").last();
			String currentPageURL = searchResultsPage.baseUri();
			String nextPageURL = "";
			if (nextPageLink != null)
				nextPageURL = nextPageLink.attr("abs:href");
			pagesVisited.add(currentPageURL);
			//I can probably combine this into one selector, but it wasn't working when I tried it,
			//so for now I'm making each its own variable and looping through and adding in all the elements seperately
			Elements dvdLinks = searchResultsPage.select("p.tmb a[href*=/mono/dvd/");
			Elements rentalElements = searchResultsPage.select("p.tmb a[href*=/rental/ppr/");
			Elements digitalElements = searchResultsPage.select("p.tmb a[href*=/digital/videoa/], p.tmb a[href*=/digital/videoc/]");

			//get /mono/dvd links
			for (int i = 0; i < dvdLinks.size(); i++) {
				String currentLink = dvdLinks.get(i).attr("abs:href");
				Element imageLinkElement = dvdLinks.get(i).select("img").first();
				if (imageLinkElement != null) {
					Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("abs:src"));
					searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
				} else {
					searchResults.add(new SearchResult(currentLink));
				}
			}
			//get /rental/ppr links
			for (int i = 0; i < rentalElements.size(); i++) {
				String currentLink = rentalElements.get(i).attr("abs:href");
				Element imageLinkElement = rentalElements.get(i).select("img").first();
				if (imageLinkElement != null) {
					Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("abs:src"));
					searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
				} else {
					searchResults.add(new SearchResult(currentLink));
				}
			}
			//get /digital/videoa links
			for (int i = 0; i < digitalElements.size(); i++) {
				String currentLink = digitalElements.get(i).attr("abs:href");
				System.out.println("currentLink = " + currentLink);
				Element imageLinkElement = digitalElements.get(i).select("img").first();
				if (imageLinkElement != null) {
					Thumb currentPosterThumbnail = new Thumb(imageLinkElement.attr("abs:src"));
					searchResults.add(new SearchResult(currentLink, "", currentPosterThumbnail));
				} else {
					searchResults.add(new SearchResult(currentLink));
				}
			}
			firstPageScraping = false;
			//get the next page of search results (if it exists) using the "next page" link, but only if we haven't visited that page before
			//TODO this is really not the cleanest way of doing this - I can probably find some way to make the selector not send me in a loop
			//of pages, but this will work for now
			if (nextPageLink != null && !pagesVisited.contains(nextPageURL))
				searchResultsPage = Jsoup.connect(nextPageURL)
				        //.header("Cache-Control", "no-store").header("Connection", "close")
				        .userAgent(UserAgent.getUserAgent(0)).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).post();
			else
				break;

		}

		return searchResults.toArray(new SearchResult[searchResults.size()]);
	}

	public SearchResult[] getSearchResultsWithoutDVDLinks(String dmmSearchString) throws IOException {
		SearchResult[] allSearchResult = getSearchResults(dmmSearchString);
		List<SearchResult> filteredSearchResults = new LinkedList<>();
		for (SearchResult currentSR : allSearchResult) {
			System.out.println("current SR = " + currentSR.getUrlPath());
			if (!currentSR.getUrlPath().contains("/mono/dvd/"))
				filteredSearchResults.add(currentSR);
		}

		return filteredSearchResults.toArray(new SearchResult[filteredSearchResults.size()]);

	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		if (super.isExtraFanartScrapingEnabled())
			return scrapePostersAndFanart(false, true);
		else
			return new Thumb[0];
	}

	@Override
	public String toString() {
		return "DMM.co.jp";
	}

	@Override
	public SiteParsingProfile newInstance() {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
		return new DmmParsingProfile(!preferences.getScrapeInJapanese());
	}

	@Override
	public String getParserName() {
		return "DMM.co.jp";
	}

	@Override
	public Document downloadDocument(SearchResult searchResult) {
		try {
			if (searchResult.isJSONSearchResult())
				return SiteParsingProfileJSON.getDocument(searchResult.getUrlPath());
			else {
				Map<String, String> cookies = new HashMap<String, String>();
				cookies.put("age_check_done", "1"); //setup cookie to bypass age check on DMM site

				String searchUrl;
				if (doEnglishVersion) {
					//setup cookies for getting English version
					cookies.put("ckcy", "2");
					cookies.put("cklg", "en");
					searchUrl = searchResult.getUrlPath().replace("dmm.co.jp/", "dmm.co.jp/en/");

					//Append a dummy URL parameter to see if it helps by pass server cache
					//searchUrl += "&ymmud=" + System.currentTimeMillis();

					System.out.println("DMM Scraper: getting EN version at " + searchUrl);
				} else {
					searchUrl = searchResult.getUrlPath();
					System.out.println("DMM Scraper: getting JP version at " + searchUrl);
				}

				Document document = Jsoup.connect(searchUrl).cookies(cookies)
				        //.header("Cache-Control", "no-store").header("Connection", "close")
				        .userAgent(UserAgent.getUserAgent(0)).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).post();

				String title = document.select("[property=og:title]").first().attr("content").toString();
				String plot = document.select("p.mg-b20").first().text();
				System.out.println("DMM Scraper: Title --> " + title);
				System.out.println("DMM Scraper: Plot  --> " + plot);

				if (doEnglishVersion) {
					//Sometimes we get Japanese results even though our request is for English.
					//Probably due to webserver caching. Our 2nd request might be too quick.
					if (this.hasJapanese(title)) {
						//one more attempt to scrape EN version after a small time delay
						System.out.println("DMM Scraper: Failed at getting EN version. Result is JP. Title --> " + title);
						System.out.println("DMM Scraper: waiting 5 seconds before attempting to get EN version again...");
						TimeUnit.SECONDS.sleep(5);

						System.out.println("DMM Scraper: getting EN version at " + searchUrl);
						document = Jsoup.connect(searchUrl).cookies(cookies)
						        //.header("Cache-Control", "no-store").header("Connection", "close")
						        .userAgent(UserAgent.getUserAgent(0)).ignoreHttpErrors(true).timeout(CONNECTION_TIMEOUT_VALUE).post();

						title = document.select("[property=og:title]").first().attr("content").toString();
						plot = document.select("p.mg-b20").first().text();
						System.out.println("DMM Scraper: Title --> " + title);
						System.out.println("DMM Scraper: Plot  --> " + plot);
					}
				}

				return document;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Check for Japanese characters
	 */
	private static boolean hasJapanese(CharSequence charSequence) {
		boolean hasJapanese = false;
		for (char c : charSequence.toString().toCharArray()) {
			if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA
			        || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
			        || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
				hasJapanese = true;
				break;
			}
		}

		return hasJapanese;
	}

}