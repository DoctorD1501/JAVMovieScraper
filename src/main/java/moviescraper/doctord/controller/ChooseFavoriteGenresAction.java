package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import moviescraper.doctord.view.FavoriteGenrePickerPanel;
import moviescraper.doctord.view.GUIMain;

public class ChooseFavoriteGenresAction implements ActionListener {

	GUIMain guiMain;

	public ChooseFavoriteGenresAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		FavoriteGenrePickerPanel genrePickerPanel = new FavoriteGenrePickerPanel();
		int result = JOptionPane.showOptionDialog(guiMain.getFrmMoviescraper(), genrePickerPanel, "Favorite Genres...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (result == JOptionPane.OK_OPTION) {
			genrePickerPanel.storeSettingValues();
		}

	}

}
