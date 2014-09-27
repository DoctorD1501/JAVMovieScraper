package moviescraper.doctord.model;

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

public class ScraperAction {

	private Collection<SiteParsingProfile> sppList;
	private AbstractMovieScraper movieScraper;
	private File toScrape;

	public ScraperAction( SiteParsingProfile spp, AbstractMovieScraper movieScraper, File toScrape ) {
		sppList = new ArrayList<>();
		sppList.add(spp);
		this.movieScraper = movieScraper;
		this.toScrape = toScrape;
	}
	
	public ScraperAction( Collection<SiteParsingProfile> spps, AbstractMovieScraper movieScraper, File toScrape ) {
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
				SearchResult searchResult = getSearchResult(selectionDialog, site);

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
