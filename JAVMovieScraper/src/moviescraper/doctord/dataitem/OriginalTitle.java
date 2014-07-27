package moviescraper.doctord.dataitem;

public class OriginalTitle extends MovieDataItem {
	private String originalTitle;

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = sanitizeString(originalTitle);
	}

	@Override
	public String toString() {
		return "OriginalTitle [originalTitle=" + originalTitle + "]";
	}

	public OriginalTitle(String originalTitle) {
		setOriginalTitle(originalTitle);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
