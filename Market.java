/**
 * Created by kunaljasty on 11/2/15.
 */
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class Market {

	// Generates random prices
	public static double genMarketData(double price) {
		Random r = new Random();
		double std = .25 * price;
		double market_price = r.nextGaussian() * std + price;

		return market_price;
	}

}
