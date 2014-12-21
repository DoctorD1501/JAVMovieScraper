package moviescraper.doctord.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moviescraper.doctord.Movie;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Genre;

abstract class AbstractFileDetailPanelEditGUI {

	protected FileDetailPanel fileDetailPanel;
	protected AbstractFileDetailPanelEditGUI( FileDetailPanel fileDetailPanel ) {
		this.fileDetailPanel = fileDetailPanel;
	}
	
	protected void showOptionDialog(JPanel panel, String title) {
		int result = JOptionPane.showOptionDialog(null, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null);
		if ( result == JOptionPane.OK_OPTION )
			try {
				addAction();
			} catch (Exception e) {
				// TODO sansibar better error detection instead of try-catch
				e.printStackTrace();
			}
	}
	
	public abstract String getMenuItemName();
	public abstract void showGUI();
	public abstract void addAction() throws Exception;
	public abstract void deleteAction();
}

class FileDetailPanelActorEditor extends AbstractFileDetailPanelEditGUI {

	private JTextField textFieldActor;
	private JTextField textFieldURL;
	
	public FileDetailPanelActorEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}
	
	private JPanel initializeInnerFrame() {
		JPanel innerPanel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		innerPanel.setLayout(gridBagLayout);
		
		JLabel lblActor = new JLabel("Actor :");
		GridBagConstraints gbc_lblActor = new GridBagConstraints();
		gbc_lblActor.anchor = GridBagConstraints.EAST;
		gbc_lblActor.insets = new Insets(0, 0, 5, 5);
		gbc_lblActor.gridx = 0;
		gbc_lblActor.gridy = 0;
		innerPanel.add(lblActor, gbc_lblActor);
		
		textFieldActor = new JTextField();
		GridBagConstraints gbc_textFieldActor = new GridBagConstraints();
		gbc_textFieldActor.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldActor.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldActor.gridx = 1;
		gbc_textFieldActor.gridy = 0;
		innerPanel.add(textFieldActor, gbc_textFieldActor);
		textFieldActor.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("URL :");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		innerPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		textFieldURL = new JTextField();
		GridBagConstraints gbc_textFieldURL = new GridBagConstraints();
		gbc_textFieldURL.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldURL.gridx = 1;
		gbc_textFieldURL.gridy = 1;
		innerPanel.add(textFieldURL, gbc_textFieldURL);
		textFieldURL.setColumns(10);
		
		return innerPanel;
	}
	
	@Override
	public void showGUI() {
		showOptionDialog(initializeInnerFrame(), "Manually Add New Actor");
	}
	
	@Override
	public void addAction() throws Exception{
		Actor newActor;
		Thumb newThumb;
		String actorName = textFieldActor.getText();
		String actorURL = textFieldURL.getText();
		if (actorURL == null || actorURL.isEmpty() ) {
			newThumb = null;
		} else {
			newThumb = new Thumb(actorURL);
		}
		newActor = new Actor(actorName, null, newThumb);
		Movie currentMovie = fileDetailPanel.getCurrentMovie();
		if (currentMovie != null) {
			currentMovie.getActors().add(newActor);
			fileDetailPanel.updateView(true, true);
		}
	}
	
	@Override
	public String getMenuItemName() {
		return "Add New Actor";
	}

	@Override
	public void deleteAction() {
		Actor actorToRemove = fileDetailPanel.getActorList().getSelectedValue();
		fileDetailPanel.getCurrentMovie().getActors().remove(actorToRemove);
		fileDetailPanel.updateView(false, false);
	}
}

class FileDetailPanelGenreEditor extends AbstractFileDetailPanelEditGUI {
	
	public FileDetailPanelGenreEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}

	private JTextField textFieldGenre;

	private JPanel initializeInnerFrame() {
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
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		innerPanel.add(textFieldGenre, gbc_textField);
		textFieldGenre.setColumns(10);
		
		return innerPanel;
	}

	@Override
	public String getMenuItemName() {
		return "Add New Genre";
	}

	@Override
	public void showGUI() {
		showOptionDialog(initializeInnerFrame(), "Add manually new Genre");
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
		String genreToRemove = fileDetailPanel.getGenreList().getSelectedValue();
		System.out.println("want to remove " + genreToRemove);
		fileDetailPanel.getCurrentMovie().getGenres().remove(new Genre(genreToRemove));
		fileDetailPanel.updateView(false, false);
		
	}
}
