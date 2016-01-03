package moviescraper.doctord.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.StringUtils;

import moviescraper.doctord.controller.amalgamation.AllAmalgamationOrderingPreferences;
import moviescraper.doctord.controller.amalgamation.DataItemSourceAmalgamationPreference;
import moviescraper.doctord.controller.amalgamation.MovieScrapeResultGroup;
import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedMovieWorker;
import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedMovieWorker.ScrapeAmalgamatedMovieWorkerProperty;
import moviescraper.doctord.controller.amalgamation.ScraperGroupAmalgamationPreference;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.CustomComponents.AsyncImageComponent;

public class ScrapeAmalgamatedProgressDialog extends JDialog implements Runnable{
	
	private static final long serialVersionUID = 1L;
	
	//FileDetailPanel fileDetailPanel;
	private List<Movie> currentMovieList;
	
	private Movie currentAmalgamatedMovie;
	//ScraperGroupAmalgamationPreference amalgamationPreferences;
	private GUIMain guiMain;
	private JButton cancelButton;
	private JProgressBar progressBar;
	private ScrapeAmalgamatedMovieWorker worker;
	int currentFileIndexToScrape = 0;
	private static final int maxLettersToDisplayOfFileName = 80;
	private AmalgamationPropertyChangeListener propertyListener;
	private ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference;
	private List<ScraperProgressView> scraperProgressViews;
	AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences;
	
	
	private List<File> filesWeAreScraping;
	private JLabel fileBeingScrapedLabel;

	private boolean showPosterPicker;
	private boolean showFanartPicker;
	
	//private final PropertyChangeSupport propertyChangeSupport;

	public static void main(String [] args)
	{
		DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(
				new TheMovieDatabaseParsingProfile(), new Data18MovieParsingProfile());
		ScraperGroupAmalgamationPreference amalgamationPreferences = new ScraperGroupAmalgamationPreference(
				ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP,
				overallOrdering);
			GUIMain guiMain = new GUIMain();
			ScrapeAmalgamatedProgressDialog action = new ScrapeAmalgamatedProgressDialog(guiMain, guiMain.getAllAmalgamationOrderingPreferences(), amalgamationPreferences);
	        // schedule this for the event dispatch thread (edt)
	        SwingUtilities.invokeLater(action);
	}
	
	public void setFilesBeingScrapedLabel() {
		if (fileBeingScrapedLabel == null)
			fileBeingScrapedLabel = new JLabel();
		
		if (filesWeAreScraping != null && filesWeAreScraping.size() > 0) {
			File currentFile = filesWeAreScraping.get(currentFileIndexToScrape);
			if (currentFile != null) {
				String truncatedName = StringUtils.abbreviate(currentFile.getName(), maxLettersToDisplayOfFileName);
				fileBeingScrapedLabel.setText("<html>Scraping: <b>" + truncatedName + "</b> ("
						+ (currentFileIndexToScrape + 1) + "/" + filesWeAreScraping.size() + ")" + "</html>");
			}
		} else {
			fileBeingScrapedLabel.setText("No files selected.");
		}
	}
	
	public void cancelRunningScraper(SiteParsingProfile scraper)
	{
		if(worker != null)
		{
			worker.cancelRunningScraper(scraper);
		}
	}
	public void reinitializeScrapingForNextMovie()
	{
		currentAmalgamatedMovie = null;
		currentMovieList = new LinkedList<Movie>();
		for(ScraperProgressView currentScraperProgressView : scraperProgressViews)
		{
			currentScraperProgressView.resetPanelForNextScrape();
		}
		setFilesBeingScrapedLabel();
		progressBar.setValue(0);
	}
	
	public ScrapeAmalgamatedProgressDialog(GUIMain guiMain,
										   AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences,
										   ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference)
	{
		super(guiMain.getFrmMoviescraper());
		setTitle("Scraping...");
		//setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		//super("Scrape Amalgamated Action Test");
		this.guiMain = guiMain;
		this.filesWeAreScraping = guiMain.getCurrentlySelectedMovieFileList();
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
		scraperProgressViews = new LinkedList<>();
		
		showPosterPicker = MoviescraperPreferences.getInstance().getSelectArtManuallyWhenScraping();
		showFanartPicker = MoviescraperPreferences.getInstance().getSelectArtManuallyWhenScraping();
		
		this.scraperGroupAmalgamationPreference = scraperGroupAmalgamationPreference;
		currentMovieList = new LinkedList<>();
		propertyListener = new AmalgamationPropertyChangeListener();
		//propertyChangeSupport = new PropertyChangeSupport(this);
		//fileDetailPanel = new FileDetailPanel(MoviescraperPreferences.getInstance(), new GUIMain());
		//fileDetailPanel.setNewMovie(getFakeMovie(), false);
		
		JPanel overallPanel = new JPanel(new BorderLayout());
        this.setLocationRelativeTo(guiMain.getFrmMoviescraper());
        //this.setSize(new Dimension(800,600));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //this.setPreferredSize(new Dimension(400, 200));
        this.add(overallPanel);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(worker != null)
				{
					worker.cancelAllRunningScrapers();
					if(ScrapeAmalgamatedProgressDialog.this.guiMain != null)
					{
						ScrapeAmalgamatedProgressDialog.this.guiMain.setMainGUIEnabled(true);
					}
					ScrapeAmalgamatedProgressDialog.this.dispose();
				}
				
			}
		});
        
        fileBeingScrapedLabel = new JLabel();
        setFilesBeingScrapedLabel();
        overallPanel.add(fileBeingScrapedLabel, BorderLayout.NORTH);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(cancelButton, BorderLayout.SOUTH);
        southPanel.add(progressBar, BorderLayout.CENTER);
        overallPanel.add(southPanel, BorderLayout.SOUTH);
        //overallPanel.add(fileDetailPanel, BorderLayout.CENTER);
        this.setVisible(true);
		/*DataItemSourceAmalgamationPreference overallOrdering = new DataItemSourceAmalgamationPreference(
				new TheMovieDatabaseParsingProfile(), new Data18MovieParsingProfile());
		amalgamationPreferences = new ScraperGroupAmalgamationPreference(
				ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP,
				overallOrdering);*/
		if (filesAreSelected()) {
				//start scraping the first item in the selected file list, further items will be scraped once this one completes and a "AllScrapesFinished" message is received
				scrapeWithIndex(currentFileIndexToScrape);
				
		} else {
			System.err.println("No file selected!");
		}
		
		JPanel individualScrapeProgressPanel = createScraperProgressPanel();
		overallPanel.add(individualScrapeProgressPanel, BorderLayout.CENTER);
		this.pack();
	}
	
	private JPanel createScraperProgressPanel() {
		JPanel scraperProgressPanel = new JPanel();
		//scraperProgressViews = new LinkedList<>();
		//JScrollPane listScroller = new JScrollPane(scraperProgressPanel);
		scraperProgressPanel.setLayout(new BoxLayout(scraperProgressPanel, BoxLayout.Y_AXIS));
		List<DataItemSource> activeScrapers = scraperGroupAmalgamationPreference.getActiveScrapersUsedInOverallPreference();
		//update the state of what's active because it may have changed from elsewhere
		
		for(DataItemSource currentScraper : activeScrapers)
		{
			if(currentScraper != null && currentScraper instanceof SiteParsingProfile && shouldScrapeThread(currentScraper))
			{
				ScraperProgressView currentProgressView = new ScraperProgressView(currentScraper, this);
				scraperProgressPanel.add(currentProgressView);
				this.addPropertyChangeListener(currentProgressView.getScraperProgressPropertyChangeListener());
				scraperProgressViews.add(currentProgressView);
			}
		}
		return scraperProgressPanel;
	}
	
	private boolean shouldScrapeThread(DataItemSource parsingProfile) {
		//Default group used for single scraper operations
		if(scraperGroupAmalgamationPreference.getScraperGroupName().equals(ScraperGroupName.DEFAULT_SCRAPER_GROUP))
		{
			return true;
		}
		for (ScraperGroupName currentName : ScraperGroupName.values()) {
			ScraperGroupAmalgamationPreference currentPref = allAmalgamationOrderingPreferences
					.getScraperGroupAmalgamationPreference(currentName);
			
			LinkedList<DataItemSource> overallPrefs = currentPref
					.getOverallAmalgamationPreference()
					.getAmalgamationPreferenceOrder();
			
			for(DataItemSource currentDataItemSource : overallPrefs)
			{	
				if(currentDataItemSource.getDataItemSourceName().equals(parsingProfile.getDataItemSourceName()))
				{
					boolean disabled = currentDataItemSource.isDisabled();
					return !disabled;
				}
			}
		}
		return false;
	}

	public void scrapeWithIndex(int index)
	{
		//TODO: I may not need to do all this reconstruction and removing listener stuff 
		//i added this in to fix a bug, but I think this was unrelated and can be cleaned up
		//it would require some pretty extensive testing though, so i'm leaving this in to be safe for now
		if(worker != null)
		{
			worker.cancel(true);
			if(propertyListener != null)
				worker.removePropertyChangeListener(propertyListener);
		}
		worker = new ScrapeAmalgamatedMovieWorker(allAmalgamationOrderingPreferences, scraperGroupAmalgamationPreference,
				guiMain.getCurrentlySelectedMovieFileList().get(currentFileIndexToScrape), this);
		propertyListener = new AmalgamationPropertyChangeListener();
		worker.addPropertyChangeListener(propertyListener);
		worker.execute();
	}
	
	/**
	 * Scrapes the next item (returns true) or returns false if there is no next item to scrape
	 * @return true if we scraped an item, false if we didn't
	 */
	public boolean scrapeNextItemIfNeeded()
	{
		
		
		if(filesAreSelected() && (currentFileIndexToScrape+1) < guiMain.getCurrentlySelectedMovieFileList().size())
		{
			currentFileIndexToScrape++;
			reinitializeScrapingForNextMovie();
			scrapeWithIndex(currentFileIndexToScrape);
			return true;
		}
		return false;
	}
	
	public boolean filesAreSelected()
	{
		if(guiMain != null && guiMain.getCurrentlySelectedMovieFileList() != null
				&& guiMain.getCurrentlySelectedMovieFileList().size() > 0)
			return true;
		else return false;
	}
	
	@Override
	public void run(){}
	
	private class AmalgamationPropertyChangeListener implements PropertyChangeListener
	{

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

			//String propertyName = evt.getPropertyName();
			//System.out.println("property changed with name = " + propertyName + " and value = " + evt.getNewValue());
			if(evt.getPropertyName().equals("progress"))
			{
				int progressAmount = (int)evt.getNewValue();
				progressBar.setValue(progressAmount);
			}
			if(evt.getPropertyName().equals(ScrapeAmalgamatedMovieWorkerProperty.ALL_SCRAPES_FINISHED.toString()))
			{
				if(currentAmalgamatedMovie == null)
				{
					guiMain.movieToWriteToDiskList.add(currentAmalgamatedMovie);
				}
				else if(guiMain != null)
				{
					promptUserToPickPoster();
					promptUserToPickFanart();
					
					guiMain.getFileDetailPanel().setNewMovie(currentAmalgamatedMovie, true, true);
					//I really shouldn't have to add this again here as setting the new movie above should do this, but that code is currently weirdly written and sometimes doesn't set it, so I've put an extra check here
					if(!guiMain.movieToWriteToDiskList.contains(currentAmalgamatedMovie))
						guiMain.movieToWriteToDiskList.add(currentAmalgamatedMovie);
				}
				//scrape the next selected item
				boolean weAreDone = !scrapeNextItemIfNeeded();
				if(weAreDone)
				{
					//renable the main gui and close our scraping dialog
					if(guiMain != null)
					{
						guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
						ScrapeAmalgamatedProgressDialog.this.guiMain.setMainGUIEnabled(true);
						ScrapeAmalgamatedProgressDialog.this.dispose();
					}
				}
			}
			if(evt.getPropertyName().equals(ScrapeAmalgamatedMovieWorkerProperty.SCRAPED_MOVIE.toString()))
			{
				@SuppressWarnings("unchecked")
				List<Map<SiteParsingProfile,Movie>> newValue = (List<Map<SiteParsingProfile,Movie>>) evt.getNewValue();
				newValue.removeAll(Collections.singleton(null));
				for(Map<SiteParsingProfile, Movie> currentMap : newValue)
				{
					List<Movie> currentMovies = new ArrayList<Movie>(currentMap.values());
					currentMovies.removeAll(Collections.singleton(null));
					currentMovieList.addAll(currentMovies);
				}
				updateAmalgamatedMovie();
			}
		}
		
	}

	public void updateAmalgamatedMovie() {
		if(currentMovieList != null && currentMovieList.size() > 0)
		{
			//we construct a new one instead of using our local variable to get around bug where changes to amalgamation settings were not getting picked up until program restart
			ScraperGroupAmalgamationPreference prefToUse = guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(scraperGroupAmalgamationPreference.getScraperGroupName());
			MovieScrapeResultGroup scrapedResultGroup = new MovieScrapeResultGroup(currentMovieList, prefToUse);
			
			currentAmalgamatedMovie = scrapedResultGroup.amalgamateMovie();
			
			//System.out.println("Amalgamated movie of " + currentMovieList + " is " + currentAmalgamatedMovie);
		}
		
	}
	
	/**
	 * Displays a dialog where the user can pick the poster to use. Only shows when there is more than one poster.
	 */
	private void promptUserToPickPoster() {
		if (showPosterPicker && currentAmalgamatedMovie != null && currentAmalgamatedMovie.getPosters() != null
				&& currentAmalgamatedMovie.getPosters().length > 1) {
			Thumb posterFromUserSelection = showArtPicker(currentAmalgamatedMovie.getPosters(),
					"Pick Poster",true);
			currentAmalgamatedMovie.moveExistingPosterToFront(posterFromUserSelection);
		}
	}
	
	/**
	 * Displays a dialog where the user can pick the fanart to use. Only shows when there is more than one fanart.
	 */
	private void promptUserToPickFanart() {
		if (showFanartPicker && currentAmalgamatedMovie != null && currentAmalgamatedMovie.getFanart() != null
				&& currentAmalgamatedMovie.getFanart().length > 1) {
			Thumb fanartFromUserSelection = showArtPicker(currentAmalgamatedMovie.getFanart(),
					"Pick Fanart",false);
			currentAmalgamatedMovie.moveExistingFanartToFront(fanartFromUserSelection);
		}
	}

	public static Thumb showArtPicker(Thumb [] thumbArray, String windowTitle, boolean isForPoster)
	{
		if(thumbArray.length > 0)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			/*JList<Thumb> labelList = new JList<Thumb>(thumbArray);
			labelList.setCellRenderer(new FanartPickerRenderer());
			labelList.setVisible(true);
			JScrollPane pane = new JScrollPane(labelList);
			panel.add(pane, BorderLayout.CENTER);
			*/
			JPanel thumbPane = new JPanel(new ModifiedFlowLayout());
			AsyncImageComponent[] thumbPanels = new AsyncImageComponent[thumbArray.length];
			boolean doAutoSelect = true;
			for(int i = 0; i < thumbArray.length; i++)
			{
				
				thumbPanels[i] = new AsyncImageComponent(thumbArray[i],false, thumbPanels, doAutoSelect, isForPoster, true);
				thumbPane.add(thumbPanels[i]);
			}
			JScrollPane thumbScroller = new JScrollPane(thumbPane);
			panel.add(thumbScroller, BorderLayout.CENTER);
			panel.setPreferredSize(new Dimension(325,600));

			final JDialog bwin = new JDialog();
			bwin.addWindowFocusListener(new WindowFocusListener()
			{
				@Override
				public void windowLostFocus(WindowEvent e)
				{
					bwin.setVisible(false);
					bwin.dispose();
				}

				@Override
				public void windowGainedFocus(WindowEvent e)
				{
				}
			}); 
			bwin.add(panel);
			bwin.pack();

			int result = JOptionPane.showOptionDialog(null, panel, windowTitle,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);
			if(result == JOptionPane.OK_OPTION)
			{
				//get the selected item's thumb
				Thumb optionPickedFromPanel = null;
				for(int i = 0; i < thumbPanels.length; i++)
				{
					if(thumbPanels[i].isSelected())
					{
						optionPickedFromPanel = thumbPanels[i].getThumb();
						System.out.println("returning option picked from panel = " + optionPickedFromPanel);
						return optionPickedFromPanel;
					}
				}
				
			}
			else return null;
		}
		return null;
	}
	
	public static boolean showPromptForUserProvidedURL(SiteParsingProfile siteScraper, File fileToScrape)
	{
		boolean promptUserForURLWhenScraping = MoviescraperPreferences.getInstance().getPromptForUserProvidedURLWhenScraping();
		boolean chooseSearchResult = MoviescraperPreferences.getInstance().getSelectSearchResultManuallyWhenScraping();
		boolean wasCustomURLSet = false;
		if(promptUserForURLWhenScraping)
		{
			System.out.println("Prompting the user for a url for " + siteScraper);
			 String userProvidedURL = (String)JOptionPane.showInputDialog(
		             null,
		             "Enter URL of " + siteScraper.toString() + " to scrape from:" + "\n" + 
		             "fileName" + " :",
		             "Scrape from this URL...",
		             JOptionPane.PLAIN_MESSAGE,
		             null,
		             null,
		             null);
			
		
			 if(userProvidedURL != null && userProvidedURL.length() > 0)
			 {
				 //TODO: validate this is a actually a URL and display an error message if it is not
				 //also maybe don't let them click OK if isn't a valid URL?
				 try{
					 URL isAValidURL = new URL(userProvidedURL);
					 siteScraper.setOverridenSearchResult(isAValidURL.toString());
					 wasCustomURLSet = true;
				 }
				 catch(MalformedURLException e)
				 {
					 e.printStackTrace();
				 }
				 
			 }
		}
		//if the user doesn't choose their URL manually and they have the preference set to allow picking of search results,
		//give the user the ability to pick one
		if(chooseSearchResult && !wasCustomURLSet)
		{
			System.out.println("Prompt user for search results for " + siteScraper);
			String searchString = siteScraper
					.createSearchString(fileToScrape);
			try {
				SearchResult[] searchResults = siteScraper.getSearchResults(searchString);
				if(searchResults != null && searchResults.length > 0)
				{
					SearchResult searchResultFromUser = showSearchResultPicker(searchResults,
									siteScraper.getDataItemSourceName());
					if(searchResultFromUser != null)
					{
						siteScraper.setOverridenSearchResult(searchResultFromUser.getUrlPath());
						wasCustomURLSet = true;
					}
					else
					{
						//User hit cancel, do not scrape from this scraper
						System.out.println("Discarding results from " + siteScraper.getDataItemSourceName());
						siteScraper.setDiscardResults(true);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return wasCustomURLSet;
	}
	
	/**
	 * Display a list of option dialog where the user can choose between various search results
	 * @param searchResults - search results to display
	 * @param siteName - Title to show in window
	 * @return - the search result the user picks or null if the user picks the cancel option
	 */
	public static SearchResult showSearchResultPicker(SearchResult [] searchResults, String siteName)
	{
		if(searchResults.length > 0)
		{

			SelectionDialog selectionDialog = new SelectionDialog(searchResults, siteName);
			Object[] choices = {"OK", "Skip Scraping From This Site"};
			Object defaultChoice = choices[0];
			int optionPicked = JOptionPane.showOptionDialog(null, selectionDialog, "Select Search Result for " + siteName,
					JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, choices, defaultChoice);
			if(optionPicked == JOptionPane.CANCEL_OPTION)
				return null;
			return selectionDialog.getSelectedValue();
		}
		else return null;
	}
	
	
	

}
