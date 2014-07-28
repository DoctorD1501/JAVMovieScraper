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
	private static final String writeFanartAndPostersPropertyName = "writeFanartAndPosters";
	private static final String overwriteFanartAndPostersPropertyName = "overWriteFanartAndPosters";
	
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
	
	public void setOverWriteFanartAndPostersPreference(boolean preferenceValue){
		if(preferenceValue)
			programPreferences.setProperty(overwriteFanartAndPostersPropertyName, "true");
		else
			programPreferences.setProperty(overwriteFanartAndPostersPropertyName, "false");
		savePreferences();
	}
	
	public boolean getOverWriteFanartAndPostersPreference()
	{
		String overwriteFanartAndPostersPref = programPreferences.getProperty(overwriteFanartAndPostersPropertyName);
		if(overwriteFanartAndPostersPref != null)
		{
		if(overwriteFanartAndPostersPref.equals("true"))
			return true;
		else return false;
		}
		else return true; //default value if no preference has been set yet
	}
	
	public void setWriteFanartAndPostersPreference(boolean preferenceValue){
		if(preferenceValue)
			programPreferences.setProperty(writeFanartAndPostersPropertyName, "true");
		else
			programPreferences.setProperty(writeFanartAndPostersPropertyName, "false");
		savePreferences();
	}
	
	public boolean getWriteFanartAndPostersPreference()
	{
		String writeFanartAndPostersPref = programPreferences.getProperty(writeFanartAndPostersPropertyName);
		if(writeFanartAndPostersPref != null)
		{
		if(writeFanartAndPostersPref.equals("true"))
			return true;
		else return false;
		}
		else return true; //default value if no preference has been set yet
	}
	

}
