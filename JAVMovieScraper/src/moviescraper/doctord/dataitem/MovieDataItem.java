package moviescraper.doctord.dataitem;

public abstract class MovieDataItem {
	
	//Any MovieDataItem needs to know how to write itself to XML
	abstract public String toXML();
	
	public final static String sanitizeString(String inputString)
	{
			return inputString.replace("\u00a0"," ").trim(); //replace non breaking space (&nbsp) with regular space then trim things
	}

}
