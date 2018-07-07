package moviescraper.doctord.model.dataitem;

import java.io.Serializable;

public abstract class MovieDataItem implements Serializable {

	protected final static int connectionTimeout = 10000; //10 seconds
	protected final static int readTimeout = 10000; //10 seconds
	private DataItemSource dataItemSource;

	//Any MovieDataItem needs to know how to write itself to XML
	abstract public String toXML();

	public final static String sanitizeString(String inputString) {
		if (inputString != null)
			return inputString.replace("\u00a0", " ").trim(); //replace non breaking space (&nbsp) with regular space then trim things
		else
			return null;
	}

	public DataItemSource getDataItemSource() {
		if (dataItemSource == null)
			return DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE;
		else
			return dataItemSource;
	}

	public void setDataItemSource(DataItemSource dataItemSource) {
		//Why do we create an instance of the same type? This is because a parsing profile can contain
		//a large document object which is the HTML of the entire page. we don't care about that. we only care
		//where this object came from, not the HTML of that page
		//if we don't retain the object that had all the HTML, it can get garbage collected
		this.dataItemSource = dataItemSource.createInstanceOfSameType();
	}

	public String dataItemSourceToString() {
		return " source=\"" + getDataItemSource() + "\"";
	}

	public boolean isStringValueEmpty() {
		String toStringValue = this.toString();
		if (toStringValue.contains("=\"\""))
			return false;
		else
			return true;
	}

}
