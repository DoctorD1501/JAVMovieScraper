package moviescraper.doctord.model.dataitem;

import java.util.ArrayList;

public class Tag extends MovieDataItem {

	public static final ArrayList<Tag> BLANK_TAGS = new ArrayList<>();
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
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
		Tag other = (Tag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	public Tag() {
		tag = "";
	}

}
