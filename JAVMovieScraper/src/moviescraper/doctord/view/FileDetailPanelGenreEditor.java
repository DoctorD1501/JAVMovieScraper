package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import moviescraper.doctord.controller.UtilityFunctions;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class FileDetailPanelGenreEditor extends AbstractFileDetailPanelEditGUI{
	
	public FileDetailPanelGenreEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}

	private JTextField textFieldGenre;
	
	
	
	/** Panel displayed in the genre editor */
	private JPanel initializeInnerFrame(Genre genreToEdit)
	{
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
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		innerPanel.add(textFieldGenre, gbc_textField);
		textFieldGenre.setColumns(10);
		
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

	@Override
	public void addAction() throws Exception {
		String genre = textFieldGenre.getText();
		Movie currentMovie = fileDetailPanel.getCurrentMovie();
		if (currentMovie != null) {
			currentMovie.getGenres().add(new Genre(genre));
			fileDetailPanel.updateView(false, false);
		}
	}

	@Override
	public void deleteAction() {
		Genre genreToRemove = fileDetailPanel.getGenreList().getSelectedValue();
		fileDetailPanel.getCurrentMovie().getGenres().remove(genreToRemove);
		fileDetailPanel.updateView(false, false);
		
	}

	@Override
	public void editAction() {
		Genre genreToEdit = fileDetailPanel.getGenreList().getSelectedValue();
		genreToEdit.setGenre(textFieldGenre.getText());
		fileDetailPanel.updateView(false, false);
		
	}
	
}