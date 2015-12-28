package moviescraper.doctord.controller.siteparsingprofile.test;

import static org.junit.Assert.*;

import java.io.File;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.OnePondoParsingProfile;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class OnePondoParsingProfileTest {
	static boolean scrapeInEnglish = true;
	static File file = new File("C:/Temp/1pondo 061314_826 abc.avi");
	OnePondoParsingProfile parser = new OnePondoParsingProfile();
	
	private static OnePondoParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		
		profile = new OnePondoParsingProfile();
		String searchString = profile.createSearchString(file);
		System.out.println(searchString);
		Document document = SiteParsingProfile.downloadDocumentFromURLString(searchString);
		profile.setDocument(document);
	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		System.out.println(findIDTagFromFile);
		assertEquals(findIDTagFromFile, "061314_826");
	}
	
	@Test
	public void testID() {
		ID scrapeID = profile.scrapeID();
		System.out.println("scrapeID = " + scrapeID);
		assertEquals("Found wrong ID", "061314_826", scrapeID.getId());
	}
	
	@Test
	public void testYear() {
		Year scrapeYear = profile.scrapeYear();
		
		assertEquals("Found wrong year", "2014", scrapeYear.getYear());
	}
	
	@Test
	public void testReleaseDate(){
		ReleaseDate scrapeReleaseDate = profile.scrapeReleaseDate();
		assertEquals("Found wrong releaseDate", "2014-06-13", scrapeReleaseDate.getReleaseDate());
	}
}
