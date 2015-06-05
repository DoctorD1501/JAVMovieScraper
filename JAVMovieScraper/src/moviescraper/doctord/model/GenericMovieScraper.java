package moviescraper.doctord.model;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;

public class GenericMovieScraper extends AbstractMovieScraper {
	
	protected SiteParsingProfile profile;

	public GenericMovieScraper( SiteParsingProfile spp ) {
		this.profile = spp;
	}
	
	@Override
	public Movie createMovie() {
		return new Movie(profile);
	}

}
