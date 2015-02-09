package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.Language;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.OneThousandGiriParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.R18ParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneThousandGiriParsingProfileTest {
	
	static boolean scrapeInEnglish = true;
	static File file = new File("C:/Temp/150130ran_rio.avi");
	OneThousandGiriParsingProfile parser = new OneThousandGiriParsingProfile();
	
	private static OneThousandGiriParsingProfile englishProfile;
	private static OneThousandGiriParsingProfile japaneseProfile;
	
	@BeforeClass
	public static void initialize() {
		englishProfile = new OneThousandGiriParsingProfile();
		japaneseProfile = new OneThousandGiriParsingProfile();
		japaneseProfile.setScrapingLanguage(Language.JAPANESE);
		String searchString = englishProfile.createSearchString(file);
		System.out.println("searchString = " + searchString);
		try {
			SearchResult[] searchResults = englishProfile.getSearchResults(searchString);
			Document document = SpecificScraperAction.downloadDocument(searchResults[0]);
			System.out.println("document of english profile set to " + document.baseUri());
			englishProfile.setDocument(document);
			
			searchResults = japaneseProfile.getSearchResults(searchString);
			document = SpecificScraperAction.downloadDocument(searchResults[0]);
			System.out.println("document of japanese profile set to " + document.baseUri());
			japaneseProfile.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		System.out.println(findIDTagFromFile);
		assertEquals(findIDTagFromFile, "150130ran_rio");
	}
	
	@Test
	public void testTitle(){
		Title testEnglishTitle = englishProfile.scrapeTitle();
		assertEquals("Wrong title", "Lesbian Colleagues Fetishism - Work Is Each Other Comfort Forget OL~", testEnglishTitle.getTitle());
		Title testJapaneseTitle = japaneseProfile.scrapeTitle();
		assertEquals("Wrong title", "レズフェティシズム 〜仕事は忘れて慰め合う同僚ＯＬ〜", testJapaneseTitle.getTitle());
	}
	
	@Test
	public void testOriginalTitle(){
		OriginalTitle testEnglishTitle = englishProfile.scrapeOriginalTitle();
		assertEquals("Wrong original title", "レズフェティシズム 〜仕事は忘れて慰め合う同僚ＯＬ〜", testEnglishTitle.getOriginalTitle());
		OriginalTitle testJapaneseTitle = japaneseProfile.scrapeOriginalTitle();
		assertEquals("Wrong original title", "レズフェティシズム 〜仕事は忘れて慰め合う同僚ＯＬ〜", testJapaneseTitle.getOriginalTitle());
	}
	
	@Test
	public void testYear(){
		Year testYear = englishProfile.scrapeYear();
		assertEquals("Wrong year", "2015", testYear.getYear());
	}
	
	@Test
	public void testPlot(){
		Plot testEnglishPlot = englishProfile.scrapePlot();
		assertTrue("Wrong plot", testEnglishPlot.getPlot().startsWith("Senior OL Rio-chan to try"));
		Plot testJapanesePlot = japaneseProfile.scrapePlot();
		assertTrue("Wrong plot", testJapanesePlot.getPlot().startsWith("仕事でミスをして落ち込むOLラン"));
	}
	
	@Test
	public void testGenres()
	{
		ArrayList<Genre> englishGenres = englishProfile.scrapeGenres();
		assertTrue(englishGenres.size() > 0);
		ArrayList<Genre> japaneseGenres = japaneseProfile.scrapeGenres();
		assertTrue(japaneseGenres.size() > 0);
	}
	
	@Test
	public void testActors()
	{
		ArrayList<Actor> englishActors = englishProfile.scrapeActors();
		assertTrue(englishActors.size() > 0);
		ArrayList<Actor> japaneseActors = japaneseProfile.scrapeActors();
		assertTrue(japaneseActors.size() > 0);
	}
	
	@Test
	public void testPoster() throws IOException {
		Thumb[] posters = englishProfile.scrapePosters();
		assertTrue("There should be 1 poster.", posters.length == 1);
		GenericProfileTest.showImage("Fanart", posters[0].getThumbImage());
	}
}
