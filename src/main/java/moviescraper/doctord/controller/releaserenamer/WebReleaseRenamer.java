package moviescraper.doctord.controller.releaserenamer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.text.WordUtils;

public class WebReleaseRenamer extends ReleaseRenamer {

	private List<CSVRecord> removeTheseWords;
	private List<CSVRecord> replaceFirstInstanceOfTheseWords;

	public WebReleaseRenamer() throws IOException {
		removeTheseWords = readWordsToRemoveFromCSV();
		replaceFirstInstanceOfTheseWords = readSiteNamesToReplaceFromCSV();
	}

	@Override
	public String getCleanName(String filename) {
		String cleanFileName = filename.toLowerCase();
		cleanFileName = replaceSeperatorsWithSpaces(cleanFileName);
		/* 
		 * remove things from the filename which are usually not part of the 
		 * scene / movie name such as par2, xvid, divx, etc
		 */
		cleanFileName += " "; //add a space at the end so we our regex works in the next step for the last word
		for (CSVRecord wordsToRemove : removeTheseWords) {
			//putting spaces in front of it so we only get an actual word, not parts of a word
			String wordToRemove = wordsToRemove.get(0).toLowerCase();
			cleanFileName = cleanFileName.replaceFirst("\\b" + wordToRemove + "\\b", "");
		}
		cleanFileName = cleanFileName.trim();
		/* 
		 * often times files are released with abbreviations in their name which 
		 * messes up doing google searches on them, so we'll do a substitution to get the full name
		 */
		boolean doneReplacingabbreviation = false;
		for (CSVRecord siteNameReplacement : replaceFirstInstanceOfTheseWords) {
			/*
			 * Our format in this file is that the first word on each line is the full name
			 * of the abbreviation and each subsequent comma seperated entry on the line
			 * is an abbreviation
			 */
			String fullSiteName = siteNameReplacement.get(0);
			//WebReleaseRenamer.System.out.println("FullSiteName = " + fullSiteName);
			for (String abbreviation : siteNameReplacement) {
				abbreviation = abbreviation.replace("\"", "");
				//System.out.println("abbreviation = " + abbreviation.trim().toLowerCase());
				if (cleanFileName.startsWith(abbreviation.trim().toLowerCase() + " ") && abbreviation.trim().length() > 0) {
					//System.out.println("Found match = " + abbreviation.trim().toLowerCase());
					cleanFileName = cleanFileName.replaceFirst(Pattern.quote(abbreviation.trim().toLowerCase() + " "), fullSiteName + " ");
					doneReplacingabbreviation = true;
					break; //just assume we want to only replace one abbreviaton
				}
				//System.out.println("CFN: " + cleanFileName);
			}
			if (doneReplacingabbreviation)
				break;
			//System.out.println(siteNameReplacement);
		}
		//Fix up the case and trim it - not needed for search but it just looks better :)
		cleanFileName = WordUtils.capitalize(cleanFileName).trim();
		return cleanFileName;
	}

	public List<CSVRecord> readWordsToRemoveFromCSV() throws IOException {
		return readFromCSVFile("/moviescraper/doctord/controller/releaserenamer/WordsToRemove.csv");
	}

	public List<CSVRecord> readSiteNamesToReplaceFromCSV() throws IOException {
		return readFromCSVFile("/moviescraper/doctord/controller/releaserenamer/SiteNameAbbreviations.csv");
	}

	public List<CSVRecord> readFromCSVFile(String filePath) throws IOException {
		CSVFormat format = CSVFormat.RFC4180.withDelimiter(',').withCommentMarker('#');
		try (InputStream inputStream = getClass().getResourceAsStream(filePath); CSVParser parser = new CSVParser(new InputStreamReader(inputStream), format);) {
			List<CSVRecord> csvRecords = parser.getRecords();
			return csvRecords;
		}
	}

}
