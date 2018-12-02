package moviescraper.doctord.controller.siteparsingprofile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import moviescraper.doctord.controller.siteparsingprofile.specific.SpecificProfile;

public class SpecificProfileFactory {

	public static Collection<SiteParsingProfileItem> getAll() {
		try {
			return getSiteParsingProfileItems();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException | URISyntaxException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("rawtypes")
	private static Vector<SiteParsingProfileItem> getSiteParsingProfileItems() throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, URISyntaxException {

		Vector<SiteParsingProfileItem> items = new Vector<>();

		String packageName = SpecificProfile.class.getPackage().getName();
		List<Class> classes = getClasses(packageName);
		for (Class c : classes) {
			if (c.isInterface())
				continue;

			Object instance = c.newInstance();
			if (instance instanceof SpecificProfile && instance instanceof SiteParsingProfile) {
				SpecificProfile sp = (SpecificProfile) instance;
				String title = sp.getParserName();
				SiteParsingProfileItem comboItem = new SiteParsingProfileItem(title, (SiteParsingProfile) instance);
				items.add(comboItem);
			}

		}

		//Make sure the scrapers in the combo box are in alphabetic order
		Collections.sort(items, new Comparator<SiteParsingProfileItem>() {

			@Override
			public int compare(SiteParsingProfileItem arg0, SiteParsingProfileItem arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		});

		return items;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException, URISyntaxException {
		List<Class> classes = new ArrayList<>();
		if (!directory.exists()) {
			//maybe we are running from a jar file, so try that

			File thisJarFile = new File(SpecificProfileFactory.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			if (thisJarFile.exists()) {
				List<Class> classNames = new ArrayList<>();
				try (ZipInputStream zip = new ZipInputStream(new FileInputStream(thisJarFile.getPath()))) {
					for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry())
						if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
							// This ZipEntry represents a class. Now, what class does it represent?
							StringBuilder className = new StringBuilder();
							for (String part : entry.getName().split("/")) {
								if (className.length() != 0)
									className.append(".");
								className.append(part);
								if (part.endsWith(".class"))
									className.setLength(className.length() - ".class".length());
							}

							if (className.toString().contains(packageName)) {
								classNames.add(Class.forName(className.toString()));
							}
							zip.closeEntry();
						}
				}
				return classNames;
			} else
				return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

}
