package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.TranslateString;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
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

public class CaribbeancomParsingProfile extends SiteParsingProfile implements
		SpecificProfile {
	
	Document japaneseDocument;
	String id;
	
	boolean useTranslationOfJapanesePageForEnglishMetadata = true;

	@Override
	public String getParserName() {
		return "Caribbeancom";
	}

	/**
	 * loads up the japanese version of this page into japaneseDocument
	 */
	private void initializeJapaneseDocument()
	{
		if(document != null && japaneseDocument == null)
		{
			String url = "http://www.caribbeancom.com/moviepages/" + id + "/index.html";
			japaneseDocument = SpecificScraperAction.downloadDocument(url);
		}
	}
	@Override
	public Title scrapeTitle() {
		Document documentToUse = document;
		Element titleElement = documentToUse.select("title").first();
		if(useTranslationOfJapanesePageForEnglishMetadata)
		{
			initializeJapaneseDocument();
			documentToUse = japaneseDocument;
			titleElement = documentToUse.select("span.movie-title h1").first();
		}
		
		if(titleElement != null)
		{
			if(useTranslationOfJapanesePageForEnglishMetadata)
			{
				return new Title(WordUtils.capitalize(TranslateString.translateStringJapaneseToEnglish(titleElement.text())));
			}
			else return new Title(titleElement.text());
		}
		return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		initializeJapaneseDocument();
		Element titleElement = japaneseDocument.select("span.movie-title h1").first();
		if(titleElement != null)
			return new OriginalTitle(titleElement.text());
		return new OriginalTitle("");
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
		initializeJapaneseDocument();
		Element stars = japaneseDocument.select("div.movie-info dl dt:contains(ユーザー評価:) ~ dd ").first();
		if(stars != null && stars.text().contains("★"))
		{
			//count the number of ★ characters, max number of stars is 5 and half stars not supported
			return new Rating(5.0, String.valueOf(stars.text().length()));
		}
		return new Rating(0,"");
	}

	@Override
	public Year scrapeYear() {
		initializeJapaneseDocument();
		Element releaseDate = japaneseDocument.select("div.movie-info dl dt:contains(配信日:) ~ dd ").first();
		if(releaseDate != null && releaseDate.text().length() >= 4)
		{
			return new Year(releaseDate.text().substring(0,4));
		}
		return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		// TODO Auto-generated method stub
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
		initializeJapaneseDocument();
		Element plotElement = japaneseDocument.select("div.movie-comment p").first();
		if(plotElement != null && plotElement.text().length() > 0)
		{
			if(useTranslationOfJapanesePageForEnglishMetadata)
				return new Plot(TranslateString.translateStringJapaneseToEnglish(plotElement.text()));
			else return new Plot(plotElement.text());
		}
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		initializeJapaneseDocument();
		Element durationElement = japaneseDocument.select("div.movie-info dl dt:contains(再生時間:) ~ dd ").first();
		if(durationElement != null && durationElement.text().trim().length() > 0)
		{
			String [] durationSplitByTimeUnit = durationElement.text().split(":");
			if(durationSplitByTimeUnit.length == 3)
			{
				int hours = Integer.parseInt(durationSplitByTimeUnit[0]);
				int minutes = Integer.parseInt(durationSplitByTimeUnit[1]);
				//we don't care about seconds
				
				int totalMinutes = (hours * 60) + minutes;
				return new Runtime(new Integer(totalMinutes).toString());
			}
		}
		return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		ID id = scrapeID();
		ArrayList<Thumb> posters = new ArrayList<Thumb>();
		if(id != null && id.getId().length() > 0)
		{
			String trailerPoster = "http://www.caribbeancom.com/moviepages/" + id.getId() + "/images/" + "l_l.jpg";
			if(SiteParsingProfile.fileExistsAtURL(trailerPoster))
			{
				try {
					posters.add(new Thumb(trailerPoster));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for(int imageNum = 1; imageNum <=5; imageNum++)
			{
				String additionalImageURLTemplate = "http://www.caribbeancom.com/moviepages/" + id.getId() + "/images/l/00" + imageNum + ".jpg";
				String additionalImageURLTemplatePreview = "http://www.caribbeancom.com/moviepages/" + id.getId() + "/images/s/00" + imageNum + ".jpg";
				if(SiteParsingProfile.fileExistsAtURL(additionalImageURLTemplate))
				{
					try {
						Thumb additionalThumb = new Thumb(additionalImageURLTemplate);
						additionalThumb.setPreviewURL(new URL(additionalImageURLTemplatePreview));
						posters.add(additionalThumb);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		return posters.toArray(new Thumb[posters.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePosters();
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		Thumb[] posters = scrapePosters();
		List<Thumb> posterList = new LinkedList<Thumb>(Arrays.asList(posters));
		if(posterList != null && posterList.size() > 0)
			posterList.remove(0);
		return posterList.toArray(new Thumb[posterList.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		//Just get the ID from the page URL by doing some string manipulation
		String baseUri = document.baseUri();
		if(baseUri.length() > 0 && baseUri.contains("caribbeancom.com"))
		{
			baseUri = baseUri.replaceFirst("/index.html", "");
			String idFromBaseUri = baseUri.substring(baseUri.lastIndexOf('/')+1);
			return new ID(idFromBaseUri);
		}
		return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		initializeJapaneseDocument();
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Elements genres = japaneseDocument.select("div.movie-info dl.movie-info-cat:contains(カテゴリー:) dd ");
		if(genres != null)
		{
			for(Element currentGenre : genres){
				if(currentGenre.text().trim().length() > 0)
					genreList.add(new Genre(TranslateString.translateStringJapaneseToEnglish(currentGenre.text().trim())));
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Element actorEnglishSearchElement = document.select("table.info_table tbody tr td.property:contains(Starring:) ~ td a").first();
		if(actorEnglishSearchElement != null)
		{
			
			String hrefText = actorEnglishSearchElement.attr("href");
			hrefText = hrefText.replaceFirst(Pattern.quote("/eng/search/"),"");
			hrefText = hrefText.replaceFirst("/[0-9].html", "");
			try {
				hrefText = URLDecoder.decode( hrefText, "UTF-8" );
				String[] actorNames = hrefText.split(",");
				for(int i = 0; i < actorNames.length; i++)
				{
					actorList.add(new Actor(actorNames[i],"",null));
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//No Director information on the site
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		return new Studio("Caribbeancom");
	}
	
	@Override
	public Trailer scrapeTrailer() {
		ID id = scrapeID();
		if(id != null && id.getId().length() > 0)
		{
			String trailerPath = "http://smovie.caribbeancom.com/sample/movies/" + id.getId() + "/sample_m.mp4";
			if(SiteParsingProfile.fileExistsAtURL(trailerPath))
				return new Trailer(trailerPath);
		}
		
		return new Trailer("");
	}

	@Override
	public String createSearchString(File file) {
		this.id = findIDTagFromFile(file);
		String englishPage = "http://en.caribbeancom.com/eng/moviepages/" + id + "/index.html";
		return englishPage;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		SearchResult englishPage = new SearchResult(searchString);
		SearchResult [] results = {englishPage};
		initializeJapaneseDocument();
		return results;
	}
	
	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}
	
	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{6}-[0-9]{3}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new CaribbeancomParsingProfile();
	}

}
