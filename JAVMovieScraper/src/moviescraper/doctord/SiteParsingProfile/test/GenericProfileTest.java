package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.*;

import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import moviescraper.doctord.SiteParsingProfile.IAFDParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Thumb;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenericProfileTest {

	static SiteParsingProfile profile;
	static File file = new File("C:/Temp/Empty File.avi"); 
	
	@BeforeClass
	public static void initialize() throws Exception {
		profile = new IAFDParsingProfile();
		String searchString = profile.createSearchString(file);
		Document document = SpecificScraperAction.downloadDocument(searchString);
		profile.setDocument(document);
	}

	@Test
	public void testActor() {
		ArrayList<Actor> actors = profile.scrapeActors();
		assertTrue("There should be 1 Actor.", actors.size() == 1);
		assertEquals("Wrong Actor.", "Gina Gerson", actors.get(0).getName());
	}
	
	@Test
	public void testDirectors() {
		ArrayList<Director> directors = profile.scrapeDirectors();
		assertTrue("There should be only 1 Director", directors.size() == 1);
		assertEquals("Wrong director", "Name", directors.get(0).getName());
	}
	
	@Test
	public void testExtraFanart() {
		Thumb[] extraFanart = profile.scrapeExtraFanart();
		assertTrue("Wrong count of extra fanart", extraFanart.length == 1);
		assertEquals("Wrong extra fanart", "http://", extraFanart[0].getThumbURL());
	}

	@Test
	public void testFanart() throws IOException {
		Thumb[] fanart = profile.scrapeFanart();
		assertTrue("There should be 1 Fanart.", fanart.length == 1);
		
		showImage("Fanart", fanart[0].getThumbImage());
	}

	@Test
	public void testGenre() {
		ArrayList<Genre> genres = profile.scrapeGenres();
		assertTrue("There should be 9 genres.", genres.size() == 9);
		
		assertEquals("First Genre not found", "Low Speck", genres.get(0).getGenre());
		assertEquals("Second Genre not found", "Shaved", genres.get(1).getGenre());
		assertEquals("Third Genre not found", "Thong", genres.get(2).getGenre());
		assertEquals("Fourth Genre not found", "Japanese Men VS", genres.get(3).getGenre());
		//there are some more genres
	}

	@Test
	public void testID() {
		ID id = profile.scrapeID();
		assertEquals("Found Wrong ID", "1000", id.getId());
	}
	
	@Test
	public void testMPAA() {
		MPAARating mpaa = profile.scrapeMPAA();
		assertEquals("Wrong MPAA", "1", mpaa.getMPAARating());
	}

	@Test
	public void testOutline() {
		Outline outline = profile.scrapeOutline();
		assertEquals("Wrong Outline", "outline", outline.getOutline());
	}

	@Test
	public void testPlot() {
		Plot plot = profile.scrapePlot();
		assertTrue("Wrong Plot", plot.getPlot().startsWith(""));
	}

	@Test
	public void testPosters() {
		Thumb[] posters = profile.scrapePosters();
		assertTrue("Wrong count of posters", posters.length == 1);
		assertEquals("Wrong poster", "http://", posters[0].getThumbURL().toString());
	}

	@Test
	public void testRating() {
		Rating rating = profile.scrapeRating();
		assertEquals("Wrong Rating", "1.0", rating.getRating());
	}

	@Test
	public void testRuntime() {
		Runtime runtime = profile.scrapeRuntime();
		assertEquals("Wrong Runtime", "26", runtime.getRuntime());
	}

	@Test
	public void testSearchString() {
		String searchString = profile.createSearchString(file);
		assertEquals("Wrong searchString.", "http://en.kin8tengoku.com/1000/pht/shosai.htm" , searchString);
	}
	
	@Test
	public void testSet() {
		Set set = profile.scrapeSet();
		assertEquals("Wrong Set", "set", set.getSet());
	}

	@Test
	public void testSortTitle() {
		SortTitle sortTitle = profile.scrapeSortTitle();
		assertEquals("Wrong SortTitle", "SortTitle", sortTitle.getSortTitle());
	}

	@Test
	public void testStudio() {
		Studio studio = profile.scrapeStudio();
		assertEquals("Wrong Studio", "Studio", studio.getStudio());
	}

	@Test
	public void testTagline() {
		Tagline tagline = profile.scrapeTagline();
		assertEquals("Wrong Tagline", "Tagline", tagline.getTagline());
	}

	@Test
	public void testTitle() {
		Title title = profile.scrapeTitle();
		assertEquals("Wrong Title", "Title", title.getTitle());
	}

	@Test
	public void testTop250() {
		Top250 top250 = profile.scrapeTop250();
		assertEquals("Wrong Top250", "Top250", top250.getTop250());
	}

	@Test
	public void testTrailer() {
		Trailer trailer = profile.scrapeTrailer();
		assertEquals("Wrong Trailer", "Trailer", trailer.getTrailer());
	}

	@Test
	public void testVotes() {
		Votes votes = profile.scrapeVotes();
		assertEquals("Wrong Votes", "1.0", votes.getVotes());
	}

	@Test
	public void testYear() {
		Year year = profile.scrapeYear();
		assertEquals("Wrong Year", "2014", year.getYear());
	}
	
	public static void showImage(final String title, final Image image) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame jFrame = new JFrame(title);
					jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					jFrame.add( new JLabel(new ImageIcon(image)) );
					jFrame.pack();
					jFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
