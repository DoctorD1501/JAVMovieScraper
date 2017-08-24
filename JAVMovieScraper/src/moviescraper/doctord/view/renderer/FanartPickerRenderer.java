package moviescraper.doctord.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import moviescraper.doctord.model.dataitem.Thumb;

public class FanartPickerRenderer extends JLabel implements ListCellRenderer<Thumb> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	public FanartPickerRenderer() {
		setOpaque(true);
		setIconTextGap(12);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Thumb> list, Thumb value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Thumb entry = (Thumb) value;
		ImageIcon currentIcon = entry.getPreviewImageIconThumbImage();
		//no preview image so let's just try to resize it
		if(currentIcon == null)
		{
			currentIcon = entry.getImageIconThumbImage();
			//Image scaledImg = currentIcon.getImage().getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH);
			BufferedImage img = (BufferedImage) currentIcon.getImage();
			BufferedImage scaledImage = Scalr.resize(img, Method.QUALITY, 250, 250, Scalr.OP_ANTIALIAS);
			currentIcon = new ImageIcon(scaledImage);
		}
		setIcon(currentIcon);
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
