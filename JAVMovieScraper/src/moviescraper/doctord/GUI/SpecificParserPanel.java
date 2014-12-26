package moviescraper.doctord.GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.SiteParsingProfile.SpecificProfileFactory;
import moviescraper.doctord.SiteParsingProfile.specific.AvEntertainmentParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.SpecificProfile;
import moviescraper.doctord.controller.SpecificScraperAction;

public class SpecificParserPanel extends JPanel {

	private static final long serialVersionUID = 4639300587159880348L;
	private JComboBox<SiteParsingProfileItem> comboBox;
	private Vector<SiteParsingProfileItem> items = new Vector<SiteParsingProfileItem>();
	
	private GUIMain main;

	public SpecificParserPanel(GUIMain main) {
		this.main = main;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel label = new JLabel("Choose Specific Scraper");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);
		
		comboBox = new JComboBox<SiteParsingProfileItem>(items);
		add(comboBox);
		
		JButton btnScrape = new JButton("Scrape");
		btnScrape.setAlignmentX( CENTER_ALIGNMENT );
		add(btnScrape);
		btnScrape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Find out the type of parser to use from the drop down box and pass it off to the scrape method
				Object selectedItem = comboBox.getSelectedItem();
				if (selectedItem instanceof SiteParsingProfileItem) {
					SiteParsingProfile spp = ((SiteParsingProfileItem) selectedItem).getParser();
					if ( spp != null ) {
						scrape(spp);
					}
				}	
			}
		});

		try {
			initSpinner();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void scrape(SiteParsingProfile spp) {
		try
		{
			main.setMainGUIEnabled(false);
			main.removeOldScrapedMovieReferences();
			List<File> toScrape = main.getCurrentFile();
			if (toScrape != null) {
				for(File currentFile : toScrape)
				{ 
					//reset the SiteParsingProfile so we don't get leftover stuff from the last file scraped
					//we want it to be of the same type, so we use the newInstance() method which will automatically
					//return a new object of the type the SiteParsingProfile actually is
					spp = spp.newInstance();
					spp.setScrapingLanguage(main.getPreferences());
					SpecificScraperAction action = new SpecificScraperAction(spp, spp.getMovieScraper(), currentFile );
					Movie scrapedMovie = action.scrape();
					if(scrapedMovie != null)
						main.movieToWriteToDiskList.add(scrapedMovie);
					main.getFileDetailPanel().setNewMovie( scrapedMovie , true);
				}

			} else {
				JOptionPane.showMessageDialog(this, "No file selected.", "No file selected.", JOptionPane.ERROR_MESSAGE);
			}
		}
		finally
		{
			main.setMainGUIEnabled(true);
		}
	}

	private void initSpinner() {
		
		items.addAll(SpecificProfileFactory.getAll());
		
		if (items.size() > 0)
			comboBox.setSelectedIndex(0);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame("Test Parser Panel");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.add( new SpecificParserPanel(null) );
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
