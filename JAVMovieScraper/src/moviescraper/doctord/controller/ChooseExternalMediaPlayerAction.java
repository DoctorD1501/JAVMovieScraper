package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.preferences.GuiSettings;

public class ChooseExternalMediaPlayerAction implements ActionListener {
	
	private final GUIMain guiMain;
	
	public ChooseExternalMediaPlayerAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("Choosing External Media Player");
		JFileChooser chooser = new JFileChooser();
		 int returnVal = chooser.showOpenDialog(guiMain.getFrmMoviescraper());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       if(chooser.getSelectedFile() != null)
		    	   GuiSettings.getInstance().setPathToExternalMediaPlayer(chooser.getSelectedFile().toString());
		    }

	}

}
