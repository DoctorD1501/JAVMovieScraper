package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import moviescraper.doctord.controller.amalgamation.AllAmalgamationOrderingPreferences;
import moviescraper.doctord.controller.amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.controller.amalgamation.ScraperGroupAmalgamationPreference;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.view.renderer.DataItemSourceRenderer;
import moviescraper.doctord.view.renderer.MovieFieldCellRenderer;

public class AmalgamationSettingsDialog {
	
	//Begin View Objects

	private JPanel panel;
	JPanel overallAmalgamationPreferencePanel;
	JPanel specificAmalgamationPreferencePanel;
	private JFrame parent;

	private JComboBox<ScraperGroupName> scraperGroupNameComboBox;
	private JList<DataItemSource> overallAmalgamationPreferenceList;
	private JList<DataItemSource> specificFieldAmalgamationPreferenceList;
	JList<Field> jListMovieFields;
	
	
	//Buttons to reorder ParsingProfiles or disable them
	JButton upButtonOverall;
	JButton downButtonOverall;
	JButton disableButtonOverall;
	
	JButton upButtonSpecific;
	JButton downButtonSpecific;
	JButton disableButtonSpecific;
	JButton useDefaultOrderingForSelectedItem;
	JButton resetToDefaultSettings;
	
	JLabel panelHeaderSpecificFieldAmalgamationPreference;
	
	private static final int layoutVerticalGap = 10;
	private static final int layoutHorizontalGap = 10;
	
	//End View Objects
	
	//Model objects
	AllAmalgamationOrderingPreferences amalgamationPreferences; //state of our choices stored here when outside this dialog
	AllAmalgamationOrderingPreferences amalgamationPreferencesOriginal; //restore us to original state of our object before opening this dialog if we hit cancel
	
	private DefaultListModel<DataItemSource> overallAmalgamationPreferenceListModel;
	private DefaultListModel<DataItemSource> specificFieldAmalgamationPreferenceListModel;
	private DefaultListModel<Field> movieFieldsListModel;
	Field selectedMovieField;

	
	
	public AmalgamationSettingsDialog(JFrame parent, AllAmalgamationOrderingPreferences amalgamationPreferences) {
		
		this.parent = parent;
		BorderLayout panelLayoutManager = new BorderLayout();
		panelLayoutManager.setHgap(layoutHorizontalGap);
		panelLayoutManager.setVgap(layoutVerticalGap);
		this.panel = new JPanel(panelLayoutManager);
		this.amalgamationPreferences = amalgamationPreferences;
		this.amalgamationPreferencesOriginal = (AllAmalgamationOrderingPreferences) cloneObject(this.amalgamationPreferences);
		
		panelHeaderSpecificFieldAmalgamationPreference = new JLabel("Specific Field", SwingConstants.CENTER);
		
		//Begin Scraper Groups
		scraperGroupNameComboBox = createScraperGroupDropDown();
		BorderLayout northPanelLayoutManager = new BorderLayout();
		northPanelLayoutManager.setVgap(layoutVerticalGap);
		JPanel northPanel = new JPanel(northPanelLayoutManager);
		JLabel helpMessage = new JLabel("<html>Select the scrapers you wish to use and the preferred order of each item to use when amalgamating data from the same scraping group.<br>Higher numbered items have precedence over lower numbered items.<br> Any scrapers disabled under \"Default Ordering\" will not scrape at all, even if enabled in the specific ordering section.</html>");
		northPanel.add(helpMessage, BorderLayout.SOUTH);
		northPanel.add(scraperGroupNameComboBox, BorderLayout.NORTH);
		panel.add(northPanel, BorderLayout.NORTH);
		//End Scraper Groups
		
		
		//Movie Field Panel Initialization
		
		JList<Field> allowedMovieFieldsList = createMovieFieldsList();
		jListMovieFields.setSelectedIndex(0);
		JScrollPane movieFieldsScrollPane = new JScrollPane(allowedMovieFieldsList);
		JPanel movieFieldPanel = new JPanel(new BorderLayout());
		movieFieldPanel.add(movieFieldsScrollPane);
		
		//Movie Field Panel End
		
		//Overall Amalgamation Preferences Panel Begin
		overallAmalgamationPreferenceList = createOverallAmalgamationPreferenceList();
		JScrollPane overallAmalgamationPreferenceScrollPane = new JScrollPane(overallAmalgamationPreferenceList);
		overallAmalgamationPreferencePanel = new JPanel(new BorderLayout());
		overallAmalgamationPreferencePanel.setPreferredSize(new Dimension(300,200));
		overallAmalgamationPreferencePanel.add(overallAmalgamationPreferenceScrollPane, BorderLayout.CENTER);
		
		Box upDownDisablePanel = Box.createVerticalBox();
		upButtonOverall = createUpButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		disableButtonOverall = createDisableButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		downButtonOverall = createDownButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		
		
		upDownDisablePanel.add(upButtonOverall);
		upDownDisablePanel.add(disableButtonOverall);
		upDownDisablePanel.add(downButtonOverall);
		
		overallAmalgamationPreferencePanel.add(upDownDisablePanel, BorderLayout.EAST);
		overallAmalgamationPreferencePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Default Ordering",TitledBorder.CENTER, TitledBorder.TOP));
		panel.add(overallAmalgamationPreferencePanel, BorderLayout.WEST);
		
		//Scraper Panel End
		
		//Specific Scraper Field Panel Begin
		
		specificFieldAmalgamationPreferenceList = createSpecificFieldAmalgamationPreferenceList();
		JScrollPane specificFieldScraperScrollPane = new JScrollPane(specificFieldAmalgamationPreferenceList);
		specificAmalgamationPreferencePanel = new JPanel(new BorderLayout());
		specificAmalgamationPreferencePanel.setPreferredSize(new Dimension(300,200));
		specificAmalgamationPreferencePanel.add(specificFieldScraperScrollPane, BorderLayout.CENTER);
		
		
		Box upDownDisablePanelSpecific = Box.createVerticalBox();
		upButtonSpecific = createUpButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		disableButtonSpecific = createDisableButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		downButtonSpecific = createDownButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		useDefaultOrderingForSelectedItem = useDefaultOrderingForSelectedItemButton();
		
		upDownDisablePanelSpecific.add(upButtonSpecific);
		upDownDisablePanelSpecific.add(disableButtonSpecific);
		upDownDisablePanelSpecific.add(downButtonSpecific);
		upDownDisablePanelSpecific.add(useDefaultOrderingForSelectedItem);
		
		
		specificAmalgamationPreferencePanel.add(upDownDisablePanelSpecific, BorderLayout.EAST);
		
		
		JPanel allSpecificFieldPanels = new JPanel(new BorderLayout());
		
		allSpecificFieldPanels.add(panelHeaderSpecificFieldAmalgamationPreference,BorderLayout.NORTH);
		allSpecificFieldPanels.add(movieFieldPanel, BorderLayout.WEST);
		allSpecificFieldPanels.add(specificAmalgamationPreferencePanel, BorderLayout.EAST);
		allSpecificFieldPanels.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Specific Ordering",TitledBorder.CENTER, TitledBorder.TOP));
		panel.add(allSpecificFieldPanels, BorderLayout.EAST);
		
		//Specific Scraper Field Panel End
		
		resetToDefaultSettings = createResetDefaultSettingsButton();
		
		panel.add(resetToDefaultSettings, BorderLayout.SOUTH);
		
		//JLabel helpLabel = new JLabel("<html>Any scrapers selected here will be used to amalgamate data when using either the<br> \"Scrape JAV\" or \"Scrape JAV (Automatic)\" scrapers</html>");
		//panel.add(helpLabel);
		

		/*for (String option : options) {
			boolean isSelected = selected.contains(option);
			JCheckBox cb = new JCheckBox(option, isSelected);
			checkboxes.add(cb);
			panel.add(cb);
		}*/
		
		
	}
	
	private JButton useDefaultOrderingForSelectedItemButton() {
		JButton button = new JButton("Use Default Ordering");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).removeCustomOrderingForField(selectedMovieField);
				createSpecificFieldAmalgamationPreferenceList();
			}
		});
		return button;
	}

	private JButton createResetDefaultSettingsButton() {
		resetToDefaultSettings = new JButton("Reset All To Default Settings");
		resetToDefaultSettings.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				amalgamationPreferences.reinitializeDefaultPreferences();
				createOverallAmalgamationPreferenceList();
				createSpecificFieldAmalgamationPreferenceList();
			}
		});
		return resetToDefaultSettings;
	}

	private void synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, boolean isOverallPrefSync)
	{
		if(amalgamationPreferenceListModel != null)
		{
			LinkedList<DataItemSource> sppiAllValues = new LinkedList<DataItemSource>();
			for(int i = 0; i < amalgamationPreferenceListModel.getSize(); i++)
			{
				sppiAllValues.add(amalgamationPreferenceListModel.get(i));
			}
			if(isOverallPrefSync)
			{
				DataItemSourceAmalgamationPreference overallAmalgamationPreference = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getOverallAmalgamationPreference();
				overallAmalgamationPreference.setAmalgamationPreferenceOrder(sppiAllValues);
			}
			else
			{
				DataItemSourceAmalgamationPreference preferenceToSet = new DataItemSourceAmalgamationPreference(sppiAllValues);
				amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox
								.getSelectedItem()).setCustomOrderingForField(selectedMovieField, preferenceToSet);
				panelHeaderSpecificFieldAmalgamationPreference.setText("<html> Using <b>Specific</b> Ordering for " + getNameOfCurrentMovieFieldSelected() + "</html>");
			}
		}
	}
	
	
	private void swapElements(final JList<DataItemSource> amalgamationPreferenceList,
			final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, boolean isOverallPrefSync, int pos1, int pos2) {
	    DataItemSource tmp = (DataItemSource) amalgamationPreferenceListModel.get(pos1);
	    amalgamationPreferenceListModel.set(pos1, amalgamationPreferenceListModel.get(pos2));
	    amalgamationPreferenceListModel.set(pos2, tmp);
	    synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(amalgamationPreferenceListModel, isOverallPrefSync);
	}
	
	private JButton createUpButton(
			final JList<DataItemSource> amalgamationPreferenceList,
			final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, final boolean isOverallPrefSync) {
		
		JButton upButton = new JButton("↑");
		upButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 int indexOfSelected = amalgamationPreferenceList.getSelectedIndex();
				 if(indexOfSelected > 0 && indexOfSelected != amalgamationPreferenceListModel.getSize() - 1)
				 {
					swapElements(amalgamationPreferenceList,
							amalgamationPreferenceListModel, isOverallPrefSync, indexOfSelected,
							indexOfSelected - 1);
					indexOfSelected = indexOfSelected - 1;
					 amalgamationPreferenceList.setSelectedIndex(indexOfSelected );
					 amalgamationPreferenceList.updateUI();
				 }
			}
		});
		return upButton;
	}
	
	private JButton createDownButton(final JList<DataItemSource> amalgamationPreferenceList,
			final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, final boolean isOverallPrefSync)
	{
		JButton downButton = new JButton("↓");
		downButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 int indexOfSelected = amalgamationPreferenceList.getSelectedIndex();
				 //We don't want to be able to move either the last item in the list or the one before that down
				 //this is because the last item in the list is always the default item, and we always want that to be last
				 if(indexOfSelected >= 0 && indexOfSelected < amalgamationPreferenceListModel.getSize() - 2)
				 {
					swapElements(amalgamationPreferenceList,
							amalgamationPreferenceListModel, isOverallPrefSync, indexOfSelected,
							indexOfSelected + 1);
					indexOfSelected = indexOfSelected + 1;
					 amalgamationPreferenceList.setSelectedIndex(indexOfSelected );
					 amalgamationPreferenceList.updateUI();
				 }
			}
		});
		return downButton;
	}
	
	private JButton createDisableButton(final JList<DataItemSource> amalgamationPreferenceList,
			final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, final boolean isOverallPrefSync)
	{
		JButton disableButton = new JButton("<html><font color='green'>Enable</font> / <font color='red'><strike>Disable</strike></font></html>");
		disableButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DataItemSource selectedItem = amalgamationPreferenceList.getSelectedValue();
				if(selectedItem != null){
					selectedItem.setDisabled(!selectedItem.isDisabled());
					synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(amalgamationPreferenceListModel, isOverallPrefSync);
					amalgamationPreferenceList.updateUI();
				}
			}
		});
		return disableButton;
	}
	
	
	private JComboBox<ScraperGroupName> createScraperGroupDropDown()
	{
		//Get any scraper groups defined except for items belonging to the default set
		EnumSet<ScraperGroupName> everythingButDefaultGroup = EnumSet
				.complementOf(EnumSet
						.of(ScraperGroupName.DEFAULT_SCRAPER_GROUP));
		
		JComboBox<ScraperGroupName> comboBox = new JComboBox<ScraperGroupName>(
				everythingButDefaultGroup
						.toArray(new ScraperGroupName[everythingButDefaultGroup
								.size()]));
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(overallAmalgamationPreferenceListModel, true);
				synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(specificFieldAmalgamationPreferenceListModel, false);
			}
		});
		return comboBox;
	}
	
	private JList<DataItemSource> createOverallAmalgamationPreferenceList()
	{
		if(overallAmalgamationPreferenceListModel == null)
			overallAmalgamationPreferenceListModel = new DefaultListModel<DataItemSource>();
		else
			overallAmalgamationPreferenceListModel.clear();
		if(overallAmalgamationPreferenceList == null)
		{
			overallAmalgamationPreferenceList = new JList<DataItemSource>(overallAmalgamationPreferenceListModel);
			overallAmalgamationPreferenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			overallAmalgamationPreferenceList.setCellRenderer(new DataItemSourceRenderer());
		}
		Collection<DataItemSource> listData = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getOverallAmalgamationPreference().getAmalgamationPreferenceOrder();
		for(DataItemSource currentItem : listData)
		{
			overallAmalgamationPreferenceListModel.addElement(currentItem);
		}

		synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(overallAmalgamationPreferenceListModel, true);
		return overallAmalgamationPreferenceList;
	}
	
	private JList<DataItemSource> createSpecificFieldAmalgamationPreferenceList()
	{
		if(specificFieldAmalgamationPreferenceListModel == null)
		{
			specificFieldAmalgamationPreferenceListModel = new DefaultListModel<DataItemSource>();
		}
		else
		{
			specificFieldAmalgamationPreferenceListModel.clear();
		}
		//case when no item is selected
		if(selectedMovieField == null)
		{
			specificFieldAmalgamationPreferenceListModel.clear();
			panelHeaderSpecificFieldAmalgamationPreference.setText("No movie field selected.");
			specificFieldAmalgamationPreferenceList.updateUI();
			return specificFieldAmalgamationPreferenceList;
			
		}
		
		if(jListMovieFields == null)
			jListMovieFields = createMovieFieldsList();
		
		if(specificFieldAmalgamationPreferenceList == null)
		{
			specificFieldAmalgamationPreferenceList = new JList<DataItemSource>(specificFieldAmalgamationPreferenceListModel);
			specificFieldAmalgamationPreferenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			specificFieldAmalgamationPreferenceList.setCellRenderer(new DataItemSourceRenderer());
		}
		
		Collection<DataItemSource> listData;
		DataItemSourceAmalgamationPreference orderingForField = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getSpecificAmalgamationPreference(selectedMovieField);
		
		if(orderingForField != null)
		{
			panelHeaderSpecificFieldAmalgamationPreference.setText("<html> Using <b>Specific</b> Ordering for " + getNameOfCurrentMovieFieldSelected() + "</html>");
			listData = orderingForField.getAmalgamationPreferenceOrder();
		}
		else
		{
			panelHeaderSpecificFieldAmalgamationPreference.setText("<html>Using <b>Default</b> Ordering for " + getNameOfCurrentMovieFieldSelected() + "</html>");
			//we need to create a new object for this field copied from the overall ordering using the same type as the original items
			listData = new LinkedList<DataItemSource>();
			DataItemSourceAmalgamationPreference overallPrefs = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getOverallAmalgamationPreference();
			LinkedList<DataItemSource> overallPrefsDataItems = overallPrefs.getAmalgamationPreferenceOrder();
			for(DataItemSource currentItem : overallPrefsDataItems)
			{
				listData.add(currentItem.createInstanceOfSameType());
			}
		}
		for(DataItemSource currentItem : listData)
		{
			specificFieldAmalgamationPreferenceListModel.addElement(currentItem);
		}

		specificFieldAmalgamationPreferenceList.updateUI();
		return specificFieldAmalgamationPreferenceList;
	}
	
	private String getNameOfCurrentMovieFieldSelected()
	{
		if(selectedMovieField == null)
			return "";
		else return MovieFieldCellRenderer.formatFieldText(selectedMovieField.getName());
	}
	
	private JList<Field> createMovieFieldsList()
	{
		movieFieldsListModel = new DefaultListModel<Field>();
		List<Field> listData = ScraperGroupAmalgamationPreference.getMoviefieldNames();
		for(Field currentField : listData)
			movieFieldsListModel.addElement(currentField);
		
		jListMovieFields = new JList<Field>(movieFieldsListModel);
		jListMovieFields.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListMovieFields.addListSelectionListener(new ListSelectionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void valueChanged(ListSelectionEvent e) {
					//no item selected
					if (((JList<Field>) e.getSource()).getSelectedIndex() == -1)
					{
						selectedMovieField = null;
						createSpecificFieldAmalgamationPreferenceList();
						specificFieldAmalgamationPreferenceList.updateUI();
					}
					Field newSelectedField = ((JList<Field>) e.getSource()).getSelectedValue();
					if(newSelectedField != null && !newSelectedField.equals(selectedMovieField))
					{
						selectedMovieField = newSelectedField;
						createSpecificFieldAmalgamationPreferenceList();
						specificFieldAmalgamationPreferenceList.updateUI();
						//updateSpecificFieldAmalgamationLists(selectedMovieField);
					}
			}
		});
		

		jListMovieFields.setCellRenderer(new MovieFieldCellRenderer());
		selectedMovieField = jListMovieFields.getSelectedValue();
		return jListMovieFields;
	}
	
	private Object cloneObject(Object root)
	{
	    return JsonReader.jsonToJava(JsonWriter.objectToJson(root));    
	}
	
	private void restorePreferencesBeforeDialogOpened()
	{
		amalgamationPreferences.setAllAmalgamationOrderingPreferences(amalgamationPreferencesOriginal.getAllAmalgamationOrderingPreferences());

	}
	
	public boolean show(){
		int result = JOptionPane.showOptionDialog(parent, panel, "Amalgamation Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (result == JOptionPane.OK_OPTION) {

			
			try {
				amalgamationPreferences.saveToPreferencesFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		else //hit cancel, undo our changes
		{
			restorePreferencesBeforeDialogOpened();
			return false;
		}
	}
	
}
