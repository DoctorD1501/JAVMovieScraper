package moviescraper.doctord.model.dataitem;

public class Title extends MovieDataItem {

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = sanitizeString(title);
	}

	public Title(String title) {
		super();
		setTitle(title);
	}

	@Override
	public String toXML() {
		return "<title>" + title + "</title>";
	}

	@Override
	public String toString() {
		return "Title [title=\"" + title + "\"" + dataItemSourceToString() + "]";
	}

	public Title()
	{
		title = "";
	}

}
