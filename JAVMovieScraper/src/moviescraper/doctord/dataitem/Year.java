package moviescraper.doctord.dataitem;



public class Year extends MovieDataItem {
	
	private String year;
	public static final Year BLANK_YEAR = new Year("");

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = sanitizeString(year);
	}

	public Year(String year) {
		setYear(year);
	}

	@Override
	public String toString() {
		return "Year [year=\"" + year + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
