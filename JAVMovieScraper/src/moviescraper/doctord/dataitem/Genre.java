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
		return "Genre [genre=\"" + genre + "\"" + dataItemSourceToString() + "]";
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = sanitizeString(genre);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genre other = (Genre) obj;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		return true;
	}
	
	

}
