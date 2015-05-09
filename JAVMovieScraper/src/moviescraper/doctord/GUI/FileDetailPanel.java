package moviescraper.doctord.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import moviescraper.doctord.Movie;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.GUI.AbstractFileDetailPanelEditGUI.Operation;
import moviescraper.doctord.GUI.renderer.ActressListRenderer;
import moviescraper.doctord.GUI.renderer.GenreListRenderer;
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
import moviescraper.doctord.preferences.MoviescraperPreferences;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileDetailPanel extends JPanel {

	private static final long serialVersionUID = 7088761619568387476L;
	
	private JComboBox<String> comboBoxMovieTitleText;
	private JLabel lblOriginalTitleText;
	private JLabel lblScrapedYearText;
	private JLabel lblIDCurrentMovie;
	private JTextField txtFieldStudio;
	private JTextField txtFieldMovieSet;
	private JTextArea moviePlotTextField;
	private JList<Actor> actorList;
	private JList<Genre> genreList;
	
	protected Movie currentMovie = getEmptyMovie();
	MoviescraperPreferences preferences;

	private ArtWorkPanel artWorkPanel;
	
	GUIMain gui;

	/**
	 * Create the panel.
	 */
	public FileDetailPanel(MoviescraperPreferences preferences, GUIMain gui) {
		this.preferences = preferences;
		this.gui = gui;
		JPanel fileDetailsPanel = this;
				
		FormLayout formLayout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, // 1 - empty space
				FormFactory.DEFAULT_COLSPEC, //2 - label for each of the form items
				FormFactory.RELATED_GAP_COLSPEC,//3 - empty space
				ColumnSpec.decode("default:grow"), // 4 - Form text items
				FormFactory.RELATED_GAP_COLSPEC,//5 - empty space
				FormFactory.DEFAULT_COLSPEC,// 6 - artwork panel
				FormFactory.RELATED_GAP_COLSPEC,//7 - empty space
			},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, //1 - empty space
				FormFactory.DEFAULT_ROWSPEC, //2 - Title and artwork panel
				FormFactory.RELATED_GAP_ROWSPEC,//3 - empty space
				FormFactory.DEFAULT_ROWSPEC,//4 - original title
				FormFactory.RELATED_GAP_ROWSPEC,//5 - empty space
				FormFactory.DEFAULT_ROWSPEC,//6 - Year
				FormFactory.RELATED_GAP_ROWSPEC,//7 - empty space
				FormFactory.DEFAULT_ROWSPEC,//8 - ID
				FormFactory.RELATED_GAP_ROWSPEC,//9 - empty space
				FormFactory.DEFAULT_ROWSPEC,//10 - Studio
				FormFactory.RELATED_GAP_ROWSPEC,//11 - empty space
				FormFactory.DEFAULT_ROWSPEC,//12 - Movie set
				FormFactory.RELATED_GAP_ROWSPEC,//13 - empty space
				FormFactory.DEFAULT_ROWSPEC,//14 - Plot
				FormFactory.RELATED_GAP_ROWSPEC,//15 - empty space
				RowSpec.decode("default:grow"),//16 - actors
				FormFactory.RELATED_GAP_ROWSPEC,//17 - empty space
				RowSpec.decode("default:grow"),//18 - genres
				FormFactory.RELATED_GAP_ROWSPEC//19 - empty space
				});
		
		
		//formLayout.setColumnGroups(new int[][]{{4, 6}});
		
		fileDetailsPanel.setLayout(formLayout);


		JLabel lblTitle = new JLabel("Title:");
		fileDetailsPanel.add(lblTitle, "2, 2");
		
		//using this workaround for JComboBox constructor for problem with generics in WindowBuilder as per this stackoverflow thread: https://stackoverflow.com/questions/8845139/jcombobox-warning-preventing-opening-the-design-page-in-eclipse
		comboBoxMovieTitleText = new JComboBox<String>();
		//Prevent the title of a really long moving from making the combo box way too long
		//Instead it will only show the first part and the user will have to scroll in the editing box to see the rest
		comboBoxMovieTitleText.setPrototypeDisplayValue("AAAAAAAAAAAAAAAAAAAAAAAAAAA");
		comboBoxMovieTitleText.setModel( new TitleListModel() );
		comboBoxMovieTitleText.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) comboBoxMovieTitleText.getSelectedItem();
	            	if(newValue != null)
	            	{
	            		currentMovie.setTitle(new Title(newValue));
	            	}
	            }
	        }
	    });
		comboBoxMovieTitleText.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) comboBoxMovieTitleText.getSelectedItem();
            	if(newValue != null)
            	{
            		currentMovie.setTitle(new Title(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		//movieTitlePanel.add(comboBoxMovieTitleText);
		fileDetailsPanel.add(comboBoxMovieTitleText, "4, 2");



		JLabel lblOriginalTitle = new JLabel("Original Title:");
		fileDetailsPanel.add(lblOriginalTitle, "2, 4");



		lblOriginalTitleText = new JLabel("");
		fileDetailsPanel.add(lblOriginalTitleText, "4, 4");


		JLabel lblYear = new JLabel("Year:");
		fileDetailsPanel.add(lblYear, "2, 6");
		

		lblScrapedYearText = new JLabel("");
		fileDetailsPanel.add(lblScrapedYearText, "4, 6");
		
		JLabel lblID = new JLabel("ID:");
		fileDetailsPanel.add(lblID, "2, 8");
		
		lblIDCurrentMovie = new JLabel("");
		fileDetailsPanel.add(lblIDCurrentMovie,"4, 8");
		
		JLabel lblStudio = new JLabel("Studio:");
		txtFieldStudio = new JTextField("");
		txtFieldStudio.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldStudio.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setStudio(new Studio(newValue));
	            	}
	            }
	        }
	    });
		txtFieldStudio.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldStudio.getText();
            	if(newValue != null)
            	{
            		currentMovie.setStudio(new Studio(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		
		fileDetailsPanel.add(lblStudio,"2,10");
		fileDetailsPanel.add(txtFieldStudio,"4,10");
		
		JLabel lblSet = new JLabel("Movie Set:");
		fileDetailsPanel.add(lblSet,"2, 12");
		txtFieldMovieSet = new JTextField("");
		txtFieldMovieSet.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldMovieSet.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setSet(new Set(newValue));
	            	}
	            }
	        }
	    });
		txtFieldMovieSet.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldMovieSet.getText();
            	if(newValue != null)
            	{
            		currentMovie.setSet(new Set(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		fileDetailsPanel.add(txtFieldMovieSet,"4,12");
		
		JLabel lblPlot = new JLabel("Plot:");
		fileDetailsPanel.add(lblPlot, "2,14");
		

		moviePlotTextField = new JTextArea(3,35);
		moviePlotTextField.setLineWrap(true);
		moviePlotTextField.setWrapStyleWord(true);
		moviePlotTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) moviePlotTextField.getText();
            	if(newValue != null)
            	{
            		currentMovie.setPlot(new Plot(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});

		JScrollPane plotPanelScrollPane = new JScrollPane(moviePlotTextField, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		fileDetailsPanel.add(plotPanelScrollPane,"4,14");
		

		JLabel lblActors = new JLabel("Actors:");
		fileDetailsPanel.add(lblActors, "2, 16");

		actorList = new JList<Actor>(new ActorItemListModel());
		List<File> currentlySelectedActorsFolderList = new ArrayList<File>();
		if(gui != null)
			currentlySelectedActorsFolderList = gui.getCurrentlySelectedActorsFolderList();
		actorList.setCellRenderer(new ActressListRenderer(currentlySelectedActorsFolderList));
		actorList.setComponentPopupMenu(new FileDetailPanelPopup(new FileDetailPanelActorEditor(this)));
		actorList.addMouseListener(new MouseAdapter() {

		     @Override
		     public void mousePressed(MouseEvent e) {
		    	 actorList.setSelectedIndex(actorList.locationToIndex(e.getPoint()));
		     }
		     //double or triple click the actor list to open the editor on the item you clicked
		     public void mouseClicked(MouseEvent evt) {
			        if (evt.getClickCount() == 2 || evt.getClickCount() == 3) {
			            // Double-click detected
			            FileDetailPanelActorEditor actorEditor = new FileDetailPanelActorEditor(FileDetailPanel.this);
			            actorEditor.showGUI(Operation.EDIT);
			        }
			    }
		});


		JScrollPane actorListScroller = new JScrollPane(actorList);
		actorListScroller.setPreferredSize(new Dimension(250, 250));
		actorList.setSize(new Dimension(250, 250));
		fileDetailsPanel.add(actorListScroller, "4, 16");

		JLabel lblGenres = new JLabel("Genres:");
		fileDetailsPanel.add(lblGenres, "2, 18");

		genreList = new JList<Genre>(new GenreItemListModel());
		//double or triple click the genre list to open the editor on the item you clicked
		genreList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() == 2 || evt.getClickCount() == 3) {

		            // Double-click detected
		            FileDetailPanelGenreEditor genreEditor = new FileDetailPanelGenreEditor(FileDetailPanel.this);
		            genreEditor.showGUI(Operation.EDIT);
		        }
		    }
		});
		genreList.setCellRenderer(new GenreListRenderer());
		JScrollPane listScrollerGenres = new JScrollPane(genreList);
		genreList.setComponentPopupMenu(new FileDetailPanelPopup(new FileDetailPanelGenreEditor(this)));
		
		genreList.setSize(new Dimension(200, 200));
		fileDetailsPanel.add(listScrollerGenres, "4, 18");
		
		artWorkPanel = new ArtWorkPanel();
		//frmMoviescraper.getContentPane().add(artworkPanelScrollPane, BorderLayout.EAST);
		fileDetailsPanel.add(artWorkPanel,"6,2,1,18");
	}
	/**
	 * Sets a new movie and updates a view
	 * @param newMovie the movie this fileDetailPanel will show
	 * @param forcePosterUpdate whether to force a redownload from the poster defined in the Thumb url
	 */
	
	public void setNewMovie(Movie newMovie, boolean forcePosterUpdate) {
		System.out.println("Setting new movie: " + newMovie);
		if(newMovie != null)
		{
			setCurrentMovie(newMovie);
			updateView(forcePosterUpdate, true);
		}
	}
	
	public void clearView() {
		artWorkPanel.clearPictures();
		setTitleEditable(false);
		this.currentMovie = getEmptyMovie();
	}

	/**
	 * Updates the view for the current movie
	 * @param forcePosterUpdate - if force a refresh of the poster from the URL by downloading the file. If false, tries to
	 * read from the local file first
	 * @param newMovieWasSet - true if you are setting a new movie. clears the old one and refreshes all fields
	 */
	public void updateView(boolean forcePosterUpdate, boolean newMovieWasSet) {
		
		List<Movie> movieToWriteToDiskList = gui.getMovieToWriteToDiskList();
		if(newMovieWasSet && movieToWriteToDiskList.size() == 0)
		{
			movieToWriteToDiskList.add(currentMovie);
		}
		//begin
		if ((movieToWriteToDiskList == null || movieToWriteToDiskList.size() == 0) && !newMovieWasSet) {
			clearView();
		} else if (movieToWriteToDiskList != null && movieToWriteToDiskList.get(0) != null) {
			if(!newMovieWasSet)
				clearView();
			if(!newMovieWasSet)
				this.setCurrentMovie(movieToWriteToDiskList.get(0));

			//All the titles from the various versions scraped of this movie from the different sites
			if(movieToWriteToDiskList != null)
				this.getCurrentMovie().getAllTitles().add( getCurrentMovie().getTitle() );
			if(gui.getCurrentlySelectedMovieR18() != null)
				this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieR18().getTitle() );
			if(gui.getCurrentlySelectedMovieDMM() != null)
				this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieDMM().getTitle() );
			if(gui.getCurrentlySelectedMovieJavLibrary() != null)
			{
				//we might have replaced JavLib's title during amalgamation, so let's get the original one
				//before this happens in the drop down title list
				if(gui.getOriginalJavLibraryMovieTitleBeforeAmalgamate() != null && 
						gui.getOriginalJavLibraryMovieTitleBeforeAmalgamate().length() > 0)
				{
					this.getCurrentMovie().getAllTitles().add(new Title(gui.getOriginalJavLibraryMovieTitleBeforeAmalgamate()));
				}
				else
				{
					this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieJavLibrary().getTitle() );
				}
			}
			if(gui.getCurrentlySelectedMovieSquarePlus() != null)
				this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieSquarePlus().getTitle() );
			if(gui.getCurrentlySelectedMovieActionJav() != null)
				this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieActionJav().getTitle() );
			if(gui.getCurrentlySelectedMovieJavZoo() != null)
				this.getCurrentMovie().getAllTitles().add( gui.getCurrentlySelectedMovieJavZoo().getTitle() );
			if(this.getCurrentMovie().getAllTitles().size() > 0)
				this.setTitleEditable(true);
		//end
		}
		comboBoxMovieTitleText.setModel( new TitleListModel() );
		comboBoxMovieTitleText.setEditable(true);
		lblOriginalTitleText.setText( currentMovie.getOriginalTitle().getOriginalTitle() );
		lblScrapedYearText.setText( currentMovie.getYear().getYear() );
		lblIDCurrentMovie.setText( currentMovie.getId().getId() );
		txtFieldStudio.setText( currentMovie.getStudio().getStudio() );
		txtFieldMovieSet.setText( currentMovie.getSet().getSet() );
		moviePlotTextField.setText( currentMovie.getPlot().getPlot() );
		
		//select first Title
		if ( comboBoxMovieTitleText.getItemCount() > 0 )
			comboBoxMovieTitleText.setSelectedIndex(0);
		
		//Actors and Genres are automatically generated
		actorList.updateUI();
		genreList.updateUI();
		comboBoxMovieTitleText.updateUI();
		
		artWorkPanel.updateView(forcePosterUpdate, gui);
	}

	public Movie getCurrentMovie() {
		return currentMovie;
	}
	public Movie getEmptyMovie() {

		ArrayList<Actor> actors = new ArrayList<Actor>();
		ArrayList<Director> directors = new ArrayList<>();
		ArrayList<Genre> genres = new ArrayList<Genre>();
		
		Thumb[] fanart = new Thumb[0]; 
		Thumb[] extraFanart = new Thumb[0]; 
		Thumb[] posters = new Thumb[0]; 
		
		ID id = new ID("");
		MPAARating mpaa = new MPAARating("");
		OriginalTitle originalTitle = OriginalTitle.BLANK_ORIGINALTITLE;
		Outline outline = Outline.BLANK_OUTLINE;
		Plot plot = Plot.BLANK_PLOT;
		Rating rating = Rating.BLANK_RATING;
		Runtime runtime = Runtime.BLANK_RUNTIME;
		Set set = Set.BLANK_SET;
		SortTitle sortTitle= SortTitle.BLANK_SORTTITLE;
		Studio studio = Studio.BLANK_STUDIO;
		Tagline tagline = Tagline.BLANK_TAGLINE;
		Title title = new Title("");
		Top250 top250 = Top250.BLANK_TOP250;
		Trailer trailer = new Trailer(null);
		Votes votes = Votes.BLANK_VOTES;
		Year year = Year.BLANK_YEAR;
		
		return new Movie(actors, directors, fanart, extraFanart, genres, id, mpaa, originalTitle, outline, plot, posters, rating, runtime, set, sortTitle, studio, tagline, title, top250, trailer, votes, year);
	}
	
	public void setTitleEditable(boolean value) {
		comboBoxMovieTitleText.setEditable(value);
	}
	
	public ArtWorkPanel getArtWorkPanel() {
		return artWorkPanel;
	}
	
	class GenreItemListModel extends AbstractListModel<Genre> {
		
		private static final long serialVersionUID = 973741706455659871L;

		@Override
		public Genre getElementAt(int index) {
			Genre genre = currentMovie.getGenres().get(index);
			return (genre != null) ? genre : new Genre("");
		}
		
		@Override
		public int getSize() {
			return currentMovie.getGenres().size();
		}
	}
	
	class ActorItemListModel extends AbstractListModel<Actor> {

		private static final long serialVersionUID = 276453659002862686L;

		@Override
		public Actor getElementAt(int index) {
			return currentMovie.getActors().get(index);
		}

		@Override
		public int getSize() {
			return currentMovie.getActors().size();
		}
		
	}
	
	class TitleListModel extends DefaultComboBoxModel<String> {

		private static final long serialVersionUID = -8954125792857066062L;

		@Override
		public int getSize() {
			return currentMovie.getAllTitles().size();
		}

		@Override
		public String getElementAt(int index) {
			return currentMovie.getAllTitles().get(index).getTitle();
		}
		
	}

	public JList<Actor> getActorList() {
		return actorList;
	}

	public void setActorList(JList<Actor> actorList) {
		this.actorList = actorList;
	}

	public JList<Genre> getGenreList() {
		return genreList;
	}

	public void setGenreList(JList<Genre> genreList) {
		this.genreList = genreList;
	}

	public void setCurrentMovie(Movie currentMovie) {
		this.currentMovie = currentMovie;
	}
	
	public void hideArtworkPanel()
	{
		artWorkPanel.setVisible(false);
	}
	
}

