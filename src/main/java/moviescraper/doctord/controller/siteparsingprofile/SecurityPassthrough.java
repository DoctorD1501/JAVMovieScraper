package moviescraper.doctord.controller.siteparsingprofile;

import org.jsoup.nodes.Document;

import moviescraper.doctord.model.SearchResult;

/**
 * An interface a SiteParsingProfile can implement which allows it to click through links that 
 * websites might put in (such as Captchas, verification pages, etc) to check if you are a robot
 *
 */
public interface SecurityPassthrough {

	/**
	 * Examines the passed in document to determine if we need to run the security passthrough method on it
	 * @param document - the document to examine. should already be downloaded with html of the target page you are trying to get
	 * @return - true if we need to run the security passthrough method, false otherwise
	 */
	public boolean requiresSecurityPassthrough(Document document);

	/**
	 * Given the passed in document, clicks any sort of captchas to get to the required page,
	 * downloads it, and returns it
	 * @param document - the document of the page which requires security
	 * @param originalSearchResult - the original search result that was used to generate the document. we will try to go to this page
	 * after solving the captcha
	 * @return the document of the new downloaded page. It's ok to download the new page synchronously
	 * since the scraping process should be happening in its own thread anyways.
	 */
	public Document runSecurityPassthrough(Document document, SearchResult originalSearchResult);

}
