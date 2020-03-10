/*
 * Name: Ryan Flynn
 * Course: CNT 4714 Spring 2020
 * Assignment title: Project Three: Two-Tier Client-Server Application Development With MySQL and JDBC
 * Date: March 5, 2020
 */

// Credit Dr. Mark Llewellyn for providing this code.
// Some minor chnages took place to make it suitable for Project 3

// A TableModel that supplies ResultSet data to a JTable.
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import javax.swing.table.AbstractTableModel;
import java.util.Properties;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;


// ResultSet rows and columns are counted from 1 and JTable 
// rows and columns are counted from 0. When processing 
// ResultSet rows or columns for use in a JTable, it is 
// necessary to add 1 to the row or column number to manipulate
// the appropriate ResultSet column (i.e., JTable column 0 is 
// ResultSet column 1 and JTable row 0 is ResultSet row 1).
public class ResultSetTableModel extends AbstractTableModel 
{
   public String err_message;
   
   private Connection connection;
   private Statement statement;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;

   // keep track of database connection status
   private boolean connectedToDatabase = false;
   
   // constructor initializes resultSet and obtains its meta data object;
   // determines number of rows
   public ResultSetTableModel(Connection connection, String query ) 
      throws SQLException, ClassNotFoundException, SQLSyntaxErrorException
   {         
	   Properties properties = new Properties();
	   FileInputStream filein = null;
	   MysqlDataSource dataSource = null;
       //read properties file
	   try {
		   // Connection is already established by SqlGUI
	
            // create Statement to query database
            statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );

            // update database connection status
            connectedToDatabase = true;

            // set query or update and execute it
            if (query.charAt(0) == 's' || query.charAt(0) == 'S')
            	setQuery( query );
            else
            	setUpdate (query);
            
	  } //end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
         System.exit( 1 );
      } // end catch

     }// end constructor ResultSetTableModel

   // get class that represents column type
   public Class getColumnClass( int column ) throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine Java class of column
      try 
      {
         String className = metaData.getColumnClassName( column + 1 );
         
         // return Class object that represents className
         return Class.forName( className );
      } // end try
      catch ( Exception exception ) 
      {
         exception.printStackTrace();
      } // end catch
      
      return Object.class; // if problems occur above, assume type Object
   } // end method getColumnClass

   // get number of columns in ResultSet
   public int getColumnCount() throws IllegalStateException
   {   
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine number of columns
      try 
      {
         return metaData.getColumnCount(); 
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return 0; // if problems occur above, return 0 for number of columns
   } // end method getColumnCount

   // get name of a particular column in ResultSet
   public String getColumnName( int column ) throws IllegalStateException
   {    
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // determine column name
      try 
      {
         return metaData.getColumnName( column + 1 );  
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return ""; // if problems, return empty string for column name
   } // end method getColumnName

   // return number of rows in ResultSet
   public int getRowCount() throws IllegalStateException
   {      
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );
 
      return numberOfRows;
   } // end method getRowCount

   // obtain value in particular row and column
   public Object getValueAt( int row, int column ) 
      throws IllegalStateException
   {
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // obtain a value at specified ResultSet row and column
      try 
      {
		   resultSet.next();  /* fixes a bug in MySQL/Java with date format */
         resultSet.absolute( row + 1 );
         return resultSet.getObject( column + 1 );
      } // end try
      catch ( SQLException sqlException ) 
      {
         sqlException.printStackTrace();
      } // end catch
      
      return ""; // if problems, return empty string object
   } // end method getValueAt
   
   // set new database query string
   public void setQuery( String query ) 
      throws SQLException, IllegalStateException, SQLSyntaxErrorException 
   {
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );

      // specify query and execute it
      try
      {
    	  resultSet = statement.executeQuery( query );
  
    	  // obtain meta data for ResultSet
    	  metaData = resultSet.getMetaData();

    	  // determine number of rows in ResultSet
    	  resultSet.last();                   // move to last row
      	  numberOfRows = resultSet.getRow();  // get row number      
      }
      
      catch(Exception e)
      {
    	  // notify JTable that model has changed
    	  fireTableStructureChanged();
    	  set_error_message(e.getMessage());
    	  e.printStackTrace();
      }
   } // end method setQuery


// set new database update-query string
   public void setUpdate( String query ) 
      throws SQLException, IllegalStateException, SQLSyntaxErrorException 
   {
	  int res;
      // ensure database connection is available
      if ( !connectedToDatabase ) 
         throw new IllegalStateException( "Not Connected to Database" );
      
      try
      {
    	// specify query and execute it
         res = statement.executeUpdate( query ); 
         set_error_message("Update/Insert completed!");
      }
      
/*
      // obtain meta data for ResultSet
      metaData = resultSet.getMetaData();
      // determine number of rows in ResultSet
      resultSet.last();                   // move to last row
      numberOfRows = resultSet.getRow();  // get row number      
*/    
      catch (Exception e)
      {
    	  e.printStackTrace();
    	  set_error_message(e.getMessage());
    	  // notify JTable that model has changed
          fireTableStructureChanged();
      }
      
   } // end method setUpdate
   
   // sets the error message recieved from mysql
   public void set_error_message(String err_message)
   {
	   this.err_message = err_message;
   }
   
   // used by SqlGUI to display the message to the user
   public String get_error_message()
   {
	   return this.err_message;
   }

   // close Statement and Connection               
   public void disconnectFromDatabase()            
   {              
      if ( !connectedToDatabase )                  
         return;
      // close Statement and Connection            
      try                                          
      {                                            
         statement.close();                        
         connection.close();                       
      } // end try                                 
      catch ( SQLException sqlException )          
      {                                            
         sqlException.printStackTrace();           
      } // end catch                               
      finally  // update database connection status
      {                                            
         connectedToDatabase = false;              
      } // end finally                             
   } // end method disconnectFromDatabase          
}  // end class ResultSetTableModel





