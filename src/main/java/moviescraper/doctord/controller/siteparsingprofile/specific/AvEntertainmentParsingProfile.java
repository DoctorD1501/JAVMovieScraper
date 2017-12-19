package moviescraper.doctord.controller.siteparsingprofile.specific;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import moviescraper.doctord.model.dataitem.Votes;
import moviescraper.doctord.model.dataitem.Year;

public class AvEntertainmentParsingProfile extends SiteParsingProfile implements SpecificProfile {

	private static final SimpleDateFormat avEntertainmentReleaseDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

	@Override
	public Title scrapeTitle() {
		Elements elements = document.select("#mini-tabet h2");
		String title = elements.first().childNode(0).toString().trim();
		return new Title(title);
	}

	@Override
	public OriginalTitle scrapeOriginalTitle() {
		return OriginalTitle.BLANK_ORIGINALTITLE;
	}

	@Override
	public SortTitle scrapeSortTitle() {
		return SortTitle.BLANK_SORTTITLE;
	}

	@Override
	public Set scrapeSet() {
		String set = getMovieData("Series", "シリーズ");
		return new Set(set);
	}

	@Override
	public Rating scrapeRating() {
		return new Rating(0, "");
	}

	@Override
	public Year scrapeYear() {
		return scrapeReleaseDate().getYear();
	}

	@Override
	public ReleaseDate scrapeReleaseDate() {

		Elements elements = document.select("div[id=titlebox] ul li:contains(Release Date:), div[id=titlebox] ul li:contains(発売日:)");
		if (elements != null) {
			String releaseDateText = elements.first().ownText();
			return new ReleaseDate(releaseDateText, avEntertainmentReleaseDateFormat);
		}
		return ReleaseDate.BLANK_RELEASEDATE;

	}

	@Override
	public Top250 scrapeTop250() {
		return Top250.BLANK_TOP250;
	}

	@Override
	public Votes scrapeVotes() {
		return Votes.BLANK_VOTES;
	}

	@Override
	public Outline scrapeOutline() {
		return Outline.BLANK_OUTLINE;
	}

	@Override
	public Plot scrapePlot() {
		return Plot.BLANK_PLOT;
	}

	@Override
	public Tagline scrapeTagline() {
		return Tagline.BLANK_TAGLINE;
	}

	@Override
	public Runtime scrapeRuntime() {
		String runtime = "";

		Elements elements = document.select("div[id=titlebox] ul li");
		for (Element element : elements) {
			if (element.childNodeSize() == 3) {
				if (element.text().startsWith("Play time") || element.text().startsWith("収録時間")) {
					String data = element.childNode(2).toString();
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(data);
					if (matcher.find()) {
						runtime = matcher.group();
						break;
					}
				}
			}
		}
		return new Runtime(runtime);
	}

	@Override
	public Thumb[] scrapePosters() {
		List<Thumb> thumbs = new ArrayList<>();
		Thumb[] fanart = scrapeFanart();
		if (fanart.length > 0) {
			try {
				BufferedImage read = ImageIO.read(fanart[0].getThumbURL());
				if (read != null) {
					//int newWidth = (int) ((1.0 - 0.526666) * read.getWidth());
					thumbs.add(new Thumb(fanart[0].getThumbURL().toString(), true));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return thumbs.toArray(new Thumb[thumbs.size()]);
	}

	@Override
	public Thumb[] scrapeFanart() {
		List<Thumb> thumbs = new ArrayList<>();
		Elements elements = document.select("li.ppvs.magnify a");
		if (elements.size() > 0) {
			Element first = elements.first();
			String attr = first.attr("onclick");
			String temp = "imagefile=";
			Pattern pattern = Pattern.compile(temp + "http.*jpg");
			Matcher matcher = pattern.matcher(attr);
			if (matcher.find()) {
				String url = matcher.group().substring(temp.length());
				try {
					thumbs.add(new Thumb(url));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return thumbs.toArray(new Thumb[thumbs.size()]);
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		return new Thumb[0];
	}

	@Override
	public MPAARating scrapeMPAA() {
		return MPAARating.RATING_XXX;
	}

	@Override
	public ID scrapeID() {
		Elements select = document.select("div[class=top-title]");
		String id = "";
		if (select.size() > 0) {
			Element element = select.get(0);
			if (element.childNodeSize() > 0) {
				id = element.childNode(0).toString();
				id = getLastWord(id);
			}
		}
		return new ID(id);
	}

	private static String getLastWord(String input) {
		String wordSeparator = " ";
		boolean inputIsOnlyOneWord = !StringUtils.contains(input, wordSeparator);
		if (inputIsOnlyOneWord) {
			return input;
		}
		return StringUtils.substringAfterLast(input, wordSeparator);
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> list = new ArrayList<>();
		Elements elements = document.select("div[id=detailbox] ul ol a");
		for (Element element : elements) {
			String genre = element.childNode(0).toString();
			if (!genre.equals("Sample Movie") && !genre.contains("(DVD)"))
				list.add(new Genre(genre));
		}
		return list;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements elements = document.select("ul li a[href~=ActressDetail]");
		ArrayList<Actor> list = new ArrayList<>();
		if (elements != null) {
			for (Element element : elements) {
				String href = element.attr("href");
				String name = WordUtils.capitalize(element.text());
				Thumb thumb = null;
				try {
					Document actorDoc = Jsoup.connect(href).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
					Element first = actorDoc.select("ul img[src~=ActressImage]").first();
					if (first != null) {
						String thumbURL = first.attr("src");
						thumb = new Thumb(thumbURL);
					}
					list.add(new Actor(name, null, thumb));
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return list;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> list = new ArrayList<>();
		return list;
	}

	@Override
	public Studio scrapeStudio() {
		String studio = getMovieData("Studio", "スタジオ");
		return new Studio(studio);
	}

	private String getMovieData(String category, String japaneseWordForCategory) {
		Elements elements = document.select("div[id=titlebox] ul li");
		for (Element element : elements) {
			Element span = element.select("span").first();
			if (span != null) {
				String cat = span.childNode(0).toString();
				if (cat.startsWith(category) || cat.startsWith(japaneseWordForCategory)) {
					Element first = element.select("a").first();
					String text = first.childNode(0).toString();
					return text;
				}
			}
		}
		return "";
	}

	@Override
	public String createSearchString(File file) {
		scrapedMovieFile = file;
		String fileNameNoExtension = findIDTagFromFile(file, isFirstWordOfFileIsID());
		return getSearchString(fileNameNoExtension);
	}

	@Override
	public SearchResult[] getSearchResults(String searchString) throws IOException {
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE).get();
		List<SearchResult> list = new ArrayList<>();
		Elements elements = doc.select(".PPV-TOP");

		for (Element e : elements) {
			String href = e.attr("href");
			String label = e.siblingElements().get(e.siblingIndex()).text();
			Elements selectThumb = e.select("img");
			Thumb thumb = null;
			for (Element thumbElement : selectThumb) {
				String attr = thumbElement.attr("src");
				if (attr.startsWith("http://imgs.aventertainments.com/product_images")) {
					thumb = new Thumb(attr);
				}
			}
			list.add(new SearchResult(href, label, thumb));
		}
		elements = doc.select(".list-cover");
		for (Element e : elements) {
			Element root = e.parent().parent().parent();
			String href = root.select(":nth-child(1) a").get(1).attr("href");
			String label = root.select(":nth-child(1) a").get(1).text();
			Elements selectThumb = e.select("img");
			Thumb thumb = null;
			for (Element thumbElement : selectThumb) {
				String attr = thumbElement.attr("src");
				if (attr.startsWith("http://imgs.aventertainments.com/product_images") || attr.startsWith("http://imgs.aventertainments.com/new/jacket_images/")
						|| attr.startsWith("http://imgs.aventertainments.com/archive/jacket_images/")) {
					thumb = new Thumb(attr);
				}
			}
			list.add(new SearchResult(href, label, thumb));
		}
		return list.toArray(new SearchResult[list.size()]);
	}

	private String getSearchString(String id) {
		String languageID = "1";
		if (getScrapingLanguage() == Language.JAPANESE)
			languageID = "2";
		return "http://www.aventertainments.com/search_Products.aspx?languageID=" + languageID + "&dept_id=29&keyword=" + id + "&searchby=item_no";
	}

	@Override
	public String getParserName() {
		return "AV Entertainment";
	}

	@Override
	public SiteParsingProfile newInstance() {
		return new AvEntertainmentParsingProfile();
	}

}
