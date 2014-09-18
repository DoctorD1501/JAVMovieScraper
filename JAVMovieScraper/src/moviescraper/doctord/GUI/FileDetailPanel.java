package moviescraper.doctord.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import moviescraper.doctord.Movie;
import moviescraper.doctord.GUI.renderer.ActressListRenderer;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.preferences.MoviescraperPreferences;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class FileDetailPanel extends JPanel {

	private static final long serialVersionUID = 7088761619568387476L;
	
	private JComboBox<String> comboBoxMovieTitleText;
	private JLabel lblOriginalTitleTextSite1;
	private JLabel lblScrapedYearText;
	private JLabel lblIDCurrentMovie;
	private JTextField txtFieldStudio;
	private JTextField txtFieldMovieSet;
	private JTextArea moviePlotTextField;
	private JList<Actor> actorListSite1;
	private JList<String> genreListSite1;
	
	protected Movie currentMovie;

	private MoviescraperPreferences preferences;

	/**
	 * Create the panel.
	 */
	public FileDetailPanel(MoviescraperPreferences preferences) {
		this.preferences = preferences;
		
		JPanel fileDetailsPanel = this;
		fileDetailsPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		FormLayout formLayout = new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, // 1 - empty space
				FormFactory.DEFAULT_COLSPEC, //2 - label for each of the form items
				FormFactory.RELATED_GAP_COLSPEC,//3 - empty space
				ColumnSpec.decode("default:grow"), // 4 - Form text items
				FormFactory.RELATED_GAP_COLSPEC,//5 - empty space
				FormFactory.DEFAULT_COLSPEC},// 6 - artwork panel
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



		String [] initialTitleComboBox = {""};
		
		//using this workaround for JComboBox constructor for problem with generics in WindowBuilder as per this stackoverflow thread: https://stackoverflow.com/questions/8845139/jcombobox-warning-preventing-opening-the-design-page-in-eclipse
		comboBoxMovieTitleText = new JComboBox<String>();
		comboBoxMovieTitleText.setModel(new DefaultComboBoxModel<String>(initialTitleComboBox));
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
		fileDetailsPanel.add(comboBoxMovieTitleText, "4, 2");



		JLabel lblOriginalTitle = new JLabel("Original Title:");
		fileDetailsPanel.add(lblOriginalTitle, "2, 4");



		lblOriginalTitleTextSite1 = new JLabel("");
		fileDetailsPanel.add(lblOriginalTitleTextSite1, "4, 4");


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

		actorListSite1 = new JList<Actor>(new ActorItemListModel());
		actorListSite1.setCellRenderer(new ActressListRenderer());
		actorListSite1.setComponentPopupMenu(new FileDetailPanelPopup(new FileDetailPanelActorAdder(this)));


		JScrollPane actorListScroller = new JScrollPane(actorListSite1);
		actorListScroller.setPreferredSize(new Dimension(250, 250));
		actorListSite1.setSize(new Dimension(250, 250));
		fileDetailsPanel.add(actorListScroller, "4, 16");

		JLabel lblGenres = new JLabel("Genres:");
		fileDetailsPanel.add(lblGenres, "2, 18");

		genreListSite1 = new JList<String>(new GenreItemListModel());
		JScrollPane listScrollerGenres = new JScrollPane(genreListSite1);
		genreListSite1.setComponentPopupMenu(new FileDetailPanelPopup(new FileDetailPanelGenreAdder(this)));
		
		genreListSite1.setSize(new Dimension(200, 200));
		fileDetailsPanel.add(listScrollerGenres, "4, 18");
		
	}
	
	//Sets a new movie and updates a view
	public void setNewMovie(Movie newMovie) {
		this.currentMovie = newMovie;
		updateView();
	}

	//Updates the view for the current movie
	public void updateView() {
		String[] titles = {currentMovie.getTitle().getTitle()};
		comboBoxMovieTitleText.setModel(new DefaultComboBoxModel<String>( titles ));
		lblOriginalTitleTextSite1.setText( currentMovie.getOriginalTitle().getOriginalTitle() );
		lblScrapedYearText.setText( currentMovie.getYear().getYear() );
		lblIDCurrentMovie.setText( currentMovie.getId().getId() );
		txtFieldStudio.setText( currentMovie.getStudio().getStudio() );
		txtFieldMovieSet.setText( currentMovie.getSet().getSet() );
		moviePlotTextField.setText( currentMovie.getPlot().getPlot() );
		
		//Actors and Genres are automatically generated
		actorListSite1.updateUI();
		genreListSite1.updateUI();
		comboBoxMovieTitleText.updateUI();
	}

	public Movie getCurrentMovie() {
		return currentMovie;
	}
	
	class GenreItemListModel extends AbstractListModel<String> {
		
		private static final long serialVersionUID = 973741706455659871L;

		@Override
		public String getElementAt(int index) {
			Genre genre = currentMovie.getGenres().get(index);
			return (genre != null) ? genre.getGenre() : "";
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
}

