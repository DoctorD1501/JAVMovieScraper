package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public abstract class FavoriteItemPickerPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	//list
	private JList<String> favoritesList;
	protected DefaultListModel<String> favoritesListModel;
	
	//buttons
	private JButton addButton;
	private JButton removeButton;
	
	//input
	private JTextField newItemTextField;

	public FavoriteItemPickerPanel() {
		super(new BorderLayout());
		
		//set up the input
		newItemTextField = new JTextField(15);
		
		//set up the list
		String[] listValues = getSettingValues();
		favoritesListModel = new DefaultListModel<>();
		favoritesList = new JList<>(favoritesListModel);
		for(String listValue : listValues)
		{
			favoritesListModel.addElement(listValue);
		}
		favoritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane favoritesListScrollPane = new JScrollPane(favoritesList);
		
		//set up the buttons
		addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = favoritesList.getSelectedIndex(); //get selected index
	            if (index == -1) { //no selection, so insert at beginning
	                index = 0;
	            } else {           //add after the selected item
	                index++;
	            }
	 
	            favoritesListModel.insertElementAt(newItemTextField.getText(), index);
	            //If we just wanted to add to the end, we'd do this:
	            //listModel.addElement(employeeName.getText());
	 
	            //Reset the text field.
	            newItemTextField.requestFocusInWindow();
	            newItemTextField.setText("");
	 
	            //Select the new item and make it visible.
	            favoritesList.setSelectedIndex(index);
	            favoritesList.ensureIndexIsVisible(index);
	            determineRemoveButtonEnabled();
				
			}
		});
		
		removeButton = new JButton("Remove");
		determineRemoveButtonEnabled();
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int index = favoritesList.getSelectedIndex();
				favoritesListModel.remove(index);

				int size = favoritesListModel.getSize();

				if (size == 0) { //Nobody's left, disable firing.
					removeButton.setEnabled(false);

				} else { //Select an index.
					if (index == favoritesListModel.getSize()) {
						//removed item in last position
						index--;
					}

					favoritesList.setSelectedIndex(index);
					favoritesList.ensureIndexIsVisible(index);
				}
				determineRemoveButtonEnabled();
			}
		});

		//add all components
		
		add(favoritesListScrollPane, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,
                                           BoxLayout.LINE_AXIS));
        buttonPane.add(addButton);
        buttonPane.add(newItemTextField);
        buttonPane.add(removeButton);
		add(buttonPane, BorderLayout.PAGE_END);

	}
	private void determineRemoveButtonEnabled()
	{
		if(favoritesListModel.size() > 0)
        	removeButton.setEnabled(true);
		else
			removeButton.setEnabled(false);
	}
	public abstract String[] getSettingValues();
	public abstract void storeSettingValues();

}
