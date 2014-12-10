package moviescraper.doctord.controller;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import moviescraper.doctord.Movie;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.GUI.renderer.FanartPickerRenderer;
import moviescraper.doctord.SiteParsingProfile.ActionJavParsingProfile;
import moviescraper.doctord.SiteParsingProfile.Data18MovieParsingProfile;
import moviescraper.doctord.SiteParsingProfile.Data18WebContentParsingProfile;
import moviescraper.doctord.SiteParsingProfile.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.IAFDParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavLibraryParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavZooParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SquarePlusParsingProfile;
import moviescraper.doctord.SiteParsingProfile.specific.CaribbeancomPremiumParsingProfile;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

import org.apache.commons.lang3.ArrayUtils;

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




	public ScrapeMovieAction(GUIMain guiMain) {
		this.guiMain = guiMain;
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
				//firePropertyChange("progress", oldProgress, progress);
				this.guiMain.getProgressMonitor().setProgress(progress);
				this.guiMain.getProgressMonitor().setNote("Completed " + progress + "% -  " + note);
			}
			else
			{
				progress = 100;
				this.guiMain.getProgressMonitor().setNote("Completed " + progress + " %: " + note);
				this.guiMain.getProgressMonitor().setProgress(progress);
			}
		}
	}

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

					initializeProgressMonitor(currentFileName);

					if (promptUserForURLWhenScraping && scrapeJAV) {
						// bring up some dialog boxes so the user can
						// choose what URL to use for each site
						try {
							DmmParsingProfile dmmPP = new DmmParsingProfile(
									!ScrapeMovieAction.this.guiMain.getPreferences().getScrapeInJapanese());
							JavLibraryParsingProfile jlPP = new JavLibraryParsingProfile();
							String searchStringDMM = dmmPP
									.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
											.get(movieNumberInList));
							SearchResult[] searchResultsDMM = dmmPP
									.getSearchResults(searchStringDMM);
							String searchStringJL = jlPP
									.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
											.get(movieNumberInList));
							if (searchResultsDMM != null
									&& searchResultsDMM.length > 0) {
								SearchResult searchResultFromUser = GUIMain
										.showOptionPane(searchResultsDMM,
												"dmm.co.jp");
								if (searchResultFromUser != null)
									overrideURLDMM = searchResultFromUser.getUrlPath();

							}
							// don't read from jav library if we're
							// scraping in japanese since that site is
							// only useful for english lang content
							if (!ScrapeMovieAction.this.guiMain.getPreferences().getScrapeInJapanese()) {
								SearchResult[] searchResultsJavLibStrings = jlPP
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
							//if we hit cancel twice while scraping, just go on to the next movie and don't scrape
							if (overrideURLDMM == null && overrideURLJavLibrary == null)
								continue;
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
								if (searchResultFromUser == null)
									continue;
								overrideURLData18Movie = searchResultFromUser
										.getUrlPath();

							}

							if (scrapeData18Movie
									&& ScrapeMovieAction.this.guiMain.getPreferences().getUseIAFDForActors()) {
								IAFDParsingProfile iafdParsingProfile = new IAFDParsingProfile();
								SearchResult[] searchResultsIAFD = iafdParsingProfile
										.getSearchResults(iafdParsingProfile
												.createSearchString(ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList()
														.get(movieNumberInList)));
								System.out.println(searchStringData18Movie);
								System.out.println(searchResultsIAFD);

								if (searchResultsIAFD != null
										&& searchResultsIAFD.length > 0) {
									SearchResult searchResultFromUser = GUIMain
											.showOptionPane(
													searchResultsIAFD,
													"Data18 Movie");
									if (searchResultFromUser == null)
										continue;
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
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// finish up the progress monitor for the current scraping
					makeProgress(100, "Done!"); 
				}

				return null;
			}

			@Override
			protected void done() {
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
				ScrapeMovieAction.this.guiMain.updateAllFieldsOfFileDetailPanel(true);
				ScrapeMovieAction.this.guiMain.getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
				ScrapeMovieAction.this.guiMain.setMainGUIEnabled(true);
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

	private void initializeProgressMonitor(String fileName) {
		this.guiMain.setProgressMonitor(new ProgressMonitor(this.guiMain.getFrmMoviescraper(),
				"Scraping Movie: " + fileName,
				"Completed 0%", 0, 100));
		this.guiMain.getProgressMonitor().setMillisToDecideToPopup(0);
		this.guiMain.getProgressMonitor().setMillisToPopup(0);
		progress = 0;
		this.guiMain.getProgressMonitor().setProgress(0);

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

		Thread scrapeQueryData18MovieThread = new Thread() {
			public void run() {
				try {
					SiteParsingProfile data18MoviePP;
					if(parsingType)
						data18MoviePP = new Data18MovieParsingProfile();
					else
						data18MoviePP = new Data18WebContentParsingProfile();
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
		if(this.guiMain.getCurrentlySelectedMovieData18Movie() != null)
			this.guiMain.movieToWriteToDiskList.add(this.guiMain.getCurrentlySelectedMovieData18Movie());

		return this.guiMain.getCurrentlySelectedMovieData18Movie();
	}


	private Movie makeJavThreadsAndScrape(int movieNumberInList) throws InterruptedException {
		//we need to create a final copy of the loop variable to pass it into each run method and make the compiler happy
		final int currentMovieNumberInList = movieNumberInList;
		Movie movieAmalgamated = null;
		// Scape dmm.co.jp for currently selected movie

		final int numberOfThreads;
		if(!this.guiMain.getPreferences().getScrapeInJapanese())
			numberOfThreads = 5;
		else numberOfThreads = 1;

		final int amountOfProgressToMakePerThread = (100 / numberOfThreads) - 1;
		amountOfProgressPerSubtask = amountOfProgressToMakePerThread;
		final ScrapeMovieAction thisScrapeAction = this;
		Thread scrapeQueryDMMThread = new Thread() {
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
		Thread scrapeQueryActionJavThread = new Thread() {
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
		Thread scrapeQuerySquarePlusThread = new Thread() {
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
		Thread scrapeQueryJavLibraryThread = new Thread() {
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

		Thread scrapeQueryJavZooThread = new Thread() {
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


		Thread scrapeQueryCaribbeancomPremium = new Thread() {
			public void run() {
				try {
					ScrapeMovieAction.this.guiMain
							.setCurrentlySelectedMovieCaribbeancomPremium(Movie.scrapeMovie(
									ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieFileList().get(currentMovieNumberInList),
									new CaribbeancomPremiumParsingProfile(), overrideURLDMM, false, thisScrapeAction));

					System.out.println("CaribbeancomPremium scrape results: "
							+ ScrapeMovieAction.this.guiMain.getCurrentlySelectedMovieCaribbeancomPremium());
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		};

		scrapeThreads = new ArrayList<Thread>(numberOfThreads);
		scrapeThreads.add(scrapeQueryDMMThread);
		if(!this.guiMain.getPreferences().getScrapeInJapanese())
		{
			scrapeThreads.add(scrapeQueryActionJavThread);
			scrapeThreads.add(scrapeQuerySquarePlusThread);
			scrapeThreads.add(scrapeQueryJavLibraryThread);
			scrapeThreads.add(scrapeQueryJavZooThread);
			scrapeThreads.add(scrapeQueryCaribbeancomPremium);
		}



		// Run all the threads in parallel
		scrapeQueryDMMThread.start();
		if(!this.guiMain.getPreferences().getScrapeInJapanese())
		{
			scrapeQueryActionJavThread.start();
			scrapeQuerySquarePlusThread.start();
			scrapeQueryJavLibraryThread.start();
			scrapeQueryJavZooThread.start();
			scrapeQueryCaribbeancomPremium.start();
		}

		if(anyThreadWasInterrupted())
		{
			System.err.println("Something was interrupted");
			cancelRunningThreads();
			return null;
		}
		// wait for them to finish before updating gui
		scrapeQueryDMMThread.join();
		if(!this.guiMain.getPreferences().getScrapeInJapanese())
		{
			if(anyThreadWasInterrupted())
			{
				System.err.println("Something was interrupted");
				cancelRunningThreads();
				return null;
			}
			scrapeQueryJavLibraryThread.join();
			if(anyThreadWasInterrupted())
			{
				System.err.println("Something was interrupted");
				cancelRunningThreads();
				return null;
			}
			scrapeQueryActionJavThread.join();
			if(anyThreadWasInterrupted())
			{
				System.err.println("Something was interrupted");
				cancelRunningThreads();
				return null;
			}
			scrapeQuerySquarePlusThread.join();
			if(anyThreadWasInterrupted())
			{
				System.err.println("Something was interrupted");
				cancelRunningThreads();
				return null;
			}
			scrapeQueryJavZooThread.join();
			if(anyThreadWasInterrupted())
				if(anyThreadWasInterrupted())
				{
					System.err.println("Something was interrupted");
					cancelRunningThreads();
					return null;
				}
			scrapeQueryCaribbeancomPremium.join();
		}

		if(anyThreadWasInterrupted())
		{
			System.err.println("Something was interrupted");
			cancelRunningThreads();
			return null;
		}

		if(this.guiMain.getPreferences().getScrapeInJapanese())
			movieAmalgamated = this.guiMain.getCurrentlySelectedMovieDMM();
		else{
			movieAmalgamated = amalgamateJAVMovie(this.guiMain.getCurrentlySelectedMovieDMM(),
					this.guiMain.getCurrentlySelectedMovieActionJav(),
					this.guiMain.getCurrentlySelectedMovieSquarePlus(),
					this.guiMain.getCurrentlySelectedMovieJavLibrary(),
					this.guiMain.getCurrentlySelectedMovieJavZoo(), movieNumberInList);
		}
		//if we didn't get a result from the general jav db's, then maybe this is from a webonly type scraper
		if(movieAmalgamated == null && this.guiMain.getCurrentlySelectedMovieCaribbeancomPremium() != null)
			movieAmalgamated = this.guiMain.getCurrentlySelectedMovieCaribbeancomPremium();
		if(movieAmalgamated != null)
		{
			this.guiMain.movieToWriteToDiskList.add(movieAmalgamated);
		}	
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
	 *  Look through the fields in the various scraped movies and try to automatically guess what the best data is and construct a Movie based on
	 * that. this function is only called for JAV movies
	 * @param currentlySelectedMovieDMM - movie scraped from dmm.co.jp
	 * @param currentlySelectedMovieActionJav - movie scraped from actionjav
	 * @param currentlySelectedMovieSquarePlus - movie scraped from squareplus
	 * @param currentlySelectedMovieJavLibrary - movie scraped from javlibrary
	 * @param currentlySelectedMovieJavZoo - movie scraped from javzoo
	 * @param movieNumberInList - index number of movieToWriteToDiskList to operate on
	 * @return The amalgamated movie which has all the "best" parts of each parameter
	 */
	protected Movie amalgamateJAVMovie(Movie currentlySelectedMovieDMM,
			Movie currentlySelectedMovieActionJav,
			Movie currentlySelectedMovieSquarePlus,
			Movie currentlySelectedMovieJavLibrary,
			Movie currentlySelectedMovieJavZoo,
			int movieNumberInList) {

		//Assuming we have a DMM movie, any of the other movies must contain the same ID as the DMM movie
		//if they aren't the same ID, discard them
		//note that sometimes the DMM ID has some weird stuff in front of it
		//so we just want to check if our target ID is contained at the end of the ID
		if (currentlySelectedMovieDMM != null && currentlySelectedMovieDMM.getId() != null)
		{
			//we don't want to worry about dashes messing up our checks because sometimes sites are not consistent with the dash in the ID
			String dmmIDString = null, actionJavIDString = null, squarePlusIDString = null,javLibraryIDString = null, javZooIDString = null;
			dmmIDString = currentlySelectedMovieDMM.getId().getId().replace("-","");
			if(currentlySelectedMovieActionJav != null)
				actionJavIDString = currentlySelectedMovieActionJav.getId().getId().replace("-","");
			if(currentlySelectedMovieSquarePlus != null)
				squarePlusIDString = currentlySelectedMovieSquarePlus.getId().getId().replace("-","");
			if(currentlySelectedMovieJavLibrary != null)
				javLibraryIDString = currentlySelectedMovieJavLibrary.getId().getId().replace("-","");
			if(currentlySelectedMovieJavZoo != null)
				javZooIDString = currentlySelectedMovieJavZoo.getId().getId().replace("-","");
			
			if(dmmIDString != null)
			{
				if(currentlySelectedMovieActionJav != null && actionJavIDString != null && !dmmIDString.endsWith(actionJavIDString)){
					System.out.println("Discarding ActionJav scraped results for faulty match based on ID = " + currentlySelectedMovieActionJav.getId());
					currentlySelectedMovieActionJav = null;
					guiMain.setCurrentlySelectedMovieActionJav(null);
				}
				if(currentlySelectedMovieSquarePlus != null && squarePlusIDString != null && !dmmIDString.endsWith(squarePlusIDString)){
					System.out.println("Discarding SquarePlus scraped results for faulty match based on ID = " + currentlySelectedMovieSquarePlus.getId());
					currentlySelectedMovieSquarePlus = null;
					guiMain.setCurrentlySelectedMovieSquarePlus(null);
				}
				if(currentlySelectedMovieJavLibrary != null && javLibraryIDString != null && !dmmIDString.endsWith(javLibraryIDString)){
					System.out.println("Discarding Jav Library scraped results for faulty match based on ID = " + currentlySelectedMovieJavLibrary.getId());
					currentlySelectedMovieJavLibrary = null;
					guiMain.setCurrentlySelectedMovieJavLibrary(null);
				}
				if(currentlySelectedMovieJavZoo != null && javZooIDString != null && !dmmIDString.endsWith(javZooIDString)){
					System.out.println("Discarding JavZoo scraped results for faulty match based on ID = " + currentlySelectedMovieJavZoo.getId());
					currentlySelectedMovieJavZoo = null;
					guiMain.setCurrentlySelectedMovieJavZoo(null);
				}
			}
		}

		if (currentlySelectedMovieDMM == null
				&& currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null
				&& currentlySelectedMovieJavLibrary == null)
			return null;
		// the case when i'm reading in a movie from a nfo file
		else if (guiMain.movieToWriteToDiskList != null
				&& currentlySelectedMovieDMM == null
				&& currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null
				&& currentlySelectedMovieJavLibrary == null) {
			return guiMain.movieToWriteToDiskList.get(movieNumberInList);
		} else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null
				&& (currentlySelectedMovieActionJav != null || currentlySelectedMovieSquarePlus != null || currentlySelectedMovieJavZoo != null)) {
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
			.setOriginalTitle(currentlySelectedMovieDMM
					.getOriginalTitle());
			currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieDMM.getSet());
			// grabbing the things from ActionJav which tend to be high quality
			// info
			if (currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getPlot() != null
					&& currentlySelectedMovieActionJav.getPlot().getPlot()
					.length() > 1 && currentlySelectedMovieActionJav.getId().getId().equals(currentlySelectedMovieJavLibrary.getId().getId()))
				currentlySelectedMovieJavLibrary
				.setPlot(currentlySelectedMovieActionJav.getPlot());
			/*if (currentlySelectedMovieSquarePlus != null
					&& currentlySelectedMovieSquarePlus.getTitle() != null
					&& currentlySelectedMovieSquarePlus.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieSquarePlus.getTitle());*/
			/*if (currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getTitle() != null
					&& currentlySelectedMovieActionJav.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieActionJav.getTitle());*/
			if (currentlySelectedMovieJavLibrary.getActors().size() == 0
					&& currentlySelectedMovieActionJav != null
					&& currentlySelectedMovieActionJav.getActors().size() > 0)
				currentlySelectedMovieJavLibrary
				.setActors(currentlySelectedMovieActionJav.getActors());
			currentlySelectedMovieJavLibrary.setFanart(currentlySelectedMovieDMM.getFanart());
			currentlySelectedMovieJavLibrary.setPosters(currentlySelectedMovieDMM.getPosters());
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			currentlySelectedMovieJavLibrary.setExtraFanart(currentlySelectedMovieDMM.getExtraFanart());
			if(currentlySelectedMovieActionJav != null && currentlySelectedMovieActionJav.getPlot() != null && currentlySelectedMovieActionJav.getPlot().getPlot().length() > 1  &&
					currentlySelectedMovieActionJav.getId().getId().equals(currentlySelectedMovieJavLibrary.getId().getId()))
				currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieActionJav.getPlot());
			currentlySelectedMovieJavLibrary.setTrailer(currentlySelectedMovieDMM.getTrailer());
			return currentlySelectedMovieJavLibrary;
		}

		else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null
				&& currentlySelectedMovieActionJav != null) {
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
			.setOriginalTitle(currentlySelectedMovieDMM
					.getOriginalTitle());
			currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieDMM.getSet());
			// grabbing the things from ActionJav which tend to be high quality
			// info
			if (currentlySelectedMovieActionJav.getPlot() != null
					&& currentlySelectedMovieActionJav.getPlot().getPlot()
					.length() > 1 && currentlySelectedMovieActionJav.getId().getId().equals(currentlySelectedMovieJavLibrary.getId().getId()))
				currentlySelectedMovieJavLibrary
				.setPlot(currentlySelectedMovieActionJav.getPlot());
			/*if (currentlySelectedMovieActionJav.getTitle() != null
					&& currentlySelectedMovieActionJav.getTitle().getTitle()
							.length() > 1)
				currentlySelectedMovieJavLibrary
						.setTitle(currentlySelectedMovieActionJav.getTitle());*/
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			currentlySelectedMovieJavLibrary.setExtraFanart(currentlySelectedMovieDMM.getExtraFanart());
			if(currentlySelectedMovieActionJav != null && currentlySelectedMovieActionJav.getPlot() != null && currentlySelectedMovieActionJav.getPlot().getPlot().length() > 1 && currentlySelectedMovieActionJav.getId().getId().equals(currentlySelectedMovieJavLibrary.getId().getId()))
				currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieActionJav.getPlot());
			currentlySelectedMovieJavLibrary.setTrailer(currentlySelectedMovieDMM.getTrailer());
			currentlySelectedMovieJavLibrary.setTrailer(currentlySelectedMovieDMM.getTrailer());
			return currentlySelectedMovieJavLibrary;
		}

		else if (currentlySelectedMovieJavLibrary != null
				&& currentlySelectedMovieDMM != null) {
			//System.out.println("Return Jav Lib movie with DMM Plot");
			currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieDMM
					.getPlot());
			currentlySelectedMovieJavLibrary
			.setOriginalTitle(currentlySelectedMovieDMM
					.getOriginalTitle());
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			currentlySelectedMovieJavLibrary.setExtraFanart(currentlySelectedMovieDMM.getExtraFanart());
			if(currentlySelectedMovieActionJav != null && currentlySelectedMovieActionJav.getPlot() != null && currentlySelectedMovieActionJav.getPlot().getPlot().length() > 1 )
				currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieActionJav.getPlot());
			currentlySelectedMovieJavLibrary.setTrailer(currentlySelectedMovieDMM.getTrailer());
			return currentlySelectedMovieJavLibrary;
		}
		// DMM was not found but JavLibrary was? This shouldn't really happen
		// too often...
		else if (currentlySelectedMovieJavLibrary != null) {
			//System.out.println("Return Jav Lib movie");
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieJavLibrary.setSet(currentlySelectedMovieJavZoo.getSet());
			if(currentlySelectedMovieActionJav != null && currentlySelectedMovieActionJav.getPlot() != null && currentlySelectedMovieActionJav.getPlot().getPlot().length() > 1 )
				currentlySelectedMovieJavLibrary.setPlot(currentlySelectedMovieActionJav.getPlot());
			return currentlySelectedMovieJavLibrary;
		}

		// Nothing on either squareplus or actionjav or JavLibrary, so just
		// return the DMM info
		else if (currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus == null) {
			if(currentlySelectedMovieJavZoo != null && currentlySelectedMovieJavZoo.getSet() != null && currentlySelectedMovieJavZoo.getSet().getSet().length() > 0)
				currentlySelectedMovieDMM.setSet(currentlySelectedMovieJavZoo.getSet());

			return currentlySelectedMovieDMM;
		}
		// ActionJav found and SquarePlus not
		else if (currentlySelectedMovieActionJav != null
				&& currentlySelectedMovieSquarePlus == null) {
			// System.out.println("ActionJav found and SquarePlus not");
			ArrayList<Actor> actorsToUse = (currentlySelectedMovieActionJav
					.getActors().size() > 0 && currentlySelectedMovieActionJav
					.getActors().size() >= currentlySelectedMovieDMM
					.getActors().size()) ? currentlySelectedMovieActionJav
					.getActors() : currentlySelectedMovieDMM.getActors();
			ArrayList<Director> directorsToUse = (currentlySelectedMovieActionJav
					.getDirectors().size() > 0) ? currentlySelectedMovieActionJav
					.getDirectors() : currentlySelectedMovieDMM.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			Thumb[] extraFanartToUse = currentlySelectedMovieDMM
					.getExtraFanart();
			ArrayList<Genre> genresToUse = (currentlySelectedMovieActionJav
					.getGenres().size() > 1) ? currentlySelectedMovieActionJav
					.getGenres() : currentlySelectedMovieDMM.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = (currentlySelectedMovieActionJav.getPlot()
					.getPlot().length() > 1) ? currentlySelectedMovieActionJav
					.getPlot() : currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Title titleToUse = (currentlySelectedMovieActionJav.getTitle()
					.getTitle().length() > 1) ? currentlySelectedMovieActionJav
					.getTitle() : currentlySelectedMovieDMM.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = (currentlySelectedMovieActionJav
					.getRuntime().getRuntime().length() > 1) ? currentlySelectedMovieActionJav
					.getRuntime() : currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = (currentlySelectedMovieActionJav.getStudio()
					.getStudio().length() > 1) ? currentlySelectedMovieActionJav
					.getStudio() : currentlySelectedMovieDMM.getStudio();
			Trailer trailerToUse = currentlySelectedMovieDMM.getTrailer();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, extraFanartToUse, genresToUse, idsToUse,
					mpaaToUse, originalTitleToUse, outlineToUse, plotToUse,
					postersToUse, ratingToUse, runtimeToUse, setToUse,
					sortTitleToUse, studioToUse, taglineToUse, titleToUse,
					top250ToUse, trailerToUse, votesToUse, yearToUse);
			return amalgamatedMovie;
		}
		// Squareplus found something, actionjav did not
		else if (currentlySelectedMovieActionJav == null
				&& currentlySelectedMovieSquarePlus != null) {
			//System.out.println("Squareplus found something, actionjav did not");
			ArrayList<Actor> actorsToUse = currentlySelectedMovieDMM
					.getActors();
			ArrayList<Director> directorsToUse = currentlySelectedMovieDMM
					.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			Thumb[] extraFanartToUse = currentlySelectedMovieDMM.getExtraFanart();
			ArrayList<Genre> genresToUse = currentlySelectedMovieDMM
					.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Trailer trailerToUse = currentlySelectedMovieDMM.getTrailer();
			Title titleToUse = currentlySelectedMovieSquarePlus.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = currentlySelectedMovieDMM.getStudio();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, extraFanartToUse, genresToUse, idsToUse, mpaaToUse,
					originalTitleToUse, outlineToUse, plotToUse, postersToUse,
					ratingToUse, runtimeToUse, setToUse, sortTitleToUse,
					studioToUse, taglineToUse, titleToUse, top250ToUse, trailerToUse,
					votesToUse, yearToUse);
			return amalgamatedMovie;
		} else // amalgamate from all 3 sources
		{
			ArrayList<Actor> actorsToUse = (currentlySelectedMovieActionJav
					.getActors().size() > 0 && currentlySelectedMovieActionJav
					.getActors().size() >= currentlySelectedMovieDMM
					.getActors().size()) ? currentlySelectedMovieActionJav
					.getActors() : currentlySelectedMovieDMM.getActors();
			ArrayList<Director> directorsToUse = (currentlySelectedMovieActionJav
					.getDirectors().size() > 0) ? currentlySelectedMovieActionJav
					.getDirectors() : currentlySelectedMovieDMM.getDirectors();
			Thumb[] fanartToUse = currentlySelectedMovieDMM.getFanart();
			Thumb[] extraFanartToUse = currentlySelectedMovieDMM
					.getExtraFanart();
			ArrayList<Genre> genresToUse = (currentlySelectedMovieActionJav
					.getGenres().size() > 1) ? currentlySelectedMovieActionJav
					.getGenres() : currentlySelectedMovieDMM.getGenres();
			ID idsToUse = currentlySelectedMovieDMM.getId();
			MPAARating mpaaToUse = currentlySelectedMovieDMM.getMpaa();
			OriginalTitle originalTitleToUse = currentlySelectedMovieDMM
					.getOriginalTitle();
			Outline outlineToUse = currentlySelectedMovieDMM.getOutline();
			Plot plotToUse = (currentlySelectedMovieActionJav.getPlot()
					.getPlot().length() > 1 && currentlySelectedMovieActionJav
					.getId().getId()
					.equals(currentlySelectedMovieDMM.getId().getId())) ? currentlySelectedMovieActionJav
					.getPlot() : currentlySelectedMovieDMM.getPlot();
			Thumb[] postersToUse = currentlySelectedMovieDMM.getPosters();
			Year yearToUse = currentlySelectedMovieDMM.getYear();
			Votes votesToUse = currentlySelectedMovieDMM.getVotes();
			Top250 top250ToUse = currentlySelectedMovieDMM.getTop250();
			Trailer trailerToUse = currentlySelectedMovieDMM.getTrailer();
			Title titleToUse = currentlySelectedMovieActionJav.getTitle();
			Tagline taglineToUse = currentlySelectedMovieDMM.getTagline();
			Rating ratingToUse = currentlySelectedMovieDMM.getRating();
			Runtime runtimeToUse = (currentlySelectedMovieActionJav
					.getRuntime().getRuntime().length() > 1) ? currentlySelectedMovieActionJav
					.getRuntime() : currentlySelectedMovieDMM.getRuntime();
			Set setToUse = currentlySelectedMovieDMM.getSet();
			SortTitle sortTitleToUse = currentlySelectedMovieDMM.getSortTitle();
			Studio studioToUse = (currentlySelectedMovieActionJav.getStudio()
					.getStudio().length() > 1) ? currentlySelectedMovieActionJav
					.getStudio() : currentlySelectedMovieDMM.getStudio();
			Movie amalgamatedMovie = new Movie(actorsToUse, directorsToUse,
					fanartToUse, extraFanartToUse, genresToUse, idsToUse,
					mpaaToUse, originalTitleToUse, outlineToUse, plotToUse,
					postersToUse, ratingToUse, runtimeToUse, setToUse,
					sortTitleToUse, studioToUse, taglineToUse, titleToUse,
					top250ToUse, trailerToUse, votesToUse, yearToUse);
			return amalgamatedMovie;
		}

	}

}