package moviescraper.doctord;

import java.io.IOException;

import moviescraper.doctord.dataitem.Thumb;

public class XbmcXmlFanartBean {
	private String [] thumb;

	public XbmcXmlFanartBean(String [] thumb) {
		super();
		this.thumb = thumb;
	}
	
	public XbmcXmlFanartBean(Thumb [] thumb){
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
