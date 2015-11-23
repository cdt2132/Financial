/**A class testing classes including Order, Display, Order, DatabaseManager
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.sql.SQLException;

import quickfix.ConfigError;

public class TestClass {	
	
	public static void main(String[] args){
		
		DatabaseManager db = DatabaseManager.getInstance();
		
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

		Order o11 = new Order("NG",5,2016,21,Market.genMarketData(60.0), 1,0,1);
		Order o12 = new Order("NG",5,2016,19,Market.genMarketData(60.0), 1,0,3);
		Order o13 = new Order("NG",10,2015,27,Market.genMarketData(60.0), -1,0,4);
		Order o14 = new Order("NG",10,2015,26,Market.genMarketData(60.0), -1,0,4);
		Order o15 = new Order("NG",10,2015,40,Market.genMarketData(90.0), -1,0,5);
	
		Order o16 = new Order("LN",4,2016,45,Market.genMarketData(100.0), 1,0,5);
		Order o17 = new Order("LN",4,2016,11,Market.genMarketData(100.0), 1,0,2);
		Order o18 = new Order("LN",1,2016,13,Market.genMarketData(100.0), 1,0,2);
		Order o19 = new Order("LN",1,2016,40,Market.genMarketData(100.0), -1,0,3);
		Order o20 = new Order("LN",1,2016,37,Market.genMarketData(100.0), -1,0,1);

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
		
		db.outputTrades("./");
		db.outputAggregate("./");
		db.outputPnL("./");
	}
	
	public static void test(String[] args){
		
	
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
