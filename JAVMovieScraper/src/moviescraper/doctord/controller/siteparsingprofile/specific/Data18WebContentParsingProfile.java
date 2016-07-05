package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Data18WebContentParsingProfile extends SiteParsingProfile implements SpecificProfile{
	boolean useSiteSearch = true;
	String yearFromFilename = "";
	String fileName;
	Thumb[] scrapedPosters;
	private static final SimpleDateFormat data18ReleaseDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
	
	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("div#centered.main2 div div h1.h1big, div#centered.main2 div h1").first();
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
		
		//relatedMovie is used for split scenes
		Element relatedMovie = document.select("div.gen:contains(Related Movie and Scenes:) ~ div ~ div p a").first();
		if(relatedMovie != null && relatedMovie.text().length() > 0)
		{
			return new Set(relatedMovie.text());
		}
		//setElement used below is for web downloads
		Element setElement = document.select("div.main.gre1 div#centered.main2 div.p8.dloc a[href*=/sites/").last();
		if(setElement != null)
			return new Set(setElement.text());
		else return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// this site doesn't have ratings
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}
	
	@Override
	public ReleaseDate scrapeReleaseDate() {
		SimpleDateFormat dateFormatToUse = data18ReleaseDateFormat;
		Element releaseDateElement = document.select("div p:contains(Date:) a").first();
		//case where the date is not a hyperlink, but just a month and a year
		if ((releaseDateElement != null && releaseDateElement.text() != null && releaseDateElement
				.text().contains("errors")) || releaseDateElement == null) {
			releaseDateElement = document.select("span.gen11:containsOwn(Release date:) b:matches(.+\\d{4})").first();
			if(releaseDateElement == null)
			{
				releaseDateElement = document.select("div p:contains(Date:) b").first();
			}
			dateFormatToUse = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
		}
		if(releaseDateElement != null)
		{
			String releaseDateText = releaseDateElement.text().trim();
			if(releaseDateText.length() > 4)
			{
				return new ReleaseDate(releaseDateText, dateFormatToUse);
			}
		}
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
		Element plotElement = document.select("div.gen12 p:contains(Story:)").first();
		if(plotElement != null)
			return new Plot(plotElement.ownText());
		//maybe if this is a scene, there is a link to the full movie where we can grab the plot from there
		else {
			Element relatedMovieTextElement = document.select("div p:containsOwn(Related Movie:)").first();
			//the actual related movie is the text's parent's previous element, if it is there
			if(relatedMovieTextElement != null && relatedMovieTextElement.parent() != null && relatedMovieTextElement.parent().previousElementSibling() != null) {
				Element relatedMovieElement = relatedMovieTextElement.parent().previousElementSibling().select("a[href*=/movies/]").first();
				if(relatedMovieElement != null) {
					//Using the full movie's plot since this is a single scene and it does not have its own plot
					String urlOfMovie = relatedMovieElement.attr("href");
					if(urlOfMovie != null && urlOfMovie.length() > 0) {
						Data18MovieParsingProfile parser = new Data18MovieParsingProfile();
						parser.setOverridenSearchResult(urlOfMovie);
						try {
							Document doc = Jsoup.connect(urlOfMovie).userAgent(getRandomUserAgent()).referrer("http://www.google.com").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
							parser.setDocument(doc);
							Plot data18FullMoviePlot = parser.scrapePlot();
							System.out.println("Using full movie's plot instead of scene's plot (which wasn't found): " + data18FullMoviePlot.getPlot());
							return data18FullMoviePlot;
						}
						catch(IOException e) {
							return Plot.BLANK_PLOT;
						}
					}
				}
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
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		//If scene is from a page that has video stills, grab the posters from the trailer link
		ArrayList<Thumb> posters = new ArrayList<>();
		ArrayList<Thumb> trailerImages = new ArrayList<>();
		
		Elements trailerImgElements = document.select("img.noborder[title=Scene Preview], div#moviewrap img[src*=/big.jpg], img.noborder[alt=Play this video]:not(img.noborder[src*=play.png]), div#pretrailer a[href*=/trailer] img.noborder:not(img.noborder[src*=play.png])");
		Elements videoStills = document.select("div:containsOwn(Video Stills:) ~ div img");
		//System.out.println("Video stills = " + videoStills);
		if(trailerImgElements != null && trailerImgElements.size() > 0 && (videoStills == null || videoStills.size() == 0))
		{
			//add the trailer image
			try {
				for(Element currentTrailerElement: trailerImgElements)
				{
					trailerImages.add(new Thumb(fixIPAddressOfData18(currentTrailerElement.attr("src"))));
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//get any video stills too
			
			if(videoStills != null && videoStills.size() > 0)
			{
				try {
					for(Element currentVideoStill : videoStills)
						posters.add(new Thumb(fixIPAddressOfData18(currentVideoStill.attr("src"))));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//return posters.toArray(new Thumb[posters.size()]);
		}
		
		
		//otherwise, find split scene links from a full movie
		ArrayList<String> contentLinks = new ArrayList<>();
		String docLocation = document.location();
		//in rare cases the content id used on the viewer page is not the same as that of the url of the page itself
		String contentIDFromViewerFoundOnPage = "";
		Element viewerElementOnPage = document.select("a[href*=/viewer/").first();
		if(viewerElementOnPage != null)
		{
			String hrefContent = viewerElementOnPage.attr("href");
			int startingIndex = hrefContent.indexOf("viewer") + "viewer".length()+1;
			int endingIndex = hrefContent.lastIndexOf("/");
			if(startingIndex != -1 && endingIndex != -1)
			{
				contentIDFromViewerFoundOnPage = hrefContent.substring(startingIndex,endingIndex);
			}
		}

		//originally get it from the url
		String contentIDToUse = docLocation.substring(docLocation.lastIndexOf("/")+1,docLocation.length());
		//if we found a better id from a link on the specified page, use it instead
		if(contentIDFromViewerFoundOnPage != null && contentIDFromViewerFoundOnPage.length() > 0)
		{
			contentIDToUse = contentIDFromViewerFoundOnPage;
		}
			
		contentLinks.add(contentIDToUse);
		//for each id, go to the viewer page for that ID
		for(String contentID : contentLinks)
		{
			for(int viewerPageNumber = 1; viewerPageNumber <= 15; viewerPageNumber++)
			{
				//System.out.println("viewerPageNumber: " + String.format("%02d", viewerPageNumber));
				String currentViewerPageURL = "http://www.data18.com/viewer/" + contentID + "/" + String.format("%02d", viewerPageNumber);
				//System.out.println("currentVIewerPageURL + " + currentViewerPageURL);
				try {

					Document viewerDocument = Jsoup.connect(currentViewerPageURL).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
					if(viewerDocument!= null)
					{
						Element imgElement = viewerDocument.select("div#post_view a[href*=/viewer/] img").first();
						if(imgElement != null)
						{
							String mainImageUrl = imgElement.attr("src");
							mainImageUrl = fixIPAddressOfData18(mainImageUrl);
							if(fileExistsAtURL(mainImageUrl))
							{
								Thumb thumbToAdd = new Thumb(mainImageUrl);
								String previewURL = mainImageUrl.substring(0,mainImageUrl.length()-6) + "th8/" + mainImageUrl.substring(mainImageUrl.length()-6,mainImageUrl.length());
								previewURL = fixIPAddressOfData18(previewURL);
								if(!fileExistsAtURL(previewURL))
									previewURL = mainImageUrl.substring(0,mainImageUrl.length()-6) + "thumb2/" + mainImageUrl.substring(mainImageUrl.length()-6,mainImageUrl.length());	
								if(fileExistsAtURL(previewURL))
									thumbToAdd.setPreviewURL(new URL(previewURL));
								//System.out.println("previewURL : " + previewURL);
								posters.add(thumbToAdd);
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		
		//get "Official Poster"
		Element officialPosterElement = document.select("a img[alt=poster]").first();
		if (officialPosterElement != null) {
			try {
				Thumb officialPosterThumb = new Thumb(fixIPAddressOfData18(officialPosterElement.attr("src")));
				posters.add(officialPosterThumb);
				
				//get the trailer images too, since items with an official poster tend to not have much else in them
				posters.addAll(trailerImages);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		scrapedPosters = posters.toArray(new Thumb[posters.size()]);
		if(scrapedPosters != null && scrapedPosters.length > 0)
			return scrapedPosters;
		else 
		{
			scrapedPosters = trailerImages.toArray(new Thumb[trailerImages.size()]);
			return scrapedPosters;
		}
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
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		//Get numerical ID that is at end of URL
		if(document.location().matches(".*/content/[0-9]+"))
		{
			String id = document.location().substring(document.location().lastIndexOf('/'));
			id = id.replace("/","");
			if(id != null && id.length() > 0)
				return new ID(id);
		}
		return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<>();
		//Elements genreElements = document.select("span.gensmall ~ a");
		Elements genreElements = document.select("div.p8 div div div:contains(Categories) a, div.gen12 div:contains(Categories) a");
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
		ArrayList<Actor> actorList = new ArrayList<>();
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
						Document actorPage = Jsoup.connect(actorPageLink).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
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
								Actor actorToAdd = new Actor(actorName, null, new Thumb(actorThumbnail));
								if(!actorList.contains(actorToAdd))
									actorList.add(actorToAdd);
							} catch (MalformedURLException e) {
								Actor actorToAdd = new Actor(actorName, null, null);
								if(!actorList.contains(actorToAdd))
									actorList.add(actorToAdd);
								e.printStackTrace();
							}
						}
						//add the actor with no thumbnail
						else
						{
							Actor actorToAdd = new Actor(actorName, null, null);
							if(!actorList.contains(actorToAdd))
								actorList.add(new Actor(actorName, null, null));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}


			}
		}
		//Actors without pictures
		Elements otherActors = document.select("[href^=http://www.data18.com/dev/]");
		if(otherActors != null) {
		    for (Element element : otherActors) {
		        String actorName = element.attr("alt");
		        actorName = element.childNode(0).toString();
		        Actor actorToAdd = new Actor(actorName, null, null);
				if(!actorList.contains(actorToAdd))
					actorList.add(new Actor(actorName, null, null));
		    }
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<>();
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		//Element studioElement = document.select("div.main.gre1 div#centered.main2 a:contains(Sites) ~ a").first();
		Element studioElement = document.select("div.main.gre1 div#centered.main2 div.p8.dloc a[href*=/sites/").first();
		//often seen in split scenes
		if(studioElement == null)
			studioElement = document.select("div div.p8.dloc a[href*=/studios/").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text();
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
			ArrayList<SearchResult> linksList = new ArrayList<>();
			Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements movieSearchResultElements = doc.select("div.bscene");
			if(movieSearchResultElements == null || movieSearchResultElements.size() == 0)
			{
				this.useSiteSearch = false;
				SearchResult[] googleResults = getData18LinksFromGoogle(fileName);
				return googleResults;
			}
			else
			{
				for(Element currentMovie : movieSearchResultElements)
				{
					String currentMovieURL = currentMovie.select("a").first().attr("href");
					String currentMovieTitle = currentMovie.select("a").first().text();
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
			SearchResult[] googleResults = getData18LinksFromGoogle(fileName);
			return googleResults;
		}
	}
	@Override
	public String toString(){
		return "Data18 Web Content";
	}
	public SearchResult[] getData18LinksFromGoogle(String fileName){
		String replacedDateNumbersWithWordsFileName = replaceFilenameNumericalDateWithWords(fileName);
		if(!replacedDateNumbersWithWordsFileName.equals(fileName))
			System.out.println("Searching google with date replaced file name: " + replacedDateNumbersWithWordsFileName);
		SearchResult[] googleResults = getLinksFromGoogle(replacedDateNumbersWithWordsFileName, "data18.com/content/");
		googleResults = removeInvalidGoogleResults(googleResults);
		if(googleResults == null || googleResults.length == 0)
		{
			googleResults = getLinksFromGoogle(replacedDateNumbersWithWordsFileName.replaceAll("[0-9]", ""), "data18.com/content/");
			googleResults = removeInvalidGoogleResults(googleResults);
		}
		return googleResults;
	}
	
	/**
	 * Data 18 search work better on google if we search using the date
	 * However, most file releases have the date in numerical format (i.e. 2014-05-26 or 14.05.26)
	 * which causes problems with the google search. this function will convert the month to the english month name
	 * and the year to the 4 digit year if it was in 2 digit format (it's assumed the year is 20XX)
	 * @param fileName
	 * @return the fileName with the year and month replaced
	 */
	private String replaceFilenameNumericalDateWithWords(String fileName)
	{
		String newFileName = fileName;
		String patternString = "\\D*([0-9]{2,4})[ \\._-]*([0-9]{1,2})[ \\._-]*([0-9]{1,2}).*";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(fileName);
		if(matcher.matches())
		{
			StringBuilder fileNameBuilder = new StringBuilder(fileName);
			
			//the year was only the 2 digits, we want it to be 4 digits, so put "20" in front of the year
			boolean replacedYear = false;
			if(matcher.group(2).length() == 2)
			{
				int yearIndexBegin = matcher.start(1);
				int yearIndexEnd = matcher.end(1);
				fileNameBuilder.replace(yearIndexBegin, yearIndexEnd, "20" + matcher.group(1));
				replacedYear = true;
			}
			int monthIndexBegin = matcher.start(2);
			int monthIndexEnd = matcher.end(2);
			if(replacedYear)
			{
				monthIndexBegin += 2;
				monthIndexEnd += 2;
			}
			
			//Data18 google search works better with the month name spelled out
			fileNameBuilder.replace(monthIndexBegin, monthIndexEnd, formatMonth(matcher.group(2)));
			newFileName = fileNameBuilder.toString();
		}
		return newFileName;
	}
	
	public String formatMonth(String monthString) {
		int month = Integer.parseInt(monthString);
	    DateFormatSymbols symbols = new DateFormatSymbols(Locale.ENGLISH);
	    String[] monthNames = symbols.getMonths();
	    return monthNames[month - 1];
	}
	
	/**
	 * Removes links from google that do not point to actual web content
	 * @param googleResults
	 * @return
	 */
	public SearchResult[] removeInvalidGoogleResults(SearchResult [] googleResults)
	{
		LinkedList<SearchResult> modifiedSearchResultList = new LinkedList<>();
		for(int i = 0; i < googleResults.length; i++)
		{
			//System.out.println("initial goog results = " + googleResults[i].getUrlPath());
			if(googleResults[i].getUrlPath().matches("http://www.data18.com/content/\\d+/?"))
			{
				//System.out.println("match = " + googleResults[i]);
				modifiedSearchResultList.add(googleResults[i]);
			}
		}
		return modifiedSearchResultList.toArray(new SearchResult[modifiedSearchResultList.size()]);
	}
	
	
	@Override
	public SiteParsingProfile newInstance() {
		return new Data18WebContentParsingProfile();
	}

	@Override
	public String getParserName() {
		return "Data18 Web Content";
	}
}
