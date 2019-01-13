/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package moviescraper.doctord.scraper;

/**
 *
 */
public class UnexpectedWebsiteData extends Exception {

	public UnexpectedWebsiteData(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedWebsiteData(String message) {
		super(message);
	}
}
