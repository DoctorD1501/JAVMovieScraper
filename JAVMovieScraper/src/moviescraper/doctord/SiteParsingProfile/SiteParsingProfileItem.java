package moviescraper.doctord.SiteParsingProfile;



public class SiteParsingProfileItem {
	String title;
	SiteParsingProfile parser;
	private boolean disabled;
	
	public SiteParsingProfileItem(String title, SiteParsingProfile parser) {
		this.title = title;
		this.parser = parser;
		this.disabled = false;
	}
	
	public SiteParsingProfile getParser() {
		return parser;
	}
	
	@Override
	public String toString() {
		return title;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
}