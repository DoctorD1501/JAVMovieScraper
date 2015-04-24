package moviescraper.doctord.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.GUI.SelectionDialog;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileJSON;
import moviescraper.doctord.SiteParsingProfile.specific.SpecificProfile;
import moviescraper.doctord.model.AbstractMovieScraper;
import moviescraper.doctord.preferences.MoviescraperPreferences;

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
			for (SiteParsingProfile spp : sppList) {
				if(preferences.getPromptForUserProvidedURLWhenScraping())
					ScrapeMovieAction.setOverridenSearchResult(spp, toScrape.toString());
				
				SearchResult searchResultToUse = null;
				if(spp.getOverridenSearchResult() != null)
				{
					searchResultToUse = spp.getOverridenSearchResult();
				}
				else
				{
					String searchString = spp.createSearchString(toScrape);
					SearchResult[] results = spp.getSearchResults(searchString);
					String site = "Select Movie to Scrape From ";				
					if ( spp instanceof SpecificProfile )
						site += ((SpecificProfile) spp).getParserName();
					else
						site += spp.getClass().getSimpleName();
					
					if(results == null || results.length == 0)
						return null;
					SelectionDialog selectionDialog = new SelectionDialog(results, site);
					SearchResult searchResult = null;
					//If there's only one item to choose from, save the user some work and just automatically choose it
					if(results != null && results.length == 1)
					{
						searchResult = results[0];
					}
					//otherwise, the user gets a dialog box where they can pick from the various search results
					else
					{
						searchResult = getSearchResult(selectionDialog, site);
					}
					
					if(searchResult != null)
						searchResultToUse = searchResult;
				}

				if (searchResultToUse != null) {
					Document document = downloadDocument(searchResultToUse);
					spp.setDocument(document);
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
	
	public static Document downloadDocument(String url) {
		try {
			return Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Document downloadDocument(SearchResult searchResult){
		try {
			if(searchResult.isJSONSearchResult())
				return SiteParsingProfileJSON.getDocument(searchResult.getUrlPath());
			else return Jsoup.connect(searchResult.getUrlPath()).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
