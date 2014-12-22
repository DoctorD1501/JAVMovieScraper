package moviescraper.doctord.dataitem;

public class Studio extends MovieDataItem {

	private String studio;
	public static final Studio BLANK_STUDIO = new Studio("");

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = sanitizeString(studio);
	}

	@Override
	public String toString() {
		return "Studio [Studio=" + studio + "]";
	}

	public Studio(String studio) {
		setStudio(studio);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
