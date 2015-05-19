package moviescraper.doctord.dataitem;

import moviescraper.doctord.Amalgamation.DataItemSourceAmalgamationPreference;

public abstract class MovieDataItem {
	
	protected final static int connectionTimeout = 10000; //10 seconds
	protected final static int  readTimeout = 10000; //10 seconds
	private DataItemSource dataItemSource;
	//if we want a custom amalgamation preference order on this particular item instead of the one assigned to the entire MovieScrapeResultGroup
	private DataItemSourceAmalgamationPreference dataItemAmalgamtionPreference; 
	
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

	public DataItemSourceAmalgamationPreference getDataItemAmalgamtionPreference() {
		return dataItemAmalgamtionPreference;
	}

	public void setDataItemAmalgamtionPreference(
			DataItemSourceAmalgamationPreference dataItemAmalgamtionPreference) {
		this.dataItemAmalgamtionPreference = dataItemAmalgamtionPreference;
	}


	
	

}
