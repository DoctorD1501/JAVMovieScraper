package moviescraper.doctord.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.MalformedURLException;

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
	
	protected enum Operation{
		ADD, EDIT, DELETE
	}
	
	protected void showOptionDialog(JPanel panel, String title, Operation operation) {
		int result = JOptionPane.showOptionDialog(null, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null);
		if ( result == JOptionPane.OK_OPTION )
			try {
				switch(operation)
				{
				case ADD:
					addAction();
					break;
				case DELETE:
					//do nothing for now, since we aren't using a form to delete items
					break;
				case EDIT:
					editAction();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO sansibar better error detection instead of try-catch
				e.printStackTrace();
			}
	}
	
	
	public abstract String getMenuItemName();
	public abstract void showGUI(Operation operation);
	public abstract void addAction() throws Exception;
	public abstract void deleteAction();
	public abstract void editAction();
}

class FileDetailPanelActorEditor extends AbstractFileDetailPanelEditGUI {

	private JTextField textFieldActor;
	private JTextField textFieldActorRole;
	private JTextField textFieldURL;
	
	public FileDetailPanelActorEditor(FileDetailPanel fileDetailPanel) {
		super(fileDetailPanel);
	}
	
	private JPanel initializeInnerFrame(){
		return initializeInnerFrame(new Actor("", "", null));
	}
	
	private JPanel initializeInnerFrame(Actor actorToInitializeFieldsWith) {
		JPanel innerPanel = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		innerPanel.setLayout(gridBagLayout);
		
		JLabel lblActor = new JLabel("Actor Name:");
		GridBagConstraints gbc_lblActor = new GridBagConstraints();
		gbc_lblActor.anchor = GridBagConstraints.EAST;
		gbc_lblActor.insets = new Insets(0, 0, 5, 5);
		gbc_lblActor.gridx = 0;
		gbc_lblActor.gridy = 0;
		innerPanel.add(lblActor, gbc_lblActor);
		
		textFieldActor = new JTextField();
		if(actorToInitializeFieldsWith.getName() != null && 
				actorToInitializeFieldsWith.getName().length() > 0)
		{
			textFieldActor.setText(actorToInitializeFieldsWith.getName());
		}
		GridBagConstraints gbc_textFieldActor = new GridBagConstraints();
		gbc_textFieldActor.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldActor.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldActor.gridx = 1;
		gbc_textFieldActor.gridy = 0;
		innerPanel.add(textFieldActor, gbc_textFieldActor);
		textFieldActor.setColumns(10);
		
		JLabel lblActorRole = new JLabel("Actor Role:");
		GridBagConstraints gbc_lblActorRole = new GridBagConstraints();
		gbc_lblActorRole.anchor = GridBagConstraints.EAST;
		gbc_lblActorRole.insets = new Insets(0, 0, 5, 5);
		gbc_lblActorRole.gridx = 0;
		gbc_lblActorRole.gridy = 1;
		innerPanel.add(lblActorRole, gbc_lblActorRole);
		
		textFieldActorRole = new JTextField();
		if(actorToInitializeFieldsWith.getRole() != null && 
				actorToInitializeFieldsWith.getRole().length() > 0)
		{
			textFieldActorRole.setText(actorToInitializeFieldsWith.getRole());
		}
		GridBagConstraints gbc_textFieldActorRole = new GridBagConstraints();
		gbc_textFieldActorRole.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldActorRole.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldActorRole.gridx = 1;
		gbc_textFieldActorRole.gridy = 1;
		innerPanel.add(textFieldActorRole, gbc_textFieldActorRole);
		textFieldActor.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("URL :");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		innerPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		textFieldURL = new JTextField();
		if(actorToInitializeFieldsWith.getThumb() != null && 
				actorToInitializeFieldsWith.getThumb().getThumbURL().toString().length() > 0)
		{
			textFieldURL.setText(actorToInitializeFieldsWith.getThumb().getThumbURL().toString());
		}
		GridBagConstraints gbc_textFieldURL = new GridBagConstraints();
		gbc_textFieldURL.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldURL.gridx = 1;
		gbc_textFieldURL.gridy = 2;
		innerPanel.add(textFieldURL, gbc_textFieldURL);
		textFieldURL.setColumns(10);
		
		return innerPanel;
	}
	
	@Override
	public void showGUI(Operation operation) {
		if(operation == Operation.ADD)
			showOptionDialog(initializeInnerFrame(), "Manually Add New Actor", operation);
		else if(operation == Operation.EDIT)
			showOptionDialog(initializeInnerFrame(fileDetailPanel.getActorList().getSelectedValue()), "Edit Actor", operation);
	}
	
	@Override
	public void addAction() throws Exception{
		Actor newActor;
		Thumb newThumb;
		String actorName = textFieldActor.getText();
		String actorRole = textFieldActorRole.getText();
		String actorURL = textFieldURL.getText();
		if (actorURL == null || actorURL.isEmpty() ) {
			newThumb = null;
		} else {
			newThumb = new Thumb(actorURL);
		}
		if(actorRole != null && actorRole.length() > 0)
			newActor = new Actor(actorName, actorRole, newThumb);
		else newActor = new Actor(actorName, null, newThumb);
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

	@Override
	public void editAction() {
		Actor actorToEdit = fileDetailPanel.getActorList().getSelectedValue();
		if(actorToEdit != null)
		{
			String newActorName = textFieldActor.getText();
			String newActorRole = textFieldActorRole.getText();
			String newActorURL = textFieldURL.getText();

			if(newActorName != null && newActorName.length() > 0){
				actorToEdit.setName(newActorName);
			}

			if(newActorRole != null && newActorRole.length() > 0)
			{
				actorToEdit.setRole(newActorRole);
			}
			
			if(newActorURL != null && newActorURL.length() > 0)
			{
				try {
					actorToEdit.setThumb(new Thumb(newActorURL));
					actorToEdit.setThumbEdited(true);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			
			fileDetailPanel.updateView(false, true);
		}
		
	}
}

class FileDetailPanelGenreEditor extends AbstractFileDetailPanelEditGUI {
	
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
