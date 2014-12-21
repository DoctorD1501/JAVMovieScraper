package moviescraper.doctord.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class MessageConsoleGUI extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageConsoleGUI frame = new MessageConsoleGUI();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public MessageConsoleGUI()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setTitle("JAVMovieScraper Console Window");
		
		JPanel somePanel = new JPanel();
		somePanel.setLayout(new BorderLayout());
		somePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(somePanel);
		JTextPane textPane = new JTextPane();
		somePanel.add( new JScrollPane(textPane), BorderLayout.CENTER);
		MessageConsole mc = new MessageConsole(textPane);
		mc.redirectOut(Color.BLACK, System.out);
		mc.redirectErr(Color.RED, System.err);
		mc.setMessageLines(750);
		this.setVisible(true);
	}

}
