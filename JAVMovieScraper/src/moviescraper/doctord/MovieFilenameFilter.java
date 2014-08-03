package moviescraper.doctord;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

public class MovieFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(allowedSuffix(FilenameUtils.getExtension(name)))
			return true;
		else return false;
	}
	
	private boolean allowedSuffix(String suffix)
	{
		switch (suffix.toLowerCase())
		{
		case "avi": case "mpeg": case "mpg": case "wmv": case "asf": case "flv": case "mkv": case "mka": case "mov": case "qt": case "mp4": 
		case "m4a": case "aac": case "nut": case "ogg": case "ogm": case "rmvb": case "rm": case "ram": case "ra": case "3gp": 
		case "vivo": case "pva": case "nuv": case "nsa": case "fli": case "flc": case "dvr-ms": case "wtv":
			return true;
		}
		return false;
	}

}
