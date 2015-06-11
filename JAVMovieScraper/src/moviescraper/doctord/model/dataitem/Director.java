package moviescraper.doctord.model.dataitem;


public class Director extends Person {

	public Director(String name, Thumb thumb) {
		super(name, thumb);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "Director [toString()=" + super.toString() + " ,\"" +dataItemSourceToString() + "\"]";
	}

	public Director(){
		super();
	}

}
