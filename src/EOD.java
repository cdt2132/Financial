/**
 EOD thread Class - run every 5 seconds, to check if system need roll the Curren_Date and update reports and flag of matured swaps
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 *
 */

import java.util.Date;

public class EOD implements Runnable {

	public void run() {
		
		// if find system time reached the next business day, roll the Current_Date , and flag the matured trade
		while (true){		
			//if find system time reached the next business day, roll the Current_Date
			if (TradeCapture.nextBusinessDay().compareTo(new Date()) <= 0){
				System.out.println("Roll Current Date!!!!");
				TradeCapture.CURRENT_DATE = TradeCapture.nextBusinessDay();
				// flag matured orders and swaps
				DatabaseManager.getInstance().setFlags(TradeCapture.CURRENT_DATE);
				//generate updated report
				DatabaseManager.getInstance().MaturingTodayTrades(".");
				DatabaseManager.getInstance().swapAggregate(".");
				DatabaseManager.getInstance().swapAllTrades(".");
				DatabaseManager.getInstance().outputTrades(".");
				DatabaseManager.getInstance().outputAggregate(".");
				DatabaseManager.getInstance().outputPnL(".");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// set flags to correct current date
			DatabaseManager.getInstance().setFlags(TradeCapture.CURRENT_DATE);
		}
	}
}
