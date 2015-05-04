package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.Movie;
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

public class R18ParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private ID id;
	private OriginalTitle originalTitle;
	private SearchResult[] searchResultsFromR18; //if we found something with the site search and didn't have to use a google search
	private DmmParsingProfile cachedDmmParseFromIdSearch;
	private Movie dmmScrapedMovie;
	
	@Override
	public String getParserName() {
		return "R18.com";
	}
	
	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("cite[itemprop=name]").first();
		if(titleElement != null)
			return new Title(titleElement.text());
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		if(originalTitle != null && originalTitle.getOriginalTitle().length() > 0)
			return originalTitle;
		if(id == null)
		{
			id = scrapeID(); 
		}
		if(id != null && cachedDmmParseFromIdSearch != null)
		{
			return cachedDmmParseFromIdSearch.scrapeOriginalTitle();
		}
		else return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		//no SortTitle - the user usually provides their own
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		Element setElement = document.select("div.product-details dl dt:contains(Series:) + dd a").first();

		if(setElement != null)
		{
			String setText = setElement.text().trim();
			if(setText.endsWith("..."))
			{
				System.out.println("Visiting set page to get full text");
				try
				{
					Document setDocument = SpecificScraperAction.downloadDocument(setElement.attr("href"));
					Element setElementFullText = setDocument.select("div.cmn-ttl-tabMain01 div.txt01").first();
					if(setElementFullText != null)
					{
						return new Set(setElementFullText.text());
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return new Set(setText);
				}
			}
			return new Set(setText);
		}
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		//this site doesn't have ratings
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		Element releaseDateElement = document.select("div.product-details dl dt:contains(Release Date) ~ dd").first();
		if(releaseDateElement != null && releaseDateElement.text().length() >= 4)
		{
			//just grab the last 4 letters of the text - that should be the year
			return new Year(releaseDateElement.text().substring(releaseDateElement.text().length() - 4));
		}
		return Year.BLANK_YEAR;
	}

	@Override
	public Top250 scrapeTop250() {
		return Top250.BLANK_TOP250;
	}
	
	@Override
	public Trailer scrapeTrailer() {
		if(dmmScrapedMovie == null)
		{
			scrapeID();
		}
		if(dmmScrapedMovie != null && dmmScrapedMovie.getTrailer() != null)
			return dmmScrapedMovie.getTrailer();
		return Trailer.BLANK_TRAILER;
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
		Element plotElement = document.select("div.cmn-box-description01 h1 ~ p").first();
		if(plotElement != null)
			return new Plot(plotElement.text());
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("div.product-details dl dt:contains(Runtime:) ~ dd").first();
		if(runtimeElement != null && runtimeElement.text().length() > 0)
		{
			String runtimeText = runtimeElement.text();
			runtimeText = runtimeText.replace(" min.", "");
			return new Runtime(runtimeText);
		}
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart(true);
	}
	
	private Thumb[] scrapePostersAndFanart(boolean doCrop)
	{
		Element dvdBoxartElement = document.select("section div.box01.mb10.detail-view.detail-single-picture img").first();
		if(dvdBoxartElement != null)
		{
			String imgSrc = dvdBoxartElement.attr("src");
			if(imgSrc.length() > 0)
			{
				try {
					Thumb poster = new Thumb(imgSrc, doCrop);
					Thumb [] posterArray = {poster};
					return posterArray;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		List<Thumb> thumbList = new LinkedList<Thumb>();
		
		Elements previewProductGalleryImgLinks = document.select(".product-gallery li a img");
		
		if(previewProductGalleryImgLinks != null)
		{
			for(Element currentPreviewImage : previewProductGalleryImgLinks)
			{
				String imgThumbnailSrc = currentPreviewImage.attr("data-original");
				if(imgThumbnailSrc != null && imgThumbnailSrc.length() > 0)
				{
					int indexOfLastDash = imgThumbnailSrc.lastIndexOf('-');
					String fullImagePath = imgThumbnailSrc.substring(0,indexOfLastDash)+ "jp" + imgThumbnailSrc.substring(indexOfLastDash);
					//we could save time by not doing this check, but we might get broken images if the site changes their format
					if(fileExistsAtURL(fullImagePath))
					{
						try {
							thumbList.add(new Thumb(fullImagePath));
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		return thumbList.toArray(new Thumb[thumbList.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		//try to use the id scraped from DMM
		if(id != null)
			return id;
		else
		{

			
			//use the one on the R18 page to do a search on DMM and get it from there
			Element idElement = document.select("div.product-details dl dt:contains(Content ID:) ~ dd").first();
			if(idElement != null && idElement.text().length() > 0 )
			{
				String r18ID = idElement.text();
				DmmParsingProfile dmm = new DmmParsingProfile(false);
				String dmmSearchString = dmm.createSearchString(new File(r18ID));
				
				try {
					dmmScrapedMovie = Movie.scrapeMovie(new File(r18ID), dmm, "", false);
					SearchResult [] searchResultsDMM = dmm.getSearchResults(dmmSearchString);
					if(searchResultsDMM != null && searchResultsDMM.length > 0)
					{
						ID dmmID = dmmScrapedMovie.getId();
						if(dmmID != null && dmmID.getId().length() > 0)
						{
							cachedDmmParseFromIdSearch = dmm;
							id = dmmID;
							return id;
						}
					}
				} catch (IOException e) {
					//dmm search didn't work, use the r18 ID instead
					return new ID(DmmParsingProfile.fixUpIDFormatting(r18ID));
				}
			}
			

		}
		return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Elements genreElements = document.select("div.product-details dl dt:contains(Categories:) ~ dd a");
		if(genreElements != null)
		{
			for(Element currentGenre : genreElements)
			{
				String genreText = currentGenre.text();
				if(genreText.length() > 0 && !genreText.equals("Hi-Def") && !genreText.equals("Featured Actress"))
				{
					genreList.add(new Genre(genreText));
				}
					
			}
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Elements actorElementTabs = document.select("div.js-tab-contents div[id]");
		if(actorElementTabs != null)
		{
			for(Element currentActor : actorElementTabs)
			{
				String actorName = currentActor.select("div.txt01 div").first().text();
				String actorThumbUrl = currentActor.select("img").first().attr("src");
				if(actorName != null && actorName.length() > 0)
				{
					if(actorThumbUrl != null && actorThumbUrl.length() > 0 && !actorThumbUrl.contains("nowprinting"))
					{
						Thumb actorThumb;
						try {
							actorThumb = new Thumb(actorThumbUrl);
							Actor actorWithThumb = new Actor(actorName,"",actorThumb);
							actorList.add(actorWithThumb);
						} catch (MalformedURLException e) {
							e.printStackTrace();
							Actor actorWithoutThumb = new Actor(actorName,"",null);
							actorList.add(actorWithoutThumb);
						}
						
					}
					else if(actorName != null)
					{
						Actor actorWithoutThumb = new Actor(actorName,"",null);
						actorList.add(actorWithoutThumb);
					}
				}
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> directorList = new ArrayList<Director>();
		Element studioElement = document.select("div.product-details dl dt:contains(Director:) + dd").first();
		if(studioElement != null)
		{
			String directorText = studioElement.text();
			if(directorText.length() > 0 && !directorText.startsWith("-"))
				directorList.add(new Director(directorText,null));
		}
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		Element studioElement = document.select("div.product-details dl dt:contains(Studio:) + dd a").first();
		if(studioElement != null)
		{
			String studioText = studioElement.text();
			if(studioText.length() > 0)
				return new Studio(studioText);
		}
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {

		// The general approach is search for 'tag' + '5-digit 0-padded number'.
		// This gets pretty good results, usually a perfect match, 
		// or 2 to 5 results for clashing ids - still good for manual picking.
		
		String baseId = findIDTagFromFile(file, isFirstWordOfFileIsID()).replace("-", "");
		Pattern patternID = Pattern.compile("([0-9]*\\D+)(\\d+)");
		Matcher matcher = patternID.matcher(baseId);
		String groupOne = "";
		String groupTwo = "";
		while (matcher.find()) {
		    groupOne = matcher.group(1);
		    groupTwo = matcher.group(2);
		}

		if (groupOne == null || groupOne.isEmpty() || groupTwo == null || groupTwo.isEmpty())
			return null;
		
		int number = Integer.parseInt(groupTwo);

		// some h.m.p. titles need extra padding
		
		if (groupOne.toUpperCase().equals("HODV")) {
			return String.format("%s%06d", groupOne, number);
		}
				
		return String.format("%s%05d", groupOne, number);
	}
	
	private SearchResult[] searchResultOnR18(String searchWord)	{
		URLCodec codec = new URLCodec();
		String searchWordURLEncoded;
		try {
			searchWordURLEncoded = codec.encode(searchWord);
			String searchPattern = "http://www.r18.com/common/search/floor=movies/searchword=" + searchWordURLEncoded;
			System.out.println("Searching on R18 with this URL:" + searchPattern);
			Document searchResultsPage = Jsoup.connect(searchPattern).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			Elements moviesFound = searchResultsPage.select(".cmn-list-product01 li");
			if(moviesFound != null && moviesFound.size() > 0)
			{
				SearchResult [] foundResults = new SearchResult[moviesFound.size()];
				int i = 0;
				for(Element searchResult : moviesFound)
				{
					String urlPath = searchResult.select("a").attr("href");
					String label = searchResult.select("img").first().attr("alt");
					Thumb previewImage = null;
					previewImage = new Thumb(searchResult.select("img").first().attr("src"));
					SearchResult searchResultToAdd = new SearchResult(urlPath, label, previewImage);
					foundResults[i] = searchResultToAdd;
					i++;
				}
				return foundResults;
			}
			
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
	private void removeZerosFromID(int numberOfZeros)
	{
		if(id != null)
		{
			Pattern patternID = Pattern.compile("([0-9]*\\D+)(\\d+)");
			Matcher matcher = patternID.matcher(id.getId());
			String groupOne = "";
			String groupTwo = "";
			while (matcher.find()) {
			    groupOne = matcher.group(1);
			    groupTwo = matcher.group(2);
			}
			if(groupOne.length() > 0 && groupTwo.length() > 0)
			{
				String newId = groupOne + groupTwo.substring(numberOfZeros,groupTwo.length());
				id.setId(newId);
			}
		}
	}
	
	
	//get a cid from DMM and use that to make a google search. if the google search returns a link on r18
	//then return it
	private String searchStringHelper(File file)
	{
		DmmParsingProfile dmm = new DmmParsingProfile(false);
		String dmmSearchString = dmm.createSearchString(file);
		int maxNumberOfTries = 4; //only do a few tries so the scraper doesn't get stuck
		try {
			SearchResult [] searchResultsDMM = dmm.getSearchResults(dmmSearchString);
			if(searchResultsDMM != null && searchResultsDMM.length > 0)
			{
				int i = 0;
				for(SearchResult currentSR : searchResultsDMM)
				{
					String urlToUse = currentSR.getUrlPath();
					if(urlToUse.contains("cid="))
					{
						String cidToUse = urlToUse.substring(urlToUse.indexOf("cid=")+4,urlToUse.length()-1);
						if(cidToUse.length() > 0)
						{
							//do a sleep before doing google searches. we're doing a lot of them are likely to get blocked
							//make the sleep somewhat random to try to make this look more like a human doing these ;)
							Random ran = new Random();
							int randomTime = ran.nextInt(2000);
							System.out.println("Sleeping thread for " + randomTime + "ms before doing google search in R18.com scraping.");
							Thread.sleep(randomTime);
							SearchResult [] googleLinkCandidate = getLinksFromGoogle(cidToUse, "r18.com");
							if(googleLinkCandidate != null && googleLinkCandidate.length > 0)
							{
								//we really just want to get the ID from DMM - the one on R18 is inconsistent
								//so we're going to save it until later so we can use it in scrapeID()
								Document document = SpecificScraperAction.downloadDocument(searchResultsDMM[i]);
								dmm.setDocument(document);
								ID idFromDMM = dmm.scrapeID();
								OriginalTitle originalTitleFromDMM = dmm.scrapeOriginalTitle();
								if(idFromDMM != null)
									id = idFromDMM;
								if(originalTitleFromDMM != null)
									originalTitle = originalTitleFromDMM;
									
								return cidToUse;
							}
						}
					}
					i++;
					if(i > maxNumberOfTries)
						break;
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		
		SearchResult[] results = null;
		
		if (searchString != null) {
			
			results = searchResultOnR18(searchString);
				
			if (results == null) {
				
				// lots of old Moodyz titles are listed by their VHS tag, 
				// those starting with 'MD' may get a good match removing the trailing 'D',
				// (MDED -> MDE, MDID -> MDI, MDLD -> MDL...)
				// result will be filtered during the amalgamation process though, need to fix that
				
				Pattern patternID = Pattern.compile("^(MD.)D(\\d+)$", Pattern.CASE_INSENSITIVE);
				Matcher matcher = patternID.matcher(searchString);
				
				if (matcher.matches()){
					String moodyzSearchPattern = matcher.replaceAll("$1$2");
					results = searchResultOnR18(moodyzSearchPattern);
				}
			}
		}

		if (results == null) {
			// results = getLinksFromGoogle(searchString, "r18.com");
			results = new SearchResult[0];
		}
		
		return results;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new R18ParsingProfile();
	}
	
	@Override
	public String toString(){
		return "R18.com";
	}

}
