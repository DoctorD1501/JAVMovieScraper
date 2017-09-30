package moviescraper.doctord.controller;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class UtilityFunctions {

	/**
	 * Returns a deep copy of the object by serializing it and then deserializing it
	 * @param root - the object to clone
	 * @return a deep copy of the object
	 */
	public static Object cloneObject(Object root) {
		return JsonReader.jsonToJava(JsonWriter.objectToJson(root));
	}

}
