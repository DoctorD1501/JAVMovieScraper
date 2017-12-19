package moviescraper.doctord.model.dataitem;

public class Tagline extends MovieDataItem {

	public static final Tagline BLANK_TAGLINE = new Tagline("");

	private String tagline;

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = sanitizeString(tagline);
	}

	public Tagline(String tagline) {
		super();
		setTagline(tagline);
	}

	@Override
	public String toString() {
		return "Tagline [tagline=\"" + tagline + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tagline() {
		tagline = "";
	}

}
