package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

public class Data18MovieParsingProfile extends SiteParsingProfile {
	
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
		Element setElement = document.select("a[href*=/series/]").first();
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
		Element releaseDateElement = document.select("div p:contains(Release Date:) b").first();
		if(releaseDateElement != null)
		{
			String releaseDateText = releaseDateElement.text().trim();
			//just get the last 4 letters which is the year
			if(releaseDateText.length() >= 4)
			{
				releaseDateText = releaseDateText.substring(releaseDateText.length()-4,releaseDateText.length());
				return new Year(releaseDateText);
			}
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
		Element posterElement = document.select("a[data-lightbox=covers]").first();
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
		Element posterElement = document.select("a[data-lightbox=covers]:contains(Back Cover)").first();
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
	public Thumb[] scrapeExtraFanart() {
		//find split scene links from a full movie
		Elements sceneContentLinks = document.select("div[onmouseout]:matches(Scene \\d\\d?)");
		//System.out.println("Scenecontentlinsk " + sceneContentLinks);
		ArrayList<String> contentLinks = new ArrayList<String>();
		ArrayList<Thumb> extraFanart = new ArrayList<Thumb>();
		if(sceneContentLinks != null)
		{
			//get just the id from url of the content
			for(Element sceneContentLink : sceneContentLinks)
			{
				Element linkElement = sceneContentLink.select("a[href*=/content/").first();
				if(linkElement != null)
					{
					String linkElementURL = linkElement.attr("href");
					if(linkElementURL.contains("/"))
					{
						String contentID = linkElementURL.substring(linkElementURL.lastIndexOf("/")+1,linkElementURL.length());
						//System.out.println(contentID);
						contentLinks.add(contentID);
					}
				}
			}
		}
		
		//for each id, go to the viewer page for that ID
		for(String contentID : contentLinks)
		{
			//int viewerPageNumber = 1;
			for(int viewerPageNumber = 1; viewerPageNumber <= 15; viewerPageNumber++)
			{
				//System.out.println("viewerPageNumber: " + String.format("%02d", viewerPageNumber));
				String currentViewerPageURL = "http://www.data18.com/viewer/" + contentID + "/" + String.format("%02d", viewerPageNumber);
				//System.out.println("currentVIewerPageURL + " + currentViewerPageURL);
				try {
					
					Document viewerDocument = Jsoup.connect(currentViewerPageURL).timeout(0).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
					if(viewerDocument!= null)
					{
						Element imgElement = viewerDocument.select("div#post_view a[href*=/viewer/] img").first();
						if(imgElement != null)
						{
							String mainImageUrl = imgElement.attr("src");
							Thumb thumbToAdd = new Thumb(mainImageUrl);
							String previewURL = mainImageUrl.substring(0,mainImageUrl.length()-6) + "th8/" + mainImageUrl.substring(mainImageUrl.length()-6,mainImageUrl.length());
							thumbToAdd.setPreviewURL(new URL(previewURL));
							//System.out.println("previewURL : " + previewURL);
							extraFanart.add(thumbToAdd);
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					//continue;
			}
			
			}
		}
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
		//Elements genreElements = document.select("span.gensmall ~ a");
		Elements genreElements = document.select("div.gen12:contains(Categories:) a");
		System.out.println("genreElements: " + genreElements);
		//alternate version data18 sometimes uses with "Categories" perhaps?
		/*if(genreElements == null || genreElements.size() == 0)
		{
			System.out.println("alt genre method");
			genreElements = document.select("div.gen12:contains(Categories:) a");
		}*/
		if (genreElements != null)
		{
			for(Element currentGenreElement : genreElements)
			{
				String genreText = currentGenreElement.text().trim();
				if(genreText != null && genreText.length() > 0)
					genreList.add(new Genre(genreText));
			}
		}
		
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("p.line1 a img");
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		if(actorElements != null)
		{
			for(Element currentActorElement : actorElements)
			{
				String actorName = currentActorElement.attr("alt");
				String actorThumbnail = currentActorElement.attr("src");
				
				//case with actor with thumbnail
				if(actorThumbnail != null && !actorThumbnail.equals("http://img.data18.com/images/no_prev_60.gif"))
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
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		Element directorElement = document.select("a[href*=director=]").first();
		if(directorElement != null)
		{
			String directorName = directorElement.text();
			if(directorName != null && directorName.length() > 0 && !directorName.equals("Unknown"))
				directorList.add(new Director(directorName,null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("p.gen12 b:contains(Studio:) ~ a").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text();
			if(studioText != null && studioText.length() > 0)
				return new Studio(studioText);
		}
		return new Studio("");
	}

	@Override
	public String createSearchString(File file) {
		String fileBaseName = FilenameUtils.getBaseName(file.getName());
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
			fileBaseName = "http://www.data18.com/search/?k=" + fileBaseName + "&t=2";
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
			Elements movieSearchResultElements = doc.select("div[style=float: left; padding: 6px; width: 130px;");
			if(movieSearchResultElements == null || movieSearchResultElements.size() == 0)
			{
				this.useSiteSearch = false;
				return getLinksFromGoogle(fileName, "data18.com/movies/");
			}
			else
			{
				for(Element currentMovie : movieSearchResultElements)
				{
					String currentMovieURL = currentMovie.select("a").first().attr("href");
					String currentMovieTitle = currentMovie.select("a").last().text();
					String releaseDateText = currentMovie.ownText();
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
			return getLinksFromGoogle(searchString, "data18.com/movies/");
		}
	}

}
