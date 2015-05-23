package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile.ScraperGroupName;
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
import moviescraper.doctord.dataitem.Thumb;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class R18ParsingProfile extends SiteParsingProfile implements SpecificProfile {
	
	@Override
	public String getParserName() {
		return "R18.com";
	}
	
	@Override
	public List<ScraperGroupName> getScraperGroupNames()
	{
		if(groupNames == null)
			groupNames = Arrays.asList(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
		return groupNames;
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
		// r18 does not have a title in japanese :(
		return OriginalTitle.BLANK_ORIGINALTITLE;
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
		Element element = document.select("object#FreeViewPlayer>param[name=flashvars]").first();
		if (element != null){
			String flashvars = element.attr("value");
			Pattern pattern = Pattern.compile("^.*&fid=(.+)&.*&bid=(\\d)(w|s)&.*$");
			Matcher matcher = pattern.matcher(flashvars);
			if (matcher.matches()){
				String cid = matcher.group(1);
				int bitrates = Integer.parseInt(matcher.group(2));
				String ratio = matcher.group(3);
				String quality = (bitrates & 0b100) != 0 ? "dmb" : (bitrates & 0b010) != 0 ? "dm" : "sm";
				String firstLetterOfCid = cid.substring(0,1);
				String threeLetterCidCode = cid.substring(0,3);
				
				String trailerURL = String.format("http://cc3001.r18.com/litevideo/freepv/%1$s/%2$s/%3$s/%3$s_%4$s_%5$s.mp4", 
			 			firstLetterOfCid, threeLetterCidCode, cid, quality, ratio);
				
				return new Trailer(trailerURL);
			}
			
		}
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
					try {
						thumbList.add(new Thumb(fullImagePath));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		//we could save time by not doing this check, but we might get broken images if the site changes their format
		//to speed things up a bit, we will check one image and assume the rest is OK
		if(thumbList.size() > 0 && !fileExistsAtURL(thumbList.get(thumbList.size()/2).getThumbURL().toString()))  {
			System.err.println("We expected to find extra fanart and did not at: " + document.location());
			return new Thumb[0];
		}
		
		return thumbList.toArray(new Thumb[thumbList.size()]);
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {		
		Element idElement = document.select("div.product-details dl dt:contains(Content ID:) ~ dd").first();
		if(idElement != null && idElement.text().length() > 0 )
		{
			// Some h.m.p titles does not feature the correct Content ID. We need to get it from another location.
			
			if (idElement.text().startsWith("41hodv") && scrapeStudio().getStudio().equals("h.m.p")) {
				Element wishListElement = document.select("div.js-add-to-wishlist[data-wishlist-id]").first();
				if (wishListElement != null) {
					return new ID(DmmParsingProfile.fixUpIDFormatting(wishListElement.attr("data-wishlist-id")));
				}
			}
			
			String r18ID = idElement.text();
			return new ID(DmmParsingProfile.fixUpIDFormatting(r18ID));
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
			return String.format("%s+%05d", groupOne, number);
		}
				
		return String.format("%s%05d", groupOne, number);
	}
	
	private SearchResult[] searchResultOnR18(String searchWord)	{
		URLCodec codec = new URLCodec();
		String searchWordURLEncoded;
		try {
			searchWordURLEncoded = codec.encode(searchWord);
			String searchPattern = "http://www.r18.com/common/search/floor=movies/searchword=" + searchWordURLEncoded + "/";
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
					Thumb previewImage = new Thumb(searchResult.select("img").first().attr("data-original"));
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
