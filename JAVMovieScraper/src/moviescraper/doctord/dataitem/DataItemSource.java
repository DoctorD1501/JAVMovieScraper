package moviescraper.doctord.dataitem;

/**
 * Where did this data item come from - i.e from disk, from a specific scraper, user supplied, etc
 *
 */
public interface DataItemSource {
	public String getDataItemSourceName();

}
