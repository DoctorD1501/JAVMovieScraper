package moviescraper.doctord.controller.siteparsingprofile.test;

import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomPremiumParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CaribbeancomPremiumParsingProfileTest {
	
	static File file = new File("C:/Temp/Caribbeancom Premium 062014_878.avi");
	static CaribbeancomPremiumParsingProfile profile = new CaribbeancomPremiumParsingProfile();
	
	
	@BeforeClass
	public static void initialize() {
		profile = new CaribbeancomPremiumParsingProfile();
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
		String findIDTagFromFile = profile.findIDTagFromFile(file, false);
		assertEquals(findIDTagFromFile, "062014_878");
	}
	
	@Test
	public void testScrapeID(){
		String id = profile.scrapeID().getId();
		assertEquals("062014_878", id);
	}
	
	@Test
	public void testScrapeTitle() {
		Title title = profile.scrapeTitle();
		//this assumes translation is done. if this test fails, it could be because translation is not done or the web
		//based translation service has changed how they do translation, so try to just see if the title is close to
		//this one and adjust as needed to fix the test case
		assertEquals("Glamorous Venus M - The Ultimate Masochist BODY Fucking -", title.getTitle());
	}
	
	@Test
	public void testScrapeOriginalTitle(){
		OriginalTitle originalTitle = profile.scrapeOriginalTitle();
		assertEquals("Wrong original title", "グラマラス・ビーナスM −究極マゾBODY姦−", originalTitle.getOriginalTitle());
	}
	
	@Test
	public void testScrapeRating(){
		Rating rating = profile.scrapeRating();
		assertEquals("Wrong rating", "", rating.getRatingOutOfTen());
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
		assertEquals("Wrong release date", "2014-01-20", releaseDate.getReleaseDate());
	}
	
	@Test
	public void testScrapePlot(){
		Plot plot = profile.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", true, plot.getPlot().length() > 35);
	}
	
	@Test
	public void testScrapeRuntime(){
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = profile.scrapeRuntime();
		assertEquals("Wrong runtime", "120", movieRuntime.getRuntime());
	}
	

	
	@Test
	public void testScrapeActors(){
		ArrayList<Actor> actorList = profile.scrapeActors();
		assertEquals("Wrong actor", "Ichiki Miho", actorList.get(0).getName());
	}
	
	@Test
	public void testScrapeGenre(){
		ArrayList<Genre> genreList = profile.scrapeGenres();
		assertEquals("Wrong genre", "Pornstar", genreList.get(0).getGenre());
	}
	
	@Test
	public void testTrailer(){
		Trailer trailer = profile.scrapeTrailer();
		assertEquals("Wrong trailer", "http://sample.caribbeancompr.com/moviepages/062014_878/sample/sample.mp4", trailer.getTrailer());
	}
	
	@Test
	public void testScrapePoster(){
		Thumb[] posters = profile.scrapePosters();
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "http://en.caribbeancompr.com/moviepages/062014_878/images/main_b.jpg", posters[0].getThumbURL().toString());
	}

}
