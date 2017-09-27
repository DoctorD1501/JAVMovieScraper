package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import moviescraper.doctord.view.GUIMain;

public class SelectAmalgamationSettingsAction implements ActionListener {

	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public SelectAmalgamationSettingsAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		guiMain.showAmalgamationSettingsDialog();
		
	}

}
