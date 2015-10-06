/*Caroline Trimble*/
/*Financial Software Systems*/
/*GUI Display for Trade Capture System using swing */

import java.awt.GridLayout;
import javax.swing.*;
import java.util.Calendar; 
import java.text.DateFormat; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class Display {
	
	public static void main(String[] args) {
		JTextField symbol = new JTextField();
		JTextField contractEx = new JTextField();
		JTextField lots = new JTextField();
		JTextField price = new JTextField();
		String[] items = {"Buy", "Sell"};
		JComboBox<String> combo = new JComboBox<String>(items);
		JTextField trader = new JTextField();
		JPanel panel = new JPanel(new GridLayout(0, 1));
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
        if(symbol.getText().equals("") || contractEx.getText().equals("") || lots.getText().equals("") || price.getText().equals("") || trader.getText().equals("")){
            JOptionPane.showMessageDialog(panel, "Please enter data into all fields.");
            result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture   System",
            		JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
        else{	
        	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        	Calendar cal = Calendar.getInstance();
        	System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
        	System.out.println(symbol.getText() + " "
        			+ contractEx.getText() + " " 
        			+ lots.getText() + " " 
        			+ combo.getSelectedItem() + " " 
        			+ price.getText() + " " 
        			+ trader.getText());
        }
        symbol.setText(""); 
    	contractEx.setText("");
    	lots.setText("");
    	price.setText(""); 
    	trader.setText("");
        result = JOptionPane.showConfirmDialog(null, panel, "Trade Capture   System",
        		JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    	}
  
    	}
	}
}


