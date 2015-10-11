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
import javax.swing.JOptionPane;

/** DatabaseManager connects to AWS MySQL database to store trades */
public class DatabaseManager {
	String url = "jdbc:mysql://csor4995.cy52ghsodz6n.us-west-2.rds.amazonaws.com/CSOR4995";
	String username = "root";
	String password = "12345678";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;

	/**  Connects to AWS MySQL database */
	public DatabaseManager()
	{
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
	 */
	public void outputTrades() {
		try {
			// create new CSV file
			PrintWriter writer = new PrintWriter("Trades.csv", "UTF-8");
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

	/** Output aggregate positions
	 */
	public void outputAggregate() {
		try {
			// create new CSV file
			PrintWriter writer = new PrintWriter("Aggregate.csv", "UTF-8");
			ResultSet rs = getResult("SELECT symbol, expMonth, expYear, SUM(buy*lot) FROM Orders GROUP BY symbol, expMonth, expYear");
			writer.println("Symbol, expMonth, expYear, Aggregate");

			// print all aggregate positions in Orders table
			while (rs.next()) {
				writer.println(rs.getString("symbol") 	 + ", "
							 + rs.getString("expMonth")  + ", "
							 + rs.getString("expYear")   + ", "
							 + rs.getString("SUM(buy*lot)")
				);
			}
			writer.close();
		} catch(SQLException sqle) {
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
