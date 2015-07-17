package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;

import moviescraper.doctord.view.GUIMain;
/**
 * Deprecated - As of v0.2.00-alpha use ScrapeAmalgamationAction instead
 */
@Deprecated
public class ScrapeMovieActionData18WebContent extends ScrapeMovieAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScrapeMovieActionData18WebContent(GUIMain guiMain)
	{
		super(guiMain);
		putValue(NAME, "Scrape Data18 WebContent");
		putValue(SHORT_DESCRIPTION, "Scrape Data18 WebContent");
		promptUserForURLWhenScraping = true;
		this.scrapeData18Movie = false;
		this.scrapeJAV = false;
		this.scrapeData18WebContent = true;
	}
	@Override
	public void actionPerformed(ActionEvent e){
		super.actionPerformed(e);
	}
}