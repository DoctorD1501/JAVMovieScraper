package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.AvEntertainmentParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestAvEntertainmentParsingProfile {

	static AvEntertainmentParsingProfile profile;
	static File file = new File("C:/temp/SKY-120.mkv");

	@BeforeClass
	public static void initialize() {
		String url = "http://www.aventertainments.com/product_lists.aspx?product_id=81366&languageID=1&dept_id=29";
		Document document = SpecificScraperAction.downloadDocument(url);
		profile = new AvEntertainmentParsingProfile();
		profile.setDocument(document);
	}
	
	@Test
	public void testSearchResult() throws IOException {
		SearchResult[] results = profile.getSearchResults("http://www.aventertainments.com/ppv/ppv_studioproducts.aspx?StudioID=45&languageID=1&VODTypeID=1");
//		for (SearchResult sr : results) {
//			TestTokyoHotParsingProfile.showImage(sr.getLabel(), sr.getPreviewImage().getThumbImage());
//		}
		assertTrue("There should be 20 results.", results.length == 20);
	}

	@Test
	public void testActors() {
		ArrayList<Actor> scrapeActors = profile.scrapeActors();
		assertTrue("There should be 1 Actor", scrapeActors.size() == 1);
		assertEquals("Wrong actor name", "Yume Mizuki", scrapeActors.get(0).getName());
		assertEquals("Wrong actor picture", "http://imgs.aventertainments.com/ActressImage/LargeImage/mizuki_yume.jpg", scrapeActors.get(0).getThumb().getThumbURL().toString());
	}
	
	@Test
	public void testTitle() {
		Title scrapeTitle = profile.scrapeTitle();
		assertEquals("Wrong Title", "GANKI MANIA : Yume Mizuki", scrapeTitle.getTitle());
	}
	
	@Test
	public void testGenres() {
		ArrayList<Genre> genres = profile.scrapeGenres();
		assertTrue("There should be 12 genres.", genres.size() == 16);
		
		assertEquals("First Genre not found", "Uncensored", genres.get(0).getGenre());
		assertEquals("Second Genre not found", "New Release", genres.get(1).getGenre());
		assertEquals("Third Genre not found", "Sample Movie", genres.get(2).getGenre());
		assertEquals("Fourth Genre not found", "Editor's Pick", genres.get(3).getGenre());
	}
	
	@Test
	public void testYear() {
		Year year = profile.scrapeYear();
		assertEquals("Found wrong year", "2014", year.getYear());
	}

	@Test
	public void testRuntime() {
		Runtime runtime = profile.scrapeRuntime();
		assertEquals("Wrong Runtime", "150", runtime.getRuntime());
	}
	
	@Test
	public void testPlot() {
		Plot plot = profile.scrapePlot();
		assertTrue("Found wrong Plot", plot.getPlot().startsWith(""));
	}
	
	@Test
	public void testPosters() throws IOException {
		Thumb[] posters = profile.scrapePosters();
		assertTrue("Wrong count of posters", posters.length == 1);
		assertEquals("Wrong poster", "http://imgs.aventertainments.com/new/bigcover/DVD1PT-154.jpg", posters[0].getThumbURL().toString());
		TestGenericProfile.showImage("posters", posters[0].getThumbImage());
	}
	
	@Test
	public void testFanart() throws IOException {
		Thumb[] fanart = profile.scrapeFanart();
		assertTrue("There should be 1 Fanart.", fanart.length == 1);
		
		assertEquals("Wrong Fanart", "http://imgs.aventertainments.com/new/bigcover/DVD1PT-154.jpg", fanart[0].getThumbURL().toString());
		TestGenericProfile.showImage("Fanart", fanart[0].getThumbImage());
	}
	
	@Test
	public void testSet() {
		Set set = profile.scrapeSet();
		assertEquals("Wrong Set", "Pork Teriyaki", set.getSet());
	}

	@Test
	public void testStudio() {
		Studio studio = profile.scrapeStudio();
		assertEquals("Wrong Studio", "Studio Teriyaki", studio.getStudio());
	}
}
