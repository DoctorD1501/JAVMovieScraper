package moviescraper.doctord.view.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import moviescraper.doctord.model.dataitem.DataItemSource;

public class DataItemSourceRenderer implements ListCellRenderer<DataItemSource> {

	private static final String strikeOpen = "<strike>";
	private static final String strikeClose = "</strike>";
	private static final String emptyString = "";
	private static final String enabledItemFontTag = "<font color='green'>";
	private static final String disabledItemFontTag = "<font color='red'>";
	private static final String fontCloseTag = "</font>";
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends DataItemSource> list, DataItemSource value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		renderer.setHorizontalTextPosition(JLabel.LEFT);

		renderer.setText("<html>" + getConditionalFontOpenTag(value) + getConditionalStrikeOpen(value) + (index + 1) + ". " + value.toString() + getConditionalStrikeClose(value)
				+ fontCloseTag + "</html>");
		renderer.setIcon(value.getProfileIcon());
		return renderer;
	}

	private String getConditionalStrikeOpen(DataItemSource value) {
		if (value.isDisabled())
			return strikeOpen;
		else
			return emptyString;
	}

	private String getConditionalFontOpenTag(DataItemSource value) {
		if (value.isDisabled())
			return disabledItemFontTag;
		else
			return enabledItemFontTag;
	}

	private String getConditionalStrikeClose(DataItemSource value) {
		if (value.isDisabled())
			return strikeClose;
		else
			return emptyString;
	}

}
