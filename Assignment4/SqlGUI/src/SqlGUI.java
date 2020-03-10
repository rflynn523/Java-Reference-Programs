/*
 * Name: Ryan Flynn
 * Course: CNT 4714 Spring 2020
 * Assignment title: Project Three: Two-Tier Client-Server Application Development With MySQL and JDBC
 * Date: March 5, 2020
 */

import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;
import java.sql.*;

public class SqlGUI implements ActionListener
{
	// Windows
	JFrame win;
	JFrame message_win;
	
	// Button variables
	JButton btn_connect;
	JButton btn_clear_sql;
	JButton btn_execute;
	JButton btn_clear_results;
	
	// Table for the Result Area
	JScrollPane result_area;
	
	// Text fields
	JTextField connection_label;
	
	JComboBox drivers_dropdown;
	JComboBox url_dropdown;
	JTextField txt_username;
	JTextField txt_password;
	
	JTextArea sql_command;
	
	// Connection
	Connection connection;
	
	// Driver
	public static void main(String []args) throws SQLException, ClassNotFoundException
	{
		new SqlGUI();	
	}
	
	// Makes the GUI and sets up the buttons
	public SqlGUI() 
	{
		win = new JFrame("SqlGUI");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Make the upper section of the GUI that includes the sign-in area,
		// the SQL command area, and the middle buttons.
		win.add(this.make_upper(), BorderLayout.NORTH);
				
		// Create the single Clear Result Button on the bottom of the GUI
		btn_clear_results = new JButton("Clear Result Window");
		btn_clear_results.setActionCommand("Clear Results");
		btn_clear_results.addActionListener(this);
		
		btn_clear_results.setBackground(Color.YELLOW);
		btn_clear_results.setForeground(Color.BLACK);
		
		win.add(btn_clear_results, BorderLayout.SOUTH);
		
		// Set the initial size
		win.setSize(1100, 550);
        win.setVisible(true);
	}
	
	// Handles the actions with each button press and calls the appropriate method
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		
		switch(command)
		{
			case "Connect":
				establishConnection();
				break;
			case "Clear Command":
				sql_command.setText("");
				break;
			case "Clear Results":
				clear_results();
				break;
			case "Execute":
				executeCommand();
				break;
		}
		
	}
	
	// Executes the command that is entered into the Sql command area
	private void executeCommand()
	{
		String query = sql_command.getText();
		String command = query.split(" ", 2)[0];
		
		ResultSetTableModel results = null;
		try
		{
			clear_results();
			
			results = new ResultSetTableModel(connection, query);
			
			result_area = new JScrollPane(new JTable(results));
			
			win.add(result_area, BorderLayout.CENTER);
			win.setVisible(true);
		}
		
		// Displays the error message or completion messages to a pop up window
		catch(Exception e) 
		{
			display_popup(results.get_error_message());
		}
	}
	
	// Gets the connection with the user's selections, username, and password
	private void establishConnection()
	{
		try
		{
			// Grab the Driver name from the drop down
			Class.forName((String)drivers_dropdown.getSelectedItem());
			
			// Grab the url from it's drop down
			String url = (String)url_dropdown.getSelectedItem();
			String username = txt_username.getText(); 
			String password = txt_password.getText();
			
			connection = DriverManager.getConnection(url, username, password);
			connection_label.setText("Connected to " + (String)url_dropdown.getSelectedItem());
		}
		
		catch(Exception e) 
		{
			connection_label.setText("			No Connection Now				");
			e.printStackTrace();
		}
	}
	
	// Clears the result area
	private void clear_results()
	{
		if(result_area != null)
		{
			win.remove(result_area);
			win.repaint();
		}
	}
	
	// Used to display a popup message with the string passed to it.
	private void display_popup(String message)
	{
		message_win = new JFrame("Message");
		
		message_win.setSize(400, 200);
		message_win.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		// Adds the message to the winow
		JLabel err_mes = new JLabel(message, SwingConstants.CENTER);
		
		// Add an "OK" button that also closes the window
		JButton ok = new JButton("OK");
		ok.setActionCommand("OK");
	
		ok.addActionListener(new java.awt.event.ActionListener() 
		{
	        public void actionPerformed(java.awt.event.ActionEvent evt)
	        {
				message_win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
	        }
	    });
					
		message_win.add(err_mes);
		message_win.add(ok, BorderLayout.SOUTH);
		
		message_win.setVisible(true);
		
		System.out.println(message);
		
	}
	
	// Flow Layout is horizontal, Box Layout is vertical
	// Makes the upper section of the GUI with the label and text fields in a spring layout and then adds it to a box layout.
	private Box make_upper()
	{
		JPanel inputs = new JPanel(new SpringLayout());
		
		// Drop Down for JBDC Driver
		JLabel driver = new JLabel("JDBC Driver:", JLabel.TRAILING);
		inputs.add(driver);
		
		String drivers_string[] = {"com.mysql.cj.jdbc.Driver", "Example", "Ryan Flynn"};
		drivers_dropdown = new JComboBox(drivers_string);

		driver.setLabelFor(drivers_dropdown);
		inputs.add(drivers_dropdown);
		
		// Drop Down for Database URL
		JLabel url = new JLabel("Database URL:");
		inputs.add(url);
		
		String url_string[] = {"jdbc:mysql://localhost:3312/project3", "jdbc:mysql://localhost:3312/bikedb"};
		url_dropdown = new JComboBox(url_string);
		
		url.setLabelFor(url_dropdown);
		inputs.add(url_dropdown);
		
		// Username text field
		JLabel username = new JLabel("Username:", JLabel.TRAILING);
		txt_username = new JTextField(30);
		
		inputs.add(username);
		username.setLabelFor(txt_username);
		inputs.add(txt_username);	
		
		// Password text field
		JLabel password = new JLabel("Password:", JLabel.TRAILING);
		txt_password = new JPasswordField(30);
		password.setLabelFor(txt_password);
		
		inputs.add(password);
		inputs.add(txt_password);
		
		inputs.setBackground(Color.LIGHT_GRAY);
		
		// Make the grid that holds the inputs together
		// ----------------------------- (Container, rows, cols, initX, initY, xPad, YPad)
		SpringUtilities.makeCompactGrid(inputs, 4, 2, 6, 6, 6, 6);
		
		// Create the SQL command textbox 
		sql_command = new JTextArea("", 1, 50);
		sql_command.setWrapStyleWord(true);
		sql_command.setLineWrap(true);
		
		// Make the box that will hold both the inputs and sql command area
		Box texts = Box.createHorizontalBox();
		texts.add(inputs);
		texts.add(sql_command);
		texts.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Create the vertical box to combine the text inputs and the buttons
		Box upper = Box.createVerticalBox();
		upper.add(texts);
		upper.add(make_buttons());
		
		upper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		return upper;
	}
	
	// Makes the connection status label and the three buttons in the center of the GUI
	private JPanel make_buttons()
	{	
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 2));
		
		// JLabel to display the current connection status to the database
		connection_label = new JTextField("No Connection Now			");
		connection_label.setEditable(false);
		
		connection_label.setBackground(Color.DARK_GRAY);
		connection_label.setForeground(Color.RED);
		connection_label.setFont(new Font("Veranda", Font.BOLD, 16));
		
		buttons.add(connection_label);
		
		// Creating the buttons with ActionListeners and adding to the panel
		// Connect to Database
		btn_connect = new JButton("Connect to Database");
		btn_connect.addActionListener(this);
		btn_connect.setActionCommand("Connect");
		
		btn_connect.setBackground(Color.BLUE);
		btn_connect.setForeground(Color.YELLOW);
		
		buttons.add(btn_connect);
		
		// Clear SQL Command
		btn_clear_sql = new JButton("Clear SQL Command");
		btn_clear_sql.addActionListener(this);
		btn_clear_sql.setActionCommand("Clear Command");
		
		btn_clear_sql.setBackground(Color.WHITE);
		btn_clear_sql.setForeground(Color.DARK_GRAY);
		
		buttons.add(btn_clear_sql);
		
		// Execute SQL Command
		btn_execute = new JButton("Execute SQL Command");
		btn_execute.addActionListener(this);
		btn_execute.setActionCommand("Execute");
		
		btn_execute.setBackground(Color.GREEN);
		btn_execute.setForeground(Color.BLACK);
		
		buttons.add(btn_execute);
		
		return buttons;		
	}
}
