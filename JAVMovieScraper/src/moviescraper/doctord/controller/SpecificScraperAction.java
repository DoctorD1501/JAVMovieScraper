package moviescraper.doctord.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.jsoup.nodes.Document;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.SpecificProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.SelectionDialog;
/**
 * Deprecated - As of v0.2.00-alpha use ScrapeAmalgamationAction instead
 */
@Deprecated
public class SpecificScraperAction {

	private Collection<SiteParsingProfile> sppList;
	private AbstractMovieScraper movieScraper;
	private File toScrape;
	private boolean manuallyPickFanart;
	private boolean manuallyPickPoster;

	public SpecificScraperAction( SiteParsingProfile spp, AbstractMovieScraper movieScraper, File toScrape ) {
		sppList = new ArrayList<>();
		sppList.add(spp);
		this.movieScraper = movieScraper;
		this.toScrape = toScrape;
		manuallyPickPoster = true;
		manuallyPickFanart = true;
	}
	
	public SpecificScraperAction( Collection<SiteParsingProfile> spps, AbstractMovieScraper movieScraper, File toScrape ) {
		this.sppList = spps;
		this.movieScraper = movieScraper;
		this.toScrape = toScrape;
		manuallyPickPoster = true;
		manuallyPickFanart = true;
	}
	
	/**
	 * Changes what poster is the first element based on a user GUI window selection
	 * @param movieToModify - the Movie to change the order of the poster
	 * @return movieToModify with the poster element's order changed
	 */
	private Movie pickPoster(Movie movieToModify)
	{
		if (manuallyPickPoster && movieToModify != null
				&& movieToModify.getPosters() != null
				&& movieToModify.getPosters().length > 1) {
			Thumb[] posters = movieToModify.getPosters();

			Thumb posterPicked = ScrapeMovieAction.showArtPicker(posters,
					"Pick Poster");
			if (posterPicked != null) {
				// remove the item from the picked from the
				// existing poster and put it at the front of
				// the list
				ArrayList<Thumb> existingPosters = new ArrayList<Thumb>(
						Arrays.asList(movieToModify
								.getPosters()));
				existingPosters.remove(posterPicked);
				existingPosters.add(0, posterPicked);
				Thumb[] posterArray = new Thumb[existingPosters
				                                .size()];
				movieToModify
				.setPosters(existingPosters
						.toArray(posterArray));

			}
		}
		return movieToModify;
	}
	
	/**
	 * Changes what fanart is the first element based on a user GUI window selection
	 * @param movieToModify - the Movie to change the order of the fanart
	 * @return movieToModify with the poster element's order changed
	 */
	private Movie pickFanart(Movie movieToModify)
	{
		if (manuallyPickFanart && movieToModify != null
				&& movieToModify.getFanart() != null
				&& movieToModify.getFanart().length > 1) {
			Thumb[] fanart = movieToModify.getFanart();

			Thumb fanartPicked = ScrapeMovieAction.showArtPicker(fanart,
					"Pick Fanart");
			if (fanartPicked != null) {
				// remove the item from the picked from the
				// existing fanart and put it at the front of
				// the list
				ArrayList<Thumb> existingFanart = new ArrayList<Thumb>(
						Arrays.asList(movieToModify
								.getFanart()));
				existingFanart.remove(fanartPicked);
				existingFanart.add(0, fanartPicked);
				Thumb[] fanartArray = new Thumb[existingFanart
				                                .size()];
				movieToModify
				.setFanart(existingFanart
						.toArray(fanartArray));

			}
		}
		return movieToModify;
	}
	
	public Movie scrape(MoviescraperPreferences preferences) {
		try {
			for (SiteParsingProfile siteParsingProfile : sppList) {
				if(preferences.getPromptForUserProvidedURLWhenScraping())
					ScrapeMovieAction.setOverridenSearchResult(siteParsingProfile, toScrape.toString());
				
				SearchResult searchResultToUse = null;
				if(siteParsingProfile.getOverridenSearchResult() != null)
				{
					searchResultToUse = siteParsingProfile.getOverridenSearchResult();
				}
				else
				{
					String searchString = siteParsingProfile.createSearchString(toScrape);
					SearchResult[] results = siteParsingProfile.getSearchResults(searchString);
					String site = "Select Movie to Scrape From ";				
					if ( siteParsingProfile instanceof SpecificProfile )
						site += ((SpecificProfile) siteParsingProfile).getParserName();
					else
						site += siteParsingProfile.getClass().getSimpleName();
					
					if(results == null || results.length == 0)
						return null;
					
					SearchResult searchResult = null;
					//If there's only one item to choose from, save the user some work and just automatically choose it
					if(results != null && results.length == 1)
					{
						searchResult = results[0];
					}
					//otherwise, the user gets a dialog box where they can pick from the various search results
					else
					{
						SelectionDialog selectionDialog = new SelectionDialog(results, site);
						searchResult = getSearchResult(selectionDialog, site);
					}
					
					if(searchResult != null)
						searchResultToUse = searchResult;
				}

				if (searchResultToUse != null) {
					Document document = siteParsingProfile.downloadDocument(searchResultToUse);
					siteParsingProfile.setDocument(document);
				}
			}
			Movie scrapedMovie = movieScraper.createMovie();
			if(scrapedMovie == null || scrapedMovie.getTitle() == null || scrapedMovie.getTitle().getTitle().length() < 1)
				return null;
			scrapedMovie = pickPoster(scrapedMovie);
			scrapedMovie = pickFanart(scrapedMovie);
			return scrapedMovie;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private SearchResult getSearchResult(SelectionDialog selectionDialog, String title) {
		int result = JOptionPane.showOptionDialog(null, selectionDialog, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null);
		return selectionDialog.getSelectedValue();
	}
	
}
