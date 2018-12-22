package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;

public class Data18SharedMethods {

	//tries to guess what the viewer url is from a passed in url
	public static URL getReferrerURLFromImageURL(URL url) {
		if (url != null && url.toString().endsWith(".jpg")) {
			int indexOfLastSlash = url.toString().lastIndexOf('/');
			if (indexOfLastSlash > 0) {

				String tempUrlString = url.toString().substring(0, indexOfLastSlash);
				String imageNumber = url.toString().substring(indexOfLastSlash);
				if (imageNumber != null && imageNumber.endsWith(".jpg") && imageNumber.length() > 5) {
					imageNumber = imageNumber.substring(1, imageNumber.length() - 4);
				}
				int indexOfSecondToLastSlash = tempUrlString.lastIndexOf('/');
				if (indexOfSecondToLastSlash > 0 && tempUrlString.length() > 1) {
					String contentID = tempUrlString.substring(indexOfSecondToLastSlash + 1);
					URL referrerURL;
					try {
						referrerURL = new URL("http://www.data18.com/viewer/" + "1" + contentID + "/" + imageNumber);
						return referrerURL;
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return url;
	}

	//Used to implement the SecurityPassthrough interface for both data18 scrapers
	public static boolean requiresSecurityPassthrough(Document document) {
		if (document != null && document.html().contains("Security page: [data18.com]")) {
			System.out.println("Found security page for data 18; attempting to bypass");
			return true;
		}
		return false;
	}

	//Used to implement the SecurityPassthrough interface for both data18 scrapers
	public static Document runSecurityPassthrough(Document document, SearchResult originalSearchResult) {
		//find the first link in the document, download the href, then try to download the original result again
		if (document != null) {
			Element firstLink = document.select("a").first();
			if (firstLink != null && firstLink.attr("href") != null) {
				Document captchaSolved = SiteParsingProfile.getDocument(new SearchResult(firstLink.attr("href")));
				if (captchaSolved != null) {
					return SiteParsingProfile.getDocument(originalSearchResult);
				}
			}
		}
		return document;
	}
}
