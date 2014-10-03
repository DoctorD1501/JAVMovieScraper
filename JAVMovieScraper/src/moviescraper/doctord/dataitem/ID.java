package moviescraper.doctord.dataitem;



public class ID extends MovieDataItem {
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = sanitizeString(id);
	}

	@Override
	public String toString() {
		return "ID [id=" + id + "]";
	}

	public ID(String id) {
		super();
		setId(id);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ID other = (ID) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
