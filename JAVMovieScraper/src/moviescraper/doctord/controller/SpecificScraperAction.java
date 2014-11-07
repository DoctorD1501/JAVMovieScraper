package moviescraper.doctord.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.GUI.SelectionDialog;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.SpecificProfile;
import moviescraper.doctord.model.AbstractMovieScraper;

public class SpecificScraperAction {

	private Collection<SiteParsingProfile> sppList;
	private AbstractMovieScraper movieScraper;
	private File toScrape;

	public SpecificScraperAction( SiteParsingProfile spp, AbstractMovieScraper movieScraper, File toScrape ) {
		sppList = new ArrayList<>();
		sppList.add(spp);
		this.movieScraper = movieScraper;
		this.toScrape = toScrape;
	}
	
	public SpecificScraperAction( Collection<SiteParsingProfile> spps, AbstractMovieScraper movieScraper, File toScrape ) {
		this.sppList = spps;
		this.movieScraper = movieScraper;
		this.toScrape = toScrape;
	}
	
	public Movie scrape() {
		try {
			for (SiteParsingProfile spp : sppList) {
				String searchString = spp.createSearchString(toScrape);
				SearchResult[] results = spp.getSearchResults(searchString);
				String site = "Select Movie to Scrape From ";				
				if ( spp instanceof SpecificProfile )
					site += ((SpecificProfile) spp).getParserName();
				else
					site += spp.getClass().getSimpleName();
				
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

				if ( searchResult != null ) {
					Document document = downloadDocument(searchResult.getUrlPath());
					spp.setDocument(document);
				}
			}
			return movieScraper.createMovie();
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
			return Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
