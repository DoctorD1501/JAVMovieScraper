package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import moviescraper.doctord.view.FavoriteTagPickerPanel;
import moviescraper.doctord.view.GUIMain;

public class ChooseFavoriteTagsAction implements ActionListener {
	
	GUIMain guiMain;

	public ChooseFavoriteTagsAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		FavoriteTagPickerPanel tagPickerPanel = new FavoriteTagPickerPanel();
		int result = JOptionPane.showOptionDialog(guiMain.getFrmMoviescraper(), tagPickerPanel, "Favorite Tags...",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null);
		if(result == JOptionPane.OK_OPTION)
		{
			tagPickerPanel.storeSettingValues();
		}
		

	}

}
