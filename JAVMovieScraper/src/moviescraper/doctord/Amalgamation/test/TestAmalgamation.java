package moviescraper.doctord.Amalgamation.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import moviescraper.doctord.Movie;
import moviescraper.doctord.Amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.Amalgamation.MovieScrapeResultGroup;
import moviescraper.doctord.SiteParsingProfile.specific.AvEntertainmentParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.R18ParsingProfile;
import moviescraper.doctord.controller.SpecificScraperAction;

public class TestAmalgamation {
	
	static Movie movieOne;
	static Movie movieTwo;
	static Movie movieThree;
	static Movie amalgamatedMovie;
	
	@BeforeClass
	public static void initialize() throws URISyntaxException {
		System.out.println("Testing amalgamation");
		DataItemSourceAmalgamationPreference allThree = new DataItemSourceAmalgamationPreference(new R18ParsingProfile(), new JavLibraryParsingProfile(), new DmmParsingProfile());
		DataItemSourceAmalgamationPreference justTwo = new DataItemSourceAmalgamationPreference(new R18ParsingProfile(), new JavLibraryParsingProfile());
		try {
			
			URI movieOneURI = new Object().getClass().getResource("/res/testdata/Movie1.nfo").toURI();
			URI movieTwoURI = new Object().getClass().getResource("/res/testdata/Movie2.nfo").toURI();
			URI movieThreeURI = new Object().getClass().getResource("/res/testdata/Movie3.nfo").toURI();
			
			//3rd, except poster which should be 1st and also actors will be first

			System.out.println(movieOneURI);
			movieOne = Movie.createMovieFromNfo(new File(movieOneURI));
			movieOne.getTitle().setDataItemSource(new DmmParsingProfile());
			movieOne.getActors().get(0).setDataItemSource(new DmmParsingProfile());
			movieOne.getPosters()[0].setDataItemSource(new DmmParsingProfile());
			movieOne.getPosters()[0].setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			movieOne.getTitle().setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile()));
			movieOne.getActors().get(0).setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			
			//1st, except poster
			movieTwo = Movie.createMovieFromNfo(new File(movieTwoURI));
			movieTwo.getTitle().setDataItemSource(new R18ParsingProfile());
			movieTwo.getActors().get(0).setDataItemSource(new R18ParsingProfile());
			movieTwo.getPosters()[0].setDataItemSource(new R18ParsingProfile());
			movieTwo.getFanart()[0].setDataItemSource(new R18ParsingProfile());
			movieTwo.getPlot().setDataItemSource(new R18ParsingProfile()); //plot will be the one returned from a global sort
			movieTwo.getPosters()[0].setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			movieTwo.getTitle().setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile()));
			movieTwo.getActors().get(0).setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			
			
			//2nd, except Title which should be first
			movieThree = Movie.createMovieFromNfo(new File(movieThreeURI));
			movieThree.getTitle().setDataItemSource(new JavLibraryParsingProfile());
			movieThree.getActors().get(0).setDataItemSource(new JavLibraryParsingProfile());
			movieThree.getPosters()[0].setDataItemSource(new JavLibraryParsingProfile());
			movieThree.getPosters()[0].setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			movieThree.getTitle().setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new JavLibraryParsingProfile()));
			movieThree.getActors().get(0).setDataItemAmalgamtionPreference(new DataItemSourceAmalgamationPreference(new DmmParsingProfile()));
			
			List<Movie> movieList = Arrays.asList(movieOne, movieTwo, movieThree);
			MovieScrapeResultGroup movieScrapeResultGroup = new MovieScrapeResultGroup(movieList, allThree);
			amalgamatedMovie = movieScrapeResultGroup.amalgamateMovie();
			System.out.println("amalgamated movie is " + amalgamatedMovie);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testAmalgamatedTitleCustomDataItemSort()
	{
		assertEquals("Wrong amalgamated title using a custom data item sort", amalgamatedMovie.getTitle(), movieThree.getTitle());
	}
	
	@Test
	public void testAmalgamatedPosterCustomDataItemSort(){
		assertEquals("Wrong amalgamated posters using a custom data item sort", amalgamatedMovie.getPosters()[0], movieOne.getPosters()[0]);
	}
	
	@Test
	public void testActorsCustomDataItemSort(){
		assertEquals("Wrong amalgamated actors using a custom data item sort", amalgamatedMovie.getActors(), movieOne.getActors());
	}
	
	@Test
	public void testPlotGlobalSort()
	{
		assertEquals("Wrong plot using a standard amalgamation", amalgamatedMovie.getPlot(), movieTwo.getPlot());
	}
	
}
