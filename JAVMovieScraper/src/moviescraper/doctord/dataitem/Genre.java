package moviescraper.doctord.dataitem;

public class Genre extends MovieDataItem {
	
	String genre;

	public Genre(String genre) {
		setGenre(genre);
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "Genre [genre=" + genre + "]";
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = sanitizeString(genre);
	}

}
