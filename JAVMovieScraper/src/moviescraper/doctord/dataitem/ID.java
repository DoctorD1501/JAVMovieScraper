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

}
