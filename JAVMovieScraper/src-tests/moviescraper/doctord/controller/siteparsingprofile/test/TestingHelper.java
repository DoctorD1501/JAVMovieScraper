package moviescraper.doctord.controller.siteparsingprofile.test;

import javax.swing.*;
import java.awt.*;

public class TestingHelper {


	
	public static void showImage(final String title, final Image image) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFrame jFrame = new JFrame(title);
					jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					jFrame.add( new JLabel(new ImageIcon(image)) );
					jFrame.pack();
					//jFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
