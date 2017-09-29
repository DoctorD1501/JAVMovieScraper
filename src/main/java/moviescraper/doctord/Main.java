package moviescraper.doctord;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import moviescraper.doctord.controller.Renamer;
import moviescraper.doctord.controller.releaserenamer.WebReleaseRenamer;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.AvEntertainmentParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.CaribbeancomPremiumParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18MovieParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Data18WebContentParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.DmmParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.ExcaliburFilmsParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.HeyzoParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.IAFDParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.JavBusParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Kin8tengokuParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.MyTokyoHotParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.OnePondoParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.OneThousandGiriParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TokyoHotParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.GUIMain;

public class Main {
	private final static int MIN_HEAP = 768; 
	public static void main(String[] args) throws Exception {
		
		//This program works better if we are running with a bigger heap size, so the code below will attempt
		//to restart the jar with a bigger heap if we are not already running with a good heap size
		//this lets the user double click the jar and still get a bigger heap without having to pass
		//command line arguments to the java program to start the jar with a bigger heap
		float heapSizeMegs = (Runtime.getRuntime().maxMemory()/1024)/1024;
		System.out.println("Heap size is " + heapSizeMegs + "M");
	    if (heapSizeMegs > MIN_HEAP) {
	    	//we will continue program execute below - we are running with a decent size max heap :)
	    	System.out.println("Heap size good, continue to launch program");
	    } else {
	    	//restart the jar file (if there is one) with a vm argument to give us more max heap size
	      String pathToJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
	      //if running from IDE, we will not be able to find the jar below. instead to get more max memory,
	      //set an vm arguments in the runtime config with this param: -Xmx1024m
	      if(pathToJar.endsWith(".jar")) {
	    	  System.out.println("Trying to launch process with heap size of 1024");
	    	  ProcessBuilder pb = new ProcessBuilder("java","-Xmx1024m", "-classpath", pathToJar, "moviescraper.doctord.Main");
	    	  pb.start();
	      return;
	      }
	    }
		
		
		if(args == null || args.length == 0)
		{
			//Start the GUI version of the program
			GUIMain.main(args);
		}
		else
		{
			
			//set up the options
			Options options = new Options();
			
			//set up -help option
			options.addOption("help", false, "display this message");
			
			//set up -filenamecleanup option
			@SuppressWarnings("static-access") //Needed until apache commons cli v1.3 which fixes this design flaw
			
			Option filenamecleanup = OptionBuilder.withArgName( "FilePath" )
                    .hasArgs(Option.UNLIMITED_VALUES)
                    .withDescription(  "Use given file argument(s) for file name cleanup process which will rename the file by expanding abbreviations and removing words which cause google scrapes to fail" )
                    .create( "filenamecleanup" );
			
			@SuppressWarnings("static-access")
			Option scrape = OptionBuilder.withArgName("ScraperName FilePath")
                    .hasArgs(2)
                    .withDescription(  "Scrapes and writes metadata of the file located at <FilePath> with type of scraper specified by <ScraperName>.\n" +
                    					"Valid ScraperNames are: \n" +
                    					"data18webcontent, data18, excaliburfilms, themoviedatabase, iafd, dmm, 1000giri, 1pondo, aventertainment, caribbeancom, caribbeancompremium, heyzo, kin8tengoku, mytokyohot, tokyohot, javbus .\n" + 
                    					"Any settings.xml file preference values will be taken into account when scraping.")
                    .create( "scrape" );
			
			@SuppressWarnings("static-access")
			Option rename = OptionBuilder.withArgName("FilePath")
                    .hasArgs(Option.UNLIMITED_VALUES)
                    .withDescription("renames the file argument(s) and any associated metadata files if the file argument has a valid movie nfo using the file name format from settings.xml")
                    .create( "rename" );

			options.addOption(filenamecleanup);
			options.addOption(scrape);
			options.addOption(rename);
			
			CommandLineParser parser = new BasicParser();
			try {
				CommandLine line = parser.parse(options, args);
				
				//-help
				if( line.hasOption( "help" )) {
					printHelpMessage(options);
				}
				//-filenamecleanup
				else if(line.hasOption("filenamecleanup"))
				{
					runFileNameCleanup(line.getOptionValues("filenamecleanup"));
				}
				//-scrape
				else if(line.hasOption("scrape"))
				{
					runScrape(line.getOptionValues("scrape"));
				}
				else if(line.hasOption("rename"))
				{
					runRename(line.getOptionValues("rename"));
				}
					
				
			} catch (ParseException exp) {
				System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );

			}
			
		}

	}
	
	private static void runRename(String[] optionValues) {
		for(String fileName : optionValues)
		{
			File currentFile = new File(fileName);
			if(!currentFile.exists())
			{
				System.err.println(currentFile + " does not exist.");
			}
			else
			{
				System.out.println("Trying to rename " + currentFile);
				MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
				System.out.println("Renaming with these preferences:");
				System.out.println(preferences);
				try {
					Renamer.rename(currentFile, preferences);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	private static void printHelpMessage(Options options)
	{
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "JAVMovieScraper", options );
	}
	
	private static void runFileNameCleanup(String [] optionValues)
	{
		for(String fileName : optionValues)
		{
			File currentFile = new File(fileName);
			if(!currentFile.exists())
			{
				System.err.println(currentFile + " does not exist.");
			}
			else
			{
				try {
					WebReleaseRenamer renamer = new WebReleaseRenamer();
					File newFile = renamer.newFileName(currentFile);
					boolean renameStatus = currentFile.renameTo(newFile);
					if(renameStatus != true)
						System.err.println("Rename failed! Perhaps a file name already exists with that name?");
					else
						System.out.println("Renamed " + currentFile + " to " + newFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void runScrape(String [] optionValues)
	{
		boolean useFileNameCleanup = false;
		if(optionValues != null && optionValues.length == 2)
		{
			String scraperName = optionValues[0];
			String fileName = optionValues[1];
			
			System.out.println("Scraping with scraper = " + scraperName);
			System.out.println("Filename =  " + fileName);
			File scrapeTarget = new File(fileName);
			if(scrapeTarget.exists())
			{
				SiteParsingProfile parsingProfile = returnParsingProfileFromCommandLineOption(scraperName);
				if(parsingProfile != null)
				{
					if(parsingProfile.getClass() == Data18WebContentParsingProfile.class)
					{
						useFileNameCleanup = true;
					}
					System.out.println("Parsing with parsing profile = " + parsingProfile.getClass());
					try {
						File scrapeTargetToUse = scrapeTarget;
						if(useFileNameCleanup)
						{
							WebReleaseRenamer renamer = new WebReleaseRenamer();
							scrapeTargetToUse = renamer.newFileName(scrapeTarget);
							System.out.println("passing in " + scrapeTargetToUse + " as the name"); 
						}
						Movie scrapedMovie = Movie.scrapeMovie(scrapeTargetToUse, parsingProfile, "", false);
						//write out the metadata to disk if we got a hit
						if(scrapedMovie != null)
						{
							System.out.println("Movie scraped as" + scrapedMovie);
							
							writeMovieToFile(scrapedMovie, scrapeTarget);
							
							System.out.println("All files written!");
						}
						else
						{
							System.err.println("No movie found");
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					System.err.println("Unsupported parsing profile passed in");
				}
			}
			else
			{
				System.err.println("File " + fileName + "does not exist");
			}
		}
		else
		{
			System.err.println("you need to pass a valid scraper and file name");
		}
	}
	
	private static void writeMovieToFile(Movie scrapedMovie, File scrapeTarget)
	{
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();

		
		File nfoFile = new File(Movie
				.getFileNameOfNfo(scrapeTarget, preferences.getNfoNamedMovieDotNfo()));
		File posterFile = new File(Movie
				.getFileNameOfPoster(scrapeTarget, preferences.getNoMovieNameInImageFiles()));
		File fanartFile = new File(Movie
				.getFileNameOfFanart(scrapeTarget, preferences.getNoMovieNameInImageFiles()));
		File currentlySelectedFolderJpgFile = new File(Movie
				.getFileNameOfFolderJpg(scrapeTarget));
		File extraFanartFolder = new File(Movie.getFileNameOfExtraFanartFolderName(scrapeTarget));
		
		File trailerFile = new File(Movie.getFileNameOfTrailer(scrapeTarget));
		
		try {
			scrapedMovie.writeToFile(nfoFile, posterFile, fanartFile, currentlySelectedFolderJpgFile, extraFanartFolder, trailerFile, preferences);
			
			//TODO: write out trailers, actor images
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static SiteParsingProfile returnParsingProfileFromCommandLineOption(String scraperName){
		SiteParsingProfile parsingProfile = null;
		switch(scraperName)
		{
			case "data18webcontent":
				parsingProfile = new Data18WebContentParsingProfile();
				break;
			case "data18":
				parsingProfile = new Data18MovieParsingProfile();
				break;
			case "1pondo":
				parsingProfile = new OnePondoParsingProfile();
				break;
			case "aventertainment":
				parsingProfile = new AvEntertainmentParsingProfile();
				break;
			case "caribbeancom":
				parsingProfile = new CaribbeancomParsingProfile();
				break;
			case "caribbeancompremium":
				parsingProfile = new CaribbeancomPremiumParsingProfile();
				break;
			case "heyzo":
				parsingProfile = new HeyzoParsingProfile();
				break;
			case "kin8tengoku":
				parsingProfile = new Kin8tengokuParsingProfile();
				break;
			case "mytokyohot":
				parsingProfile = new MyTokyoHotParsingProfile();
				break;
			case "tokyohot":
				parsingProfile = new TokyoHotParsingProfile();
				break;
			case "iafd":
				parsingProfile = new IAFDParsingProfile();
				break;
			case "themoviedatabase":
				parsingProfile = new TheMovieDatabaseParsingProfile();
				break;
			case "excaliburfilms":
				parsingProfile = new ExcaliburFilmsParsingProfile();
				break;
			case "1000giri":
				parsingProfile = new OneThousandGiriParsingProfile();
				break;
			case "javbus":
				parsingProfile = new JavBusParsingProfile();
				break;
			case "dmm":
				parsingProfile = new DmmParsingProfile();
				break;
		default:
			break;
		}
		return parsingProfile;
		
	}

}
