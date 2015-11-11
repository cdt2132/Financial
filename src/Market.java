/**
 * Class that generates market data 
 * 
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */
import java.util.Random;

public class Market {

	// Generates random prices
	public static double genMarketData(double price) {

		Random r = new Random();
		
		// std = 25% of purchase price, marketprice normally distributed
		double std = .25 * price;
		return r.nextGaussian() * std + price;

	}

}
