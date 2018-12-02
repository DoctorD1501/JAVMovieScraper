package moviescraper.doctord.view;

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

import moviescraper.doctord.controller.Renamer;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tag;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.model.preferences.Settings;

public class RenamerGUI extends JFrame {

	private static final long serialVersionUID = -5068144242360229926L;
	private JPanel contentPane;
	private JTextField textFieldRenameString;
	private JTextField textFieldFolderRenameString;
	private JTextField textFieldSanitizerString;
	private FileDetailPanel fileDetailPanel;
	private JPanel buttonsPanel;
	private JButton btnOk;
	private JButton btnCancel;
	private JLabel lblExample;
	private JTextField textFieldExample;
	private JScrollPane scrollPane;

	//order of form elements, y direction
	private static final int fileRenameY = 0;
	private static final int folderRenameY = 1;
	private static final int availableTagsForFileY = 2;
	private static final int availableTagsForFolderY = 3;
	private static final int sanitizerY = 4;
	private static final int exampleY = 5;
	private static final int filePreviewerY = 6;
	private static final int buttonsPanelY = 7;

	private Movie sampleMovie;
	private File sampleFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					RenamerGUI frame = new RenamerGUI(MoviescraperPreferences.getInstance(), null, new File("C:/Temp/Good old Movie.avi"));
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
	public RenamerGUI(final MoviescraperPreferences preferences, Movie sampleMovie, File sampleFile) {
		this.setTitle("Rename Settings");
		this.setIconImage(GUICommon.getProgramIcon());
		this.sampleMovie = sampleMovie;
		if (this.sampleMovie == null || !this.sampleMovie.hasValidTitle())
			this.sampleMovie = getFakeMovie();
		if (sampleFile == null)
			this.sampleFile = getFakeFile();
		else
			this.sampleFile = sampleFile;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1200, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 }; //file detail panel (2nd from last) expands to fill space
		contentPane.setLayout(gbl_contentPane);

		//File Rename String
		JLabel lblRenamestring = new JLabel("File Rename String:");
		GridBagConstraints gbc_lblRenamestring = new GridBagConstraints();
		gbc_lblRenamestring.insets = new Insets(0, 0, 5, 5);
		gbc_lblRenamestring.anchor = GridBagConstraints.EAST;
		gbc_lblRenamestring.gridx = 0;
		gbc_lblRenamestring.gridy = fileRenameY;
		contentPane.add(lblRenamestring, gbc_lblRenamestring);

		textFieldRenameString = new JTextField(MoviescraperPreferences.getRenamerString());
		GridBagConstraints gbc_textFieldRenameString = new GridBagConstraints();
		gbc_textFieldRenameString.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldRenameString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldRenameString.gridx = 1;
		gbc_textFieldRenameString.gridy = fileRenameY;
		contentPane.add(textFieldRenameString, gbc_textFieldRenameString);
		textFieldRenameString.setColumns(10);
		textFieldRenameString.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				preferences.setRenamerString(textFieldRenameString.getText());
				updateExample();
			}
		});

		//Folder Rename String
		JLabel lblFolderRenamestring = new JLabel("Folder Rename/Move String:");
		GridBagConstraints gbc_lblFolderRenamestring = new GridBagConstraints();
		gbc_lblFolderRenamestring.insets = new Insets(0, 0, 5, 5);
		gbc_lblFolderRenamestring.anchor = GridBagConstraints.EAST;
		gbc_lblFolderRenamestring.gridx = 0;
		gbc_lblFolderRenamestring.gridy = folderRenameY;
		contentPane.add(lblFolderRenamestring, gbc_lblFolderRenamestring);

		textFieldFolderRenameString = new JTextField(MoviescraperPreferences.getFolderRenamerString());
		GridBagConstraints gbc_textFieldFolderRenameString = new GridBagConstraints();
		gbc_textFieldFolderRenameString.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldFolderRenameString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldFolderRenameString.gridx = 1;
		gbc_textFieldFolderRenameString.gridy = folderRenameY;
		contentPane.add(textFieldFolderRenameString, gbc_textFieldFolderRenameString);
		textFieldFolderRenameString.setColumns(10);
		textFieldFolderRenameString.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				preferences.setFolderRenamerString(textFieldFolderRenameString.getText());
				updateExample();
			}
		});

		//Available Tags - File

		JLabel lblAvailableString = new JLabel("Available Tags For File Rename String:");
		GridBagConstraints gbc_lblavailablestring = new GridBagConstraints();
		gbc_lblavailablestring.insets = new Insets(0, 0, 5, 5);
		gbc_lblavailablestring.anchor = GridBagConstraints.EAST;
		gbc_lblavailablestring.gridx = 0;
		gbc_lblavailablestring.gridy = availableTagsForFileY;
		contentPane.add(lblAvailableString, gbc_lblavailablestring);

		JTextField availbleTagsTextField = new JTextField(Renamer.getAvailableFileTags());
		availbleTagsTextField.setEditable(false);
		GridBagConstraints gbc_textFieldRenameStringTwo = new GridBagConstraints();
		gbc_textFieldRenameStringTwo.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldRenameStringTwo.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldRenameStringTwo.gridx = 1;
		gbc_textFieldRenameStringTwo.gridy = availableTagsForFileY;
		contentPane.add(availbleTagsTextField, gbc_textFieldRenameStringTwo);
		availbleTagsTextField.setColumns(10);

		//Available Tags - Folder
		JLabel lblAvailableFolderString = new JLabel("Available Tags For Folder Rename/Move String:");
		GridBagConstraints gbc_lblavailableFolderstring = new GridBagConstraints();
		gbc_lblavailableFolderstring.insets = new Insets(0, 0, 5, 5);
		gbc_lblavailableFolderstring.anchor = GridBagConstraints.EAST;
		gbc_lblavailableFolderstring.gridx = 0;
		gbc_lblavailableFolderstring.gridy = availableTagsForFolderY;
		contentPane.add(lblAvailableFolderString, gbc_lblavailableFolderstring);

		JTextField availbleTagsFolderTextField = new JTextField(Renamer.getAvailableFolderTags());
		availbleTagsFolderTextField.setEditable(false);
		GridBagConstraints gbc_textFieldAvailableFolder = new GridBagConstraints();
		gbc_textFieldAvailableFolder.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldAvailableFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAvailableFolder.gridx = 1;
		gbc_textFieldAvailableFolder.gridy = availableTagsForFolderY;
		contentPane.add(availbleTagsFolderTextField, gbc_textFieldAvailableFolder);
		availbleTagsFolderTextField.setColumns(10);

		//Sanitizer
		JLabel lblSanitizerString = new JLabel("Sanitizer String:");
		GridBagConstraints gbc_lblSanitizerString = new GridBagConstraints();
		gbc_lblSanitizerString.insets = new Insets(0, 0, 5, 5);
		gbc_lblSanitizerString.anchor = GridBagConstraints.EAST;
		gbc_lblSanitizerString.gridx = 0;
		gbc_lblSanitizerString.gridy = sanitizerY;
		contentPane.add(lblSanitizerString, gbc_lblSanitizerString);

		textFieldSanitizerString = new JTextField(MoviescraperPreferences.getSanitizerForFilename());
		GridBagConstraints gbc_textFieldSanitizerString = new GridBagConstraints();
		gbc_textFieldSanitizerString.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSanitizerString.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSanitizerString.gridx = 1;
		gbc_textFieldSanitizerString.gridy = sanitizerY;
		contentPane.add(textFieldSanitizerString, gbc_textFieldSanitizerString);
		textFieldSanitizerString.setColumns(10);
		textFieldSanitizerString.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				preferences.setSanitizerForFilename(textFieldSanitizerString.getText());
				updateExample();
			}
		});

		//Preview Results
		lblExample = new JLabel("Example :");
		GridBagConstraints gbc_lblExample = new GridBagConstraints();
		gbc_lblExample.anchor = GridBagConstraints.EAST;
		gbc_lblExample.insets = new Insets(0, 0, 5, 5);
		gbc_lblExample.gridx = 0;
		gbc_lblExample.gridy = exampleY;
		contentPane.add(lblExample, gbc_lblExample);

		textFieldExample = new JTextField();
		textFieldExample.setEditable(false);
		GridBagConstraints gbc_textFieldExample = new GridBagConstraints();
		gbc_textFieldExample.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldExample.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldExample.gridx = 1;
		gbc_textFieldExample.gridy = exampleY;
		contentPane.add(textFieldExample, gbc_textFieldExample);
		textFieldExample.setColumns(10);

		//File Detail Panel
		fileDetailPanel = new FileDetailPanel(preferences, new GUIMain());
		fileDetailPanel.hideArtworkPanel();

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(fileDetailPanel);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = filePreviewerY;
		contentPane.add(scrollPane, gbc_scrollPane);

		//OK, Cancel Button Panel
		buttonsPanel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = buttonsPanelY;
		contentPane.add(buttonsPanel, gbc_panel);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		btnOk = new JButton("OK");
		buttonsPanel.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.savePreferences();
				RenamerGUI.this.dispose();
			}
		});

		btnCancel = new JButton("Cancel");
		buttonsPanel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RenamerGUI.this.dispose();
			}
		});

		fileDetailPanel.setNewMovie(getFakeMovie(), false, true);
		updateExample();
		this.setVisible(true);
	}

	protected void updateExample() {
		Renamer renamer = new Renamer(textFieldRenameString.getText(), textFieldFolderRenameString.getText(), textFieldSanitizerString.getText(), fileDetailPanel.currentMovie, sampleFile);
		textFieldExample.setText(renamer.getNewFileName(sampleFile.isDirectory()));
	}

	private File getFakeFile() {
		return new File("C:/Temp/Good old Movie.avi");
	}

	private Movie getFakeMovie() {

		if (sampleMovie != null && sampleMovie.hasValidTitle())
			return sampleMovie;

		Actor actorA = new Actor("Actor A", null, null);
		Actor actorB = new Actor("Actor B", null, null);
		ArrayList<Actor> actors = new ArrayList<>(5);
		Collections.addAll(actors, actorA, actorB);

		Director directorA = new Director("Director Name", null);
		ArrayList<Director> directors = new ArrayList<>();
		Collections.addAll(directors, directorA);

		Thumb[] fanart = new Thumb[0];
		Thumb[] extraFanart = new Thumb[0];

		Genre genreA = new Genre("GenreA");
		Genre genreB = new Genre("GenreB");
		ArrayList<Genre> genres = new ArrayList<>(5);
		Collections.addAll(genres, genreA, genreB);

		Tag tagA = new Tag("TagA");
		Tag tagB = new Tag("TagB");
		ArrayList<Tag> tags = new ArrayList<>(5);

		Collections.addAll(tags, tagA, tagB);

		ID id = new ID("ABC-123");
		MPAARating mpaa = new MPAARating("PG-13");
		OriginalTitle originalTitle = new OriginalTitle("Original Title");
		Outline outline = new Outline("Outline");
		Plot plot = new Plot("Plot");
		Thumb[] posters = new Thumb[0];

		Rating rating = new Rating(6.0, "Rating");
		ReleaseDate releaseDate = new ReleaseDate("1999-08-25");
		Runtime runtime = new Runtime("60 min");
		Set set = new Set("Set");
		SortTitle sortTitle = new SortTitle("SortTitle");
		Studio studio = new Studio("Studio");
		Tagline tagline = new Tagline("Tagline");
		Title title = new Title("MovieTitle");
		Top250 top250 = new Top250("Top250");
		Trailer trailer = new Trailer(null);
		Votes votes = new Votes("Votes");
		Year year = new Year("1999");

		return new Movie(actors, directors, fanart, extraFanart, genres, tags, id, mpaa, originalTitle, outline, plot, posters, rating, releaseDate, runtime, set, sortTitle, studio, tagline, title,
		        top250, trailer, votes, year);
	}
}
