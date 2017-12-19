package moviescraper.doctord.model.dataitem;

public class Outline extends MovieDataItem {

	private String outline;
	public static final Outline BLANK_OUTLINE = new Outline("");

	public String getOutline() {
		return outline;
	}

	public void setOutline(String outline) {
		this.outline = sanitizeString(outline);
	}

	@Override
	public String toString() {
		return "Outline [outline=\"" + outline + "\"" + dataItemSourceToString() + "]";
	}

	public Outline(String outline) {
		super();
		setOutline(outline);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Outline() {
		outline = "";
	}

}
