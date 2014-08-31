JAVMovieScraper
===============

JAVMovieScraper is a Java Swing program to scrape English [XBMC](http://xbmc.org/) metadata for Japanese Adult Videos (JAV) found on JavLibrary.com, DMM.co.jp, and Caribbeancompr.com (Carribeancom Premium) and American adult DVDs and web content found on Data18.com.

As no one site has a complete set of English metadata, the program amalgamates metadeta info from a variety of sources, including dmm.co.jp, javlibrary.com, squareplus.co.jp, and actionjav.com.
The data is then fed through a machine translation (if original data is in Japanese) and then quality checked to sanitize it and poster elements are cropped so only the cover is shown.



This program is in alpha. Please submit bugs and feature requests here on github on the issues page!

###### Usage

1. Make sure you have the Java JRE installed. You will need at least Java version 7. Java can be downloaded here: https://www.java.com/en/download/index.jsp
2. Either compile the source yourself or download and run the precompiled JAR from here: http://www.mediafire.com/download/pm3d2yl49qa99fe/JAVMovieScraper.jar
3. Initially, the program will load your home directory in the file pane on the left. Click the open directory button below this file list and point it to the directory where your movie file you wish to scrape is.
4. Select the movie file or folder the movie is in (if the folder is named the same as the movie) in the list of files. You can select multiple files by holding the control or shift keys to do batch scraping. Your movie file MUST have the JAV ID as the last word within the filename, not including stacked file indicators such as DISC1 or CD1. The JAV ID (or Caribbeancom Release ID) can be optionally surrounded by brackets or parenthesis and can contain a dash before the numerical part. Examples of OK file names: My Movie - ABC-123, My Movie - [ABC123] CD1, ABC-123, (ABC-123), Caribbeancompr Movie -  080114_123, 080114_123. For American movies, the filename must be the name of the movie, optionally followed by the year in parenthesis e.g. MovieName (2014).
5. Click either the "Scrape JAV" button or the "Scrape JAV (Automatic)" button on the bottom part of the program for Japanese content or the "Scrape Data18 Movie" for adult DVDs or the "Scrape Data18 WebContent" for content downloaded from websites or split scenes. For Japanese movies, "Scrape JAV (Automatic)" will work 99% of the time, but if you get the wrong result when scraping, try using "Scrape" instead to manually specify which URL to use when scraping dmm.co.jp and javlibrary. It's also worth trying "Scrape" if actor images are not appearing since perhaps JAVLibrary and DMM were scraping two different movies.
6. After a little while, the metadeta for the movie will appear in the editor pane. You can select one of the several titles found using the drop down list, or edit the entry by typing in your own text. For now, there is no way to edit the genres or actors.
7. When you are happy with the way the metadata looks, click the "Write File Data" button to create the poster,fanart and nfo files for your movie. Note that for now, not all metadata downloaded is shown in the editor, but this data IS written to the nfo file.
8. If your file wasn't already in its own directory, you can click the "Move Selected Movie File to New Folder" button to move the nfo, poster, movie files, fanart, .actor files, and trailer to a new folder. If the movie file was only named by the ID name (e.g. ABC-123), then the title of the movie will be automatically appended to the folder name.
9. It's worth checking out the preferences menu to customize what info gets written and how it is named.


In the future I intend to add command line options.


###### What If I Use Plex?
XBMC Metadata is compatible with [Plex](https://plex.tv/) using the [XBMCnfoMovieImporter](https://forums.plex.tv/index.php/topic/38402-metadata-agents-for-exported-xbmc-library/) from the [Unsupported Appstore Channel](https://forums.plex.tv/index.php/topic/25523-unsupported-as-in-totally-unofficial-appstore/).

###### Other Good Programs for Viewing/Browsing XBMC Scraped Files
Try [Media Companion](https://mediacompanion.codeplex.com/). It's easier to use when sitting at a computer than XBMC because the interface is designed for keyboard & mouse use rather than a remote and large screen.