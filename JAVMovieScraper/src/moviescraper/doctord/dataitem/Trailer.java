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
		//we don't want to rewrite trailer if the file already exists since that can retrigger a pointlessly long download
		if(getTrailer() != null && getTrailer().length() > 0 && !fileNameToWrite.exists())
		{
			System.out.println("Writing trailer: " + this.toString() + " into file " + fileNameToWrite);
			FileUtils.copyURLToFile(new URL(getTrailer()), fileNameToWrite, connectionTimeout, readTimeout);
		}
	}
	
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
