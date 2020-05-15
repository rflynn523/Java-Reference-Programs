/* Name: Ryan Flynn
     Course: CNT 4714 - Spring 2020 - Project Four
     Assignment Title: A Three-Tier Distributed Web-Based Application
     Date: April 5, 2020
*/

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class Project4Servlet extends HttpServlet 
{   
	// process "get" requests from clients
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException 
    {
   	  // String to pass to the java page.
      String sql_command = request.getParameter("sqlcommand");
      String table;
	  
      if (sql_command.charAt(0) == 's' || sql_command.charAt(0) == 'S')
      {
	      try
	      {
	    	  DatabaseQuery results = new DatabaseQuery(sql_command);
	          table = results.formatSQL();  
	      }
	      
	      catch(Exception e)
	      {
	    	  table = "<p>Error executing the SQL statement:<br>";
	    	  table += e.getMessage() + "</p>";
			  e.printStackTrace();
	      }
      
      }
      
      else
      {
    	  try
    	  {
    		  DatabaseQuery results = new DatabaseQuery(sql_command);
    		  table = results.getUpdateResults();
    	  }
    	  
    	  catch (Exception e)
    	  {
	    	  table = "<p>Error executing the SQL statement:<br>";
	    	  table += e.getMessage() + "</p>";
			  e.printStackTrace();
	      }
      }
      
      HttpSession session = request.getSession();
      session.setAttribute("table", table);
      session.setAttribute("sqlcommand", sql_command);
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
      dispatcher.forward(request, response);      
    }   //end doGet() method
    
    public void doPost(HttpServletRequest request, HttpServletResponse response )throws IOException, ServletException 
    {
    	doGet(request, response);
   	}
   
}