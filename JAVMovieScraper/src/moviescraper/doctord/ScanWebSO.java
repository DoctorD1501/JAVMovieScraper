package moviescraper.doctord;

import java.io.IOException;
import java.net.URLDecoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class ScanWebSO 
{
public static void main (String args[])
{
    Document doc;
    try{
    	String encodingScheme = "UTF-8";
    	String site = "javlibrary.com/en/";
    	String searchQuery = "MIDE-077";
    	String queryToEncode = "site:" + site + " " + searchQuery;
    	String encodedSearchQuery = URLEncoder.encode(queryToEncode, encodingScheme);
    	System.out.println(encodedSearchQuery);
    	
        doc = Jsoup.connect("https://www.google.com/search?as_q=&as_epq="+encodedSearchQuery).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
        Elements links = doc.select("li[class=g]");
        for (Element link : links) {
            //Elements titles = link.select("h3[class=r]");
            //String title = titles.text();

            //Elements bodies = link.select("span[class=st]");
            //String body = bodies.text();
            
            Elements hrefs = link.select("h3.r a");
            String href = hrefs.attr("href");
            href = URLDecoder.decode(href, encodingScheme);
            href = href.replaceFirst(Pattern.quote("/url?q="), "");
            
            System.out.println("Href: " + href);
            //System.out.println("Title: "+title);
            //System.out.println("Body: "+body+"\n");
        }
    }
    catch (IOException e) {
        e.printStackTrace();
    }
}
}
