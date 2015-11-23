package moviescraper.doctord.controller;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import moviescraper.doctord.view.FileDetailPanel;
import moviescraper.doctord.view.TagEditorPanel;

public class EditTagsAction extends AbstractAction {
	
	private static final long serialVersionUID = 1L;
	FileDetailPanel fileDetailPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		TagEditorPanel tagEditorPanel = new TagEditorPanel(fileDetailPanel.getCurrentMovie().getTags());
		int result = JOptionPane.showOptionDialog(fileDetailPanel.gui.getFrmMoviescraper(), tagEditorPanel, "Edit tags...",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null);
		if(result == JOptionPane.OK_OPTION)
		{
			tagEditorPanel.save();
			/*tagItemListModel listModel = (tagItemListModel) fileDetailPanel.gettagList().getModel();
			listModel.clear();
			for(tag currenttag : fileDetailPanel.getCurrentMovie().gettags())
			{
				listModel.addElement(currenttag);
			}*/
			fileDetailPanel.getTagList().setText(FileDetailPanel.toTagListFormat(fileDetailPanel.getCurrentMovie().getTags()));
			//listModel
			fileDetailPanel.updateUI();
		}
		
		
	}

	public EditTagsAction(FileDetailPanel fileDetailPanel) {
		super();
		this.fileDetailPanel = fileDetailPanel;
	}

}
