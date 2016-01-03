package moviescraper.doctord.controller.siteparsingprofile.test;

import moviescraper.doctord.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;
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

public class TheMovieDatabaseParsingProfileTest {
	static File file = new File("C:/Temp/Pirates (2005).avi");
	TheMovieDatabaseParsingProfile parser = new TheMovieDatabaseParsingProfile();
	
	private static TheMovieDatabaseParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		profile = new TheMovieDatabaseParsingProfile();
		String searchString = profile.createSearchString(file);
		System.out.println("searchString = " + searchString);
		try {
			SearchResult[] searchResults = profile.getSearchResults(searchString);
			Document document = profile.downloadDocument(searchResults[0]);
			System.out.println("document set to " + document.baseUri());
			profile.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	@Test
	public void testTitle(){
		Title testTitle = profile.scrapeTitle();
		assertEquals("Wrong title", "Pirates", testTitle.getTitle());
	}
	
	@Test
	public void originalTitle(){
		OriginalTitle testOriginalTitle = profile.scrapeOriginalTitle();
		assertEquals("Wrong title", "Pirates", testOriginalTitle.getOriginalTitle());
	}
	
	@Test
	public void testSet(){
		Set testSet = profile.scrapeSet();
		assertEquals("Wrong set", "Pirates Collection", testSet.getSet());
	}
	
	@Test
	public void testRating(){
		Rating testRating = profile.scrapeRating();
		System.out.println("rating was " + testRating);
		assertTrue(Character.isDigit(testRating.getRatingOutOfTen().charAt(0)));
	}
	
	@Test
	public void testPlot(){
		Plot testPlot = profile.scrapePlot();
		assertEquals("Wrong plot", "This electrifying, swashb", testPlot.getPlot().substring(0,25));
	}
	
	@Test
	public void testRuntime(){
		moviescraper.doctord.model.dataitem.Runtime testRuntime = profile.scrapeRuntime();
		assertEquals("Wrong runtime", "129", testRuntime.getRuntime());
	}
	
	@Test
	public void testID() {
		ID scrapeID = profile.scrapeID();
		System.out.println("scrapeID = " + scrapeID);
		assertEquals("Found wrong ID", "13860", scrapeID.getId());
	}
	
	@Test
	public void testVotes(){
		Votes scrapeVotes = profile.scrapeVotes();
		System.out.println("Votes = " + scrapeVotes);
		assertTrue(!(scrapeVotes == Votes.BLANK_VOTES));
	}
	
	@Test
	public void testYear() {
		Year scrapeYear = profile.scrapeYear();	
		assertEquals("Found wrong year", "2005", scrapeYear.getYear());
	}
	
	@Test
	public void testReleaseDate()
	{
		ReleaseDate releaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong release date ", "2005-09-26", releaseDate.getReleaseDate());
	}
	
	@Test
	public void testTagline() {
		Tagline scrapeTagline = profile.scrapeTagline();
		assertEquals("Wrong tagline", "Swash and unbuckle...", scrapeTagline.getTagline());
	}
	
	@Test
	public void testStudio() {
		Studio scrapeStudio = profile.scrapeStudio();	
		assertEquals("Found wrong Studio", "Adam & Eve", scrapeStudio.getStudio());
	}
	
	@Test
	public void testDirector(){
		ArrayList<Director> scrapeDirectors = profile.scrapeDirectors();
		assertEquals("Found wrong director", "Joone", scrapeDirectors.get(0).getName());
	}
	
	@Test
	public void testGenre()
	{
		ArrayList<Genre> testGenre = profile.scrapeGenres();
		System.out.println(testGenre);
		assertTrue("There should be 3 genres", testGenre.size() == 3);
	}
	
	@Test
	public void testActor()
	{
		//ArrayList<Actor> testActor = profile.scrapeActors();
	}
	
	@Test
	public void testExtraFanart() throws IOException
	{
		//Thumb  [] extraFanart = profile.scrapeExtraFanart();
	}
	
	@Test
	public void testPoster() throws IOException {
		Thumb[] poster = profile.scrapePosters();
		assertTrue("There should be 1 Poster or more.", poster.length > 1);
		
		TestingHelper.showImage("Poster", poster[0].getThumbImage());
	}
	

	

}
