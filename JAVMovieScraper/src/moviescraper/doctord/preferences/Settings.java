package moviescraper.doctord.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Settings {

	private static Properties programPreferences;
	private static final String fileNameOfPreferences = "settings.xml";

	protected interface Key {
		String getKey(); 
	}

	/*initialization block*/{
		programPreferences = new Properties();
		try {
			programPreferences.loadFromXML(new FileInputStream(fileNameOfPreferences));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			savePreferences(); //file doesn't exist. this will create the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Settings() {
		super();
	}

	public void savePreferences() {
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

	protected <K extends Key> void setBooleanValue(K preferenceName, boolean preferenceValue) {
		String key = preferenceName.getKey();
		if(preferenceValue)
			programPreferences.setProperty(key, "true");
		else
			programPreferences.setProperty(preferenceName.toString(), "false");
		
		savePreferences();
	}

	/**
	 * 
	 * @param preferenceName the preference field to set
	 * @param defaultValue the value to return if the preference has not been set
	 * @return
	 */
	protected <K extends Key> boolean getBooleanValue(K preferenceName, boolean defaultValue) {
		String fieldValue = preferenceName.getKey();
		String preferenceValue = programPreferences.getProperty(fieldValue);
		if(preferenceValue == null)
			return defaultValue;
		if(preferenceValue.equals("true"))
			return true;
		else if(preferenceValue.equals("false"))
			return false;
		
		return defaultValue;
	}
	
	protected <K extends Key>  void setStringValue(K preferenceName, String preferenceValue) {
		programPreferences.setProperty(preferenceName.getKey(), preferenceValue);
		savePreferences();
	}

	protected <K extends Key> String getStringValue(K preferenceName, String defaultValue) {
		String fieldValue = preferenceName.getKey();
		String preferenceValue = programPreferences.getProperty(fieldValue);
		if(preferenceValue != null)
			return preferenceValue;
		
		return defaultValue;
	}
	
		
	public String toString(){
		return programPreferences.toString();
	}
}