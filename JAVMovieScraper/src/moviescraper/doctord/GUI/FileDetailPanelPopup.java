package moviescraper.doctord.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class FileDetailPanelPopup extends JPopupMenu {
	
	private static final long serialVersionUID = 8711342488935696278L;
	private AbstractFileDetailPanelAddGUI add;

	public FileDetailPanelPopup(AbstractFileDetailPanelAddGUI add) {
		this.add = add;
		intialize();
	}

	private void intialize() {
		JMenuItem addItem = new JMenuItem(add.getMenuItemName());
		addItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				add.showGUI();
			}
		});
		this.add(addItem);
	}
}
