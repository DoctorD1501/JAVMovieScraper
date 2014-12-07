package moviescraper.doctord.ReleaseRenamer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.text.WordUtils;

public class WebReleaseRenamer extends ReleaseRenamer {

	private List<CSVRecord> removeTheseWords;
	private List<CSVRecord> replaceFirstInstanceOfTheseWords;

	public WebReleaseRenamer() throws IOException
	{
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
		for(CSVRecord wordsToRemove : removeTheseWords)
		{
			cleanFileName = cleanFileName.replaceFirst(wordsToRemove.get(0), "");
		}

		/* 
		 * often times files are released with abbreviations in their name which 
		 * messes up doing google searches on them, so we'll do a substitution to get the full name
		 */
		boolean doneReplacingabbreviation = false;
		for(CSVRecord siteNameReplacement : replaceFirstInstanceOfTheseWords)
		{
			/*
			 * Our format in this file is that the first word on each line is the full name
			 * of the abbreviation and each subsequent comma seperated entry on the line
			 * is an abbreviation
			 */
			String fullSiteName = siteNameReplacement.get(0);
			//WebReleaseRenamer.System.out.println("FullSiteName = " + fullSiteName);
			for(String abbreviation : siteNameReplacement)
			{
				//System.out.println("abbreviation = " + abbreviation);
				if(cleanFileName.startsWith(abbreviation.trim().toLowerCase()) && abbreviation.trim().length() > 0)
				{
					//System.out.println("Found match = " + abbreviation.trim().toLowerCase());
					cleanFileName = cleanFileName.replaceFirst(Pattern.quote(abbreviation.trim().toLowerCase()), fullSiteName);
					doneReplacingabbreviation = true;
					break; //just assume we want to only replace one abbreviaton
				}
				//System.out.println("CFN: " + cleanFileName);
			}
			if(doneReplacingabbreviation)
				break;
			//System.out.println(siteNameReplacement);
		}
		//Fix up the case - not needed for search but it just looks better :)
		cleanFileName = WordUtils.capitalize(cleanFileName);
		return cleanFileName;
	}

	public List<CSVRecord> readWordsToRemoveFromCSV() throws IOException
	{
		return readFromCSVFile("/moviescraper/doctord/ReleaseRenamer/WordsToRemove.csv");
	}

	public List<CSVRecord> readSiteNamesToReplaceFromCSV() throws IOException{
		return readFromCSVFile("/moviescraper/doctord/ReleaseRenamer/SiteNameAbbreviations.csv");
	}

	public List<CSVRecord> readFromCSVFile(String filePath) throws IOException
	{
		//URL url = getClass().getResource(filePath);
		//System.out.println("filePath = " + filePath);
		InputStream inputStream = getClass().getResourceAsStream(filePath);
		//System.out.println("inputStream = " + inputStream);
		//System.out.println("URL in fixer = " + url);
		//File file = new File(url.getPath());
		//FileReader fileReader = new FileReader(file);
		CSVFormat format = CSVFormat.RFC4180.withDelimiter(',').withCommentMarker('#');
		CSVParser parser = new CSVParser(new InputStreamReader(inputStream), format);
		List<CSVRecord> csvRecords = parser.getRecords();
		parser.close();
		return csvRecords;
	}

}
