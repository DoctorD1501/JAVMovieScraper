package moviescraper.doctord.dataitem;



public class Year extends MovieDataItem {
	
	private String year;

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
		return "Year [year=" + year + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
