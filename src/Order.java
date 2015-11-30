/**
*Equity order. Includes information 
*like price, trader etc. and methods like print order
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import quickfix.ConfigError;

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
	/** Type of order */
	int orderType;
	/** The number of the trader */
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
	 * @param ot order type (0: market, 1: limit ,2: pegged)
	 * @param t trader id
	 * */
	public Order(String s, int m, int y, int l, double p, int b, int ot, int t){
		symbol = s; 
		expMonth = m; 
		expYear = y;  
		lots = l; 
		price = p; 
		buySell = b;
		orderType = ot;
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

	/** Sends the order to test exchange 
	 * @throws Throwable */
	public void sendOrdertoExchange() throws Throwable {
		try {
			ClientInitiator fixInitiator = ClientInitiator.getInstance();
			fixInitiator.sendOrder(Integer.toString(trader), symbol, lots, price, buySell, expMonth, expYear, orderType);
		} catch (ConfigError e) {
			e.printStackTrace();
		}
		
	}

}
