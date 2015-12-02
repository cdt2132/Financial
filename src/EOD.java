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
				// flag matured swaps
				DatabaseManager.getInstance().swapFlag(TradeCapture.CURRENT_DATE);
				//generate updated report
				DatabaseManager.getInstance().swapMaturingTodayTrades(".");
				DatabaseManager.getInstance().swapAggregate(".");
				DatabaseManager.getInstance().swapAllTrades(".");
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DatabaseManager.getInstance().swapFlag(TradeCapture.CURRENT_DATE);

			System.out.println("Current_Date:"+TradeCapture.CURRENT_DATE);
			System.out.println("Next Business Day:"+ TradeCapture.nextBusinessDay());

		}

	}

}
