package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
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
	private JButton previewLinkButton;

	/**
	 * Create the frame.
	 */
	public SelectionDialog(SearchResult[] searchResults, String siteName) {
		panel.setLayout(new BorderLayout());
		labelList = new JList<>(searchResults);
		labelList.setCellRenderer(new SearchResultsRenderer());
		labelList.setVisible(true);
		JScrollPane pane = new JScrollPane(labelList);
		panel.add(pane, BorderLayout.CENTER);

		previewLinkButton = new JButton("Preview Link in Browser");
		//previewLinkButton.setEnabled(false);
		previewLinkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SearchResult selectedValueinAction = labelList.getSelectedValue();
				if (selectedValueinAction != null && selectedValueinAction.getUrlPath() != null && selectedValueinAction.getUrlPath().length() > 0) {
					try {
						Desktop.getDesktop().browse(new URI(selectedValueinAction.getUrlPath()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		panel.add(previewLinkButton, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(500, 400));

		final JDialog bwin = new JDialog();
		bwin.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				bwin.setVisible(false);
				bwin.dispose();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
			}
		});
		bwin.add(panel);
		bwin.pack();

		labelList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				//capture left-double mouse click and dispose frame
				if (evt.getButton() == MouseEvent.BUTTON1) {
					if (evt.getClickCount() == 2) {
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
