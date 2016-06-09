package moviescraper.doctord.controller;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
/**
 * Wrapper class around standard methods to download images from urls or write a url to a file 
 * so that set up a custom connection that allows us to set a user agent, etc.
 * This is necessary because some servers demand a user agent to download from them or a 403 error will be encountered.
 */
public class FileDownloaderUtilities {
	
	private static URLConnection getDefaultUrlConnection(URL url) throws IOException
	{
		final URLConnection connection = (URLConnection) url
		        .openConnection();
		connection.setRequestProperty(
		    "User-Agent",
		    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
		return connection;
	}
	
	public static Image getImageFromUrl(URL url) throws IOException
	{
		URLConnection urlConnectionToUse = FileDownloaderUtilities.getDefaultUrlConnection(url);
		try (InputStream inputStreamToUse = urlConnectionToUse.getInputStream();) {
			Image imageFromUrl = ImageIO.read(inputStreamToUse);
			return imageFromUrl;
		}
	}
	
	public static void writeURLToFile(URL url, File file) throws IOException
	{
		FileUtils.copyInputStreamToFile(FileDownloaderUtilities.getDefaultUrlConnection(url).getInputStream(), file);
	}

}
