package moviescraper.doctord.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;


public class IconCache {

	/**
	 * Enumeration of icon providers for the items on the file list view
	 */
	public enum IconProviderType
	{
		SYSTEM,  /// Icon provider that uses the system shell to get an icon (default)
		CONTENT, /// Icon provider that determines icon by file content type
	}

	// Internal interface used by IconCache
	private interface IconProvider
	{
		// return an icon for the specified file
		Icon getIcon(File iconType) throws IOException;
	}

	// Default icon provider implementation: get shell icons on windows, only folder or file on linux
	private static class SystemIconProvider implements IconProvider
	{
		@Override
		public Icon getIcon(File iconType) throws IOException {
			return FileSystemView.getFileSystemView().getSystemIcon(iconType);
		}
	}

	// Alternative icon provider implementation: return resource icons based on file content type
	private static class ContentIconProvider implements IconProvider
	{
		private Icon createIcon(String name)
		{
			return new ImageIcon(getClass().getResource("/res/mime/" + name + ".png"));
		}

		@Override
		public Icon getIcon(File iconType) throws IOException {

			// return default icon for folders (no need for a custom png)

			if (iconType.isDirectory())
				return FileSystemView.getFileSystemView().getSystemIcon(iconType);

			// don't probe content type for files without extension as this icon will be cached for all of them,
			// skip dot files too (.hidden files on Linux)

			String name = FilenameUtils.getName(iconType.getName());
			String ext  = FilenameUtils.getExtension(iconType.getName());

			if (ext != "" && !name.startsWith("."))
			{
				// determine content type

				String mimeType = Files.probeContentType(iconType.toPath());

				if (mimeType != null)
				{
					// take top-level type into account only, this five types should cover the vast majority of cases

					for(String type: new String[] { "text", "image", "video", "audio", "application" })
						if (mimeType.startsWith(type + "/"))
							return createIcon(type);
				}
			}

			// fallback to default file icon
			return createIcon("file");
		}
	}

	private static IconProvider iconProvider = new SystemIconProvider();

	private static Map<String, Icon> cache = Collections.synchronizedMap(new HashMap<String, Icon>());

	/**
	 * Sets the icon provider for the file list view
	 * @param type The icon provider type
	 */
	public static void setIconProvider(IconProviderType type)
	{
		// determine current provider by class name, set only if necessary

		String newType = type.toString() + "IconProvider";
		String oldType = iconProvider.getClass().getSimpleName();

		if (!newType.equalsIgnoreCase(oldType))
		{
			// flush cache
			cache.clear();

			switch(type)
			{
			case SYSTEM:    
				iconProvider = new SystemIconProvider();
				break;
			case CONTENT:  
				iconProvider = new ContentIconProvider();
				break;
			default:
				break;
			}
		}
	}



	public static Icon getIconFromCache(File iconType) throws IOException
	{
		// use "." as key for folders so we don't get a cached folder icon for files without extension or vice versa
		// use "" as key for dot files (hidden files on Linux), to prevent getting a cache entry per file

		String name = FilenameUtils.getName(iconType.getName());
		String ext  = FilenameUtils.getExtension(iconType.getName());
		String key  = iconType.isDirectory() ? "." : name.startsWith(".") ? "" : ext;

		//Cache already contains the item, so just return it
		if(cache.containsKey(key))
		{
			return cache.get(key);
		}
		//we didn't find it, so read the Icon into the cache and also return it
		else
		{
			Icon iconToCache = iconProvider.getIcon(iconType);
			cache.put(key, iconToCache);

			// System.err.println("[IconCache] Caching type " + key);
			return iconToCache;
		}
	}
}
