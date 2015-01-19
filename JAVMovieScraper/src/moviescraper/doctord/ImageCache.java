package moviescraper.doctord;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class ImageCache {
	private static Map<URL, Image> cache = Collections.synchronizedMap(new HashMap<URL, Image>());
	
	public static Image getImageFromCache(URL url) throws IOException
	{
		//Cache already contains the item, so just return it
		if(cache.containsKey(url))
		{
			return cache.get(url);
		}
		//we didn't find it, so read the Image into the cache and also return it
		else
		{
			try{
				if(url != null)
				{
					Image imageFromUrl = ImageIO.read(url);
					if(imageFromUrl != null)
					{
					cache.put(url, imageFromUrl);
					return imageFromUrl;
					}
				}

					//we couldn't read in the image from the URL so just return a blank image
					
					Image blankImage = createBlankImage();
					cache.put(url, blankImage);
					return blankImage;
			}
			catch (OutOfMemoryError e) {
                System.out.println("We ran out of memory..clearing the cache. It was size " + cache.size() 
                		+ " before the clear");
                cache.clear();
                return ImageIO.read(url);
            }
			catch(IIOException e)
			{
				e.printStackTrace();
				Image blankImage = createBlankImage();
				cache.put(url, blankImage);
				return blankImage;
			}
		}
	}
	
	private static Image createBlankImage()
	{
		return new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static void removeImageFromCachce(URL url)
	{
		cache.remove(url);
	}
	
	public static boolean isImageCached(URL url)
	{
		return cache.containsKey(url);
	}
}
