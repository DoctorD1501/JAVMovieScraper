package moviescraper.doctord.controller.siteparsingprofile.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;

public abstract class GenericParsingProfileTest {
	
	protected static Movie expectedMovie;
	protected static Movie actualMovie;
	protected static File expectedValueFile;
	protected static SiteParsingProfile overloadedScraper;
	
	/**
	 * Set up values of expectedValueMovie, scrapedValueMovie, expectedValueFile, and overloadedScraper in one of the subclasses with
	 * the correct values for the scraper you are testing. This should be the only method you need to implement
	 * in a subclass of GenericParsingProfileTest to run a unit test for that parsing profile.
	 * Unfortunately, I couldn't make this method abstract due to limitations of java in that a static method can not be abstract,
	 * so I have commented this method out
	 */
	//@BeforeClass
	//public static void initialize(){};
	
	
	
	@Test
	public void testTitle(){
		assertEquals(expectedMovie.getTitle().getTitle(), actualMovie.getTitle().getTitle());
	}
	
	@Test
	public void testOriginalTitle(){
		assertEquals(expectedMovie.getOriginalTitle().getOriginalTitle(), actualMovie.getOriginalTitle().getOriginalTitle());
	}
	
	@Test
	public void testSet(){
		assertEquals(expectedMovie.getSet().getSet(), actualMovie.getSet().getSet());
	}
	
	@Test
	public void testPlot(){
		//allow a few differences because they may have just added a space or two in there or fixed a small typo
		assertTrue(StringUtils.getLevenshteinDistance(expectedMovie.getPlot().getPlot(), actualMovie.getPlot().getPlot()) < 4);
		//assertEquals(expectedValueMovie.getPlot().getPlot(), scrapedValueMovie.getPlot().getPlot());
	}
	
	@Test
	public void testRuntime(){
		assertEquals(expectedMovie.getRuntime().getRuntime(), actualMovie.getRuntime().getRuntime());
	}
	
	@Test
	public void testID() {
		assertEquals(expectedMovie.getId().getId(), actualMovie.getId().getId());
	}
	
	@Test
	public void testYear() {
		assertEquals(expectedMovie.getYear().getYear(), actualMovie.getYear().getYear());
	}
	
	@Test
	public void testTrailer() {
		assertEquals(expectedMovie.getTrailer().getTrailer(), actualMovie.getTrailer().getTrailer());
	}
	
	@Test
	public void testReleaseDate() {
		assertEquals(expectedMovie.getReleaseDate().getReleaseDate(), actualMovie.getReleaseDate().getReleaseDate());
	}
	
	@Test
	public void testGenre()
	{
		assertEquals(expectedMovie.getGenres(), actualMovie.getGenres());
	}
	
	@Test
	public void testTop250()
	{
		assertEquals(expectedMovie.getTop250().getTop250(), actualMovie.getTop250().getTop250());
	}
	
	@Test
	public void testTagline()
	{
		assertEquals(expectedMovie.getTagline().getTagline(), actualMovie.getTagline().getTagline());
	}
	
	@Test
	public void testOutline()
	{
		assertEquals(expectedMovie.getOutline().getOutline(), actualMovie.getOutline().getOutline());
	}
	
	@Test
	public void testMPAARating()
	{
		assertEquals(expectedMovie.getMpaa().getMPAARating(), actualMovie.getMpaa().getMPAARating());
	}
	
	@Test
	public void testSortTitle()
	{
		assertEquals(expectedMovie.getSortTitle().getSortTitle(), actualMovie.getSortTitle().getSortTitle());
	}
	
	@Test
	public void testStudio()
	{
		assertEquals(expectedMovie.getStudio().getStudio(), actualMovie.getStudio().getStudio());
	}
	
	@Test
	public void testActor()
	{
		assertEquals(expectedMovie.getActors().size(), actualMovie.getActors().size());
		Comparator<Actor> sortByActorName = new Comparator<Actor>() {

			@Override
			public int compare(Actor actor1, Actor actor2) {
				return actor1.getName().compareTo(actor2.getName());
			}

		};
		expectedMovie.getActors().sort(sortByActorName);
		actualMovie.getActors().sort(sortByActorName);
		for (int i = 0; i < expectedMovie.getActors().size(); i++) {
			Actor actorExpected = expectedMovie.getActors().get(i);
			Actor actorActual = actualMovie.getActors().get(i);
			assertEquals(actorExpected.getName(), actorActual.getName());
			assertEquals(actorExpected.getRole(), actorActual.getRole());
			assertEquals(actorExpected.getThumb(), actorActual.getThumb());
		}
			
	}
	
	@Test
	public void testDirector()
	{
		assertEquals(expectedMovie.getDirectors().size(), actualMovie.getDirectors().size());
		Comparator<Director> sortByDirectorName = new Comparator<Director>() {

			@Override
			public int compare(Director director1, Director director2) {
				return director1.getName().compareTo(director2.getName());
			}

		};
		expectedMovie.getDirectors().sort(sortByDirectorName);
		actualMovie.getDirectors().sort(sortByDirectorName);
		for (int i = 0; i < expectedMovie.getDirectors().size(); i++) {
			Director directorExpected = expectedMovie.getDirectors().get(i);
			Director directorActual = actualMovie.getDirectors().get(i);
			assertEquals(directorExpected.getName(), directorActual.getName());
			//Oddly, directors cannot have thumbnails in Kodi, so we aren't writing this info to the nfo
			//That is why I have removed the line below. If they ever allow it, we can uncomment this
			//assertEquals(directorExpected.getThumb(), directorActual.getThumb());
		}
			
	}
	
	@Test
	public void testPosters()
	{
		assertEquals(expectedMovie.getPosters().length, actualMovie.getPosters().length);
		for(int i = 0; i < expectedMovie.getPosters().length; i++)
		{
			assertEquals(expectedMovie.getPosters()[i], actualMovie.getPosters()[i]);
		}
	}
	
	@Test
	public void testFanart()
	{
		assertEquals(expectedMovie.getFanart().length, actualMovie.getFanart().length);
		for(int i = 0; i < expectedMovie.getFanart().length; i++)
		{
			assertEquals(expectedMovie.getFanart()[i], actualMovie.getFanart()[i]);
		}
	}
	
	public static Movie createMovieFromFileName(String fileName)
	{
		Movie scrapedMovieFromFile = null;
		URI movieOneURI;
		try {
			movieOneURI = new Object().getClass().getResource("/testdata/" + fileName).toURI();
			System.out.println("movieOneUri = " + movieOneURI);
			scrapedMovieFromFile = Movie.createMovieFromNfo(new File(movieOneURI));
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scrapedMovieFromFile;
	}

}
