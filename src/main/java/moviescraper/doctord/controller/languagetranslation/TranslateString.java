package moviescraper.doctord.controller.languagetranslation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TranslateString {

	private static final String japaneseSentenceEnders = "[。？�?]";
	private static final int maxCharsPerRequest = 100;

	/**
	 * @param japanesePersonName - Name of the person to translate. Method works best if the name is hiragana or katakana
	 * @return The name of person in Romaji
	 */
	public static String translateJapanesePersonNameToRomaji(String japanesePersonName) {
		//if we have any kanji at all in the string, we'll have to use google translate
		for (int i = 0; i < japanesePersonName.length(); i++) {
			if (JapaneseCharacter.isKanji(japanesePersonName.charAt(i)))
				return translateStringJapaneseToEnglish(japanesePersonName);
		}
		String romaji = JapaneseCharacter.convertToRomaji(japanesePersonName);
		if (romaji != null) {
			romaji = WordUtils.capitalize(romaji).trim();
			return romaji;
		} else
			return translateStringJapaneseToEnglish(japanesePersonName);
	}

	public static String translateStringJapaneseToEnglish(String japaneseKanjiString) {

		//Our overall approach for translation here is first try to split up string and translate one sentence at a time. 
		//If a sentence is still too long, then just translate part of it at one time
		//We're splitting up strings because there's only so much we can pack in a URL at a time per request

		//contains the original string split up by the japaneseSentenceEnders 
		//This uses lookahead to still keep the punctuation instead of discarding it during the split.
		String[] splitBySentenceEnders = japaneseKanjiString.split("(?=" + japaneseSentenceEnders + ")");

		//Split our sentences into maxCharPerRequest sized chunks, in case we had some super long sentence
		List<List<String>> allSplits = new ArrayList<>();
		for (int i = 0; i < splitBySentenceEnders.length; i++) {
			allSplits.add(splitStringIntoArrayList(splitBySentenceEnders[i], maxCharsPerRequest));
		}
		//flatten out our list so we can look through it easily
		List<String> runTranslationFromThisList = new ArrayList<>(splitBySentenceEnders.length);
		for (List<String> current : allSplits) {
			runTranslationFromThisList.addAll(current);
		}

		String englishStringBuilder = "";
		final String encodingType = "UTF-8";
		for (String japaneseString : runTranslationFromThisList) {
			try {
				japaneseString = replaceAllKnownTranslations(japaneseString);
				String translateBaseURL = "http://translate.google.com/?sl=ja&tl=en&js=n&prev=_t&hl=en&ie=utf-8&eotf=1&text=";
				String postURLString = "&file=";
				int urlLengthLimit = 2038;
				int totalUrlLength = URLEncoder.encode(japaneseString, encodingType).length() + translateBaseURL.length() + postURLString.length();
				//There's a 2000 character limit on URLs, so we may need to truncate in case we try to do something dumb like paste in some really long article
				//This was needed using the old method of translation, but I don't think this if statement should happen anymore.
				//I've kept this code in as a defensive measure in case for some reason it does, at least we'll still get some kind of translation
				//back instead of getting a HTTP error
				if (totalUrlLength >= urlLengthLimit) {
					int numberOfCharactersToGetRidOf = totalUrlLength - urlLengthLimit;
					int newStringLength = (numberOfCharactersToGetRidOf / 3); //each unicode character corresponds to 3 characters in the URL
					//maybe it had multi-byte Kanji or weird characters and it took more than 3 URL characters per string? This workaround seems to fix this issue, but more testing is needed
					if (newStringLength > japaneseString.length()) {
						//divide by 8 because I think that's how big multi-byte kanji are in a URL? For now this seems to work but it may be worth revisting this later
						newStringLength = numberOfCharactersToGetRidOf / 8;
					}
					japaneseString = japaneseString.substring(newStringLength);
				}

				String translationServicePostURL = translateBaseURL + URLEncoder.encode(japaneseString, encodingType) + postURLString;
				Document doc = Jsoup.connect(translationServicePostURL).referrer("http://translate.google.com").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
				        .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
				Element translatedTextElement = doc.select(".short_text").first();
				if (translatedTextElement == null)
					translatedTextElement = doc.select(".long_text").first();
				if (translatedTextElement == null)
					englishStringBuilder += "";
				else
					englishStringBuilder += translatedTextElement.text();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return englishStringBuilder; //return a blank translation in case of error for now
	}

	//Sometimes we just know some string isn't going to be translated right (a good example is a person's name) and we want to override the web translation engine for this string
	//in that case. make a call to replaceAll with the japanese and english equivalent so we can manually override things
	private static String replaceAllKnownTranslations(String japaneseString) {
		String returnString = japaneseString;
		returnString = japaneseString.replaceAll("�?��?��?�", "Tsubomi");
		returnString = japaneseString.replaceAll("芦�??ユリア", "Yuria Ashina");
		returnString = japaneseString.replaceAll("�?��?��?��?��??ら", "Sakura Aida");
		returnString = japaneseString.replaceAll("野原ニコ", "Nico Nohara");
		return returnString;
	}

	private static List<String> splitStringIntoArrayList(String text, int stringSizePerIndex) {
		List<String> strings = new ArrayList<>();
		int index = 0;
		while (index < text.length()) {
			strings.add(text.substring(index, Math.min(index + stringSizePerIndex, text.length())));
			index += stringSizePerIndex;
		}
		return strings;
	}

}
