package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
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

public class IAFDParsingProfile extends SiteParsingProfile {
	
	boolean useSiteSearch = true;
	String yearFromFilename = "";
	String fileName;

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div#centered.main2 div div h1.h1big").first();
		if(titleElement != null)
			return new Title(titleElement.text());
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// TODO Auto-generated method stub
		return new SortTitle("");
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("div div.p8 div p a[href*=/series/]").first();
		if(setElement != null)
			return new Set(setElement.text());
		else return new Set("");
	}

	@Override
	public Rating scrapeRating() {
		// TODO Auto-generated method stub
		return new Rating(0, "");
	}

	@Override
	public Year scrapeYear() {
		
		String yearText = "year=";
		String uri = document.baseUri();
		int indexOf = uri.indexOf(yearText) + yearText.length();
		String releaseDateText = uri.substring(indexOf, indexOf + 4);
		if(releaseDateText.length() == 4)
		{
			return new Year(releaseDateText);
		}
		else return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		// TODO Auto-generated method stub
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		// TODO Auto-generated method stub
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		// TODO Auto-generated method stub
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
		Element plotElement = document.select("div.gen12 b:contains(Description:) ~ p").first();
		if(plotElement != null)
			return new Plot(plotElement.text());
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		// TODO Auto-generated method stub
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("p.gen12:contains(Length:)").first();
		if(runtimeElement != null)
		{
			String runtimeElementText = runtimeElement.text().replaceFirst(Pattern.quote("Length:"), "").replaceFirst(Pattern.quote(" min."), "").trim();
			return new Runtime(runtimeElementText);
		}
		//System.out.println("runtime " + runtimeElement.text());
		else return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		Element posterElement = document.select("a[rel=covers]").first();
		if(posterElement != null)
		{
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
		ArrayList<Thumb> extraFanart = new ArrayList<Thumb>();
		return extraFanart.toArray(new Thumb[extraFanart.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		// TODO Auto-generated method stub
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		// TODO Auto-generated method stub
		return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		//No Genres in IAFD
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
//		Elements actorElements = document.select("div [id=actor] li");	//male actors
		Elements actorElements = document.select("div [id=actress] li");  //female actors
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		if(actorElements != null)
		{
			for(Element currentActorElement : actorElements)
			{
				String actorName = currentActorElement.childNode(0).childNode(0).toString().trim();
				String actorAlias = "(as ";
				int indexOfAs = actorName.indexOf(actorAlias);
				if ( indexOfAs >= 0 ) {
					actorName = actorName.substring(indexOfAs + actorAlias.length(), actorName.lastIndexOf(")") );
				}
				String actorThumbnailSite = "http://www.iafd.com" + currentActorElement.childNode(0).attr("href");
				
				Document searchActor;
				try {
					searchActor = Jsoup.connect(actorThumbnailSite).timeout(0).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
					Element actorPicture = searchActor.select("div[id=headshot] img").first();
					if (actorPicture == null)
						continue;	//found something like "Non Sex Performers" Text between actors
					String actorThumbnail = "http://www.iafd.com" + actorPicture.attr("src");
					//case with actor with thumbnail
					if(actorThumbnail != null && !actorThumbnail.equals("http://www.iafd.com/graphics/headshots/no_photo.gif"))
					{
						try {
							actorThumbnail = actorThumbnail.replaceFirst(Pattern.quote("/60/"), "/120/");
							actorList.add(new Actor(actorName, null, new Thumb(actorThumbnail)));
						} catch (MalformedURLException e) {
							actorList.add(new Actor(actorName, null, null));
							e.printStackTrace();
						}
					}
					//add the actor with no thumbnail
					else
					{
						actorList.add(new Actor(actorName, null, null));
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		Element directorElement = document.select("dd a[href^=/person.rme/]").first();
		if(directorElement != null)
		{
			String directorName = directorElement.childNode(0).toString().trim();
			if(directorName != null && directorName.length() > 0 && !directorName.equals("Unknown"))
				directorList.add(new Director(directorName,null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("dd a[href^=/studio.rme/]").first();
		if(studioElement != null)
		{
			String studioText = studioElement.childNode(0).toString().trim();
			if(studioText != null && studioText.length() > 0)
				return new Studio(studioText);
		}
		return new Studio("");
	}

	@Override
	public String createSearchString(File file) {
		String fileBaseName;
		if(file.isFile())
			fileBaseName = FilenameUtils.getBaseName(file.getName());
		else
			fileBaseName = file.getName();
		fileBaseName = fileBaseName.replaceFirst("\\s?CD[1234]", "");
		fileName = fileBaseName;
		String [] splitBySpace = fileBaseName.split(" ");
		if(splitBySpace.length > 1)
		{
			//check if last word in filename contains a year like (2012) or [2012]
			if(splitBySpace[splitBySpace.length-1].matches("[\\(\\[]\\d{4}[\\)\\]]"))
			{
				yearFromFilename = splitBySpace[splitBySpace.length-1].replaceAll("[\\(\\[\\)\\]]", "");
				fileBaseName = fileBaseName.replaceFirst("[\\(\\[]\\d{4}[\\)\\]]","").trim();

			}
		}
		if(useSiteSearch)
		{
			URLCodec codec = new URLCodec();
			try {
				fileBaseName = codec.encode(fileBaseName);
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileBaseName = "http://www.iafd.com/results.asp?searchtype=title&searchstring=" + fileBaseName;
			return fileBaseName;
		}
		return FilenameUtils.getBaseName(file.getName());
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		if(useSiteSearch)
		{
			ArrayList<SearchResult> linksList = new ArrayList<SearchResult>();
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
			Elements movieSearchResultElements = doc.select("li b a");
			if(movieSearchResultElements == null || movieSearchResultElements.size() == 0)
			{
				this.useSiteSearch = false;
				return getLinksFromGoogle(fileName, "www.iafd.com/title.rme");
			}
			else
			{
				for(Element currentMovie : movieSearchResultElements)
				{
					String currentMovieURL = currentMovie.select("a").first().attr("href");
					String currentMovieTitle = currentMovie.select("a").last().text();
					final String searchForYearText = "year=";
					int index = currentMovieURL.indexOf(searchForYearText) + searchForYearText.length();
					String releaseDateText = currentMovieURL.substring(index, index+4);
					if(releaseDateText != null && releaseDateText.length() > 0)
						currentMovieTitle = currentMovieTitle + " (" + releaseDateText + ")";
					Thumb currentMovieThumb = new Thumb(currentMovie.select("img").attr("src"));
					linksList.add(new SearchResult(currentMovieURL, currentMovieTitle, currentMovieThumb));
				}
				return linksList.toArray(new SearchResult[linksList.size()]);
			}
		}
		else
		{
			this.useSiteSearch = false;
			return getLinksFromGoogle(searchString, "www.iafd.com/title.rme");
		}
	}
	public String toString(){
		return "IAFD";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new IAFDParsingProfile();
	}

}
