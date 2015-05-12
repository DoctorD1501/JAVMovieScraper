package moviescraper.doctord.GUI;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class SelectScrapersDialog {
	private List<JCheckBox> checkboxes;
	private List<String> selected;
	private Box panel;
	private JFrame parent;
	
	public SelectScrapersDialog(JFrame parent, List<String> options, List<String> selected) {
		
		this.checkboxes = new ArrayList<>();
		this.selected = selected;
		this.panel = Box.createVerticalBox();

		JLabel helpLabel = new JLabel("<html>Any scrapers selected here will be used to amalgamate data when using either the<br> \"Scrape JAV\" or \"Scrape JAV (Automatic)\" scrapers</html>");
		Font baseFont = helpLabel.getFont().deriveFont(Font.PLAIN);
		//helpLabel.setFont(baseFont);
		panel.add(helpLabel);
		this.parent = parent;

		for (String option : options) {
			boolean isSelected = selected.contains(option);
			JCheckBox cb = new JCheckBox(option, isSelected);
			checkboxes.add(cb);
			panel.add(cb);
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
