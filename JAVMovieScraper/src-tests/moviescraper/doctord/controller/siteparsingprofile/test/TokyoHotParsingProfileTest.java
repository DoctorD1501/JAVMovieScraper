package moviescraper.doctord.controller.siteparsingprofile.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import moviescraper.doctord.controller.siteparsingprofile.specific.TokyoHotParsingProfile;
import moviescraper.doctord.model.dataitem.*;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TokyoHotParsingProfileTest {
	public static final String ID_TAG_FROM_FILE = "n0754";
	public static final String DETAIL_URL = "http://my.tokyo-hot.com/product/20876/";
	public static final String TITLE = "The Erotic Toilet";
	public static final String YEAR = "2012";
	public static final String RELEASE_DATE = "2012-06-15";
	public static final String RUN_TIME = "01:35:24";
	public static final String PLOT = "It is the plump body";
	public static final String FILENAME = "C:/Temp/Tokyo Hot n0754 abc.avi";
	public static List<Thumb> FAN_ART;
	public static List<Thumb> POSTER;
	public static List<Actor> ACTORS = Lists.newArrayList(
			new Actor("Iori Tsukimoto", null, null)
	);

	static {
		try {
			FAN_ART = Lists.newArrayList(
                new Thumb("http://my.cdn.tokyo-hot.com/media/20876/list_image/n0754/820x462_default.jpg"),
                new Thumb("http://my.cdn.tokyo-hot.com/media/20876/scap/001.jpg"),
                new Thumb("http://my.cdn.tokyo-hot.com/media/20876/scap/002.jpg"));
			POSTER = FAN_ART;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	static File file = new File(FILENAME);

	TokyoHotParsingProfile parser = new TokyoHotParsingProfile();
	
	private static TokyoHotParsingProfile profile;

	@BeforeClass
	public static void initialize() throws IOException {
		FAN_ART = Lists.newArrayList(
				new Thumb("http://my.cdn.tokyo-hot.com/media/20876/list_image/n0754/820x462_default.jpg"),
				new Thumb("http://my.cdn.tokyo-hot.com/media/20876/scap/001.jpg"),
				new Thumb("http://my.cdn.tokyo-hot.com/media/20876/scap/002.jpg"));

		profile = new TokyoHotParsingProfile();
		String searchString = profile.createSearchString(file);
		System.out.println(searchString);
		Document document = profile.downloadDocumentFromURLString(searchString);
		profile.setDocument(document);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, ID_TAG_FROM_FILE);
	}
	
	@Test
	public void testFindGoogleLink() {
		String searchString = parser.createSearchString(file);		
		assertEquals("Found no Link", DETAIL_URL, searchString);
	}
	
	@Test
	public void testActor() {
		ArrayList<Actor> actors = profile.scrapeActors();
		assertArrayEquals("The actors should match",
				Iterables.toArray(ACTORS, Actor.class),
				Iterables.toArray(actors, Actor.class));
	}
	
	@Test
	public void testTitle() {
		Title scrapeTitle = profile.scrapeTitle();
		assertEquals("Wrong Title found.", TITLE, scrapeTitle.getTitle());
	}
	
	@Test
	public void testYear() {
		Year scrapeYear = profile.scrapeYear();
		assertEquals("Found wrong year", YEAR, scrapeYear.getYear());
	}
	
	@Test
	public void testReleaseDate()
	{
		ReleaseDate releaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong release date ", RELEASE_DATE, releaseDate.getReleaseDate());
	}

	@Test
	public void testRuntime() {
		moviescraper.doctord.model.dataitem.Runtime runTime = profile.scrapeRuntime();
		assertEquals("Wrong Runtime", RUN_TIME, runTime.getRuntime());
	}
	
	@Test
	public void testPlot() {
		Plot scrapePlot = profile.scrapePlot();
		assertTrue("Found wrong Plot", scrapePlot.getPlot().startsWith(PLOT));
	}
	
	@Test
	public void testFanart() throws IOException {
		Thumb[] scrapeFanart = profile.scrapeFanart();

		assertArrayEquals("The actors should match", Iterables.toArray(FAN_ART, Thumb.class), scrapeFanart);
	}
	
	@Test
	public void testPoster() throws IOException {
		Thumb[] scrapePosters = profile.scrapePosters();
		assertArrayEquals("The actors should match", Iterables.toArray(POSTER, Thumb.class), scrapePosters);
	}
}
