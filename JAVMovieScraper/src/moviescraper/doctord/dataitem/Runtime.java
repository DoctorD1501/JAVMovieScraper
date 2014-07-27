package moviescraper.doctord.dataitem;

public class Runtime extends MovieDataItem {

	private String runtime;

	@Override
	public String toString() {
		return "Runtime [runtime=" + runtime + "]";
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = sanitizeString(runtime);
	}

	public Runtime(String runtime) {
		super();
		setRuntime(runtime);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
