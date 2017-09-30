package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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

public class IAFDParsingProfile extends SiteParsingProfile implements SpecificProfile {

	boolean useSiteSearch = true;
	String yearFromFilename = "";
	String fileName;

	@Override
	public List<ScraperGroupName> getScraperGroupNames() {
		if (groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
		return groupNames;
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select(getTitleElementSelector()).first();
		System.out.println(titleElement);
		//System.out.println(document);
		System.out.println("Scraping title");
		if (titleElement != null) {
			String titleText = titleElement.text().trim();
			//remove year like (2015) from text
			if (titleText.matches(".+\\(\\d{4}\\)")) {
				titleText = StringUtils.substringBeforeLast(titleText, " ");
			}
			//titleText = titleText.replaceFirst("(\\d{4}", "");
			//System.err.println("New title text = " + titleText);
			return new Title(titleText);
		} else
			return new Title("");
	}

	private String getTitleElementSelector() {
		return "div.col-sm-12 h1";
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
		Element setElement = findSidebarElement("Studio");
		if (setElement != null && setElement.text().contains(".com")) {
			return new Set(setElement.text());
		} else
			return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// TODO Auto-generated method stub
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {

		String yearText = "year=";
		String uri = document.baseUri();
		int indexOf = uri.indexOf(yearText) + yearText.length();
		String releaseDateText = uri.substring(indexOf, indexOf + 4);
		if (releaseDateText.length() == 4) {
			return new Year(releaseDateText);
		} else
			return Year.BLANK_YEAR;
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		//I don't think IAFD has the month or day a movie was released - only the year
		//In some rare cases they have this information in the comments. There is also a release date field
		//in the side bar that always seems to be blank. Maybe they are going to populate this in the future?
		//TODO: get this info out of the comments field. this info may be inconsistently formatted, so watch out
		return ReleaseDate.BLANK_RELEASEDATE;
	}

	@Override
	public Top250 scrapeTop250() {
		// TODO Auto-generated method stub
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		// TODO Auto-generated method stub
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		// TODO Auto-generated method stub
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		Element plotElement = document.select("div#sceneinfo ul").first();
		if (plotElement != null) {
			//try to put each scene on its own new line
			Elements sceneBreakdown = plotElement.select("li");
			if (sceneBreakdown != null) {
				String sceneText = "";
				for (Element scene : sceneBreakdown) {
					sceneText += scene.text() + "\n";
				}
				return new Plot(sceneText);
			} else {
				return new Plot(plotElement.text());
			}
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		// TODO Auto-generated method stub
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = findSidebarElement("Minutes");
		if (runtimeElement != null) {
			return new Runtime(runtimeElement.text());
		} else
			return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		Element posterElement = document.select("a[rel=covers]").first();
		if (posterElement != null) {
			Thumb[] posterThumbs = new Thumb[1];
			try {
				posterThumbs[0] = new Thumb(posterElement.attr("href"));
				return posterThumbs;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		ArrayList<Thumb> extraFanart = new ArrayList<>();
		return extraFanart.toArray(new Thumb[extraFanart.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		// TODO Auto-generated method stub
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		//No Genres in IAFD
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("div.castbox:not(.nonsex) a"); //performers who are not just extras, etc
		ArrayList<Actor> actorList = new ArrayList<>();
		if (actorElements != null) {
			for (Element currentActorElement : actorElements) {
				String actorName = currentActorElement.ownText();
				String actorAlias = "(as ";
				int indexOfAs = actorName.indexOf(actorAlias);
				if (indexOfAs >= 0) {
					actorName = actorName.substring(indexOfAs + actorAlias.length(), actorName.lastIndexOf(")"));
				}
				Element actorPicture = currentActorElement.select("img").first();
				if (actorPicture == null)
					continue; //found something like "Non Sex Performers" Text between actors
				String actorThumbnail = actorPicture.absUrl("src");
				//case with actor with thumbnail
				if (actorThumbnail != null && !actorThumbnail.contains("nophoto")) {
					try {
						actorThumbnail = actorThumbnail.replaceFirst(Pattern.quote("/60/"), "/120/");
						actorList.add(new Actor(actorName, null, new Thumb(actorThumbnail)));
					} catch (MalformedURLException e) {
						actorList.add(new Actor(actorName, null, null));
						e.printStackTrace();
					}
				}
				//add the actor with no thumbnail
				else {
					actorList.add(new Actor(actorName, null, null));
				}
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		Element directorElement = findSidebarElement("Director");
		if (directorElement != null) {
			String directorName = directorElement.text().trim();
			if (directorName != null && directorName.length() > 0 && !directorName.equals("Unknown"))
				directorList.add(new Director(directorName, null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = findSidebarElement("Distributor");
		if (studioElement != null) {
			String studioText = studioElement.text().trim();
			if (studioText != null && studioText.length() > 0)
				return new Studio(studioText);
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileBaseName;
		if (file.isFile())
			fileBaseName = FilenameUtils.getBaseName(file.getName());
		else
			fileBaseName = file.getName();
		fileBaseName = fileBaseName.replaceFirst("\\s?CD[1234]", "");
		fileName = fileBaseName;
		String[] splitBySpace = fileBaseName.split(" ");
		if (splitBySpace.length > 1) {
			//check if last word in filename contains a year like (2012) or [2012]
			if (splitBySpace[splitBySpace.length - 1].matches("[\\(\\[]\\d{4}[\\)\\]]")) {
				yearFromFilename = splitBySpace[splitBySpace.length - 1].replaceAll("[\\(\\[\\)\\]]", "");
				fileBaseName = fileBaseName.replaceFirst("[\\(\\[]\\d{4}[\\)\\]]", "").trim();

			}
		}
		if (useSiteSearch) {
			URLCodec codec = new URLCodec();
			try {
				fileBaseName = codec.encode(fileBaseName);
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileBaseName = "http://www.iafd.com/results.asp?searchtype=comprehensive&searchstring=" + fileBaseName;
			return fileBaseName;
		}
		return FilenameUtils.getBaseName(file.getName());
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		if (useSiteSearch) {
			ArrayList<SearchResult> linksList = new ArrayList<>();
			Document doc = Jsoup.connect(searchString).userAgent(getRandomUserAgent()).referrer("http://www.iafd.com").ignoreHttpErrors(true)
					.timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			//check to see if we directly found the title
			if (doc != null && doc.location().contains("title.asp?title=")) {
				String title = doc.select(getTitleElementSelector()).first().text();
				linksList.add(new SearchResult(doc.location(), title));
			}
			Elements movieSearchResultElements = null;
			if (doc != null) {
				movieSearchResultElements = doc.select("table#titleresult tr td a[href*=title.rme");
			}
			if (linksList.size() == 0 && (movieSearchResultElements == null || movieSearchResultElements.size() == 0)) {
				this.useSiteSearch = false;
				return getLinksFromGoogle(fileName, "www.iafd.com/title.rme");
			} else if (movieSearchResultElements != null) {
				for (Element currentMovie : movieSearchResultElements) {
					String currentMovieURL = currentMovie.absUrl("href");
					String currentMovieTitle = currentMovie.text();
					final String searchForYearText = "year=";
					int index = currentMovieURL.indexOf(searchForYearText) + searchForYearText.length();
					String releaseDateText = currentMovieURL.substring(index, index + 4);
					if (releaseDateText != null && releaseDateText.length() > 0)
						currentMovieTitle = currentMovieTitle + " (" + releaseDateText + ")";
					Thumb currentMovieThumb = new Thumb(currentMovie.select("img").attr("src"));
					linksList.add(new SearchResult(currentMovieURL, currentMovieTitle, currentMovieThumb));
				}
				return linksList.toArray(new SearchResult[linksList.size()]);
			}
			return linksList.toArray(new SearchResult[linksList.size()]);
		} else {
			this.useSiteSearch = false;
			return getLinksFromGoogle(searchString, "www.iafd.com/title.rme");
		}
	}

	@Override
	public String toString() {
		return "IAFD";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new IAFDParsingProfile();
	}

	@Override
	public String getParserName() {
		return "IAFD";
	}

	private Element findSidebarElement(String textOfSideBarElement) {
		String selector = "p:containsOwn(" + textOfSideBarElement + ") + p.biodata";
		System.out.println("selector = " + selector);
		Element sidebarElement = document.select(selector).first();
		return sidebarElement;
	}

}
