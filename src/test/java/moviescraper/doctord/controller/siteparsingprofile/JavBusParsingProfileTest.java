package moviescraper.doctord.controller.siteparsingprofile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import moviescraper.doctord.controller.languagetranslation.Language;

import moviescraper.doctord.controller.siteparsingprofile.specific.JavBusParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavBusParsingProfileTest {
	static boolean scrapeInEnglish = true;
	static File file = new File("C:/Temp/MIDE-058.avi");
	private final JavBusParsingProfile parser = new JavBusParsingProfile();

	private static JavBusParsingProfile profile;

	@BeforeClass
	public static void initialize() {
		profile = new JavBusParsingProfile();
		profile.setScrapingLanguage(Language.JAPANESE);
		String searchString = profile.createSearchString(file);
		System.out.println("searchString = " + searchString);
		try {
			SearchResult[] searchResults = profile.getSearchResults(searchString);
			Document document = SiteParsingProfile.getDocument(searchResults[0]);
			System.out.println("document set to " + document.baseUri());
			profile.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file, false);
		System.out.println(findIDTagFromFile);
		assertEquals(findIDTagFromFile, "MIDE-058");
	}

	@Test
	public void testTitle() {
		Title testTitle = profile.scrapeTitle();
		assertEquals("Wrong title", "猛烈なKISSと絡み合う肉体 木下あずみ", testTitle.getTitle());
	}

	@Test
	public void testOriginalTitle() {
		OriginalTitle testOriginalTitle = profile.scrapeOriginalTitle();
		assertEquals("Wrong original title", "猛烈なKISSと絡み合う肉体 木下あずみ", testOriginalTitle.getOriginalTitle());
	}

	@Test
	public void testSet() {
		Set testSet = profile.scrapeSet();
		assertEquals("Wrong set", "猛烈なKISSと絡み合う肉体", testSet.getSet());
	}

	@Test
	public void testStudio() {
		Studio testStudio = profile.scrapeStudio();
		assertEquals("Wrong studio", "ムーディーズ", testStudio.getStudio());
	}

	@Test
	public void testDirector() {
		ArrayList<Director> testDirectors = profile.scrapeDirectors();
		assertEquals("Missing director", 1, testDirectors.size());
		Director testDirector = testDirectors.get(0);
		if (profile.getScrapingLanguage() == Language.JAPANESE) {
			assertEquals("Wrong director", "紋℃", testDirector.getName());
		} else {
			assertEquals("Wrong director", "Crest℃", testDirector.getName());
		}
	}

	@Test
	public void testPlot() {
		Plot testPlot = profile.scrapePlot();
		assertEquals("Wrong plot", "", testPlot.getPlot());
	}

	@Test
	public void testRuntime() {
		moviescraper.doctord.model.dataitem.Runtime testRuntime = profile.scrapeRuntime();
		assertEquals("Wrong runtime", "150", testRuntime.getRuntime());
	}

	@Test
	public void testID() {
		ID scrapeID = profile.scrapeID();
		System.out.println("scrapeID = " + scrapeID);
		assertEquals("Found wrong ID", "MIDE-058", scrapeID.getId());
	}

	@Test
	public void testYear() {
		Year scrapeYear = profile.scrapeYear();
		assertEquals("Found wrong year", "2014", scrapeYear.getYear());
	}

	@Test
	public void testReleaseDate() {
		ReleaseDate scrapeReleaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong releaseDate", "2014-01-09", scrapeReleaseDate.getReleaseDate());
	}

	@Test
	public void testGenre() {
		ArrayList<Genre> testGenre = profile.scrapeGenres();
		System.out.println(testGenre);
		assertTrue("There should be genres", testGenre.size() > 1);
		if (profile.getScrapingLanguage() == Language.JAPANESE) {
			assertTrue(testGenre.contains(new Genre("ハイビジョン")));
		} else {
			assertTrue(testGenre.contains(new Genre("Lingerie")));
		}
	}

	@Test
	public void testActor() {
		ArrayList<Actor> testActor = profile.scrapeActors();
		System.out.println("actor = " + testActor.get(0));
		assertEquals("Found wrong actor", "木下あずみ", testActor.get(0).getName());
	}

	/*
	 * @Test
	 * public void testExtraFanart() throws IOException
	 * {
	 * Thumb [] extraFanart = profile.scrapeExtraFanart();
	 * GenericProfileTest.showImage("Extrafanart", extraFanart[0].getThumbImage());
	 * }
	 */

	@Test
	public void testPoster() throws IOException {
		Thumb[] poster = profile.scrapePosters();
		assertTrue("There should be 1 Fanart.", poster.length == 1);

		//TestingHelper.showImage("Poster", poster[0].getThumbImage());
	}

}
