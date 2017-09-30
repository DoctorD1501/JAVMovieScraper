package moviescraper.doctord.model.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Settings {

	private final static Properties programPreferences = new Properties();
	private static final String fileNameOfPreferences = "settings.xml";

	protected interface Key {
		String getKey();
	}

	//Initialization that only happens once
	static {
		try (FileInputStream settingsInputStream = new FileInputStream(fileNameOfPreferences);) {
			programPreferences.loadFromXML(settingsInputStream);

		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Creating settings.xml since it was not found...");
			savePreferences(); //file doesn't exist. this will create the file
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected Settings() {
		//prevent other people from calling this
	}

	public static void savePreferences() {
		try (FileOutputStream settingsOutputStream = new FileOutputStream(fileNameOfPreferences);) {
			programPreferences.storeToXML(settingsOutputStream, "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected <K extends Key> void setBooleanValue(K preferenceName, Boolean preferenceValue) {
		String key = preferenceName.getKey();
		if (preferenceValue.booleanValue())
			programPreferences.setProperty(key, "true");
		else
			programPreferences.setProperty(key, "false");

		savePreferences();
	}

	/**
	 * 
	 * @param preferenceName the preference field to set
	 * @param defaultValue the value to return if the preference has not been set
	 * @return
	 */
	protected <K extends Key> Boolean getBooleanValue(K preferenceName, Boolean defaultValue) {
		String fieldValue = preferenceName.getKey();
		String preferenceValue = programPreferences.getProperty(fieldValue);
		if (preferenceValue == null)
			return defaultValue;
		if (preferenceValue.equals("true"))
			return true;
		else if (preferenceValue.equals("false"))
			return false;

		return defaultValue;
	}

	protected <K extends Key> void setStringValue(K preferenceName, String preferenceValue) {
		programPreferences.setProperty(preferenceName.getKey(), preferenceValue);
		savePreferences();
	}

	protected static <K extends Key> String getStringValue(K preferenceName, String defaultValue) {
		String fieldValue = preferenceName.getKey();
		String preferenceValue = programPreferences.getProperty(fieldValue);
		if (preferenceValue != null)
			return preferenceValue;

		return defaultValue;
	}

	@Override
	public String toString() {
		return programPreferences.toString();
	}
}