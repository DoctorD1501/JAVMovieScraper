package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import moviescraper.doctord.controller.UtilityFunctions;
import moviescraper.doctord.model.dataitem.Genre;



public class GenreEditorPanel extends JPanel implements ItemListener {
	
	private static final long serialVersionUID = 1L;
	private JTextField textFieldGenre;
	private ArrayList<JCheckBox> genreCheckBoxes;
	private List<Genre> originalGenreList; //original values in case we hit cancel
	private List<Genre> editedGenreList; //edited values which become active when we hit ok
	
	private static final int DEFAULT_GENRE_LENGTH = 15;
	
	@SuppressWarnings("unchecked")
	public GenreEditorPanel(final List<Genre> genreList){
		super(new BorderLayout());
		this.originalGenreList = genreList;
		editedGenreList = (List<Genre>) UtilityFunctions.cloneObject(originalGenreList);
	
		super.setMaximumSize(new Dimension(600,400));
		final JPanel currentMovieGenresPanel = new JPanel(new ModifiedFlowLayout());
		
		Border panelBorder = BorderFactory.createEtchedBorder();
		JPanel favoriteMovieGenresPanel = new JPanel();
		favoriteMovieGenresPanel.setLayout(new BoxLayout(favoriteMovieGenresPanel, BoxLayout.Y_AXIS));
		JPanel enterANewGenrePanel = new JPanel();
		//enterANewGenrePanel.setBorder(blackline);
		

		//enter a new genre panel
		JLabel lblGenre = new JLabel("Add Genre to Current Movie: ");
		JButton addNewGenreButton = new JButton("Add");
		
		//Adds a new checkbox, in the checked state. Also adds this to the list of genres
		ActionListener addGenreActionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(textFieldGenre != null && 
						textFieldGenre.getText()!= null 
						&& textFieldGenre.getText().length() > 0 && editedGenreList != null){
					Genre genreToAdd = new Genre(textFieldGenre.getText());
					if(!editedGenreList.contains(genreToAdd))
					{
						editedGenreList.add(genreToAdd);
						addGenreCheckBoxToPanel(currentMovieGenresPanel, genreToAdd, true);
						if(textFieldGenre != null)
						{
							textFieldGenre.setText("");
							textFieldGenre.requestFocus();
						}
					}
				}
			}
		};
		
		addNewGenreButton.addActionListener(addGenreActionListener);
		
		enterANewGenrePanel.add(lblGenre);
		textFieldGenre = new JTextField(DEFAULT_GENRE_LENGTH);
		textFieldGenre.addActionListener(addGenreActionListener);
		enterANewGenrePanel.add(textFieldGenre);
		enterANewGenrePanel.add(addNewGenreButton);
		
		//existing genres panel
		genreCheckBoxes = new ArrayList<>(editedGenreList.size());
		//get all the labels of the check boxes set up
		for(Genre currentGenreToAdd : editedGenreList)
		{
			addGenreCheckBoxToPanel(currentMovieGenresPanel, currentGenreToAdd, true);
		}
		
		
		//favorite genres panel
		JLabel favoriteLabel = new JLabel("Favorites");
		ArrayList<Genre> existingFavoriteGenresArray = FavoriteGenrePickerPanel.getFavoriteGenresFromPreferences();
		final JPanel quickGenresCheckboxPanel = new JPanel();
		quickGenresCheckboxPanel.setLayout(new BoxLayout(quickGenresCheckboxPanel, BoxLayout.Y_AXIS));
		for(Genre existingFavoriteGenres : existingFavoriteGenresArray)
		{
			addGenreCheckBoxToPanel(quickGenresCheckboxPanel, existingFavoriteGenres, false);
		}
		quickGenresCheckboxPanel.add(Box.createVerticalGlue());
		JScrollPane quickGenresCheckboxPanelScrollPane = new JScrollPane(quickGenresCheckboxPanel);
		quickGenresCheckboxPanelScrollPane.setBorder(BorderFactory.createEmptyBorder());
		//quickGenresCheckboxPanel.setBorder(BorderFactory.createEmptyBorder());
		quickGenresCheckboxPanelScrollPane.setPreferredSize(new Dimension(240, 310));
		
		favoriteMovieGenresPanel.add(favoriteLabel);
		favoriteMovieGenresPanel.add(quickGenresCheckboxPanelScrollPane);
		favoriteMovieGenresPanel.add(Box.createVerticalGlue());
		
		
		//scroll panes
		final JScrollPane currentMovieGenresPanelScrollPane = new JScrollPane(currentMovieGenresPanel);
		//currentMovieGenresPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		currentMovieGenresPanelScrollPane.setBorder(BorderFactory.createEmptyBorder());
		currentMovieGenresPanelScrollPane.setPreferredSize(new Dimension(400, 310));
		
		JPanel westPanel = new JPanel(new BorderLayout());
		westPanel.setBorder(panelBorder);
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.setBorder(panelBorder);
		
		
		
		westPanel.add(enterANewGenrePanel, BorderLayout.NORTH);
		
		westPanel.add(currentMovieGenresPanelScrollPane, BorderLayout.CENTER);
		eastPanel.add(favoriteMovieGenresPanel);
		add(westPanel, BorderLayout.WEST);
		add(eastPanel, BorderLayout.EAST);
		
	}
	
	private void addGenreCheckBoxToPanel(JPanel panel, Genre genre, boolean initialStatus)
	{
		JCheckBox checkBoxOfGenre = new JCheckBox(genre.getGenre());
		checkBoxOfGenre.setSelected(initialStatus);
		checkBoxOfGenre.addItemListener(this);
		panel.add(checkBoxOfGenre);
		panel.revalidate();
	}
	
	//What happens when a checkbox in the genre editor is checked or unchecked
	@Override
	public void itemStateChanged(ItemEvent event) {
		System.out.println("Checked box with event = " + event);
		if(genreCheckBoxes != null && editedGenreList != null)
		{
			JCheckBox eventItem = (JCheckBox) event.getItem();
			Genre genreNameToEdit = new Genre(eventItem.getText());
			if(event.getStateChange() == ItemEvent.SELECTED)
			{
				if(!editedGenreList.contains(genreNameToEdit))
				{
					System.out.println("Adding " + genreNameToEdit);
					editedGenreList.add(genreNameToEdit);
				}
			}
			else if(event.getStateChange() == ItemEvent.DESELECTED)
			{
				System.out.println("editedgenrelist before + " + editedGenreList);
				System.out.println("Removing " + genreNameToEdit);
				editedGenreList.remove(genreNameToEdit);
			}
		}
	}
	
	/**
	 * Finalize changes made
	 */
	public void save()
	{
		System.out.println("orig genreList in save: " + originalGenreList);
		System.out.println("edit genreList in save: " + editedGenreList);
		originalGenreList.clear();
		originalGenreList.addAll(editedGenreList);
		System.out.println("orig genreList in save2: " + originalGenreList);
	}
}
