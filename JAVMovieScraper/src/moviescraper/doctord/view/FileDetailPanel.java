package moviescraper.doctord.view;

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
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import moviescraper.doctord.controller.EditGenresAction;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Year;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.AbstractFileDetailPanelEditGUI.Operation;
import moviescraper.doctord.view.renderer.ActressListRenderer;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileDetailPanel extends JPanel {

	private static final long serialVersionUID = 7088761619568387476L;
	
	private JComboBox<String> comboBoxMovieTitleText;
	private JTextField txtFieldOriginalTitleText;
	private JTextField txtFieldScrapedYearText;
	private JTextField txtFieldIDCurrentMovie;
	private JTextField txtFieldStudio;
	private JTextField txtFieldMovieSet;
	private JTextArea moviePlotTextField;
	private JList<Actor> actorList;
	private JTextField genreList;
	
	protected Movie currentMovie = getEmptyMovie();
	MoviescraperPreferences preferences;

	private ArtWorkPanel artWorkPanel;
	private final static int DEFAULT_TEXTFIELD_LENGTH = 35;
	
	public GUIMain gui;

	private JTextField txtFieldReleaseDateText;

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
				FormFactory.DEFAULT_ROWSPEC,//8 - Release Date
				FormFactory.RELATED_GAP_ROWSPEC,//8 - empty space
				FormFactory.DEFAULT_ROWSPEC,//9 - ID
				FormFactory.RELATED_GAP_ROWSPEC,//10 - empty space
				FormFactory.DEFAULT_ROWSPEC,//11 - Studio
				FormFactory.RELATED_GAP_ROWSPEC,//12 - empty space
				FormFactory.DEFAULT_ROWSPEC,//13 - Movie set
				FormFactory.RELATED_GAP_ROWSPEC,//14 - empty space
				FormFactory.DEFAULT_ROWSPEC,//15 - Plot
				FormFactory.RELATED_GAP_ROWSPEC,//16 - empty space
				RowSpec.decode("default:grow"),//17 - actors
				FormFactory.RELATED_GAP_ROWSPEC,//18 - empty space
				RowSpec.decode("default:grow"),//19 - genres
				FormFactory.RELATED_GAP_ROWSPEC//20 - empty space
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



		txtFieldOriginalTitleText = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldOriginalTitleText.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldOriginalTitleText.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setOriginalTitle(new OriginalTitle(newValue));
	            	}
	            }
	        }
	    });
		txtFieldOriginalTitleText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldOriginalTitleText.getText();
				if(newValue != null)
				{
					currentMovie.setOriginalTitle(new OriginalTitle(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldOriginalTitleText, "4, 4");


		JLabel lblYear = new JLabel("Year:");
		fileDetailsPanel.add(lblYear, "2, 6");
		

		txtFieldScrapedYearText = new JTextField("",4);
		fileDetailsPanel.add(txtFieldScrapedYearText, "4, 6");
		txtFieldScrapedYearText.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldScrapedYearText.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setYear(new Year(newValue));
	            	}
	            }
	        }
	    });
		txtFieldScrapedYearText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldScrapedYearText.getText();
				if(newValue != null)
				{
					currentMovie.setYear(new Year(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		
		
		JLabel lblReleaseDate = new JLabel("Release Date:");
		fileDetailsPanel.add(lblReleaseDate, "2, 8");
		
		txtFieldReleaseDateText = new JTextField("",12);
		txtFieldReleaseDateText.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldReleaseDateText.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setReleaseDate(new ReleaseDate(newValue));
	            	}
	            }
	        }
	    });
		txtFieldReleaseDateText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldReleaseDateText.getText();
				if(newValue != null)
				{
					currentMovie.setReleaseDate(new ReleaseDate(newValue));
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

		});
		fileDetailsPanel.add(txtFieldReleaseDateText, "4, 8");
		
		JLabel lblID = new JLabel("ID:");
		fileDetailsPanel.add(lblID, "2, 10");
		
		txtFieldIDCurrentMovie = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		txtFieldIDCurrentMovie.addActionListener(new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            
	            if(currentMovie != null)
	            {
	            	String newValue = (String) txtFieldIDCurrentMovie.getText();
	            	if(newValue != null)
	            	{
	            		currentMovie.setId(new ID(newValue));
	            	}
	            }
	        }
	    });
		txtFieldIDCurrentMovie.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				String newValue = (String) txtFieldIDCurrentMovie.getText();
            	if(newValue != null)
            	{
            		currentMovie.setId(new ID(newValue));
            	}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			
		});
		fileDetailsPanel.add(txtFieldIDCurrentMovie,"4, 10");
		
		JLabel lblStudio = new JLabel("Studio:");
		txtFieldStudio = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
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
		
		fileDetailsPanel.add(lblStudio,"2,12");
		fileDetailsPanel.add(txtFieldStudio,"4,12");
		
		JLabel lblSet = new JLabel("Movie Set:");
		fileDetailsPanel.add(lblSet,"2, 14");
		txtFieldMovieSet = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
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
		fileDetailsPanel.add(txtFieldMovieSet,"4,14");
		
		JLabel lblPlot = new JLabel("Plot:");
		fileDetailsPanel.add(lblPlot, "2,16");
		

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

		fileDetailsPanel.add(plotPanelScrollPane,"4,16");
		

		JLabel lblActors = new JLabel("Actors:");
		fileDetailsPanel.add(lblActors, "2, 18");

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
		     @Override
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
		
		fileDetailsPanel.add(actorListScroller, "4, 18");

		JLabel lblGenres = new JLabel("Genres:");
		fileDetailsPanel.add(lblGenres, "2, 20");

		genreList = new JTextField("", DEFAULT_TEXTFIELD_LENGTH);
		//the user clicks the field to edit it - we don't want them typing directly here
		genreList.addKeyListener(new KeyListenerIgnoreTyping());
		genreList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				EditGenresAction editGenresAction = new EditGenresAction(FileDetailPanel.this);
				editGenresAction.actionPerformed(null);
		    }
		});
		fileDetailsPanel.add(genreList, "4,20");
		
		artWorkPanel = new ArtWorkPanel();
		//frmMoviescraper.getContentPane().add(artworkPanelScrollPane, BorderLayout.EAST);
		fileDetailsPanel.add(artWorkPanel,"6,2,1,20");
	}
	/**
	 * Sets a new movie and updates a view
	 * @param newMovie the movie this fileDetailPanel will show
	 * @param forcePosterUpdate whether to force a redownload from the poster defined in the Thumb url
	 */
	
	public void setNewMovie(Movie newMovie, boolean forcePosterUpdate) {
		setNewMovie(newMovie, forcePosterUpdate, false);
	}
	
	/**
	 * Sets a new movie and updates a view
	 * @param newMovie the movie this fileDetailPanel will show
	 * @param forcePosterUpdate whether to force a redownload from the poster defined in the Thumb url
	 * @param modifyWriteToDiskList - whether to modify the gui object's disk list by adding the currently viewed item if the disk list is empty
	 */
	public void setNewMovie(Movie newMovie, boolean forcePosterUpdate, boolean modifyWriteToDiskList) {
		System.out.println("Setting new movie: " + newMovie);
		if(newMovie != null)
		{
			setCurrentMovie(newMovie);
			updateView(forcePosterUpdate, modifyWriteToDiskList);
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
			{
				this.getCurrentMovie().getAllTitles().add(getCurrentMovie().getTitle());
				String fileName = this.getCurrentMovie().getFileName();
				if(fileName != null && fileName.trim().length() > 0)
					this.getCurrentMovie().getAllTitles().add(new Title(fileName));
			}
			
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
		txtFieldOriginalTitleText.setText( currentMovie.getOriginalTitle().getOriginalTitle() );
		txtFieldOriginalTitleText.setCaretPosition(0);
		txtFieldScrapedYearText.setText( currentMovie.getYear().getYear() );
		txtFieldReleaseDateText.setText(currentMovie.getReleaseDate().getReleaseDate());
		txtFieldIDCurrentMovie.setText( currentMovie.getId().getId() );
		txtFieldStudio.setText( currentMovie.getStudio().getStudio() );
		txtFieldStudio.setCaretPosition(0);
		txtFieldMovieSet.setText( currentMovie.getSet().getSet() );
		txtFieldMovieSet.setCaretPosition(0);
		moviePlotTextField.setText( currentMovie.getPlot().getPlot() );
		moviePlotTextField.setCaretPosition(0);
		genreList.setText(toGenreListFormat(currentMovie.getGenres()));
		genreList.setCaretPosition(0);
		
		//select first Title 
		//TODO: for some reason this has the side effect of clearing out the data item source of the title in the movieToWriteToDiskList so I may need to revisit this later
		if ( comboBoxMovieTitleText.getItemCount() > 0 )
		{
			comboBoxMovieTitleText.setSelectedIndex(0);
		}
		
		//Actors and Genres are automatically generated
		actorList.updateUI();
		
		comboBoxMovieTitleText.updateUI();
        ComboBoxEditor editor = comboBoxMovieTitleText.getEditor();
        JTextField textField = (JTextField)editor.getEditorComponent();
        textField.setCaretPosition(0);
		
		artWorkPanel.updateView(forcePosterUpdate, gui);
	}

	public Movie getCurrentMovie() {
		return currentMovie;
	}
	public Movie getEmptyMovie() {
		return Movie.getEmptyMovie();
		
	}
	
	public void setTitleEditable(boolean value) {
		comboBoxMovieTitleText.setEditable(value);
	}
	
	public ArtWorkPanel getArtWorkPanel() {
		return artWorkPanel;
	}
	
	private final class KeyListenerIgnoreTyping implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			e.consume();
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			e.consume();
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			e.consume();
			
		}
	}

	public class GenreItemListModel extends DefaultListModel<Genre> {
		
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

	/*public JList<Genre> getGenreList() {
		return genreList;
	}

	public void setGenreList(JList<Genre> genreList) {
		this.genreList = genreList;
	}*/
	
	public JTextField getGenreList()
	{
		return genreList;
	}
	
	public void setGenreList(JTextField genreList)
	{
		this.genreList = genreList;
	}

	public void setCurrentMovie(Movie currentMovie) {
		this.currentMovie = currentMovie;
	}
	
	public void hideArtworkPanel()
	{
		artWorkPanel.setVisible(false);
	}
	public static String toGenreListFormat(ArrayList<Genre> genres) {
		String genreText = "";
		for(Genre currentGenre : genres)
		{
			genreText += currentGenre.getGenre();
			genreText += " \\ ";
		}
		if(genreText.endsWith(" \\ "))
		{
			genreText = genreText.substring(0, genreText.length()-3);
		}
		return genreText;
	}
	
}

