package moviescraper.doctord.controller;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;

public class GenericMovieScraper extends AbstractMovieScraper {

	protected SiteParsingProfile profile;

	public GenericMovieScraper(SiteParsingProfile spp) {
		this.profile = spp;
	}

	@Override
	public Movie createMovie() {
		return new Movie(profile);
	}

}
