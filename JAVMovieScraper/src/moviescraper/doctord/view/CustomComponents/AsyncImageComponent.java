package moviescraper.doctord.view.CustomComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import moviescraper.doctord.model.ImageCache;
import moviescraper.doctord.model.dataitem.Thumb;

public class AsyncImageComponent extends JPanel implements ImageConsumer, MouseListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage resizedImage;
    URL url;
    Thumb thumb;
    private int preferredX = 135;
    private int preferredY = 135;
    private boolean doAutoSelect;
    private boolean doneLoading; 
    private boolean selected;
    private static final Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE, 3);
    private static final Border deselectedBorder = BorderFactory.createEmptyBorder();
    private AsyncImageComponent[] siblings; //other items we may need to disable when we are clicked on. note this array contains self, so just be aware of that and avoid infinite loops and such
	private boolean userMadeSelection = false;
	private boolean isPosterCandidate = true; //height >= width, making this image suitable for a poster image
	private boolean autoSelectFavorsHeight;
	private boolean showPreviewImage;
	private boolean useBorder;
	
	/**
	 * 
	 * @param thumb - image to show
	 * @param showPreviewImage - use the thumb's preview image, if possible, instead of the full resolution image
	 * @param siblings - any siblings of this image. used for selecting things in a list - siblings become unselected
	 * @param doAutoSelect - automatically select an image when all are done loading, the user hasn't clicked anything
	 * @param autoSelectFavorsHeight - We prefer images taller than their width when doing autoselection. used for posters. if false that tends to be used for fanarts
	 */
    public AsyncImageComponent(Thumb thumb, boolean showPreviewImage, AsyncImageComponent[] siblings, boolean doAutoSelect, boolean autoSelectFavorsHeight, boolean useBorder){
		
    	this.setPreferredSize(new Dimension(preferredX,preferredY));
    	selected = false;
    	this.siblings = siblings;
    	this.thumb = thumb;
    	this.useBorder = useBorder;
    	this.showPreviewImage = showPreviewImage;
    	this.autoSelectFavorsHeight = autoSelectFavorsHeight;
    	this.doAutoSelect = doAutoSelect;
    	super.addMouseListener(this);
    	
    	setURLFromThumb();
        new ImageLoader(this, url).execute();
    }
    
    public void setIcon(Thumb thumb, Dimension newSize)
    {
    	//this.resizedImage = null;
    	this.thumb = thumb;
    	this.setPreferredSize(newSize);
    	setURLFromThumb();
    	new ImageLoader(this, url).execute();
    }
    
    public void setIcon(Thumb thumb)
    {
    	//this.resizedImage = null;
    	this.thumb = thumb;
    	setURLFromThumb();
    	new ImageLoader(this, url).execute();
    }
    
    
    public void setIcon(BufferedImage image)
    {
    	//this.resizedImage = null;
    	this.thumb = null;
    	this.img = image;
    	this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    	setURLFromThumb();
    	repaint();
    }
    
    public void setIcon(BufferedImage image, Dimension preferredSize)
    {
    	//this.resizedImage = null;
    	this.thumb = null;
    	this.img = image;
    	this.setPreferredSize(preferredSize);
    	setURLFromThumb();
    	repaint();
    }
    
    
    private void setURLFromThumb()
    {
    	if(thumb != null)
    	{
    		if(thumb.getPreviewURL() != null && showPreviewImage)
    			this.url = thumb.getPreviewURL();
    		else this.url = thumb.getThumbURL();
    	}
    	else
    	{
    		this.url = null;
    	}
    }

    @Override
	public void imageLoaded(BufferedImage img) {
    	this.img = img;
    	if(img != null)
    	{
    		isPosterCandidate = (img.getHeight() >= img.getWidth());
    		Dimension newImageSize = calculateDimensionFit(img.getWidth(), img.getHeight(), getSize().width, getSize().height);
    		this.resizedImage = Scalr.resize(img, Method.QUALITY, Scalr.Mode.AUTOMATIC, newImageSize.width, newImageSize.height, Scalr.OP_ANTIALIAS);
    		
    	}
    	doneLoading = true;
        repaint();
        handleAutoSelection();
    }
    
	/**
	 * Calculate the max size we can resize an image while fitting within maxWidth and maxHeight
	 * and still maintaining the aspect ratio
	 * @param imageWidth - the width of the image to resize
	 * @param imageHeight - the height of the image to resize
	 * @param maxWidth - the maximum width the image can be
	 * @param maxHeight - the maximum height the image can be
	 * @return A Dimension object with the calculated width and heights set on it
	 */
	private static Dimension calculateDimensionFit(int imageWidth, int imageHeight, 
			int maxWidth, int maxHeight)
	{
		double aspectRatio = Math.min((double) maxWidth / (double) imageWidth,
				(double) maxHeight / (double) imageHeight);
		return new Dimension((int)(imageWidth * aspectRatio), (int)(imageHeight * aspectRatio));
	}
    
    @Override
    public void setPreferredSize(Dimension newPreferredSize)
    {
    	super.setPreferredSize(newPreferredSize);
    	revalidate();
    	if(img != null)
    	{
    		Dimension newImageSize = calculateDimensionFit(img.getWidth(), img.getHeight(), getSize().width, getSize().height);
    		this.resizedImage = Scalr.resize(img, Method.QUALITY, Scalr.Mode.AUTOMATIC, newImageSize.width, newImageSize.height, Scalr.OP_ANTIALIAS);
    	}
    	repaint();
    }

    /**
     * Used to automatically select the first poster sized or fanart sized image in the sibling list
     */
    private void handleAutoSelection() {
		if(siblings != null && doAutoSelect && didMyselfAndAllSiblingsFinishLoading() && !didUserMakeSelectionOnMyselfOrAnySiblings())
		{
			int itemToSelect = 0;
			for(int i = 0; i < siblings.length; i++)
			{
				AsyncImageComponent currentImage = siblings[i];
				if(autoSelectFavorsHeight && currentImage.isPosterCandidate)
				{
					itemToSelect = i;
					break;
				}
				else if(!autoSelectFavorsHeight && !currentImage.isPosterCandidate)
				{
					itemToSelect = i;
					break;
				}
			}
			siblings[itemToSelect].selectSelf();
		}
	}

	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //Image as async loaded - we can draw it
        if(resizedImage != null)
        {
        	g.drawImage(resizedImage, 0, 0, this);
        }
        else if (img != null) {
            g.drawImage(img, 0, 0, this);
        }
        //draw a place holder if image still loading
        else
        {
        	if(useBorder)
        		g.drawRect(0, 0, getWidth(), getHeight());
        }
    }

    protected class ImageLoader extends SwingWorker<BufferedImage, BufferedImage> {

        private ImageConsumer consumer;
        URL url;
        BufferedImage pictureLoaded;

        public ImageLoader(ImageConsumer consumer, URL url) {
        	this.url = url;
            this.consumer = consumer;
        }

        @Override
        protected BufferedImage doInBackground() throws IOException {

        	if(ImageCache.isImageCached(url))
        	{
        		pictureLoaded = Thumb.convertToBufferedImage(ImageCache.getImageFromCache(url));
        	}
        	else
        	{
        		pictureLoaded = ImageIO.read(url);
        		ImageCache.putImageInCache(url, pictureLoaded);
        	}
            return pictureLoaded;

        }

        @Override
		protected void done() {
            try {
                if(pictureLoaded != null)
                	consumer.imageLoaded(pictureLoaded);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }           
    }



	private void toggleSelected() {
		if(selected)
		{
			deselectSelf();
		}
		else if(!selected)
		{
			selectSelf();
		}
	}
	
	private void selectSelf()
	{
		deselectSiblings();
		selected = true;
		if(useBorder)
			super.setBorder(selectedBorder);
	}
	
	private void deselectSelf()
	{
		selected = false;
		if(useBorder)
			super.setBorder(deselectedBorder);
	}

	private void deselectSiblings() {
		if(siblings != null && siblings.length > 1)
		{
			for(int i = 0; i < siblings.length; i++)
			{
				if(siblings[i] != this)
				{
					siblings[i].deselectSelf();
				}
			}
		}
		
	}
	
	public boolean isSelected() {
		return selected;
	}

	public Thumb getThumb() {
		return thumb;
	}
	
	public boolean didUserMakeSelection()
	{
		return userMadeSelection ;
	}
	
	public boolean didMyselfAndAllSiblingsFinishLoading()
	{
		if(siblings != null)
		{
			for(int i = 0; i < siblings.length; i++)
			{
				if(!siblings[i].doneLoading)
					return false;
			}
			return true;
		}
		return true;
	}
	
	public boolean didUserMakeSelectionOnMyselfOrAnySiblings()
	{
		if(siblings != null)
		{
			for(int i = 0; i < siblings.length; i++)
			{
				if(siblings[i].didUserMakeSelection())
					return true;
			}
			return false;
		}
		return true;
	}
	
	/*MouseListener methods*/
	@Override
	public void mouseClicked(MouseEvent e) {
		userMadeSelection = true;
		toggleSelected();
		
	}
	@Override public void mouseEntered(MouseEvent e){}
	@Override public void mouseExited(MouseEvent e){}
	@Override public void mousePressed(MouseEvent e){}
	@Override public void mouseReleased(MouseEvent e){}

	public void clear() {
		this.thumb = null;
		this.img = null;
		this.resizedImage = null;
		repaint();
		
	}


	
	
}
