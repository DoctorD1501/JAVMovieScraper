package moviescraper.doctord.scraper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.logging.Logger;

public class DitzyHeadlessBrowser {

	private final String userAgent;
	private Map<String, Map<String, String>> cookies;
	private final int timeout;
	private static final Logger LOGGER = Logger.getLogger(DitzyHeadlessBrowser.class.getName());

	public DitzyHeadlessBrowser(String userAgent, int timeout) {
		this.userAgent = userAgent;
		this.timeout = timeout;
		this.cookies = new HashMap<>();
		LOGGER.log(Level.INFO, "Build browser with U: {0}", this.userAgent);
	}

	public DitzyHeadlessBrowser() {
		this(UserAgent.getRandomUserAgent(), 10000);
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

	/**
	 * Get cookies that would be sent for a given realm
	 *
	 * @param realm Realm to get cookies for
	 * @return A map of key value for each cookie
	 */
	public Map<String, String> getCookies(String realm) {
		if (!cookies.containsKey(realm)) {
			cookies.put(realm, new HashMap<>());
		}

		return cookies.get(realm);
	}

	/**
	 * Get cookies that would be sent for a given URL
	 *
	 * @param url url to get cookies for
	 * @return A map of key value for each cookie
	 */
	public Map<String, String> getCookies(URL url) {
		return getCookies(url.getHost());
	}

	/**
	 * Add a cookie to a realm
	 * The cookies will be added to existing cookies.
	 *
	 * @param realm Realm to get cookies for
	 * @param key name of the cookie
	 * @param value value of the cookie
	 */
	public void addCookie(String realm, String key, String value) {
		getCookies(realm).put(key, value);
	}

	/**
	 * Add a cookies to a realm
	 * The cookies will be merged with existing cookies.
	 * If any of the given cookies already exists it will be replaced
	 *
	 * @param realm Realm to get cookies for
	 * @param newCookies Map of cookies
	 */
	public void addCookies(String realm, Map<String, String> newCookies) {
		getCookies(realm).putAll(newCookies);
	}

	private Connection connect(URL url, boolean followRedirect) throws IOException {
		Connection connection = Jsoup.connect(url.toString()).userAgent(userAgent).ignoreHttpErrors(true).timeout(timeout).followRedirects(followRedirect).method(Connection.Method.GET);

		connection = connection.cookies(this.getCookies(url));

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

		connection = connection.cookies(this.getCookies(url));

		Response response = connection.execute();
		if (response.cookies().size() > 0) {
			addCookies(url.getHost(), response.cookies());
		}

		if (response.statusCode() == 503 && response.hasHeader("Server")) {
			if (response.header("Server").compareTo("cloudflare") == 0) {
				try {
					LOGGER.log(Level.INFO, "Detected cloudflare protected request. Try to resolve the challenge");
					URL cloudflareUrl = CloudflareHandler.handleCloudflare(url, response.parse());
					response = connect(cloudflareUrl, false).execute();
					if (response.statusCode() != 302) {
						return null;
					}
					addCookies(url.getHost(), response.cookies());
					connection = connect(url, true);
					response = connection.execute();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Cannot register on cloudflare protection", e);
				}
			}
		}

		return response.parse();
	}

}
