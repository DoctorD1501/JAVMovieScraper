package moviescraper.doctord.dataitem;

public class Votes extends MovieDataItem {

	private String votes;
	public static final Votes BLANK_VOTES = new Votes("");

	public String getVotes() {
		return votes;
	}

	public void setVotes(String votes) {
		this.votes = sanitizeString(votes);
	}

	public Votes(String votes) {
		super();
		setVotes(votes);
	}

	@Override
	public String toString() {
		return "Votes [votes=\"" + votes + "\"" + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Votes(){
		votes = "";
	}

}
