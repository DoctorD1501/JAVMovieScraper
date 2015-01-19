package moviescraper.doctord.GUI.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import moviescraper.doctord.ImageCache;
import moviescraper.doctord.dataitem.Actor;

@SuppressWarnings("serial")
public class ActressListRenderer extends DefaultListCellRenderer {

	private static final Font font = new Font("helvitica", Font.PLAIN, 12);
	private static final Dimension maxActorSizeDimension = new Dimension (150,150);

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
			String role = actor.getRole();
			if(role != null && role.length() > 0)
			{
				//two line version if thumbnail, otherwise show it one lines
				if(actor.getThumb() != null && actor.getThumb().getThumbURL() != null)
					value = "<html><body><b>" + actor.getName() + "</b><br> as <b>" + role + "</b></body></html>";
				else
					value = "<html><body><b>" + actor.getName() + "</b> as <b>" + role + "</b></body></html>";
			}
			else value = "<html><body><b> "+ actor.getName() + "</b>";
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
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	private static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	private ImageIcon resizeToMaxDimensions(Image image)
	{
		if(image.getWidth(null) > maxActorSizeDimension.width || image.getHeight(null) > maxActorSizeDimension.height)
			return new ImageIcon(Scalr.resize(toBufferedImage(image), Method.QUALITY, maxActorSizeDimension.width, maxActorSizeDimension.height, Scalr.OP_ANTIALIAS));
		else
			return new ImageIcon(toBufferedImage(image));
	}
	
	//TODO: I should probably re-implement this to use Maps instead of arrays
	private ImageIcon getImageIconForLabelName(Actor currentActor) {
	
		if (currentActor.getThumb() != null)
		{
			//see if we can find a local copy in the .actors folder before trying to download, but only if the image is not already in memory
			if(currentlySelectedActorsFolderList != null && currentlySelectedActorsFolderList.size() > 0 && currentlySelectedActorsFolderList.get(0).isDirectory())
			{
				String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
				File [] listFiles = currentlySelectedActorsFolderList.get(0).listFiles();
				if(listFiles != null)
				{
					for(File currentFile : listFiles)
					{
						if(currentFile.isFile() && FilenameUtils.removeExtension(currentFile.getName()).equals(currentActorNameAsPotentialFileName)){
							try {
								return resizeToMaxDimensions(ImageCache.getImageFromCache(currentFile.toURI().toURL()));
							} catch (MalformedURLException e) {
								return new ImageIcon();
							} catch (IOException e) {
								return new ImageIcon();
							}
						}
					}
				}
				if(currentActor.getThumb().getThumbURL() != null)
				{
					try {
						return resizeToMaxDimensions(currentActor.getThumb().getThumbImage());
					} catch (IOException e) {
						return new ImageIcon();
					}
				}
			}

			else 
			{
				try {
					return resizeToMaxDimensions(currentActor.getThumb().getThumbImage());
				} catch (IOException | NullPointerException e) {
					return new ImageIcon();
				}
			}
		}
		return new ImageIcon();			
	}
}