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
<<<<<<< HEAD
	
	/** Start day of swap*/
	int startDay; 
=======
	/** Start day of swap */
	int startDay;
>>>>>>> f96fcd41573cfe6e97eacfbb1a36b3b04b51262c
	/** Start month of swap*/
	int startMonth;
	/** Start year of swap*/
	int startYear;
<<<<<<< HEAD
	/** Termination day of swap*/
	int termDay; 
=======
	/** Termination day of swap */
	int termDay;
>>>>>>> f96fcd41573cfe6e97eacfbb1a36b3b04b51262c
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
	/** the number of the trader */ 
	int trader; 
	int fixedOrfloat;
	
	Date date;
	
<<<<<<< HEAD
	public Swap(int sDay, int sMonth, int sYear, int tDay, int tMonth, int tYear, double flRate, double spread, double fiRate, int t) {
		startDay = sDay; 
		startMonth = sMonth;
		startYear = sYear;
		termDay = tDay; 
=======
	public Swap(int sDay, int sMonth, int sYear, int tDay, int tMonth, int tYear, double flRate, double spread, double fiRate, int fixorfloat) {
		startDay = sDay;
		startMonth = sMonth;
		startYear = sYear;
		termDay = tDay;
>>>>>>> f96fcd41573cfe6e97eacfbb1a36b3b04b51262c
		termMonth = tMonth;
		termYear = tYear;
		floatRate = flRate;
		floatRateSpread = spread;
		fixedRate = fiRate;
<<<<<<< HEAD
		int trader = t; 
		date = new Date();
=======
		fixedOrfloat = fixorfloat;
>>>>>>> f96fcd41573cfe6e97eacfbb1a36b3b04b51262c
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
