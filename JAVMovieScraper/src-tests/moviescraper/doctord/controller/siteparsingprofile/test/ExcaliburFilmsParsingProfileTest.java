package moviescraper.doctord.controller.siteparsingprofile.test;

import moviescraper.doctord.controller.siteparsingprofile.specific.ExcaliburFilmsParsingProfile;
import moviescraper.doctord.model.Movie;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class ExcaliburFilmsParsingProfileTest extends GenericParsingProfileTest{

	@BeforeClass
	public static void initialize() {
		System.out.println("Testing Excalibur Films Parsing Profile");
		overloadedScraper = new ExcaliburFilmsParsingProfile();
		expectedValueFile = new File("C:/Temp/Pirates 2/");
		
		try {
			actualMovie = Movie.scrapeMovie(expectedValueFile, overloadedScraper, "", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull("Movie should be scrapped", actualMovie);
		expectedMovie = GenericParsingProfileTest.createMovieFromFileName("ExcaliburFilmsSiteParsingProfileTestMovie.nfo");
	}

}
