package moviescraper.doctord.scraper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.logging.Logger;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class DitzyHeadlessBrowser {

	private String userAgent;
	private DitzyCookies cookies;
	private final int timeout;
	private static final Logger LOGGER = Logger.getLogger(DitzyHeadlessBrowser.class.getName());

	public DitzyHeadlessBrowser(String userAgent, int timeout) {
		this.userAgent = userAgent;
		this.timeout = timeout;
		this.cookies = new DitzyCookies();
		LOGGER.log(Level.INFO, "Build browser with U: {0}", this.userAgent);
	}

	public DitzyHeadlessBrowser() {
		this(UserAgent.getRandomUserAgent(), 10000);
	}

	public void configure() throws IOException {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
		setUserAgent(preferences.getUserAgent());
		Cookies().LoadCookieJar(new File(preferences.getCookieJar()));
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(this.userAgent);
		string.append(", timeout: ");
		string.append(this.timeout);
		if (this.cookies != null) {
			string.append(", cookies: ");
			string.append(this.cookies);
		}
		return string.toString();
	}

	private Connection connect(URL url, boolean followRedirect) throws IOException {
		Connection connection = Jsoup.connect(url.toString()).userAgent(userAgent).ignoreHttpErrors(true).timeout(timeout).followRedirects(followRedirect).method(Connection.Method.GET);

		connection = connection.cookies(this.cookies.getCookies(url));

		return connection;
	}

	/**
	 * get a document from an URL
	 *
	 * @param url URL to get
	 * @return The document corresponding to the URL
	 * @throws IOException Cannot parse the document
	 */
	public Document get(URL url) throws IOException {
		LOGGER.log(Level.INFO, "Get request on {0}", url.toString());
		Connection connection = connect(url, true);

		connection = connection.cookies(this.cookies.getCookies(url));

		Response response = connection.execute();

		if (response.cookies().size() > 0) {
			cookies.addCookies(url.getHost(), response.cookies());
		}

		if (response.statusCode() == 503 && response.hasHeader("Server")) {
			if (response.header("Server").compareTo("cloudflare") == 0) {
				throw new RuntimeException("Cannot connect to cloudflare walled url. Make sure you set and update the cookieJar");
			}
		}

		return response.parse();
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public DitzyCookies Cookies() {
		return this.cookies;
	}
}
