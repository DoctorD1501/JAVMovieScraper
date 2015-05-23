package moviescraper.doctord.dataitem;

public class DefaultDataItemSource implements DataItemSource{
	
	public static final DefaultDataItemSource DEFAULT_DATA_ITEM_SOURCE = new DefaultDataItemSource();
	private boolean isDisabled;
	
	@Override
	public String getDataItemSourceName() {
		return "Default Data Item Source";
	}
	
	@Override
	public String toString()
	{
		return getDataItemSourceName();
	}

	@Override
	public DataItemSource createInstanceOfSameType() {
		DefaultDataItemSource newInstance = new DefaultDataItemSource();
		newInstance.setDisabled(isDisabled());
		return newInstance;
	}

	@Override
	public boolean isDisabled() {
		return isDisabled;
	}

	@Override
	public void setDisabled(boolean value) {
		isDisabled = value;
	}

}
