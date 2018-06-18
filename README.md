JAVMovieScraper
===============
[![Build Status](https://travis-ci.org/Wizell/JAVMovieScraper.svg?branch=master)](https://travis-ci.org/Wizell/JAVMovieScraper)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c4c66ac391234156b9796d9a6e286843)](https://www.codacy.com/app/Wizell/JAVMovieScraper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Wizell/JAVMovieScraper&amp;utm_campaign=Badge_Grade)

JAVMovieScraper is a Java Swing program to scrape English [Kodi](http://kodi.tv/) metadata for Japanese Adult Videos (JAV) are searched on:
* JavLibrary.com
* R18.com
* DMM.co.jp
* Caribbeancompr.com (Carribeancom Premium)
* AV Entertainment
* Kin8Tengoku
* Tokyo Hot
* 1pondo
* HEYZO
* American adult DVDs
* Data18.com

Adult dvds informations are searched on:
* The Movie Database (TMDb)
* Excalibur Films

As no one site has a complete set of English metadata, the program amalgamates metadeta info from a variety of sources, including dmm.co.jp, javlibrary.com, javbus.com, squareplus.co.jp, and actionjav.com.
The data is then fed through a machine translation (if original data is in Japanese) and then quality checked to sanitize it and poster elements are cropped so only the cover is shown.


This program is in alpha. Please submit bugs and feature requests here on github on the [issues page](https://github.com/Wizell/JAVMovieScraper/issues).

### [Download the latest JAVMovieScraper.jar release here to get started! ](https://github.com/DoctorD1501/JAVMovieScraper/releases)

## Screenshots
Main Window
![alt text](https://github.com/Wizell/JAVMovieScraper/blob/master/docs/images/JAVMovieScraperScreenShot1%20-%20Main%20Window.jpg "Main Window")

Renamer Window
![alt text](https://github.com/Wizell/JAVMovieScraper/blob/master/docs/images/JAVMovieScraperScreenShot1%20-%20Renamer%20Window.jpg "Renamer")

## Usage

1. Make sure you have the Java JRE installed. You will need at least Java version 8. Java can be downloaded here: https://www.java.com/en/download/index.jsp
2. Download the newest development build JAR from github releases releases from the [release page](https://github.com/Wizell/JAVMovieScraper/releases).
3. Double click the jar file - or to run from program from the commandline, see the section below. Initially, the program will load your home directory in the file pane on the left. Click the "Browse" button below this file list and point it to the directory where your movie file you wish to scrape is.
4. Select the movie file or folder the movie is in (if the folder is named the same as the movie) in the list of files. You can select multiple files by holding the control or shift keys to do batch scraping. Your movie file MUST have the JAV ID as the last word within the filename, not including stacked file indicators such as DISC1 or CD1. The JAV ID (or Caribbeancom Release ID) can be optionally surrounded by brackets or parenthesis and can contain a dash before the numerical part. Examples of OK file names for JAV DVD Movies: My Movie - ABC-123, My Movie - [ABC123] CD1, ABC-123, (ABC-123), For American movies, the filename must be the name of the movie, optionally followed by the year in parenthesis e.g. MovieName (2014). For web releases, a google search is done on the entire file name, so it's more flexible, but it works best if you include the name of the episode and at least one of the actors in your file name. See the section below for more file naming conventions for the site specific scraper.
5. Using either the Scrape menu or the scrape button, select the appropriate amalgamated or specific scraper for the type of content you are scraping. Note that for American web content (or split scenes from DVDs), you should pick "Data18 WebContent". Regular American DVDs should use the Amalgamated American Adult DVD scraper or one of the specific scrapers. If you get the wrong result when scraping, try enabling the "Select Search Results Manually When Scraping" option, or as a last resort, the "Provide the URL yourself when scraping" option.
6. After a little while, the metadata for the movie will appear in the editor pane. You can select one of the several titles found using the drop down list, or edit the entry by typing in your own text and then hitting enter. You can right click genres or actors to get a menu to add, edit, or delete items.
7. When you are happy with the way the metadata looks, click the "Write File Data" button to create the poster,fanart and nfo files for your movie. Note that for now, not all metadata downloaded is shown in the editor, but this data IS written to the nfo file. Any renaming settings will take effect during this process.
8. If your file wasn't already in its own directory, you can click the "Move File to New Folder" button to move the nfo, poster, movie files, fanart, .actor files, and trailer to a new folder.
9. It's worth checking out the preferences menu to customize what info gets written and how it is named.


## Command Line Options
This program now supports command line options. Starting the program without any command line option will load the graphical user interface version of the program. I'm still actively working on the command line options to make sure all scrapers are accounted for and any settings.xml values are taken into account.
<p>
Usage:
<br>
<b> -filenamecleanup &#60;FilePath&#62; </b>   Use given file argument(s) for file name cleanup process which will rename the file by expanding abbreviations and removing words which cause google scrapes to fail
<br>
<b> -help</b>                               display list of command line options
<br>
<b> -rename &#60;FilePath&#62; </b> renames the file argument(s) and any associated metadata files if the file argument has a valid movie nfo using the file name format from settings.xml
<br>
<b> -scrape &#60;ScraperName FilePath&#62;</b> Scrapes and writes metadata of the file located at &#60;FilePath&#62; with type of scraper specified by &#60;ScraperName&#62;. Valid ScraperNames are: data18webcontent, data18, excaliburfilms, themoviedatabase, iafd, dmm, 1000giri, 1pondo, aventertainment, caribbeancom, caribbeancompremium, heyzo, kin8tengoku, mytokyohot, tokyohot, javbus. Any settings.xml file preference values will be taken into account when scraping.
</p>
<p>
Example command to run filenamecleanup on two different files:
<br>
<b>java -jar JAVMovieScraper.jar -filenamecleanup "C:\myfile1.mp4" "C:\myfile2.mp4"</b>
<br>
<br>
Example command to scrape and write metadata info of a file located at "C:\myfile1.mp4" with the data18webcontent scraper:
<br>
<b>java -jar JAVMovieScraper.jar -scrape data18webcontent "C:\myfile1.mp4"</b>
<br>
<br>
Example command to rename a file "C:\myfile1.mp4" which also has a "C:\myfile1.nfo" in the same directory:
<br>
<b>java -jar JAVMovieScraper.jar -rename "C:\myfile1.mp4"</b>
<br>
<br>
Example command to rename a directory located at "C:\Movie (2014)" which has a nfo file contained within the directory called "C:\Movie (2014)\Movie (2014).nfo"
<br>
<b>java -jar JAVMovieScraper.jar -rename "C:\Movie (2014)"</b>
<br>
<br>
If you're having trouble getting matches with -scrape data18webcontent, try to first run -filenamecleanup on the file and then run -scrape on the file.
</p>

## Site Specific File Name Conventions
When using the site specific scraper feature, your file name must contain an ID number which conforms to the release ID conventions set by that site.
* Aventertainments: This follows the usual JAV id naming structure like ABC-123. It does a search on the site using this ID.<br>
* Kin8tengoku: The ID is in the URL. It is the numeric part before /pht/ and is usually 4 numeric digits. Example: 1147.<br>
* Tokyohot: The ID follows the format of n123 or n1234 k123/k1234. In other words, a lowercase n or k followed by a 3 or 4 digit number.<br>
* 1pondo: The ID is in the URL of the movie, right before /index.html. The first part of the ID is a 6 digit number corresponding to the release date, followed by an underscore, followed by a 3 digit number. Example: 061314_826<br>
* Caribbeancom Premium: The ID is in the URL of the movie, right before /index.html. The first part of the ID is a 6 digit number corresponding to the release date, followed by an underscore, followed by a 3 digit number. Example: 061314_826<br>
* Heyzo: The ID is a 4 digit number in the url right after /moviepages/. Example: 0123<br>
* JavBus: You can search using a censored standard jav ID or try using one of the formats of the uncensored sites above.<br>

## File Name Cleanup Feature
This attempts to rename a file to make it more likely a match will be found with the Data18 Web Content Scraper. This is done by replacing website abbreviations ([current list here - more to be added soon](https://raw.githubusercontent.com/DoctorD1501/JAVMovieScraper/master/JAVMovieScraper/src/moviescraper/doctord/controller/releaserenamer/SiteNameAbbreviations.csv)) at the beginning of the file name with the full site name. It will also remove [words from the file](https://raw.githubusercontent.com/DoctorD1501/JAVMovieScraper/master/JAVMovieScraper/src/moviescraper/doctord/controller/releaserenamer/WordsToRemove.csv) that interfere with scraping and replace underscores and periods in the filename with spaces.
The [list of site name abbreviations](https://raw.githubusercontent.com/DoctorD1501/JAVMovieScraper/master/JAVMovieScraper/src/moviescraper/doctord/controller/releaserenamer/SiteNameAbbreviations.csv) still needs more work. Please consider contributing to this list if you use this feature and would like to see it work better! Note that the list of abbreviations usually contains a short 2-4 letter abbreviation as the second entry in the list. This is the abbreviation used in the scene release of the file.


## What If I Use Plex?
Kodi metadata is compatible with [Plex](https://plex.tv/) using the [XBMCnfoMovieImporter](https://forums.plex.tv/index.php/topic/38402-metadata-agents-for-exported-xbmc-library/) ([github link here](https://github.com/gboudreau/XBMCnfoMoviesImporter.bundle)).

## Other Good Programs for Viewing/Browsing Kodi Scraped Files
Try [Media Companion](https://mediacompanion.codeplex.com/). It's easier to use when sitting at a computer than Kodi because the interface is designed for keyboard & mouse use rather than a remote and large screen.
