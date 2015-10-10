/**The basic unit of this project, the order. Includes information 
*like price, trader etc. and methods like print order
*
*@author Caroline Trimble 
*@version 1 Build October 2015
*/ 

import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
	String buySell;
	/** The first and last name of the trader*/
	String trader; 

	
	/* The construction for an order simply assigns the parameters to the variables*/
	public Order(String s, int m, int y, int l, double p, String b, String t){
		symbol = s; 
		expMonth = m; 
		expYear = y;  
		lots = l; 
		price = p; 
		buySell = b; 
		trader = t; 
	}

	/*The print method prints the date and time of the order and all the information about the order*/
	void printOrder(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
		System.out.println(symbol + " "
                                          + expMonth + " " + expYear + " " 
                                          + lots + " "
                                          + price + " "
                                          + buySell + " "
                                          + trader);
        }
}
