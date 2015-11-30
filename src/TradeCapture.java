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

	public static Date CURRENT_DATE;
	public static ArrayList<Date> holidays;
	
	// Main function
	public static void main(String[] args) throws SQLException {
		formatDates();
		
		// create new ExchangeListener and Display
		(new Thread(new ExchangeListener())).start();
		(new Thread(new Display())).start();
	}
	
	public static void formatDates() throws ParseException {
		CURRENT_DATE = new Date();
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
}
