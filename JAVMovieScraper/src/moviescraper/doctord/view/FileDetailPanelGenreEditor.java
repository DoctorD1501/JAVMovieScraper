package moviescraper.doctord.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.Genre;

public class FileDetailPanelGenreEditor extends AbstractFileDetailPanelEditGUI {
	
	public FileDetailPanelGenreEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}

	private JTextField textFieldGenre;
	
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