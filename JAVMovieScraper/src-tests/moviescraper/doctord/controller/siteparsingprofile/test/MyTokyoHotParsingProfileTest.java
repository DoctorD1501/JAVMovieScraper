package moviescraper.doctord.controller.siteparsingprofile.test;

import moviescraper.doctord.controller.siteparsingprofile.specific.MyTokyoHotParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MyTokyoHotParsingProfileTest {
	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	static MyTokyoHotParsingProfile profile = new MyTokyoHotParsingProfile();
	
	
	@BeforeClass
	public static void initialize() {
		profile = new MyTokyoHotParsingProfile();
		String searchString = profile.createSearchString(file);
		try {
			SearchResult[] searchResults = profile.getSearchResults(searchString);
			Document document = profile.downloadDocument(searchResults[0]);
			profile.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = profile.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "n0754");
	}
	
	@Test
	public void testScrapeTitle()
	{
		Title title = profile.scrapeTitle();
		assertEquals("Title not correct", "The Erotic Toilet", title.getTitle());
	}
	
	@Test
	public void testScrapeOriginalTitle(){ }
	
	@Test
	public void testScrapeYear(){
		Year year = profile.scrapeYear();
		assertEquals("Year not correct", "2012", year.getYear());
	}
	
	@Test
	public void testScrapePlot(){
		Plot plot = profile.scrapePlot();
		assertEquals("Plot not correct", "It is the plump body", plot.getPlot().substring(0,20));
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.model.dataitem.Runtime runtime = profile.scrapeRuntime();
		assertEquals("Runtime not correct", "95", runtime.getRuntime());
	}
	
	@Test
	public void testScrapeID(){
		ID id = profile.scrapeID();
		assertEquals("ID not correct", "n0754", id.getId());
	}
	
	@Test
	public void testScrapePoster(){
		Thumb[] posters = profile.scrapePosters();
		assertEquals("Poster not correct", "http://my.cdn.tokyo-hot.com/media/20876/list_image/n0754/820x462_default.jpg", posters[0].getThumbURL().toString());
	}
	
	@Test public void testScrapeActor(){
		ArrayList<Actor> actors = profile.scrapeActors();
		assertEquals("Actor name not correct", "Iori Tsukimoto", actors.get(0).getName());
		assertEquals("Actor thumb path not correct", "http://my.cdn.tokyo-hot.com/media/cast/5859/thumbnail.jpg", actors.get(0).getThumb().getThumbURL().toString());
	}
	
	@Test public void testScrapeGenre(){
		ArrayList<Genre> genres = profile.scrapeGenres();
		assertEquals("Genre not correct", "Toys", genres.get(3).getGenre());
	}
	
	@Test public void testScrapeTrailer(){
		Trailer trailer = profile.scrapeTrailer();
		assertEquals("Trailer not correct", "http://my.cdn.tokyo-hot.com/media/samples/20876.mp4", trailer.getTrailer());
	}
	
	@Test
	public void testReleaseDate() {
		ReleaseDate scrapeReleaseDate = profile.scrapeReleaseDate();
		assertEquals("Wrong release date", "2012-06-15", scrapeReleaseDate.getReleaseDate());
	}
}
