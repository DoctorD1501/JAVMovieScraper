package moviescraper.doctord.controller.xmlserialization;

import java.io.IOException;

import moviescraper.doctord.model.dataitem.Thumb;

/**
 * Helper class for serializing a fanart object to and from XML
 */
public class KodiXmlFanartBean {
	private String [] thumb;

	public KodiXmlFanartBean(String [] thumb) {
		super();
		this.thumb = thumb;
	}
	
	public KodiXmlFanartBean(Thumb [] thumb){
		if (thumb.length == 0)
		{
			this.thumb = new String[0];
		}
		else
		{
		this.thumb = new String[thumb.length];
		for (int i = 0; i < thumb.length; i++){
			this.thumb[i] = thumb[i].getThumbURL().toString();
		}
		}
	}

	public String [] getThumb() {
		return thumb;
	}

	public void setThumb(String[] thumb) {
		this.thumb = thumb;
	}
	
	public Thumb[] toFanart() throws IOException{
		Thumb[] fanart = new Thumb[thumb.length];
		for(int i = 0; i < fanart.length; i++)
		{
			fanart[i] = new Thumb(thumb[i]);
		}
		return fanart;
	}
}
