package moviescraper.doctord.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Settings {

	private static Properties programPreferences;
	private static final String fileNameOfPreferences = "settings.xml";
	
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

	protected void setBooleanValue(String preferenceName, boolean preferenceValue) {
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			if(preferenceValue)
				programPreferences.setProperty((String)fieldToUse.get(new String()), "true");
			else
				programPreferences.setProperty((String)fieldToUse.get(new String()), "false");
			savePreferences();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	/**
	 * 
	 * @param preferenceName the preference field to set - must be the exact same as the field's local variable name in {@link MoviescraperPreferences}
	 * @param defaultValue the value to return if the preference has not been set
	 * @return
	 */
	protected boolean getBooleanValue(String preferenceName, boolean defaultValue) {
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			String fieldValue = (String)fieldToUse.get(new String());
			String preferenceValue = programPreferences.getProperty(fieldValue);
			if(preferenceValue == null)
				return defaultValue;
			if(preferenceValue.equals("true"))
				return true;
			else if(preferenceValue.equals("false"))
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultValue;
	}
	

	protected void setStringValue(String preferenceName, String preferenceValue) {
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			programPreferences.setProperty((String)fieldToUse.get(new String()), preferenceValue);
			savePreferences();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	/**
	 * 
	 * @param preferenceName the preference field to set - must be the exact same as the field's local variable name in {@link MoviescraperPreferences}
	 * @param defaultValue the value to return if the preference has not been set
	 * @return
	 */
	protected String getStringValue(String preferenceName, String defaultValue) {
		Field fieldToUse;
		try {
			fieldToUse = this.getClass().getDeclaredField(preferenceName);
			String fieldValue = (String)fieldToUse.get(new String());
			String preferenceValue = programPreferences.getProperty(fieldValue);
			if(preferenceValue != null)
				return preferenceValue;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	
	public String toString(){
		return programPreferences.toString();
	}
}