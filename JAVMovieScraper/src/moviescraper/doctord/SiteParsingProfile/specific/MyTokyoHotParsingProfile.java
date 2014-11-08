package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

public class MyTokyoHotParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private boolean scrapeInEnglish = true;
	Document japaneseDocument;
	
	@Override
	public String getParserName() {
		return "MyTokyoHot";
	}
	
	/**
	 * loads up the japanese version of this page into japaneseDocument
	 */
	private void initializeJapaneseDocument()
	{
		if(document != null && japaneseDocument == null)
		{
			String url = document.baseUri().replaceFirst(Pattern.quote("lang=en"), Pattern.quote("lang=ja"));
			japaneseDocument = SpecificScraperAction.downloadDocument(url);
		}
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div.pagetitle").first();
		if(titleElement != null)
		{
			return new Title(titleElement.text().trim());
		}
		return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		initializeJapaneseDocument();
		Element originalTitleElement = japaneseDocument.select("div.pagetitle").first();
		if(originalTitleElement != null)
		{
			return new OriginalTitle(originalTitleElement.text().trim());
		}
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
		return new Rating(10, "");
	}

	@Override
	public Year scrapeYear() {
		Element releaseDateElement = document.select("dl.info dt:contains(Release Date) + dd, dl.info dt:contains(配信開始日) + dd").first();
		if(releaseDateElement != null && releaseDateElement.text().length() >= 4)
		{
			String yearText = releaseDateElement.text().substring(0, 4);
			return new Year(yearText);
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
		Element plotElement = document.select("div.contents div.sentence").first();
		if(plotElement != null)
			return new Plot(plotElement.text().trim());
		else return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		Element durationElement = document.select("dl.info dt:contains(Duration) + dd, dl.info dt:contains(収録時間) + dd").first();
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
		ArrayList<Thumb> posters  = new ArrayList<Thumb>();
		Element trailerBackgroundImage = document.select("video[poster]").first();
		if(trailerBackgroundImage != null)
		{
			String posterAttr = trailerBackgroundImage.attr("poster");
			try {
				//TODO: crop this poster
				posters.add(new Thumb(posterAttr));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Elements photoGalleryImgs = document.select("div.scap a");
		for(Element galleryImg : photoGalleryImgs)
		{
			String imgLink = galleryImg.attr("abs:href");
			try {
				Thumb imgThumb = new Thumb(imgLink);
				posters.add(imgThumb);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		return posters.toArray(new Thumb[posters.size()]);
	}
	
	@Override
	public Trailer scrapeTrailer(){
		//ArrayList<Thumb> posters  = new ArrayList<Thumb>();
		Element trailer = document.select("video source[src*=/samples/]").first();
		if(trailer != null)
		{
			String trailerSrc = trailer.attr("src");
			return new Trailer(trailerSrc);
		}
		return new Trailer("");
	}

	@Override
	public Thumb[] scrapeFanart() {
		//TODO: get all the other posters on the page
		return scrapePosters();
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		//TODO: get all the other posters on the page
		return scrapePosters();
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select("dl.info dt:contains(Product ID) + dd, dl.info dt:contains(作品番号) + dd").first();
		if(idElement != null && idElement.text().length() > 0)
			return new ID(idElement.text().trim());
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Elements genreElements = document.select("dl.info dt:contains(Category) + dd a, dl.info dt:contains(カテゴリ) + dd a");
		for(Element currentGenre : genreElements)
		{
			genreList.add(new Genre(currentGenre.text().trim()));
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Elements actressElements = document.select("dl.info dt:contains(Actress) + dd a, dl.info dt:contains(出演者) + dd a");
		for(Element currentActress : actressElements)
		{
			String name = currentActress.text();
			String href = currentActress.attr("href");
			href = href.replaceAll(Pattern.quote("/cast/"), "");
			href = href.replaceAll("/", "");
			//now href is just the numerical number of this actor
			String thumbnailLink = "http://my.cdn.tokyo-hot.com/media/cast/" + href + "/thumbnail.jpg";
			if(SiteParsingProfile.fileExistsAtURL(thumbnailLink))
			{
				try {
					actorList.add(new Actor(name, "", new Thumb(thumbnailLink)));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					actorList.add(new Actor(name, "", null));
				}
			}
			else
			{
				actorList.add(new Actor(name, "", null));
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
		return new Studio("Tokyo-Hot");
	}

	@Override
	public String createSearchString(File file) {
		String fileID = findIDTagFromFile(file).toLowerCase();
		if(fileID == null)
			return null;
		String searchURL = "http://my.tokyo-hot.com/product/?q=" + fileID + "&x=0&y=0";
		return searchURL;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		if(searchString == null)
			return null;
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		Elements movieElements = doc.select("ul.list.slider.cf li.detail");
		SearchResult[] searchResults = new SearchResult[movieElements.size()];
		int indexNum = 0;
		final String languageSuffixEnglish = "?lang=en";
		final String languageSuffixJapanese = "?lang=ja";
		String languageSuffixToUse = "";
		if(scrapeInEnglish)
			languageSuffixToUse = languageSuffixEnglish;
		else
			languageSuffixToUse = languageSuffixJapanese;
		if(scrapeInEnglish)
		for(Element movie : movieElements)
		{
			SearchResult currentSearchResult = null;
			String urlPath = movie.select("a.rm").attr("abs:href") + languageSuffixToUse;
			String label = movie.select("div.title").first().text();
			String imageSrc = movie.select("a.rm img").attr("src");
			if(imageSrc != null && imageSrc.length() > 0)
			{
				currentSearchResult = new SearchResult(urlPath, label, new Thumb(imageSrc));
			}
			else
			{
				currentSearchResult = new SearchResult(urlPath, label);
			}
			searchResults[indexNum] = currentSearchResult;
			indexNum++;
		}
		return searchResults;
	}
	
	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}
	
	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[kn][0-9]{3,4}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

}
