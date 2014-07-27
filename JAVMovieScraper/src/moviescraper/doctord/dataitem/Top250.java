package moviescraper.doctord.dataitem;



public class Top250 extends MovieDataItem {
	
	private String top250;

	public String getTop250() {
		return top250;
	}

	public void setTop250(String top250) {
		this.top250 = sanitizeString(top250);
	}

	@Override
	public String toString() {
		return "Top250 [top250=" + top250 + "]";
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

}
