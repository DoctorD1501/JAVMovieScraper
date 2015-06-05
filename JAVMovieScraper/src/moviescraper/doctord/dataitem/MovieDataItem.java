package moviescraper.doctord.dataitem;

public abstract class MovieDataItem {
	
	protected final static int connectionTimeout = 10000; //10 seconds
	protected final static int  readTimeout = 10000; //10 seconds
	private DataItemSource dataItemSource;
	
	//Any MovieDataItem needs to know how to write itself to XML
	abstract public String toXML();
	
	public final static String sanitizeString(String inputString)
	{
		if(inputString != null)
			return inputString.replace("\u00a0"," ").trim(); //replace non breaking space (&nbsp) with regular space then trim things
		else return null;
	}

	public DataItemSource getDataItemSource() {
		if(dataItemSource == null)
			return DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE;
		else return dataItemSource;
	}

	public void setDataItemSource(DataItemSource dataItemSource) {
		this.dataItemSource = dataItemSource;
	}
	
	public String dataItemSourceToString(){
		return " source=\"" + getDataItemSource() + "\"";
	}
	
	public boolean isStringValueEmpty()
	{
		String toStringValue = this.toString();
		toStringValue.replace("source=\"", "");
		if(toStringValue.contains("=\"\""))
			return false;
		else return true;
	}

}
