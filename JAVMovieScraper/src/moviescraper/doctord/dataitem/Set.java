package moviescraper.doctord.dataitem;

public class Set extends MovieDataItem {

	private String set;
	public static final Set BLANK_SET = new Set("");

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = sanitizeString(set);
	}

	public Set(String set) {
		setSet(set);
	}

	@Override
	public String toString() {
		return "Set [set=\"" + set + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
