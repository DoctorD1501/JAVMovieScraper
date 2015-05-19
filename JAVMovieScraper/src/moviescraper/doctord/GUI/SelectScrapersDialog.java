package moviescraper.doctord.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;

import moviescraper.doctord.Amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.GUI.renderer.SiteParsingProfileItemRenderer;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.SiteParsingProfile.SpecificProfileFactory;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile.ScraperGroupName;

public class SelectScrapersDialog {
	
	//Begin View Objects
	private List<JCheckBox> checkboxes;
	private List<String> selected;
	private JPanel panel;
	private JFrame parent;

	private JComboBox<ScraperGroupName> scraperGroups;
	private JList<SiteParsingProfileItem> allowedScraperList;
	
	
	//Buttons to reorder ParsingProfiles or disable them
	JButton upButtonParsingProfile;
	JButton downButtonParsingProfile;
	JButton disableButtonParsingProfile;
	
	//End View Objects
	
	//Model objects
	private DefaultListModel<SiteParsingProfileItem> allowedScraperListModel;
	private DataItemSourceAmalgamationPreference overallAmalgamationPreference;
	
	//End Model Objects
	
	JPanel allowedScraperPanel;
	
	public SelectScrapersDialog(JFrame parent, List<String> options, List<String> selected) {
		
		this.checkboxes = new ArrayList<>();
		this.selected = selected;
		this.panel = new JPanel();
		
		scraperGroups = createScraperGroupDropDown();
		allowedScraperList = createAllowedScraperList();
		panel.add(scraperGroups);
		JScrollPane allowedScraperScrollPane = new JScrollPane(allowedScraperList);
		allowedScraperPanel = new JPanel(new BorderLayout());
		allowedScraperPanel.setPreferredSize(new Dimension(200,200));
		allowedScraperPanel.add(allowedScraperScrollPane, BorderLayout.CENTER);
		
		Box upDownDisablePanel = Box.createVerticalBox();
		upButtonParsingProfile = createUpButtonParsingProfile();
		downButtonParsingProfile = createDownButtonParsingProfile();
		disableButtonParsingProfile = createDisableButtonParsingProfile();
		
		upDownDisablePanel.add(upButtonParsingProfile);
		upDownDisablePanel.add(disableButtonParsingProfile);
		upDownDisablePanel.add(downButtonParsingProfile);
		
		allowedScraperPanel.add(upDownDisablePanel, BorderLayout.EAST);
		
		panel.add(allowedScraperPanel);
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
	
	private void synchronizeScraperListToAmalgamationPreference()
	{
		if(allowedScraperListModel != null)
		{
			SiteParsingProfileItem[] sppiAllValues = new SiteParsingProfileItem[allowedScraperListModel.getSize()];
			for(int i = 0; i < allowedScraperListModel.getSize(); i++)
			{
				sppiAllValues[i] = allowedScraperListModel.get(i);
			}

			overallAmalgamationPreference = new DataItemSourceAmalgamationPreference();
			overallAmalgamationPreference.setAmalgamationPreferenceOrder(sppiAllValues);
			System.out.println(overallAmalgamationPreference);
		}
	}
	
	private void swapElements(int pos1, int pos2) {
	    SiteParsingProfileItem tmp = (SiteParsingProfileItem) allowedScraperListModel.get(pos1);
	    allowedScraperListModel.set(pos1, allowedScraperListModel.get(pos2));
	    allowedScraperListModel.set(pos2, tmp);
	    synchronizeScraperListToAmalgamationPreference();
	}
	
	public JButton createUpButtonParsingProfile()
	{
		JButton upButton = new JButton("↑");
		upButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 int indexOfSelected = allowedScraperList.getSelectedIndex();
				 if(indexOfSelected != 0)
				 {
					 swapElements(indexOfSelected, indexOfSelected - 1);
					 indexOfSelected = indexOfSelected - 1;
					 allowedScraperList.setSelectedIndex(indexOfSelected );
					 allowedScraperList.updateUI();
				 }
			}
		});
		return upButton;
	}
	
	public JButton createDownButtonParsingProfile()
	{
		JButton downButton = new JButton("↓");
		downButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 int indexOfSelected = allowedScraperList.getSelectedIndex();
				 if(indexOfSelected != allowedScraperListModel.getSize() - 1)
				 {
					 swapElements(indexOfSelected, indexOfSelected + 1);
					 indexOfSelected = indexOfSelected + 1;
					 allowedScraperList.setSelectedIndex(indexOfSelected );
					 allowedScraperList.updateUI();
				 }
			}
		});
		return downButton;
	}
	
	public JButton createDisableButtonParsingProfile()
	{
		JButton disableButton = new JButton("<html><font color='green'>Enable</font> / <font color='red'><strike>Disable</strike></font></html>");
		disableButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SiteParsingProfileItem selectedItem = allowedScraperList.getSelectedValue();
				selectedItem.setDisabled(!selectedItem.isDisabled());
				synchronizeScraperListToAmalgamationPreference();
				allowedScraperList.updateUI();
			}
		});
		return disableButton;
	}
	
	
	public JComboBox<ScraperGroupName> createScraperGroupDropDown()
	{
		JComboBox<ScraperGroupName> comboBox = new JComboBox<ScraperGroupName>(ScraperGroupName.values());
		comboBox.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				 JComboBox<ScraperGroupName> cbEventSource = (JComboBox<ScraperGroupName>)e.getSource();
				System.out.println("Now selected " + cbEventSource.getSelectedItem());
				filterSiteParsingProfileItemsByActiveScraperGroup();
				synchronizeScraperListToAmalgamationPreference();
			}
		});
		return comboBox;
	}
	
	public JList<SiteParsingProfileItem> createAllowedScraperList()
	{
		allowedScraperListModel = new DefaultListModel<SiteParsingProfileItem>();
		Collection<SiteParsingProfileItem> listData = SpecificProfileFactory.getAll();
		for(SiteParsingProfileItem currentItem : listData)
		{
			if(currentItem.getParser().getScraperGroupNames().contains(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP))
				allowedScraperListModel.addElement(currentItem);
		}
		
		JList<SiteParsingProfileItem> jListSiteParsingProfile = new JList<SiteParsingProfileItem>(allowedScraperListModel);
		jListSiteParsingProfile.setCellRenderer(new SiteParsingProfileItemRenderer());
		synchronizeScraperListToAmalgamationPreference();
		return jListSiteParsingProfile;
	}
	
	public void filterSiteParsingProfileItemsByActiveScraperGroup()
	{
		ScraperGroupName currentScraperGroupName = (ScraperGroupName) scraperGroups.getSelectedItem();
		
		Collection<SiteParsingProfileItem> listData = SpecificProfileFactory.getAll();
		allowedScraperListModel.clear();
		for(SiteParsingProfileItem currentItem : listData)
		{
			if(currentItem.getParser().getScraperGroupNames().contains(currentScraperGroupName))
				allowedScraperListModel.addElement(currentItem);
		}
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
