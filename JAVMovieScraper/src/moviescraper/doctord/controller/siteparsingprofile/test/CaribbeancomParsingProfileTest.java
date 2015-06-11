package moviescraper.doctord.controller.siteparsingprofile.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class CaribbeancomParsingProfileTest {
	static File file = new File("C:/Temp/Caribbeancom 070514-637 abc.avi");
	static CaribbeancomParsingProfile parser = new CaribbeancomParsingProfile();
	
	
	@BeforeClass
	public static void initialize() {
		parser = new CaribbeancomParsingProfile();
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
		assertEquals(findIDTagFromFile, "070514-637");
	}
	
	@Test
	public void testScrapeTitle() {
		Title title = parser.scrapeTitle();
		//this assumes translation is done. if this test fails, it could be because translation is not done or the web
		//based translation service has changed how they do translation, so try to just see if the title is close to
		//this one and adjust as needed to fix the test case
		assertEquals("Wrong title", "CA Orgy Party ~ Comfortable Intercourse Space ~", title.getTitle());
	}
	
	@Test
	public void testScrapeOriginalTitle(){
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Wrong original title", "CA乱交パーティ 〜快�?��?�性交空間〜", originalTitle.getOriginalTitle());
	}
	
	@Test
	public void testScrapeRating(){
		Rating rating = parser.scrapeRating();
		assertEquals("Wrong rating", "8.0", rating.getRatingOutOfTen());
	}
	
	@Test
	public void testScrapeYear(){
		Year year = parser.scrapeYear();
		assertEquals("Wrong year", "2014", year.getYear());
	}
	
	@Test
	public void testScrapeReleaseDate()
	{
		ReleaseDate releaseDate = parser.scrapeReleaseDate();
		assertEquals("Wrong release date", "2014-01-05", releaseDate.getReleaseDate());
	}
	
	@Test
	public void testScrapePlot(){
		Plot plot = parser.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", true, plot.getPlot().length() > 35);
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
		assertEquals("Wrong runtime", "80", movieRuntime.getRuntime());
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testScrapeID(){
		ID id = parser.scrapeID();
		assertEquals("Wrong ID", "070514-637", id.getId());
		assertEquals("ID did not match from filename", parser.findIDTagFromFile(file), id.getId());
	}
	
	@Test
	public void testScrapeActors(){
		ArrayList<Actor> actorList = parser.scrapeActors();
		assertTrue("Wrong actor name", actorList.get(0).getName().contains("Ichinose"));
	}
	
	@Test
	public void testScrapeGenre(){
		ArrayList<Genre> genreList = parser.scrapeGenres();
		assertEquals("Wrong genre", "Exclusive Video", genreList.get(0).getGenre());
	}
	
	@Test
	public void testTrailer(){
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Wrong trailer", "http://smovie.caribbeancom.com/sample/movies/070514-637/sample_m.mp4", trailer.getTrailer());
	}
	
	@Test
	public void testScrapePoster(){
		Thumb[] posters = parser.scrapePosters();
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "http://www.caribbeancom.com/moviepages/070514-637/images/l_l.jpg", posters[0].getThumbURL().toString());
	}
}
