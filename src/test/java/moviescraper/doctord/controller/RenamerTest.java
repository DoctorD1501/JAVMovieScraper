package moviescraper.doctord.controller;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile;
import moviescraper.doctord.controller.siteparsingprofile.specific.R18ParsingProfile;
import moviescraper.doctord.model.dataitem.ID;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import org.junit.BeforeClass;
import org.junit.Test;

import moviescraper.doctord.controller.Renamer;
import moviescraper.doctord.model.Movie;

public class RenamerTest {

    static Movie movieAtFile;
    static File movieFilePath;

    static Movie movieAtDirectory;
    static File movieDirectoryPath;

    @BeforeClass
    public static void initialize() throws URISyntaxException, NoSuchFieldException, SecurityException {
        System.out.println("Testing Renamer");
        try {

            File testMovieNfo = new File("src/test/resources/testdata/Movie1.nfo");
            movieFilePath = new File("src/test/resources/testdata/SDDE-502.mp4");

            //1st
            movieAtFile = Movie.createMovieFromNfo(testMovieNfo);
            movieAtFile.setFileName(movieFilePath.getName());
            movieAtFile.setId(new ID("SDDE-502"));
//            movieAtFile.getTitle().setDataItemSource(new R18ParsingProfile());
//            movieAtFile.getActors().get(0).setDataItemSource(new R18ParsingProfile());
//            movieAtFile.getPosters()[0].setDataItemSource(new R18ParsingProfile());
//            movieAtFile.getFanart()[0].setDataItemSource(new R18ParsingProfile());
//            movieAtFile.getPlot().setDataItemSource(new R18ParsingProfile()); //plot will be the one returned from a global sort

            //2nd
            File testDirectoryNfo = new File("src/test/resources/testdata/Movie2.nfo");
            movieDirectoryPath = new File("src/test/resources/testdata/RCT-500/Movie - RCT-500.mp4");

            movieAtDirectory = Movie.createMovieFromNfo(testDirectoryNfo);
            movieAtDirectory.setFileName(movieDirectoryPath.getName());
            movieAtDirectory.setId(new ID("RCT-500"));

//            movieAtDirectory.getTitle().setDataItemSource(new R18ParsingProfile());
//            movieAtDirectory.getActors().get(0).setDataItemSource(new R18ParsingProfile());
//            movieAtDirectory.getPosters()[0].setDataItemSource(new R18ParsingProfile());
//            movieAtDirectory.getFanart()[0].setDataItemSource(new R18ParsingProfile());
//            movieAtDirectory.getPlot().setDataItemSource(new R18ParsingProfile()); //plot will be the one returned from a global sort

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Test
    public void testFileRenameToDirectory() {

        String sanitizerString = MoviescraperPreferences.getSanitizerForFilename();
//        String fileRenameString = MoviescraperPreferences.getRenamerString();
//        String folderRenameString = MoviescraperPreferences.getFolderRenamerString();
        String fileRenameString = "<ID>";
        String folderRenameString = "<BASEDIRECTORY><PATHSEPERATOR><ID> - <TITLE><PATHSEPERATOR>";

        Renamer renamer = new Renamer(fileRenameString, folderRenameString, sanitizerString, movieAtFile, movieFilePath);
        String newMovieFilename = renamer.getNewFileName(movieFilePath.isDirectory());
        System.out.println("Original filename : " + movieFilePath.toString());
        System.out.println("New Filename : " + newMovieFilename);

        assertEquals("NOOOO", "src/test/resources/testdata/SDDE-502 - Movie 1 title/SDDE-502.mp4", newMovieFilename);
    }

}
