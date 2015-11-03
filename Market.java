/**
 * Created by kunaljasty on 11/2/15.
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
