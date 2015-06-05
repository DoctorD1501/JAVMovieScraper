package moviescraper.doctord.SiteParsingProfile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import moviescraper.doctord.Language;
import moviescraper.doctord.SearchResult;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfile;
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
import moviescraper.doctord.dataitem.Thumb;
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Trailer;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class OnePondoParsingProfile extends SiteParsingProfile implements SpecificProfile {

	//private boolean scrapeInEnglish;
	private String englishPage;
	private String japanesePage;
	
	
	@Override
	public String getParserName() {
		return "1pondo";
	}

	@Override
	public Title scrapeTitle() {
		Element titleElement = document.select("title").first();
		if(titleElement != null)
		{
			String id = scrapeID().getId();
			String title = titleElement.text().trim();
			//replace used for english title
			title = title.replaceAll(Pattern.quote("::"), "-");
			//replace used for japanese title
			title = title.replaceAll(Pattern.quote(":"), "-");
			//old scenes on the site that do no contain the actor name in the title
			if(title.equals("1pondo.tv -"))
				title = title + " " + id;
			else
				title = title + " - " + id;
			return new Title(title);
		}
		return new Title("");
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		//the original title is the japanese title
		if(scrapingLanguage == Language.JAPANESE)
			return new OriginalTitle(scrapeTitle().getTitle());
		else
		{
			Document originalDocument = document;
			try {
				document = Jsoup.connect(japanesePage).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OriginalTitle originalTitle = new OriginalTitle(scrapeTitle().getTitle());
			document = originalDocument;
			return originalTitle;
		}
	}

	@Override
	public SortTitle scrapeSortTitle() {
		// This site has no sort title information
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		// This site has no set information
		return Set.BLANK_SET;
	}

	@Override
	public Rating scrapeRating() {
		// This site has no rating information
		return new Rating(0,"0");
	}

	@Override
	public Year scrapeYear() {
		//Get year from last 2 digits before the underscore in the ID of the movie
		//Add "20" to these digits, so "14" becomes 2014, for example
		//(this is ok because there are no scenes on 1pondo from 1999 or earlier)
		ID movieID = scrapeID();
		if(movieID != null)
		{
			return new Year("20" + movieID.getId().substring(4,6));
		}
		return null;
	}

	@Override
	public Top250 scrapeTop250() {
		//This site has no top250 information
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		//This site has no vote information
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		//This site has no outline for movies
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		//This site has no plot for movies
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		// TODO Auto-generated method stub
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		// TODO Auto-generated method stub
		return Runtime.BLANK_RUNTIME;
	}

	@Override
	public Thumb[] scrapePosters() {
		try {
			ArrayList<Thumb> thumbList = new ArrayList<Thumb>();
			String bannerURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/str.jpg";
			String backgroundURLTwo = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/2.jpg";
			String popupOneURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/1.jpg";
			String popupTwoURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/2.jpg";
			String popupThreeURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/3.jpg";
			String popupFourURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu.jpg";
			if(SiteParsingProfile.fileExistsAtURL(popupOneURL))
				thumbList.add(new Thumb(popupOneURL));
			if(SiteParsingProfile.fileExistsAtURL(popupTwoURL))
				thumbList.add(new Thumb(popupTwoURL));
			if(SiteParsingProfile.fileExistsAtURL(popupThreeURL))
				thumbList.add(new Thumb(popupThreeURL));
			if(SiteParsingProfile.fileExistsAtURL(bannerURL))
				thumbList.add(new Thumb(bannerURL));
			if(SiteParsingProfile.fileExistsAtURL(backgroundURLTwo))
				thumbList.add(new Thumb(backgroundURLTwo));
			if(SiteParsingProfile.fileExistsAtURL(popupFourURL))
				thumbList.add(new Thumb(popupFourURL));
			return thumbList.toArray(new Thumb[thumbList.size()]);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeFanart() {
		try {
			ArrayList<Thumb> thumbList = new ArrayList<Thumb>();
			String bannerURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/str.jpg";
			String backgroundURLOne = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/1.jpg";
			String backgroundURLTwo = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/2.jpg";
			String popupOneURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/1.jpg";
			String popupTwoURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/2.jpg";
			String popupThreeURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu/3.jpg";
			String popupFourURL = "http://www.1pondo.tv/moviepages/" + scrapeID().getId() + "/images/popu.jpg";
			if(SiteParsingProfile.fileExistsAtURL(bannerURL))
				thumbList.add(new Thumb(bannerURL));
			if(SiteParsingProfile.fileExistsAtURL(popupOneURL))
				thumbList.add(new Thumb(popupOneURL));
			if(SiteParsingProfile.fileExistsAtURL(popupTwoURL))
				thumbList.add(new Thumb(popupTwoURL));
			if(SiteParsingProfile.fileExistsAtURL(popupThreeURL))
				thumbList.add(new Thumb(popupThreeURL));
			//combine the two background images together to make the fanart if we are on a page that has split things into two images
			if(SiteParsingProfile.fileExistsAtURL(backgroundURLOne) && SiteParsingProfile.fileExistsAtURL(backgroundURLTwo))
			{
				try {
					/*BufferedImage img1 = ImageIO.read(new URL(backgroundURLOne));
					BufferedImage img2 = ImageIO.read(new URL(backgroundURLTwo));
					BufferedImage joinedImage = joinBufferedImage(img1, img2);
					Thumb joinedImageThumb = new Thumb(backgroundURLTwo);
					joinedImageThumb.setImage(joinedImage);
					//we did an operation to join the images, so we'll need to re-encode the jpgs. set the modified flag to true
					//so we know to do this
					joinedImageThumb.setIsModified(true);*/
					
					Thumb joinedImageThumb = new Thumb(backgroundURLOne, backgroundURLTwo);
					thumbList.add(joinedImageThumb);
				} catch (IOException e) {
					thumbList.add(new Thumb(backgroundURLTwo));
				}


			}
			else if(SiteParsingProfile.fileExistsAtURL(backgroundURLTwo))
				thumbList.add(new Thumb(backgroundURLTwo));
			if(SiteParsingProfile.fileExistsAtURL(popupFourURL))
				thumbList.add(new Thumb(popupFourURL));
			return thumbList.toArray(new Thumb[thumbList.size()]);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Thumb[0];
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return scrapeFanart();
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		//Just get the ID from the page URL by doing some string manipulation
		String baseUri = document.baseUri();
		if(baseUri.length() > 0 && baseUri.contains("1pondo.tv"))
		{
			baseUri = baseUri.replaceFirst("/index.html", "");
			baseUri = baseUri.replaceFirst("/index.htm", "");
			String idFromBaseUri = baseUri.substring(baseUri.lastIndexOf('/')+1);
			return new ID(idFromBaseUri);
		}
		return null;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		//For now, I wasn't able to find any genres on the page
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<Actor>(1);
		Element profileArea = document.select("div#profile-area").first();
		if(profileArea != null)
		{
		String actressThumbURL = profileArea.select("img").attr("src");
		String actressName = profileArea.select(".bgoose h2, .bgg1").text();
			try {
				actorList.add(new Actor(actressName, "", new Thumb(actressThumbURL)));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				actorList.add(new Actor(actressName, "", null));
			}
		}
		return actorList;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		//No Directors listed for this site, return an empty list
		ArrayList<Director> directorList = new ArrayList<Director>();
		return directorList;
	}

	@Override
	public Studio scrapeStudio() {
		return new Studio("1pondo");
	}
	
	@Override
	public Trailer scrapeTrailer() {
		ID movieID = scrapeID();
		String potentialTrailerURL = "http://smovie.1pondo.tv/moviepages/" + movieID.getId() + "/sample/sample.avi";
		if(SiteParsingProfile.fileExistsAtURL(potentialTrailerURL))
			return new Trailer(potentialTrailerURL);
		else return Trailer.BLANK_TRAILER;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileID = findIDTagFromFile(file);
		if(fileID == null)
			return null;
		fileID = fileID.toLowerCase();

		if (fileID != null) {
			englishPage = "http://en.1pondo.tv/eng/moviepages/" + fileID + "/index.htm";
			japanesePage = "http://www.1pondo.tv/moviepages/" + fileID + "/index.html";
			if(scrapingLanguage == Language.ENGLISH)
			{
				return englishPage;
			}
			else
			{
				return japanesePage;
			}
		}

		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		SearchResult[] searchResultArray = {searchResult};
		return searchResultArray;
	}
	
	public static String findIDTagFromFile(File file) {
		return findIDTag(FilenameUtils.getName(file.getName()));
	}
	
	public static String findIDTag(String fileName) {
		Pattern pattern = Pattern.compile("[0-9]{6}_[0-9]{3}");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			String searchString = matcher.group();
			return searchString;
		}
		return null;
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new OnePondoParsingProfile();
	}

}
