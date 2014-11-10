package moviescraper.doctord.GUI.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FilenameUtils;

import moviescraper.doctord.dataitem.Actor;

@SuppressWarnings("serial")
public class ActressListRenderer extends DefaultListCellRenderer {

	Font font = new Font("helvitica", Font.BOLD, 12);

	List<File> currentlySelectedActorsFolderList;
	public ActressListRenderer(List<File> currentlySelectedActorsFolderList) {
		setBorder(new EmptyBorder(1, 1, 1, 1));
		this.currentlySelectedActorsFolderList = currentlySelectedActorsFolderList;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof Actor) {
			Actor actor = (Actor) value;
			value = actor.getName();
		}
		JLabel label = (JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		Object listElement = list.getModel().getElementAt(index);
		if (listElement != null && listElement instanceof Actor && label.getIcon() == null) {
			label.setIcon(getImageIconForLabelName((Actor)listElement));			
		}
		label.setHorizontalTextPosition(JLabel.RIGHT);
		label.setFont(font);
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		if (index % 2 == 0) {
			label.setBackground(SystemColor.controlShadow);
		} else {
			label.setBackground(SystemColor.controlHighlight);
		}
		return label;
	}
	
	//TODO sansibar reimplement Cache function
	//TODO: I should probably re-implement this to use Maps instead of arrays
	//TODO: Store the files from .actor in a cache somewhere
	private ImageIcon getImageIconForLabelName(Actor currentActor) {
	
		if (currentActor.getThumb() != null)
		{
			//see if we can find a local copy in the .actors folder before trying to download, but only if the image is not already in memory
			if(currentlySelectedActorsFolderList != null && currentlySelectedActorsFolderList.get(0).isDirectory())
			{
				String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
				File [] listFiles = currentlySelectedActorsFolderList.get(0).listFiles();
				for(File currentFile : listFiles)
				{
					if(currentFile.isFile() && FilenameUtils.removeExtension(currentFile.getName()).equals(currentActorNameAsPotentialFileName)){
						return new ImageIcon(currentFile.getPath());
					}
				}
			}

			else 
			{
				return currentActor.getThumb().getImageIconThumbImage();
			}
		}
		return new ImageIcon();			
	}
}