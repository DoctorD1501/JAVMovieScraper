package moviescraper.doctord.controller.languagetranslation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TranslateString {
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
		StringBuilder englishStringBuilder = new StringBuilder();
		try {
			Map<String, String> data = new HashMap<>();
			data.put("text", japaneseKanjiString);
			data.put("lang_from", "ja");
			data.put("resulsts", "");
			data.put("lang", "en");
			Document doc = Jsoup.connect("https://grammarchecker.net/translate/ajax.php").referrer("https://grammarchecker.net/translate/")
			        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).data(data).post();
			englishStringBuilder.append(doc.select("#results").first().text());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return englishStringBuilder.toString(); //return a blank translation in case of error for now
	}
}
