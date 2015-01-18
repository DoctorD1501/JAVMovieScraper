package moviescraper.doctord.SiteParsingProfile.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.specific.HeyzoParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Year;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

public class HeyzoParsingProfileTest {
	
	static File file = new File("C:/Temp/Heyzo 0194.wmv");
	static HeyzoParsingProfile profile;
	
	@BeforeClass
	public static void initialize() {
		profile = new HeyzoParsingProfile( );
		String url = profile.createSearchString(file);
		Document document = SpecificScraperAction.downloadDocument(url);
		profile.setDocument(document);
	}
	
	@Test
	public void testSearchString() {
		String searchString = profile.createSearchString(file);
		assertEquals("Wrong searchString.", "http://en.heyzo.com/moviepages/0194/index.html" , searchString);
	}
	
	@Test
	public void testFindID() {
		@SuppressWarnings("static-access")
		String id = profile.findIDTagFromFile(file);
		assertEquals("Found Wrong ID", "0194", id);
	}
	
	@Test
	public void testRuntime() {
		//It seems there might be a bug on the site where the runtimes are all displaying as zero
		//This might get fixed later, so if this test case is failing, it may be OK for now until Heyzo fixes
		//their issue because this file used to be 68 minutes
		Runtime runtime = profile.scrapeRuntime();
		System.out.println("Runtime = " + runtime.getRuntime());
		assertEquals("Wrong Runtime", "68", runtime.getRuntime());
	}
	
	@Test public void testTitle(){
		Title title = profile.scrapeTitle();
		System.out.println("title = " + title);
		assertEquals("Wrong Title", "Make Woopie with The Cutest Costume Idol! - Miku Oguri", title.getTitle());
	}
	
	@Test public void testOriginalTitle(){
		OriginalTitle originalTitle = profile.scrapeOriginalTitle();
		System.out.println("originalTitle = " + originalTitle);
		assertEquals("Wrong original title", "ロリカワ！人気絶頂コスドルを撮影会でヤッちゃいました - おぐりみく", originalTitle.getOriginalTitle());
	}
	
	@Test public void testRating(){
		Rating rating = profile.scrapeRating();
		System.out.println("rating = " + rating);
		assertEquals("Wrong rating", "7.2", rating.getRatingOutOfTen());
	}
	
	@Test public void testYear(){
		Year year = profile.scrapeYear();
		System.out.println("year = " + year);
		assertEquals("Wrong year", "2012", year.getYear());
	}
	
	@Test public void testPoster(){
		String posterURL = profile.scrapePosters()[0].getThumbURL().toString();
		System.out.println("poster url of first poster = " + posterURL);
		assertEquals("Wrong poster url", "http://en.heyzo.com/contents/3000/0194/gallery/001.jpg", posterURL);
	}
	
	@Test public void testTrailer(){
		Trailer trailer = profile.scrapeTrailer();
		System.out.println("trailer = " + trailer);
		assertEquals("Wrong trailer url", "http://sample.heyzo.com/contents/3000/0194/heyzo_hd_0194_sample.mp4", trailer.getTrailer());
	}
	
	@Test public void testActors(){
		ArrayList<Actor> actorList = profile.scrapeActors();
		System.out.println("actorList = " + actorList);
		assertEquals("Wrong actor list name", "Miku Oguri", actorList.get(0).getName());
		try {
			assertEquals("Wrong actor list url", new Thumb("http://en.heyzo.com/actorprofile/3000/0192/profile.jpg").toString(), actorList.get(0).getThumb().toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test public void testGenres(){
		ArrayList<Genre> genreList = profile.scrapeGenres();
		System.out.println("genreList = " + genreList);
		assertEquals("Wrong genre list item", "Cosplay", genreList.get(6).getGenre());
	}

}
