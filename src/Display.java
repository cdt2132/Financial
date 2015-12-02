
/**
 * Display Class creates the GUI for the Trade Capture System
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.GridLayout;
import javax.swing.*;
import java.util.Date;

import com.sun.media.sound.Toolkit;

import quickfix.ConfigError;

public class Display implements Runnable {

	/** Run display in new thread */
	public void run() {

		// DatabaseManager used to query MySQL database
		final DatabaseManager db = DatabaseManager.getInstance();
		int initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
				new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" }, "default");
		while(initial != 0){
		if (initial == 1) {
			JPanel report = new JPanel(new GridLayout(0, 1));
			
			//Category of Report 
			String[] category = {"Future", "Swap"};
			JComboBox<String> categories = new JComboBox<String>(category);
			
			report.add(new JLabel("Product Type (for Trades Entered and Aggregate Positions)"));
			report.add(categories);
			
			int result = JOptionPane.showOptionDialog(null, report, "What type of report would you like to produce?",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
					new String[] { "Trades Entered", "Trades Expiring Today", "Aggregate Positions", "PnL Report" },
					"default");
			

			// CSV of trades entered
			if (result == 0) {
				if(categories.getSelectedItem().equals("Future")){
					//REPORT FOR FUTURE
					// New save dialog object created; displays window
					// for user to pick a directory to save CSV report
					SaveDialog s = new SaveDialog();
					// Saves the filename
					String filename = s.getName();

					System.out.println("CSV of Trades Entered - Futures");
					db.outputTrades(filename);
				}
				else{
					//REPORT FOR SWAP
					SaveDialog s = new SaveDialog();
					String filename = s.getName(); 
					
					System.out.println("CSV of Trades Entered - Swaps");
					db.swapAllTrades(filename);
				}
			}
			
			if (result == 1){
				//trades expiring today (both futures and swaps)
				
				SaveDialog s = new SaveDialog(); 
				
				String filename = s.getName(); 
				System.out.println("CSV of trades expiring today");
				
				db.MaturingTodayTrades(filename); 
			}

			// CSV of aggregate positions
			if (result == 2) {
				if(categories.getSelectedItem().equals("Future")){
					//REPORT FOR FUTURE
					SaveDialog s = new SaveDialog();

					// Saves the filename
					String filename = s.getName();
				
					System.out.println("CSV Showing Aggregate Positions -- Future");
					db.outputAggregate(filename);
				}
				else{
					//REPORT FOR SWAP
					
					SaveDialog s = new SaveDialog(); 
					
					String filename = s.getName(); 
					System.out.println("CSV Showing Aggregate Positions -- Swap ");
					
					db.swapAggregate(filename);
				}
			}
			//PnL report 
			if (result == 3) {
			
					SaveDialog s = new SaveDialog();

					String filename = s.getName();

					System.out.println("PnL Report -- Future");
					db.outputPnL(filename);	
				
			}
			initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
					new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" },
					"default");
		} else if (initial == 3) {

			// CME commodity symbol
			JTextField symbol = new JTextField();

			// Month of expiry
			Integer[] months = { 1, 2, 3, 4, 5, 6, 7, 8, 8, 10, 11, 12 };
			JComboBox<Integer> month = new JComboBox<Integer>(months);

			// Year of expiry
			Integer[] years = { 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025, 2026 };
			JComboBox<Integer> year = new JComboBox<Integer>(years);

			// # of lots and price
			JTextField lots = new JTextField();
			JTextField price = new JTextField();

			// Buy or Sell
			String[] items = { "Buy", "Sell" };
			JComboBox<String> buySell = new JComboBox<String>(items);

			// Type of Order
			String[] orders = { "Market", "Limit", "Pegged" };
			JComboBox<String> orderT = new JComboBox<String>(orders);

			// Each trader will be assigned a unique id
			JTextField trader = new JTextField();

			// Create fields and comboboxes
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(new JLabel("Symbol (e.g. AA):"));
			panel.add(symbol);
			panel.add(new JLabel("Contract Expiry Month:"));
			panel.add(month);
			panel.add(new JLabel("Contract Expiry Year:"));
			panel.add(year);
			panel.add(new JLabel("Lots:"));
			panel.add(lots);
			panel.add(new JLabel("Price(Limit Order) or Offset(Pegged Order):"));
			panel.add(price);
			panel.add(buySell);
			panel.add(new JLabel("Order Type:"));
			panel.add(orderT);
			panel.add(new JLabel("Trader"));
			panel.add(trader);
			// Adds a label to each and then adds to panel

			int result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture System",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			// Users can enter multiple trades
			if (result == JOptionPane.CANCEL_OPTION)
			{
				symbol.setText("");
				lots.setText("");
				price.setText("");
				trader.setText("");
				initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
						new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" },
						"default");
			}
			if (result == JOptionPane.OK_OPTION) {

					// Ensure that all fields are completed
					if (symbol.getText().equals("") || lots.getText().equals("") || trader.getText().equals("")) {
						JOptionPane.showMessageDialog(panel, "Please enter data into all fields.");
						result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture System",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					} else {

						// int l for lots and double p for price
						int l = 0;
						double p = 0.0;

						// Type checking
						try {
							l = Integer.parseInt(lots.getText());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(panel, "Please enter an integer for lots.");
						}

						try {
							db.getMarketPrice(symbol.getText());
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(panel, "Wrong symbol!");
						}

						if (orderT.getSelectedItem().equals("Market") == true) {
							p = 0;
						} else {
							try {
								p = Double.parseDouble(price.getText());
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(panel, "Please enter number for price.");
							}
						}

						int b = ((String) buySell.getSelectedItem()).equals("Buy") ? 1 : -1;
						int ot;
						if (((String) orderT.getSelectedItem()).equals("Market") == true) {
							ot = 0;
						} else if (((String) orderT.getSelectedItem()).equals("Limit") == true)
							ot = 1;
						else
							ot = 2;
						// Create a new order and insert into DB
						Order o = new Order(symbol.getText(), (Integer) month.getSelectedItem(),
								(Integer) year.getSelectedItem(), l, p, b, ot, Integer.parseInt(trader.getText()));
						o.printOrder();
						try {
							o.sendOrdertoExchange();
						} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// Resets GUI
						symbol.setText("");
						lots.setText("");
						price.setText("");
						trader.setText("");
						initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
								new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" },
								"default");
					}
				}
			}
		else if (initial == 2){
			//Day of start date
			Integer[] d = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
			JComboBox<Integer> startDay = new JComboBox<Integer>(d);
			
			//Month of start date
			Integer[] m= {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
			JComboBox<Integer> startMonth = new JComboBox<Integer>(m);
			
			// Year of start date 
			Integer[] years = { 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025, 2026 };
			JComboBox<Integer> startYear = new JComboBox<Integer>(years);
			
			//Day of termination date
			JComboBox<Integer> termDay = new JComboBox<Integer>(d);
			
			//Month of start date
			JComboBox<Integer> termMonth = new JComboBox<Integer>(m);
			
			// Year of start date 
			JComboBox<Integer> termYear = new JComboBox<Integer>(years);
			
			//floating rate 
			JTextField rate = new JTextField();
			
			//spread on floating rate 
			JTextField spread = new JTextField(); 
			
			//Fixed rate
			JTextField fixedRate = new JTextField(); 
			
			//Who pays fixed leg?
			String[] fix = {"Me", "CME", "LCH"};
			JComboBox<String> whoFix = new JComboBox<String>(fix);
			
			//Who pays float leg?
			String[] floats = {"Me", "CME", "LCH"};
			JComboBox<String> whoFloat = new JComboBox<String>(floats);
			
			// Each trader will be assigned a unique id
			JTextField trader = new JTextField();

			// Create fields and comboboxes
			JPanel swap = new JPanel(new GridLayout(0, 1));
			swap.add(new JLabel("Day of Start Date"));
			swap.add(startDay); 
			swap.add(new JLabel("Month of Start Date"));
			swap.add(startMonth);
			swap.add(new JLabel("Year of Start Date"));
			swap.add(startYear);
			swap.add(new JLabel("Month of Termination Date"));
			swap.add(termMonth);
			swap.add(new JLabel("Year of Termination Date"));
			swap.add(termYear);
			swap.add(new JLabel("Floating Rate (Decimal Form)")); 
			swap.add(rate); 
			swap.add(new JLabel("Spread on Floating Rate (Decimal Form)"));
			swap.add(spread);
			swap.add(new JLabel("Fixed Rate (Decimal Form)")); 
			swap.add(fixedRate);
			swap.add(new JLabel("Who pays fixed leg?"));
			swap.add(whoFix);
			swap.add(new JLabel("Who pays float leg?"));
			swap.add(whoFloat);
			swap.add(new JLabel("Trader:")); 
			swap.add(trader); 
			
			//Puts up ok or cancel 
			int result = JOptionPane.showConfirmDialog(null, swap, "Trade Capture System",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			if (result == JOptionPane.CANCEL_OPTION){
				rate.setText("");
				spread.setText("");
				fixedRate.setText("");
				trader.setText("");
				initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
						new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" },
						"default");
			}
			
			if (result == JOptionPane.OK_OPTION) {

					// Ensure that all fields are completed
					if (rate.getText().equals("") || spread.getText().equals("") || fixedRate.getText().equals("") || trader.getText().equals("")) {
						JOptionPane.showMessageDialog(swap, "Please enter data into all fields.");
						result = JOptionPane.showConfirmDialog(null, swap, "Trade Capture System",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					} else {
						double r = 0.0; 
						double s = 0.0; 
						double fr = 0.0; 
						int t = 0; 
						// Type checking
						
						
						
						try {
							r = Double.parseDouble(rate.getText());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(swap, "Please enter a decimal for floating rate.");
						}
						
						// Type checking
						try {
							s = Double.parseDouble(spread.getText());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(swap, "Please enter a decimal for spread on floating rate.");
						}
						
						// Type checking
						try {
							fr = Double.parseDouble(fixedRate.getText());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(swap, "Please enter a decimal for fixed rate.");
						}
						
						//Type checking 
						try{
							t = Integer.parseInt(trader.getText()); 
						}
						catch (NumberFormatException e){
							JOptionPane.showMessageDialog(swap, "Please enter an integer for trader");
						}
						/* Getting information from drop down menus */ 
						int sd = (Integer) startDay.getSelectedItem(); 
						int sm = (Integer) startMonth.getSelectedItem(); 
						int sy = (Integer) startYear.getSelectedItem(); 
						int tm = (Integer) termMonth.getSelectedItem(); 
						int ty = (Integer) termYear.getSelectedItem(); 
						
						Date termDate = TradeCapture.getThirdBeforeEOM(tm, ty);
						int td = termDate.getDay();

						/* Creating a swap object */ 
						Swap o = new Swap(sd, sm, sy, td, tm, ty, r, s, fr, whoFloat.getSelectedItem().toString(),whoFix.getSelectedItem().toString(),t);
						o.printSwap();
						/*try {
							o.sendOrdertoExchange();
						} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
						db.insertSwap(o);
						// Resets GUI
						rate.setText("");
						spread.setText("");
						fixedRate.setText("");
						trader.setText("");
						initial = JOptionPane.showOptionDialog(null, "Welcome to Trade Capture", "Feedback",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
								new String[] { "Cancel", "Produce a Report", "Record a Swap", "Record a Future or Option Trade" },
								"default");
								
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Display d = new Display();
		d.run();
	}
}
