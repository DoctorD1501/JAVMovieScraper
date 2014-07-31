package moviescraper.doctord;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import moviescraper.doctord.SiteParsingProfile.ActionJavParsingProfile;
import moviescraper.doctord.SiteParsingProfile.DmmParsingProfile;
import moviescraper.doctord.SiteParsingProfile.JavZooParsingProfile;
import moviescraper.doctord.SiteParsingProfile.SquarePlusParsingProfile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean runParser = true;
		if (runParser) {
			System.out.println("Starting Parser");
			String URLStringToUse = "http://www.javzoo.com/en/movie/1iab";
			String fileName = "SVDVD021.nfo";
			String posterFileName = "SVDVD021.jpg";
			// String URLStringToUse =
			// "http://www.dmm.co.jp/mono/dvd/-/detail/=/cid=mird112/";
			// String URLStringToUse =
			// "http://www.dmm.co.jp/mono/dvd/-/detail/=/cid=midd970/";
			
			Document searchMatch;
			try {
				/*Begin testing search string*/
				System.out.println("Testing URL to use");

				JavZooParsingProfile searchResultCreator = new JavZooParsingProfile();
				String searchString = searchResultCreator
						.createSearchString(new File(fileName));
				System.out.println("searchString was " + searchString);
				SearchResult[] searchResults = searchResultCreator
						.getSearchResults(searchString);
				if (searchResults.length > 0) {
					System.out.println("Scraping this webpage for movie: "
							+ searchResults[0]);
					// for now just set the movie to the first thing found
					searchMatch = Jsoup.connect(searchResults[0].getUrlPath())
							.timeout(0).get();
					
					// TODO find out something to do with the timeout 0
					//Document doc = Jsoup.connect(URLStringToUse).timeout(0).get();
					System.out.println("Opened URL: " + URLStringToUse);
					Movie scrapedMovie = initializeMovieJavZoo(searchMatch);
					System.out.println("scrapedMovie: " + scrapedMovie);
					String xml = new XbmcXmlMovieBean(scrapedMovie).toXML();
					// add the xml header since xstream doesn't do this
					// xml =
					// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
					// + "\n" + xml;

					/*FileUtils.writeStringToFile(new File(fileName), xml,
							org.apache.commons.lang3.CharEncoding.UTF_8);*/

					// save the first poster out
					/*if (scrapedMovie.getPosters().length > 0)
						ImageIO.write(
								(RenderedImage) scrapedMovie.getPosters()[0].thumbImage,
								"jpg", new File(posterFileName));*/

					System.out.println(xml);
				}

				
				

				


				

				// Output the movie to XML using XStream and a proxy class to
				// translate things to a format that xbmc expects

				

				// System.out.println(scrapedMovie.toXML());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * String stringToTranslate = "こんにちは";
		 * System.out.println("Testing translation system");
		 * System.out.println("Original String: " + stringToTranslate);
		 * System.out.println("Translated String: " +
		 * TranslateString.translateStringJapaneseToEnglish(stringToTranslate));
		 */

	}

	public static Movie initializeMovie() {
		boolean runParser = true;
		if (runParser) {
			System.out.println("Starting Parser");
			String URLStringToUse = "http://www.dmm.co.jp/mono/dvd/-/detail/=/cid=juc696/";

			Document doc;
			try {
				// TODO find out something to do with the timeout 0
				doc = Jsoup.connect(URLStringToUse).timeout(0).get();
				System.out.println("Opened URL: " + URLStringToUse);
				Movie scrapedMovie = new Movie(new DmmParsingProfile(doc));
				return scrapedMovie;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Movie initializeMovieActionJav() {
		boolean runParser = true;
		if (runParser) {
			System.out.println("Starting Parser");
			// String URLStringToUse =
			// "http://www.actionjav.com/title.cfm?iid=15015"; //has plot
			String URLStringToUse = "http://www.actionjav.com/title.cfm?iid=24770"; // no
																					// plot

			Document doc;
			try {
				// TODO find out something to do with the timeout 0
				doc = Jsoup.connect(URLStringToUse).timeout(0).get();
				System.out.println("Opened URL: " + URLStringToUse);
				Movie scrapedMovie = new Movie(new ActionJavParsingProfile(doc));
				return scrapedMovie;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Movie initializeMovieActionJav(Document doc) {
		boolean runParser = true;
		if (runParser) {
				// TODO find out something to do with the timeout 0
				Movie scrapedMovie = new Movie(new ActionJavParsingProfile(doc));
				return scrapedMovie;
		}
		return null;
	}
	
	public static Movie initializeMovieSquarePlus(Document doc) {
		boolean runParser = true;
		if (runParser) {
				// TODO find out something to do with the timeout 0
				Movie scrapedMovie = new Movie(new SquarePlusParsingProfile(doc));
				return scrapedMovie;
		}
		return null;
	}
	
	public static Movie initializeMovieJavZoo(Document doc){
		boolean runParser = true;
		if(runParser)
		{
			Movie scrapedMovie = new Movie(new JavZooParsingProfile(doc));
			return scrapedMovie;
		}
		return null;
	}

}
