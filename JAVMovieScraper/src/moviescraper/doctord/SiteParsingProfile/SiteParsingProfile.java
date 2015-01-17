package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.Language;
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
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;
import moviescraper.doctord.model.AbstractMovieScraper;
import moviescraper.doctord.model.GenericMovieScraper;
import moviescraper.doctord.preferences.MoviescraperPreferences;

public abstract class SiteParsingProfile {
	
	protected Language scrapingLanguage;

	public Document document; // the base page to start parsing from
	
	public String overrideURLDMM;
	
	private boolean extraFanartScrapingEnabled = false;
	
	/**
	 * If this has a value when scraping, will use overridenSearchResult 
	 * from a user provided URL without looking at file name
	 */
	private SearchResult overridenSearchResult; 
	
	public boolean isExtraFanartScrapingEnabled() {
		return extraFanartScrapingEnabled;
	}

	public void setExtraFanartScrapingEnabled(boolean extraFanartScrapingEnabled) {
		this.extraFanartScrapingEnabled = extraFanartScrapingEnabled;
	}

	public String getOverrideURLDMM() {
		return overrideURLDMM;
	}

	public void setOverrideURLDMM(String overrideURL) {
		this.overrideURLDMM = overrideURL;
	}

	public SiteParsingProfile(Document document) {
		this.document = document;
		overrideURLDMM = null;
		scrapingLanguage = Language.ENGLISH;
	}
	
	public SiteParsingProfile(){
		scrapingLanguage = Language.ENGLISH;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * Sets the {@link SiteParsingProfile#overridenSearchResult} to the URL defined by @param urlPath
	 * This will cause the scraper to ignore the file name of the file when scraping
	 * @param urlPath
	 */
	public void setOverridenSearchResult(String urlPath)
	{
		overridenSearchResult = new SearchResult(urlPath);
	}
	
	/**
	 * @return {@link SiteParsingProfile#overridenSearchResult}
	 */
	public SearchResult getOverridenSearchResult()
	{
		return overridenSearchResult;
	}
	
	
	//Gets the ID number from the file and considers stripped out multipart file identifiers like CD1, CD2, etc
	//The ID number needs to be the last word in the filename or the next to the last word in the file name if the file name
	//ends with something like CD1 or Disc 1
	//So this filename "My Movie ABC-123 CD1" would return the id as ABC-123
	//This filename "My Movie ABC-123" would return the id as ABC-123
	public static String findIDTagFromFile(File file)
	{
		String fileNameNoExtension;
		if(file.isDirectory())
		{
			fileNameNoExtension = file.getName();
		}
		else fileNameNoExtension = FilenameUtils.removeExtension(file.getName());
		String fileNameNoExtensionNoDiscNumber = stripDiscNumber(fileNameNoExtension);
		String[] splitFileName = fileNameNoExtensionNoDiscNumber.split(" ");
		String lastWord = splitFileName[splitFileName.length-1];
		
		//Some people like to enclose the ID number in parenthesis or brackets like this (ABC-123) or this [ABC-123] so this gets rid of that
		//TODO: Maybe consolidate these lines of code using a single REGEX?
		lastWord = lastWord.replace("(","");
		lastWord = lastWord.replace(")","");
		lastWord = lastWord.replace("[","");
		lastWord = lastWord.replace("]","");
		return lastWord;
	}

	public static String stripDiscNumber(String fileNameNoExtension) {
		//replace <cd/dvd/part/pt/disk/disc/d> <0-N>  (case insensitive) with empty
		String discNumberStripped =  fileNameNoExtension.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[0-9]+$", "");
		//replace <cd/dvd/part/pt/disk/disc/d> <a-d> (case insensitive) with empty
		discNumberStripped = discNumberStripped.replaceAll("(?i)[ _.]+(?:cd|dvd|p(?:ar)?t|dis[ck]|d)[ _.]*[a-d]$","");
		return discNumberStripped.trim();
	}

	public abstract Title scrapeTitle();

	public abstract OriginalTitle scrapeOriginalTitle();

	public abstract SortTitle scrapeSortTitle();

	public abstract Set scrapeSet();

	public abstract Rating scrapeRating();

	public abstract Year scrapeYear();

	public abstract Top250 scrapeTop250();

	public abstract Votes scrapeVotes();

	public abstract Outline scrapeOutline();

	public abstract Plot scrapePlot();

	public abstract Tagline scrapeTagline();

	public abstract moviescraper.doctord.dataitem.Runtime scrapeRuntime();

	public abstract Thumb[] scrapePosters();

	public abstract Thumb[] scrapeFanart();
	
	public abstract Thumb[] scrapeExtraFanart();

	public abstract MPAARating scrapeMPAA();

	public abstract ID scrapeID();

	public abstract ArrayList<Genre> scrapeGenres();

	public abstract ArrayList<Actor> scrapeActors();

	public abstract ArrayList<Director> scrapeDirectors();

	public abstract Studio scrapeStudio();
	
	public  abstract String createSearchString(File file);
	
	public Trailer scrapeTrailer() {
		return Trailer.BLANK_TRAILER;
	}
	
	public abstract SearchResult[] getSearchResults(String searchString) throws IOException;
	
	public SearchResult [] getLinksFromGoogle(String searchQuery, String site)
	{
		//System.out.println("calling get links from google with searchQuery = " + searchQuery);
		Document doc;
		ArrayList<SearchResult> linksToReturn = new ArrayList<SearchResult>();
	    try{
	    	String encodingScheme = "UTF-8";
	    	String queryToEncode = "site:" + site + " " + searchQuery;
	    	String encodedSearchQuery = URLEncoder.encode(queryToEncode, encodingScheme);
	        doc = Jsoup.connect("https://www.google.com/search?q="+encodedSearchQuery).userAgent(getRandomUserAgent()).referrer("http://www.google.com").ignoreHttpErrors(true).timeout(0).get();
	        Elements sorryLink = doc.select("form[action=CaptchaRedirect] input");
	        Map<String, String> captchaData = new HashMap<>();
	        for (Element element : sorryLink) {
	        	String key = element.attr("name");
	        	String value = element.attr("value");
	        	captchaData.put(key, value);
			}
	        if ( captchaData.size() > 0 )
	        {
	        	System.out.println("Found Captchadata : " + captchaData);
	        	return new SearchResult[0];
	        }
	        
	        Elements links = doc.select("li[class=g]");
	        for (Element link : links) {	            
	            Elements hrefs = link.select("h3.r a");
	            String href = hrefs.attr("href");
	            href = URLDecoder.decode(href, encodingScheme);
	            href = href.replaceFirst(Pattern.quote("/url?q="), "");
	            href = href.replaceFirst(Pattern.quote("http://www.google.com/url?url="),"");
	            //remove some junk referrer stuff
	            int startIndexToRemove = href.indexOf("&sa=");
	            if (startIndexToRemove > -1)
	            	href = href.substring(0, startIndexToRemove);
	            linksToReturn.add(new SearchResult(href,hrefs.text()));
	        }
	        return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	        return linksToReturn.toArray(new SearchResult[linksToReturn.size()]);
	    }
	}

	protected static boolean fileExistsAtURL(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      // note : you may also need
	      //        HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con =
	         (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	  }

	public AbstractMovieScraper getMovieScraper() {
		return new GenericMovieScraper(this);
	}
	
	
	public abstract SiteParsingProfile newInstance();

	public Language getScrapingLanguage() {
		return scrapingLanguage;
	}

	public void setScrapingLanguage(Language scrapingLanguage) {
		this.scrapingLanguage = scrapingLanguage;
	}
	
	public void setScrapingLanguage(MoviescraperPreferences preferences)
	{
		if(preferences.getScrapeInJapanese())
			scrapingLanguage = Language.JAPANESE;
		else
			scrapingLanguage = Language.ENGLISH;
	}
	
	public abstract String getParserName();
	
	public String toString(){
		return getParserName();
	}
	
	/**
	 * Maybe we are less likely to get blocked on google if we don't always use the same user agent when searching,
	 * so this method is designed to pick a random one from a list of valid user agent strings
	 * @return a random user agent string that can be passed to .userAgent() when calling Jsoup.connect
	 */
	public String getRandomUserAgent()
	{
		String[] userAgent = {"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6",
				"Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
				"Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",
				"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0) Opera 12.14",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A",
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; tr-TR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27",
				"Mozilla/5.0 (Windows; U; Windows NT 5.0; en-en) AppleWebKit/533.16 (KHTML, like Gecko) Version/4.1 Safari/533.16"};
		return userAgent[new Random().nextInt(userAgent.length)];
	}
	
}
