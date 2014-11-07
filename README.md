JAVMovieScraper
===============

JAVMovieScraper is a Java Swing program to scrape English [XBMC](http://xbmc.org/) metadata for Japanese Adult Videos (JAV) found on JavLibrary.com, DMM.co.jp, and Caribbeancompr.com (Carribeancom Premium), AV Entertainment, Kin8Tengoku, Tokyo Hot, 1pondo, HEYZO, and American adult DVDs and web content found on Data18.com.

As no one site has a complete set of English metadata, the program amalgamates metadeta info from a variety of sources, including dmm.co.jp, javlibrary.com, squareplus.co.jp, and actionjav.com.
The data is then fed through a machine translation (if original data is in Japanese) and then quality checked to sanitize it and poster elements are cropped so only the cover is shown.



This program is in alpha. Please submit bugs and feature requests here on github on the issues page!

###### Usage

1. Make sure you have the Java JRE installed. You will need at least Java version 7. Java can be downloaded here: https://www.java.com/en/download/index.jsp
2. Either compile the source yourself or download and run the precompiled JAR from here: http://www.mediafire.com/download/pm3d2yl49qa99fe/JAVMovieScraper.jar
3. Initially, the program will load your home directory in the file pane on the left. Click the "Browse Directory" button below this file list and point it to the directory where your movie file you wish to scrape is.
4. Select the movie file or folder the movie is in (if the folder is named the same as the movie) in the list of files. You can select multiple files by holding the control or shift keys to do batch scraping. Your movie file MUST have the JAV ID as the last word within the filename, not including stacked file indicators such as DISC1 or CD1. The JAV ID (or Caribbeancom Release ID) can be optionally surrounded by brackets or parenthesis and can contain a dash before the numerical part. Examples of OK file names for JAV DVD Movies: My Movie - ABC-123, My Movie - [ABC123] CD1, ABC-123, (ABC-123), For American movies, the filename must be the name of the movie, optionally followed by the year in parenthesis e.g. MovieName (2014). For web releases, a google search is done on the entire file name, so it's more flexible, but it works best if you include the name of the episode and at least one of the actors in your file name. See the section below for more file naming conventions for the site specific scraper.
5. Click either the "Scrape JAV" button or the "Scrape JAV (Automatic)" button on the bottom part of the program for Japanese content or the "Scrape Data18 Movie" for adult DVDs or the "Scrape Data18 WebContent" for content downloaded from websites or split scenes. For Japanese movies, "Scrape JAV (Automatic)" will work 99% of the time, but if you get the wrong result when scraping, try using "Scrape" instead to manually specify which URL to use when scraping dmm.co.jp and javlibrary. It's also worth trying "Scrape" if actor images are not appearing since perhaps JAVLibrary and DMM were scraping two different movies.
6. After a little while, the metadata for the movie will appear in the editor pane. You can select one of the several titles found using the drop down list, or edit the entry by typing in your own text. For now, there is no way to edit the genres or actors.
7. When you are happy with the way the metadata looks, click the "Write File Data" button to create the poster,fanart and nfo files for your movie. Note that for now, not all metadata downloaded is shown in the editor, but this data IS written to the nfo file.
8. If your file wasn't already in its own directory, you can click the "Move File to New Folder" button to move the nfo, poster, movie files, fanart, .actor files, and trailer to a new folder. If the movie file was only named by the ID name (e.g. ABC-123), then the title of the movie will be automatically appended to the folder name.
9. It's worth checking out the preferences menu to customize what info gets written and how it is named.


In the future I intend to add command line options.

###### Site Specific File Name Conventions
When using the site specific scraper feature, your file name must contain an ID number which conforms to the release ID conventions set by that site. 
* Aventertainments: This follows the usual JAV id naming structure like ABC-123. It does a search on the site using this ID.
* Kin8tengoku: The ID is in the URL. It is the numeric part before /pht/ and is usually 4 numeric digits. Example: 1147.
* Tokyohot: The ID follows the format of n123 or n1234. In other words, a lowercase n followed by a 3 or 4 digit number. 
* 1pondo: The ID is in the URL of the movie, right before /index.html. The first part of the ID is a 6 digit number corresponding to the release date, followed by an underscore, followed by a 3 digit number. Example: 061314_826
* Caribbeancom Premium: The ID is in the URL of the movie, right before /index.html. The first part of the ID is a 6 digit number corresponding to the release date, followed by an underscore, followed by a 3 digit number. Example: 061314_826
* Heyzo: The ID is a 4 digit number in the url right after /moviepages/. Example: 0123

###### What If I Use Plex?
XBMC Metadata is compatible with [Plex](https://plex.tv/) using the [XBMCnfoMovieImporter](https://forums.plex.tv/index.php/topic/38402-metadata-agents-for-exported-xbmc-library/) from the [Unsupported Appstore Channel](https://forums.plex.tv/index.php/topic/25523-unsupported-as-in-totally-unofficial-appstore/).

###### Other Good Programs for Viewing/Browsing XBMC Scraped Files
Try [Media Companion](https://mediacompanion.codeplex.com/). It's easier to use when sitting at a computer than XBMC because the interface is designed for keyboard & mouse use rather than a remote and large screen.