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
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.StringWriter;

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
	public void printSwap() {
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
	
	/**
	 * Generate FpML message for swap trade
	 * Uses example from www.mkyong.com
	 * @return FpML Message
	 */
	public String createFpMLMessage() {
		
		try {
			
			String filepath ="./bin/RequestConsent.xml";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			
			Node startDate = doc.getElementsByTagName("startDate").item(0);
			if(startDate != null) startDate.setTextContent(startDay + "/" + startMonth + "/" + startYear);
			
			Node terminationDate = doc.getElementsByTagName("terminationDate").item(0);
			if (terminationDate != null) terminationDate.setTextContent(termDay + "/" + termMonth + "/" + termYear);
			
			Node whoFloat = doc.getElementsByTagName("whoFloat").item(0);
			if (whoFloat != null) whoFloat.setTextContent(PayerFloat);
			
			Node whoFixed = doc.getElementsByTagName("whoFixed").item(0);
			if (whoFixed != null) whoFixed.setTextContent(PayerFixed);
			
			Node floatingRate = doc.getElementsByTagName("floatingRate").item(0);
			if (floatingRate != null) floatingRate.setTextContent(Double.toString(floatRate));
			
			Node floatingRateSpread = doc.getElementsByTagName("floatingRateSpread").item(0);
			if (floatingRateSpread != null) floatingRateSpread.setTextContent(Double.toString(floatRateSpread));
			
			Node fRate = doc.getElementsByTagName("fixedRate").item(0);
			if (fRate != null) fRate.setTextContent(Double.toString(fixedRate));
	
			StringWriter sw = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
		    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        System.out.println(sw.toString());
	        return sw.toString();
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
		
	}
	
}
