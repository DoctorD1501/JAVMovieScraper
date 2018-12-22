package moviescraper.doctord.controller.siteparsingprofile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import moviescraper.doctord.controller.languagetranslation.Language;

import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class CaribbeancomParsingProfileTest {
	static File file = new File("C:/Temp/Caribbeancom 070514-637 abc.avi");
	static CaribbeancomParsingProfile parser = new CaribbeancomParsingProfile();

	@BeforeClass
	public static void initialize() {
		parser = new CaribbeancomParsingProfile();
		parser.setScrapingLanguage(Language.ENGLISH);
		String searchString = parser.createSearchString(file);
		try {
			SearchResult[] searchResults = parser.getSearchResults(searchString);
			Document document = SiteParsingProfile.getDocument(searchResults[0]);
			parser.setDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Test
	public void testFindID() {
		String findIDTagFromFile = parser.findIDTagFromFile(file);
		assertEquals(findIDTagFromFile, "070514-637");
	}

	@Test
	public void testScrapeTitle() {
		Title title = parser.scrapeTitle();
		assertEquals("Wrong title", "Ruka Ichinose", title.getTitle());
	}

	@Test
	public void testScrapeOriginalTitle() {
		OriginalTitle originalTitle = parser.scrapeOriginalTitle();
		assertEquals("Wrong original title", OriginalTitle.BLANK_ORIGINALTITLE, originalTitle);
	}

	@Test
	public void testScrapeRating() {
		Rating rating = parser.scrapeRating();
		assertEquals("Wrong rating", Rating.BLANK_RATING, rating);
	}

	@Test
	public void testScrapeYear() {
		Year year = parser.scrapeYear();
		assertEquals("Wrong year", "2014", year.getYear());
	}

	@Test
	public void testScrapeReleaseDate() {
		ReleaseDate releaseDate = parser.scrapeReleaseDate();
		assertEquals("Wrong release date", "2014-01-05", releaseDate.getReleaseDate());
	}

	@Test
	public void testScrapePlot() {
		Plot plot = parser.scrapePlot();
		assertEquals("Didn't scrape something which is long and looks like a plot", true, plot.getPlot().length() > 35);
	}

	@Test
	public void testScrapeRuntime() {
		moviescraper.doctord.model.dataitem.Runtime movieRuntime = parser.scrapeRuntime();
		assertEquals("Wrong runtime", "80", movieRuntime.getRuntime());
	}

	@SuppressWarnings("static-access")
	@Test
	public void testScrapeID() {
		ID id = parser.scrapeID();
		assertEquals("Wrong ID", "070514-637", id.getId());
		assertEquals("ID did not match from filename", parser.findIDTagFromFile(file), id.getId());
	}

	@Test
	public void testScrapeActors() {
		ArrayList<Actor> actorList = parser.scrapeActors();
		assertEquals("Wrong actor name", "Ruka Ichinose", actorList.get(0).getName());
	}

	@Test
	public void testScrapeGenre() {
		ArrayList<Genre> genreList = parser.scrapeGenres();
		assertEquals("Wrong genre", "creampie", genreList.get(0).getGenre());
	}

	@Test
	public void testTrailer() {
		Trailer trailer = parser.scrapeTrailer();
		assertEquals("Wrong trailer", "http://smovie.caribbeancom.com/sample/movies/070514-637/sample_m.mp4", trailer.getTrailer());
	}

	@Test
	public void testScrapePoster() {
		Thumb[] posters = parser.scrapePosters();
		assertEquals("Poster size not right", true, posters.length > 0);
		assertEquals("Wrong poster url", "http://www.caribbeancom.com/moviepages/070514-637/images/l_l.jpg", posters[0].getThumbURL().toString());
	}
}
