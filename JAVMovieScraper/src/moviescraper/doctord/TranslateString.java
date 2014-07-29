package moviescraper.doctord;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TranslateString {
	
	public static String translateStringJapaneseToEnglish(String japaneseString)
	{
		final String encodingType = "UTF-8";
		try {
			japaneseString = replaceAllKnownTranslations(japaneseString);
			String translateBaseURL = "http://translate.google.com/?sl=ja&tl=en&js=n&prev=_t&hl=en&ie=utf-8&eotf=1&text=";
			String postURLString = "&file=";
			int urlLengthLimit = 2038;
			int totalUrlLength = URLEncoder.encode(japaneseString, encodingType).length() + translateBaseURL.length() + postURLString.length();
			//There's a 2000 character limit on URLs, so we may need to truncate in case we try to do something dumb like paste in some really long article
			if (totalUrlLength >= urlLengthLimit)
			{
				int numberOfCharactersToGetRidOf = totalUrlLength - urlLengthLimit;
				int newStringLength = (numberOfCharactersToGetRidOf / 3); //each unicode character corresponds to 3 characters in the URL
				//maybe it had multi-byte Kanji or weird characters and it took more than 3 URL characters per string? This workaround seems to fix this issue, but more testing is needed
				if(newStringLength > japaneseString.length())
				{
					//divide by 9 because I think that's how big multi-byte kanji are in a URL? For now this seems to work but it may be worth revisting this later
					newStringLength = numberOfCharactersToGetRidOf / 9;
				}
				japaneseString = japaneseString.substring(newStringLength);
			}
					
			String translationServicePostURL = translateBaseURL + URLEncoder.encode(japaneseString, encodingType) + postURLString;
			//System.out.println("URLToPost: " + translationServicePostURL);
			
			/*
			byte[] encodedBytes = Base64.encodeBase64(japaneseString.getBytes());
			System.out.println("encodedBytes " + new String(encodedBytes));
			byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
			System.out.println("decodedBytes " + new String(decodedBytes));
			
			String encodedString = Base64.encodeBase64String(japaneseString.getBytes());
			System.out.println("Encoded String: " + encodedString);
			
			System.out.println("Japanese text (encoded as URL): " + URLEncoder.encode(japaneseString, "UTF-8"));
			*/
			//System.out.println("Encoded string: " + DatatypeConverter.printBase64Binary(japaneseStringAsByteArray));
			/*Document doc = Jsoup.connect(translateBaseURL)
					.data("sl","ja") //source language is japanese
					.data("tl","en") //translate to english
					.data("js","n") // i have no idea what these next few parameters do
					.data("prev","_t")
					.data("hl","en")
					.data("ie","utf-8") //intended encoding is utf-8
					.data("eotf","1")
					//.data("text", Base64.encode)
					.data("text", URLEncoder.encode(japaneseString, "UTF-8"))
					//.data("text", japaneseString)
					.data("file", "")
					.referrer("http://translate.google.com")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
					.post();
					*/
			Document doc = Jsoup.connect(translationServicePostURL).referrer("http://translate.google.com").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(0).post();
			//System.out.println(doc);
			Element translatedTextElement = doc.select(".short_text").first();
			if (translatedTextElement == null)
				translatedTextElement = doc.select(".long_text").first();
			//System.out.println (translatedTextElement);
			if(translatedTextElement == null)
				return "";
			else return translatedTextElement.text();
			//System.out.println("Selected Item:" + translatedTextElement.text());
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ""; //return a blank translation in case of error for now
	}

	//Sometimes we just know some string isn't going to be translated right (a good example is a person's name) and we want to override the web translation engine for this string
	//in that case. make a call to replaceAll with the japanese and english equivalent so we can manually override things
	private static String replaceAllKnownTranslations(String japaneseString) {
		String returnString = japaneseString;
		returnString = japaneseString.replaceAll("つぼみ", "Tsubomi");
		returnString = japaneseString.replaceAll("芦名ユリア", "Yuria Ashina");
		returnString = japaneseString.replaceAll("あいださくら", "Sakura Aida");
		returnString = japaneseString.replaceAll("野原ニコ", "Nico Nohara");
		return returnString;
	}

}
