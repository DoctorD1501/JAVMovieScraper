package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
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
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class HeyzoParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private boolean scrapeInEnglish;
	private String englishPage;
	private String japanesePage;
	
	Document japaneseDocument;
	
	@Override
	public String getParserName() {
		return "HEYZO";
	}
	
	public HeyzoParsingProfile()
	{
		scrapeInEnglish = true;
	}
	
	public HeyzoParsingProfile(boolean scrapeInEnglish)
	{
		this.scrapeInEnglish = scrapeInEnglish;
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div#movie h1").first();
		if(titleElement != null)
		{
			String titleElementText = titleElement.text().trim().replaceAll("[ ]+", " ");
			return new Title(titleElementText);
		}
		return null;
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		if(!scrapeInEnglish)
			return new OriginalTitle(scrapeTitle().getTitle());
		else
		{
			Document originalDocument = document;
			document = japaneseDocument;
			OriginalTitle originalTitle = new OriginalTitle(scrapeTitle().getTitle());
			document = originalDocument;
			return originalTitle;
		}
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return new SortTitle("");
	}

	@Override
	public Set scrapeSet() {
		return new Set("");
	}

	@Override
	public Rating scrapeRating() {
		Element ratingValueElement = japaneseDocument.select("span.dataInfo span[property=v:average]").first();
		if(ratingValueElement != null)
		{
			return new Rating(5.0, ratingValueElement.text().trim());
		}
		else return new Rating(0,"");
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = japaneseDocument.select("div.movieInfo span.release-day + span.dataInfo").first();
		if(yearElement != null)
		{
			//System.out.println("year = " + yearElement.text());
			String yearText = yearElement.text();
			if(yearText.length() >= 4)
			{
				yearText = yearText.substring(0,4);
				return new Year(yearText);
			}
			
		}
		return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("tbody:contains(Whole Movie File Download) tr:contains(:) td").first();
		if(runtimeElement != null)
		{
			String[] runtimeTextSplit = runtimeElement.text().trim().split((":"));
			if(runtimeTextSplit.length == 3)
			{
				int hours = Integer.parseInt(runtimeTextSplit[0]);
				int minutes = Integer.parseInt(runtimeTextSplit[1]);
				int totalMinutes = (hours * 60) + minutes;
				return new Runtime(new Integer(totalMinutes).toString());
			}
		}
		return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		ArrayList<Thumb> thumbList = new ArrayList<Thumb>();
		String scrapedId = scrapeID().getId();
		try {
			//gallery links
			for(int i = 1; i <= 21; i++)
			{
				String potentialGalleryImageURL = "http://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/0" + String.format("%02d",i) + ".jpg";
				String potentialGalleryPreviewImageURL = "http://en.heyzo.com/contents/3000/" + scrapedId + "/gallery/thumbnail_0" + String.format("%02d",i) + ".jpg";
				if(SiteParsingProfile.fileExistsAtURL(potentialGalleryImageURL))
				{
					Thumb thumbToAdd = new Thumb(potentialGalleryImageURL);
					thumbToAdd.setPreviewURL(new URL(potentialGalleryPreviewImageURL));
					thumbList.add(thumbToAdd);
						
				}
			}
			//image that is the preview of the trailer
			Thumb trailerPreviewThumb = new Thumb("http://www.heyzo.com/contents/3000/" + scrapedId + "/images/player_thumbnail_450.jpg");
			thumbList.add(trailerPreviewThumb);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return thumbList.toArray(new Thumb[thumbList.size()]);
		}
		// TODO Auto-generated method stub
		return thumbList.toArray(new Thumb[thumbList.size()]);
	}

	@Override
	public Trailer scrapeTrailer(){
		String scrapedId = scrapeID().getId();
		String trailerURL = "http://sample.heyzo.com/contents/3000/" + scrapedId + "/heyzo_hd_0194_sample.mp4";
		if(SiteParsingProfile.fileExistsAtURL(trailerURL))
			return new Trailer(trailerURL);
		return new Trailer("");
	}
	
	@Override
	public Thumb[] scrapeFanart() {
		return scrapePosters();
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return scrapePosters();
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		//Just get the ID from the page URL by doing some string manipulation
		String baseUri = document.baseUri();
		if(baseUri.length() > 0 && baseUri.contains("heyzo.com"))
		{
			baseUri = baseUri.replaceFirst("/index.html", "");
			String idFromBaseUri = baseUri.substring(baseUri.lastIndexOf('/')+1);
			return new ID(idFromBaseUri);
		}
		return null;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Elements genreElements = document.select("div.movieInfo a[href*=/listpages/category");
		if(genreElements != null)
		{
			for(Element currentGenre : genreElements)
			{
				if(currentGenre.text().trim().length() > 0)
					genreList.add(new Genre(currentGenre.text().trim()));
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("div.movieInfo span.dataInfo a[href*=/listpages/actor");
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		for(Element currentActor : actorElements)
		{
			String actorName = currentActor.text().trim();
			String actorHref = currentActor.attr("href");
			String actorNumber = null;
			String actorThumbUrl = null;
			if(actorHref != null && actorHref.length() > 0)
			{
				String [] splitHrefByUnderScore = actorHref.split("_");
				if(splitHrefByUnderScore.length > 0)
				{
					actorNumber = splitHrefByUnderScore[1];
					actorThumbUrl = "http://en.heyzo.com/actorprofile/3000/" + String.format("%04d", Integer.parseInt(actorNumber)) + "/profile.jpg";
				}
			}
			//we found a thumbnail image for this actor
			if(actorThumbUrl != null && SiteParsingProfile.fileExistsAtURL(actorThumbUrl))
			{
				try {
					actorList.add(new Actor(actorName, "", new Thumb(actorThumbUrl)));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					actorList.add(new Actor(actorName, "", null));
				}
			}
			//we didn't find a thumbnail image for this actor
			else
			{
				actorList.add(new Actor(actorName, "", null));
			}	
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		// TODO Auto-generated method stub
		return new Studio("HEYZO");
	}

	@Override
	public String createSearchString(File file) {
		String fileID = findIDTagFromFile(file).toLowerCase();

		if (fileID != null) {
			
			englishPage = "http://en.heyzo.com/moviepages/" + fileID + "/index.html";
			japanesePage = "http://www.heyzo.com/moviepages/" + fileID + "/index.html";
			try {
				japaneseDocument = Jsoup.connect(japanesePage).timeout(0).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(scrapeInEnglish)
			{
				return englishPage;
			}
			else
			{
				return japanesePage;
			}
		}

		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		SearchResult[] searchResultArray = {searchResult};
		return searchResultArray;
	}
	
	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}
	
	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{4}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

}
