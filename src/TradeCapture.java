import java.sql.SQLException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * Main class of application. 
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */

public class TradeCapture {

	public static Date CURRENT_DATE;   // system-wide parameter Current date
	public static ArrayList<Date> holidays;  // A list of holiday dates
	
	// Main function
	public static void main(String[] args) throws SQLException, ParseException {
		formatDates();
		// create new ExchangeListener and Display
		(new Thread(new ExchangeListener())).start();
		(new Thread(new Display())).start();
		(new Thread(new EOD())).start();
	}
	

	/**
	 * Add holiday date to arraylist
	 */
	public static void formatDates() throws ParseException {
		CURRENT_DATE = new Date();
		holidays = new ArrayList<Date>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
		holidays.add((Date) sdf.parse("01/01/2015"));
		holidays.add((Date) sdf.parse("19/01/2015"));
		holidays.add((Date) sdf.parse("16/02/2015"));
		holidays.add((Date) sdf.parse("03/04/2015"));
		holidays.add((Date) sdf.parse("25/05/2015"));
		holidays.add((Date) sdf.parse("04/07/2015"));
		holidays.add((Date) sdf.parse("07/09/2015"));
		holidays.add((Date) sdf.parse("26/11/2015"));
		holidays.add((Date) sdf.parse("25/12/2015"));
		holidays.add((Date) sdf.parse("01/01/2016"));
		holidays.add((Date) sdf.parse("18/01/2016"));
		holidays.add((Date) sdf.parse("15/02/2016"));
		holidays.add((Date) sdf.parse("25/03/2016"));
		holidays.add((Date) sdf.parse("30/05/2016"));
		holidays.add((Date) sdf.parse("04/07/2016"));
		holidays.add((Date) sdf.parse("05/09/2016"));
		holidays.add((Date) sdf.parse("24/11/2016"));
		holidays.add((Date) sdf.parse("25/12/2016"));
	}
	
	
	/**
	 * A function to get next Business day
	 * @return next business date
	 */
	public static Date nextBusinessDay(){
		Date date = TradeCapture.CURRENT_DATE;
		do{
			date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
		}while (!isBusinessDay(date));
		return date;
	}
	

	/** 
	 * A function to get next Three BusinessDay 
	 * @return next three business date
	 */
	public static Date nextThreeBusinessDay(){
		Date date = TradeCapture.CURRENT_DATE;
		int count = 0;
		while (count<3){
			date = new Date(date.getTime() + (1000 * 60 * 60 * 24));
			if (isBusinessDay(date)) count = count + 1;
		}
		System.out.print("Next Three Buiness Day:"+ date);
		return date;
		
	}
	
	/** To judge whether the date is business day
	 * @param date 
	 * @return true if it is business day, otherwise return false
	 */
	public static boolean isBusinessDay(Date date){
		if (date.getDay()==6 || date.getDay() ==0)
			return false;
		if (TradeCapture.holidays.contains(date))
			return false;
		return true;
	}	

}
