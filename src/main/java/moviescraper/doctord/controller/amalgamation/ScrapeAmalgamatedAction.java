package moviescraper.doctord.controller.amalgamation;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.view.GUIMain;
import moviescraper.doctord.view.ScrapeAmalgamatedProgressDialog;

public class ScrapeAmalgamatedAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	//a reference to the gui if we need to update the view in any workers. Can be null if we have no gui to update.
	private GUIMain guiMain;

	public static final String SCRAPE_KEY = "SCRAPE_KEY";

	ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference;

	public ScrapeAmalgamatedAction(GUIMain guiMain, ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference) {
		this.guiMain = guiMain;
		this.scraperGroupAmalgamationPreference = scraperGroupAmalgamationPreference;
		initializeDefaultValues("Scrape Adult Amalgamated " + scraperGroupAmalgamationPreference.toFriendlyString());
	}

	private void initializeDefaultValues(String name) {
		putValue(NAME, name);
		//putValue(SHORT_DESCRIPTION, name);
		//this allows us to have a unique name for this action; used to restore the last used scraper in the GUI as the default choice
		putValue(SCRAPE_KEY, scraperGroupAmalgamationPreference.toFriendlyString());
	}

	//Used for just scraping from one specific site. Allows us to reuse code, even though we are just amalgamating from one movie source
	public ScrapeAmalgamatedAction(GUIMain guiMain, SiteParsingProfile siteParsingProfile) {
		this.guiMain = guiMain;
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(siteParsingProfile);
		ScraperGroupAmalgamationPreference preferences = new ScraperGroupAmalgamationPreference(ScraperGroupName.DEFAULT_SCRAPER_GROUP, overallOrdering);
		this.scraperGroupAmalgamationPreference = preferences;
		if (this.guiMain != null)
			this.guiMain.getAllAmalgamationOrderingPreferences().allAmalgamationOrderingPreferences.put(ScraperGroupName.DEFAULT_SCRAPER_GROUP, this.scraperGroupAmalgamationPreference);
		initializeDefaultValues("Scrape " + siteParsingProfile.getDataItemSourceName());
		putValue(SCRAPE_KEY, siteParsingProfile.getDataItemSourceName());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (guiMain.getCurrentlySelectedMovieFileList() == null || guiMain.getCurrentlySelectedMovieFileList().size() == 0) {
			JOptionPane.showMessageDialog(null, "You must select a file before clicking scrape.", "No File Selected", JOptionPane.ERROR_MESSAGE);
			return;

		}

		if (guiMain != null) {

			guiMain.setMainGUIEnabled(false);
			guiMain.movieToWriteToDiskList.clear();
			guiMain.removeOldScrapedMovieReferences();
		}

		ScrapeAmalgamatedProgressDialog scraperWindow = new ScrapeAmalgamatedProgressDialog(guiMain, guiMain.getAllAmalgamationOrderingPreferences(), scraperGroupAmalgamationPreference);
		scraperWindow.setVisible(true);
	}

}
