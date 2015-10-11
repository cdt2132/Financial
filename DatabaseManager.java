import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class DatabaseManager {
	String url = "jdbc:mysql://csor4995.cy52ghsodz6n.us-west-2.rds.amazonaws.com/CSOR4995";
	String username = "root";
	String password = "12345678";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;

	public DatabaseManager() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
		} catch (Exception sqle) {
			System.out.println(sqle.toString());
		}
	}

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

	public void outputTrades() {
		try {
			PrintWriter writer = new PrintWriter("Trades.csv", "UTF-8");
			ResultSet rs = getResult("SELECT * FROM Orders");
			writer.println("Symbol, orderTime, expMonth, expYear, lot, price, buy, trader");
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

	public void outputAggregate() {
		try {
			PrintWriter writer = new PrintWriter("Aggregate.csv", "UTF-8");
			ResultSet rs = getResult("SELECT symbol, expMonth, expYear, SUM(buy*lot) FROM Orders GROUP BY symbol, expMonth, expYear");
			writer.println("Symbol, expMonth, expYear, Aggregate");
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

	public void closeConnection() {
		try {
			con.close();
		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
		}
	}
}
