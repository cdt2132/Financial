/**
 * Created by kunaljasty on 11/3/15.
 */
public class TradeCapture {

	public static void main(String[] args) {
		(new Thread(new Display())).start();
		(new Thread(new ExchangeListener())).start();
	}
}
