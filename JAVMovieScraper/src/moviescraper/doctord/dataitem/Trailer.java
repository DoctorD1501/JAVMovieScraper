package moviescraper.doctord.dataitem;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Trailer extends MovieDataItem {

	private String trailer;

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		if(trailer == null)
			this.trailer = "";
		else this.trailer = trailer;
	}

	public Trailer(String trailer) {
		setTrailer(trailer);
	}

	@Override
	public String toString() {
		return "Trailer [trailer=" + trailer + "]";
	}
	
	public void writeTrailerToFile(File fileNameToWrite) throws IOException {
		if(getTrailer() != null && getTrailer().length() > 0)
			FileUtils.copyURLToFile(new URL(getTrailer()), fileNameToWrite, connectionTimeout, readTimeout);
	}
	
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
