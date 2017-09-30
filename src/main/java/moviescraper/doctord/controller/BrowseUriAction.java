package moviescraper.doctord.controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowseUriAction implements ActionListener {

	String uri;

	public BrowseUriAction(String uri) {
		this.uri = uri;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			Desktop.getDesktop().browse(new URI(uri));
		} catch (IOException | URISyntaxException ex) {
			ex.printStackTrace();
		}
	}

	public static final String MainWebsiteUri = "https://github.com/DoctorD1501/JAVMovieScraper";
	public static final String ReportBugUri = "https://github.com/DoctorD1501/JAVMovieScraper/issues?q=label%3Abug";
}
