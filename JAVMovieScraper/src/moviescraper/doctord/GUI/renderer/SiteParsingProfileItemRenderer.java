package moviescraper.doctord.GUI.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;



public class SiteParsingProfileItemRenderer implements ListCellRenderer<SiteParsingProfileItem> {
	
	private static final long serialVersionUID = 1L;
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);
	private static final String strikeOpen = "<strike>";
	private static final String stikeClose = "</strike>";
	private static final String emptyString = "";
	private static final String enabledItemFontTag = "<font color='green'>";
	private static final String disabledItemFontTag = "<font color='red'>";
	private static final String fontCloseTag = "</font>";
	 protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();


	public Component getListCellRendererComponent(JList<? extends SiteParsingProfileItem> list, SiteParsingProfileItem value,
			int index, boolean isSelected, boolean cellHasFocus) {
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
		        isSelected, cellHasFocus);
		SiteParsingProfileItem entry = (SiteParsingProfileItem) value;
		
		renderer.setText("<html>" + getConditionalFontOpenTag(value) + getConditionalStrikeOpen(value) + 
				value.getParser().toString() + getConditionalStrikeClose(value) + fontCloseTag + "</html>");
		return renderer;
	}
	
	private String getConditionalStrikeOpen(SiteParsingProfileItem value)
	{
		if(value.isDisabled())
			return strikeOpen;
		else return emptyString;
	}
	
	private String getConditionalFontOpenTag(SiteParsingProfileItem value)
	{
		if(value.isDisabled())
			return disabledItemFontTag;
		else return enabledItemFontTag;
	}
	
	private String getConditionalStrikeClose(SiteParsingProfileItem value)
	{
		if(value.isDisabled())
			return strikeOpen;
		else return emptyString;
	}


}
