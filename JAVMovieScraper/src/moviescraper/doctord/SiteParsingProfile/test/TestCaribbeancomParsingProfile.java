package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.CaribbeancomParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCaribbeancomParsingProfile {
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
		assertEquals("Wrong original title", "CA乱交パーティ 〜快適な性交空間〜", originalTitle.getOriginalTitle());
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
	public void testScrapePlot(){
		Plot plot = parser.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", true, plot.getPlot().length() > 35);
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
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
		assertEquals("Wrong actor", "Ruka Ichinose", actorList.get(0).getName());
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
