package moviescraper.doctord.controller.amalgamation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.dataitem.DataItemSource;
import moviescraper.doctord.view.ScrapeAmalgamatedProgressDialog;

public class ScrapeAmalgamatedMovieWorker extends SwingWorker<Void, Map<SiteParsingProfile, Movie>> {

	List<Thread> scrapeThreads;
	
	boolean promptUserForURLWhenScraping = true; //do we stop to ask the user to pick a URL when scraping
	
	int progress;
	int amountOfProgressPerSubtask;
	SwingWorker<Void, String> worker;
	boolean scrapeCanceled;
	List<Map<SiteParsingProfile,Movie>> currentPublishedMovies;
	int numberOfScrapesToRun = 0;
	int numberOfScrapesFinished = 0;
	Map<String, SwingWorker<Void, Void>> runningWorkers;
	File fileToScrape;
	
	AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences;
	ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference;
	ScrapeAmalgamatedProgressDialog parent;
	
	/**
	 * 
	 * @param allAmalgamationOrderingPreferences 
	 * @param guiMain
	 * @param scraperGroupAmalgamationPreference
	 * @param fileToScrape - file scraped if no gui (if there is a gui we use the state variable from there wich is the file to scrape)
	 */
	public ScrapeAmalgamatedMovieWorker(AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences,
										ScraperGroupAmalgamationPreference scraperGroupAmalgamationPreference, File fileToScrape, ScrapeAmalgamatedProgressDialog parent)
	{
		runningWorkers = new HashMap<>();
		progress = 0;
		amountOfProgressPerSubtask = 0;
		scrapeCanceled = false;
		this.scraperGroupAmalgamationPreference = scraperGroupAmalgamationPreference;
		this.fileToScrape = fileToScrape;
		this.parent = parent;
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
	}
	
	SwingWorker<Void, Void> getWorkerByScraperName(SiteParsingProfile scraper)
	{
		SwingWorker<Void, Void> worker = runningWorkers.get(scraper.getDataItemSourceName());
		return worker;
	}
	
	public void cancelRunningScraper(SiteParsingProfile scraper)
	{
		SwingWorker<Void, Void> scraperToCancel = runningWorkers.get(scraper.getDataItemSourceName());
		if(scraperToCancel != null)
		{
			System.out.println("Canceling " + scraper + " + thread.");
			boolean wasThreadCanceled = scraperToCancel.cancel(true);
			if(wasThreadCanceled)
			{
				numberOfScrapesFinished++;
			}
		}
	}
	
	public void cancelAllRunningScrapers()
	{
		for(SwingWorker<Void, Void> currentWorker : runningWorkers.values())
		{
			if(currentWorker != null)
			{
				System.out.println("Canceling " + currentWorker);
				currentWorker.cancel(true);
			}
		}
	}
	
	  /*private static void failIfInterrupted() throws InterruptedException {
		    if (Thread.currentThread().isInterrupted()) {
		      throw new InterruptedException("Interrupted while searching files");
		    }
		  }*/
	
	/**
	 * 
	 * @param parsingProfile - item to check if scraping is enabled for this parsing profile
	 * @return true if scraper should scrape for parsingProfile, false otherwise
	 */
	private boolean shouldScrapeThread(DataItemSource parsingProfile) {
		//Default group used for site specific scraping - always want to return true since there's just one thread to scrape
		if(scraperGroupAmalgamationPreference.getScraperGroupName().equals(ScraperGroupName.DEFAULT_SCRAPER_GROUP))
			return true;
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
	


	
	@Override
	protected Void doInBackground() {

		setProgress(0);
		//failIfInterrupted();
		LinkedList<DataItemSource> scraperList = scraperGroupAmalgamationPreference.getOverallAmalgamationPreference().getAmalgamationPreferenceOrder();
		//calculate progress amount per worker
		int numberOfScrapes = 0;
		for(DataItemSource currentScraper : scraperList)
		{
			if(shouldScrapeThread(currentScraper) && currentScraper instanceof SiteParsingProfile)
				numberOfScrapes++;
		}
		
		
		int progressAmountPerWorker = 100 / numberOfScrapes;
		
               for(DataItemSource currentScraper : scraperList)
		{
			//We don't want to read any leftover properties from our JSON - we want to start fresh so things like scraping language do not get set in our scraper
			currentScraper = currentScraper.createInstanceOfSameType();
			if(currentScraper instanceof SiteParsingProfile)
			{
				
				
				if(shouldScrapeThread(currentScraper))
				{
					scrapeMovieInBackground(fileToScrape, currentScraper, progressAmountPerWorker);
					numberOfScrapesToRun++;
				}
			}
		}
		
		//failIfInterrupted();
		
		//System.out.println("returnMovie is " + returnMovie);
		
		//setProgress(100);
		
		return null;
	}
	
	private Movie scrapeMovieInBackground(File fileToScrape, DataItemSource scraper, int amountOfProgress) {
		// failIfInterrupted();
		if (scraper instanceof SiteParsingProfile) {
			final SiteParsingProfile siteScraper = (SiteParsingProfile) scraper;
			final ScrapeAmalgamatedMovieWorker self = this;
			final int amtOfProgressFinal = amountOfProgress;
			final File fileToScrapeFinal = fileToScrape;
                        
                        System.out.println(fileToScrapeFinal);
			
			SwingWorker<Void, Void> scraperWorker = new SwingWorker<Void, Void>() {
				Movie returnMovie;
				@Override
				protected Void doInBackground() throws Exception {
					try {
						//delegate back to the parent, if we have one, to override the URL we are going to scrape with a custom URL provided by the user.
						boolean customURLSet = false;
						if(parent != null)
						{
							customURLSet = ScrapeAmalgamatedProgressDialog.showPromptForUserProvidedURL(siteScraper, fileToScrapeFinal);
						}
						returnMovie = Movie.scrapeMovie(fileToScrapeFinal, siteScraper, "", customURLSet);
						
						
						return null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				
				@Override
				protected void done()
				{
					
					self.numberOfScrapesFinished++;
					//System.out.println("Movie scraped = " + returnMovie);
					Map<SiteParsingProfile, Movie> resultToPublish = new HashMap<>();
					resultToPublish.put(siteScraper, returnMovie);
					self.publish(resultToPublish);
					self.progress = amtOfProgressFinal + self.progress;
					self.setProgress(self.progress);
					System.out.println("Scraping complete of siteScraper = " + siteScraper);
					self.runningWorkers.remove(siteScraper);
				}
			};
			self.runningWorkers.put(scraper.getDataItemSourceName(), scraperWorker);
			scraperWorker.execute();
		}

		// failIfInterrupted();
		return null;

	}
	

	@Override
	protected void done() {
		
		
	}
	
	/**
	 * Enums used to fire properties.
	 * ALL_SCRAPES_FINISHED - used when all scraper workers have finished or been canceled
	 * SCRAPED_MOVIE - One of the scraper threads has finished and is returning back the amalgamated movie it found
	 */
	public enum ScrapeAmalgamatedMovieWorkerProperty
	{
		ALL_SCRAPES_FINISHED, SCRAPED_MOVIE
	}
	
	@Override
	protected void process(List<Map<SiteParsingProfile,Movie>> movies)
	{
		
		firePropertyChange(ScrapeAmalgamatedMovieWorkerProperty.SCRAPED_MOVIE.toString(), currentPublishedMovies, movies);
		currentPublishedMovies = movies;
		
		if(numberOfScrapesFinished >= numberOfScrapesToRun)
			firePropertyChange(ScrapeAmalgamatedMovieWorkerProperty.ALL_SCRAPES_FINISHED.toString(), null, numberOfScrapesFinished);
		else {
			System.out.println("Finished " + numberOfScrapesFinished + "/" + numberOfScrapesToRun + " scrape threads.");
		}
	}
	
	
	


}
