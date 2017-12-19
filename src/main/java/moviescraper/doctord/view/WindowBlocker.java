package moviescraper.doctord.view;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * 
 * Class to block input to frame below.
 * 
 * @see <a href="http://book.javanb.com/swing-hacks/swinghacks-chp-8-sect-4.html">Source of code</a>
 */
public class WindowBlocker extends JComponent implements MouseInputListener {

	/**
		 * 
		 */
	private static final long serialVersionUID = 775901218088693908L;
	private Cursor old_cursor;

	public WindowBlocker() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// You must beave this empty
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// You must beave this empty
	}

	public void block() {
		old_cursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setVisible(true);
	}

	public void unBlock() {
		setCursor(old_cursor);
		setVisible(false);
	}

}
