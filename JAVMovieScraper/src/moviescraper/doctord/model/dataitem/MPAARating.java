package moviescraper.doctord.model.dataitem;



public class MPAARating extends MovieDataItem {
	
	private String MPAARating;
	
	public static final MPAARating RATING_XXX = new MPAARating("XXX");
	public static final MPAARating BLANK_RATING = new MPAARating("");

	public MPAARating()
	{
		this.MPAARating = "";
	}
	public MPAARating(String MPAARating) {
		setMPAARating(MPAARating);
	}

	public String getMPAARating() {
		return MPAARating;
	}

	public void setMPAARating(String mPAARating) {
		this.MPAARating = sanitizeString(mPAARating);
	}

	@Override
	public String toString() {
		return "MPAARating [MPAARating=\"" + MPAARating + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
