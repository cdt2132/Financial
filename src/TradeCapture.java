/**
 * TradeCapture class that creates a new ExchangeListener and Display
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */
public class TradeCapture {

	public static void main(String[] args) {
		(new Thread(new ExchangeListener())).start();
		(new Thread(new Display())).start();
	}
}
