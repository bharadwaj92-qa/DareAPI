package ogs.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DBUtils {

	/**
	 * Convert the ResultSet to a List of Maps, where each Map represents a row with columnNames and columValues
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static List<Map<String, String>> resultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
		while (rs.next()){
			Map<String, String> row = new HashMap<String, String>(columns);
			for(int i = 1; i <= columns; ++i){
				row.put(md.getColumnName(i), rs.getObject(i)!=null?rs.getObject(i).toString().matches("[.0-9]+")?new BigDecimal(rs.getObject(i).toString()).stripTrailingZeros().toPlainString():rs.getObject(i).toString():"");
			}
			rows.add(row);
		}
		return rows;
	}
	
	public static Map<String, String> getDareData(String query){
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("dare01_own");
		dataSource.setPassword("GEHkFNHW");
		dataSource.setServerName("dbvrt20648");
		try {
			dataSource.setConnectionCollation("");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			List<Map<String, String>> data = resultSetToList(rs);
			return data!=null && data.size()>0?data.get(0):null;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public static void main(String[] args) {
		
		//mySqlDBConnection("select * from dare01.dbo_tbltoollicensure where State='Texas'");
		
		
		/*// variables
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        // Step 1: Loading or 
        // registering Oracle JDBC driver class
        try {

            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        }
        catch(ClassNotFoundException cnfex) {

            System.out.println("Problem in loading or "
                    + "registering MS Access JDBC driver");
            cnfex.printStackTrace();
        }

        // Step 2: Opening database connection
        try {

            String msAccDB = "D:/WORKSPACE/TEST_WORKSPACE"
                    + "/Java-JDBC/Player.accdb";
            String dbURL = "jdbc:ucanaccess://"
                    + msAccDB; 

            // Step 2.A: Create and 
            // get connection using DriverManager class
            connection = DriverManager.getConnection(dbURL); 

            // Step 2.B: Creating JDBC Statement 
            statement = connection.createStatement();

            // Step 2.C: Executing SQL and 
            // retrieve data into ResultSet
            resultSet = statement.executeQuery("SELECT * FROM PLAYER");

            System.out.println("ID\tName\t\t\tAge\tMatches");
            System.out.println("==\t================\t===\t=======");

            // processing returned data and printing into console
            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1) + "\t" + 
                        resultSet.getString(2) + "\t" + 
                        resultSet.getString(3) + "\t" +
                        resultSet.getString(4));
            }
        }
        catch(SQLException sqlex){
            sqlex.printStackTrace();
        }
        finally {
            // Step 3: Closing database connection
            try {
                if(null != connection) {
                    // cleanup resources, once after processing
                    resultSet.close();
                    statement.close();

                    // and then finally close connection
                    connection.close();
                }
            }
            catch (SQLException sqlex) {
                sqlex.printStackTrace();
            }
        }*/

		
	}
	
	public static List<Map<String, String>> getDataFromDB(String query,String dbEnv){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String connString = PropertyLoader.getProperty(dbEnv+".db.connstring.url");
		    String user      = PropertyLoader.getProperty(dbEnv+".db.username");
		    String password  = PropertyLoader.getProperty(dbEnv+".db.password");
		    conn = DriverManager.getConnection(connString, user, password);
		    stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			List<Map<String, String>> data = resultSetToList(rs);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return null;
	}
	
}