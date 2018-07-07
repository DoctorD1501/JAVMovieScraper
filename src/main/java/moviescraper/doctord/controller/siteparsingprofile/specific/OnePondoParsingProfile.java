package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileJSON;
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
import org.json.JSONArray;
import org.json.JSONObject;

public class OnePondoParsingProfile extends SiteParsingProfileJSON implements SpecificProfile {

	//private boolean scrapeInEnglish;
	private String englishPage;
	private String japanesePage;

	@Override
	public String getParserName() {
		return "1pondo";
	}

	@Override
	public Title scrapeTitle() {
		JSONObject pageJSON = getMovieJSON();
		return new Title(pageJSON.getString("Title"));
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		JSONObject pageJSON = getMovieJSON();
		return new OriginalTitle(pageJSON.getString("Title"));
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
		JSONObject pageJSON = getMovieJSON();
		String releaseYear = pageJSON.getString("Year");
		return new Year(releaseYear);
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {
		JSONObject pageJSON = getMovieJSON();
		String releaseDate = pageJSON.getString("Release");
		return new ReleaseDate(releaseDate);
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
		JSONObject pageJSON = getMovieJSON();
		String duration = String.valueOf(pageJSON.getInt("Duration"));
		return new Runtime(duration);
	}

	@Override
	public Thumb[] scrapePosters() {
		ArrayList<Thumb> thumbList = new ArrayList<>();
		JSONObject pageJSON = getMovieJSON();
		try {
			thumbList.add(new Thumb(pageJSON.getString("ThumbHigh")));
			thumbList.add(new Thumb(pageJSON.getString("MovieThumb")));
			thumbList.add(new Thumb(pageJSON.getString("ThumbUltra")));
			thumbList.add(new Thumb(pageJSON.getString("ThumbMed")));
			return thumbList.toArray(new Thumb[thumbList.size()]);
		} catch (MalformedURLException ex) {
			Logger.getLogger(OnePondoParsingProfile.class.getName()).log(Level.SEVERE, null, ex);
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
		JSONObject pageJSON = getMovieJSON();
		String movieID = pageJSON.getString("MovieID");
		return new ID(movieID);
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
		JSONObject pageJSON = getMovieJSON();
		JSONArray actors = pageJSON.getJSONArray("ActressesEn");
		for (Object actor : actors) {
			actorList.add(new Actor((String) actor, "", null));
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
			return "https://www.1pondo.tv/dyn/phpauto/movie_details/movie_id/" + fileID + ".json";
		}

		return null;
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		SearchResult searchResult = new SearchResult(searchString);
		searchResult.setJSONSearchResult(true);
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
