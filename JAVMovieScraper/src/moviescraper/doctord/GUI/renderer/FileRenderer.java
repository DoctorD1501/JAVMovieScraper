package moviescraper.doctord.GUI.renderer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import moviescraper.doctord.IconCache;

public class FileRenderer extends DefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean pad;
	private Border padBorder = new EmptyBorder(3, 3, 3, 3);


	public FileRenderer(boolean pad) {
		this.pad = pad;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		Component c = super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		JLabel l = (JLabel) c;
		File f = (File) value;
		l.setText(f.getName());
		try {
			l.setIcon(IconCache.getIconFromCache(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Setting Icon at " + System.currentTimeMillis());
		//l.setIcon(FileSystemView.getFileSystemView().getSystemIcon(f));

		if (pad) {
			l.setBorder(padBorder);
		}

		return l;
	}
}