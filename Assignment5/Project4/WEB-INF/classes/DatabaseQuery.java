/* Name: Ryan Flynn
     Course: CNT 4714 - Spring 2020 - Project Four
     Assignment Title: A Three-Tier Distributed Web-Based Application
     Date: April 5, 2020
*/

import java.sql.*;
import java.io.FileWriter;


public class DatabaseQuery 
{
	Connection connection;
	Statement statement;
	ResultSet resultSet;
	ResultSetMetaData metaData;
	int numRows;
	int numCols;
	
	String databaseDriver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3312/project4";
	String username = "root";
	String password = "root";
	
	String query;
	String updateResults;
	
	public DatabaseQuery(String sql_command)
	{
		// Establish the connection to the data base
		try
		{
			this.query = sql_command;
		
			// Establish a connection to the database
			Class.forName(databaseDriver);
		
			connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

		}
		
		catch(Exception e) {e.printStackTrace();}
		
		executeCommand();
	}
	
	private void executeCommand()
	{		
		// Select Command
		if(query.charAt(0) == 's' || query.charAt(0) == 'S')
		{
			try
			{
				// Executing the query returns a result set
				resultSet = statement.executeQuery(query);
				
				// Get the meta data
				metaData = resultSet.getMetaData();
				
				// Set the resultset to the last row
				resultSet.last();
				
				// Get the last number of rows based on the row that the resultSet is at.
				numRows = resultSet.getRow();
				numCols = metaData.getColumnCount();
				
				resultSet.first();
			}
			
			catch(Exception e) {e.printStackTrace();}
		}
		
		// Update or Insert
		else
		{
			try
			{
				// Perform the business logic
				setUpdateResults();
			}
			
			catch(Exception e) {e.printStackTrace();}
		}
	}
	
	private void setUpdateResults()
	{
		// Perform the business logic and finally set the update results.
		String results = "Results are empty";
		String divStyle = "<div style=" + "\"background-color:green\"" + ">";
		String errorStyle = "<div style=" + "\"background-color:red\"" + ">";
		try
		{
			// Get the total number of shipments that are greater than or equal to 100 BEFORE the insert/update
			ResultSet beforeQuantityQuery = statement.executeQuery("select COUNT(*) from shipments where quantity >= 100");
			beforeQuantityQuery.next();
			int beforeQuantity = beforeQuantityQuery.getInt(1);
			
			statement.executeUpdate("create table initialShipments like shipments");
			statement.executeUpdate("insert into initialShipments select * from shipments");

			// Execute command
			int numRowsAffected = statement.executeUpdate(query);
			results = divStyle + "<p>The statement was executed successfully.<br>" + numRowsAffected + " row(s) affected.<br><br>";
			
			// Get the total number of shipments that are greater than or equal to 100 AFTER the insert/update
			ResultSet afterQuantityQuery = statement.executeQuery("select COUNT(*) from shipments where quantity >= 100");
			afterQuantityQuery.next();
			int afterQuantity = afterQuantityQuery.getInt(1); 
			
			// Check for the business logic
			if(afterQuantity > beforeQuantity)
			{
				results += "Business Logic Detected! Updating Supplier Status<br><br>";
				
				// Increase the supplier status by 5
				String bisLogicQuery = "update suppliers set status = status + 5 where snum in "
										+ "(select distinct snum from shipments left join initialShipments using (snum, pnum, jnum, quantity)"
										+ "where initialShipments.snum is null)";
				
				int bisLogicAffected = statement.executeUpdate(bisLogicQuery);
				
				results += "Business Logic updated " + bisLogicAffected + " supplier status marks.</p></div>";
			}
			
			else
				results += "</p></div>";
			
			statement.executeUpdate("drop table initialShipments");

		}
		
		catch (Exception e) 
		{
			results = errorStyle + "There was an error exectuting the command <br><br>" + e.getMessage() + "</p></div>";
			e.printStackTrace();
		}
		
		this.updateResults = results;
		
	}

	public String getUpdateResults()
	{
		return this.updateResults;
	}
	
	public String formatSQL()
	{
		String results = "<table style=" + "\"margin-left:auto; margin-right:auto; background-color: blue\"" + ">";
		String headers = "<tr>";
		String data = "";
		
		String headerStyle = " style=" + "\"border:2 px solid black; background-color:red\"";
		String evenDataStyle = " style=" + "\"border:2 px solid black; background-color:#dddddd\"";
		String oddDataStyle = " style=" + "\"border:2 px solid black; background-color:white\"";
		String errorStyle = "<div style=" + "\"background-color:red\"" + ">";
		
		try
		{
			resultSet.beforeFirst();
			
			// Add the column names as table headers
			for(int i = 1; i <= numCols; i++)
				headers +=  "<th" + headerStyle + ">" + metaData.getColumnName(i) + "</th>";
			
			// Close of the table headers row
			headers += "</tr>";
			
			// Enter the data for each row, even used to alternate the color of the rows
			int even = 0;
			while(resultSet.next())
			{
				data += "<tr>";
				for(int j = 1; j <= numCols; j++)
				{
					if(even % 2 == 0)
						data += "<td"+ evenDataStyle +">" + resultSet.getString(j) + "</td>";
					else
						data += "<td"+ oddDataStyle + ">" + resultSet.getString(j) + "</td>";
				}
				
				data += "</tr>";
				even++;
			}
		}
		
		catch(Exception e) 
		{
			results = errorStyle + "Error executing the SQL statement:<br>" + e.getMessage() + "</p></div>";
			e.printStackTrace();
			return results;
		}
		
		results += headers + data + "</table>";

		return results;
	}

}
