package moviescraper.doctord.SiteParsingProfile.debug;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import moviescraper.doctord.SiteParsingProfile.TokyoHotParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Year;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestTokyoHotParsingProfile {

	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	TokyoHotParsingProfile parser = new TokyoHotParsingProfile();
	
	private static TokyoHotParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		TokyoHotParsingProfile parser = new TokyoHotParsingProfile();
		String searchString = parser.createSearchString(file);
		profile = new TokyoHotParsingProfile(searchString);
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
		assertEquals("Found no Link", searchString, "n0754_iori_tsukimoto_eq");
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
	public void testRuntime() {
		moviescraper.doctord.dataitem.Runtime runTime = profile.scrapeRuntime();
		
		assertEquals("Wrong Runtime", "95", runTime.getRuntime());
	}
	
	@Test
	public void testPlot() {
		Plot scrapePlot = profile.scrapePlot();
		
		assertTrue("Found wrong Plot", scrapePlot.getPlot().startsWith("It is the plump body"));
	}
}
