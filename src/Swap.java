/**
 * Swap Order
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 *
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Swap {
	/** Start day of swap*/
	int startDay; 
	/** Start month of swap*/
	int startMonth;
	/** Start year of swap*/
	int startYear;
	/** Termination day of swap*/
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
	/** Payer of Fixed leg*/
	String PayerFixed;
	/** Payer of Float leg*/
	String PayerFloat;
	/** the number of the trader */ 
	int trader; 
	Date date;
	

	/** Constructor of Swap class
	 * @param sDay: startday
	 * @param sMonth: startmonth
	 * @param sYear: startyear
	 * @param tDay: terminal day
	 * @param tMonth: terminal month
	 * @param tYear: terminal year
	 * @param flRate: floatrate
	 * @param spread: floatspread
	 * @param fiRate: fixed rate
	 * @param payerFloat: payer of float leg
	 * @param payerFixed: payer of fixed leg
	 * @param t: trader
	 */
	public Swap(int sDay, int sMonth, int sYear, int tDay, int tMonth, int tYear, double flRate, double spread, double fiRate, String payerFloat, String payerFixed, int t) {
		date = new Date();
		startDay = sDay;
		startMonth = sMonth;
		startYear = sYear;

		termDay = tDay;
		termMonth = tMonth;
		termYear = tYear;
		
		floatRate = flRate;
		floatRateSpread = spread;
		fixedRate = fiRate;

		PayerFixed = payerFixed;
		PayerFloat = payerFloat;
		trader = t; 
	}
	
	/**
	 * Print swap trade
	 */
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
				 + PayerFloat + " "
				 + PayerFixed + " trader"
				 + trader);
	}
	
	

	
}
