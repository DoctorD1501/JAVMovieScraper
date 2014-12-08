package moviescraper.doctord;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import moviescraper.doctord.GUI.GUIMain;
import moviescraper.doctord.ReleaseRenamer.WebReleaseRenamer;

public class Main {

	public static void main(String[] args) {
		if(args == null || args.length == 0)
		{
			//Start the GUI version of the program
			GUIMain.main(args);
		}
		else
		{
			//set up the options
			Options options = new Options();
			
			//set up -help option
			options.addOption("help", false, "display this message");
			
			//set up -filenamecleanup option
			@SuppressWarnings("static-access") //Needed until apache commons cli v1.3 which fixes this design flaw
			
			Option filenamecleanup = OptionBuilder.withArgName( "file" )
                    .hasArgs(Option.UNLIMITED_VALUES)
                    .withDescription(  "Use given file argument(s) for file name cleanup process which will rename the file by expanding abbreviations and removing words which cause google scrapes to fail" )
                    .create( "filenamecleanup" );

			options.addOption(filenamecleanup);
			
			CommandLineParser parser = new BasicParser();
			try {
				CommandLine line = parser.parse(options, args);
				
				//-help
				if( line.hasOption( "help" )) {
					printHelpMessage(options);
				}
				//-filenamecleanup
				else if(line.hasOption("filenamecleanup"))
				{
					runFileNameCleanup(line.getOptionValues("filenamecleanup"));
				}
					
				
			} catch (ParseException exp) {
				System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );

			}
			
		}

	}
	
	private static void printHelpMessage(Options options)
	{
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "JAVMovieScraper", options );
	}
	
	private static void runFileNameCleanup(String [] optionValues)
	{
		for(String fileName : optionValues)
		{
			File currentFile = new File(fileName);
			if(!currentFile.exists())
			{
				System.err.println(currentFile + " does not exist.");
			}
			else
			{
				try {
					WebReleaseRenamer renamer = new WebReleaseRenamer();
					File newFile = renamer.newFileName(currentFile);
					boolean renameStatus = currentFile.renameTo(newFile);
					if(renameStatus != true)
						System.err.println("Rename failed! Perhaps a file name already exists with that name?");
					else
						System.out.println("Renamed " + currentFile + " to " + newFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
