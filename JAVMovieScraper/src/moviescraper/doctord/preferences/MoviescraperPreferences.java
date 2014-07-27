package moviescraper.doctord.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class MoviescraperPreferences {
	
	Properties programPreferences;
	private static final String fileNameOfPreferences = "settings.xml";
	private static final String lastUsedDirectoryPropertyName = "lastUsedDirectory";
	
	public MoviescraperPreferences()
	{
		programPreferences = new Properties();
		try {
			programPreferences.loadFromXML(new FileInputStream(fileNameOfPreferences));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void savePreferences(){
		try {
			programPreferences.storeToXML(new FileOutputStream(fileNameOfPreferences), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public File getLastUsedDirectory(){
		String lastUsedDir = programPreferences.getProperty(lastUsedDirectoryPropertyName);
		if(lastUsedDir != null)
		{
			File lastUsedDirFile = new File(lastUsedDir);
			if(lastUsedDirFile.exists())
				return lastUsedDirFile;
			else return new File(System.getProperty("user.home"));
		}
		else return new File(System.getProperty("user.home"));
	}
	
	public void setLastUsedDirectory(File lastUsedDirectory){
		programPreferences.setProperty(lastUsedDirectoryPropertyName, lastUsedDirectory.getPath());
		savePreferences();
	}

}
