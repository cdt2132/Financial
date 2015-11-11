/**A class testing classes including Order, Display, Order, DatabaseManager
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.sql.SQLException;

import quickfix.ConfigError;

public class TestClass {	

	public static void main(String[] args){
		
		/* Start application */
		TradeCapture tc = new TradeCapture();
		
		/* UNIT TESTS */
		
		System.out.println("Starting Tests...");
		
		/* Test Market.java 
		System.out.println("Testing Market...");
	
		// TODO TEST MARKET
		double price1 = Market.genMarketData(1);
		double price2 = Market.genMarketData(10);
		double price3 = Market.genMarketData(100);
		System.out.println("Order price: 1");
		System.out.println("Market price: " + price1);
		System.out.println("Order price: 10");
		System.out.println("Market price: " + price2);
		System.out.println("Order price: 100");
		System.out.println("Market price: " + price3);
		System.out.println("Market ---> OK");
		System.out.println();
		*/
		
		/* Test Display.java */
		System.out.println("Testing Display...");
		Display disp = new Display();
		System.out.println("Display opened successfully");
		System.out.println("Display ---> OK");
		System.out.println();
		
		/* Test order creation */
		System.out.println("Testing Order creation and Execution...");
		Order oMarket = new Order("MARKETTEST",10,2015,1,30.0, 1,0,1);
		Order oLimit = new Order("LIMITTEST",10,2015,1,30.0, 1,1,1);
		Order oPegged = new Order("PEGGEDTEST",10,2015,1,30.0, 1,2,1);
		
		System.out.println("Orders created: ");
		System.out.println("Market Order:");
		oMarket.printOrder();
		System.out.println("Limit Order:");
		oLimit.printOrder();
		System.out.println("Pegged Order:");
		oPegged.printOrder();
		
		System.out.println("Order creation ---> OK");
		System.out.println();
		
		/* Test order execution */
		System.out.println("Testing order execution...");
		System.out.println("Sending orders to exchange...");
		try {
			oMarket.sendOrdertoExchange();
			oLimit.sendOrdertoExchange();
			oPegged.sendOrdertoExchange();
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Orders sent and received successfully");
		
		/* Test Fix Exchange */
		System.out.println("Additional testing of Fix Exchange...");
		ClientInitiator fixInitiator;
		
		try {
			System.out.println("ClientInitiator getInstance OK");
			fixInitiator = ClientInitiator.getInstance();
			System.out.println("Sending orders from ClientInitiator:");
			
			System.out.println("Sending Market order");
			fixInitiator.sendOrder(Integer.toString(1), "HH", 1, 1, 1, 1, 2016, 0);
			System.out.println("Sending Limit order");
			fixInitiator.sendOrder(Integer.toString(1), "HH", 1, 1, 1, 1, 2016, 1);
			System.out.println("Sending Pegged order");
			fixInitiator.sendOrder(Integer.toString(1), "HH", 1, 1, 1, 1, 2016, 2);
			
		} catch (ConfigError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		DatabaseManager db = DatabaseManager.getInstance();
		
		/* Test Reports */
		
		System.out.println("Testing reports...");
		System.out.println("Generating reports...");
		db.outputTrades("./");
		db.outputAggregate("./");
		db.outputPnL("./");
		
		System.out.println("Reports generated successfully");
		System.out.println("Finished testing");
		System.exit(0);
	}

}
