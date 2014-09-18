package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
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

public class TokyoHotParsingProfile extends SiteParsingProfile {

	private String searchString;
	private Document docSite;
	private Document docImage;

	public TokyoHotParsingProfile() {
	}
	
	public TokyoHotParsingProfile(String searchString) {
		this.searchString = searchString;
		getDocuments();
	}
	
	private void getDocuments() {
		String siteLink = getSiteLink(searchString);
		String imageLink = getImageLink(searchString);
		
		try {
			docSite = Jsoup.connect(siteLink).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
			docImage = Jsoup.connect(imageLink).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		} catch (IOException e) {
			System.out.println("Error downloading Data from ToykoHot");
			e.printStackTrace();
		}
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
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return new SortTitle("");
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
		Elements e = docImage.select("td[align=right]");
		if (e.size() > 2) {
			Pattern pattern = Pattern.compile("[0-9]{4}");
			String timecode = e.get(1).ownText();
			Matcher matcher = pattern.matcher(timecode);
			if (matcher.find()) {
				return new Year( matcher.group() );
			}
		}
		return null;
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
		Elements elements = docSite.select("tr td[align=left]");
		if (elements.size() > 0) {
			String ownText = elements.get(0).childNode(0).childNode(0).toString().trim();
			return new Plot(ownText);
		}
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
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
		return new MPAARating("XXX");
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
		String fileID = findIDTagFromFile(file).toLowerCase();
		
		if ( fileID != null ) {
			try {
				Document doc = Jsoup.connect("http://cdn.www.tokyo-hot.com/igs/").userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
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
				
				return foundLink.replace("/", "");
						
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
		SearchResult searchResult = new SearchResult( getSiteLink(searchString), searchString);
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
		return "http://cdn.www.tokyo-hot.com/e/" + searchString + "_e.html";
	}
}
