package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;

import moviescraper.doctord.GUI.GUIMain;

public class ScrapeMovieActionData18Movie extends ScrapeMovieAction
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScrapeMovieActionData18Movie(GUIMain guiMain)
	{
		super(guiMain);
		putValue(NAME, "Scrape Data18 Movie");
		putValue(SHORT_DESCRIPTION, "Scrape Data18 Movie");
		promptUserForURLWhenScraping = true;
		this.scrapeData18Movie = true;
		this.scrapeJAV = false;
	}
	public void actionPerformed(ActionEvent e){
		super.actionPerformed(e);
	}
}