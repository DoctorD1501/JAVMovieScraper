package moviescraper.doctord.model.dataitem;

public class Top250 extends MovieDataItem {

	private String top250;
	public static final Top250 BLANK_TOP250 = new Top250("");

	public String getTop250() {
		return top250;
	}

	public void setTop250(String top250) {
		this.top250 = sanitizeString(top250);
	}

	@Override
	public String toString() {
		return "Top250 [top250=\"" + top250 + "\"" + dataItemSourceToString() + "]";
	}

	public Top250(String top250) {
		super();
		setTop250(top250);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Top250() {
		top250 = "";
	}

}
