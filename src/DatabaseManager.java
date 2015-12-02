
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DatabaseManager {

	// Instance variables
	String url = "jdbc:mysql://csor4995.cy52ghsodz6n.us-west-2.rds.amazonaws.com/CSOR4995";
	String username = "root";
	String password = "12345678";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;

	private static DatabaseManager instance = null;

	/** Constructor */
	protected DatabaseManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			System.out.println("Connected!");
		} catch (Exception sqle) {
			System.out.println(sqle.toString());
		}
	}

	/**
	 * Get instance of class
	 * 
	 * @return DatabaseManager instance
	 */
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	/**
	 * Receive ResultSet
	 * 
	 * @param strSQL
	 *            The sql query to evaluate (SELECT)
	 * @return the result set generated by the query
	 */
	public ResultSet getResult(String strSQL) {
		try {
			rs = stmt.executeQuery(strSQL);
			rsm = rs.getMetaData();
			return rs;
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
			return null;
		}
	}

	/**
	 * Execute SQL statement
	 * 
	 * @param strSQL
	 *            the sql query to execute (INSERT)
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

	/**
	 * Get Market price of a symbol
	 * 
	 * @throws SQLException
	 */
	public double getMarketPrice(String symbol) throws SQLException {
		String sql = "SELECT * FROM Market where symbol=\"" + symbol + "\"";
		System.out.println(sql);
		ResultSet rs = getResult(sql);
		rs.next();
		return rs.getDouble(2);
	}

	/**
	 * Get Market size of a symbol
	 * 
	 * @throws SQLException
	 */
	public double getMarketSize(String symbol) throws SQLException {
		String sql = "SELECT * FROM Market where symbol=\"" + symbol + "\"";
		System.out.println(sql);
		ResultSet rs = getResult(sql);
		rs.next();
		return rs.getDouble(3);
	}

	/** Prints all trades in database */
	public void displayTrades() {
		try {

			// Query all trades
			ResultSet rs = getResult("SELECT * FROM Trades");
			while (rs.next()) {

				// print each trade
				System.out.println(rs.getString("symbol") + ", " + rs.getString("ordertime") + ", "
						+ rs.getString("expMonth") + ", " + rs.getString("expYear") + ", " + rs.getString("lot") + ", "
						+ rs.getString("price") + ", " + rs.getString("buy") + ", " + rs.getString("trader") + ", ");
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		}
	}

	/**
	 * Inserts an order into the database
	 *
	 * @param o
	 *            An order to insert into the database
	 * @return true if the order was successfully inserted
	 */
	public boolean insertOrder(Order o) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	

		Date date = TradeCapture.getThirdBeforeEOM(o.expMonth, o.expYear);
		
		// insert order into database
		String strSQL = "INSERT INTO Trades(" + "symbol, " + "ordertime, " + "expMonth, " + "expYear, " + "lot, "
				+ "price, " + "buy, " + "trader, " + "maturity) " + "VALUES ( " + "'" + o.symbol + "', " + "'"
				+ dateFormat.format(o.date) + "', " + o.expMonth + ", " + o.expYear + ", " + o.lots + ", " + o.price
				+ ", " + o.buySell + ", " + o.trader + ", " + dateFormat.format(date) + ")";
		System.out.println(strSQL);
		if (updateSql(strSQL))
			return true;

		// return false if order was not inserted
		return false;
	}

	/**
	 * Inserts a swap trade into the database
	 *
	 * @param s
	 *            A swap to insert into the database
	 * @return true if the order was successfully inserted
	 */

	public boolean insertSwap(Swap s) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startdate = new Date();
		startdate.setMonth(s.startMonth - 1);
		startdate.setYear(s.startYear - 1900);
		startdate.setDate(s.startDay);

		Date termdate = new Date();
		termdate.setMonth(s.termMonth - 1);
		termdate.setYear(s.termYear - 1900);
		termdate.setDate(s.termDay);

		String strSQL = "INSERT INTO Swaps(" + "startdate, " + "termdate, " + " fixedrate," + " floatrate, "
				+ "floatratespread, " + "swaptime, " + "payerfloat, " + "payerfixed," + "trader" + ") " + "VALUES ( "
				+ "'" + dateFormat.format(startdate) + "', " + "'" + dateFormat.format(termdate) + "', " + s.fixedRate
				+ ", " + s.floatRate + ", " + s.floatRateSpread + ", " + "'" + dateFormat.format(s.date) + "'" + ", "
				+ "'" + s.PayerFloat + "'" + ", " + "'" + s.PayerFixed + "'," + s.trader + ")";
		System.out.println(strSQL);
		if (updateSql(strSQL))
			return true;

		// return false if order was not inserted
		return false;

	}

	/**
	 * Outputs a CSV file with all trades in the database
	 * 
	 * @param filePath
	 *            path to save trade report
	 */
	public void outputTrades(String filePath) {
		try {
			// create new CSV file
			// saves CSV file to directory specified by user with file name
			// "timestamp + Trades.csv"
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp + "Trades.csv";
			System.out.println(filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			ResultSet rs = getResult("SELECT * FROM Trades");
			writer.println("Symbol, orderTime, expMonth, expYear, lot, price, buy, trader");

			// print all rows in Trades table
			while (rs.next()) {
				writer.println(rs.getString("symbol") + ", " + rs.getString("ordertime") + ", "
						+ rs.getString("expMonth") + ", " + rs.getString("expYear") + ", " + rs.getString("lot") + ", "
						+ rs.getString("price") + ", " + rs.getString("buy") + ", " + rs.getString("trader") + ", ");
			}
			writer.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Output aggregate positions
	 * 
	 * @param filePath
	 *            path to save aggregate report
	 */
	public void outputAggregate(String filePath) {
		try {
			// create new CSV file
			// saves CSV file to directory specified by user with file name
			// "timestamp + Aggregate.csv"
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp + "Aggregate.csv";
			System.out.println(filename);
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			ResultSet rs = getResult(
					"SELECT symbol, expMonth, expYear, SUM(buy*lot*price) " + "						  FROM Trades "
							+ "						  GROUP BY symbol, expMonth, expYear");
			writer.println("Symbol, expMonth, expYear, Aggregate");

			// print all aggregate positions in Trades table
			while (rs.next()) {
				writer.println(rs.getString("symbol") + ", " + rs.getString("expMonth") + ", " + rs.getString("expYear")
						+ ", " + rs.getString("SUM(buy*lot*price)"));
			}
			writer.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Output PnL report (shows PnL of each trade, each trader, and each future
	 * type)
	 * 
	 * @param filePath
	 *            path to save PnL report
	 */
	public void outputPnL(String filePath) {

		try {

			// Filename is <timestamp>PnL
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filePath + "/" + timeStamp + "PnL.csv";
			System.out.println(filename);

			// Create new printwriter, print column titles
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("Date, Trader, Symbol, expMonth, expYear, purchasePrice, marketPrice, PnL");

			// Data structures store PnL information after sql query
			double totalPnL = 0;
			HashMap<String, Double> symbolPnL = new HashMap<String, Double>();
			HashMap<String, Double> prices = new HashMap<String, Double>();
			HashMap<Integer, Double> traderPnL = new HashMap<Integer, Double>();
			ArrayList<Trader> tsortedPnL = new ArrayList<Trader>();
			ArrayList<TradeSymbol> ssortedPnL = new ArrayList<TradeSymbol>();

			// Compares two trades pnl's
			Comparator<Trader> tcomparator = new Comparator<Trader>() {
				public int compare(Trader t1, Trader t2) {
					return t1.getPnL() < t2.getPnL() ? 1 : -1;
				}
			};

			// Compares two symbols pnl's
			Comparator<TradeSymbol> scomparator = new Comparator<TradeSymbol>() {
				public int compare(TradeSymbol s1, TradeSymbol s2) {
					return s1.getPnL() < s2.getPnL() ? 1 : -1;
				}
			};

			// To create market prices, first receive price by symbol
			ResultSet rs = getResult("SELECT symbol, price FROM Market");

			// Create market prices for each symbol in database
			// Market price is normally distributed from the average price
			// grouped by symbol
			while (rs.next()) {
				double price = rs.getDouble("price");
				prices.put(rs.getString("symbol"), price);
			}

			// Iterate through all trades in database
			rs = getResult("SELECT * FROM Trades");

			while (rs.next()) {

				// Calculate PnL of trade
				String symbol = rs.getString("symbol");
				double price = rs.getDouble("price");
				double market_price = prices.get(symbol);
				double pnl = market_price - price;
				totalPnL += pnl;

				// Update trades HM and symbols HM with pnl
				int trader = rs.getInt("trader");
				if (traderPnL.containsKey(trader)) {
					traderPnL.put(trader, traderPnL.get(trader) + pnl);
				} else {
					traderPnL.put(trader, pnl);
				}

				if (symbolPnL.containsKey(symbol)) {
					symbolPnL.put(symbol, symbolPnL.get(symbol) + pnl);
				} else {
					symbolPnL.put(symbol, pnl);
				}

				// Print pnl info to file
				writer.println(rs.getString("ordertime") + ", " + rs.getString("trader") + ", " + rs.getString("symbol")
						+ ", " + rs.getString("expMonth") + ", " + rs.getString("expYear") + ", "
						+ rs.getString("price") + ", " + market_price + ", " + pnl + ", ");

			}

			writer.println();

			// Convert hashmaps to arraylists (hacky way to sort)
			for (Integer traderId : traderPnL.keySet()) {
				Trader newTrader = new Trader(traderId, traderPnL.get(traderId));
				tsortedPnL.add(newTrader);
			}

			for (String symbolId : symbolPnL.keySet()) {
				TradeSymbol newSymbol = new TradeSymbol(symbolId, symbolPnL.get(symbolId));
				ssortedPnL.add(newSymbol);
			}

			// Sort traders and symbols by pnl
			Collections.sort(tsortedPnL, tcomparator);
			Collections.sort(ssortedPnL, scomparator);

			// Write out trader and symbol pnls to report
			writer.println("Trader, PnL");
			for (Trader t : tsortedPnL) {
				writer.println(t.getId() + ", " + t.getPnL());
			}
			writer.println();
			writer.println("Symbol, PnL");
			for (TradeSymbol s : ssortedPnL) {
				writer.println(s.getId() + ", " + s.getPnL());
			}

			// Write out total pnl .. prices are normally distributed so should
			// be close to 0
			writer.println();
			writer.println("TOTAL PNL: " + totalPnL);

			writer.close();

		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Generate report of all trades with flags of whether the swap is matured
	 * @param filepath
	 */
	public void swapAllTrades(String filepath) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filepath + "/" + timeStamp + "swapAllTrades.csv";
			System.out.println(filename);

			PrintWriter writer = new PrintWriter(filename, "UTF-8");

			ResultSet rs = getResult("SELECT * FROM Swaps");
			
			writer.println("startdate, termdate, fixedrate,floatrate,floatratespread,swaptime,payerfloat,payerfixed,trader,isMatured");

			// print all rows in Trades table
			while (rs.next()) {
				writer.println(rs.getString("startdate") + ", " + rs.getString("termdate") + ", "
						+ rs.getString("fixedrate") + ", " + rs.getString("floatrate") + ", " + rs.getString("floatratespread")
						+ ", " + rs.getString("swaptime") + ", " + rs.getString("payerfloat") + ", "
						+ rs.getString("payerfixed") + ", " + rs.getString("trader")+", " + rs.getString("isMatured"));
			}
			writer.close();

		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**Generate report of swaps maturing today
	 * @param filepath
	 */
	public void MaturingTodayTrades(String filepath) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filepath + "/" + timeStamp + "TodaysMaturingTrades.csv";
			System.out.println(filename);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("FUTURES");
			ResultSet rs = getResult("SELECT * FROM Trades WHERE maturity ='" + sdf.format(TradeCapture.CURRENT_DATE) + "';");
			writer.println("Symbol, orderTime, expMonth, expYear, lot, price, buy, trader");

			// print all rows in result set
			while (rs.next()) {
				writer.println(rs.getString("symbol") + ", " + rs.getString("ordertime") + ", "
						+ rs.getString("expMonth") + ", " + rs.getString("expYear") + ", " + rs.getString("lot") + ", "
						+ rs.getString("price") + ", " + rs.getString("buy") + ", " + rs.getString("trader") + ", ");
			}

			rs = getResult("SELECT * FROM Swaps WHERE termdate ='" + sdf.format(TradeCapture.CURRENT_DATE) + "';");
			writer.println("SWAPS");
			writer.println("startdate, termdate, fixedrate,floatrate,floatratespread,swaptime,payerfloat,payerfixed,trader,isMatured");

			// print all rows in Trades table
			while (rs.next()) {
				writer.println(rs.getString("startdate") + ", " + rs.getString("termdate") + ", "
						+ rs.getString("fixedrate") + ", " + rs.getString("floatrate") + ", " + rs.getString("floatratespread")
						+ ", " + rs.getString("swaptime") + ", " + rs.getString("payerfloat") + ", "
						+ rs.getString("payerfixed") + ", " + rs.getString("trader")+", " + rs.getString("isMatured"));
			}
			writer.close();

		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/** Aggregate by trader, but swaps are independently listed
	 * @param filepath
	 */
	public void swapAggregate(String filepath) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String filename = filepath + "/" + timeStamp + "swapAggregatedTrades.csv";
			System.out.println(filename);

			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			ResultSet rs = getResult("SELECT * FROM Swaps ORDER BY trader;");
			
			writer.println("startdate, termdate, fixedrate,floatrate,floatratespread,swaptime,payerfloat,payerfixed,trader,isMatured");
			// print all rows in Trades table
			while (rs.next()) {
				writer.println(rs.getString("startdate") + ", " + rs.getString("termdate") + ", "
						+ rs.getString("fixedrate") + ", " + rs.getString("floatrate") + ", " + rs.getString("floatratespread")
						+ ", " + rs.getString("swaptime") + ", " + rs.getString("payerfloat") + ", "
						+ rs.getString("payerfixed") + ", " + rs.getString("trader")+", " + rs.getString("isMatured"));
			}
			writer.close();

		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Flag matured swap before current date
	 * @param curr: current date
	 */
	public void swapFlag(Date curr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String strSQL = "UPDATE Swaps SET ismatured = 1 WHERE termdate < '" + sdf.format(curr) + "'";
		System.out.println(strSQL);
		updateSql(strSQL);
		
		strSQL = "UPDATE Swaps SET ismatured = 0 WHERE termdate >= '"+sdf.format(curr)+"'";
		updateSql(strSQL);
	
	}

	/**
	 * Close connection to the database
	 */
	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		}
	}

}
