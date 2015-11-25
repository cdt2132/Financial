/**
 * Swap Order
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 *
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Swap {
	
	/** Start month of swap*/
	int startMonth;
	/** Start year of swap*/
	int startYear;
	/** Termination month of swap*/
	int termMonth;
	/** Termination year of swap*/
	int termYear;
	/** Floating rate*/
	double floatRate;
	/** Floating rate spread*/
	double floatRateSpread;
	/** Fixed rate*/
	double fixedRate;
	/** 1 = pays fixed rate
	 *  -1 = pays floating rate
	 */
	int fixedOrfloat;
	Date date;
	
	public Swap(int sMonth, int sYear, int tMonth, int tYear, double flRate, double spread, double fiRate) {
		startMonth = sMonth;
		startYear = sYear;
		termMonth = tMonth;
		termYear = tYear;
		floatRate = flRate;
		floatRateSpread = spread;
		fixedRate = fiRate;
		date = new Date();
	}
	
	void printSwap() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.format(date));
		System.out.println(startMonth   + " "
				 + startYear + " " 
				 + termMonth  + " "
				 + termYear     + " "
				 + floatRate    + " "
				 + floatRateSpread  + " "
				 + fixedRate);
	}
	
	
	
}
