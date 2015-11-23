/**
 * Class that generates market data 
 * 
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */
import java.sql.SQLException;
import java.util.Random;

public class Market {

	// Generates random prices
	public static double genMarketData(double price) {
		Random r = new Random();
		double std = .15*price;
		return r.nextGaussian()*std + price;
	}
	
	public static double genMarketDatafromDB(String symbol) throws SQLException {
	
		// Return gaussian distributed market value
		Random r = new Random();
		DatabaseManager db = DatabaseManager.getInstance();
		double price = db.getMarketPrice(symbol);
		double std = .25 * price;
		return r.nextGaussian() * std + price;
		
	}
	
	public static double getMarketSize(String symbol) throws SQLException {
		DatabaseManager db = DatabaseManager.getInstance();
		double size = db.getMarketSize(symbol);
		return size;
	}

}
