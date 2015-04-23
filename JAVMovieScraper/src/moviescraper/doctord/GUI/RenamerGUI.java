package moviescraper.doctord.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import moviescraper.doctord.Movie;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;
import moviescraper.doctord.model.Renamer;
import moviescraper.doctord.preferences.MoviescraperPreferences;

public class RenamerGUI extends JFrame {

	private static final long serialVersionUID = -5068144242360229926L;
	private JPanel contentPane;
	private JTextField textFieldRenameString;
	private JTextField textFieldSanitizerString;
	private FileDetailPanel fileDetailPanel;
	private JPanel panel;
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblExample;
	private JTextField textFieldExample;
	private JScrollPane scrollPane;
	
	Movie sampleMovie;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RenamerGUI frame = new RenamerGUI(MoviescraperPreferences.getInstance(),null);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RenamerGUI(final MoviescraperPreferences preferences, Movie sampleMovie) {
		System.out.println("calling constr with sampleMovie = " + sampleMovie);
		this.sampleMovie = sampleMovie;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblRenamestring = new JLabel("Rename String:");
		GridBagConstraints gbc_lblRenamestring = new GridBagConstraints();
		gbc_lblRenamestring.insets = new Insets(0, 0, 5, 5);
		gbc_lblRenamestring.anchor = GridBagConstraints.EAST;
		gbc_lblRenamestring.gridx = 0;
		gbc_lblRenamestring.gridy = 0;
		contentPane.add(lblRenamestring, gbc_lblRenamestring);
		
		textFieldRenameString = new JTextField(preferences.getRenamerString());
		GridBagConstraints gbc_textFieldRenameString = new GridBagConstraints();
		gbc_textFieldRenameString.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldRenameString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldRenameString.gridx = 1;
		gbc_textFieldRenameString.gridy = 0;
		contentPane.add(textFieldRenameString, gbc_textFieldRenameString);
		textFieldRenameString.setColumns(10);
		textFieldRenameString.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				preferences.setRenamerString( textFieldRenameString.getText() );
				updateExample();
			}
		});
		
		JLabel lblAvailableString = new JLabel("Available Tags For Rename String:");
		GridBagConstraints gbc_lblavailablestring = new GridBagConstraints();
		gbc_lblavailablestring.insets = new Insets(0, 0, 5, 5);
		gbc_lblavailablestring.anchor = GridBagConstraints.EAST;
		gbc_lblavailablestring.gridx = 0;
		gbc_lblavailablestring.gridy = 1;
		contentPane.add(lblAvailableString, gbc_lblavailablestring);
		
		JTextField availbleTagsTextField = new JTextField(Renamer.getAvailableTags());
		availbleTagsTextField.setEditable(false);
		GridBagConstraints gbc_textFieldRenameStringTwo = new GridBagConstraints();
		gbc_textFieldRenameStringTwo.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldRenameStringTwo.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldRenameStringTwo.gridx = 1;
		gbc_textFieldRenameStringTwo.gridy = 1;
		contentPane.add(availbleTagsTextField, gbc_textFieldRenameStringTwo);
		availbleTagsTextField.setColumns(10);
		
		JLabel lblSanitizerString = new JLabel("Sanitizer String:");
		GridBagConstraints gbc_lblSanitizerString = new GridBagConstraints();
		gbc_lblSanitizerString.insets = new Insets(0, 0, 5, 5);
		gbc_lblSanitizerString.anchor = GridBagConstraints.EAST;
		gbc_lblSanitizerString.gridx = 0;
		gbc_lblSanitizerString.gridy = 2;
		contentPane.add(lblSanitizerString, gbc_lblSanitizerString);
		
		textFieldSanitizerString = new JTextField(preferences.getSanitizerForFilename());
		GridBagConstraints gbc_textFieldSanitizerString = new GridBagConstraints();
		gbc_textFieldSanitizerString.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSanitizerString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSanitizerString.gridx = 1;
		gbc_textFieldSanitizerString.gridy = 2;
		contentPane.add(textFieldSanitizerString, gbc_textFieldSanitizerString);
		textFieldSanitizerString.setColumns(10);
		textFieldSanitizerString.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				preferences.setSanitizerForFilename( textFieldSanitizerString.getText() );
				updateExample();
			}
		});
		
		lblExample = new JLabel("Example :");
		GridBagConstraints gbc_lblExample = new GridBagConstraints();
		gbc_lblExample.anchor = GridBagConstraints.EAST;
		gbc_lblExample.insets = new Insets(0, 0, 5, 5);
		gbc_lblExample.gridx = 0;
		gbc_lblExample.gridy = 3;
		contentPane.add(lblExample, gbc_lblExample);
		
		textFieldExample = new JTextField();
		textFieldExample.setEditable(false);
		GridBagConstraints gbc_textFieldExample = new GridBagConstraints();
		gbc_textFieldExample.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldExample.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldExample.gridx = 1;
		gbc_textFieldExample.gridy = 3;
		contentPane.add(textFieldExample, gbc_textFieldExample);
		textFieldExample.setColumns(10);		

		fileDetailPanel = new FileDetailPanel(preferences, new GUIMain());
		fileDetailPanel.hideArtworkPanel();
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(fileDetailPanel);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 5;
		contentPane.add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		btnOk = new JButton("OK");
		panel.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				preferences.savePreferences();
				RenamerGUI.this.dispose();
			}
		});
		
		btnCancel = new JButton("Cancel");
		panel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RenamerGUI.this.dispose();
			}
		});

		fileDetailPanel.setNewMovie(getFakeMovie(), false);
		updateExample();
		this.setVisible(true);
	}

	protected void updateExample() {
		Renamer renamer = new Renamer(textFieldRenameString.getText(), textFieldSanitizerString.getText(),
				fileDetailPanel.currentMovie, getFakeFile());
		textFieldExample.setText( renamer.getNewFileName() );
	}
	
	private File getFakeFile() {
		return new File("C:/Temp/Good old Movie.avi");
	}

	private Movie getFakeMovie() {
		
		if(sampleMovie != null)
			return sampleMovie;
		
		
		Actor actorA = new Actor("Actor A", null, null);
		Actor actorB = new Actor("Actor B", null, null);
		ArrayList<Actor> actors = new ArrayList<Actor>(5);
		Collections.addAll(actors, actorA, actorB);
		
		Director directorA = new Director("Director Name", null);
		ArrayList<Director> directors = new ArrayList<>();
		Collections.addAll(directors, directorA);
		
		Thumb[] fanart = new Thumb[0]; 
		Thumb[] extraFanart = new Thumb[0]; 

		Genre genreA = new Genre("GenreA");
		Genre genreB = new Genre("GenreB");
		ArrayList<Genre> genres = new ArrayList<Genre>(5);
		Collections.addAll(genres, genreA, genreB);
		
		ID id = new ID("ABC-123");
		MPAARating mpaa = new MPAARating("PG-13");
		OriginalTitle originalTitle = new OriginalTitle("Original Title");
		Outline outline = new Outline("Outline");
		Plot plot = new Plot("Plot");
		Thumb[] posters = new Thumb[0]; 

		Rating rating = new Rating(6.0, "Rating");
		Runtime runtime = new Runtime("60 min");
		Set set = new Set("Set");
		SortTitle sortTitle= new SortTitle("SortTitle");
		Studio studio = new Studio("Studio");
		Tagline tagline = new Tagline("Tagline");
		Title title = new Title("MovieTitle");
		Top250 top250 = new Top250("Top250");
		Trailer trailer = new Trailer(null);
		Votes votes = new Votes("Votes");
		Year year = new Year("1999");
		
		return new Movie(actors, directors, fanart, extraFanart, genres, id, mpaa, originalTitle, outline, plot, posters, rating, runtime, set, sortTitle, studio, tagline, title, top250, trailer, votes, year);
	}
}
