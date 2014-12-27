package moviescraper.doctord.SiteParsingProfile;



public class SiteParsingProfileItem {
	String title;
	SiteParsingProfile parser;
	
	public SiteParsingProfileItem(String title, SiteParsingProfile parser) {
		this.title = title;
		this.parser = parser;
	}
	
	public SiteParsingProfile getParser() {
		return parser;
	}
	
	@Override
	public String toString() {
		return title;
	}
}