/**The basic unit of this project, the order. Includes information 
*like price, trader etc. and methods like print order
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order{

	/** The symbol, like "HH" for the order */
	String symbol;
	/** The month that the contract expires */
	int expMonth;
	/** The year that the contract expires */ 
	int expYear;
	/** Number of lots bought or sold */
	int lots; 
	/** Price per lot */
	double price; 
	/** Whether the order was bought or sold */
	int buySell;
	/** The first and last name of the trader */
	int trader;
	/** The time the order is placed */
	Date date;
	
	/** Constructor for an order
	 * @param s futures symbol (from CME)
	 * @param m expiry month
	 * @param y expiry year
	 * @param l lot size
	 * @param p future price
	 * @param b buy or sell (1 or -1)
	 * @param t trader id
	 * */
	public Order(String s, int m, int y, int l, double p, int b, int t){
		symbol = s; 
		expMonth = m; 
		expYear = y;  
		lots = l; 
		price = p; 
		buySell = b; 
		trader = t;
		date = new Date();
	}

	/** Prints the order */
	void printOrder(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.format(date)); //2014/08/06 16:00:22
		System.out.println(symbol   + " "
				 + expMonth + " " 
				 + expYear  + " "
				 + lots     + " "
				 + price    + " "
				 + buySell  + " "
				 + trader);
        }

	/** Sends the order to test exchange */
	public void sendOrdertoExchange() {

	}


}
