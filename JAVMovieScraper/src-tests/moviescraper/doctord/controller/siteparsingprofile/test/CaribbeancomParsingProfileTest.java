package moviescraper.doctord.controller.siteparsingprofile.test;

import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaribbeancomParsingProfileTest {
	static File file = new File("C:/Temp/Caribbeancom 070514-637 abc.avi");
	static CaribbeancomParsingProfile profile = new CaribbeancomParsingProfile();
	
	
	@BeforeClass
	public static void initialize() {
		profile = new CaribbeancomParsingProfile();
		String searchString = profile.createSearchString(file);
		try {
			SearchResult[] searchResults = profile.getSearchResults(searchString);
			Document document = profile.downloadDocument(searchResults[0]);
			profile.setDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = profile.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "070514-637");
	}
	
	@Test
	public void testScrapeTitle() {
		Title title = profile.scrapeTitle();
		//this assumes translation is done. if this test fails, it could be because translation is not done or the web
		//based translation service has changed how they do translation, so try to just see if the title is close to
		//this one and adjust as needed to fix the test case
		assertEquals("Wrong title", "CA Orgy Party ~ Comfortable Intercourse Space ~", title.getTitle());
	}
	
	@Test
	public void testScrapeOriginalTitle(){
		OriginalTitle originalTitle = profile.scrapeOriginalTitle();
		assertEquals("Wrong original title", "CA乱交パーティ 〜快適な性交空間〜", originalTitle.getOriginalTitle());
	}
	
	@Test
	public void testScrapeRating(){
		Rating rating = profile.scrapeRating();
		assertEquals("Wrong rating", "8.0", rating.getRatingOutOfTen());
	}
	
	@Test
	public void testScrapeYear(){
		Year year = profile.scrapeYear();
		assertEquals("Wrong year", "2014", year.getYear());
	}
	
	@Test
	public void testScrapeReleaseDate()
	{
		ReleaseDate releaseDate = profile.scrapeReleaseDate();
		assertEquals("Wrong release date", "2014-01-05", releaseDate.getReleaseDate());
	}
	
	@Test
	public void testScrapePlot(){
		Plot plot = profile.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", true, plot.getPlot().length() > 35);
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = profile.scrapeRuntime();
		assertEquals("Wrong runtime", "80", movieRuntime.getRuntime());
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testScrapeID(){
		ID id = profile.scrapeID();
		assertEquals("Wrong ID", "070514-637", id.getId());
		assertEquals("ID did not match from filename", profile.findIDTagFromFile(file), id.getId());
	}
	
	@Test
	public void testScrapeActors(){
		ArrayList<Actor> actorList = profile.scrapeActors();
		assertTrue("Wrong actor name", actorList.get(0).getName().contains("Ichinose"));
	}
	
	@Test
	public void testScrapeGenre(){
		ArrayList<Genre> genreList = profile.scrapeGenres();
		assertEquals("Wrong genre", "Original Video", genreList.get(0).getGenre());
	}
	
	@Test
	public void testTrailer(){
		Trailer trailer = profile.scrapeTrailer();
		assertEquals("Wrong trailer", "http://smovie.caribbeancom.com/sample/movies/070514-637/sample_m.mp4", trailer.getTrailer());
	}
	
	@Test
	public void testScrapePoster(){
		Thumb[] posters = profile.scrapePosters();
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "http://www.caribbeancom.com/moviepages/070514-637/images/l_l.jpg", posters[0].getThumbURL().toString());
	}
}
