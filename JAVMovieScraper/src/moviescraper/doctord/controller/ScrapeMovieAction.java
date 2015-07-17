package moviescraper.doctord.controller;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import moviescraper.doctord.controller.amalgamation.MovieScrapeResultGroup;
import moviescraper.doctord.controller.amalgamation.ScraperGroupAmalgamationPreference;
import moviescraper.doctord.controller.siteparsingprofile.IAFDParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.controller.siteparsingprofile.specific.ActionJavParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18WebContentParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.DmmParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavZooParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.R18ParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.SquarePlusParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.view.GUIMain;
import moviescraper.doctord.view.renderer.FanartPickerRenderer;

import org.apache.commons.lang3.ArrayUtils;
/**
 * Deprecated - As of v0.2.00-alpha use ScrapeAmalgamationAction instead
 */
@Deprecated
public class ScrapeMovieAction extends AbstractAction {
	/**
	 * 
	 */
	private final GUIMain guiMain;
	String overrideURLDMM;
	String overrideURLJavLibrary;
	String overrideURLData18Movie;
	String overrideURLIAFD;
	private static final long serialVersionUID = 1L;
	boolean promptUserForURLWhenScraping; //do we stop to ask the user to pick a URL when scraping
	boolean scrapeJAV = true;
	boolean scrapeData18Movie = false;
	boolean scrapeData18WebContent = false;
	boolean manuallyPickFanart = true;
	boolean manuallyPickPoster = true;
	int progress;
	int amountOfProgressPerSubtask;
	SwingWorker<Void, String> worker;
	List<Thread> scrapeThreads;
	boolean scrapeCanceled;


	public static final String SCRAPE_KEY = "SCRAPE_KEY";


	public ScrapeMovieAction(GUIMain guiMain) {
		this.guiMain = guiMain;
		putValue(SCRAPE_KEY, getClass().getName());
		putValue(NAME, "Scrape JAV");
		putValue(SHORT_DESCRIPTION, "Scrape Selected Movie");
		overrideURLDMM = "";
		overrideURLJavLibrary = "";
		overrideURLData18Movie = "";
		promptUserForURLWhenScraping = true;
		progress = 0;
		amountOfProgressPerSubtask = 0;
		scrapeCanceled = false;
	}
	
	/**
	 * 
	 * @param parsingProfile - item to check if scraping is enabled for this parsing profile
	 * @return true if scraper should scrape for parsingProfile, false otherwise
	 */
	protected boolean shouldScrapeThread(DataItemSource parsingProfile) {
		for (ScraperGroupName currentName : ScraperGroupName.values()) {
			ScraperGroupAmalgamationPreference currentPref = this.guiMain
					.getAllAmalgamationOrderingPreferences()
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

	public void makeProgress(int amount, String note)
	{
		if(this.guiMain.getProgressMonitor().isCanceled())
		{
			cancelRunningThreads();
			return;
		}
		if(progress < 100)
		{

			if(amount + progress <= 100)
			{
				progress += amount;
			}
			else
			{
				progress = 100;
			}
			
			this.guiMain.getProgressMonitor().update(progress, note);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		resetScrapeMovieActionCounters();

		this.guiMain.setMainGUIEnabled(false);
		// this takes a while to do, so set the cursor to busy

		// clear out all old values of the scraped movie
		this.guiMain.removeOldScrapedMovieReferences();
		clearOverrides();
		
		worker = new SwingWorker<Void, String>() {

			Movie javMovie = null;
			Movie data18Movie = null;

			@Override
			protected Void doInBackground() {

				for (int movieNumberInList = 0; movieNumberInList < ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
						.size(); movieNumberInList++) {
					if (this.isCancelled()) {
						System.err.println("Found the thread was canceled");
						cancelRunningThreads();
						return null;
					}
					final int movieNumberInListFinal = movieNumberInList;
					// set the cursor to busy as this may take a while
					ScrapeMovieAction.this.guiMain.getFrmMoviescraper().setCursor(Cursor
							.getPredefinedCursor(Cursor.WAIT_CURSOR));
					// We don't want to block the UI while waiting for a
					// time consuming
					// scrape, so make new threads for each scraping
					// query
					String currentFileName = ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
							.get(movieNumberInList).toString();

					initializeProgressMonitor(currentFileName, movieNumberInList, ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().size());

					if (promptUserForURLWhenScraping && scrapeJAV) {
						// bring up some dialog boxes so the user can
						// choose what URL to use for each site
						try {
							DmmParsingProfile dmmParsingProfile = new DmmParsingProfile(
									!ScrapeMovieAction.this.guiMain.getPreferences().getScrapeInJapanese());
							if(guiMain.getPreferences().getPromptForUserProvidedURLWhenScraping())
							{
								setOverridenSearchResult(dmmParsingProfile, ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
										.get(movieNumberInList).toString());

							}
							if(dmmParsingProfile.getOverridenSearchResult() != null)
							{
								overrideURLDMM = dmmParsingProfile.getOverridenSearchResult().toString();
							}
							else
							{
								String searchStringDMM = dmmParsingProfile
										.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
												.get(movieNumberInList));
								SearchResult[] searchResultsDMM = dmmParsingProfile
										.getSearchResults(searchStringDMM);
								if (searchResultsDMM != null
										&& searchResultsDMM.length > 0) {
									SearchResult searchResultFromUser = GUIMain
											.showOptionPane(searchResultsDMM,
													"dmm.co.jp");
									if (searchResultFromUser != null)
										overrideURLDMM = searchResultFromUser.getUrlPath();
									else
									{
										guiMain.movieToWriteToDiskList.add(null);
										clearOverrides();
									}
								}

							}
							// don't read from jav library if we're
							// scraping in japanese since that site is
							// only useful for english lang content
							if (!ScrapeMovieAction.this.guiMain.getPreferences().getScrapeInJapanese()) {
								JavLibraryParsingProfile javLibParsingProfile = new JavLibraryParsingProfile();
								if(guiMain.getPreferences().getPromptForUserProvidedURLWhenScraping())
								{
									setOverridenSearchResult(javLibParsingProfile, ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
											.get(movieNumberInList).toString());

								}
								if(javLibParsingProfile.getOverridenSearchResult() != null)
								{
									overrideURLJavLibrary = javLibParsingProfile.getOverridenSearchResult().toString();
								}
								else
								{
									String searchStringJL = javLibParsingProfile
											.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
													.get(movieNumberInList));
									SearchResult[] searchResultsJavLibStrings = javLibParsingProfile
											.getSearchResults(searchStringJL);
									if (searchResultsJavLibStrings != null
											&& searchResultsJavLibStrings.length > 0) {
										SearchResult searchResultFromUser = GUIMain
												.showOptionPane(
														searchResultsJavLibStrings,
														"javlibrary.com");
										if (searchResultFromUser != null)
											overrideURLJavLibrary = searchResultFromUser.getUrlPath();
									}
								}
							}
							//if we hit cancel twice while scraping, just go on to the next movie and don't scrape
							if (overrideURLDMM == null && overrideURLJavLibrary == null)
							{
								guiMain.movieToWriteToDiskList.add(null);
								clearOverrides();
								continue;
							}
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}

					else if (promptUserForURLWhenScraping
							&& (scrapeData18Movie || scrapeData18WebContent)) {
						try {
							SiteParsingProfile data18ParsingProfile = null;
							if (scrapeData18Movie)
								data18ParsingProfile = new Data18MovieParsingProfile();
							else if (scrapeData18WebContent)
								data18ParsingProfile = new Data18WebContentParsingProfile();
							if(guiMain.getPreferences().getPromptForUserProvidedURLWhenScraping())
							{
								setOverridenSearchResult(data18ParsingProfile, ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
										.get(movieNumberInList).toString());
							}
							if(data18ParsingProfile.getOverridenSearchResult() != null)
							{
								overrideURLData18Movie = data18ParsingProfile.getOverridenSearchResult().toString();
							}
							else
							{
								String searchStringData18Movie = data18ParsingProfile
										.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
												.get(movieNumberInList));
								SearchResult[] searchResultData18Movie = data18ParsingProfile
										.getSearchResults(searchStringData18Movie);
								if (searchResultData18Movie != null
										&& searchResultData18Movie.length > 0) {
									SearchResult searchResultFromUser = GUIMain
											.showOptionPane(
													searchResultData18Movie,
													"Data18 Movie");
									if (searchResultFromUser == null){
										guiMain.movieToWriteToDiskList.add(null);
										clearOverrides();
										continue;
									}
									overrideURLData18Movie = searchResultFromUser
											.getUrlPath();
								}

							}

							if (scrapeData18Movie
									&& ScrapeMovieAction.this.guiMain.getPreferences().getUseIAFDForActors()) {
								IAFDParsingProfile iafdParsingProfile = new IAFDParsingProfile();
								SearchResult[] searchResultsIAFD = iafdParsingProfile
										.getSearchResults(iafdParsingProfile
												.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
														.get(movieNumberInList)));
								//System.out.println(searchStringData18Movie);
								System.out.println(searchResultsIAFD);

								if (searchResultsIAFD != null
										&& searchResultsIAFD.length > 0) {
									SearchResult searchResultFromUser = GUIMain
											.showOptionPane(
													searchResultsIAFD,
													"Data18 Movie");
									if (searchResultFromUser == null)
									{
										guiMain.movieToWriteToDiskList.add(null);
										clearOverrides();
										continue;
									}
									overrideURLIAFD = searchResultFromUser
											.getUrlPath();
									if (!overrideURLIAFD
											.contains("iafd.com"))
										overrideURLIAFD = "http://www.iafd.com/"
												+ overrideURLIAFD;
									System.out.println("Neue URL "
											+ overrideURLIAFD);

								}
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					try {
						if (scrapeJAV)
							javMovie = makeJavThreadsAndScrape(movieNumberInListFinal);
						else if (scrapeData18Movie)
							data18Movie = makeData18MovieThreadsAndScrape(
									movieNumberInListFinal, true);
						else if (scrapeData18WebContent) {
							data18Movie = makeData18MovieThreadsAndScrape(
									movieNumberInListFinal, false);
						}
						clearOverrides();
					} catch (InterruptedException e) {
						e.printStackTrace();
						clearOverrides();
					}
					// finish up the progress monitor for the current scraping
					makeProgress(100, "Done!"); 
				}

				return null;
			}

			@Override
			protected void done() {
				
				 guiMain.getProgressMonitor().stop();
				// Allow the user to manually pick poster from a dialog
				// box for data18 movies
				if (manuallyPickPoster && data18Movie != null
						&& data18Movie.getPosters() != null
						&& data18Movie.getPosters().length > 1) {
					// get all unique elements from the posters and the
					// extrafanart - my method here is probably pretty
					// inefficient, but the lists aren't more than 100
					// items, so no big deal
					HashSet<Thumb> uniqueElements = new HashSet<Thumb>(
							Arrays.asList(data18Movie.getPosters()));
					uniqueElements.addAll(Arrays.asList(data18Movie
							.getExtraFanart()));
					ArrayList<Thumb> uniqueElementsList = (new ArrayList<Thumb>(
							uniqueElements));
					Thumb[] uniqueElementsArray = uniqueElementsList
							.toArray(new Thumb[uniqueElementsList.size()]);

					Thumb posterPicked = showArtPicker(uniqueElementsArray,
							"Pick Poster");
					if (posterPicked != null) {
						// remove the item from the picked from the
						// existing poster and put it at the front of
						// the list
						ArrayList<Thumb> existingPosters = new ArrayList<Thumb>(
								Arrays.asList(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie()
										.getPosters()));
						existingPosters.remove(posterPicked);
						existingPosters.add(0, posterPicked);
						Thumb[] posterArray = new Thumb[existingPosters
						                                .size()];
						ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie()
						.setPosters(existingPosters
								.toArray(posterArray));
					}
				}

				// Allow the user to manually pick fanart from a dialog
				// box
				if (manuallyPickFanart && data18Movie != null
						&& data18Movie.getFanart() != null
						&& data18Movie.getFanart().length > 1) {
					// get all unique elements from the fanart and the
					// extrafanart - my method here is probably pretty
					// inefficient, but the lists aren't more than 100
					// items, so no big deal
					HashSet<Thumb> uniqueElements = new HashSet<Thumb>(
							Arrays.asList(data18Movie.getFanart()));
					uniqueElements.addAll(Arrays.asList(data18Movie
							.getExtraFanart()));
					ArrayList<Thumb> uniqueElementsList = (new ArrayList<Thumb>(
							uniqueElements));
					Thumb[] uniqueElementsArray = uniqueElementsList
							.toArray(new Thumb[uniqueElementsList.size()]);

					Thumb fanartPicked = showArtPicker(uniqueElementsArray,
							"Pick Fanart");
					if (fanartPicked != null) {
						// remove the item from the picked from the
						// existing fanart and put it at the front of
						// the list
						ArrayList<Thumb> existingFanart = new ArrayList<Thumb>(
								Arrays.asList(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie()
										.getFanart()));
						existingFanart.remove(fanartPicked);
						existingFanart.add(0, fanartPicked);
						Thumb[] fanartArray = new Thumb[existingFanart
						                                .size()];
						ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie()
						.setFanart(existingFanart
								.toArray(fanartArray));
					}
				}

				else if (manuallyPickFanart && javMovie != null) {
					// we don't need to worry about picking out the
					// unique elements like above since there is no
					// duplication between fanart and extrafanart in
					// this case
					Thumb fanartPicked = showArtPicker(
							ArrayUtils.addAll(javMovie.getFanart(),
									javMovie.getExtraFanart()),
							"Pick Fanart");
					if (fanartPicked != null)
						javMovie.setFanart(ArrayUtils.toArray(fanartPicked));
				}

				if (!scrapeCanceled
						&& (ScrapeMovieAction.this.guiMain.movieToWriteToDiskList == null || ScrapeMovieAction.this.guiMain.movieToWriteToDiskList
						.size() == 0)) {
					System.out.println("No movie result found");
					JOptionPane
					.showMessageDialog(
							ScrapeMovieAction.this.guiMain.getFrmMoviescraper(),
							"Could not find any movies that match the selected file while scraping.",
							"No Movies Found",
							JOptionPane.ERROR_MESSAGE, null);
				}

				clearOverrides();
				// by calling this with the parameter of true, we'll
				// force a refresh from the URL not just update the
				// poster from the file on disk
				ScrapeMovieAction.this.guiMain.updateAllFieldsOfFileDetailPanel(true, false);
				ScrapeMovieAction.this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
				ScrapeMovieAction.this.guiMain.setMainGUIEnabled(true);
				
				System.out.println("Scraping completed");
			}

		};


		worker.execute();
	}

	//Sets the scraper action's fields back to a pristine state so that
	//leftover state from the last state is not still used
	private void resetScrapeMovieActionCounters() {
		scrapeCanceled = false;
		progress = 0;
		amountOfProgressPerSubtask = 0;
	}


	protected void handleCancelWorker() {
		scrapeCanceled = true;
		this.guiMain.removeOldScrapedMovieReferences();
		worker.cancel(true);
		this.guiMain.setMainGUIEnabled(true);


	}

	private void initializeProgressMonitor(String fileName, int currentIndex, int totalItems) {
		String text;

		if (totalItems == 1)
			text = "Scraping Movie: " + fileName;
		else
			text = String.format("Scraping Movie %d of %d: %s", currentIndex + 1, totalItems, fileName);

		this.guiMain.getProgressMonitor().start(text);
		progress = 0;

	}


	private void clearOverrides() {
		this.overrideURLData18Movie = "";
		this.overrideURLDMM = "";
		this.overrideURLJavLibrary = "";

	}

	//Selection Dialog box used to display posters and fanarts
	public static Thumb showArtPicker(Thumb [] thumbArray, String windowTitle)
	{
		if(thumbArray.length > 0)
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			JList<Thumb> labelList = new JList<Thumb>(thumbArray);
			labelList.setCellRenderer(new FanartPickerRenderer());
			labelList.setVisible(true);
			JScrollPane pane = new JScrollPane(labelList);
			panel.add(pane, BorderLayout.CENTER);
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
				Thumb optionPickedFromPanel = labelList.getSelectedValue();
				return optionPickedFromPanel;
			}
			else return null;
		}
		else return null;
	}


	private Movie makeData18MovieThreadsAndScrape(int movieNumberInList, boolean isData18Movie) throws InterruptedException {
		//we need to create a final copy of the loop variable to pass it into each run method and make the compiler happy
		final int currentMovieNumberInList = movieNumberInList;
		final boolean parsingType = isData18Movie;
		final int numberOfThreads;
		if(this.guiMain.getPreferences().getUseIAFDForActors())
			numberOfThreads = 2;
		else numberOfThreads = 1;
		final int amountOfProgressToMakePerThread = (100 / numberOfThreads) - 1;
		amountOfProgressPerSubtask = amountOfProgressToMakePerThread;
		final ScrapeMovieAction thisScrapeAction = this;
		final String overriddenURL = overrideURLData18Movie;

		Thread scrapeQueryData18MovieThread = new Thread() {
			@Override
			public void run() {
				try {
					SiteParsingProfile data18MoviePP;
					if(parsingType)
						data18MoviePP = new Data18MovieParsingProfile();
					else
						data18MoviePP = new Data18WebContentParsingProfile();
					if(overriddenURL != null && overriddenURL.length() >0)
						data18MoviePP.setOverridenSearchResult(overriddenURL);
					//data18MoviePP.setExtraFanartScrapingEnabled(preferences.getExtraFanartScrapingEnabledPreference());
					ScrapeMovieAction.this.guiMain.debugWriter("Scraping this file (Data18) " + ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList));
					ScrapeMovieAction.this.guiMain
							.setCurrentlySelectedMovieData18Movie(Movie.scrapeMovie(
									ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
									data18MoviePP, overrideURLData18Movie, promptUserForURLWhenScraping, thisScrapeAction));

					System.out.println("Data18 Scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie());

					if ( ScrapeMovieAction.this.guiMain.getPreferences().getUseIAFDForActors() && ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie() != null) {
						Movie scrapeMovieIAFD = Movie.scrapeMovie(
								ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
								new IAFDParsingProfile(), overrideURLIAFD, promptUserForURLWhenScraping, thisScrapeAction);
						System.out.println("IAFD Scrape results: "
								+ scrapeMovieIAFD);
						
						ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieData18Movie().setActors( scrapeMovieIAFD.getActors() );
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};


		scrapeQueryData18MovieThread.start();
		scrapeQueryData18MovieThread.join();
		if(this.guiMain.getCurrentlySelectedMovieData18Movie() == null)
			System.out.print("No results found for file: " + 
					guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList).getName());
			
		// Add null results too
		this.guiMain.movieToWriteToDiskList.add(this.guiMain.getCurrentlySelectedMovieData18Movie());

		return this.guiMain.getCurrentlySelectedMovieData18Movie();
	}


	private Movie makeJavThreadsAndScrape(int movieNumberInList) throws InterruptedException {
		//we need to create a final copy of the loop variable to pass it into each run method and make the compiler happy
		final int currentMovieNumberInList = movieNumberInList;
		Movie movieAmalgamated = null;



		final ScrapeMovieAction thisScrapeAction = this;
		Thread scrapeQueryDMMThread = new Thread("DMM") {
			@Override
			public void run() {
				try {
					DmmParsingProfile dmmPP = new DmmParsingProfile(!ScrapeMovieAction.this.guiMain.getPreferences().getScrapeInJapanese());
					dmmPP.setExtraFanartScrapingEnabled(ScrapeMovieAction.this.guiMain.getPreferences().getExtraFanartScrapingEnabledPreference());
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieDMM(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							dmmPP, overrideURLDMM, promptUserForURLWhenScraping, thisScrapeAction));
					System.out.println("DMM scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieDMM());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e1),"Unhandled Exception",JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		// Scrape ActionJav.com for currently selected movie
		Thread scrapeQueryActionJavThread = new Thread("ActionJav") {
			@Override
			public void run() {
				try {
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieActionJav(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							new ActionJavParsingProfile(), overrideURLDMM, false, thisScrapeAction));

					System.out.println("Action jav scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieActionJav());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};

		// Scrape SquarePlus.co.jp for currently selected movie
		Thread scrapeQuerySquarePlusThread = new Thread("SquarePlus") {
			@Override
			public void run() {
				try {
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieSquarePlus(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							new SquarePlusParsingProfile(), overrideURLDMM, false, thisScrapeAction));

					System.out.println("SquarePlus scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieSquarePlus());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};

		// Scrape JavLibrary for currently selected movie
		Thread scrapeQueryJavLibraryThread = new Thread("JavLibrary") {
			@Override
			public void run() {
				try {
					JavLibraryParsingProfile jlParsingProfile = new JavLibraryParsingProfile();
					jlParsingProfile.setOverrideURLJavLibrary(overrideURLJavLibrary);
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieJavLibrary(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							jlParsingProfile, overrideURLDMM, promptUserForURLWhenScraping, thisScrapeAction));

					System.out.println("JavLibrary scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieJavLibrary());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};
		
		//Scrape Javzoo for currently selected movie
		Thread scrapeQueryJavZooThread = new Thread("JavZoo") {
			@Override
			public void run() {
				try {
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieJavZoo(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							new JavZooParsingProfile(), overrideURLDMM, false, thisScrapeAction));

					System.out.println("JavZoo scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieJavZoo());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};
		
		//Scrape R18 for currently selected movie
		Thread scrapeQueryR18Thread = new Thread("R18.com") {
			@Override
			public void run() {
				try {
					ScrapeMovieAction.this.guiMain.setCurrentlySelectedMovieR18(Movie.scrapeMovie(
							ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
							new R18ParsingProfile(), overrideURLDMM, false, thisScrapeAction));

					System.out.println("R18 scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieR18());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};



		
		scrapeThreads = new LinkedList<Thread>();
		
		if (shouldScrapeThread(new DmmParsingProfile()) || this.guiMain.getPreferences().getScrapeInJapanese())
			scrapeThreads.add(scrapeQueryDMMThread);
		
		if(!this.guiMain.getPreferences().getScrapeInJapanese())
		{
			if (shouldScrapeThread(new ActionJavParsingProfile())) {
				scrapeThreads.add(scrapeQueryActionJavThread);
			}
			if (shouldScrapeThread(new SquarePlusParsingProfile())) {
				
				scrapeThreads.add(scrapeQuerySquarePlusThread);
			}
			if (shouldScrapeThread(new JavLibraryParsingProfile())) {
				
				scrapeThreads.add(scrapeQueryJavLibraryThread);
			}
			if (shouldScrapeThread(new JavZooParsingProfile())) {
				
				scrapeThreads.add(scrapeQueryJavZooThread);
			}
			if (shouldScrapeThread(new R18ParsingProfile())) {
				scrapeThreads.add(scrapeQueryR18Thread);
			}
		}
		
		final int numberOfThreads = scrapeThreads.size();

		final int amountOfProgressToMakePerThread = (100 / numberOfThreads) - 1;
		amountOfProgressPerSubtask = amountOfProgressToMakePerThread;



		// Run all the threads in parallel
		
		for(Thread t: scrapeThreads)
			t.start();
		
		// wait for them to finish before updating gui
		
		for(Thread t: scrapeThreads){
			t.join();

			System.out.println(t.getName() + " thread complete");
			
			if(anyThreadWasInterrupted()) {
				System.err.println("Something was interrupted");
				cancelRunningThreads();
				return null;
			}
		}
		
		System.out.println("Now amalgamating movie from all different sources...");
		if(this.guiMain.getPreferences().getScrapeInJapanese())
			movieAmalgamated = this.guiMain.getCurrentlySelectedMovieDMM();
		else{
			List<Movie> scrapedMovies = new LinkedList<Movie>();
			if (this.guiMain.getCurrentlySelectedMovieDMM() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieDMM());
			
			if (this.guiMain.getCurrentlySelectedMovieActionJav() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieActionJav());
			
			if (this.guiMain.getCurrentlySelectedMovieSquarePlus() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieSquarePlus());
			
			if (this.guiMain.getCurrentlySelectedMovieJavLibrary() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieJavLibrary());
			
			if (this.guiMain.getCurrentlySelectedMovieJavZoo() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieJavZoo());
			
			if (this.guiMain.getCurrentlySelectedMovieR18() != null)
				scrapedMovies.add(this.guiMain.getCurrentlySelectedMovieR18());
			
			ScraperGroupAmalgamationPreference javPrefs = this.guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP);
			
			MovieScrapeResultGroup scrapedResultGroup = new MovieScrapeResultGroup(scrapedMovies, javPrefs);
			
			movieAmalgamated = scrapedResultGroup.amalgamateMovie();
		}
		//if we didn't get a result from the general jav db's, then maybe this is from a webonly type scraper
		if(movieAmalgamated == null && this.guiMain.getCurrentlySelectedMovieCaribbeancomPremium() != null)
			movieAmalgamated = this.guiMain.getCurrentlySelectedMovieCaribbeancomPremium();

		if (movieAmalgamated == null)
			System.out.print("No results found for file: " + 
				guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList).getName());
		
		// Add null results too
		this.guiMain.movieToWriteToDiskList.add(movieAmalgamated);
		
		return movieAmalgamated;
	}

	private boolean anyThreadWasInterrupted() {
		for(Thread currentThread : scrapeThreads)
		{
			if(currentThread.isInterrupted())
				return true;
		}
		if(this.guiMain.getProgressMonitor().isCanceled())
			return true;
		return false;
	}

	private void cancelRunningThreads()
	{
		if(!scrapeCanceled )
		{
			for (Thread currentThread : scrapeThreads)
			{
				currentThread.interrupt();
			}
			handleCancelWorker();
		}
	}

	public int getAmountOfProgressPerSubtask() {
		return amountOfProgressPerSubtask;
	}
	
	/**
	 * Displays a dialog where the user can enter in a specific url to scrape from
	 * Does some minimal checking on the input before setting the overriden search result
	 * @param spp the {@link SiteParsingProfile} object to set the overriden search result on
	 */
	public static void setOverridenSearchResult(SiteParsingProfile spp, String fileName)
	{
		 String userProvidedURL = (String)JOptionPane.showInputDialog(
	             null,
	             "Enter URL of " + spp.toString() + " to scrape from:" + "\n" + 
	             fileName + " :",
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
				 spp.setOverridenSearchResult(isAValidURL.toString());
			 }
			 catch(MalformedURLException e)
			 {
				 e.printStackTrace();
				 return;
			 }
			 
		 }
	}

}
