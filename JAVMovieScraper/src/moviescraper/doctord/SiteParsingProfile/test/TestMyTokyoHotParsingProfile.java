package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.MyTokyoHotParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMyTokyoHotParsingProfile {
	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	static MyTokyoHotParsingProfile parser = new MyTokyoHotParsingProfile();
	
	
	@BeforeClass
	public static void initialize() {
		parser = new MyTokyoHotParsingProfile();
		String searchString = parser.createSearchString(file);
		try {
			SearchResult[] searchResults = parser.getSearchResults(searchString);
			Document document = SpecificScraperAction.downloadDocument(searchResults[0]);
			parser.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "n0754");
	}
	
	@Test
	public void testScrapeTitle()
	{
		Title title = parser.scrapeTitle();
		assertEquals("Title not correct", "The Erotic Toilet", title.getTitle());
	}
	
	@Test
	public void testScrapeOriginalTitle(){
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Original Title Not Correct", "月本いおり東熱水着膣射", originalTitle.getOriginalTitle());
	}
	
	@Test
	public void testScrapeYear(){
		Year year = parser.scrapeYear();
		assertEquals("Year not correct", "2012", year.getYear());
	}
	
	@Test
	public void testScrapePlot(){
		Plot plot = parser.scrapePlot();
		assertEquals("Plot not correct", "It is the plump body", plot.getPlot().substring(0,20));
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.dataitem.Runtime runtime = parser.scrapeRuntime();
		assertEquals("Runtime not correct", "95", runtime.getRuntime());
	}
	
	@Test
	public void testScrapeID(){
		ID id = parser.scrapeID();
		assertEquals("ID not correct", "n0754", id.getId());
	}
	
	@Test
	public void testScrapePoster(){
		Thumb[] posters = parser.scrapePosters();
		assertEquals("Poster not correct", "http://my.cdn.tokyo-hot.com/media/20876/list_image/n0754/820x462_default.jpg", posters[0].getThumbURL().toString());
	}
	
	@Test public void testScrapeActor(){
		ArrayList<Actor> actors = parser.scrapeActors();
		assertEquals("Actor name not correct", "Iori Tsukimoto", actors.get(0).getName());
		assertEquals("Actor thumb path not correct", "http://my.cdn.tokyo-hot.com/media/cast/5859/thumbnail.jpg", actors.get(0).getThumb().getThumbURL().toString());
	}
	
	@Test public void testScrapeGenre(){
		ArrayList<Genre> genres = parser.scrapeGenres();
		assertEquals("Genre not correct", "Toys", genres.get(3).getGenre());
	}
	
	@Test public void testScrapeTrailer(){
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Trailer not correct", "http://my.cdn.tokyo-hot.com/media/samples/20876.mp4", trailer.getTrailer());
	}
}
