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
import moviescraper.doctord.model.dataitem.Tag;



public class TagEditorPanel extends JPanel implements ItemListener {
	
	private static final long serialVersionUID = 1L;
	private JTextField textFieldTag;
	private ArrayList<JCheckBox> tagCheckBoxes;
	private List<Tag> originalTagList; //original values in case we hit cancel
	private List<Tag> editedTagList; //edited values which become active when we hit ok
	
	private static final int DEFAULT_GENRE_LENGTH = 15;
	
	@SuppressWarnings("unchecked")
	public TagEditorPanel(final List<Tag> tagList){
		super(new BorderLayout());
		this.originalTagList = tagList;
		editedTagList = (List<Tag>) UtilityFunctions.cloneObject(originalTagList);
	
		super.setMaximumSize(new Dimension(600,400));
		final JPanel currentMovieTagsPanel = new JPanel(new ModifiedFlowLayout());
		
		Border panelBorder = BorderFactory.createEtchedBorder();
		JPanel favoriteMovieTagsPanel = new JPanel();
		favoriteMovieTagsPanel.setLayout(new BoxLayout(favoriteMovieTagsPanel, BoxLayout.Y_AXIS));
		JPanel enterANewTagPanel = new JPanel();
		//enterANewTagPanel.setBorder(blackline);
		

		//enter a new tag panel
		JLabel lblTag = new JLabel("Add Tag to Current Movie: ");
		JButton addNewTagButton = new JButton("Add");
		
		//Adds a new checkbox, in the checked state. Also adds this to the list of tags
		ActionListener addTagActionListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(textFieldTag != null && 
						textFieldTag.getText()!= null 
						&& textFieldTag.getText().length() > 0 && editedTagList != null){
					Tag tagToAdd = new Tag(textFieldTag.getText());
					if(!editedTagList.contains(tagToAdd))
					{
						editedTagList.add(tagToAdd);
						addTagCheckBoxToPanel(currentMovieTagsPanel, tagToAdd, true);
						if(textFieldTag != null)
						{
							textFieldTag.setText("");
							textFieldTag.requestFocus();
						}
					}
				}
			}
		};
		
		addNewTagButton.addActionListener(addTagActionListener);
		
		enterANewTagPanel.add(lblTag);
		textFieldTag = new JTextField(DEFAULT_GENRE_LENGTH);
		textFieldTag.addActionListener(addTagActionListener);
		enterANewTagPanel.add(textFieldTag);
		enterANewTagPanel.add(addNewTagButton);
		
		//existing tags panel
		tagCheckBoxes = new ArrayList<>(editedTagList.size());
		//get all the labels of the check boxes set up
		for(Tag currentTagToAdd : editedTagList)
		{
			addTagCheckBoxToPanel(currentMovieTagsPanel, currentTagToAdd, true);
		}
		
		
		//favorite tags panel
		JLabel favoriteLabel = new JLabel("Favorites");
		ArrayList<Tag> existingFavoriteTagsArray = FavoriteTagPickerPanel.getFavoriteTagsFromPreferences();
		final JPanel quickTagsCheckboxPanel = new JPanel();
		quickTagsCheckboxPanel.setLayout(new BoxLayout(quickTagsCheckboxPanel, BoxLayout.Y_AXIS));
		for(Tag existingFavoriteTags : existingFavoriteTagsArray)
		{
			addTagCheckBoxToPanel(quickTagsCheckboxPanel, existingFavoriteTags, false);
		}
		quickTagsCheckboxPanel.add(Box.createVerticalGlue());
		JScrollPane quickTagsCheckboxPanelScrollPane = new JScrollPane(quickTagsCheckboxPanel);
		quickTagsCheckboxPanelScrollPane.setBorder(BorderFactory.createEmptyBorder());
		//quickTagsCheckboxPanel.setBorder(BorderFactory.createEmptyBorder());
		quickTagsCheckboxPanelScrollPane.setPreferredSize(new Dimension(240, 310));
		
		favoriteMovieTagsPanel.add(favoriteLabel);
		favoriteMovieTagsPanel.add(quickTagsCheckboxPanelScrollPane);
		favoriteMovieTagsPanel.add(Box.createVerticalGlue());
		
		
		//scroll panes
		final JScrollPane currentMovieTagsPanelScrollPane = new JScrollPane(currentMovieTagsPanel);
		//currentMovieTagsPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		currentMovieTagsPanelScrollPane.setBorder(BorderFactory.createEmptyBorder());
		currentMovieTagsPanelScrollPane.setPreferredSize(new Dimension(400, 310));
		
		JPanel westPanel = new JPanel(new BorderLayout());
		westPanel.setBorder(panelBorder);
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.setBorder(panelBorder);
		
		
		
		westPanel.add(enterANewTagPanel, BorderLayout.NORTH);
		
		westPanel.add(currentMovieTagsPanelScrollPane, BorderLayout.CENTER);
		eastPanel.add(favoriteMovieTagsPanel);
		add(westPanel, BorderLayout.WEST);
		add(eastPanel, BorderLayout.EAST);
		
	}
	
	private void addTagCheckBoxToPanel(JPanel panel, Tag tag, boolean initialStatus)
	{
		JCheckBox checkBoxOfTag = new JCheckBox(tag.getTag());
		checkBoxOfTag.setSelected(initialStatus);
		checkBoxOfTag.addItemListener(this);
		panel.add(checkBoxOfTag);
		panel.revalidate();
	}
	
	//What happens when a checkbox in the tag editor is checked or unchecked
	@Override
	public void itemStateChanged(ItemEvent event) {
		if(tagCheckBoxes != null && editedTagList != null)
		{
			JCheckBox eventItem = (JCheckBox) event.getItem();
			Tag tagNameToEdit = new Tag(eventItem.getText());
			if(event.getStateChange() == ItemEvent.SELECTED)
			{
				if(!editedTagList.contains(tagNameToEdit))
				{
					editedTagList.add(tagNameToEdit);
				}
			}
			else if(event.getStateChange() == ItemEvent.DESELECTED)
			{
				editedTagList.remove(tagNameToEdit);
			}
		}
	}
	
	/**
	 * Finalize changes made
	 */
	public void save()
	{
		originalTagList.clear();
		originalTagList.addAll(editedTagList);
	}
}
