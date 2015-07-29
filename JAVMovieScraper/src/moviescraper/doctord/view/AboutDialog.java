package moviescraper.doctord.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

public class AboutDialog extends JDialog {
	
	private static final long serialVersionUID = 2426089852777554719L;
	
	//FIXME: is there a way to extract from project??
	private static final String versionString = "v0.2.04-alpha";
	private static final String nameString = "JAVMovieScraper";
	private static final String aboutString = 
			"Scrape XBMC and Kodi movie metadata and automatically rename " +
			"files for Japanese Adult Videos (JAV), American Adult DVDs, " +
			"and American Adult Webcontent";
	
	public AboutDialog(JFrame parent) {
		
		super(parent, "About " + nameString, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		JLabel nameLabel = new JLabel(nameString);

		Font baseFont = nameLabel.getFont().deriveFont(Font.PLAIN);
		nameLabel.setFont(baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 4.0f));
		
		JLabel versionLabel = new JLabel(versionString);
		versionLabel.setFont(baseFont.deriveFont(baseFont.getSize() + 2.0f));
		
		JTextArea aboutText = new JTextArea(aboutString);
		aboutText.setFont(baseFont);
		aboutText.setBackground(null);
		aboutText.setEditable(false);
		aboutText.setBorder(null);
		aboutText.setLineWrap(true);
		aboutText.setWrapStyleWord(true);
		aboutText.setFocusable(false);
			
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(createAppIcon());
		
		final int margin = 24;
		final int topMargin = margin * 3 / 2;
		
		Box namePanel = Box.createVerticalBox();
		namePanel.add(nameLabel);
		namePanel.add(Box.createVerticalStrut(margin));
		namePanel.add(versionLabel);
		
		Box iconPanel = Box.createHorizontalBox();
		iconPanel.add(iconLabel);
		iconPanel.add(Box.createHorizontalStrut(margin));
		iconPanel.add(namePanel);
		
		Box topPanel = Box.createVerticalBox();
		topPanel.setBorder(new EmptyBorder(topMargin, margin, margin, margin));
		topPanel.add(iconPanel);
		topPanel.add(Box.createVerticalStrut(topMargin));
		topPanel.add(aboutText);
				
		setContentPane(topPanel);
		
		setPreferredSize(new Dimension(320, 240));
		pack();
		setLocationByPlatform(true);
		setLocationRelativeTo(parent);
	}
	
	private ImageIcon createAppIcon()
	{
		try {
			BufferedImage iconBufferedImage;
			iconBufferedImage = ImageIO.read(getClass().getResource("/res/AppIcon.png"));
			iconBufferedImage = Scalr.resize(iconBufferedImage, Method.QUALITY, 64, 64, Scalr.OP_ANTIALIAS);
			ImageIcon icon = new ImageIcon(iconBufferedImage);
			return icon;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
