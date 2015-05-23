package moviescraper.doctord.Amalgamation;

import java.util.LinkedList;
import moviescraper.doctord.SiteParsingProfile.SiteParsingProfileItem;
import moviescraper.doctord.dataitem.DataItemSource;
import moviescraper.doctord.dataitem.DefaultDataItemSource;

/**
 * Ranked list of what DataItemSources I would prefer to pick from when doing an amalgamation
 *
 */
public class DataItemSourceAmalgamationPreference {
	
	private LinkedList<DataItemSource> amalgamationPreferenceOrder;
	
	/**
	 * 
	 * @param dataItemSources - the list of preferred items to use to amalgamate. the first parameter passed
	 * in is the most preferred item, the second the second most preferred, and so on
	 */
	public DataItemSourceAmalgamationPreference(DataItemSource ...dataItemSources)
	{
		amalgamationPreferenceOrder = new LinkedList<DataItemSource>();
		for (DataItemSource dis : dataItemSources)
		{
			amalgamationPreferenceOrder.add(dis);
		}
		
		//Always put this at the end as a fall back for items which didn't get their data item source set another way
		//to allow us to still pick them in case no other item from a more preferred source was found first when amalgamating
		if(!amalgamationPreferenceOrder.contains(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE))
			amalgamationPreferenceOrder.add(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE);
	}
	
	public DataItemSourceAmalgamationPreference(LinkedList<DataItemSource> amalgamationPreferenceOrder)
	{
		this.amalgamationPreferenceOrder = amalgamationPreferenceOrder;
	}

	public LinkedList<DataItemSource> getAmalgamationPreferenceOrder() {
		return amalgamationPreferenceOrder;
	}

	public void setAmalgamationPreferenceOrder(
			LinkedList<DataItemSource> amalgamationPreferenceOrder) {
		this.amalgamationPreferenceOrder = amalgamationPreferenceOrder;
	}
	
	public void setAmalgamationPreferenceOrder(SiteParsingProfileItem[] parsingProfileItems)
	{
		if(parsingProfileItems != null)
		{
			amalgamationPreferenceOrder = new LinkedList<DataItemSource>();
			for(SiteParsingProfileItem currentParsingProfileItem : parsingProfileItems)
			{
				if(!currentParsingProfileItem.isDisabled())
					amalgamationPreferenceOrder.add(currentParsingProfileItem.getParser());
			}
			if(!amalgamationPreferenceOrder.contains(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE))
				amalgamationPreferenceOrder.add(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE);
		}
		else
		{
			amalgamationPreferenceOrder = new LinkedList<DataItemSource>();
			amalgamationPreferenceOrder.add(DefaultDataItemSource.DEFAULT_DATA_ITEM_SOURCE);
		}
	}
	
	@Override
	public String toString()
	{
		return amalgamationPreferenceOrder.toString();
	}
}
