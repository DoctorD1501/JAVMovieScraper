package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.SiteParsingProfile.SpecificProfileFactory;

public class SelectScrapersAction implements ActionListener {

	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public SelectScrapersAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<String> scrapers = Arrays.asList("DMM.co.jp","ActionJav","SquarePlus","JavLibrary", "JavZoo", "R18.com");
		List<String> saved = Arrays.asList(guiMain.getPreferences().getSelectedScrapers());
		List<String> selected = new ArrayList<String>(saved);
		
		selected.retainAll(scrapers);
		
		if (guiMain.showSelectScrapersDialog(scrapers, selected))
			guiMain.getPreferences().setSelectedScrapers(selected.toArray(new String[0]));
		
	}

}
