package moviescraper.doctord.dataitem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Rating extends MovieDataItem {
	
	private double maxRating;
	private String rating;
	//use this rating when trying to create a movie that doesn't write out a rating to the XML
	public static final Rating BLANK_RATING = new Rating(0,"");

	public double getMaxRating() {
		return maxRating;
	}

	public void setMaxRating(double maxRating) {
		this.maxRating = maxRating;
	}

	public String getRating() {
		return rating;
	}
	
	/**
	 * Uses the maxRating and rating score to convert the rating to a score out of 10 with one decimal place
	 * @return
	 */
	public String getRatingOutOfTen(){
			if (this == null || rating.equals("") || maxRating == 0.0)
				return "";
			try{
			double ratingValue = Double.valueOf(rating).doubleValue();
			double ratingOutOfTenValue = 10 * (ratingValue/((double)maxRating));
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			DecimalFormat oneDigit = new DecimalFormat("###0.0", symbols);//format to 1 decimal place
			return oneDigit.format(ratingOutOfTenValue);
			}
			catch (NumberFormatException e)
			{
				//eh, somehow the rating didn't get scraped as a number. no big deal - just don't put in a rating for this element
				return "";
			}
	}

	public void setRating(String rating) {
		this.rating = sanitizeString(rating);
	}

	public Rating(double maxRating, String rating) {
		this.maxRating = maxRating;
		setRating(rating);
	}

	@Override
	public String toString() {
		return "Rating [maxRating=\"" + maxRating + "\", rating=\"" + rating + "\" " + dataItemSourceToString() + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public Rating() {
		super();
		maxRating = 0;
		rating = "";
	}



}
