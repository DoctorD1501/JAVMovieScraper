package moviescraper.doctord.SiteParsingProfile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
import moviescraper.doctord.TranslateString;
import moviescraper.doctord.dataitem.Actor;
import moviescraper.doctord.dataitem.Director;
import moviescraper.doctord.dataitem.Genre;
import moviescraper.doctord.dataitem.ID;
import moviescraper.doctord.dataitem.MPAARating;
import moviescraper.doctord.dataitem.OriginalTitle;
import moviescraper.doctord.dataitem.Outline;
import moviescraper.doctord.dataitem.Plot;
import moviescraper.doctord.dataitem.Rating;
import moviescraper.doctord.dataitem.Runtime;
import moviescraper.doctord.dataitem.Set;
import moviescraper.doctord.dataitem.SortTitle;
import moviescraper.doctord.dataitem.Studio;
import moviescraper.doctord.dataitem.Tagline;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class CaribbeancomPremiumParsingProfile extends SiteParsingProfile {
	
	private Document japanesePage;

	@Override
	public Title scrapeTitle() {
		Element titleElement = document
				.select("title")
				.first();
		if(titleElement == null)
			return new Title("");
		String titleElementText = WordUtils.capitalize(titleElement.text());
		
		//we want to add a space before the first parenthesis to make the title look a bit better
		if(titleElementText.contains("("))
		{
			int indexOfFirstLeftParen = titleElementText.indexOf('(');
			titleElementText = titleElementText.substring(0,indexOfFirstLeftParen) + " " + titleElementText.substring(indexOfFirstLeftParen);
		}
		
		return new Title(titleElementText);
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return new OriginalTitle("");
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return new SortTitle("");
	}

	@Override
	public Set scrapeSet() {
		try {
			//the studio is not on the english version of this page, so we need to go to the japanese one
			Document japanesePage = getJapanesePage();
			if (japanesePage != null)
			{
				Element setElement = japanesePage.select("div.movie-info dl dt:contains(シリーズ:) ~ dd a").first();
				if(setElement != null)
				{
					String setElementTranslatedText = TranslateString.translateStringJapaneseToEnglish(setElement.text().trim());
					if(setElementTranslatedText != null && setElementTranslatedText.length() > 0)
						return new Set(setElementTranslatedText);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Set("");
	}

	@Override
	public Rating scrapeRating() {
		// this site does not have ratings, so just return some default values
		return new Rating(0, "0");
	}

	@Override
	public Year scrapeYear() {
		Element yearElement = document
				.select("tr td:contains(Update:) ~ td:contains(-)")
				.first();
		if(yearElement != null && yearElement.text().length() >= 4)
		{
			return new Year(yearElement.text().substring(0,4));
		}
		else return new Year("");
	}

	@Override
	public Top250 scrapeTop250() {
		// This type of info doesn't exist on this site
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		// This type of info doesn't exist on this site
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		// This type of info doesn't exist on this site
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
		//This type of info doesn't exist on this site
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		//This type of info doesn't exist on this site
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		//This type of info doesn't exist on this site
		return new Runtime("");
	}

	@Override
	public Thumb[] scrapePosters() {
		Element posterElement = document
				.select("td.detail_main a[href*=/images/")
				.first();
		if(posterElement != null)
		{
			Thumb returnResults[] = new Thumb[1];
			try {
				returnResults[0] = new Thumb(posterElement.attr("abs:href"));
				return returnResults;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return new Thumb[0];
			}
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		//Believe it or not, the fanart (dvd cover) exists, but is normally only set as the preview of the trailer
		//it follows a predictable URL structure though, so we can grab it anyways :)
		
		//start by grabbing the ID part of the current page
		String urlOfCurrentPage = document.location();
		if(urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages"))
		{
			urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/moviepages/"), "");
			String movieID = urlOfCurrentPage.replaceFirst(Pattern.quote("/index.html"), "");
			if(urlOfCurrentPage.length() > 1)
			{
				String imageURL = "http://www.caribbeancompr.com/moviepages/" + movieID + "/images/l_l.jpg";
				try {
					Thumb fanartThumbs[] = new Thumb[1];
					Thumb fanartThumb = new Thumb(imageURL);
					fanartThumbs[0] = fanartThumb;
					return fanartThumbs;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return new Thumb[0];
				}
				
			}
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		String urlOfCurrentPage = document.location();
		if(urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages"))
		{
			urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/moviepages/"), "");
			String movieID = urlOfCurrentPage.replaceFirst(Pattern.quote("/index.html"), "");
			if(urlOfCurrentPage.length() > 1)
			{
				Thumb extraFanartThumbs[] = new Thumb[3];
				for(int i = 1; i < 4; i++)
				{
					String extraThumbURL = "http://en.caribbeancompr.com/moviepages/" + movieID + "/images/l/00" + i + ".jpg";
					try {
						Thumb extraFanartThumb = new Thumb(extraThumbURL);
						extraFanartThumbs[i-1] = extraFanartThumb;
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return new Thumb[0];
					}
				}
				return extraFanartThumbs;
			}
		}
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Element idElement = document
				.select("tr td:contains(Movie ID:) ~ td:contains(_)")
				.first();
		if(idElement != null && idElement.text().length() > 0)
		{
			return new ID(idElement.text());
		}
		else return new ID("");
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> genresReturned = new ArrayList<Genre>();
		Document japanesePage;
		try {
			japanesePage = getJapanesePage();

			Elements genreElementsInJapanese = japanesePage.select("dl.movie-info-cat dd a");
			for(Element currentGenre : genreElementsInJapanese)
			{
				//the genre is coded as a specific webpage number. we can call our helper function to translate a number
				//like 1_1.html into the actual english genre this represents
				String currentGenreCode = currentGenre.attr("href");
				if(currentGenreCode.contains("/"))
				{
					//currentGenreCode will just be the numerical part after this function call (e.g. 1_1)
					currentGenreCode = currentGenreCode.substring(currentGenreCode.lastIndexOf('/')).replaceFirst(Pattern.quote(".html"),"").replaceFirst(Pattern.quote("/"), "");
					String englishGenreName = convertGenreCodeToDescription(currentGenreCode);
					if(englishGenreName != null && !genresReturned.contains(englishGenreName))
						genresReturned.add(new Genre(englishGenreName));
				}
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return genresReturned;
	}

	private String convertGenreCodeToDescription(String currentGenreCode) {
		switch(currentGenreCode)
		{
		case "1_1": return "Pornstar";
		case "2_1": return "School Girls";
		case "3_1": return "Amateur";
		case "4_1": return "Sister";
		case "5_1": return "Lolita";
		case "6_1": return "MILF / Housewife";
		case "8_1": return "Slut";
		case "9_1": return "Big Tits";
		case "10_1": return "Gonzo";
		case "11_1": return "Creampie";
		case "12_1": return "Squirting";
		case "13_1": return "Orgy";
		case "14_1": return "Cosplay";
		case "15_1": return "Teen";
		case "16_1": return "Gal";
		case "17_1": return "Idol";
		case "18_1": return "Teacher";
		case "20_1": return "Big Tits";
		case "21_1": return "Swimsuit";
		case "22_1": return "Bondage";
		case "24_1": return "Outdoor Exposure";
		case "26_1": return "Documentary";
		case "27_1": return "Seduction";
		case "28_1": return "S&M";
		case "29_1": return "Shaved Pussy";
		case "30_1": return "Restraints";
		case "31_1": return "Masturbation";
		case "32_1": return "Vibrator";
		case "33_1": return "Fucking";
		case "34_1": return "Blowjob";
		case "35_1": return "Semen";
		case "36_1": return "Cum Swallow";
		case "37_1": return "Golden Shower";
		case "38_1": return "Handjob";
		case "39_1": return "69";
		case "40_1": return "Anal";
		case "42_1": return "Cunnilingus";
		case "43_1": return "Best / VA";
		case "44_1": return "Bareback Fucking";
		case "45_1": return "Nurse";
		case "46_1": return "Bloomers";
		case "47_1": return "Molester";
		case "49_1": return "White Girl";
		case "51_1": return "Anime";
		case "52_1": return "Insult";
		case "53_1": return "First Time Porn";
		case "56_1": return "Uniforms";
		case "55_1": return "Pornstar";
		case "62_1": return "Ass";
		case "64_1": return "Legs";
		case "65_1": return "Bukkake";
		case "67_1": return "Deep Throating";
		case "69_1": return "Transsexual";
		case "70_1": return "Teen";
		case "71_1": return "Look-alike";
		case "72_1": return "Small Tits";
		case "73_1": return "Slender";
		case "74_1": return "Car Sex";
		case "75_1": return "Shaving";
		case "77_1": return "Dirty Words";
		case "78_1": return "Cumshot";
		case "79_1": return "Facial";
		case "80_1": return "Apron";
		case "81_1": return "Glasses";
		case "82_1": return "OL";
		case "83_1": return "Maid";
		case "84_1": return "Yukata / Kimono";
		}
		System.out.println("No genre match for " + currentGenreCode);
		return null;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		Element actorElement = document
				.select("tr td:contains(Starring:) ~ td a")
				.first();
		String urlOfCurrentPage = document.location();
		String actorThumbURL = null;
		if(actorElement != null)
		{
			String actorName = WordUtils.capitalize(actorElement.attr("title"));
			//get the actor thumbnail associated with this page
			if(urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages"))
			{
				urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/moviepages/"), "http://www.caribbeancompr.com/moviepages/");
				actorThumbURL = urlOfCurrentPage.replaceFirst(Pattern.quote("/index.html"), "/images/n.jpg");
			}
			//we will try to sort out multiple actors into seperate ones if the number of words is even so that each 2 words can be a complete name
			String [] actorNamesSplitUp = actorName.split(" ");
			if(actorNamesSplitUp.length >= 2 && (actorNamesSplitUp.length % 2 == 0))
			{
				for (int i = 0; i < actorNamesSplitUp.length; i+=2)
				{
					String currentActorName = actorNamesSplitUp[i] + " " + actorNamesSplitUp[i+1];
					try {
						actorList.add(new Actor(currentActorName,"",new Thumb(actorThumbURL)));
					} catch (MalformedURLException e) {
						actorList.add(new Actor(currentActorName,"",null));
					}
				}
				return actorList;
			}
			else
			{
				actorList.add(new Actor(actorName,"",null));
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		return new ArrayList<Director>();
	}
	@Override
	public Trailer scrapeTrailer()
	{
		try {
			Document japanesePage = getJapanesePage();
			Element trailerElement = japanesePage.select("div.movie-download div.sb-btn a").first();
			if(trailerElement != null)
			{
				String trailerLink = trailerElement.attr("href");
				System.out.println("Trailer: " + trailerLink);
				if(trailerLink != null && trailerLink.length() > 0)
					return new Trailer(trailerLink);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Trailer("");
	}
	@Override
	public Studio scrapeStudio() {

			try {
				//the studio is not on the english version of this page, so we need to go to the japanese one
				Document japanesePage = getJapanesePage();
				if (japanesePage != null)
				{
					Element studioElement = japanesePage.select("div.movie-info dl dt:contains(スタジオ:) ~ dd a").first();
					if(studioElement != null)
					{
						String studioElementTranslatedText = TranslateString.translateStringJapaneseToEnglish(studioElement.text().trim());
						if(studioElementTranslatedText != null && studioElementTranslatedText.length() > 0)
							return new Studio(studioElementTranslatedText);
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return new Studio("");
	}

	@Override
	public String createSearchString(File file) {
		String fileNameNoExtension = findIDTagFromFile(file);
		return fileNameNoExtension;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		return getLinksFromGoogle(searchString, "http://en.caribbeancompr.com/eng/moviepages/");
	}

	private Document getJapanesePage() throws IOException {
		if(japanesePage == null)
		{
			String urlOfCurrentPage = document.location();
			if(urlOfCurrentPage != null && urlOfCurrentPage.contains("moviepages"))
			{
				//the genres are only available on the japanese version of the page
				urlOfCurrentPage = urlOfCurrentPage.replaceFirst(Pattern.quote("http://en.caribbeancompr.com/eng/"), "http://www.caribbeancompr.com/");
				if(urlOfCurrentPage.length() > 1)
				{
						japanesePage = Jsoup.connect(urlOfCurrentPage).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
						return japanesePage;
				}
			}
			return null;
		}
		else return japanesePage;
	}

}
