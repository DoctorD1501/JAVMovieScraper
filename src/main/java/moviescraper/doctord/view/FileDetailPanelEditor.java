package moviescraper.doctord.view;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

abstract class AbstractFileDetailPanelEditGUI {

	protected FileDetailPanel fileDetailPanel;
	protected AbstractFileDetailPanelEditGUI( FileDetailPanel fileDetailPanel ) {
		this.fileDetailPanel = fileDetailPanel;
	}
	
	protected enum Operation{
		ADD, EDIT, DELETE, EDIT_ALL
	}
	
	protected void showOptionDialog(JPanel panel, String title, Operation operation) {
		System.out.println("show option dialog");
		int result = JOptionPane.showOptionDialog(null, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null);
		if ( result == JOptionPane.OK_OPTION )
			try {
				switch(operation)
				{
				case ADD:
					addAction();
					break;
				case DELETE:
					//do nothing for now, since we aren't using a form to delete items
					break;
				case EDIT:
					editAction();
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// TODO sansibar better error detection instead of try-catch
				e.printStackTrace();
			}
	}
	
	
	public abstract String getMenuItemName();
	public abstract void showGUI(Operation operation);
	public abstract void addAction() throws Exception;
	public abstract void deleteAction();
	public abstract void editAction();
}
