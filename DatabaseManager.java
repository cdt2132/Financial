import java.sql.*;

public  class DatabaseManager {
	String url="jdbc:mysql://csor4995.cy52ghsodz6n.us-west-2.rds.amazonaws.com";
	String username="root";
	String password="12345678";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;
	
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
		
	public ResultSet getResult(String strSQL)
	{
		try{
			rs=stmt.executeQuery(strSQL);
			rsm=rs.getMetaData();
			return rs;
		}
		catch(SQLException sqle)
		{
			System.out.println(sqle.toString());
			return null;
		}
	}
	
	 public boolean updateSql(String strSQL)
     {
      	try{
      		stmt.executeUpdate(strSQL);
      		con.setAutoCommit(false); 
      		con.commit();
              return true;	
      	}
      	catch(SQLException sqle)
      	{
      		System.out.println(sqle.toString());
      		return false;
      	}
      }
	 
	  public void closeConnection()
      {
      	try
      	{
      		con.close();
      	}
      	catch(SQLException sqle)
      	{
      		System.out.println(sqle.toString());
      	}
      }      
	}
