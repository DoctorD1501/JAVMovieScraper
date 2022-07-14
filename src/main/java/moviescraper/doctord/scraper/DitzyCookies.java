package moviescraper.doctord.scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Cookie manipulation class
 */
public class DitzyCookies {

	private final Map<String, Map<String, String>> cookies;

	public DitzyCookies() {
		cookies = new HashMap<>();
	}

	public void LoadCookieJar(File cookieJar) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(cookieJar));
		try (Stream<String> stream = Files.lines(Paths.get(cookieJar.getPath()))) {
			stream.filter(line -> (line.trim().length() != 0 && !line.startsWith("# ") && line.split("\t").length == 7)).forEach(line -> {
				String[] l = line.split("\t");

				try {
					HttpCookie cookie = new HttpCookie(l[5], l[6]);
					String cookie_domain = l[0];
					if (cookie_domain.startsWith("#HttpOnly_.")) {
						cookie_domain = cookie_domain.replace("#HttpOnly_.", "");
						cookie.setHttpOnly(true);
					}
					if (cookie_domain.startsWith(".")) {
						cookie_domain = cookie_domain.substring(1);
					}
					cookie.setDomain(cookie_domain);
					cookie.setPath(l[2]);
					cookie.setSecure("TRUE".equals(l[3]));
					long expire = Long.parseLong(l[4]);
					addCookie(cookie.getDomain(), cookie.getName(), cookie.getValue());
				} catch (IllegalArgumentException ex) {
					// Ignore bad cookies
				}
			});
		} catch (Exception e) {
			// Ignore bad cookies
		}
	}

	/**
	 * Get cookies that would be sent for a given realm
	 *
	 * @param domain Domain to get cookies for
	 * @return A map of key value for each cookie
	 */
	public Map<String, String> getCookies(String domain) {
		Map<String, String> domain_cookies = new HashMap<>();

		List<String> domain_parts = Arrays.asList(domain.split("\\."));
		for (int domain_index = 0; domain_index < domain_parts.size(); domain_index++) {
			String subdomain = String.join(".", domain_parts.subList(domain_index, domain_parts.size()));
			if (cookies.containsKey(subdomain)) {
				Map<String, String> realm_cookies = getRealmCookies(subdomain);
				domain_cookies.putAll(realm_cookies);
			}
		}

		return domain_cookies;
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

	private Map<String, String> getRealmCookies(String realm) {
		if (!cookies.containsKey(realm)) {
			cookies.put(realm, new HashMap<>());
		}
		return cookies.get(realm);
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
		getRealmCookies(realm).put(key, value);
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
		getRealmCookies(realm).putAll(newCookies);
	}

}
