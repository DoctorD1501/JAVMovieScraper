package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class MessageConsoleGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final MessageConsoleGUI window = new MessageConsoleGUI();

	public static void showWindow() {
		window.setVisible(true);
		window.setState(NORMAL);
		window.toFront();
	}

	/**
	 * Create the frame.
	 */
	private MessageConsoleGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setTitle("JAVMovieScraper Console Window");

		JPanel somePanel = new JPanel();
		somePanel.setLayout(new BorderLayout());
		somePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(somePanel);
		JTextPane textPane = new JTextPane();
		somePanel.add(new JScrollPane(textPane), BorderLayout.CENTER);
		MessageConsole mc = new MessageConsole(textPane);
		mc.redirectOut(Color.BLACK, System.out);
		mc.redirectErr(Color.RED, System.err);
		mc.setMessageLines(750);
		this.setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				window.setVisible(false);
			}
		});
	}

}
