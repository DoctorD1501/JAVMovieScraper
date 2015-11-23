package moviescraper.doctord.model.dataitem;

import java.util.ArrayList;

public class Tag extends MovieDataItem {
	
	public static final ArrayList<Tag> BLANK_TAGS = new ArrayList<Tag>();
	String tag;

	public Tag(String tag) {
		setTag(tag);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "Tag [tag=\"" + tag + "\"" + dataItemSourceToString() + "]";
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = sanitizeString(tag);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public Tag(){
		tag = "";
	}
	
	

}

