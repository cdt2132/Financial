import java.sql.*;  

import javax.swing.JOptionPane;

public  class DatabaseManager {
	String url="jdbc:mysql://localhost/dbcourse";
	String username="root";
	String password="123456";
	Connection con;
	ResultSet rs;
	ResultSetMetaData rsm;
	Statement stmt;
	
	public DatabaseManager()
	{
		try{
			Class.forName( "org.gjt.mm.mysql.Driver" ); 
			con= DriverManager.getConnection(url, username, password ); 
			stmt=con.createStatement();
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
			rsm=rs.getMetaData();//ÐÂÌíµÄÒ»ÐÐ£¡£¡£¡£¡
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
      		con.setAutoCommit(false);  ///????????
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
