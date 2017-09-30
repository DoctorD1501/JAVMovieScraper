package moviescraper.doctord.model.dataitem;

public class SortTitle extends MovieDataItem {

	private String sortTitle;
	public static final SortTitle BLANK_SORTTITLE = new SortTitle("");

	@Override
	public String toString() {
		return "SortTitle [sortTitle=\"" + sortTitle + "\"" + dataItemSourceToString() + "]";
	}

	public String getSortTitle() {
		return sortTitle;
	}

	public void setSortTitle(String sortTitle) {
		if (sortTitle != null)
			this.sortTitle = sanitizeString(sortTitle);
		else
			this.sortTitle = "";
	}

	public SortTitle(String sortTitle) {
		setSortTitle(sortTitle);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public SortTitle() {
		sortTitle = "";
	}

}
