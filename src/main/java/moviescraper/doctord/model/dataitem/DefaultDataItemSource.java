package moviescraper.doctord.model.dataitem;

import javax.swing.ImageIcon;

public class DefaultDataItemSource implements DataItemSource{
	
	public static final DefaultDataItemSource DEFAULT_DATA_ITEM_SOURCE = new DefaultDataItemSource();
	private boolean isDisabled;
	ImageIcon profileIcon;
	
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

	@Override
	public ImageIcon getProfileIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
