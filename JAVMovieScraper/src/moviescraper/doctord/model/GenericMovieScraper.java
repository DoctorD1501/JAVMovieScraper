package moviescraper.doctord.model;

import java.util.ArrayList;
import java.util.List;

import moviescraper.doctord.Movie;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;
import moviescraper.doctord.preferences.MoviescraperPreferences;

public class GenericMovieScraper extends AbstractMovieScraper {
	
	protected SiteParsingProfile profile;
	protected ArrayList<Actor> scrapeActors;
	protected ArrayList<Director> scrapeDirectors;
	protected Thumb[] scrapeExtraFanart;
	protected Thumb[] scrapeFanart;
	protected ArrayList<Genre> scrapeGenres;
	protected ID scrapeID;
	protected MPAARating scrapeMPAA;
	protected OriginalTitle scrapeOriginalTitle;
	protected Outline scrapeOutline;
	protected Plot scrapePlot;
	protected Thumb[] scrapePosters;
	protected Rating scrapeRating;
	protected Runtime scrapeRuntime;
	protected Set scrapeSet;
	protected SortTitle scrapeSortTitle;
	protected Studio scrapeStudio;
	protected Tagline scrapeTagline;
	protected Title scrapeTitle;
	protected Top250 scrapeTop250;
	protected Trailer scrapeTrailer;
	protected Votes scrapeVotes;
	protected Year scrapeYear;

	public GenericMovieScraper( SiteParsingProfile spp ) {
		this.profile = spp;
	}
	
	public Movie createMovie() {
		scrapeGeneric(profile);
		return generateGeneric();
	}

	protected void scrapeGeneric(SiteParsingProfile spp) {
		scrapeActors = spp.scrapeActors();
		scrapeDirectors = spp.scrapeDirectors();
		scrapeExtraFanart = spp.scrapeExtraFanart();
		scrapeFanart = spp.scrapeFanart();
		scrapeGenres = spp.scrapeGenres();
		scrapeID = spp.scrapeID();
		scrapeMPAA = spp.scrapeMPAA();
		scrapeOriginalTitle = spp.scrapeOriginalTitle();
		scrapeOutline = spp.scrapeOutline();
		scrapePlot = spp.scrapePlot();
		scrapePosters = spp.scrapePosters();
		scrapeRating = spp.scrapeRating();
		scrapeRuntime = spp.scrapeRuntime();
		scrapeSet = spp.scrapeSet();
		scrapeSortTitle = spp.scrapeSortTitle();
		scrapeStudio = spp.scrapeStudio();
		scrapeTagline = spp.scrapeTagline();
		scrapeTitle = spp.scrapeTitle();
		scrapeTop250 = spp.scrapeTop250();
		scrapeTrailer = spp.scrapeTrailer();
		scrapeVotes = spp.scrapeVotes();
		scrapeYear = spp.scrapeYear();
		
		MoviescraperPreferences scraperPreferences = new MoviescraperPreferences();
		if(scraperPreferences.getAppendIDToStartOfTitle() && scrapeID != null && 
				scrapeID.getId() != null && scrapeID.getId().trim().length() > 0 && scrapeTitle != null
				&& scrapeTitle.getTitle() != null && scrapeTitle.getTitle().length() > 0)
		{
			scrapeTitle.setTitle(scrapeID.getId() + " - " + scrapeTitle.getTitle());
		}
	}
	
	protected Movie generateGeneric() {
		Movie movie = new Movie(scrapeActors, scrapeDirectors, scrapeFanart, scrapeExtraFanart, 
				scrapeGenres, scrapeID, scrapeMPAA, scrapeOriginalTitle, scrapeOutline, 
				scrapePlot, scrapePosters, scrapeRating, scrapeRuntime, scrapeSet, 
				scrapeSortTitle, scrapeStudio, scrapeTagline, scrapeTitle, scrapeTop250, 
				scrapeTrailer, scrapeVotes, scrapeYear);
		List<Title> titles = new ArrayList<>();
		titles.add( scrapeTitle );
		movie.setAllTitles(titles);
		return movie;
	}

}
