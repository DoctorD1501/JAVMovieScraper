package moviescraper.doctord.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.GlobalGenreList;

public class FileDetailPanelGenreEditor extends AbstractFileDetailPanelEditGUI {
	
	public FileDetailPanelGenreEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}

	private JTextField textFieldGenre;
	
//	private JPanel initializeInnerFrame(Genre genreToEdit)
//	{
//		JPanel innerPanel = new JPanel();
//		GridBagLayout gridBagLayout = new GridBagLayout();
//		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
//		innerPanel.setLayout(gridBagLayout);
//		
//		JLabel lblGenre = new JLabel("Genre :");
//		GridBagConstraints gbc_lblGenre = new GridBagConstraints();
//		gbc_lblGenre.insets = new Insets(0, 0, 0, 5);
//		gbc_lblGenre.anchor = GridBagConstraints.EAST;
//		gbc_lblGenre.gridx = 0;
//		gbc_lblGenre.gridy = 0;
//		innerPanel.add(lblGenre, gbc_lblGenre);
//		
//		textFieldGenre = new JTextField();
//		if(genreToEdit != null && genreToEdit.getGenre().length() >0)
//			textFieldGenre.setText(genreToEdit.getGenre());
//		GridBagConstraints gbc_textField = new GridBagConstraints();
//		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
//		gbc_textField.gridx = 1;
//		gbc_textField.gridy = 0;
//		innerPanel.add(textFieldGenre, gbc_textField);
//		textFieldGenre.setColumns(10);
//		
//		return innerPanel;
//	}
	private JPanel initializeInnerFrame(Genre genreToEdit)
	{

// ======== Genrelist - copy down for now ====================		
		
		JPanel innerPanel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
	
		innerPanel.setLayout(gridBagLayout);
		
		JLabel lblGenre = new JLabel("Genre :");
		GridBagConstraints gbc_lblGenre = new GridBagConstraints();
		gbc_lblGenre.insets = new Insets(0, 0, 0, 5);
		gbc_lblGenre.anchor = GridBagConstraints.EAST;
		gbc_lblGenre.gridx = 0;
		gbc_lblGenre.gridy = 0;
		innerPanel.add(lblGenre, gbc_lblGenre);
		
		
		textFieldGenre = new JTextField();
		if(genreToEdit != null && genreToEdit.getGenre().length() >0)
			textFieldGenre.setText(genreToEdit.getGenre());
		
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.REMAINDER;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		// madaustrian
		gbc_textField.gridwidth = 5;
		innerPanel.add(textFieldGenre, gbc_textField);
//		textFieldGenre.setColumns(60);

// madaustrian - added genre checkboxes
		textFieldGenre.setColumns(60);
		
		GridBagConstraints gbc_check = new GridBagConstraints();
		gbc_check.fill = GridBagConstraints.HORIZONTAL;
		
		String testgenretext;
		
		GlobalGenreList.RealNumberOfGenreItems_1 = 0;
		for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_1; i++){
			testgenretext = GlobalGenreList.GenreList_1[i];
			char firstCharacter = testgenretext.charAt(0);
		    char secondCharacter = testgenretext.charAt(1);
			if (firstCharacter != '@' && secondCharacter != '-') {	
				gbc_check.gridx = 1;
				gbc_check.gridy = 2+i;
				GlobalGenreList.GenreButton_1[GlobalGenreList.RealNumberOfGenreItems_1] = new JCheckBox(GlobalGenreList.GenreList_1[i]);
				GlobalGenreList.GenreButton_1[GlobalGenreList.RealNumberOfGenreItems_1].setSelected(false);
				innerPanel.add(GlobalGenreList.GenreButton_1[GlobalGenreList.RealNumberOfGenreItems_1], gbc_check);
				GlobalGenreList.RealNumberOfGenreItems_1++;
			}
		}
		GlobalGenreList.RealNumberOfGenreItems_2 = 0;
		for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_2; i++){
			testgenretext = GlobalGenreList.GenreList_2[i];
			char firstCharacter = testgenretext.charAt(0);
		    char secondCharacter = testgenretext.charAt(1);
			if (firstCharacter != '@' && secondCharacter != '-') {			
				gbc_check.gridx = 2;
				gbc_check.gridy = 2+i;
				GlobalGenreList.GenreButton_2[GlobalGenreList.RealNumberOfGenreItems_2] = new JCheckBox(GlobalGenreList.GenreList_2[i]);
				GlobalGenreList.GenreButton_2[GlobalGenreList.RealNumberOfGenreItems_2].setSelected(false);
				innerPanel.add(GlobalGenreList.GenreButton_2[GlobalGenreList.RealNumberOfGenreItems_2], gbc_check);
				GlobalGenreList.RealNumberOfGenreItems_2++;
			}
		}
		GlobalGenreList.RealNumberOfGenreItems_3 = 0;
		for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_3; i++){
			testgenretext = GlobalGenreList.GenreList_3[i];
			char firstCharacter = testgenretext.charAt(0);
		    char secondCharacter = testgenretext.charAt(1);
			if (firstCharacter != '@' && secondCharacter != '-') {
				gbc_check.gridx = 3;
				gbc_check.gridy = 2+i;
				GlobalGenreList.GenreButton_3[GlobalGenreList.RealNumberOfGenreItems_3] = new JCheckBox(GlobalGenreList.GenreList_3[i]);
				GlobalGenreList.GenreButton_3[GlobalGenreList.RealNumberOfGenreItems_3].setSelected(false);
				innerPanel.add(GlobalGenreList.GenreButton_3[GlobalGenreList.RealNumberOfGenreItems_3], gbc_check);
				GlobalGenreList.RealNumberOfGenreItems_3++;
			}
		}
		GlobalGenreList.RealNumberOfGenreItems_4 = 0;
		for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_4; i++){
			testgenretext = GlobalGenreList.GenreList_4[i];
			char firstCharacter = testgenretext.charAt(0);
		    char secondCharacter = testgenretext.charAt(1);
			if (firstCharacter != '@' && secondCharacter != '-') {
				gbc_check.gridx = 4;
				gbc_check.gridy = 2+i;
				GlobalGenreList.GenreButton_4[GlobalGenreList.RealNumberOfGenreItems_4] = new JCheckBox(GlobalGenreList.GenreList_4[i]);
				GlobalGenreList.GenreButton_4[GlobalGenreList.RealNumberOfGenreItems_4].setSelected(false);
				innerPanel.add(GlobalGenreList.GenreButton_4[GlobalGenreList.RealNumberOfGenreItems_4], gbc_check);
				GlobalGenreList.RealNumberOfGenreItems_4++;
			}
		}
		
		GlobalGenreList.RealNumberOfGenreItems_5 = 0;
		for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_5; i++){
			testgenretext = GlobalGenreList.GenreList_5[i];
			char firstCharacter = testgenretext.charAt(0);
		    char secondCharacter = testgenretext.charAt(1);
//			if (GlobalGenreList.GenreList_5[i] != "@-") {
// for some reason I can not compare the strings in above manner ???		    
			if (firstCharacter != '@' && secondCharacter != '-') {
				gbc_check.gridx = 5;
				gbc_check.gridy = 2+i;
				GlobalGenreList.GenreButton_5[GlobalGenreList.RealNumberOfGenreItems_5] = new JCheckBox(GlobalGenreList.GenreList_5[i]);
				GlobalGenreList.GenreButton_5[GlobalGenreList.RealNumberOfGenreItems_5].setSelected(false);
				innerPanel.add(GlobalGenreList.GenreButton_5[GlobalGenreList.RealNumberOfGenreItems_5], gbc_check);
				GlobalGenreList.RealNumberOfGenreItems_5++;
//				System.out.println("Numberofgenreitems:" + GlobalGenreList.NumberOfGenreItems_5 + "| realnumber:" + GlobalGenreList.RealNumberOfGenreItems_5);
			}
		}
		
		return innerPanel;
	}
	private JPanel initializeInnerFrame() {
		return initializeInnerFrame(new Genre(""));
	}

	@Override
	public String getMenuItemName() {
		return "Add New Genre";
	}

	@Override
	public void showGUI(Operation operation) {
		if(operation == Operation.ADD)
			showOptionDialog(initializeInnerFrame(), "Add New Genre", operation);
		else if(operation == Operation.EDIT)
			showOptionDialog(initializeInnerFrame(fileDetailPanel.getGenreList().getSelectedValue()),"Edit Genre", operation);
	}

//	@Override
//	public void addAction() throws Exception {
//		String genre = textFieldGenre.getText();
//		Movie currentMovie = fileDetailPanel.getCurrentMovie();
//		if (currentMovie != null) {
//			currentMovie.getGenres().add(new Genre(genre));
//			fileDetailPanel.updateView(false, false);
//		}
//	}
	
//	===============================================================================
// madaustrian			
	@Override
	public void addAction() throws Exception {
		String genre = textFieldGenre.getText();

		// madaustrian - genre checkboxes
		// da hier müssen wir die Genres einpassen!
		
		Movie currentMovie = fileDetailPanel.getCurrentMovie();
		if (currentMovie != null) {
			currentMovie.getGenres().add(new Genre(genre));
			
			//for (int i=0 ; i < GlobalGenreList.NumberOfGenreItems_1 ; i++){
			for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_1 ; i++){
				if (GlobalGenreList.GenreButton_1[i].isSelected()==true) {
//					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreList_1[i]));
					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_1[i].getText()));
				}	
			}
			for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_2 ; i++){
				if (GlobalGenreList.GenreButton_2[i].isSelected()==true) {
//					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreList_2[i]));
					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_2[i].getText()));
				}	
			}
			for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_3 ; i++){
				if (GlobalGenreList.GenreButton_3[i].isSelected()==true) {
//					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreList_3[i]));
					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_3[i].getText()));
				}	
			}
			for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_4 ; i++){
				if (GlobalGenreList.GenreButton_4[i].isSelected()==true) {
//					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreList_4[i]));
					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_4[i].getText()));
				}	
			}
			for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_5 ; i++){
				if (GlobalGenreList.GenreButton_5[i].isSelected()==true) {
//					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreList_5[i]));
					currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_5[i].getText()));
				}	
			}

			fileDetailPanel.updateView(false, false);
		}
		
		
	}
	

	@Override
	public void deleteAction() {
		Genre genreToRemove = fileDetailPanel.getGenreList().getSelectedValue();
		fileDetailPanel.getCurrentMovie().getGenres().remove(genreToRemove);
		fileDetailPanel.updateView(false, false);
		
	}

//	@Override
//	public void editAction() {
//		Genre genreToEdit = fileDetailPanel.getGenreList().getSelectedValue();
//		genreToEdit.setGenre(textFieldGenre.getText());
//		fileDetailPanel.updateView(false, false);
//		
//	}
	@Override
	public void editAction() {

		
		Movie currentMovie = fileDetailPanel.getCurrentMovie();

		Genre genreToEdit = fileDetailPanel.getGenreList().getSelectedValue();
		genreToEdit.setGenre(textFieldGenre.getText());
		
			
		for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_1 ; i++){
			if (GlobalGenreList.GenreButton_1[i].isSelected()==true) {
				currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_1[i].getText()));
			}	
		}
		for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_2 ; i++){
			if (GlobalGenreList.GenreButton_2[i].isSelected()==true) {
				currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_2[i].getText()));
			}	
		}
		for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_3 ; i++){
			if (GlobalGenreList.GenreButton_3[i].isSelected()==true) {
				currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_3[i].getText()));
			}	
		}
		for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_4 ; i++){
			if (GlobalGenreList.GenreButton_4[i].isSelected()==true) {
				currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_4[i].getText()));
			}	
		}
		for (int i=0 ; i < GlobalGenreList.RealNumberOfGenreItems_5 ; i++){
			if (GlobalGenreList.GenreButton_5[i].isSelected()==true) {
				currentMovie.getGenres().add(new Genre(GlobalGenreList.GenreButton_5[i].getText()));
			}	
		}
		fileDetailPanel.updateView(false, false);
		
	}	
}