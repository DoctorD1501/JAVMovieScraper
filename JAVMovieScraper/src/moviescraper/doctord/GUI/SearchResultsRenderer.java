package moviescraper.doctord.GUI;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import moviescraper.doctord.SearchResult;



class SearchResultsRenderer extends JLabel implements ListCellRenderer<SearchResult> {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	  public SearchResultsRenderer() {
	    setOpaque(true);
	    setIconTextGap(12);
	  }

	  public Component getListCellRendererComponent(JList<? extends SearchResult> list, SearchResult value,
	      int index, boolean isSelected, boolean cellHasFocus) {
	    SearchResult entry = (SearchResult) value;
	    if(entry.getLabel().length() > 0)
	    	setText("<html>" + "<p>" + entry.getLabel() + "</p>" + "<br>" + "<p>" + entry.getUrlPath() + "</p" +  "</html>");
	    else
	    	setText(entry.getUrlPath());
	    if(entry.getPreviewImage().getThumbURL() != null)
	    	setIcon(entry.getPreviewImage().getImageIconThumbImage());
	    if (isSelected) {
	      setBackground(HIGHLIGHT_COLOR);
	      setForeground(Color.white);
	    } else {
	      setBackground(Color.white);
	      setForeground(Color.black);
	    }
	    return this;
	  }

	}