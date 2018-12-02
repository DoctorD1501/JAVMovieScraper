package moviescraper.doctord.controller.siteparsingprofile;

import java.io.File;

import moviescraper.doctord.controller.siteparsingprofile.specific.TokyoHotParsingProfile;

/*
 * import java.io.IOException;
 * import java.util.ArrayList;
 * 
 * import moviescraper.doctord.model.dataitem.Actor;
 * import moviescraper.doctord.model.dataitem.Plot;
 * import moviescraper.doctord.model.dataitem.ReleaseDate;
 * import moviescraper.doctord.model.dataitem.Thumb;
 * import moviescraper.doctord.model.dataitem.Title;
 * import moviescraper.doctord.model.dataitem.Year;
 * 
 * import org.junit.Test;
 * import static org.junit.Assert.*;
 */
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;

public class TokyoHotParsingProfileTest {

	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	TokyoHotParsingProfile parser = new TokyoHotParsingProfile();

	private static TokyoHotParsingProfile profile;

	@BeforeClass
	public static void initialize() {
		profile = new TokyoHotParsingProfile();
		String searchString = profile.createSearchString(file);
		if (searchString == null) {
			return;
		}
		System.out.println(searchString);
		Document document = SiteParsingProfile.downloadDocumentFromURLString(searchString);
		profile.setDocument(document);
	}
	/*
	 * @SuppressWarnings("static-access")
	 * 
	 * @Test
	 * public void testFindID() {
	 * String findIDTagFromFile = parser.findIDTagFromFile(file);
	 * assertEquals(findIDTagFromFile, "n0754");
	 * }
	 * 
	 * @Test
	 * public void testFindGoogleLink() {
	 * String searchString = parser.createSearchString(file);
	 * assertEquals("Found no Link", searchString, "http://cdn.www.tokyo-hot.com/e/n0754_iori_tsukimoto_eq_e.html");
	 * }
	 * 
	 * @Test
	 * public void testActor() {
	 * ArrayList<Actor> actors = profile.scrapeActors();
	 * 
	 * assertTrue("There should be only one actor.", actors.size() == 1);
	 * assertEquals("Wrong Actor found.", "Iori Tsukimoto", actors.get(0).getName());
	 * }
	 * 
	 * @Test
	 * public void testTitle() {
	 * Title scrapeTitle = profile.scrapeTitle();
	 * 
	 * assertEquals("Wrong Title found.", "The Erotic Toilet", scrapeTitle.getTitle());
	 * }
	 * 
	 * @Test
	 * public void testYear() {
	 * Year scrapeYear = profile.scrapeYear();
	 * 
	 * assertEquals("Found wrong year", "2012", scrapeYear.getYear());
	 * }
	 * 
	 * @Test
	 * public void testReleaseDate()
	 * {
	 * ReleaseDate releaseDate = profile.scrapeReleaseDate();
	 * assertEquals("Found wrong release date ", "2012-06-15", releaseDate.getReleaseDate());
	 * }
	 * 
	 * @Test
	 * public void testRuntime() {
	 * moviescraper.doctord.model.dataitem.Runtime runTime = profile.scrapeRuntime();
	 * 
	 * assertEquals("Wrong Runtime", "95", runTime.getRuntime());
	 * }
	 * 
	 * @Test
	 * public void testPlot() {
	 * Plot scrapePlot = profile.scrapePlot();
	 * 
	 * assertTrue("Found wrong Plot", scrapePlot.getPlot().startsWith("It is the plump body"));
	 * }
	 * 
	 * @Test
	 * public void testFanart() throws IOException {
	 * Thumb[] scrapeFanart = profile.scrapeFanart();
	 * assertTrue("There should only be 1 fanart", scrapeFanart.length == 1);
	 * //TestingHelper.showImage("Test Fanart", scrapeFanart[0].getThumbImage());
	 * }
	 * 
	 * @Test
	 * public void testPoster() throws IOException {
	 * Thumb[] scrapePosters = profile.scrapePosters();
	 * assertTrue("There should only be 1 fanart", scrapePosters.length == 1);
	 * //TestingHelper.showImage("Test Fanart", scrapePosters[0].getThumbImage());
	 * }
	 */

}
