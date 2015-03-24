package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
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

public class SquarePlusParsingProfile extends SiteParsingProfile implements SpecificProfile {

	public SquarePlusParsingProfile(Document document) {
		super(document);
	}

	public SquarePlusParsingProfile() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Title scrapeTitle() {

		Element titleElement = document
				.select("div.product-name.page-title h1")
				.first();
		//remove the ID number off the end of the title, if it exists
		if(titleElement != null)
		{
			String titleElementText = titleElement.text().trim();
			if(titleElementText.contains("("))
			{
				titleElementText = titleElementText.substring(0, StringUtils.lastIndexOf(titleElementText,"("));
			}
			return new Title(titleElementText);
		}
		//this shouldn't really ever happen...
		else return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		//Does not have original japanese title, so don't return anything
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		// Site doesn't have any set information
		return Set.BLANK_SET;

	}

	@Override
	public Rating scrapeRating() {
		//site doesn't have a rating
		return Rating.BLANK_RATING;
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = document.select("th.label:containsOwn(Release date) ~ td").first();
		if(yearElement != null && yearElement.text().length() >= 4)
		{
			String yearText = yearElement.text();
			yearText = yearText.substring(yearText.length()-4);
			return new Year(yearText);
		}
		return Year.BLANK_YEAR;
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on ActionJav
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
			return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		Element runtimeElement = document.select("th.label:containsOwn(Play Time) ~ td").first();
		if(runtimeElement != null)
			return new Runtime(runtimeElement.text());
		else return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		return scrapePostersAndFanart(true);
	}

	@Override
	public Thumb[] scrapeFanart() {
		return scrapePostersAndFanart(false);
	}
	
	private Thumb[] scrapePostersAndFanart(boolean doCrop)
	{
		Element boxArtElement = document.select("p.product-image a").first();
		if(boxArtElement != null)
		{
			Thumb poster;
			try {
				poster = new Thumb(boxArtElement.attr("href"), doCrop);
				Thumb[] posters = {poster};
				return posters;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		// It's always XXX content on ActionJav! ;)
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Element idElement = document
				.select("div.page-title h1")
				.first();
		//just get the ID number off the end of the title, if it exists
		if(idElement != null && idElement.text().contains("("))
		{
			String idElementText = idElement.text().trim();
			idElementText = idElementText.substring(StringUtils.lastIndexOf(idElementText,"(")+1, idElementText.length()-1);
			return new ID(idElementText);
		}
		//maybe some titles don't have ID numbers on squareplus or we got some other error
		else return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		Element genreElement = document.select("th.label:containsOwn(Genre) ~ td").first();
		if(genreElement != null)
		{
			String [] actorSplitList = genreElement.text().split(",");
			for(String genreToAdd : actorSplitList)
				genreList.add(new Genre(genreToAdd));
		}
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		
		Element featuringElement = document.select("th.label:containsOwn(Featuring) ~ td").first();
		if(featuringElement != null)
		{
			String [] actorSplitList = featuringElement.text().split(",");
			for(String actorToAdd : actorSplitList)
				actorList.add(new Actor(actorToAdd,"",null));
		}
		
		Element starringElement = document.select("th.label:containsOwn(Starring) ~ td").first();
		if(starringElement != null)
		{
			String [] actorSplitList = starringElement.text().split(",");
			for(String actorToAdd : actorSplitList)
				actorList.add(new Actor(actorToAdd,"",null));
		}
		return actorList;	
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//SquarePlus doesn't have director information, so just return an empty list
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		return Studio.BLANK_STUDIO;
	}

	@Override
	public String createSearchString(File file) {
		String fileNameNoExtension = findIDTagFromFile(file);
		return fileNameNoExtension;
		/*URLCodec codec = new URLCodec();
		try {
			String fileNameURLEncoded = codec.encode(fileNameNoExtension);
			return fileNameURLEncoded;
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		*/
		
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		/*//System.out.println("searchString = " + searchString);
		Document searchResultsPage = Jsoup.connect(searchString).referrer("http://google.com").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
		Elements dvdLinks = searchResultsPage
				.select("h3.r a");
		//String[] searchResults = new String[dvdLinks.size()];
		ArrayList<String> searchResults = new ArrayList<String>();
		for (int i = 0; i < dvdLinks.size() ; i++) {
			String currentLink = dvdLinks.get(i).attr("href");
			//don't add in things from the gallery, these aren't the movie page
			if (!currentLink.contains("gallery"))
			{
				System.out.println("adding in " + currentLink);
				searchResults.add(currentLink);
				//searchResults[i] = currentLink;
			}
			//System.out.println("currentLink: " + currentLink);
		}
		//System.out.println("dvdlinks: " + dvdLinks);
		//System.out.println("searchResults: " + searchResults);
		return searchResults.toArray(new String [searchResults.size()]);
		*/
		return getLinksFromGoogle(searchString, "squareplus.co.jp");
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		Elements extraFanartElements = document.select("div.more-views ul li a");
		if(extraFanartElements != null)
		{
			ArrayList<Thumb> extrafanartThumbList = new ArrayList<Thumb>(extraFanartElements.size());
			for(Element extraFanartElement : extraFanartElements)
			{
				Thumb thumbToAdd;
				try {
					thumbToAdd = new Thumb(extraFanartElement.attr("href"));
					extrafanartThumbList.add(thumbToAdd);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			return extrafanartThumbList.toArray(new Thumb[extrafanartThumbList.size()]);
		}
		return new Thumb[0];
	}
	
	public String toString(){
		return "SquarePlus";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new SquarePlusParsingProfile();
	}

	@Override
	public String getParserName() {
		return "SquarePlus";
	}

}
