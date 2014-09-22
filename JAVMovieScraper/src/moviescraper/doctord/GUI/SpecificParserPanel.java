package moviescraper.doctord.GUI;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.AvEntertainmentParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.SpecificProfile;
import moviescraper.doctord.model.ScraperAction;

public class SpecificParserPanel extends JPanel {

	private static final long serialVersionUID = 4639300587159880348L;
	private JComboBox<ComboItem> comboBox;
	private Vector<ComboItem> items = new Vector<>();
	
	private GUIMain main;

	public SpecificParserPanel(GUIMain main) {
		this.main = main;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel label = new JLabel("Choose specific parser");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);
		
		comboBox = new JComboBox<>(items);
		add(comboBox);
		
		JButton btnScrape = new JButton("Scrape");
		btnScrape.setAlignmentX( CENTER_ALIGNMENT );
		add(btnScrape);
		btnScrape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = comboBox.getSelectedItem();
				if (selectedItem instanceof ComboItem) {
					SiteParsingProfile spp = ((ComboItem) selectedItem).parser;
					if ( spp != null ) {
						scrape(spp);
					}
				}
			}
		});

		try {
			initSpinner();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void scrape(SiteParsingProfile spp) {
		File toScrape = main.getCurrentFile();
		if (toScrape != null) {
			ScraperAction action = new ScraperAction(spp, spp.getMovieScraper(), toScrape );
			Movie scrapedMovie = action.scrape();
			main.getFileDetailPanel().setNewMovie( scrapedMovie );
		} else {
			JOptionPane.showMessageDialog(this, "No file selected.", "No file selected.", JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings("rawtypes")
	private void initSpinner() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String packageName = AvEntertainmentParsingProfile.class.getPackage().getName();
		List<Class> classes = getClasses(packageName);
		for (Class c : classes) {
			if (c.isInterface())
				continue;
			
			try {
				Object instance = c.newInstance();
				if (instance instanceof SpecificProfile && 
						instance instanceof SiteParsingProfile ) {
					SpecificProfile sp = (SpecificProfile) instance;
					String title = sp.getParserName();
					ComboItem comboItem = new ComboItem(title, (SiteParsingProfile) instance);
					items.add(comboItem);
					System.out.println( comboItem );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (items.size() > 0)
			comboBox.setSelectedIndex(0);
	}
	
	/**
	* Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	*
	* @param packageName The base package
	* @return The classes
	* @throws ClassNotFoundException
	* @throws IOException
	*/
	@SuppressWarnings("rawtypes")
	private static List<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}
	

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame frame = new JFrame("Test Parser Panel");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.add( new SpecificParserPanel(null) );
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class ComboItem {
		String title;
		SiteParsingProfile parser;
		
		public ComboItem(String title, SiteParsingProfile parser) {
			this.title = title;
			this.parser = parser;
		}
		
		@Override
		public String toString() {
			return title;
		}
	}
}
