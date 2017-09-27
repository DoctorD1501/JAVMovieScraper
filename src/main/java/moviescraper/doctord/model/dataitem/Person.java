package moviescraper.doctord.model.dataitem;


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

	@Override
	abstract public String toXML();

	@Override
	public String toString() {
		return "Person [name=\"" + name + "\", thumb=" + thumb + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((thumb == null) ? 0 : thumb.hashCode());
		result = prime * result + (thumbEdited ? 1231 : 1237);
		return result;
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
		if (thumbEdited != other.thumbEdited)
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
