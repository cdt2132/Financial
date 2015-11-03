/**A class testing classes including Order, Display, Order, DatabaseManager
*
*@author Caroline Trimble, Kunal Jasty, Haoxiang Gao
*@version 1 Build October 2015
*/ 

import java.sql.SQLException;

public class TestClass {	
	
	public static void main(String[] args){


		DatabaseManager db = new DatabaseManager();
		db.displayTrades();
		db.PnL("./");
		Display disp = new Display();
		db.outputAggregate("./");
		/*
		Order ord = new Order("BB",10,2015,1,30.0, 1, 1);
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
		

		Display disp = new Display();
		System.out.println("Display class OK");
		*/
	}

}
