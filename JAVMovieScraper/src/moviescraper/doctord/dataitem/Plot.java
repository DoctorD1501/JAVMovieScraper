package moviescraper.doctord.dataitem;

public class Plot extends MovieDataItem {

	private String plot;
	public static final Plot BLANK_PLOT = new Plot("");

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = sanitizeString(plot);
	}

	@Override
	public String toString() {
		return "Plot [plot=\"" + plot + "\"" + dataItemSourceToString() + "]";
	}

	public Plot(String plot) {
		super();
		setPlot(plot);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
