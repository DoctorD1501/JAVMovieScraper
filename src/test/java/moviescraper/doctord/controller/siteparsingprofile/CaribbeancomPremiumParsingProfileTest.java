package moviescraper.doctord.controller.siteparsingprofile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomPremiumParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.controller.languagetranslation.Language;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class CaribbeancomPremiumParsingProfileTest {

	static File file = new File("C:/Temp/Caribbeancom Premium 122716_008.avi");
	static CaribbeancomPremiumParsingProfile parser = new CaribbeancomPremiumParsingProfile();

	@BeforeClass
	public static void initialize() {
		parser = new CaribbeancomPremiumParsingProfile();
		parser.setScrapingLanguage(Language.ENGLISH);
		String searchString = parser.createSearchString(file);
		try {
			SearchResult[] searchResults = parser.getSearchResults(searchString);
			Document document = SiteParsingProfile.getDocument(searchResults[0]);
			System.out.println("Scrape: " + document.location());
			parser.setDocument(document);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file, false);
		assertEquals(findIDTagFromFile, "122716_008");
	}

	@Test
	public void testScrapeID() {
		String id = parser.scrapeID().getId();
		assertEquals("122716_008", id);
	}

	@Test
	public void testScrapeTitle() {
		Title title = parser.scrapeTitle();
		//this assumes translation is done. if this test fails, it could be because translation is not done or the web
		//based translation service has changed how they do translation, so try to just see if the title is close to
		//this one and adjust as needed to fix the test case
		assertEquals("", title.getTitle());
	}

	@Test
	public void testScrapeOriginalTitle() {
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Wrong original title", "", originalTitle.getOriginalTitle());
	}

	@Test
	public void testScrapeRating() {
		Rating rating = parser.scrapeRating();
		assertEquals("Wrong rating", "", rating.getRatingOutOfTen());
	}

	@Test
	public void testScrapeYear() {
		Year year = parser.scrapeYear();
		assertEquals("Wrong year", "2016", year.getYear());
	}

	@Test
	public void testScrapeReleaseDate() {
		ReleaseDate releaseDate = parser.scrapeReleaseDate();
		assertEquals("Wrong release date", "2016-12-28", releaseDate.getReleaseDate());
	}

	@Test
	public void testScrapePlot() {
		Plot plot = parser.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", Plot.BLANK_PLOT, plot);
	}

	@Test
	public void testScrapeRuntime() {
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
		assertEquals("Wrong runtime", "189", movieRuntime.getRuntime());
	}

	@Test
	public void testScrapeActors() {
		ArrayList<Actor> actorList = parser.scrapeActors();
		assertEquals("Wrong actor", "Akari Asagiri", actorList.get(0).getName());
	}

	@Test
	public void testScrapeGenre() {
		ArrayList<Genre> genreList = parser.scrapeGenres();
		assertEquals("Wrong genre size", 14, genreList.size());
		assertEquals("Wrong genre", "AV Idol", genreList.get(0).getGenre());
	}

	@Test
	public void testTrailer() {
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Wrong trailer", "https://smovie.caribbeancompr.com/sample/movies/122716_008/480p.mp4", trailer.getTrailer());
	}

	@Test
	public void testScrapePoster() {
		Thumb[] posters = parser.scrapePosters();
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "https://en.caribbeancompr.com/moviepages/122716_008/images/l_l.jpg", posters[0].getThumbURL().toString());
	}

}
