package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
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

public class ActionJavParsingProfile extends SiteParsingProfile {

	public ActionJavParsingProfile(Document document) {
		super(document);
	}

	public ActionJavParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Title scrapeTitle() {

		Element titleElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Video Title) td ~ td p")
				.first();
		return new Title(titleElement.text());
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		// ActionJav doesn't have the Japanese title, so we don't want to return
		// anything but a blank text element
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
		// ActionJav doesn't have any set information
		return new Set("");

	}

	@Override
	public Rating scrapeRating() {
		Element ratingElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Rated) td ~ td p img")
				.first();
		String ratingImgUrl = ratingElement.attr("src");
		String ratingIntegerIntegerPart = ratingImgUrl.substring(
				ratingImgUrl.length() - 7, ratingImgUrl.length() - 6);
		String ratingDecimalIntegerPart = ratingImgUrl.substring(
				ratingImgUrl.length() - 5, ratingImgUrl.length() - 4);
		return new Rating(5.0, ratingIntegerIntegerPart + "."
				+ ratingDecimalIntegerPart);
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Date Added) td ~ td p")
				.first();
		String yearText = yearElement.text().toString().trim();
		yearText = yearText.substring(yearText.length() - 4);
		return new Year(yearText);
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on ActionJav
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		Element votesElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Rated) td ~ td p font")
				.first();
		String votes = votesElement.text();
		votes = votes.substring(2,votes.indexOf('v')-1);
		return new Votes(votes);
	}

	@Override
	public Outline scrapeOutline() {
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {


		Element plotElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table[width=372] tbody tr td table tbody tr td p[align=left] font[color=696981]")
				.first();
		if (plotElement != null)
			return new Plot(plotElement.text().toString());
		else
			return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
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
		ArrayList<String> movieFileName = new ArrayList<String>(
				movieDownloadParts.size());
		Hashtable<String, Integer> runtimesByPart = new Hashtable<String, Integer>(
				movieDownloadParts.size());
		// we got to do some processing to get the unique runtime per part,
		// ignoring file extension
		for (Element movieElement : movieDownloadParts) {
			// get the filename without extension
			// System.out.println("movieElement: " + movieElement);
			Element movieElementLink = movieElement.select("a").first();
			if(movieElementLink != null)
			{
			String filePath = movieElementLink.attr("href");
			String[] splitBySlash = filePath.split("/");
			//get just the file
			String fileNameNoExtension = splitBySlash[splitBySlash.length - 1]; 
																				
			fileNameNoExtension = fileNameNoExtension.substring(0,
					fileNameNoExtension.length() - 4); // strip the extension
			
			movieFileName.add(filePath);

			// get the runtime
			String runtimeText = movieElement.select("font").last().text();
																			
			//get whole text element
			Integer runtimeAmt = new Integer(runtimeText.substring(1, runtimeText.indexOf('m')-1)); 
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
			return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		String coverLink = document.select("a[href$=&console=cover]").first()
				.attr("abs:href");
		try {
			Document coverPage = Jsoup.connect(coverLink).timeout(0).get();
			Element posterImg = coverPage.select(
					"img[src*=/web_img/covers_hires_full/]").first();
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
		String coverLink = document.select("a[href$=&console=cover]").first()
				.attr("abs:href");
		try {
			Document coverPage = Jsoup.connect(coverLink).timeout(0).get();
			Element posterImg = coverPage.select(
					"img[src*=/web_img/covers_hires_full/]").first();
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
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Element idElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Publisher ID) td ~ td p")
				.first();
		
		if(idElement != null)
		{
			String idElementText = idElement.text();
			int firstNumberIndex = StringUtils.indexOfAny(idElementText, "0123456789");
			idElementText = idElementText.substring(0,firstNumberIndex) + "-" + idElementText.substring(firstNumberIndex);
			return new ID(idElementText);
		}
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Elements genreElements = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Fetishes) td ~ td p");
		ArrayList<Genre> genreList = new ArrayList<Genre>(genreElements.size());
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
		Elements actorElements = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Starring) td ~ td p");
		if(actorElements != null)
		{
		ArrayList<Actor> actorList = new ArrayList<Actor>(actorElements.size());
		try {

			for (Element actorElement : actorElements) {
				String currentActorName = actorElement.select("font").first()
						.text();
				String currentActorDetailFileNameURL = actorElement.select("a")
						.attr("href");
				currentActorDetailFileNameURL = currentActorDetailFileNameURL
						.substring(currentActorDetailFileNameURL.indexOf('=') + 1);
				currentActorDetailFileNameURL = "http://images2.tsunami-ent.com/web_img/av_idols_300/"
						+ currentActorDetailFileNameURL + ".jpg";
				Actor currentActor = new Actor(currentActorName, "", new Thumb(
						currentActorDetailFileNameURL));
				actorList.add(currentActor);
			}
			return actorList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return new ArrayList<Actor>();
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//ActionJav doesn't have director information, so just return an empty list
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document
				.select("body table tbody tr td table tbody tr td div table tbody tr td table tbody tr td table tbody tr:contains(Publisher) td ~ td p")
				.first();
		return new Studio(studioElement.text());
	}

	@Override
	public String createSearchString(File file) {
		String fileNameNoExtension = findIDTagFromFile(file);
		return fileNameNoExtension;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		return getLinksFromGoogle(searchString, "actionjav.com/title.cfm?iid=");
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		//No extrafanart from ActionJav, for now
		return new Thumb[0];
	}
	
	public String toString(){
		return "ActionJav";
	}

}
