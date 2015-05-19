package moviescraper.doctord;

import moviescraper.doctord.dataitem.Thumb;

public class SearchResult {
	
	private String urlPath;
	private String label;
	private Thumb previewImage;
	boolean isJSONSearchResult;
	
	
	public SearchResult(String urlPath, String label, Thumb previewImage) {
		super();
		this.urlPath = urlPath;
		this.previewImage = previewImage;
		this.label = label;
	}
	public SearchResult(String urlPath) {
		this.urlPath = urlPath;
		previewImage = new Thumb();
		label = "";
	}
	
	public SearchResult(String urlPath, String label)
	{
		this.urlPath = urlPath;
		this.label = label;
		previewImage = new Thumb();
	}
	public String getUrlPath() {
		return urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	public Thumb getPreviewImage() {
		return previewImage;
	}
	public void setPreviewImage(Thumb previewImage) {
		this.previewImage = previewImage;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString()
	{
		if(label.length() > 0)
			return label + " - " + urlPath;
		else return urlPath;
	}
	public boolean isJSONSearchResult() {
		return isJSONSearchResult;
	}
	public void setJSONSearchResult(boolean isJSONSearchResult) {
		this.isJSONSearchResult = isJSONSearchResult;
	}
	
}
