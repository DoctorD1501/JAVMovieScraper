package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.controller.siteparsingprofile.SecurityPassthrough;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
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

public class Data18MovieParsingProfile extends SiteParsingProfile implements SpecificProfile, SecurityPassthrough {
	
	boolean useSiteSearch = true;
	String yearFromFilename = "";
	String fileName;
	Thumb[] scrapedExtraFanart;
	boolean hasRunScrapeExtraFanart = false;
	
	//I've unfortunately had to make this static due to the current mess of a way this type of scraping is done where the object used
	//to create the search results is not the same as the object used to actually scrape the document.
	private static HashMap<String, String> releaseDateMap; 

	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP);
		return groupNames;
	}
	
	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div#centered.main2 div h1").first();
		if(titleElement != null)
			return new Title(titleElement.text());
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// TODO Auto-generated method stub
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("div div.p8 div p a[href*=/series/]").first();
		if(setElement != null)
			return new Set(setElement.text());
		else return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// TODO Auto-generated method stub
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {

		//old method before site update in September 2014
		Element releaseDateElement = document.select("div p:contains(Release Date:) b").first();
		//new method after site update in mar 2015
		if(releaseDateElement == null)
		{

			releaseDateElement = document.select("div.p8 div.gen12 p:contains(Release Date:), div.p8 div.gen12 p:contains(Production Year:)").first();
			if(releaseDateElement != null)
			{
				String releaseDateText = releaseDateElement.text().trim();
				final Pattern pattern = Pattern.compile("(\\d{4})"); //4 digit years
				final Matcher matcher = pattern.matcher(releaseDateText);
				if ( matcher.find() ) {
					String year = matcher.group(matcher.groupCount());
					return new Year(year);
				}
				if(releaseDateText.length() > 4)
				{
					//just get the first 4 letters which is the year
					releaseDateText = releaseDateText.substring(0,4);
					return new Year(releaseDateText);
				}
				else return Year.BLANK_YEAR;
			}
		}
		else
		{
			String releaseDateText = releaseDateElement.text().trim();
			//just get the last 4 letters which is the year
			if(releaseDateText.length() >= 4)
			{
				releaseDateText = releaseDateText.substring(releaseDateText.length()-4,releaseDateText.length());
				return new Year(releaseDateText);
			}
		}
		return Year.BLANK_YEAR;
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
		Element plotElement = document.select("p.gen12:contains(Description:)").first();
		if(plotElement != null)
		{
			String plotText = plotElement.text();
			if(plotText.startsWith("Description: "))
			{
				plotText = plotText.replaceFirst("Description:", "");
			}
			return new Plot(plotText);
		}
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("p.gen12:contains(Length:)").first();
		if(runtimeElement != null)
		{
			String runtimeElementText = runtimeElement.text().replaceFirst(Pattern.quote("Length:"), "").replaceFirst(Pattern.quote(" min."), "").trim();
			return new Runtime(runtimeElementText);
		}
		else return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		Element posterElement = document.select("a[rel=covers]").first();
		if(posterElement != null)
		{
			Thumb[] posterThumbs = new Thumb[1];
			try {
				posterThumbs[0] = new Thumb(fixIPAddressOfData18(posterElement.attr("href")));
				return posterThumbs;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
		}
		return new Thumb[0];
	}
	
	/**
	 * Fix for Github issue 97 (https://github.com/DoctorD1501/JAVMovieScraper/issues/97)
	 * The european IP address for galleries gives us a HTTP response code of 302 (redirect), which prevents us from downloading things
	 * we will route to the american IP address instead
	 */
	private String fixIPAddressOfData18(String mainImageUrl) {
		if(mainImageUrl == null)
			return mainImageUrl;
		else 
		{
			//tends to be links for main cover, etc
			String stringWithIPAdressReplaced = mainImageUrl.replaceFirst("94.229.67.74", "74.50.117.45");
			//tends to be image gallery on movie page
			stringWithIPAdressReplaced = stringWithIPAdressReplaced.replaceFirst("78.110.165.210", "74.50.117.48");
			return stringWithIPAdressReplaced;
		}
	}

	@Override
	public Thumb[] scrapeFanart() {
		if(!hasRunScrapeExtraFanart && scrapedExtraFanart == null)
		{
			scrapeExtraFanart();
		}
		Element posterElement = document.select("a[rel=covers]:contains(Back Cover)").first();
		if(posterElement != null)
		{
			Thumb[] posterThumbs = new Thumb[1];
			try {
				posterThumbs[0] = new Thumb(fixIPAddressOfData18(posterElement.attr("href")));
				return (Thumb[]) ArrayUtils.addAll(scrapedExtraFanart,  posterThumbs);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				if(scrapedExtraFanart != null)
					return scrapedExtraFanart;
				else return new Thumb[0];
			}
		}
		if(scrapedExtraFanart != null)
			return scrapedExtraFanart;
		else return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		hasRunScrapeExtraFanart = true;
		if(scrapedExtraFanart != null)
		{
			return scrapedExtraFanart;
		}
		//find split scene links from a full movie
		Elements sceneContentLinks = document.select("div[onmouseout]:matches(Scene \\d\\d?)");
		ArrayList<String> contentLinks = new ArrayList<>();
		ArrayList<Thumb> extraFanart = new ArrayList<>();
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
						contentLinks.add(contentID);
					}
				}
			}
		}

                // Checking for changed and/or different contentIDs from the main/root item and building a new array
                ArrayList<String> galleryLinks = new ArrayList<>();
		for(String myID : contentLinks)
		{
                    String currentGalleryURL = "http://www.data18.com/content/" + myID;
                    try {

                            Document galleryDocument = Jsoup.connect(currentGalleryURL).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
                            if(galleryDocument!= null)
                            {                    
                                Elements galleryElement = galleryDocument.select("div a[href*=/viewer/]");
                                Element linkElement = galleryElement.select("a[href*=/viewer/").first();
                                if(linkElement != null)
                                    {
                                    String linkElementURL = linkElement.attr("href");
                                    if(linkElementURL.contains("/"))
                                    {
                                         String [] parts = linkElementURL.split("/");
                                         galleryLinks.add(parts[4]);
                                    }
                                }
                            }
                    } catch (IOException e) {
                            e.printStackTrace();
                            //continue; 
                    }
                }                
                
                // Results would be duplicated due to "Scene 1: xxxxxxxx" as well as "scene 1" later.  
                // Just removing those to get a clean list of galleries
                ArrayList<String> resultList = new ArrayList<>();
                HashSet<String> set = new HashSet<>();
                for (String link : galleryLinks){
                    if (!set.contains(link)){
                        resultList.add(link);
                        set.add(link);
                    }
                }
                

		//for each id, go to the viewer page for that ID
		//for(String contentID : contentLinks)
		for(String contentID : resultList)
		{
			//int viewerPageNumber = 1;
			for(int viewerPageNumber = 1; viewerPageNumber <= 15; viewerPageNumber++)
			{
				String currentViewerPageURL = "http://www.data18.com/viewer/" + contentID + "/" + String.format("%02d", viewerPageNumber);
				try {
					Document viewerDocument = Jsoup.connect(currentViewerPageURL).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
					if(viewerDocument!= null)
					{
						Element imgElement = viewerDocument.select("div#post_view a[href*=/viewer/] img").first();
						if(imgElement != null)
						{
							String mainImageUrl = imgElement.attr("src");
							Thumb thumbToAdd = new Thumb(fixIPAddressOfData18(mainImageUrl));
							String previewURL = mainImageUrl.substring(0,mainImageUrl.length()-6) + "th8/" + mainImageUrl.substring(mainImageUrl.length()-6,mainImageUrl.length());
							if(fileExistsAtURL(previewURL))
								thumbToAdd.setPreviewURL(new URL(fixIPAddressOfData18(previewURL)));
                                                        //System.out.println("Scraped Viewer: " + currentViewerPageURL);
                                                        thumbToAdd.setViewerURL(new URL(currentViewerPageURL));
							extraFanart.add(thumbToAdd);
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					//continue;
			}
			
			}
		}
     		scrapedExtraFanart = extraFanart.toArray(new Thumb[extraFanart.size()]);
                System.out.println("Number of Thumbs: " + scrapedExtraFanart.length); 
                    
		return scrapedExtraFanart;
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		Elements genreElements = document.select("div.gen12:has(b:containsOwn(Categories:)) p a[href*=/movies/], div.p8:has(div:containsOwn(Categories:)) a[href*=/movies/]");
		//System.out.println("genreElements = " + genreElements);
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
		ArrayList<Actor> actorList = new ArrayList<>();
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
		
		Elements otherActors = document.select("[href^=http://www.data18.com/dev/]");
		if(otherActors != null) {
			for (Element element : otherActors) {
				String actorName = element.attr("alt");
				actorName = element.childNode(0).toString();
				actorList.add(new Actor(actorName, null, null));
			}
		}
		
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
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
		Element studioElement = document.select("div div.p8 div p a[href*=/studios/").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text().trim();
			if(studioText != null && studioText.length() > 0)
				return new Studio(studioText);
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileBaseName;
		if(file.isFile())
			fileBaseName = FilenameUtils.getBaseName(Movie.getUnstackedMovieName(file));
		else
			fileBaseName = file.getName();
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
		//System.out.println("Trying to scrape with URL = " + searchString);
		if(useSiteSearch)
		{
			ArrayList<SearchResult> linksList = new ArrayList<>();
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements movieSearchResultElements = doc.select("div[style=float: left; padding: 6px; width: 130px;]");
			if(movieSearchResultElements == null || movieSearchResultElements.size() == 0)
			{
				this.useSiteSearch = false;
				return getLinksFromGoogle(fileName.replace("-", ""), "data18.com/movies/");
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
					if(releaseDateMap == null)
						releaseDateMap = new HashMap<>();
					//I'm putting into a static variable that never gets freed, so this could be a potential memory leak
					//TODO: find a better way to do this without a global variable
					releaseDateMap.put(currentMovieURL, releaseDateText);
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
	@Override
	public String toString(){
		return "Data18Movie";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new Data18MovieParsingProfile();
	}

	@Override
	public String getParserName() {
		return "Data18 Movie";
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		//Unfortunately this data is not available on full on the page we are scraping, so we store the info from the search result
		//creation and retrieve it here
		if(releaseDateMap != null && releaseDateMap.containsKey(document.location()))
		{
			String releaseDate = releaseDateMap.get(document.location());
			if(releaseDate != null && releaseDate.length() > 4)
				return new ReleaseDate(releaseDate);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
	}
	
	@Override
	public boolean requiresSecurityPassthrough(Document document) {
		return Data18SharedMethods.requiresSecurityPassthrough(document);
	}


	@Override
	public Document runSecurityPassthrough(Document document, SearchResult originalSearchResult) {
		return Data18SharedMethods.runSecurityPassthrough(document, originalSearchResult);
	}

}
