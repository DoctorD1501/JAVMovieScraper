package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Data18WebContentParsingProfile extends SiteParsingProfile{
	boolean useSiteSearch = true;
	String yearFromFilename = "";
	String fileName;
	Thumb[] scrapedPosters;
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
		
		//relatedMovie is used for split scenes
		Element relatedMovie = document.select("div.gen:contains(Related Movie and Scenes:) ~ div ~ div p a").first();
		if(relatedMovie != null && relatedMovie.text().length() > 0)
		{
			return new Set(relatedMovie.text());
		}
		//setElement used below is for web downloads
		Element setElement = document.select("div#centered.main2 div.dloc.gen12 a").last();
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
		Element releaseDateElement = document.select("div p:contains(Date:) a").first();
		//case where the date is not a hyperlink, but just a month and a year
		if(releaseDateElement!= null & releaseDateElement.text().contains("errors"))
			releaseDateElement = document.select("div p:contains(Date:) b").first();
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
		Element plotElement = document.select("div.gen12 p:contains(Story:)").first();
		if(plotElement != null)
			return new Plot(plotElement.ownText());
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		// TODO Auto-generated method stub
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		//find split scene links from a full movie
		ArrayList<String> contentLinks = new ArrayList<String>();
		ArrayList<Thumb> extraFanart = new ArrayList<Thumb>();
		String docLocation = document.location();
		String contentIDFromPage = docLocation.substring(docLocation.lastIndexOf("/")+1,docLocation.length());
		contentLinks.add(contentIDFromPage);
		//for each id, go to the viewer page for that ID
		for(String contentID : contentLinks)
		{
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
							if(!fileExistsAtURL(previewURL))
								previewURL = mainImageUrl.substring(0,mainImageUrl.length()-6) + "thumb2/" + mainImageUrl.substring(mainImageUrl.length()-6,mainImageUrl.length());	
							if(fileExistsAtURL(previewURL))
								thumbToAdd.setPreviewURL(new URL(previewURL));
							//System.out.println("previewURL : " + previewURL);
							extraFanart.add(thumbToAdd);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		scrapedPosters = extraFanart.toArray(new Thumb[extraFanart.size()]);
		return scrapedPosters;
	}

	@Override
	public Thumb[] scrapeFanart() {
		if(scrapedPosters != null)
			return scrapedPosters;
		else return scrapePosters();
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		if(scrapedPosters != null)
			return scrapedPosters;
		else return scrapePosters();
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
		Elements genreElements = document.select("div.gen12 div:contains(Categories) a");
		if (genreElements != null)
		{
			for(Element currentGenreElement : genreElements)
			{
				//Quick fix to get rid of the search link from appearing as a category
				if(!(currentGenreElement.attr("href").equals("http://www.data18.com/content/search.html")))
				{
					String genreText = currentGenreElement.text().trim();
					if(genreText != null && genreText.length() > 0)
						genreList.add(new Genre(genreText));
				}

			}
		}
		
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements actorElements = document.select("p:contains(Starring:) a.bold");
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		if(actorElements != null)
		{
			for(Element currentActorElement : actorElements)
			{
				String actorPageLink = currentActorElement.attr("href");
				String actorName = currentActorElement.text();
				//Connect to the actor page to get the thumbnail
				if(actorPageLink!= null)
				{
					try {
						Document actorPage = Jsoup.connect(actorPageLink).timeout(0).get();
						Element actorThumbnailElement = actorPage.select("div.imagepic a img[src*=/stars/]").first();
						String actorThumbnail = null;
						if(actorThumbnailElement != null)
						{
							actorThumbnail = actorThumbnailElement.attr("src");
						}

						//case with actor with thumbnail
						if(actorThumbnail != null && 
								!actorThumbnail.equals("http://img.data18.com/images/no_prev_60.gif")
								&& (!actorThumbnail.equals("http://img.data18.com/images/no_prev_star.jpg")))
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}


			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("div#centered.main2 div.dloc.gen12 a:contains(Sites) ~ a").first();
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
		String fileBaseName;
		if(file.isFile())
			fileBaseName = FilenameUtils.getBaseName(file.getName());
		else
			fileBaseName = file.getName();
		System.out.println("fileBaseName = " + fileBaseName);
		fileName = fileBaseName;
		/*String [] splitBySpace = fileBaseName.split(" ");
		if(splitBySpace.length > 1)
		{
			//check if last word in filename contains a year like (2012) or [2012]
			if(splitBySpace[splitBySpace.length-1].matches("[\\(\\[]\\d{4}[\\)\\]]"))
			{
				yearFromFilename = splitBySpace[splitBySpace.length-1].replaceAll("[\\(\\[\\)\\]]", "");
				fileBaseName = fileBaseName.replaceFirst("[\\(\\[]\\d{4}[\\)\\]]","").trim();

			}
		}*/
		if(useSiteSearch)
		{
			URLCodec codec = new URLCodec();
			try {
				fileBaseName = codec.encode(fileBaseName);
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileBaseName = "http://www.data18.com/search/?k=" + fileBaseName + "&t=0";
			return fileBaseName;
		}
		return FilenameUtils.getBaseName(file.getName());
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		//we're searching for new file, reset the scraped posters
		System.out.println("searchString = " + searchString);
		scrapedPosters = null;
		if(useSiteSearch)
		{
			ArrayList<SearchResult> linksList = new ArrayList<SearchResult>();
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
			Elements movieSearchResultElements = doc.select("div[style=float: left; padding: 6px; width: 130px;");
			if(movieSearchResultElements == null || movieSearchResultElements.size() == 0)
			{
				this.useSiteSearch = false;
				SearchResult[] googleResults = getLinksFromGoogle(fileName, "data18.com/content/");
				if(googleResults == null || googleResults.length == 0)
					googleResults = getLinksFromGoogle(fileName.replaceAll("[0-9]", ""), "data18.com/content/");
				return googleResults;
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
			SearchResult[] googleResults = getLinksFromGoogle(searchString, "data18.com/content/");
			if(googleResults == null || googleResults.length == 0)
				googleResults = getLinksFromGoogle(fileName.replaceAll("[0-9]", ""), "data18.com/content/");
			return googleResults;
		}
	}
}
