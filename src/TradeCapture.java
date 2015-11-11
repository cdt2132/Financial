import java.sql.SQLException;

/**
 * Main class of application. 
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */

public class TradeCapture {

	// Main function
	public static void main(String[] args) throws SQLException {
		// create new ExchangeListener and Display
		(new Thread(new ExchangeListener())).start();
		(new Thread(new Display())).start();
	}
}
