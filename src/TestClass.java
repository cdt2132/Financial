/**A class testing classes including Order, Display, Order, DatabaseManager
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.sql.SQLException;

public class TestClass {	


	public static void main(){
		
		TradeCapture tc = new TradeCapture();
		
		/* UNIT TESTS */
		System.out.println("Starting Unit Tests...");
		
		/* Test Display.java */
		System.out.println("Testing Display.java...");
		Display disp = new Display();
		System.out.println("Display opened successfully");
		
		/* Test Order.java */
		System.out.println("Testing Order.java...");
		Order oMarket = new Order("MARKETTEST",10,2015,1,30.0, 1,0,1);
		Order oLimit = new Order("LIMITTEST",10,2015,1,30.0, 1,1,1);
		Order oPegged = new Order("PEGGEDTEST",10,2015,1,30.0, 1,2,1);
		
		System.out.println("Orders created");
		System.out.println("Market Order:");
		oMarket.printOrder();
		System.out.println("Limit Order:");
		oLimit.printOrder();
		System.out.println("Pegged Order:");
		oPegged.printOrder();
		
		System.out.println("Sending orders to ecxhange");
		try {
			oMarket.sendOrdertoExchange();
			oLimit.sendOrdertoExchange();
			oPegged.sendOrdertoExchange();
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/* Test DatabaseManager.java */
		System.out.println("Testing DatabaseManager.java...");
		DatabaseManager db = DatabaseManager.getInstance();
		
		System.out.println("Trades in database:");
		db.displayTrades();
		
		System.out.println("Inserting orders into database...");
		db.insertOrder(oMarket);
		db.insertOrder(oLimit);
		db.insertOrder(oPegged);
		System.out.println("Generating reports...");
		db.outputTrades("./");
		db.outputAggregate("./");
		db.outputPnL("./");
		
		
		/* Test ExchangeAcceptor.java */
		System.out.println("Testing ExchangeAcceptor.java...");
		
		
		/* Test ExchangeListener.java */
		System.out.println("Testing ExchangeListener.java...");
		
		
		/* Test Market.java */
		System.out.println("Testing Market.java...");
	
		double price1 = Market.genMarketData(1);
		double price2 = Market.genMarketData(10);
		double price3 = Market.genMarketData(100);
		System.out.println("Order price: 1");
		System.out.println("Market price: " + price1);
		System.out.println("Order price: 2");
		System.out.println("Market price: " + price2);
		System.out.println("Order price: 3");
		System.out.println("Market price: " + price3);
		System.out.println();
		
		/* Test Order.java */
		System.out.println("Testing Order.java...");
		
		
		
		Display disp1 = new Display();
		
		
		Order ord = new Order("BB",10,2015,1,30.0, 1,1,1);
		ord.printOrder();
		System.out.println("Order Class OK");
		


		db.insertOrder(ord);
		System.out.println("DatabaseManager->insertOrder OK");
		db.getResult("SELECT * FROM Orders ORDER BY ordertime DESC LIMIT 1");
		try {
			while (db.rs.next()){
				for (int i = 1; i <= 8;i++)
				System.out.println(db.rs.getString(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("DatabaseManager->getResult OK");
		db.outputTrades("./");
		System.out.println("DatabaseManager->outputTrades OK");
		db.outputAggregate("./");
		System.out.println("DatabaseManager->outputAggregate OK");


		SaveDialog svd = new SaveDialog();
		System.out.println(svd.getName());
		System.out.println("SaveDialog Class OK");
		


		System.out.println("Display class OK");
		
	}

}
