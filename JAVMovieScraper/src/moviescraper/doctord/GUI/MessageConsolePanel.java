package moviescraper.doctord.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class MessageConsolePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public MessageConsolePanel() {
		super();
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
		JTextPane textPane = new JTextPane();
		setPreferredSize(new Dimension(100,100));
		setMaximumSize(new Dimension(100,100));
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		MessageConsole mc = new MessageConsole(textPane);
		mc.redirectOut(Color.BLACK, System.out);
		mc.redirectErr(Color.RED, System.err);
		mc.setMessageLines(1000);
		this.setVisible(false);
	}

}
