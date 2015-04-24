package moviescraper.doctord;
import java.io.IOException;
import java.util.ArrayList;

import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestJavLibSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//http://www.javlibrary.com/en/vl_searchbyid.php
		try {
			/*Connection.Response loginForm = Jsoup.connect("http://www.javlibrary.com/en/vl_searchbyid.php")
			        .method(Connection.Method.GET)
			        .userAgent("Mozilla")
			        .execute();*/
			/*Connection.Response response = Jsoup.connect("http://www.javlibrary.com/en/vl_searchbyid.php&keyword=SNIS")
						.method(Connection.Method.GET)
			            .userAgent("Mozilla")
			            //.cookies(loginForm.cookies())
			            .execute();
			 */
			ArrayList<String> linksList = new ArrayList<String>();
			String currentlySelectedLang = "en";
			String searchTerm = "SNIS-091";
			String websiteURLBegin = "http://www.javlibrary.com/" + currentlySelectedLang;
			Document doc = Jsoup.connect("http://www.javlibrary.com/en/vl_searchbyid.php?keyword=" + searchTerm).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
			//http://www.javlibrary.com/en/vl_searchbyid.php?keyword=SNIS
			//The search found the page directly
			if(doc.baseUri().contains("/?v="))
			{
				linksList.add(doc.baseUri());
				System.out.println("Added " + doc.baseUri());
			}
			else
			{
				//The search didn't find an exact match and took us to the search results page
				Elements videoLinksElements = doc.select("div.video a");
				for(Element videoLink : videoLinksElements)
				{
					String currentLink = videoLink.attr("href");
					if(currentLink.length() > 1)
					{
						String fullLink = websiteURLBegin + currentLink.substring(1);
						linksList.add(fullLink);
						System.out.println("Added " + fullLink);
					}
				}
			}
			//System.out.println(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
