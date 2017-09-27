package moviescraper.doctord.model.dataitem;

import javax.swing.ImageIcon;

/**
 * Where did this data item come from - i.e from disk, from a specific scraper, user supplied, etc
 *
 */
public interface DataItemSource {
	public String getDataItemSourceName();
	
	/**
	 * 
	 * @return A new object of the same class as the caller. All fields are reinitialized
	 * to default state except for the disabled field, which will have the same value as the caller
	 */
	public DataItemSource createInstanceOfSameType();

	public boolean isDisabled();

	public void setDisabled(boolean b);
	
	public ImageIcon getProfileIcon();

}
