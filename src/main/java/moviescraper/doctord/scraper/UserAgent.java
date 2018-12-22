package moviescraper.doctord.scraper;

import java.util.Random;

public class UserAgent {
	private static final String[] DEFAULT_USER_AGENTS = { "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36",
	        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/65.0.3325.181 Chrome/65.0.3325.181 Safari/537.36",
	        "Mozilla/5.0 (Linux; Android 7.0; Moto G (5) Build/NPPS25.137-93-8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.137 Mobile Safari/537.36",
	        "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_4 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11B554a Safari/9537.53",
	        "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:59.0) Gecko/20100101 Firefox/59.0",
	        "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0" };

	public static String getUserAgent(int index) {
		if (index > 0 && index < DEFAULT_USER_AGENTS.length) {
			return DEFAULT_USER_AGENTS[new Random().nextInt(DEFAULT_USER_AGENTS.length)];
		} else {
			return DEFAULT_USER_AGENTS[0];
		}
	}

	public static String getRandomUserAgent() {
		return DEFAULT_USER_AGENTS[new Random().nextInt(DEFAULT_USER_AGENTS.length)];
	}
}
