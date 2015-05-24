package moviescraper.doctord.dataitem;


public abstract class Person extends MovieDataItem {
	private String name;
	private Thumb thumb;
	private boolean thumbEdited; //did we change the URL of the thumb since loading and thus need to force a refresh


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
		this.thumbEdited = false;
	}

	abstract public String toXML();

	@Override
	public String toString() {
		return "Person [name=\"" + name + "\", thumb=" + thumb + "]";
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (thumb == null) {
			if (other.thumb != null)
				return false;
		} else if (!thumb.equals(other.thumb))
			return false;
		return true;
	}

	public boolean isThumbEdited() {
		return thumbEdited;
	}

	public void setThumbEdited(boolean thumbEdited) {
		this.thumbEdited = thumbEdited;
	}
	
	public Person(){
		name = "";
		thumb = null;
		thumbEdited = false;
	}

}
