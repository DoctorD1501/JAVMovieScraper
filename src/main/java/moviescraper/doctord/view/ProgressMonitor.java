package moviescraper.doctord.view;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ProgressMonitor {
	private JLabel progressMessage;
	private javax.swing.ProgressMonitor progressMonitor;

	public ProgressMonitor(JFrame mainWindow) {
		progressMessage = new JLabel();
		progressMonitor = new javax.swing.ProgressMonitor(mainWindow, progressMessage, "", 0, 100);
		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);
	}

	public void start(String progressTitle) {
		progressMessage.setText(progressTitle);
		progressMonitor.setNote("Completed 0%");
		progressMonitor.setProgress(0);
	}

	public void update(int percentDone, String progressMessage) {
		if (percentDone < 0)
			percentDone = 0;

		if (percentDone > 100)
			percentDone = 100;

		String text = String.format("Completed %d%% - %s", percentDone, progressMessage);

		// showing the progress window is constantly stealing focus from other programs on Linux,
		// progress will never reach 100% to prevent closing the progress window automatically
		// an reuse the same window on multifile operations

		if (percentDone == 100)
			percentDone = 99;

		progressMonitor.setNote(text);
		progressMonitor.setProgress(percentDone);
	}

	public void stop() {
		progressMonitor.close();
	}

	public boolean isCanceled() {
		return progressMonitor.isCanceled();
	}
}