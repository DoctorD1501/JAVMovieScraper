package moviescraper.doctord.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import moviescraper.doctord.Amalgamation.AllAmalgamationOrderingPreferences;
import moviescraper.doctord.Amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.Amalgamation.ScraperGroupAmalgamationPreference;
import moviescraper.doctord.GUI.renderer.MovieFieldCellRenderer;
import moviescraper.doctord.GUI.renderer.DataItemSourceRenderer;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.SiteParsingProfile.SpecificProfileFactory;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.dataitem.DataItemSource;

public class SelectScrapersDialog {
	
	//Begin View Objects
	private List<JCheckBox> checkboxes;
	private List<String> selected;
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
	
	JLabel panelHeaderSpecificFieldAmalgamationPreference;
	
	//End View Objects
	
	//Model objects
	AllAmalgamationOrderingPreferences amalgamationPreferences; //state of our choices stored here when outside this dialog
	
	private DefaultListModel<DataItemSource> overallAmalgamationPreferenceListModel;
	private DefaultListModel<DataItemSource> specificFieldAmalgamationPreferenceListModel;
	private DefaultListModel<Field> movieFieldsListModel;
	Field selectedMovieField;

	
	
	public SelectScrapersDialog(JFrame parent, AllAmalgamationOrderingPreferences amalgamationPreferences, List<String> options, List<String> selected) {
		
		this.checkboxes = new ArrayList<>();
		this.selected = selected;
		this.panel = new JPanel();
		this.amalgamationPreferences = amalgamationPreferences;
		
		panelHeaderSpecificFieldAmalgamationPreference = new JLabel("Specific Field");
		
		//Begin Scraper Groups
		scraperGroupNameComboBox = createScraperGroupDropDown();
		panel.add(scraperGroupNameComboBox);
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
		overallAmalgamationPreferencePanel.setPreferredSize(new Dimension(200,200));
		overallAmalgamationPreferencePanel.add(overallAmalgamationPreferenceScrollPane, BorderLayout.CENTER);
		
		Box upDownDisablePanel = Box.createVerticalBox();
		upButtonOverall = createUpButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		disableButtonOverall = createDisableButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		downButtonOverall = createDownButton(overallAmalgamationPreferenceList, overallAmalgamationPreferenceListModel, true);
		
		
		upDownDisablePanel.add(upButtonOverall);
		upDownDisablePanel.add(disableButtonOverall);
		upDownDisablePanel.add(downButtonOverall);
		
		overallAmalgamationPreferencePanel.add(upDownDisablePanel, BorderLayout.EAST);
		JLabel panelHeaderOverallAmalgamationPreference = new JLabel("Overall");
		overallAmalgamationPreferencePanel.add(panelHeaderOverallAmalgamationPreference,BorderLayout.NORTH);
		overallAmalgamationPreferencePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(overallAmalgamationPreferencePanel);
		
		//Scraper Panel End
		
		//Specific Scraper Field Panel Begin
		
		specificFieldAmalgamationPreferenceList = createSpecificFieldAmalgamationPreferenceList();
		JScrollPane specificFieldScraperScrollPane = new JScrollPane(specificFieldAmalgamationPreferenceList);
		specificAmalgamationPreferencePanel = new JPanel(new BorderLayout());
		specificAmalgamationPreferencePanel.setPreferredSize(new Dimension(200,200));
		specificAmalgamationPreferencePanel.add(specificFieldScraperScrollPane, BorderLayout.CENTER);
		
		
		Box upDownDisablePanelSpecifc = Box.createVerticalBox();
		upButtonSpecific = createUpButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		disableButtonSpecific = createDisableButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		downButtonSpecific = createDownButton(specificFieldAmalgamationPreferenceList, specificFieldAmalgamationPreferenceListModel, false);
		
		
		upDownDisablePanelSpecifc.add(upButtonSpecific);
		upDownDisablePanelSpecifc.add(disableButtonSpecific);
		upDownDisablePanelSpecifc.add(downButtonSpecific);
		
		specificAmalgamationPreferencePanel.add(upDownDisablePanelSpecifc, BorderLayout.EAST);
		
		
		JPanel allSpecificFieldPanels = new JPanel(new BorderLayout());
		
		allSpecificFieldPanels.add(panelHeaderSpecificFieldAmalgamationPreference,BorderLayout.NORTH);
		allSpecificFieldPanels.add(movieFieldPanel, BorderLayout.WEST);
		allSpecificFieldPanels.add(specificAmalgamationPreferencePanel, BorderLayout.EAST);
		allSpecificFieldPanels.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(allSpecificFieldPanels);
		
		//Specific Scraper Field Panel End
		
		JLabel helpLabel = new JLabel("<html>Any scrapers selected here will be used to amalgamate data when using either the<br> \"Scrape JAV\" or \"Scrape JAV (Automatic)\" scrapers</html>");
		panel.add(helpLabel);
		this.parent = parent;

		for (String option : options) {
			boolean isSelected = selected.contains(option);
			JCheckBox cb = new JCheckBox(option, isSelected);
			checkboxes.add(cb);
			panel.add(cb);
		}
		
		
	}
	
	private void synchronizeAmalgamationPreferenceListToDataItemSourceAmalgamationPreference(final DefaultListModel<DataItemSource> amalgamationPreferenceListModel, boolean isOverallPrefSync)
	{
		System.out.println("Current state before sync " + amalgamationPreferences);
		if(amalgamationPreferenceListModel != null)
		{
			LinkedList<DataItemSource> sppiAllValues = new LinkedList<DataItemSource>();
			for(int i = 0; i < amalgamationPreferenceListModel.getSize(); i++)
			{
				sppiAllValues.add(amalgamationPreferenceListModel.get(i));
			}
			if(isOverallPrefSync)
			{
				System.out.println("overall case");
				DataItemSourceAmalgamationPreference overallAmalgamationPreference = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getOverallAmalgamationPreference();
				overallAmalgamationPreference.setAmalgamationPreferenceOrder(sppiAllValues);
				System.out.println("overall=" + overallAmalgamationPreference);
			}
			else
			{
				System.out.println("Specific case");
				System.out.println("selectedMovieField in sync= " + selectedMovieField);
				DataItemSourceAmalgamationPreference preferenceToSet = new DataItemSourceAmalgamationPreference(sppiAllValues);
				amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox
								.getSelectedItem()).setCustomOrderingForField(selectedMovieField, preferenceToSet);
				panelHeaderSpecificFieldAmalgamationPreference.setText("Specific Field Value Set");
			}
		}
		System.out.println("Current state after sync " + amalgamationPreferences);
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
				 System.out.println("selected index in down is " + indexOfSelected);
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
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				 JComboBox<ScraperGroupName> cbEventSource = (JComboBox<ScraperGroupName>)e.getSource();
				System.out.println("Now selected " + cbEventSource.getSelectedItem());
				//filterSiteParsingProfileItemsByActiveScraperGroup();
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
			specificFieldAmalgamationPreferenceListModel = new DefaultListModel<DataItemSource>();
		else
			specificFieldAmalgamationPreferenceListModel.clear();
		
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
			System.out.println("Found an existing ordering");
			panelHeaderSpecificFieldAmalgamationPreference.setText("Specific Ordering Set");
			listData = orderingForField.getAmalgamationPreferenceOrder();
		}
		else
		{
			System.out.println("No existing ordering found - creating a new one");
			panelHeaderSpecificFieldAmalgamationPreference.setText("No Specific Ordering Set");
			//we need to create a new object for this field copied from the overall ordering using the same type as the original items
			listData = new LinkedList<DataItemSource>();
			DataItemSourceAmalgamationPreference overallPrefs = amalgamationPreferences.getScraperGroupAmalgamationPreference((ScraperGroupName) scraperGroupNameComboBox.getSelectedItem()).getOverallAmalgamationPreference();
			LinkedList<DataItemSource> overallPrefsDataItems = overallPrefs.getAmalgamationPreferenceOrder();
			for(DataItemSource currentItem : overallPrefsDataItems)
			{
				listData.add(currentItem.createInstanceOfSameType());
			}
			System.out.println("List data is now " + listData);
		}
		System.out.println("Selected movie field = " + selectedMovieField);
		for(DataItemSource currentItem : listData)
		{
			specificFieldAmalgamationPreferenceListModel.addElement(currentItem);
		}

		System.out.println("specific list moidel is now " + specificFieldAmalgamationPreferenceListModel);
		specificFieldAmalgamationPreferenceList.updateUI();
		return specificFieldAmalgamationPreferenceList;
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
					Field newSelectedField = ((JList<Field>) e.getSource()).getSelectedValue();
					if(!newSelectedField.equals(selectedMovieField))
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
	
	
	public boolean show(){
		int result = JOptionPane.showOptionDialog(parent, panel, "Select JAV sites to scrape", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (result == JOptionPane.OK_OPTION) {
			selected.clear();
			
			for(JCheckBox checkbox : checkboxes)
				if (checkbox.isSelected())
					selected.add(checkbox.getText());
			
			return true;
		}
		
		return false;
	}
	
}
