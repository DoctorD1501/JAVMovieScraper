package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.view.renderer.SearchResultsRenderer;

/**
 * Generating a dialog for selecting a specific element.
 * @author sansibar
 */
public class SelectionDialog extends JPanel {

	private static final long serialVersionUID = 5244704222222415993L;
	
	private final JPanel panel = this;
	
	private JList<SearchResult> labelList;
	private SearchResult optionPickedFromPanel;

	/**
	 * Create the frame.
	 */
	public SelectionDialog(SearchResult[] searchResults, String siteName) {
		panel.setLayout(new BorderLayout());
		labelList = new JList<SearchResult>(searchResults);
		labelList.setCellRenderer(new SearchResultsRenderer());
		labelList.setVisible(true);
		JScrollPane pane = new JScrollPane(labelList);
		panel.add(pane, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(500,400));
		
		final JDialog bwin = new JDialog();
        bwin.addWindowFocusListener(new WindowFocusListener() {
             @Override
             public void windowLostFocus(WindowEvent e)
             {
               bwin.setVisible(false);
               bwin.dispose();
             }

             @Override
             public void windowGainedFocus(WindowEvent e)
             {
             }
         });
         bwin.add(panel);
         bwin.pack();
       
         labelList.addMouseListener(new MouseAdapter() {
        	 @Override
			public void mouseClicked(MouseEvent evt) {
        		 //capture left-double mouse click and dispose frame
        		 if ( evt.getButton() == MouseEvent.BUTTON1 ){
        			 if ( evt.getClickCount() == 2 ) {
        				 optionPickedFromPanel = labelList.getSelectedValue();
        				 SwingUtilities.getWindowAncestor(bwin).dispose();
        			 }
        		 }
        	 }
         });
	}
	
	public SearchResult getSelectedValue() {
		if (optionPickedFromPanel == null)
			optionPickedFromPanel = labelList.getSelectedValue();
		return optionPickedFromPanel;
	}

}
