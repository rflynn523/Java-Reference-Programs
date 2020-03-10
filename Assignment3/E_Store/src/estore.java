/* Name: Ryan Flynn
   Course: CNT 4714-Spring 2020
   Assignment title: Project 1 - Event-driven Enterprise Simulation
   Date: Sunday January 26, 2020
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.List;

import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class estore implements ActionListener
{
	JFrame window;
	JFrame message_window;

	HashMap<String, String[]> invo;
	List<String> order;

	Integer num_of_items;
	Double order_sum;
	int current_item;

	// Button instance variables
	JButton process_BTN;
	JButton confirm_BTN;
	JButton view_BTN;
	JButton finish_BTN;
	JButton new_BTN;
	JButton exit_BTN;

	// Text field and text labels instance variables
	JTextField num_items_field;
	JTextField book_id_field;
	JTextField quantity_field;
	JTextField info_field;
	JTextField subtotal_field;

	JLabel book_id;
	JLabel quantity;
	JLabel info;
	JLabel subtotal;


	public static void main(String[] args) throws IOException
	{
		new estore();
	}

	public estore() throws IOException
	{
		this.current_item = 1;
		this.order_sum = 0.0;
		order = new ArrayList<String>();

		load_data();
		initialize_GUI();
	}

	// Method that initially loads the data from the inventory text file which
	// simulates the a database.
	public void load_data() throws IOException
	{
		Scanner in = new Scanner(new File("inventory.txt"));

		invo = new HashMap<String, String[]>();

		String[] temp = new String[41];

		String id;

		// Loops through and loads in the entire input file "inventory.txt"
		while(in.hasNext())
		{
			// Holds each line
			String line = in.nextLine();

			// Splits the line into an array of Strings ex. {"11111", name and price of the book}
			temp = line.split("," , 3);

			// Variable names help me understand what is being put in the hashmap
			id = temp[0];
			String[] name_price = {temp[1], temp[2]};

			invo.put(id, name_price);

		}

		in.close();

	}

	// Simply creates the GUI
	public void initialize_GUI() throws IOException
	{
		// Will just make a basic window
		window = new JFrame("e-store");

		window.setSize(1000, 400);
		window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Add the panel of buttons for the south border
		window.add(this.make_buttons(), BorderLayout.SOUTH);

		// Add the panel of texts and text fields
		window.add(this.make_texts());

		// Show the window
		window.setVisible(true);

	}

	// Helper method to handle the few message windows that pop up
	private void display_message(String message)
	{
		message_window = new JFrame("Message");

		message_window.setSize(400, 200);
		message_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JLabel err_mes = new JLabel(message, SwingConstants.CENTER);
		JButton ok = new JButton("OK");
		ok.setActionCommand("OK");

		ok.addActionListener(new java.awt.event.ActionListener()
		{
	        public void actionPerformed(java.awt.event.ActionEvent evt)
	        {
				message_window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	        }
	    });

		message_window.add(err_mes);
		message_window.add(ok, BorderLayout.SOUTH);

		message_window.setVisible(true);

	}

	public void actionPerformed(ActionEvent e)
	{
	  try
	  {
		String clicked = e.getActionCommand();

		switch(clicked)
		{
			case "Process":
				this.num_of_items = Integer.parseInt(num_items_field.getText());
				process(book_id_field.getText(), Double.parseDouble(quantity_field.getText()));
				break;

			case "Confirm":
				confirm();
				break;

			case "View Order":
				view();
				break;

			case "Finish Order":
				finish();
				break;

			case "New Order":
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				new estore();
				break;

			case "Exit":
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
		}

	  }

	  catch(Exception exc) {exc.printStackTrace();}
	}

	// Looks up the book id and displys the title, price, and discount to the GUI
	private void process( String id, Double quantity)
	{
		// Checks to see if the user entered an id that is in the inventory.
		if (!invo.containsKey(id))
		{
			display_message("Book ID " + id + " is not in file");
			return;
		}

		// Updates the buttons and text fields available
		process_BTN.setEnabled(false);
		confirm_BTN.setEnabled(true);
		num_items_field.setEditable(false);

		// Returns the name of the book along with the price
		String title = invo.get(id)[0];
		Double price = Double.parseDouble(invo.get(id)[1]);
		Double item_total = 0.0;

		// Determines the price with the quantity and the discount
		item_total = price * quantity;

		// Determines the percentage off based on the number of books
		Double perc = 0.0;
		if (quantity >= 5 && quantity <= 9)
			perc = 0.1;
		else if(quantity >= 10 && quantity <= 14)
			perc = 0.15;
		else if(quantity > 14)
			perc = 0.2;

		// Applys the discount
		Double discount = item_total * perc;
		item_total -= discount;
		order_sum += item_total;

		// Displays the info onto the text field
		info_field.setText(id + title + String.format(" $%.2f", price) + " " + quantity.intValue() + " " + (int)(perc*100) + "% " + String.format("$%.2f", item_total));
	}

	// Adds the items to the order ArrayList
	private void confirm()
	{
		view_BTN.setEnabled(true);
		finish_BTN.setEnabled(true);
		subtotal_field.setText(String.format("$%.2f", order_sum));

		book_id_field.setText("");
		quantity_field.setText("");

		// Create pop up window confirming the item just added
		display_message("Item #" + current_item + " accepted!");
		order.add(info_field.getText());

		current_item++;

		// Reset the GUI until the last item was confirmed
		if (current_item <= num_of_items)
		{
			process_BTN.setEnabled(true);
			confirm_BTN.setEnabled(false);
			update_texts();
		}

		// Last item was confirmed
		// User can no longer process or confirm, book_id and quantity labels disappear
		else
		{
			process_BTN.setEnabled(false);
			confirm_BTN.setEnabled(false);

			book_id.setText("");
			quantity.setText("");
		}
	}

	// Displays a new window with a list of the current order when "View Order" is clicked
	private void view()
	{
		JFrame view_window = new JFrame("Current Order");

		view_window.setSize(new Dimension(500, 200));
		view_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel order_items = new JPanel();

		int size = order.size();
		for (int i = 1; i <= size; i++)
			order_items.add(new JLabel(i + ". " + order.get(i - 1), SwingConstants.LEFT));

		JButton ok = new JButton("OK");
		ok.addActionListener(new java.awt.event.ActionListener()
		{
	        public void actionPerformed(java.awt.event.ActionEvent evt)
	        {
				view_window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	        }
	    });

		view_window.add(order_items);
		view_window.add(ok, BorderLayout.SOUTH);
		view_window.setVisible(true);
	}

	// Displays the order summary when "Finish Order" is clicked
	private void finish()
	{
		JFrame order_summary = new JFrame("Order Summary");

		order_summary.setSize(new Dimension(600, 300));
		order_summary.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel text_panel = new JPanel();
		BoxLayout box = new BoxLayout(text_panel, BoxLayout.Y_AXIS);
		text_panel.setLayout(box);

		Date date = new Date();
		SimpleDateFormat invoice_format = new SimpleDateFormat ("MM/dd/yy',' hh:mm:ss a zzz");
		SimpleDateFormat transaction_format = new SimpleDateFormat("ddMMYYYYhhmm");

		text_panel.add(new JLabel("Date: " + invoice_format.format(date)));

		text_panel.add(new JLabel("Number of line items: " + num_of_items.toString()));

		text_panel.add(new JLabel("Item# / ID / Title / Price / Qty / Disc% / Subtotal:"));

		// Add the list of items
		for (int i = 1; i <= num_of_items; i++)
			text_panel.add(new JLabel(i + ". " + order.get(i - 1), SwingConstants.LEFT));

		// Subtotal
		text_panel.add(new JLabel("Order subtotal: " + String.format("$%.2f", order_sum)));

		// Tax Rate
		text_panel.add(new JLabel("Tax Rate: 	6%"));

		// Tax Amount
		Double tax = order_sum * 0.06;
		text_panel.add(new JLabel("Tax Amount: " + String.format("$%.2f", tax)));

		// Overall order total
		Double order_total = order_sum + tax;
		text_panel.add(new JLabel("Order Total: " + String.format("$%.2f", order_total)));

		text_panel.add(new JLabel("Thanks for shopping at the estore!"));

		JButton ok = new JButton("OK");
		ok.addActionListener(new java.awt.event.ActionListener()
		{
	        public void actionPerformed(java.awt.event.ActionEvent evt)
	        {
				order_summary.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	        }
	    });

		text_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		text_panel.setBorder(new EmptyBorder(20, 20, 20, 20));

		order_summary.add(text_panel);
		order_summary.add(ok, BorderLayout.SOUTH);
		order_summary.setVisible(true);

		// WRITE to the transaction file
		try
		{
			FileWriter writer = new FileWriter("transactions.txt", true);
			for (int i = 1; i <= num_of_items; i++)
				writer.write("\n" + transaction_format.format(date) + ", " + order.get(i - 1) + ", " + invoice_format.format(date));

			writer.close();
		}

		catch (IOException e) {e.printStackTrace(); }
	}

	private void update_texts()
	{
		process_BTN.setText("Process Item #" + current_item);
		confirm_BTN.setText("Confirm Item #" + current_item);

		book_id.setText("Enter Book ID for Item #" + current_item + ":");
		quantity.setText("Enter quantity for Item #" + current_item + ":");
		info.setText("Item #" + current_item + " info:");
		subtotal.setText("Order subtotal for " + (current_item - 1) + " item(s):");

	}

	private JPanel make_buttons() throws IOException
	{
		// Make the grid layout with the in-between gaps
		GridLayout grid = new GridLayout(1,6);
		grid.setHgap(25);

		// Make the panel and set it to the layout that was just made
		JPanel buttons = new JPanel();
		buttons.setLayout(grid);

		// Creating the buttons with ActionListeners and adding to the panel
		process_BTN = new JButton("Process Item #" + current_item);
		process_BTN.addActionListener(this);
		process_BTN.setActionCommand("Process");
		buttons.add(process_BTN);

		confirm_BTN = new JButton("Confirm Item #" + current_item);
		confirm_BTN.addActionListener(this);
		confirm_BTN.setEnabled(false);
		confirm_BTN.setActionCommand("Confirm");
		buttons.add(confirm_BTN);

		view_BTN = new JButton("View Order");
		view_BTN.addActionListener(this);
		view_BTN.setEnabled(false);
		buttons.add(view_BTN);

		finish_BTN = new JButton("Finish Order");
		finish_BTN.addActionListener(this);
		finish_BTN.setEnabled(false);
		buttons.add(finish_BTN);

		new_BTN = new JButton("New Order");
		new_BTN.addActionListener(this);
		buttons.add(new_BTN);

		exit_BTN = new JButton("Exit");
		exit_BTN.addActionListener(this);
		buttons.add(exit_BTN);

		buttons.setBackground(Color.BLUE);
		buttons.setPreferredSize(new Dimension(800, 50));

		return buttons;
	}

	public JPanel make_texts()
	{
		GridLayout grid = new GridLayout(5, 2);
		grid.setVgap(10);

		Rectangle r = new Rectangle(150, 20);

		JPanel texts = new JPanel();
		texts.setLayout(grid);
		texts.setBackground(Color.CYAN);

		// Making the labels
		JLabel num_items = new JLabel("Enter number of items in this order:");
		num_items.setHorizontalAlignment(JLabel.CENTER);

		num_items_field = new JTextField();
		num_items_field.setBounds(r);

		book_id = new JLabel("Enter Book ID for Item #" + current_item + ":");
		book_id.setHorizontalAlignment(JLabel.CENTER);

		book_id_field = new JTextField();
		book_id_field.setBounds(r);

		quantity = new JLabel("Enter quantity for Item #" + current_item + ":");
		quantity.setHorizontalAlignment(JLabel.CENTER);

		quantity_field = new JTextField();
		quantity_field.setBounds(r);

		info = new JLabel("Item #" + current_item + " info:");
		info.setHorizontalAlignment(JLabel.CENTER);

		info_field = new JTextField();
		info_field.setBounds(r);
		info_field.setEditable(false);

		subtotal = new JLabel("Order subtotal for " + (current_item - 1) + " item(s):");
		subtotal.setHorizontalAlignment(JLabel.CENTER);

		subtotal_field = new JTextField();
		subtotal_field.setBounds(r);
		subtotal_field.setEditable(false);

		texts.add(num_items);
		texts.add(num_items_field);

		texts.add(book_id);
		texts.add(book_id_field);

		texts.add(quantity);
		texts.add(quantity_field);

		texts.add(info);
		texts.add(info_field);

		texts.add(subtotal);
		texts.add(subtotal_field);

		return texts;

	}
}
