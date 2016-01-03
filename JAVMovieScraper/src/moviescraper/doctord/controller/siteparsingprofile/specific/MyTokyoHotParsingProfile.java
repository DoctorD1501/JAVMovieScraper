package moviescraper.doctord.controller.siteparsingprofile.specific;

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTokyoHotParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private boolean scrapeInEnglish = true;
	Document japaneseDocument;
	private static final SimpleDateFormat myTokyoHotReleaseDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
	
	@Override
	public String getParserName() {
		return "My Tokyo-Hot";
	}
	
	/**
	 * loads up the japanese version of this page into japaneseDocument
	 */
	private void initializeJapaneseDocument()
	{
		if(document != null && japaneseDocument == null)
		{
			String url = document.baseUri().replaceFirst(Pattern.quote("lang=en"), Pattern.quote("lang=ja"));
			japaneseDocument = downloadDocumentFromURLString(url);
		}
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = null;
		if(getScrapingLanguage() == Language.ENGLISH)
			titleElement = document.select("div.pagetitle").first();
		else if(getScrapingLanguage() == Language.JAPANESE)
		{
			initializeJapaneseDocument();
			titleElement = japaneseDocument.select("div.pagetitle").first();
		}
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
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		return new Rating(10, "");
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		Element releaseDateElement = document.select("dl.info dt:contains(Release Date) + dd, dl.info dt:contains(�?信開始日) + dd").first();
		if(releaseDateElement != null && releaseDateElement.text().length() > 4)
		{
			String releaseDateText = releaseDateElement.text().trim();
			return new ReleaseDate(releaseDateText, myTokyoHotReleaseDateFormat);
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
		Element plotElement = null;
		if(getScrapingLanguage() == Language.ENGLISH)
			plotElement = document.select("div.contents div.sentence").first();
		else if(getScrapingLanguage() == Language.JAPANESE)
		{
			initializeJapaneseDocument();
			plotElement = japaneseDocument.select("div.contents div.sentence").first();
		}
		if(plotElement != null)
			return new Plot(plotElement.text().trim());
		else return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element durationElement = document.select("dl.info dt:contains(Duration) + dd, dl.info dt:contains(�?�録時間) + dd").first();
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
		return Runtime.BLANK_RUNTIME;
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
			Element thumbnailLink = galleryImg.select("img").first();
			try {
				Thumb imgThumb = new Thumb(imgLink);
				if(thumbnailLink != null)
					imgThumb.setPreviewURL(new URL(thumbnailLink.attr("src")));
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
		return Trailer.BLANK_TRAILER;
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
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Element idElement = document.select("dl.info dt:contains(Product ID) + dd, dl.info dt:contains(作�?番�?�) + dd").first();
		if(idElement != null && idElement.text().length() > 0)
			return new ID(idElement.text().trim());
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Elements genreElements = null;
		if(getScrapingLanguage() == Language.ENGLISH)
			genreElements = document.select("dl.info dt:contains(Category) + dd a, dl.info dt:contains(カテゴリ) + dd a");
		else if(getScrapingLanguage() == Language.JAPANESE)
		{
			initializeJapaneseDocument();
			genreElements = japaneseDocument.select("dl.info dt:contains(Category) + dd a, dl.info dt:contains(カテゴリ) + dd a");
		}
		if(genreElements != null)
		{
			for(Element currentGenre : genreElements)
			{
				genreList.add(new Genre(WordUtils.capitalize(currentGenre.text().trim())));
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Elements actressElements = null;
		if(getScrapingLanguage() == Language.ENGLISH)
			actressElements = document.select("dl.info dt:contains(Actress) + dd a, dl.info dt:contains(出演者) + dd a");
		else if(getScrapingLanguage() == Language.JAPANESE)
		{
			initializeJapaneseDocument();
			actressElements = japaneseDocument.select("dl.info dt:contains(Actress) + dd a, dl.info dt:contains(出演者) + dd a");
		}
		if(actressElements != null)
		{
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
		scrapedMovieFile = file;
		String fileID = findIDTagFromFile(file);
		if(fileID == null)
			return null;
		fileID = fileID.toLowerCase();
		if(fileID == null)
			return null;
		String searchURL = "http://my.tokyo-hot.com/product/?q=" + fileID + "&x=0&y=0";
		return searchURL;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		if(searchString == null)
			return new SearchResult[0];
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
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

	@Override
	public SiteParsingProfile newInstance() {
		return new MyTokyoHotParsingProfile();
	}

}
