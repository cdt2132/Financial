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
	/** Start day of swap */
	int startDay;
	/** Start month of swap*/
	int startMonth;
	/** Start year of swap*/
	int startYear;
	/** Termination day of swap */
	int termDay;
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
	
	public Swap(int sDay, int sMonth, int sYear, int tDay, int tMonth, int tYear, double flRate, double spread, double fiRate, int fixorfloat) {
		startDay = sDay;
		startMonth = sMonth;
		startYear = sYear;
		termDay = tDay;
		termMonth = tMonth;
		termYear = tYear;
		floatRate = flRate;
		floatRateSpread = spread;
		fixedRate = fiRate;
		fixedOrfloat = fixorfloat;
	}
	
	void printSwap() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.format(date));
		System.out.println(startDay + " "
				 + startMonth   + " "
				 + startYear 	+ " " 
				 + termDay		+ " "
				 + termMonth  	+ " "
				 + termYear     + " "
				 + floatRate    + " "
				 + floatRateSpread  + " "
				 + fixedRate	+ " "
				 + fixedOrfloat);
	}
	
	
	
}
