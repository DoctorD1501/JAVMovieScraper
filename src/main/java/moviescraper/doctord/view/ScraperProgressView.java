package moviescraper.doctord.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedMovieWorker.ScrapeAmalgamatedMovieWorkerProperty;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.DataItemSource;

/**
 * Shows progress of an individual scraper during an amalgamated scrape and has
 *  controls to cancel the scrape
 *
 */
public class ScraperProgressView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel scraperNameLabel;

	private JLabel scrapedMovieTitle;

	private JButton progressButton;

	private DataItemSource scraper;

	private ScraperProgressPropertyChangeListener listener;

	private ScrapeAmalgamatedProgressDialog parentDialog;

	public ScraperProgressView(DataItemSource scraper, ScrapeAmalgamatedProgressDialog parentDialog) {
		//set up compenents
		this.scraper = scraper;
		listener = new ScraperProgressPropertyChangeListener();
		scraperNameLabel = new JLabel(scraper.getDataItemSourceName());
		setScraperLabel(scraperNameLabel);
		scrapedMovieTitle = new JLabel();
		progressButton = createCancelButton();
		this.parentDialog = parentDialog;
		//lay them out
		add(scraperNameLabel);
		add(scrapedMovieTitle);
		add(progressButton);
	}

	private void setScraperLabel(JLabel label) {
		Icon scraperLabelIcon;
		if (scraper instanceof SiteParsingProfile) {
			scraperLabelIcon = ((SiteParsingProfile) scraper).getProfileIcon();
			if (scraperLabelIcon != null && label != null)
				label.setIcon(scraperLabelIcon);
		}

	}

	public ScraperProgressPropertyChangeListener getScraperProgressPropertyChangeListener() {
		return listener;
	}

	private JButton createCancelButton() {
		JButton cancelButton = new JButton("Stop");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trying to cancel scraping of " + scraper.getDataItemSourceName());
				if (parentDialog != null) {
					if (scraper instanceof SiteParsingProfile) {
						System.out.println("Calling cancel method in parent dialog for " + scraper);
						parentDialog.cancelRunningScraper((SiteParsingProfile) scraper);
					}
				}

			}
		});
		return cancelButton;
	}

	private class ScraperProgressPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			//String propertyName = evt.getPropertyName();
			//System.out.println("ScraperProgressPropertyChangeListener: property changed with name = " + propertyName + " and oldValue =  " + evt.getOldValue() + " and newValue = " + evt.getNewValue());
			if (evt.getPropertyName().equals(ScrapeAmalgamatedMovieWorkerProperty.SCRAPED_MOVIE.toString())) {
				@SuppressWarnings("unchecked")
				List<Map<SiteParsingProfile, Movie>> newValue = (List<Map<SiteParsingProfile, Movie>>) evt.getNewValue();
				//if our view is the same type as one of the incoming scrapers, we can update our view with the status of the scraper
				for (Map<SiteParsingProfile, Movie> currentMap : newValue) {
					Set<SiteParsingProfile> keySet = currentMap.keySet();
					for (SiteParsingProfile currentSiteParsingProfile : keySet) {
						if (currentSiteParsingProfile.getDataItemSourceName().equals(scraper.getDataItemSourceName())) {
							List<Movie> movieScrapedList = new ArrayList<>(currentMap.values());
							Movie movieScraped = null;
							if (movieScrapedList.size() > 0)
								movieScraped = movieScrapedList.get(0);
							ScraperProgressView.this.updateProgressViewWithScrapeFinished(movieScraped);
						}
					}
				}
			}
		}

	}

	public void updateProgressViewWithScrapeFinished(Movie movieThatScraped) {
		progressButton.setText("Done");
		progressButton.setEnabled(false);
		if (movieThatScraped != null && movieThatScraped.hasValidTitle()) {
			//Commented this out because it is causing text not to fit
			//I will revist this in a later release because while nice to have it is not mandatory to see the movie name that has been scraped
			//scrapedMovieTitle.setText(movieThatScraped.getTitle().getTitle());
		}

	}

	public void resetPanelForNextScrape() {
		progressButton.setText("Stop");
		progressButton.setEnabled(true);

	}

}
