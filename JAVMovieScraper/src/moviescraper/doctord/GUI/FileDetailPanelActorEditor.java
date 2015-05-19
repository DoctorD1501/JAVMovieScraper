package moviescraper.doctord.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.MalformedURLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import moviescraper.doctord.Movie;
import moviescraper.doctord.GUI.AbstractFileDetailPanelEditGUI.Operation;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Thumb;

public class FileDetailPanelActorEditor extends AbstractFileDetailPanelEditGUI {

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
				actorToInitializeFieldsWith.getThumb().getThumbURL() != null &&
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