package moviescraper.doctord.model.dataitem;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import moviescraper.doctord.model.ImageCache;

import org.apache.commons.io.FileUtils;

public class Thumb extends MovieDataItem {
	private URL thumbURL;
	private URL previewURL; //smaller version of the image used in GUI pickers
	
	//use soft references here to hold onto our memory of a loaded up image for as long as possible and only GC it when we have no choice
	//note that the strong reference will be in the image cache. the image cache has logic in place to purge items if it gets too full
	private SoftReference<? extends Image> thumbImage;
	private SoftReference<? extends Image> previewThumbImage;
	private SoftReference<? extends ImageIcon> imageIconThumbImage;
	private SoftReference<? extends ImageIcon> previewIconThumbImage;
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
		if(thumbURL == null && imageIconThumbImage.get() != null)
		{
			return imageIconThumbImage.get();
		}
		try {
			getThumbImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageIconThumbImage.get();
	}
	
	public ImageIcon getPreviewImageIconThumbImage(){
		if(previewURL == null && previewIconThumbImage.get() != null)
			return previewIconThumbImage.get();
		try{
			getPreviewImage();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return previewIconThumbImage.get();
	}




	public void setThumbURL(URL thumbURL) {
		this.thumbURL = thumbURL;
		needToReloadThumbImage = true;
	}

	public void setThumbImage(Image thumbImage) {
		this.thumbImage = new SoftReference<>(thumbImage);
		this.imageIconThumbImage = new SoftReference<>(new ImageIcon(this.thumbImage.get()));
		needToReloadThumbImage = false;
	}

	public Thumb (URL thumbURL)
	{
		//Delay the call to actually reading in the thumbImage until it is needed
		this.thumbURL = thumbURL;
		isImageModified = false;
		needToReloadThumbImage = true;
	}

	//call this with whole numbers for percents; must be smaller than 100 and greater than 0
	//Don't use this anymore - it will mess up the new soft references
	@Deprecated
	public Thumb (String url, double horizontalPercentLeft, double horizontalPercentRight, double verticalPercentTop, double verticalPercentBottom) throws IOException
	{
		//System.out.println("old crop method being called");
		thumbURL = new URL(url);
		isImageModified = true;
		//get our image from the cache, if it exists. otherwise, download it from the URL and put in the cache
		BufferedImage tempImage = (BufferedImage)ImageCache.getImageFromCache(thumbURL, isImageModified);
		int newXLeft = (int) (0 + (tempImage.getWidth()*(horizontalPercentLeft/100))); //left x bound of rectangle
		int newXRight = (int) (tempImage.getWidth() - (tempImage.getWidth()*(horizontalPercentRight/100)));// right x bound of rectangle
		int newYTop = (int) (0 + (tempImage.getHeight()*(verticalPercentTop/100))); //top y bound of rectangle
		int newYBottom = (int) (tempImage.getHeight() - (tempImage.getHeight()*(verticalPercentBottom/100))); //bottom y bound of rectangle
		tempImage = tempImage.getSubimage(newXLeft, newYTop, newXRight - newXLeft, newYBottom - newYTop);
		thumbImage = new SoftReference<Image>(tempImage);
		imageIconThumbImage = new SoftReference<>(new ImageIcon(thumbImage.get()));
		needToReloadThumbImage = false;
	}
	
	public Thumb(String url, boolean useJavCoverCropRoutine) throws IOException
	{
		
		thumbURL = new URL(url);
		BufferedImage tempImage = (BufferedImage)ImageCache.getImageFromCache(thumbURL, false); //get the unmodified, uncropped image
		//just get the jpg from the url
		String filename = fileNameFromURL(url);
		//routine adapted from pythoncovercrop.py
		if(useJavCoverCropRoutine) {
			tempImage = doJavCoverCropRoutine(tempImage, filename);
			this.isImageModified = true;
			ImageCache.putImageInCache(thumbURL, tempImage, true); //cache cropped image so we don't need to do this again
		}
		else {
			this.isImageModified = false;
		}
		thumbImage = new SoftReference<>(tempImage);
		imageIconThumbImage = new SoftReference<>(new ImageIcon(tempImage));
		needToReloadThumbImage = false;
	}
	
	/**
	 * Utility function to get the last part of a URL formatted string (the filename) and return it. Usually used in conjunction with {@link Thumb.doJavCoverCropRoutine}
	 * @param url
	 * @return
	 */
	public static String fileNameFromURL(String url) {
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}
	
	/**
	 * Crops a JAV DVD jacket image so that only the cover is returned. This usually means the left half of the jacket image is cropped out.
	 * @param originalImage - Image you wish to crop
	 * @param filename - filename of the image. If you have a URL, you can get this from {@link Thumb.fileNameFromURL} 
	 * @return A new BufferedImage object with the back part of the jacket cover cropped out
	 */
	public static BufferedImage doJavCoverCropRoutine(BufferedImage originalImage, String filename) {
			
			BufferedImage tempImage;
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			int croppedWidth = (int) ( width / 2.11);
			
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
			if(height == 513 && width == 800)
			{
				croppedWidth = croppedWidth -14;
			}
			
			//now crop the image

			//handling some weird inverted covers
			if(filename.contains("DNPD"))
			{
				tempImage = originalImage.getSubimage(0,0,croppedWidth,height);
			}
			else
				tempImage = originalImage.getSubimage(width-croppedWidth,0,croppedWidth,height);
			
		return tempImage;
	}
	
	/**
	 * Thumb constructor which joins the leftImage to the rightImage in one new thumb
	 */
	public Thumb (String leftImage, String rightImage) throws IOException {
		setThumbURL(new URL(leftImage));
		this.isImageModified = true;
		BufferedImage leftBufferedImage = (BufferedImage)ImageCache.getImageFromCache(new URL(leftImage), isImageModified);
		BufferedImage rightBufferedImage = (BufferedImage)ImageCache.getImageFromCache(new URL(rightImage), isImageModified);
		BufferedImage joinedImage = joinBufferedImage(leftBufferedImage, rightBufferedImage);
		setImage(joinedImage);

	}
	
	private static BufferedImage joinBufferedImage(BufferedImage img1,
			BufferedImage img2) {

		int wid = img1.getWidth() + img2.getWidth();
		int height = Math.max(img1.getHeight(), img2.getHeight());
		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height,
				BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		// fill background
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, wid, height);
		// draw image
		g2.setColor(oldColor);
		g2.drawImage(img1, null, 0, 0);
		g2.drawImage(img2, null, img1.getWidth(), 0);
		g2.dispose();
		return newImage;
	}

	public Thumb (String url) throws MalformedURLException 
	{
		if(url != null && url.length() > 1)
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
		thumbURL = file.toURI().toURL();
	}

	public URL getThumbURL() {
		return thumbURL;
	}

	//change the thumb's image and URL at the same time
	public void setImage(URL thumbURL) {
		this.thumbURL = thumbURL;
		needToReloadThumbImage = false;
	}

	public void setImage(Image thumbImage){
		this.thumbImage = new SoftReference<>(thumbImage);
		this.imageIconThumbImage = new SoftReference<>(new ImageIcon(thumbImage));
		needToReloadThumbImage = false;
	}

	public Image getThumbImage() throws IOException {
		//if the cached image is old or it hadn't been loaded yet, load 'er up!
		if(thumbURL == null)
		{
			needToReloadThumbImage = false;
			return thumbImage.get();
		}
		if((needToReloadThumbImage) || (thumbImage == null) || thumbImage.get() == null)
		{
			//rather than downloading the image every time, we can instead see if it's already in the cache
			//if it's not in the cache, then we will actually download the image
			thumbImage = new SoftReference<>(ImageCache.getImageFromCache(thumbURL, isImageModified));
			imageIconThumbImage = new SoftReference<>(new ImageIcon(thumbImage.get()));

			needToReloadThumbImage = false;
		}
		return thumbImage.get();
	}
	
	/**
	 * 
	 * @return true if this thumb already exist in the cache and doesn't need to be downloaded again, false otherwise
	 */
	public boolean isCached()
	{
		return ImageCache.isImageCached(thumbURL, isImageModified);
	}
	
	public Image getPreviewImage() throws IOException
	{
		if(previewURL == null)
		{
			needToReloadPreviewImage = false;
			return previewThumbImage.get();
		}
		if(needToReloadPreviewImage || previewThumbImage == null || previewThumbImage.get() == null)
		{
			previewThumbImage = new SoftReference<>(ImageCache.getImageFromCache(previewURL, isImageModified));
			previewIconThumbImage = new SoftReference<>(new ImageIcon(previewThumbImage.get()));
			needToReloadPreviewImage = false;
		}
		return previewThumbImage.get();
	}

	@Override
	public String toXML()
	{
		return "<thumb>"+thumbURL.getPath()+"</thumb>";
	}

	@Override
	public String toString() {
		return "Thumb [thumbURL=" + thumbURL + "\"" + dataItemSourceToString() + "]";
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((previewURL == null) ? 0 : previewURL.hashCode());
		result = prime * result
				+ ((thumbURL == null) ? 0 : thumbURL.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Thumb other = (Thumb) obj;
		if (previewURL == null) {
			if (other.previewURL != null)
				return false;
		} else if (!previewURL.equals(other.previewURL))
			return false;
		if (thumbURL == null) {
			if (other.thumbURL != null)
				return false;
		} else if (!thumbURL.equals(other.thumbURL))
			return false;
		return true;
	}

	public void setIsModified(boolean value)
	{
		this.isImageModified = value;
	}
	
	public BufferedImage toBufferedImage() throws IOException
	{
		//in case our reference has gone cold, reget it from the cache/internet
		if (thumbImage.get() == null) {
			getThumbImage();
		}
	    if (thumbImage.get() instanceof BufferedImage)
	    {
	        return (BufferedImage) thumbImage.get();
	    }

	    // Create a buffered image
	    BufferedImage bimage = new BufferedImage(thumbImage.get().getWidth(null), thumbImage.get().getHeight(null), BufferedImage.TYPE_INT_RGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(thumbImage.get(), 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	/**
	 * Utility method to convert a Image type object to a BufferedImage type object
	 * @param image - the image to convert
	 * @return the same image, but as a BufferedImage
	 */
	public static BufferedImage convertToBufferedImage(Image image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}

}
