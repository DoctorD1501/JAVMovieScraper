package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;

import moviescraper.doctord.GUI.GUIMain;

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
	public void actionPerformed(ActionEvent e){
		super.actionPerformed(e);
	}
}