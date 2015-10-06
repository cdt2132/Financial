/*Caroline Trimble*/
/*Financial Software Systems*/
/*GUI Display for Trade Capture System using swing */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import javax.swing.*;
import java.util.Calendar; 
import java.text.DateFormat; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class Display {
	
	public static void main(String[] args) {
		JButton button = new JButton(); 
		button.setText("Generate a Report"); 
		button.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int result = JOptionPane.showOptionDialog(null, 
        				"What type of report would you like to produce?", 
        				"Feedback", 
        				JOptionPane.OK_CANCEL_OPTION, 
        				JOptionPane.INFORMATION_MESSAGE, 
        				null, 
        				new String[]{"CSV of Trades Entered", "CSV Showing Aggregate Positions"}, // this is the array
        				"default");
					if(result == 0){
						System.out.println("CSV of Trades Entered");
					}
					if(result == 1){
						System.out.println("CSV Showing Aggregate Positions");
					}
					}
				}
			);
		JTextField symbol = new JTextField();
		JTextField contractEx = new JTextField();
		JTextField lots = new JTextField();
		JTextField price = new JTextField();
		String[] items = {"Buy", "Sell"};
		JComboBox<String> combo = new JComboBox<String>(items);
		JTextField trader = new JTextField();
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(button);
		panel.add(new JLabel("Symbol (e.g. HH):"));
		panel.add(symbol);
		panel.add(new JLabel("Contract Expiry (e.g. JUL 16):"));
		panel.add(contractEx);
		panel.add(new JLabel("Lots:"));
		panel.add(lots);
		panel.add(new JLabel("Price:"));
		panel.add(price);
		panel.add(combo);
		panel.add(new JLabel("Trader"));
		panel.add(trader);

		int result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture System",
        	JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    		while(result != JOptionPane.CANCEL_OPTION){
    			if (result == JOptionPane.OK_OPTION) {
        			if(symbol.getText().equals("") || 
					contractEx.getText().equals("") || 
					lots.getText().equals("") || 
					price.getText().equals("") || 
					trader.getText().equals("")){
            			JOptionPane.showMessageDialog(panel, "Please enter data into all fields.");
            			result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture   System",
            			JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        			}
        			else{	
        				int l = 0; 
					double p = 0.0; 
					try {
   						 l = Integer.parseInt(lots.getText());
  					} catch (NumberFormatException e) {
    					JOptionPane.showMessageDialog(panel, "Please enter an integer for lots."); 
  					}
					try{
						p = Double.parseDouble(price.getText());
					} catch(NumberFormatException e){
						JOptionPane.showMessageDialog(panel, "Please enter number for price.");
					}
					Order o = new Order(symbol.getText(), contractEx.getText(), l, p, (String)combo.getSelectedItem(), trader.getText()); 
					o.printOrder(); 
			

        				symbol.setText(""); 
    					contractEx.setText("");
    					lots.setText("");
    					price.setText(""); 
    					trader.setText("");
        				result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture System",
        				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    				}
    			}

		}
	}
}

