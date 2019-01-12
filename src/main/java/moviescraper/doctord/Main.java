package moviescraper.doctord;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
import moviescraper.doctord.controller.siteparsingprofile.specific.ActionJavParsingProfile;
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
import moviescraper.doctord.controller.siteparsingprofile.specific.JavLibraryParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.Kin8tengokuParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.MyTokyoHotParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.OnePondoParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.R18ParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TheMovieDatabaseParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.TokyoHotParsingProfile;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.GUIMain;
import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		long freeMem = Runtime.getRuntime().freeMemory();
		long heapSize = Runtime.getRuntime().maxMemory();
		String jvmSpecVersion = System.getProperty("java.specification.version");
		String jvmSpecVendor = System.getProperty("java.specification.vendor");
		String jvmVendor = System.getProperty("java.vendor");
		String jvmVersion = System.getProperty("java.runtime.version");
		String jvmName = System.getProperty("java.runtime.name");

		System.out.println(jvmName + " " + jvmVersion + "(" + jvmVendor + ") -- " + jvmSpecVersion + "(" + jvmSpecVendor + ")");
		System.out.println("Heap: " + FileUtils.byteCountToDisplaySize(heapSize));
		System.out.println("Free mem: " + FileUtils.byteCountToDisplaySize(freeMem));

		if (args == null || args.length == 0) {
			//Start the GUI version of the program
			GUIMain.main(args);
		} else {

			//set up the options
			Options options = new Options();

			//set up -help option
			options.addOption("help", false, "display this message");

			//set up -filenamecleanup option
			@SuppressWarnings("static-access") //Needed until apache commons cli v1.3 which fixes this design flaw

			Option filenamecleanup = OptionBuilder.withArgName("FilePath").hasArgs(Option.UNLIMITED_VALUES)
			        .withDescription(
			                "Use given file argument(s) for file name cleanup process which will rename the file by expanding abbreviations and removing words which cause google scrapes to fail")
			        .create("filenamecleanup");

			@SuppressWarnings("static-access")
			Option scrape = OptionBuilder.withArgName("ScraperName FilePath").hasArgs(2)
			        .withDescription("Scrapes and writes metadata of the file located at <FilePath> with type of scraper specified by <ScraperName>.\n" + "Valid ScraperNames are: \n"
			                + "data18webcontent, data18, excaliburfilms, themoviedatabase, iafd, dmm, 1000giri, 1pondo, aventertainment, caribbeancom, caribbeancompremium, heyzo, kin8tengoku, mytokyohot, tokyohot, javbus, javlibrary, r18, actionjav.\n"
			                + "Any settings.xml file preference values will be taken into account when scraping.")
			        .create("scrape");

			@SuppressWarnings("static-access")
			Option scrapeUrl = OptionBuilder.withArgName("ScrapeUrl").hasArgs(1).withDescription("Scrape from the given url.").create("scrapeurl");

			@SuppressWarnings("static-access")
			Option rename = OptionBuilder.withArgName("FilePath").hasArgs(Option.UNLIMITED_VALUES)
			        .withDescription("renames the file argument(s) and any associated metadata files if the file argument has a valid movie nfo using the file name format from settings.xml")
			        .create("rename");

			options.addOption(filenamecleanup);
			options.addOption(scrape);
			options.addOption(scrapeUrl);
			options.addOption(rename);

			CommandLineParser parser = new BasicParser();
			try {
				CommandLine line = parser.parse(options, args);

				//-help
				if (line.hasOption("help")) {
					printHelpMessage(options);
				}
				//-filenamecleanup
				else if (line.hasOption("filenamecleanup")) {
					runFileNameCleanup(line.getOptionValues("filenamecleanup"));
				}
				//-scrape
				else if (line.hasOption("scrape")) {
					if (line.hasOption("scrapeurl")) {
						runScrape(line.getOptionValues("scrape"), line.getOptionValue("scrapeurl"));
					} else {
						runScrape(line.getOptionValues("scrape"), null);
					}
				} else if (line.hasOption("rename")) {
					runRename(line.getOptionValues("rename"));
				}

			} catch (ParseException exp) {
				System.err.println("Parsing failed.  Reason: " + exp.getMessage());

			}

		}

	}

	private static void runRename(String[] optionValues) {
		for (String fileName : optionValues) {
			File currentFile = new File(fileName);
			if (!currentFile.exists()) {
				System.err.println(currentFile + " does not exist.");
			} else {
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

	private static void printHelpMessage(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("JAVMovieScraper", options);
	}

	private static void runFileNameCleanup(String[] optionValues) {
		for (String fileName : optionValues) {
			File currentFile = new File(fileName);
			if (!currentFile.exists()) {
				System.err.println(currentFile + " does not exist.");
			} else {
				try {
					WebReleaseRenamer renamer = new WebReleaseRenamer();
					File newFile = renamer.newFileName(currentFile);
					boolean renameStatus = currentFile.renameTo(newFile);
					if (renameStatus)
						System.out.println("Renamed " + currentFile + " to " + newFile);
					else
						System.err.println("Rename failed! Perhaps a file name already exists with that name?");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void runScrape(String[] optionValues, String userProvidedURL) {
		boolean useFileNameCleanup = false;
		if (optionValues != null && optionValues.length == 2) {
			String scraperName = optionValues[0];
			String fileName = optionValues[1];

			System.out.println("Scraping with scraper = " + scraperName);
			System.out.println("Filename =  " + fileName);
			File scrapeTarget = new File(fileName);
			if (scrapeTarget.exists()) {
				SiteParsingProfile parsingProfile = returnParsingProfileFromCommandLineOption(scraperName);
				if (parsingProfile != null) {
					if (parsingProfile.getClass() == Data18WebContentParsingProfile.class) {
						useFileNameCleanup = true;
					}
					System.out.println("Parsing with parsing profile = " + parsingProfile.getClass());
					try {
						File scrapeTargetToUse = scrapeTarget;
						if (useFileNameCleanup) {
							WebReleaseRenamer renamer = new WebReleaseRenamer();
							scrapeTargetToUse = renamer.newFileName(scrapeTarget);
							System.out.println("passing in " + scrapeTargetToUse + " as the name");
						}

						boolean wasCustomURLSet = false;
						if (userProvidedURL != null && userProvidedURL.length() > 0) {
							//TODO: validate this is a actually a URL and display an error message if it is not
							//also maybe don't let them click OK if isn't a valid URL?
							try {
								URL isAValidURL = new URL(userProvidedURL);
								parsingProfile.setOverridenSearchResult(isAValidURL.toString());
								wasCustomURLSet = true;
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}

						}

						Movie scrapedMovie = Movie.scrapeMovie(scrapeTargetToUse, parsingProfile, "", wasCustomURLSet);
						//write out the metadata to disk if we got a hit
						if (scrapedMovie != null) {
							System.out.println("Movie scraped as" + scrapedMovie);

							writeMovieToFile(scrapedMovie, scrapeTarget);

							System.out.println("All files written!");
						} else {
							System.err.println("No movie found");
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.err.println("Unsupported parsing profile passed in");
				}
			} else {
				System.err.println("File " + fileName + "does not exist");
			}
		} else {
			System.err.println("you need to pass a valid scraper and file name");
		}
	}

	private static void writeMovieToFile(Movie scrapedMovie, File scrapeTarget) {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();

		File nfoFile = new File(Movie.getFileNameOfNfo(scrapeTarget, preferences.getNfoNamedMovieDotNfo()));
		File posterFile = new File(Movie.getFileNameOfPoster(scrapeTarget, preferences.getNoMovieNameInImageFiles()));
		File fanartFile = new File(Movie.getFileNameOfFanart(scrapeTarget, preferences.getNoMovieNameInImageFiles()));
		File currentlySelectedFolderJpgFile = new File(Movie.getFileNameOfFolderJpg(scrapeTarget));
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

	private static SiteParsingProfile returnParsingProfileFromCommandLineOption(String scraperName) {
		SiteParsingProfile parsingProfile = null;
		switch (scraperName) {
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
			case "javbus":
				parsingProfile = new JavBusParsingProfile();
				break;
			case "dmm":
				parsingProfile = new DmmParsingProfile();
				break;
			case "javlibrary":
				parsingProfile = new JavLibraryParsingProfile();
				break;
			case "r18":
				parsingProfile = new R18ParsingProfile();
				break;
			case "actionjav":
				parsingProfile = new ActionJavParsingProfile();
				break;
			default:
				break;
		}
		return parsingProfile;

	}

}
