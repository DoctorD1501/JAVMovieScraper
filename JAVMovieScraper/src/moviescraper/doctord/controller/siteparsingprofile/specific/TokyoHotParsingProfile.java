package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.SpecificScraperAction;
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

public class TokyoHotParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private String searchString;
	private Document docSite;
	private Document docImage;
	private String siteLink;
	private String imageLink;
	private String id;
	private static final SimpleDateFormat tokyoHotReleaseDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm");

	public TokyoHotParsingProfile() {
	}
	
	@Override
	public void setDocument(Document document) {
		super.setDocument(document);
		docSite = document;
		docImage = SpecificScraperAction.downloadDocument(imageLink);
	}
	
	@Override
	public Title scrapeTitle() {
		Elements elements = docSite.select("div font[size=2]");
		if ( elements.size() > 2 )
			return new Title( elements.get(0).ownText().replace("&quot;", "").replace("\"", "") );
		return null;
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		return new Set("Tokyo Hot");
	}

	@Override
	public Rating scrapeRating() {
		return new Rating(0,"");
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate()
	{
		ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
		Elements releaseDateElements = docImage.select("td[align=right]");
		for(Element currentElement : releaseDateElements)
		{
			if (releaseDateElements.size() > 2) {
				Pattern pattern = Pattern.compile("[0-9]{4}");
				String timecode = currentElement.ownText();
				Matcher matcher = pattern.matcher(timecode);
				if (matcher.find()) {
					// the last element we find seems to be the most accurate
					// date, but I'm not 100% sure what each of these dates
					// represents
					// since they seem to vary by a few days usually
					releaseDate = new ReleaseDate(timecode,
							tokyoHotReleaseDateFormat);
				}
			}
		}
		return releaseDate;
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
		Elements elements = docSite.select("tr td[align=left]");
		if (elements.size() > 0) {
			String ownText = elements.get(0).childNode(0).childNode(0).toString().trim();
			return new Plot(ownText);
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Elements elements = docSite.select("td[align=center] font strong");
		Pattern timePattern = Pattern.compile("[0-9]{2,3} min");
		Pattern minPattern = Pattern.compile("[0-9]{2,3}");
		String time = "";
		for (Element element : elements) {
			String node = element.childNode(0).toString();
			Matcher matcher = timePattern.matcher(node);
			if (matcher.find()) {
				time = matcher.group();
				Matcher minMatcher = minPattern.matcher(time);
				if ( minMatcher.find() ) {
					time = minMatcher.group();
				}
			}
		}
		return new Runtime(time);
	}

	@Override
	public Thumb[] scrapePosters() {
		try {
			Thumb[] thumbs = new Thumb[1];
			thumbs[0] = new Thumb(getImageLink(searchString) + "_v.jpg");
			return thumbs;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		try {
			Thumb[] thumbs = new Thumb[1];
			thumbs[0] = new Thumb(getImageLink(searchString) + "_vb.jpg", getImageLink(searchString) + "_v.jpg");
			return thumbs;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> extraFanart = new ArrayList<Thumb>();
		return extraFanart.toArray(new Thumb[extraFanart.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		return new ID(findIDTag(searchString).toUpperCase());
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		Genre a = new Genre("uncensored");
		Genre b = new Genre("No condom");
		ArrayList<Genre> list = new ArrayList<>();
		Collections.addAll(list, a, b);
		return list;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> list = new ArrayList<>();
		Elements elements = docSite.select("div font[size=2]");
		if ( elements.size() > 2 )
			list.add( new Actor(elements.get(1).childNode(0).toString(), null, null) );
		return list;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		Director a = new Director("Tokyo Hot", null);
		ArrayList<Director> list = new ArrayList<>();
		Collections.addAll(list, a);
		return list;
	}

	@Override
	public Studio scrapeStudio() {
		return new Studio("Tokyo Hot");
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileID = findIDTagFromFile(file).toLowerCase();
		
		if ( fileID != null ) {
			try {
				Document doc = Jsoup.connect("http://cdn.www.tokyo-hot.com/igs/").userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
				Elements select = doc.select("tr td a");
				String foundLink = null;
				for (Element element : select) {
					String link = element.attr("href");
					if ( link.startsWith( fileID ) ) {
						foundLink = link;
						break;
					}
				}
				if ( foundLink == null ) {
					System.out.println("Found no Link for TokyoHot");
					return null;
				}
				id = foundLink.replace("/", "");
				imageLink = getImageLink(id);
				siteLink = getSiteLink(id);
				
				return siteLink;
						
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		SearchResult searchResult = new SearchResult( searchString, searchString);
		SearchResult[] sr = {searchResult};
		return sr;
	}
	
	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}
	
	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("n[0-9]{3,4}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	private String getImageLink(String searchString) {
		return "http://cdn.www.tokyo-hot.com/igs/" + searchString + "/";
	}
	
	private String getSiteLink(String searchString) {
		this.searchString = searchString;
		return "http://cdn.www.tokyo-hot.com/e/" + searchString + "_e.html";
	}

	@Override
	public String getParserName() {
		return "Tokyo Hot";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new TokyoHotParsingProfile();
	}
}
