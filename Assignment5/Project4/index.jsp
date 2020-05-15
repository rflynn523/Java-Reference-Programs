<%-- Name: Ryan Flynn
     Course: CNT 4714 - Spring 2020 - Project Four
     Assignment Title: A Three-Tier Distributed Web-Based Application
     Date: April 5, 2020
--%>

<!DOCTYPE html>
<!-- Project 4 Java Servlet Page -->
<%-- start sciplet --%>
<%
  // String to pass to the java page.
  String sql_command = (String) session.getAttribute("sqlcommand");
  if(sql_command == null)
    sql_command = "";

  // String to set the table
  String table = (String) session.getAttribute("table");
  if(table == null)
    table = "There is nothing in table.";
%>

<html lang = "en">
<head>
  <title>Project 4 Servlet</title>
  <link rel = "stylesheet" href = "index.css">
    <script>
        function resetForm()
        {
            document.getElementById('textBox').innerHTML = "";
        }
    </script>
</head>
<body>
  <h1>Welcome to the Spring 2020 Project 4 Enterprise System<br><br>
  A Remote Database Management System</h1>
  <hr>
  <p>
    You are connected to the Project 4 Enterprise System database.<br>
    Please enter any valid SQL query or update statement.<br>
    If no query/update command is initial provided the Execute button will
    display all supplier information in the database.<br>
    All execution will appear below.<br>
  </p>

  <!-- Buttons -->
  <form action = "/Project4/Project4Servlet" method = "post">
    <br>
    <textarea id="textBox" name= "sqlcommand" placeholder="Enter any SQL Command" rows="10" cols="50"><%=sql_command%></textarea>
    <br><br>
    <input type ="submit" value = "Execute Command"></input>
    <input type= "reset" onclick="resetForm()" value = "Reset Form" ></input>
  </form>
  <hr>

  <%-- Table Results area --%>
  <h2>Database Results:</h2>
  <p id="table_place"><p>
  <div>
    <%= table %>
  </div>
</body>
</html>
