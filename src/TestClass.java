/**A class testing classes including Order, Display, Order, DatabaseManager
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.util.Date;
import java.sql.SQLException;
import java.text.ParseException;

import quickfix.ConfigError;


public class TestClass {	
	
	/**
	 * Main testing function
	 * @param args
	 */
	public static void main(String[] args){
		
		System.out.println("Starting Tests...");
		
		// Initiatlize tc application
		TradeCapture tc = new TradeCapture();
		
		try {
			TradeCapture.formatDates();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Initialized Application");

		DatabaseManager db = DatabaseManager.getInstance();
		db.clearTradesandSwaps();
		System.out.println("Cleared trades and swaps");
		
		Order o1 = new Order("HH",11,2015,20,Market.genMarketData(80.0), 1,0,1);
		Order o2 = new Order("HH",11,2015,25,Market.genMarketData(80.0), 1,0,1);
		Order o3 = new Order("HH",11,2015,23,Market.genMarketData(80.0), -1,0,2);
		Order o4 = new Order("HH",2,2016,30,Market.genMarketData(80.0), -1,0,3);
		Order o5 = new Order("HH",2,2016,15,Market.genMarketData(80.0), 1,0,2);
		
		Order o6 = new Order("NN",4,2016,15,Market.genMarketData(90.0), -1,0,1);
		Order o7 = new Order("NN",4,2016,20,Market.genMarketData(90.0), 1,0,5);
		Order o8 = new Order("NN",3,2016,48,Market.genMarketData(90.0), -1,0,4);
		Order o9 = new Order("NN",3,2016,38,Market.genMarketData(90.0), 1,0,2);
		Order o10 = new Order("NN",3,2016,19,Market.genMarketData(90.0), 1,0,1);

		Order o11 = new Order("NG",12,2015,21,Market.genMarketData(60.0), 1,0,1);
		Order o12 = new Order("NG",12,2015,19,Market.genMarketData(60.0), 1,0,3);
		Order o13 = new Order("NG",12,2015,27,Market.genMarketData(60.0), -1,0,4);
		Order o14 = new Order("NG",12,2015,26,Market.genMarketData(60.0), -1,0,4);
		Order o15 = new Order("NG",12,2015,40,Market.genMarketData(90.0), -1,0,5);
	
		Order o16 = new Order("LN",12,2015,45,Market.genMarketData(100.0), 1,0,5);
		Order o17 = new Order("LN",12,2015,11,Market.genMarketData(100.0), 1,0,2);
		Order o18 = new Order("LN",12,2015,13,Market.genMarketData(100.0), 1,0,2);
		Order o19 = new Order("LN",12,2015,40,Market.genMarketData(100.0), -1,0,3);
		Order o20 = new Order("LN",12,2015,37,Market.genMarketData(100.0), -1,0,1);

		Swap s0 = new Swap(1, 1, 2015, 28, 12, 2015, .00, .005, .115, "Me", "CME", 1);
		Swap s1 = new Swap(1, 1, 2015, 28, 12, 2015, .01, .005, .95, "Me", "CME", 2);
		Swap s2 = new Swap(1, 1, 2015, 28, 12, 2015, .02, .005, .85, "Me", "CME", 3);
		Swap s3 = new Swap(1, 1, 2015, 28, 12, 2015, .03, .005, .75, "Me", "CME", 4);
		Swap s4 = new Swap(1, 1, 2015, 28, 12, 2015, .04, .005, .65, "Me", "CME", 5);
		
		Swap s5 = new Swap(1, 1, 2015, 28, 12, 2015, .05, .005, .55, "LCH", "Me", 1);	
		Swap s6 = new Swap(1, 1, 2015, 28, 12, 2015, .06, .005, .45, "LCH", "Me", 2);
		Swap s7 = new Swap(1, 1, 2015, 28, 12, 2015, .07, .005, .35, "LCH", "Me", 3);
		Swap s8 = new Swap(1, 1, 2015, 28, 12, 2015, .08, .005, .25, "LCH", "Me", 4);
		Swap s9 = new Swap(1, 1, 2015, 28, 12, 2015, .09, .005, .15, "LCH", "Me", 5);
		
		
		db.insertOrder(o1);
		db.insertOrder(o2);
		db.insertOrder(o3);
		db.insertOrder(o4);
		db.insertOrder(o5);
		
		db.insertOrder(o6);
		db.insertOrder(o7);
		db.insertOrder(o8);
		db.insertOrder(o9);
		db.insertOrder(o10);
		
		db.insertOrder(o11);
		db.insertOrder(o12);
		db.insertOrder(o13);
		db.insertOrder(o14);
		db.insertOrder(o15);
		
		db.insertOrder(o16);
		db.insertOrder(o17);
		db.insertOrder(o18);
		db.insertOrder(o19);
		db.insertOrder(o20);
		
		db.insertSwap(s0);
		db.insertSwap(s1);
		db.insertSwap(s2);
		db.insertSwap(s3);
		db.insertSwap(s4);
		
		db.insertSwap(s5);
		db.insertSwap(s6);
		db.insertSwap(s7);
		db.insertSwap(s8);
		db.insertSwap(s9);
		System.out.println("Inserted orders and swaps");
	
		/* Test Reports */
		
		System.out.println("Testing reports...");
		System.out.println("Generating reports...");
		db.outputTrades(".");
		db.outputAggregate(".");
		db.outputPnL(".");
		db.swapAllTrades(".");
		db.swapAggregate(".");
		db.MaturingTodayTrades(".");
	
		/* UNIT TESTS */
		
		
		// Rolling Date
		TradeCapture.CURRENT_DATE = TradeCapture.getThirdBeforeEOM(12, 2015);
				
		// Generate report for pre-date roll
		DatabaseManager.getInstance().MaturingTodayTrades(".");
				
		// Roll the date
		TradeCapture.CURRENT_DATE = TradeCapture.nextBusinessDay();
		System.out.println("ROLLED THE DATE TO: " + TradeCapture.CURRENT_DATE.toString());
				
		// flag matured orders and swaps
		DatabaseManager.getInstance().setFlags(TradeCapture.CURRENT_DATE);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		// Generate report for post-date roll (should have no trades..) 
		DatabaseManager.getInstance().MaturingTodayTrades(".");
			
		// reset the date
		TradeCapture.CURRENT_DATE = new Date();
		
		/* Test Display.java */
		System.out.println("Testing Display...");
		Display disp = new Display();
		System.out.println("Display opened successfully");
		System.out.println("Display ---> OK");
		System.out.println();
		
		/* Test order creation */ 
		System.out.println("Testing Order creation and Execution...");
		Order oMarket = new Order("HH",10,2015,10,80.0, 1,0,1);
		Order oLimit = new Order("HH",10,2015,10,80.0, 1,1,1);
		Order oPegged = new Order("HH",10,2015,10,80.0, 1,2,1);
		
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
			Thread.sleep(10000);
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

		System.out.println("Reports generated successfully");
		System.out.println("Finished testing");
		System.exit(0);
		
	}

}
