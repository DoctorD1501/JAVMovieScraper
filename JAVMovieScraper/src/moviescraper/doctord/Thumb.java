package moviescraper.doctord;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;

public class Thumb {
	private URL thumbURL;
	private URL previewURL; //smaller version of the image used in GUI pickers
	Image thumbImage;
	Image previewThumbImage;
	ImageIcon imageIconThumbImage;
	ImageIcon previewIconThumbImage;
	private String thumbLabel;
	private boolean loadedFromDisk;
	protected final static int connectionTimeout = 10000; //10 seconds
	protected final static int  readTimeout = 10000; //10 seconds
	//Did the image change from the original image from url (this matters when knowing whether we need to reencode when saving it back to disk)
	private boolean isImageModified;
	private boolean needToReloadThumbImage = false;
	private boolean needToReloadPreviewImage = false;
	
	public String getThumbLabel() {
		return thumbLabel;
	}

	public void setThumbLabel(String thumbLabel) {
		this.thumbLabel = thumbLabel;
	}

	public ImageIcon getImageIconThumbImage() {
		try {
			getThumbImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageIconThumbImage;
	}
	
	public ImageIcon getPreviewImageIconThumbImage(){
		try{
			getPreviewImage();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return previewIconThumbImage;
	}




	public void setThumbURL(URL thumbURL) {
		this.thumbURL = thumbURL;
		needToReloadThumbImage = true;
	}

	public void setThumbImage(Image thumbImage) {
		this.thumbImage = thumbImage;
		this.imageIconThumbImage = new ImageIcon(this.thumbImage);
		needToReloadThumbImage = false;
	}

	public Thumb (URL thumbURL) throws IOException
	{
		//Delay the call to actually reading in the thumbImage until it is needed
		this.thumbURL = thumbURL;
		isImageModified = false;
		needToReloadThumbImage = true;
	}

	//call this with whole numbers for percents; must be smaller than 100 and greater than 0
	public Thumb (String url, double horizontalPercentLeft, double horizontalPercentRight, double verticalPercentTop, double verticalPercentBottom) throws IOException
	{
		System.out.println("old crop method being called");
		thumbURL = new URL(url);
		//get our image from the cache, if it exists. otherwise, download it from the URL and put in the cache
		BufferedImage tempImage = (BufferedImage)ImageCache.getImageFromCache(thumbURL);
		int newXLeft = (int) (0 + (tempImage.getWidth()*(horizontalPercentLeft/100))); //left x bound of rectangle
		int newXRight = (int) (tempImage.getWidth() - (tempImage.getWidth()*(horizontalPercentRight/100)));// right x bound of rectangle
		int newYTop = (int) (0 + (tempImage.getHeight()*(verticalPercentTop/100))); //top y bound of rectangle
		int newYBottom = (int) (tempImage.getHeight() - (tempImage.getHeight()*(verticalPercentBottom/100))); //bottom y bound of rectangle
		tempImage = tempImage.getSubimage(newXLeft, newYTop, newXRight - newXLeft, newYBottom - newYTop);
		thumbImage = tempImage;
		imageIconThumbImage = new ImageIcon(thumbImage);
		isImageModified = true;
		needToReloadThumbImage = false;
	}
	
	
	public Thumb(String url, boolean useJavCoverCropRoutine) throws IOException
	{
		
		thumbURL = new URL(url);
		BufferedImage tempImage = (BufferedImage)ImageCache.getImageFromCache(thumbURL);
		//routine adapted from pythoncovercrop.py
		if(useJavCoverCropRoutine)
		{
			int width = tempImage.getWidth();
			int height = tempImage.getHeight();
			int croppedWidth = (int) ( width / 2.11);
			
			//just get the jpg from the url
			String filename = url.substring(url.lastIndexOf("/") + 1, url.length());
			//Presets

			//SOD (SDMS, SDDE) - crop 3 pixels
			if(filename.contains("SDDE") || filename.contains("SDMS"))
				croppedWidth = croppedWidth - 3;
			//Natura High - crop 2 pixels
			if(filename.contains("NHDT"))
				croppedWidth = croppedWidth - 2;
			//HTY - crop 1 pixel
			if(filename.contains("HTV"))
				croppedWidth = croppedWidth - 1;
			//Prestige (EVO, DAY, ZER, EZD, DOM) crop 1 pixel
			if(filename.contains("EVO") || filename.contains("DAY") || filename.contains("ZER") || filename.contains("EZD") || filename.contains("DOM") && height == 522)
				croppedWidth = croppedWidth - 1;
			//DOM - overcrop a little
			if(filename.contains("DOM") && height == 488)
				croppedWidth = croppedWidth + 13;
			//DIM - crop 5 pixels
			if(filename.contains("DIM"))
				croppedWidth = croppedWidth - 5;
			//DNPD - the front is on the left and a different crop routine will be used below
			//CRZ - crop 5 pixels
			if(filename.contains("CRZ") && height == 541)
				croppedWidth = croppedWidth - 5;
			//FSET - crop 2 pixels
			if(filename.contains("FSET") && height == 675)
				croppedWidth = croppedWidth - 2;
			//Moodyz (MIRD dual discs - the original code says to center the overcropping but provides no example so I'm not dooing anything for now)
			//Opera (ORPD) - crop 1 pixel
			if(filename.contains("DIM"))
				croppedWidth = croppedWidth - 1;
			//Jade (P9) - crop 2 pixels
			if(filename.contains("P9"))
				croppedWidth = croppedWidth - 2;
			//Rocket (RCT) - Crop 2 Pixels
			if(filename.contains("RCT"))
				croppedWidth = croppedWidth - 2;
			//SIMG - crop 10 pixels
			if(filename.contains("SIMG") && height == 864)
				croppedWidth = croppedWidth - 10;
			//SIMG - crop 4 pixels
			if(filename.contains("SIMG") && height == 541)
				croppedWidth = croppedWidth - 4;
			//SVDVD - crop 2 pixels
			if(filename.contains("SVDVD") && height == 950)
				croppedWidth = croppedWidth - 4;
			//XV-65 - crop 6 pixels
			if(filename.contains("XV-65") && height == 750)
				croppedWidth = croppedWidth - 6;
			//800x538 - crop 2 pixels
			if(height == 538 && width == 800)
				croppedWidth = croppedWidth - 2;
			//800x537 - crop 1 pixel
			if(height == 537 && width == 800)
				croppedWidth = croppedWidth - 1;
			
			//now crop the image

			//handling some weird inverted covers
			if(filename.contains("DNPD"))
			{
				tempImage = tempImage.getSubimage(0,0,croppedWidth,height);
			}
			else
				tempImage = tempImage.getSubimage(width-croppedWidth,0,croppedWidth,height);
			this.isImageModified = true;
		}
		if(!useJavCoverCropRoutine)
			this.isImageModified = false;
		thumbImage = tempImage;
		imageIconThumbImage = new ImageIcon(tempImage);
		needToReloadThumbImage = false;
	}

	public Thumb (String url) throws MalformedURLException 
	{
		if(url.length() > 1)
			thumbURL = new URL(url);
		else
			thumbURL = null;
		//Delay the call to actually reading in the thumbImage until it is needed
		this.isImageModified = false;
		needToReloadThumbImage = true;
	}


	//TODO: Generate an empty thumbnail that points to nowhere
	public Thumb() {
		this.isImageModified = false;
		needToReloadThumbImage = false;
	}
	
	public Thumb(File file, String url) throws IOException
	{
		this.setImage(ImageIO.read(file));
		this.isImageModified = false;
		this.thumbURL = new URL(url);
		loadedFromDisk = true;
	}
	
	public Thumb(File file) throws IOException
	{
		this.setImage(ImageIO.read(file));
		this.isImageModified = false;
		loadedFromDisk = true;
	}

	public URL getThumbURL() {
		return thumbURL;
	}

	//change the thumb's image and URL at the same time
	public void setImage(URL thumbURL) throws IOException {
		this.thumbURL = thumbURL;
		needToReloadThumbImage = false;
	}

	public void setImage(Image thumbImage){
		this.thumbImage = thumbImage;
		this.imageIconThumbImage = new ImageIcon(thumbImage);
		needToReloadThumbImage = false;
	}

	public Image getThumbImage() throws IOException {
		//if the cached image is old or it hadn't been loaded yet, load 'er up!
		if(thumbURL == null)
		{
			needToReloadThumbImage = false;
			return thumbImage;
		}
		if((needToReloadThumbImage) || (thumbImage == null))
		{
			//rather than downloading the image every time, we can instead see if it's already in the cache
			//if it's not in the cache, then we will actually download the image
			thumbImage = ImageCache.getImageFromCache(thumbURL);
			imageIconThumbImage = new ImageIcon(thumbImage);

			needToReloadThumbImage = false;
		}
		return thumbImage;
	}
	
	public Image getPreviewImage() throws IOException
	{
		if(previewURL == null)
		{
			needToReloadPreviewImage = false;
			return previewThumbImage;
		}
		if(needToReloadPreviewImage || previewThumbImage == null)
		{
			previewThumbImage = ImageCache.getImageFromCache(previewURL);
			previewIconThumbImage = new ImageIcon(previewThumbImage);
			needToReloadPreviewImage = false;
		}
		return previewThumbImage;
	}

	public String toXML()
	{
		return "<thumb>"+thumbURL.getPath()+"</thumb>";
	}

	@Override
	public String toString() {
		return "Thumb [thumbURL=" + thumbURL + "]";
	}

	public boolean isModified(){
		return isImageModified;
	}

	public static boolean fileExistsAtUrl(String URLName){
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con =
					(HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void writeImageToFile(File fileNameToWrite) throws IOException {
			FileUtils.copyURLToFile(thumbURL, fileNameToWrite, connectionTimeout, readTimeout);
	}

	public boolean isLoadedFromDisk() {
		return loadedFromDisk;
	}

	public URL getPreviewURL() {
		return previewURL;
	}

	public void setPreviewURL(URL previewURL) {
		this.previewURL = previewURL;
	}

}
