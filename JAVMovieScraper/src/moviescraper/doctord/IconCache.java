package moviescraper.doctord;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;


public class IconCache {
private static Map<String, Icon> cache = Collections.synchronizedMap(new HashMap<String, Icon>());
	
	public static Icon getIconFromCache(File iconType) throws IOException
	{
		//Cache already contains the item, so just return it
		if(cache.containsKey(FilenameUtils.getExtension(iconType.getName())))
		{
			return cache.get(FilenameUtils.getExtension(iconType.getName()));
		}
		//we didn't find it, so read the Icon into the cache and also return it
		else
		{
			Icon iconToCache = FileSystemView.getFileSystemView().getSystemIcon(iconType);
			cache.put(FilenameUtils.getExtension(iconType.getName()), iconToCache);
			return iconToCache;
		}
	}
}
