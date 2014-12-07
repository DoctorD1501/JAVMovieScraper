package moviescraper.doctord.ReleaseRenamer;

import java.io.File;
import org.apache.commons.io.FilenameUtils;

public abstract class ReleaseRenamer {
	
  public abstract String getCleanName(String filename);
  
  public String replaceSeperatorsWithSpaces(String name)
  {
	  return name.replaceAll("[\\._]", " ");
  }
  
  public File newFileName(File fileToRename)
  {
	  String newBaseName = "";
	  if(fileToRename.isFile())
		  newBaseName = getCleanName(FilenameUtils.getBaseName(fileToRename.toString()));
	  else if(fileToRename.isDirectory())
		  newBaseName = getCleanName(FilenameUtils.getName(fileToRename.toString()));
	  String extension = FilenameUtils.getExtension(fileToRename.toString());
	  if(fileToRename.isDirectory())
	  {
		  return new File(fileToRename.getParent() + File.separator + newBaseName);
	  }
	  else if(fileToRename.isFile())
	  {
		  return new File(fileToRename.getParent() + File.separator + newBaseName + "." + extension);
	  }
	  return fileToRename;
  }
  
  public static void renameToCleanName(File fileToRename, ReleaseRenamer releaseRenamerToUse)
  {
	  File newFileName = releaseRenamerToUse.newFileName(fileToRename);
	  boolean renameStatus = fileToRename.renameTo(newFileName);
	  if(renameStatus != true)
		  System.err.println("Rename failed on file: " + fileToRename.toString());
  }

}
