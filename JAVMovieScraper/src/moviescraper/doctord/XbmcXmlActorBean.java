package moviescraper.doctord;

import java.io.IOException;

import moviescraper.doctord.dataitem.Actor;

public class XbmcXmlActorBean {
	
	public String name;
	public String role;
	public String thumb;
	public XbmcXmlActorBean(String name, String role, String thumb) {
		super();
		this.name = name;
		this.role = role;
		this.thumb = thumb;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	
	public Actor toActor() throws IOException{
		return new Actor(name,role,new Thumb(thumb));
	}

}
