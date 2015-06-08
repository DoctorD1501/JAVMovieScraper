package moviescraper.doctord.dataitem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The full date on which a movie is released.
 * 
 * The correct format for this variable is YYYY-MM-DD Where YYYY is the year, MM
 * is the month, and DD is the day.
 */
public class ReleaseDate extends MovieDataItem {

	public static final ReleaseDate BLANK_RELEASEDATE = new ReleaseDate("");

	private String releaseDate;
	
	/**
	 * 
	 * @param releaseDate - the releaseDate must be a string in the YYYY-MM-DD format (include the separators when using this constructor)
	 */
	public ReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public ReleaseDate(String releaseDate, SimpleDateFormat format) {
		if (releaseDate.length() > 0) {
			try {
				Calendar gregorianCalendar = new GregorianCalendar();
				Date dateValue;
				dateValue = format.parse(releaseDate);
				gregorianCalendar.setTime(dateValue);
				this.releaseDate = String.valueOf(gregorianCalendar
						.get(Calendar.YEAR))
						+ "-"
						+ String.format("%02d",gregorianCalendar.get(Calendar.MONTH) + 1)
						+ "-"
						+ String.format("%02d",gregorianCalendar.get(Calendar.DATE));
			} catch (ParseException e) {
				e.printStackTrace();
				this.releaseDate = "";
			}

		}
	}
	
	/**
	 * 
	 * @param year - passed in as a 4 digit string representing the year number (e.g. 2015)
	 * @param month - passed in as a 2 digit string representing the month number (e.g. 12)
	 * @param day - passed in as a 2 digit string represneting the day number (e.g. 25)
	 */
	public ReleaseDate(String year, String month, String day)
	{
		this.releaseDate = "year" + "-" + month + "-" + day;
	}
	

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReleaseDate() {
		return releaseDate;
	}
	
	@Override
	public String toString() {
		return "ReleaseDate [releasedate=\"" + releaseDate + "\"" + dataItemSourceToString() + "]";
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = sanitizeString(releaseDate);
	}
	
	public boolean isReleaseDateFormattedCorrectly()
	{
		if(releaseDate != null && releaseDate.matches("\\d{4}-\\d{2}-\\d{2}"))
			return true;
		else return false;
	}
	
	public Year getYear()
	{
		if(isReleaseDateFormattedCorrectly())
			return new Year(releaseDate.substring(0,4));
		else return Year.BLANK_YEAR;
	}
	
	public String getMonth(){
		if(isReleaseDateFormattedCorrectly())
			return releaseDate.substring(5,7);
		else return "";
	}
	
	public String getDay()
	{
		if(isReleaseDateFormattedCorrectly())
			return releaseDate.substring(8,10);
		else return "";
	}
	
	public static void main (String [] args)
	{
		ReleaseDate christmasDay = new ReleaseDate("2015-12-25");
		System.out.println("good = " + christmasDay);
		System.out.println("isvalid on good = " + christmasDay.isReleaseDateFormattedCorrectly());
		System.out.println("Month = " +  christmasDay.getMonth() + " Year =" + christmasDay.getYear() + " Day = " + christmasDay.getDay());
		System.out.println("DateFormatter version");
		ReleaseDate newYear = new ReleaseDate("March 4, 2015", new SimpleDateFormat("MMMM dd, yyyy"));
		System.out.println(newYear);
	}
	

}
