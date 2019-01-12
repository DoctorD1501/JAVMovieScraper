/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moviescraper.doctord.scraper;

public class DitzyHeadlessBrowserSingle {

	private static DitzyHeadlessBrowser instance;

	static public DitzyHeadlessBrowser getBrowser() {
		if (instance == null) {
			instance = new DitzyHeadlessBrowser();
		}
		return instance;
	}
}
