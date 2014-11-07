package moviescraper.doctord.controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import moviescraper.doctord.GUI.GUIMain;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class OpenFileAction implements ActionListener {
	/**
	 * 
	 */
	private final GUIMain guiMain;

	/**
	 * @param guiMain
	 */
	public OpenFileAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	public void actionPerformed(ActionEvent arg0) {
		for(int movieNumberInList = 0; movieNumberInList < this.guiMain.getCurrentlySelectedMovieFileList().size(); movieNumberInList++)
		{
			if (this.guiMain.getCurrentlySelectedMovieFileList() != null) {
				try {
					Desktop.getDesktop().open(this.guiMain.getCurrentlySelectedMovieFileList().get(movieNumberInList));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
				}
			}

		}
	}
}