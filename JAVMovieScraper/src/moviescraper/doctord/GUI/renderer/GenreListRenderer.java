package moviescraper.doctord.GUI.renderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import moviescraper.doctord.dataitem.Genre;

public class GenreListRenderer extends DefaultListCellRenderer {
	

	private static final long serialVersionUID = 3855101965030097525L;
	private static final Font font = new Font("helvitica", Font.PLAIN, 12);

	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		label.setFont(font);
		setText(((Genre)value).getGenre());
		return this;
	}

}
