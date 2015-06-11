package moviescraper.doctord.controller.languagetranslation;

/**
 * JapaneseCharacter contains static functions to do various tests
 * on characters to determine if it is one of the various types of
 * characters used in the japanese writing system.
 * <p/>
 * There are also a functions to translate between Katakana, Hiragana,
 * and Romaji.
 *
 * @author Duane J. May <djmay@mayhoo.com>
 * @version $Id: JapaneseCharacter.java,v 1.2 2002/04/20 18:10:24 djmay Exp $
 * @since 10:37 AM - 6/3/14
 *
 * @see <a href="http://sourceforge.net/projects/kanjixml/">http://sourceforge.net/projects/kanjixml/</a>
 */
public class JapaneseCharacter {

	/**
	 * Version information
	 */
	@SuppressWarnings("unused")
	private final static String VERSION =
			"$Id: JapaneseCharacter.java,v 1.2 2002/04/20 18:10:24 djmay Exp $";

	/**
	 * Determines if this character is a Japanese Kana.
	 */
	public static boolean isKana(char c) {
		return (isHiragana(c) || isKatakana(c));
	}

	/**
	 * Determines if this character is one of the Japanese Hiragana.
	 */
	public static boolean isHiragana(char c) {
		return (('\u3041' <= c) && (c <= '\u309e'));
	}

	/**
	 * Determines if this character is one of the Japanese Katakana.
	 */
	public static boolean isKatakana(char c) {
		return (isHalfWidthKatakana(c) || isFullWidthKatakana(c));
	}

	/**
	 * Determines if this character is a Half width Katakana.
	 */
	public static boolean isHalfWidthKatakana(char c) {
		return (('\uff66' <= c) && (c <= '\uff9d'));
	}

	/**
	 * Determines if this character is a Full width Katakana.
	 */
	public static boolean isFullWidthKatakana(char c) {
		return (('\u30a1' <= c) && (c <= '\u30fe'));
	}

	/**
	 * Determines if this character is a Kanji character.
	 */
	public static boolean isKanji(char c) {
		if (('\u4e00' <= c) && (c <= '\u9fa5')) {
			return true;
		}
		if (('\u3005' <= c) && (c <= '\u3007')) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if this character could be used as part of
	 * a romaji character.
	 */
	public static boolean isRomaji(char c) {
		if (('\u0041' <= c) && (c <= '\u0090'))
			return true;
		else if (('\u0061' <= c) && (c <= '\u007a'))
			return true;
		else if (('\u0021' <= c) && (c <= '\u003a'))
			return true;
		else if (('\u0041' <= c) && (c <= '\u005a'))
			return true;
		else
			return false;
	}

	/**
	 * Translates this character into the equivalent Katakana character.
	 * The function only operates on Hiragana and always returns the
	 * Full width version of the Katakana. If the character is outside the
	 * Hiragana then the origianal character is returned.
	 */
	public static char toKatakana(char c) {
		if (isHiragana(c)) {
			return (char) (c + 0x60);
		}
		return c;
	}

	/**
	 * Translates this character into the equivalent Hiragana character.
	 * The function only operates on Katakana characters
	 * If the character is outside the Full width or Half width
	 * Katakana then the origianal character is returned.
	 */
	public static char toHiragana(char c) {
		if (isFullWidthKatakana(c)) {
			return (char) (c - 0x60);
		} else if (isHalfWidthKatakana(c)) {
			return (char) (c - 0xcf25);
		}
		return c;
	}

	/**
	 * Translates this character into the equivalent Romaji character.
	 * The function only operates on Hiragana and Katakana characters
	 * If the character is outside the given range then
	 * the origianal character is returned.
	 * <p/>
	 * The resulting string is lowercase if the input was Hiragana and
	 * UPPERCASE if the input was Katakana.
	 */
	public static String toRomaji(char c) {
		if (isHiragana(c)) {
			return lookupRomaji(c);
		} else if (isKatakana(c)) {
			c = toHiragana(c);
			String str = lookupRomaji(c);
			return str.toUpperCase();
		}
		return String.valueOf(c);
	}

	/**
	 * The array used to map hirgana to romaji.
	 */
	protected static String romaji[] = {
		"a", "a",
		"i", "i",
		"u", "u",
		"e", "e",
		"o", "o",

		"ka", "ga",
		"ki", "gi",
		"ku", "gu",
		"ke", "ge",
		"ko", "go",

		"sa", "za",
		"shi", "ji",
		"su", "zu",
		"se", "ze",
		"so", "zo",

		"ta", "da",
		"chi", "ji",
		"tsu", "tsu", "zu",
		"te", "de",
		"to", "do",

		"na",
		"ni",
		"nu",
		"ne",
		"no",

		"ha", "ba", "pa",
		"hi", "bi", "pi",
		"fu", "bu", "pu",
		"he", "be", "pe",
		"ho", "bo", "po",

		"ma",
		"mi",
		"mu",
		"me",
		"mo",

		"a", "ya",
		"u", "yu",
		"o", "yo",

		"ra",
		"ri",
		"ru",
		"re",
		"ro",

		"wa", "wa",
		"wi", "we",
		"o",
		"n",

		"v",
		"ka",
		"ke"

	};

	/**
	 * Access the array to return the correct romaji string.
	 */
	private static String lookupRomaji(char c) {
		return romaji[c - 0x3041];
	}
	
	public static String convertToRomaji(String input)
	{
		if (input == null || input.length() == 0)
			return "";
		
		StringBuilder out = new StringBuilder();
		
		
		for (int i = 0; i < input.length(); i++)
		{
			char ch = input.charAt(i);
			if(JapaneseCharacter.isHiragana(ch))
				out.append(JapaneseCharacter.toRomaji(ch));
			else if(JapaneseCharacter.isKatakana(ch))
				out.append(JapaneseCharacter.toRomaji(ch));
			else return null;
		}
		return out.toString();
		
		
	}
	
	/**
	 * Returns true if one or more letters in the word are katakana, kanji, or hiragana
	 */
	public static boolean containsJapaneseLetter(String word)
	{
		for(int i = 0; i < word.length(); i++)
		{
			char currentChar = word.charAt(i);
			if(JapaneseCharacter.isHiragana(currentChar) || JapaneseCharacter.isKanji(currentChar) 
					|| JapaneseCharacter.isKatakana(currentChar))
			{
				return true;
			}
		}
		return false;
	}
}
