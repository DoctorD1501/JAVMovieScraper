package moviescraper.doctord.controller;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.nodes.Element;

public class UtilityFunctions {

	/**
	 * Returns a deep copy of the object by serializing it and then deserializing it
	 * @param root - the object to clone
	 * @return a deep copy of the object
	 */
	public static Object cloneObject(Object root) {
		return JsonReader.jsonToJava(JsonWriter.objectToJson(root));
	}

	public static String HtmlElementPreferredAttributeGet(Element element, String[] attributes) {
		for (String attribute : attributes) {
			if (element.hasAttr(attribute)) {
				return element.attr(attribute);
			}
		}
		return null;
	}

	public static boolean saveFile(URL url, File outputFile, String referer) {
		boolean isSucceed = true;

		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url.toString());
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.11 Safari/537.36");
		httpGet.addHeader("Referer", referer);

		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity imageEntity = httpResponse.getEntity();

			if (imageEntity != null) {
				FileUtils.copyInputStreamToFile(imageEntity.getContent(), outputFile);
			}

		} catch (IOException e) {
			isSucceed = false;
		}

		httpGet.releaseConnection();

		return isSucceed;
	}

	public static boolean saveFile(URL url, String outputPath) {

		return saveFile(url, new File(outputPath), url.toString());
	}

	public static boolean saveFile(URL url, File outputFile) {

		return saveFile(url, outputFile, url.toString());
	}

}
