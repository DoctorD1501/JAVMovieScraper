package moviescraper.doctord.SiteParsingProfile.specific;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import moviescraper.doctord.SearchResult;
import moviescraper.doctord.Thumb;
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
import moviescraper.doctord.dataitem.Title;
import moviescraper.doctord.dataitem.Top250;
import moviescraper.doctord.dataitem.Votes;
import moviescraper.doctord.dataitem.Year;

public class AvEntertainmentParsingProfile extends SiteParsingProfile implements SpecificProfile {
	
	private Document downloadDocument(String url) {
		try {
			return Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Title scrapeTitle() {
		Elements elements = document.select("#mini-tabet h2");
		String title = elements.first().childNode(0).toString().trim();
		return new Title(title);
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
		String set = getMovieData("Series");
		return new Set(set);
	}

	@Override
	public Rating scrapeRating() {
		return new Rating(0,"");
	}

	@Override
	public Year scrapeYear() {
		String year = "";
		
		Elements elements = document.select("div[id=titlebox] ul li");
		for (Element element : elements) {
			if (element.childNodeSize() >= 3) {
				Node childNode = element.childNode(2);
				if ( childNode instanceof TextNode && element.childNode(1).childNode(0).toString().startsWith("Release Date")) {
					
					String data = element.childNode(2).toString();
					Pattern pattern = Pattern.compile("\\d{4}");
					Matcher matcher = pattern.matcher(data);
					if ( matcher.find() ) {
						year = matcher.group();
						break;
					}
				}
			}
		}
		return new Year(year);
	}

	@Override
	public Top250 scrapeTop250() {
		return new Top250("");
	}

	@Override
	public Votes scrapeVotes() {
		return new Votes("");
	}

	@Override
	public Outline scrapeOutline() {
		return new Outline("");
	}

	@Override
	public Plot scrapePlot() {
		return new Plot("");
	}

	@Override
	public Tagline scrapeTagline() {
		return new Tagline("");
	}

	@Override
	public Runtime scrapeRuntime() {
		String runtime = "";
		
		Elements elements = document.select("div[id=titlebox] ul li");
		for (Element element : elements) {
			if (element.childNodeSize() == 3) {
				Node childNode = element.childNode(2);
				if ( childNode instanceof TextNode && element.childNode(1).childNode(0).toString().startsWith("Playing time")) {
					String data = element.childNode(2).toString();
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(data);
					if ( matcher.find() ) {
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
				BufferedImage read = ImageIO.read( fanart[0].getThumbURL() );
				int newWidth = (int) ((1.0 - 0.526666) * read.getWidth());
				thumbs.add( new Thumb(fanart[0].getThumbURL().toString(), newWidth) );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return thumbs.toArray( new Thumb[ thumbs.size() ] );
	}

	@Override
	public Thumb[] scrapeFanart() {
		List<Thumb> thumbs = new ArrayList<>();
		Elements elements = document.select("li.ppvs.magnify a");
		if (elements.size() > 0) {
			Element first = elements.first();
			String attr = first.attr("onclick");
			String temp = "imagefile=";
			Pattern pattern = Pattern.compile(temp+"http.*jpg");
			Matcher matcher = pattern.matcher(attr);
			if (matcher.find()) {
				String url = matcher.group().substring(temp.length());
				try {
					thumbs.add( new Thumb(url) );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return thumbs.toArray( new Thumb[ thumbs.size() ] );
	}

	@Override
	public Thumb[] scrapeExtraFanart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MPAARating scrapeMPAA() {
		return new MPAARating("XXX");
	}

	@Override
	public ID scrapeID() {
		Elements select = document.select("div[class=top-title]");
		String id = "";
		if (select.size() > 0) {
			Element element = select.get(0);
			if (element.childNodeSize() > 0) {
				id = element.childNode(0).toString();
				id = id.substring(8);
			}
		}
		return new ID( id );
	}

	@Override
	public ArrayList<Genre> scrapeGenres() {
		ArrayList<Genre> list = new ArrayList<>();
		Elements elements = document.select("div[id=detailbox] ul ol a");
		for (Element element : elements) {
			String genre = element.childNode(0).toString();
			list.add(new Genre( genre ));
		}
		return list;
	}

	@Override
	public ArrayList<Actor> scrapeActors() {
		Elements elements = document.select("ul li a[href~=ActressDetail]");
		ArrayList<Actor> list = new ArrayList<>();
		for (Element element : elements) {
			String href = element.attr("href");
			String name = WordUtils.capitalize(element.childNode(0).toString());
			Thumb thumb = null;
			try {
				Document actorDoc = Jsoup.connect(href).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
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
		return list;
	}

	@Override
	public ArrayList<Director> scrapeDirectors() {
		ArrayList<Director> list = new ArrayList<>();
		return list;
	}

	@Override
	public Studio scrapeStudio() {
		String studio = getMovieData("Studio");
		return new Studio( studio );
	}
	
	private String getMovieData(String category) {
		Elements elements = document.select("div[id=titlebox] ul li");
		for (Element element : elements) {
			Element span = element.select("span").first();
			if (span != null) {
				String cat = span.childNode(0).toString();
				if (cat.startsWith(category)) {
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
		String fileNameNoExtension = findIDTagFromFile(file);
		return getSearchString(fileNameNoExtension);
	}

	@Override
	public SearchResult[] getSearchResults(String searchString)
			throws IOException {
		Document doc = Jsoup.connect(searchString).userAgent("Mozilla").ignoreHttpErrors(true).timeout(0).get();
		List<SearchResult> list = new ArrayList<>();
		Elements elements = doc.select("td[valign=top] table tbody tr td");

		for (Element e : elements) {
			Elements selectLink = e.select("h4 a[href^=http://www.aventertainments.com]");
			if (selectLink.size() > 0) {
				String href = selectLink.get(0).attr("href");
				String label = selectLink.get(0).childNode(0).toString();
				Elements selectThumb = e.select("a img");
				Thumb thumb = null;
				for (Element thumbElement : selectThumb) {
					String attr = thumbElement.attr("src");
					if (attr.startsWith("http://imgs.aventertainments.com/product_images")) {
						thumb = new Thumb(attr);
					}
				}
				list.add( new SearchResult(href, label, thumb) );
			}
		}
		return list.toArray(new SearchResult[list.size()]);
	}

	private String getSearchString(String id) {
		return "http://www.aventertainments.com/search_Products.aspx?languageID=1&dept_id=29&keyword="
				+ id + "&searchby=item_no";
	}
	
	private String getSearchStringPPV(String id) {
		return "http://www.aventertainments.com/ppv/ppv_searchproducts.aspx?languageID=1&VODTypeID=1&keyword="
				+ id + "&searchby=item_no";
	}

	@Override
	public String getParserName() {
		return "AV Entertainment";
	}

}
