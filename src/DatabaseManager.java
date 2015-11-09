/**
 * DatabaseManager Class connects to AWS Database
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.util.HashMap;
import java.sql.Timestamp;

/** DatabaseManager is a singleton class that connects to AWS MySQL database to store trades */
public class DatabaseManager {
	String url = "jdbc:mysql://csor4995.cy52ghsodz6n.us-west-2.rds.amazonaws.com/CSOR4995";
	String username = "root";
	String password = "12345678";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;
	
	private static DatabaseManager instance = null;
	protected DatabaseManager() {
		try{
			Class.forName( "com.mysql.jdbc.Driver" ); 
			con= DriverManager.getConnection(url, username, password ); 
			stmt=con.createStatement();
			System.out.println("Connected!");
		}
		catch(Exception sqle)
		{
			System.out.println(sqle.toString());
		}
	}
	
	public static DatabaseManager getInstance() {
	      if(instance == null) {
	         instance = new DatabaseManager();
	      }
	      return instance;
	}

	

	/** Receive ResultSet
	 * @param strSQL The sql query to evaluate (SELECT)
	 * @return the result set generated by the query
	 */
	public ResultSet getResult(String strSQL)
	{
		try{
			rs=stmt.executeQuery(strSQL);
			rsm=rs.getMetaData();
			return rs;
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
			return null;
		}
	}

	/** Execute SQL statement
	 * @param strSQL the sql query to execute (INSERT)
	 * @return true if sql statement executed
	 */
	public boolean updateSql(String strSQL) {
		try {
			stmt.executeUpdate(strSQL);
			con.setAutoCommit(true);
			return true;
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
			return false;
		}
	}

	public void displayTrades() {
		try {
			ResultSet rs = getResult("SELECT * FROM Orders");
			while (rs.next()) {
				System.out.println(rs.getString("symbol") + ", "
				+ rs.getString("ordertime") + ", "
				+ rs.getString("expMonth") + ", "
				+ rs.getString("expYear") + ", "
				+ rs.getString("lot") + ", "
				+ rs.getString("price") + ", "
				+ rs.getString("buy") + ", "
				+ rs.getString("trader") + ", "
				);
			}
		} catch(SQLException sqle) {
			System.out.println(sqle.toString());
		}
	}


	/** Inserts an order into the database
	 *
	 * @param o An order to insert into the database
	 * @return true if the order was successfully inserted
	 */
	public boolean insertOrder(Order o) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String strSQL = "INSERT INTO Orders("
						+ "symbol, "
						+ "ordertime, "
						+ "expMonth, "
						+ "expYear, "
						+ "lot, "
						+ "price, "
						+ "buy, "
						+ "trader) "
						+ "VALUES ( "
						+ "'" + o.symbol   + "', "
						+ "'" + dateFormat.format(o.date) + "', "
						+ o.expMonth + ", "
						+ o.expYear  + ", "
						+ o.lots     + ", "
						+ o.price    + ", "
						+ o.buySell  + ", "
						+ o.trader   + ")";
		System.out.println(strSQL);
		if (updateSql(strSQL)) return true;
		return false;
	}

	/**
	 * Outputs a CSV file with all orders in the database
	 * @param filePath path to save trade report
	 */
	public void outputTrades(String filePath) {
		try {
			// create new CSV file
			// saves CSV file to directory specified by user with file name "timestamp + Trades.csv"
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp +"Trades.csv";
			System.out.println(filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			ResultSet rs = getResult("SELECT * FROM Orders");
			writer.println("Symbol, orderTime, expMonth, expYear, lot, price, buy, trader");

			// print all rows in Orders table
			while (rs.next()) {
				writer.println(rs.getString("symbol") 	 + ", "
							 + rs.getString("ordertime") + ", "
				             + rs.getString("expMonth")  + ", "
				             + rs.getString("expYear")   + ", "
							 + rs.getString("lot")       + ", "
							 + rs.getString("price")     + ", "
							 + rs.getString("buy")       + ", "
							 + rs.getString("trader")    + ", "
				);
			}
			writer.close();
		} catch(SQLException sqle) {
			System.out.println(sqle.toString());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Output aggregate positions
	 * @param filePath path to save aggregate report
	 */
	public void outputAggregate(String filePath) {
		try {
			// create new CSV file 
			// saves CSV file to directory specified by user with file name "timestamp + Aggregate.csv"
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp +"Aggregate.csv";
			System.out.println(filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			ResultSet rs = getResult("SELECT symbol, expMonth, expYear, SUM(buy*lot*price) " +
			"						  FROM Orders " +
			"						  GROUP BY symbol, expMonth, expYear");
			writer.println("Symbol, expMonth, expYear, Aggregate");

			// print all aggregate positions in Orders table
			while (rs.next()) {
				writer.println(rs.getString("symbol") 	 + ", "
							 + rs.getString("expMonth")  + ", "
							 + rs.getString("expYear")   + ", "
							 + rs.getString("SUM(buy*lot*price)")
				);
			}
			writer.close();
		} catch(SQLException sqle) {
			System.out.println(sqle.toString());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void PnL(String filePath) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp +"PnL.csv";
			System.out.println(filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("Symbol, expMonth, expYear, purchasePrice, marketPrice, PnL");
			ResultSet rs = getResult("SELECT symbol, AVG(price) as price FROM Orders GROUP BY symbol");

			HashMap<String, Double> prices = new HashMap<String, Double>();

			while (rs.next()) {
				String symbol = rs.getString("symbol");
				double avg_price = rs.getDouble("price");
				double market_price = Market.genMarketData(avg_price);
				prices.put(rs.getString("symbol"), market_price);

				/*
				System.out.println(symbol + ":");
				System.out.println("Price: " + avg_price);
				System.out.println("Market Price: " + market_price);
				*/
			}

			rs = getResult("SELECT * FROM Orders");

			while (rs.next()) {

				String symbol = rs.getString("symbol");
				double price = rs.getDouble("price");
				double market_price = prices.get(symbol);
				double pnl = market_price - price;

				writer.println(rs.getString("symbol") 	 + ", "
							 + rs.getString("expMonth")  + ", "
						     + rs.getString("expYear")   + ", "
						     + rs.getString("price")	 + ", "
							 + market_price				 + ", "
				             + pnl						 + ", "
				);

				System.out.println(symbol + ":");
				System.out.println("Price: " + price);
				System.out.println("Market Price: " + market_price);
				System.out.println("PnL: " + pnl + "\n");

			}
			writer.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/** Close connection to the database
	 */
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		}
	}

}