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

import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.TokyoHotParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Year;
import moviescraper.doctord.model.ScraperAction;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTokyoHotParsingProfile {

	static File file = new File("C:/Temp/Tokyo Hot n0754 abc.avi");
	TokyoHotParsingProfile parser = new TokyoHotParsingProfile();
	
	private static TokyoHotParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		profile = new TokyoHotParsingProfile();
		String searchString = profile.createSearchString(file);
		System.out.println(searchString);
		Document document = ScraperAction.downloadDocument(searchString);
		profile.setDocument(document);
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
	
	@Test
	public void testFanart() throws IOException {
		Thumb[] scrapeFanart = profile.scrapeFanart();
		assertTrue("There should only be 1 fanart", scrapeFanart.length == 1);
		showImage("Test Fanart", scrapeFanart[0].getThumbImage());
	}
	
	@Test
	public void testPoster() throws IOException {
		Thumb[] scrapePosters = profile.scrapePosters();
		assertTrue("There should only be 1 fanart", scrapePosters.length == 1);
		showImage("Test Fanart", scrapePosters[0].getThumbImage());
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
