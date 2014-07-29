package moviescraper.doctord.dataitem;

import java.text.DecimalFormat;




public class Rating extends MovieDataItem {
	
	private double maxRating;
	private String rating;

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
			if (this == null || rating.equals(""))
				return "";
			try{
			double ratingValue = Double.valueOf(rating).doubleValue();
			double ratingOutOfTenValue = 10 * (ratingValue/((double)maxRating));
			DecimalFormat oneDigit = new DecimalFormat("#,##0.0");//format to 1 decimal place
			return Double.valueOf(oneDigit.format(ratingOutOfTenValue)).toString();
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
		return "Rating [maxRating=" + maxRating + ", rating=" + rating + "]";
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
