import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Order{

	String symbol;
	String contractEx;
	int lots; 
	double price; 
	String buySell;
	String trader; 

	
	public Order(String s, String c, int l, double p, String b, String t){
		symbol = s; 
		contractEx = c; 
		lots = l; 
		price = p; 
		buySell = b; 
		trader = t; 
	}

	void printOrder(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
		System.out.println(symbol + " "
                                          + contractEx + " "
                                          + lots + " "
                                          + price + " "
                                          + buySell + " "
                                          + trader);
        }
}
