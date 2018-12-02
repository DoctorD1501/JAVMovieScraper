package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

public class ActionJavParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private static final SimpleDateFormat actionJavReleaseDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

	@Override
	public Title scrapeTitle() {

		Element titleElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Video Title) td ~ td p").first();
		return new Title(titleElement.text());
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		// ActionJav doesn't have the Japanese title, so we don't want to return
		// anything but a blank text element
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
		// ActionJav doesn't have any set information
		return Set.BLANK_SET;

	}

	@Override
	public Rating scrapeRating() {
		Element ratingElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Rated) td ~ td p img").first();
		if (ratingElement != null) {
			String ratingImgUrl = ratingElement.attr("src");
			String ratingIntegerIntegerPart = ratingImgUrl.substring(ratingImgUrl.length() - 7, ratingImgUrl.length() - 6);
			String ratingDecimalIntegerPart = ratingImgUrl.substring(ratingImgUrl.length() - 5, ratingImgUrl.length() - 4);
			return new Rating(5.0, ratingIntegerIntegerPart + "." + ratingDecimalIntegerPart);
		}
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Date Added) td ~ td p").first();
		if (releaseDateElement != null && releaseDateElement.text().length() > 4) {
			String releaseDateText = releaseDateElement.text().trim();
			if (!Character.isAlphabetic(releaseDateText.charAt(0))) //fix for weird white space trim() is not getting rid of
				releaseDateText = releaseDateText.substring(1);
			if (releaseDateText.length() > 4)
				return new ReleaseDate(releaseDateText.trim(), actionJavReleaseDateFormat);
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
		Element votesElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Rated) td ~ td p font").first();
		if (votesElement != null) {
			String votes = votesElement.text();
			votes = votes.substring(2, votes.indexOf('v') - 1);
			return new Votes(votes);
		}
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {

		Element plotElement = document
		        .select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table[width=372] tbody tr td table tbody tr td p[align=left] font[color=696981]").first();
		if (plotElement != null)
			return new Plot(plotElement.text().toString());
		else
			return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		// Find text elements that contain the word "min"
		// We might get some duplicates here if the movie is offered in multiple
		// codecs
		// but we can do some filtering later on to fix things by using a
		// HashTable to take care of the duplicate format problems
		Elements movieDownloadParts = document
		        .select("html body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr td table tbody tr td p:has(font:containsOwn(min, ))");
		ArrayList<String> movieFileName = new ArrayList<>(movieDownloadParts.size());
		Hashtable<String, Integer> runtimesByPart = new Hashtable<>(movieDownloadParts.size());
		// we got to do some processing to get the unique runtime per part,
		// ignoring file extension
		for (Element movieElement : movieDownloadParts) {
			// get the filename without extension
			// System.out.println("movieElement: " + movieElement);
			Element movieElementLink = movieElement.select("a").first();
			if (movieElementLink != null) {
				String filePath = movieElementLink.attr("href");
				String[] splitBySlash = filePath.split("/");
				//get just the file
				String fileNameNoExtension = splitBySlash[splitBySlash.length - 1];

				fileNameNoExtension = fileNameNoExtension.substring(0, fileNameNoExtension.length() - 4); // strip the extension

				movieFileName.add(filePath);

				// get the runtime
				String runtimeText = movieElement.select("font").last().text();

				//get whole text element
				Integer runtimeAmt = new Integer(runtimeText.substring(1, runtimeText.indexOf('m') - 1));
				//narrow it down to just the numeric part since we want to ignore the other garbage in the string
				runtimesByPart.put(fileNameNoExtension, runtimeAmt);
			}
		}
		int totalRuntime = 0;
		// Our hastable has automatically taken care of the duplicate format
		// problem with listing each runtime part twice
		for (Integer uniqueRuntime : runtimesByPart.values()) {
			totalRuntime += uniqueRuntime.intValue();
		}

		if (totalRuntime != 0) {
			return new Runtime(Integer.toString(totalRuntime));
		} else
			return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		try {
			Element posterImg = document.select("img[src*=/web_img/covers_hires_full/]").first();
			//Thumb coverImageCrop = new Thumb(posterImg.attr("src"), 52.7, 0, 0,0);
			Thumb coverImageCrop = new Thumb(posterImg.attr("src"), true);
			//ActionJav has back and front cover in one jpg, so we need to crop to just get the movie poster
			Thumb[] returnResult = new Thumb[1];
			returnResult[0] = coverImageCrop;
			return returnResult;
		} catch (IOException e) {
			e.printStackTrace();
			return new Thumb[0];
		}
	}

	@Override
	public Thumb[] scrapeFanart() {
		try {
			Element posterImg = document.select("img[src*=/web_img/covers_hires_full/]").first();
			Thumb coverImageCrop = new Thumb(posterImg.attr("src"));
			Thumb[] returnResult = new Thumb[1];
			returnResult[0] = coverImageCrop;
			return returnResult;
		} catch (IOException e) {
			e.printStackTrace();
			return new Thumb[0];
		}
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Publisher ID) td ~ td p").first();

		if (idElement != null) {
			String idElementText = idElement.text();
			int firstNumberIndex = StringUtils.indexOfAny(idElementText, "0123456789");
			idElementText = idElementText.substring(0, firstNumberIndex) + "-" + idElementText.substring(firstNumberIndex);
			return new ID(idElementText);
		} else
			return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Fetishes) td ~ td p");
		ArrayList<Genre> genreList = new ArrayList<>(genreElements.size());
		for (Element genreElement : genreElements) {
			String genre = genreElement.select("a").first().attr("href");
			genre = genre.substring(genre.indexOf('=') + 1);
			genre = genre.replaceAll("_", " ");
			genre = WordUtils.capitalizeFully(genre);
			genreList.add(new Genre(genre));

		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Starring) td ~ td p");
		if (actorElements != null) {
			ArrayList<Actor> actorList = new ArrayList<>(actorElements.size());
			try {

				for (Element actorElement : actorElements) {
					String currentActorName = actorElement.select("font").first().text();
					String currentActorDetailFileNameURL = actorElement.select("a").attr("href");
					currentActorDetailFileNameURL = currentActorDetailFileNameURL.substring(currentActorDetailFileNameURL.indexOf('=') + 1);
					currentActorDetailFileNameURL = "http://images2.tsunami-ent.com/web_img/av_idols_300/" + currentActorDetailFileNameURL + ".jpg";
					Actor currentActor = new Actor(currentActorName, "", new Thumb(currentActorDetailFileNameURL));
					actorList.add(currentActor);
				}
				return actorList;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<>();
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//ActionJav doesn't have director information, so just return an empty list
		return new ArrayList<>();
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Publisher) td ~ td p").first();
		return new Studio(studioElement.text());
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String idTag = findIDTagFromFile(file, isFirstWordOfFileIsID());
		if (idTag != null)
			return "http://www.actionjav.com/results_title.cfm?sortby=pub_idu&direction=ASC&searchterm=" + idTag.replace("-", "");

		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		if (searchString == null)
			return new SearchResult[0];

		LinkedList<SearchResult> searchItems = new LinkedList<>();
		String searchId = searchString.replaceAll(".*searchterm=(\\D+)(\\d+)", "$1-$2").toUpperCase();
		Document doc = Jsoup.connect(searchString).timeout(CONNECTION_TIMEOUT_VALUE).get();
		Elements rows = doc.select("table table table tr:has(a[href^=title.cfm?iid=])");

		for (Element row : rows) {
			String id = row.select("td:nth-child(2)").first().text().replaceAll("(\\D+)(\\d+)", "$1-$2").toUpperCase();
			Element link = row.select("a[href^=title.cfm?iid=]").first();
			Element actress = row.select("a[href^=model.cfm?actress_filename=]").first();
			String title = "[" + id + "] " + link.text();
			if (actress != null)
				title = title + " - " + actress.ownText();

			String url = "http://www.actionjav.com/" + link.attr("href");
			SearchResult result = new SearchResult(url, title);

			if (id.equals(searchId))
				searchItems.addFirst(result);
			else
				searchItems.addLast(result);
		}

		return searchItems.toArray(new SearchResult[searchItems.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> imageList = new ArrayList<>();

		Element script = document.select("head > script:nth-of-type(2)").first();
		if (script != null) {
			String data = script.data();
			Pattern pattern = Pattern.compile("\"(http://images2.tsunami-ent.com/web_img/.*\\.jpg)\"");
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {
				try {
					imageList.add(new Thumb(matcher.group(1)));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		return imageList.toArray(new Thumb[imageList.size()]);
	}

	@Override
	public String toString() {
		return "ActionJav";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new ActionJavParsingProfile();
	}

	@Override
	public String getParserName() {
		return "ActionJav";
	}

}
