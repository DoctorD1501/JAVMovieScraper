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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.DmmParsingProfile;
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
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class R18ParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private ID id;
	private OriginalTitle originalTitle;
	
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
			return new Set(setElement.text());
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
			//use the one on the R18 page
			Element idElement = document.select("div.product-details dl dt:contains(Content ID:) ~ dd").first();
			if(idElement != null && idElement.text().length() > 0 )
				return new ID(idElement.text());
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
				if(genreText.length() > 0 && !genreText.equals("Hi-Def"))
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

	//It's pretty hard to find a match on r18.com - their search sucks. So we need to try to find
	//a potential cid on dmm (their search is better) to try to get the exact match on r18
	//we'll also check to see if we get a google result with our cid before actually returning it
	@Override
	public String createSearchString(File file) {
		String attemptOneBaseFileName = searchStringHelper(file);
		if(attemptOneBaseFileName != null)
			return attemptOneBaseFileName;
		
		//Well that didn't work if we are still here, so let's try to guess what the ID is on R18 using some simple rules
		//let's try to put some more zeros in the file name between the first part of the  ID and the numbers at the end
		//We need to do this since we'll actually get different results on DMM by putting in some extra zeros
		String baseId = findIDTagFromFile(file).replace("-", "");
		Pattern patternID = Pattern.compile("([0-9]*\\D+)(\\d+)");
		Matcher matcher = patternID.matcher(baseId);
		String groupOne = "";
		String groupTwo = "";
		while (matcher.find()) {
		    groupOne = matcher.group(1);
		    groupTwo = matcher.group(2);
		}
		
		String attemptFileNameWithOneExtraZero = searchStringHelper(new File(groupOne + "0" + groupTwo));
		if(attemptFileNameWithOneExtraZero != null)
		{
			//get rid of the extra zeros we put in
			removeZerosFromID(1);
			return attemptFileNameWithOneExtraZero;
		}
		
		String attemptFileNameWithTwoExtraZero = searchStringHelper(new File(groupOne + "00" + groupTwo));
		if(attemptFileNameWithTwoExtraZero != null)
		{
			//get rid of the extra zeros we put in
			removeZerosFromID(2);
			return attemptFileNameWithTwoExtraZero;
		}
		
		String attemptFileNameWithThreeExtraZero = searchStringHelper(new File(groupOne + "000" + groupTwo));
		if(attemptFileNameWithThreeExtraZero != null)
		{
			//get rid of the extra zeros we put in
			removeZerosFromID(3);
			return attemptFileNameWithThreeExtraZero;
		}
		
		String attemptFileNameWithFourExtraZero = searchStringHelper(new File(groupOne + "0000" + groupTwo));
		if(attemptFileNameWithFourExtraZero != null)
		{
			//get rid of the extra zeros we put in
			removeZerosFromID(4);
			return attemptFileNameWithFourExtraZero;
		}
		
		//after all that we still didn't find anything, oh well, it happens! maybe the method above can be improved
		//if we're positive r18 should have had a match on that file, so if you're reading this comment
		//feel free to add some more cases like above to try to get a match :)
		return null;
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
							int randomTime = ran.nextInt(2000) + 3500;
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
		return getLinksFromGoogle(searchString, "r18.com");
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new R18ParsingProfile();
	}

}
