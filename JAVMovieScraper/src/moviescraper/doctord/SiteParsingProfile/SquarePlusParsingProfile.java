package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

public class SquarePlusParsingProfile extends SiteParsingProfile {

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
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// we don't need any special sort title - that's usually something the
		// user provides
		return new SortTitle("");
	}

	@Override
	public Set scrapeSet() {
		// Site doesn't have any set information
		return new Set("");

	}

	@Override
	public Rating scrapeRating() {
		//site doesn't have a rating
		return new Rating(0,"");
	}

	@Override
	public Year scrapeYear() {
		return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on ActionJav
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
			return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
			return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		// It's always XXX content on ActionJav! ;)
		return new MPAARating("XXX");
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
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		return new ArrayList<Genre>();
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		return new ArrayList<Actor>();
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//SquarePlus doesn't have director information, so just return an empty list
		return new ArrayList<Director>();
	}

	@Override
	public Studio scrapeStudio() {
		return new Studio("");
	}

	@Override
	public String createSearchString(File file) {
		String fileNameNoExtension = FilenameUtils.removeExtension(file
				.getName());
		fileNameNoExtension = findIDTagFromFile(file);
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
	public String[] getSearchResults(String searchString) throws IOException {
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

}
