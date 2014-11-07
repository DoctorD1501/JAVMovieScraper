package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;

import moviescraper.doctord.GUI.GUIMain;

public class ScrapeMovieActionAutomatic extends ScrapeMovieAction
{
	private static final long serialVersionUID = 1L;

	public ScrapeMovieActionAutomatic(GUIMain guiMain)
	{
		super(guiMain);
		putValue(NAME, "Scrape JAV (Automatic)");
		putValue(SHORT_DESCRIPTION, "Scrape Selected Movie (Automatic)");
		promptUserForURLWhenScraping = false;
		manuallyPickFanart = false;
	}
	public void actionPerformed(ActionEvent e){
		super.actionPerformed(e);
	}
}