package moviescraper.doctord.controller.siteparsingprofile.specific;

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

import moviescraper.doctord.controller.languagetranslation.Language;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.dataitem.Director;
import moviescraper.doctord.model.dataitem.Genre;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.dataitem.MPAARating;
import moviescraper.doctord.model.dataitem.OriginalTitle;
import moviescraper.doctord.model.dataitem.Outline;
import moviescraper.doctord.model.dataitem.Plot;
import moviescraper.doctord.model.dataitem.Rating;
import moviescraper.doctord.model.dataitem.ReleaseDate;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import moviescraper.doctord.model.dataitem.SortTitle;
import moviescraper.doctord.model.dataitem.Studio;
import moviescraper.doctord.model.dataitem.Tagline;
import moviescraper.doctord.model.dataitem.Thumb;
import moviescraper.doctord.model.dataitem.Title;
import moviescraper.doctord.model.dataitem.Top250;
import moviescraper.doctord.model.dataitem.Trailer;
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

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
		if (titleElement != null) {
			String id = scrapeID().getId();
			String title = titleElement.text().trim();
			//replace used for english title
			title = title.replaceAll(Pattern.quote("::"), "-");
			//replace used for japanese title
			title = title.replaceAll(Pattern.quote(":"), "-");
			//old scenes on the site that do no contain the actor name in the title
			if (title.equals("1pondo.tv -"))
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
		if (scrapingLanguage == Language.JAPANESE)
			return new OriginalTitle(scrapeTitle().getTitle());
		else {
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
		return new Rating(0, "0");
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {

		//Still having problems with this due to release-date element not loading on japanese site

		//new method after site redesign
		Element releaseDate = document.select("dl.release-date dd.ng-binding").first();
		if (releaseDate != null && releaseDate.text().length() == 10) {
			String releaseDateText = releaseDate.text().replaceAll("/", "-");
			return new ReleaseDate(releaseDateText);
		}

		//Old method of site before redesign: Get year from last 2 digits before the underscore in the ID of the movie
		//Add "20" to these digits, so "14" becomes 2014, for example
		//(this is ok because there are no scenes on 1pondo from 1999 or earlier)
		ID movieID = scrapeID();
		if (movieID != null && movieID.getId().contains("_")) {
			String year = "20" + movieID.getId().substring(4, 6);
			String month = movieID.getId().substring(0, 2);
			String day = movieID.getId().substring(2, 4);
			return new ReleaseDate(year + "-" + month + "-" + day);
		}
		return ReleaseDate.BLANK_RELEASEDATE;
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
			ArrayList<Thumb> thumbList = new ArrayList<>();
			String bannerURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/str.jpg";
			System.out.println("bannerURL = " + bannerURL);
			String backgroundURLTwo = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/2.jpg";
			String popupOneURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/1.jpg";
			String popupTwoURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/2.jpg";
			String popupThreeURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/3.jpg";
			String popupFourURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu.jpg";
			if (SiteParsingProfile.fileExistsAtURL(popupOneURL))
				thumbList.add(new Thumb(popupOneURL));
			if (SiteParsingProfile.fileExistsAtURL(popupTwoURL))
				thumbList.add(new Thumb(popupTwoURL));
			if (SiteParsingProfile.fileExistsAtURL(popupThreeURL))
				thumbList.add(new Thumb(popupThreeURL));
			if (SiteParsingProfile.fileExistsAtURL(bannerURL))
				thumbList.add(new Thumb(bannerURL));
			if (SiteParsingProfile.fileExistsAtURL(backgroundURLTwo))
				thumbList.add(new Thumb(backgroundURLTwo));
			if (SiteParsingProfile.fileExistsAtURL(popupFourURL))
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
			ArrayList<Thumb> thumbList = new ArrayList<>();
			String bannerURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/str.jpg";
			String backgroundURLOne = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/1.jpg";
			String backgroundURLTwo = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/2.jpg";
			String popupOneURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/1.jpg";
			String popupTwoURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/2.jpg";
			String popupThreeURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu/3.jpg";
			String popupFourURL = "http://www.1pondo.tv/assets/sample/" + scrapeID().getId() + "/popu.jpg";
			if (SiteParsingProfile.fileExistsAtURL(bannerURL))
				thumbList.add(new Thumb(bannerURL));
			if (SiteParsingProfile.fileExistsAtURL(popupOneURL))
				thumbList.add(new Thumb(popupOneURL));
			if (SiteParsingProfile.fileExistsAtURL(popupTwoURL))
				thumbList.add(new Thumb(popupTwoURL));
			if (SiteParsingProfile.fileExistsAtURL(popupThreeURL))
				thumbList.add(new Thumb(popupThreeURL));
			//combine the two background images together to make the fanart if we are on a page that has split things into two images
			if (SiteParsingProfile.fileExistsAtURL(backgroundURLOne) && SiteParsingProfile.fileExistsAtURL(backgroundURLTwo)) {
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

			} else if (SiteParsingProfile.fileExistsAtURL(backgroundURLTwo))
				thumbList.add(new Thumb(backgroundURLTwo));
			if (SiteParsingProfile.fileExistsAtURL(popupFourURL))
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
		String documentURL = document.location();
		if (documentURL.length() > 0 && documentURL.contains("1pondo.tv")) {
			documentURL = documentURL.replaceFirst("/index.html", "");
			documentURL = documentURL.replaceFirst("/index.htm", "");
			if (documentURL.endsWith("/"))
				documentURL = documentURL.substring(0, documentURL.length() - 1);
			String idFromBaseUri = documentURL.substring(documentURL.lastIndexOf('/') + 1);
			return new ID(idFromBaseUri);
		}
		return ID.BLANK_ID;
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		//For now, I wasn't able to find any genres on the page
		ArrayList<Genre> genreList = new ArrayList<>();
		return genreList;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		ArrayList<Actor> actorList = new ArrayList<>(1);
		Element profileArea = document.select("div#profile-area").first();
		if (profileArea != null) {
			String actressThumbURL = profileArea.select("img").attr("src");
			//Fix for redirect 1pondo is doing for actor images due to new site layout
			if (actressThumbURL.contains("/moviepages/"))
				actressThumbURL = actressThumbURL.replace("/moviepages/", "/assets/sample/").replace("/images/", "/");
			String actressName = profileArea.select(".bgoose h2, .bgg1").first().text();
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
		ArrayList<Director> directorList = new ArrayList<>();
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
		if (SiteParsingProfile.fileExistsAtURL(potentialTrailerURL))
			return new Trailer(potentialTrailerURL);
		else
			return Trailer.BLANK_TRAILER;
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileID = findIDTagFromFile(file);
		if (fileID == null)
			return null;
		fileID = fileID.toLowerCase();

		if (fileID != null) {
			englishPage = "http://en.1pondo.tv/eng/moviepages/" + fileID + "/index.htm";
			japanesePage = "http://www.1pondo.tv/moviepages/" + fileID + "/index.html";
			if (scrapingLanguage == Language.ENGLISH) {
				return englishPage;
			} else {
				return japanesePage;
			}
		}

		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		SearchResult[] searchResultArray = { searchResult };
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
