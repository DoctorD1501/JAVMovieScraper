package moviescraper.doctord.GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import moviescraper.doctord.controller.SpecificScraperAction;

public class SpecificParserPanel extends JPanel {

	private static final long serialVersionUID = 4639300587159880348L;
	private JComboBox<ComboItem> comboBox;
	private Vector<ComboItem> items = new Vector<ComboItem>();
	
	private GUIMain main;

	public SpecificParserPanel(GUIMain main) {
		this.main = main;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel label = new JLabel("Choose Specific Scraper");
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);
		
		comboBox = new JComboBox<ComboItem>(items);
		add(comboBox);
		
		JButton btnScrape = new JButton("Scrape");
		btnScrape.setAlignmentX( CENTER_ALIGNMENT );
		add(btnScrape);
		btnScrape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Find out the type of parser to use from the drop down box and pass it off to the scrape method
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
		try
		{
			main.setMainGUIEnabled(false);
			main.removeOldScrapedMovieReferences();
			List<File> toScrape = main.getCurrentFile();
			if (toScrape != null) {
				for(File currentFile : toScrape)
				{ 
					//reset the SiteParsingProfile so we don't get leftover stuff from the last file scraped
					//we want it to be of the same type, so we use the newInstance() method which will automatically
					//return a new object of the type the SiteParsingProfile actually is
					spp = spp.newInstance();
					SpecificScraperAction action = new SpecificScraperAction(spp, spp.getMovieScraper(), currentFile );
					Movie scrapedMovie = action.scrape();
					if(scrapedMovie != null)
						main.movieToWriteToDiskList.add(scrapedMovie);
					main.getFileDetailPanel().setNewMovie( scrapedMovie , true);
				}

			} else {
				JOptionPane.showMessageDialog(this, "No file selected.", "No file selected.", JOptionPane.ERROR_MESSAGE);
			}
		}
		finally
		{
			main.setMainGUIEnabled(true);
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
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Make sure the scrapers in the combo box are in alphabetic order
		Collections.sort(items, new Comparator<ComboItem>() {

			@Override
			public int compare(ComboItem arg0, ComboItem arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		});;
		
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
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	    	//maybe we are running from a jar file, so try that
	    	File thisJarFile = new File(new java.io.File(SpecificParserPanel.class.getProtectionDomain()
	    			  .getCodeSource()
	    			  .getLocation()
	    			  .getPath())
	    			.getName());
	    	if(thisJarFile != null && thisJarFile.exists())
	    	{
	    		List<Class> classNames=new ArrayList<Class>();
	    		ZipInputStream zip = new ZipInputStream(new FileInputStream(thisJarFile.getPath()));
	    		for(ZipEntry entry=zip.getNextEntry();entry!=null;entry=zip.getNextEntry())
	    		    if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
	    		        // This ZipEntry represents a class. Now, what class does it represent?
	    		        StringBuilder className=new StringBuilder();
	    		        for(String part : entry.getName().split("/")) {
	    		            if(className.length() != 0)
	    		                className.append(".");
	    		            className.append(part);
	    		            if(part.endsWith(".class"))
	    		                className.setLength(className.length()-".class".length());
	    		        }

	    		        if(className.toString().contains(packageName))
	    		        {
	    		        	classNames.add(Class.forName(className.toString()));
	    		        }
	    		        zip.closeEntry();
	    		    }
	    		zip.close();
	    		return classNames;
	    	}
	    	else return classes;
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
