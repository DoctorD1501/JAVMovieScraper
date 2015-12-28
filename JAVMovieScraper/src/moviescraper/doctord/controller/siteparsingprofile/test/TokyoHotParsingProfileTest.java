package moviescraper.doctord.controller.siteparsingprofile.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TokyoHotParsingProfile;
import moviescraper.doctord.model.dataitem.*;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TokyoHotParsingProfileTest {

	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	TokyoHotParsingProfile parser = new TokyoHotParsingProfile();
	
	private static TokyoHotParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		profile = new TokyoHotParsingProfile();
		String searchString = profile.createSearchString(file);
		System.out.println(searchString);
		Document document = SiteParsingProfile.downloadDocumentFromURLString(searchString);
		profile.setDocument(document);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "n0754");
	}
	
	@Test
	public void testFindGoogleLink() {
		String searchString = parser.createSearchString(file);		
		assertEquals("Found no Link", searchString, "http://my.tokyo-hot.com/product/20876/");
	}
	
	@Test
	public void testActor() {
		ArrayList<Actor> actors = profile.scrapeActors();
		
		assertTrue("There should be only one actor.", actors.size() == 1);
		assertEquals("Wrong Actor found.", "Iori Tsukimoto", actors.get(0).getName());
	}
	
	@Test
	public void testTitle() {
		Title scrapeTitle = profile.scrapeTitle();
		
		assertEquals("Wrong Title found.", "The Erotic Toilet", scrapeTitle.getTitle());
	}
	
	@Test
	public void testYear() {
		Year scrapeYear = profile.scrapeYear();
		
		assertEquals("Found wrong year", "2012", scrapeYear.getYear());
	}
	
	@Test
	public void testReleaseDate()
	{
		ReleaseDate releaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong release date ", "2012-06-15", releaseDate.getReleaseDate());
	}

	@Test
	public void testRuntime() {
		moviescraper.doctord.model.dataitem.Runtime runTime = profile.scrapeRuntime();
		assertEquals("Wrong Runtime", "01:35:24", runTime.getRuntime());
	}
	
	@Test
	public void testPlot() {
		Plot scrapePlot = profile.scrapePlot();
		assertTrue("Found wrong Plot", scrapePlot.getPlot().startsWith("It is the plump body"));
	}
	
	@Test
	public void testFanart() throws IOException {
		Thumb[] scrapeFanart = profile.scrapeFanart();
		assertTrue("There should only be 1 fanart", scrapeFanart.length == 0);
	}
	
	@Test
	public void testPoster() throws IOException {
		Thumb[] scrapePosters = profile.scrapePosters();
		assertTrue("There should only be 1 fanart", scrapePosters.length == 1);
		//TestingHelper.showImage("Test Fanart", scrapePosters[0].getThumbImage());
	}
}
