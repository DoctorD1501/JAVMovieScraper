package moviescraper.doctord.dataitem;

public class DefaultDataItemSource implements DataItemSource{
	
	public static final DefaultDataItemSource DEFAULT_DATA_ITEM_SOURCE = new DefaultDataItemSource(); 
	
	@Override
	public String getDataItemSourceName() {
		return "Default Data Item Source";
	}
	
	@Override
	public String toString()
	{
		return getDataItemSourceName();
	}

}
