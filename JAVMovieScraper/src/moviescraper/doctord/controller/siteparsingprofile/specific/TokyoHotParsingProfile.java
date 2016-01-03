package moviescraper.doctord.controller.siteparsingprofile.specific;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.*;
import moviescraper.doctord.model.dataitem.Runtime;
import moviescraper.doctord.model.dataitem.Set;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokyoHotParsingProfile extends SiteParsingProfile implements SpecificProfile {
    Logger log = Logger.getLogger(TokyoHotParsingProfile.class.getName());

    private String searchString;
    private Document docSite;
    private String siteLink;
    private String imageLink;
    private static final SimpleDateFormat tokyoHotReleaseDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    Map<String, String> cookie;
    public static final String AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";

    public TokyoHotParsingProfile() {
    }

    @Override
    public void setDocument(Document document) {
        super.setDocument(document);
        docSite = document;

        // get image link from document
        Elements elements = document.select("video");
        if (elements.size() > 0) {
            imageLink = elements.first().attr("poster");
        }
    }

    @Override
    public Title scrapeTitle() {
        Elements elements = docSite.select(".pagetitle h2");
        if (elements.size() > 0)
            return new Title(elements.first().text());
        return null;
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
        return new Set("Tokyo Hot");
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
        ReleaseDate releaseDate = ReleaseDate.BLANK_RELEASEDATE;
        Elements releaseDateElements = document.select(".info dd");
        if (releaseDateElements.size() >= 4) {
            Pattern pattern = Pattern.compile("[0-9]{4}");
            String timecode = releaseDateElements.get(3).text();
            Matcher matcher = pattern.matcher(timecode);

            if (matcher.find()) {
                releaseDate = new ReleaseDate(timecode,
                        tokyoHotReleaseDateFormat);
            }
        }
        return releaseDate;
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
        Elements elements = docSite.select("div.sentence");
        if (elements.size() > 0) {
            String ownText = elements.first().text();
            return new Plot(ownText);
        }
        return Plot.BLANK_PLOT;
    }

    @Override
    public Tagline scrapeTagline() {
        return Tagline.BLANK_TAGLINE;
    }

    @Override
    public Runtime scrapeRuntime() {
        Elements elements = document.select(".info dd");
        String time = "";

        if (elements.size() >= 5) {
            time = elements.get(4).text();
        }

        return new Runtime(time);
    }

    @Override
    public Thumb[] scrapePosters() {
        try {
            List<Thumb> thumbs = Lists.newArrayList();
            if (imageLink != null) {
                thumbs.add(new Thumb(imageLink));
            }

            Elements elements = document.select(".scap a");
            for (Element element : elements) {
                thumbs.add(new Thumb(element.attr("href")));
            }

            return Iterables.toArray(thumbs, Thumb.class);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new Thumb[0];
    }

    @Override
    public Document downloadDocumentFromURLString(String url) {
        try {
            if (this.cookie == null) {
                String enUrl = "http://my.tokyo-hot.com/product/6185/?lang=en";
                try {
                    Connection.Response response = Jsoup.connect(enUrl)
                            .userAgent(AGENT)
                            .ignoreHttpErrors(true)
                            .header("Accept-Language", "en-US,en")
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*")
                            .execute();
                    cookie = response.cookies();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                    e.printStackTrace();
                    cookie = null;
                }
            }

            return Jsoup.connect(url)
                    .userAgent(AGENT)
                    .ignoreHttpErrors(true)
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .cookies(cookie == null ? Maps.newHashMap() : cookie)
                    .timeout(CONNECTION_TIMEOUT_VALUE)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Thumb[] scrapeFanart() {
        return scrapePosters();
    }

    @Override
    public Thumb[] scrapeExtraFanart() {
        ArrayList<Thumb> extraFanart = new ArrayList<Thumb>();
        return extraFanart.toArray(new Thumb[extraFanart.size()]);
    }

    @Override
    public MPAARating scrapeMPAA() {
        return MPAARating.RATING_XXX;
    }

    @Override
    public ID scrapeID() {
        return new ID(findIDTag(searchString).toUpperCase());
    }

    @Override
    public ArrayList<Genre> scrapeGenres() {
        Genre a = new Genre("uncensored");
        Genre b = new Genre("No condom");
        ArrayList<Genre> list = new ArrayList<>();
        Collections.addAll(list, a, b);
        return list;
    }

    @Override
    public ArrayList<Actor> scrapeActors() {
        ArrayList<Actor> list = new ArrayList<>();
        Elements elements = docSite.select(".info dd");
        if (elements.size() > 2) {
            Elements actors = elements.get(0).select("a");
            for (Element element : actors) {
                list.add(new Actor(element.text(), null, null));
            }
        }
        return list;
    }

    @Override
    public ArrayList<Director> scrapeDirectors() {
        Director a = new Director("Tokyo Hot", null);
        ArrayList<Director> list = new ArrayList<>();
        Collections.addAll(list, a);
        return list;
    }

    @Override
    public Studio scrapeStudio() {
        return new Studio("Tokyo Hot");
    }

    @Override
    public ArrayList<Tag> scrapeTags() {
        ArrayList<Tag> tags = new ArrayList<>();
        ArrayList<Actor> list = new ArrayList<>();
        Elements elements = docSite.select(".info dd");
        if (elements.size() >= 3) {
            Elements children = elements.get(2).children();
            for (Element element : children) {
                tags.add(new Tag(element.text()));
            }
        }

        return tags;
    }

    @Override
    public String createSearchString(File file) {
        scrapedMovieFile = file;
        String fileID = findIDTagFromFile(file).toLowerCase();

        searchString = fileID;
        if (fileID != null) {
            try {
                String url = "http://cdn.www.tokyo-hot.com/igs/";
                url = "http://my.tokyo-hot.com/product/?q=" + fileID;
                Connection connection = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .ignoreHttpErrors(true)
                        .header("Accept-Language", "en-US")
                        .cookie("sessionid", "odc30090rhn0ans7x9cqnx5pbtz8qe5q")
                        .timeout(SiteParsingProfile.CONNECTION_TIMEOUT_VALUE);

                Document doc = connection.get();
                Elements select = doc.select("ul li a.rm");
                for (Element element : select) {
                    siteLink = element.attr("href");
                    break;
                }

                if (siteLink == null) {
                    System.out.println("Found no Link for TokyoHot");
                    return null;
                }

                siteLink = "http://my.tokyo-hot.com" + siteLink;
                return siteLink;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public SearchResult[] getSearchResults(String searchString)
            throws IOException {
        SearchResult searchResult = new SearchResult(searchString, searchString);
        SearchResult[] sr = {searchResult};
        return sr;
    }

    public static String findIDTagFromFile(File file) {
        return findIDTag(FilenameUtils.getName(file.getName()));
    }

    public static String findIDTag(String fileName) {
        Pattern pattern = Pattern.compile("n[0-9]{3,4}");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            String searchString = matcher.group();
            return searchString;
        }
        return null;
    }

    @Override
    public String getParserName() {
        return "Tokyo Hot";
    }

    @Override
    public SiteParsingProfile newInstance() {
        return new TokyoHotParsingProfile();
    }
}
