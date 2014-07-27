package moviescraper.doctord.dataitem;

import moviescraper.doctord.Thumb;

public abstract class Person extends MovieDataItem {
	private String name;
	private Thumb thumb;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = sanitizeString(name);
	}

	public Thumb getThumb() {
		return thumb;
	}

	public void setThumb(Thumb thumb) {
		this.thumb = thumb;
	}

	public Person(String name, Thumb thumb) {
		setName(name);
		this.thumb = thumb;
	}

	abstract public String toXML();

	@Override
	public String toString() {
		return "Person [name=" + name + ", thumb=" + thumb + "]";
	}

}
