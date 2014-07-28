package moviescraper.doctord;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Thumb {
	private URL thumbURL;
	Image thumbImage;
	ImageIcon imageIconThumbImage;
	public ImageIcon getImageIconThumbImage() {
		try {
			getThumbImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageIconThumbImage;
	}

	//Did the image change from the original image from url (this matters when knowing whether we need to reencode when saving it back to disk)
	private boolean isImageModified;
	private boolean needToReloadThumbImage = false;


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

		thumbURL = new URL(url);
		//get our image from the cachce, if it exists. otherwise, download it from the URL and put in the cache
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

	public Thumb (String url) throws IOException
	{
		if(url.length() > 1)
			thumbURL = new URL(url);
		else
			thumbURL = null;
		//Delay the call to actually reading in the thumbImage until it is needed
		isImageModified = false;
		needToReloadThumbImage = true;
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

}
