package moviescraper.doctord.dataitem;

public class SortTitle extends MovieDataItem {

	@Override
	public String toString() {
		return "SortTitle [sortTitle=" + sortTitle + "]";
	}

	public String getSortTitle() {
		return sortTitle;
	}

	public void setSortTitle(String sortTitle) {
		this.sortTitle = sanitizeString(sortTitle);
	}

	private String sortTitle;

	public SortTitle(String sortTitle) {
		setSortTitle(sortTitle);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
