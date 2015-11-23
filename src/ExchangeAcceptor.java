/**
 A dummy exchange acceptor, which receives order and response with execution report:
 *  Mechanism: one in five limit orders are partially filled, and the rest are fully filled;
 *  Market order is filled by market price
 *  Pegged order is filled by market price plus or minus the offset 
 *  
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */

import java.sql.SQLException;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.ClientID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.PegDifference;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

public class ExchangeAcceptor extends MessageCracker implements Application {

	// Current order ID 
	private int orderIDCounter;
	
	// Current execution ID 
	private int executionIDCounter;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	@Override
	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		//System.out.println("(ExchangeAcceptor) fromAdmin: " + arg0);
	}

	@Override
	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		System.out.println("(ExchangeAcceptor) Received Message: " + message);
		crack(message, sessionID);

	}

	@Override
	public void onCreate(SessionID arg0) {
	}

	@Override
	public void onLogon(SessionID sessionID) {
		System.out.println("onLogon of "+sessionID);
	}

	@Override
	public void onLogout(SessionID arg0) {

	}

	@Override
	public void toAdmin(Message arg0, SessionID arg1) {
	}

	@Override
	public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
	}
	
	
	/** After receive the order, execute the order and send back to initiator the execution reports
	 * @param order NewOrderSingle type
	 * @param sessionID current FIX sessionId
	 * @throws SQLException 
	 */
	public void onMessage(NewOrderSingle order, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue, SQLException {
		
		System.out.print("(ExchangeAcceptor) Received a new order: ");
		System.out.println(order.toString());
		// Order and session information
		Symbol symbol = new Symbol();
		Side side = new Side();
		OrdType ordType = new OrdType();
		OrderQty orderQty = new OrderQty();
		Price price = new Price();
		ClOrdID clOrdID = new ClOrdID();
		ClientID clientID = new ClientID();
		MaturityMonthYear expdate = new MaturityMonthYear();
		
		// Extract Order Information
		order.get(symbol);
		order.get(side);
		order.get(orderQty);
		order.get(price);
		order.get(clOrdID);
		order.get(ordType);
		order.get(expdate);
		order.get(clientID);
		System.out.println("(ExchangeAcceptor) Extracted order information");
		
		System.out.print("(ExchangeAcceptor) Symbol: " + symbol.getValue());
		
		double marketprice = Market.genMarketDatafromDB(symbol.getValue());
		double limitprice = price.getValue();
		double qty = orderQty.getValue();
		double marketsize = Market.getMarketSize(symbol.getValue());
				
		// Execute limit order
		if (ordType.getValue() == ordType.LIMIT){
			System.out.println("(ExchangeAcceptor) Limit order type");
			// if the limit order is within 10% variation of market price, full fill the order
			if (limitprice>=marketprice*0.9 &&  limitprice<=marketprice*1.1 && qty <= marketsize/2){
				System.out.println("(ExchangeAcceptor) Sending fully filled limit order");
				sendExecutionReport(sessionID,limitprice,qty,false,symbol,side,clOrdID,clientID,expdate);
			}
			// if limit price is 10% - 20% change and qty < marketsize, fill half of order CHANGE THIS
			else if (limitprice>=marketprice*0.8 &&  limitprice<=marketprice*1.2 && qty <= marketsize){
				System.out.println("(ExchangeAcceptor) Sending partially filled limit order");
				sendExecutionReport(sessionID,limitprice,qty/2,true,symbol,side,clOrdID,clientID,expdate);
			}
			// if difference between limit price and market price exceeds 20%, order is not executed and canceled
			else{
				System.out.println("(ExchangeAcceptor) Cancelling limit order");		
			}
		}
		
		// Execute market order
		if (ordType.getValue() == ordType.MARKET){
			System.out.println("(ExchangeAcceptor) Market order type");
			if (qty <= marketsize/2) {
				System.out.println("(ExchangeAcceptor) Sending fully filled market order");
				sendExecutionReport(sessionID,marketprice,qty,false,symbol,side,clOrdID,clientID,expdate);
			} else if (qty <= marketsize){
				// fill half of order (partial fill)
				System.out.println("(ExchangeAcceptor) Sending partially filled market order");
				sendExecutionReport(sessionID,marketprice,qty/2,false,symbol,side,clOrdID,clientID,expdate);
			} else {
				System.out.println("(ExchangeAcceptor) Cancelling market order");
			}
		}
		
		// Execute pegged order
		if (ordType.getValue() == ordType.PEGGED){
			System.out.println("(ExchangeAcceptor) Pegged order type");
			PegDifference offset = new PegDifference();
			order.get(offset);	
			double pegprice = marketprice;
			// calculate marketprice based on offset
			if (side.getValue() == side.BUY) pegprice = Math.max(marketprice - offset.getValue(),0);
			if (side.getValue() == side.SELL) pegprice = marketprice + offset.getValue();
			sendExecutionReport(sessionID,pegprice,qty,false,symbol,side,clOrdID,clientID,expdate);
		}
	}
	
	
	/** A counter to record order ID*/
	public OrderID getOrderIDCounter() {
		orderIDCounter++;
		return new OrderID(String.valueOf(orderIDCounter));
	}
	
	/** A counter to record execution ID*/
	public ExecID getExecutionIDCounter() {
		executionIDCounter++;
		return new ExecID(String.valueOf(executionIDCounter));
	}
	
	
	/** This function generate and send execution report
	 * @param sessionID 
	 * @param price Execution price
	 * @param qty Execution quantity
	 * @param ispartialfill partial fill or full fill
	 * @param symbol Symbol
	 * @param side Buy or Sell
	 * @param clOrdID OrderID
	 * @param clientID ClientID
	 * @param expdate Expiration Year and Month
	 */
	public void sendExecutionReport(SessionID sessionID, double price, double qty, boolean ispartialfill,Symbol symbol, Side side,ClOrdID clOrdID,ClientID clientID,MaturityMonthYear expdate ){

		// create new execution report
		ExecutionReport executionReport = new ExecutionReport(
				getOrderIDCounter(), getExecutionIDCounter(),
				new ExecTransType(ExecTransType.NEW), new ExecType(
						ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
				symbol, side, new LeavesQty(0),
				new CumQty(qty), new AvgPx(price));
		
		if (ispartialfill){
			executionReport.set(new ExecType(ExecType.PARTIAL_FILL));
			executionReport.set(new OrdStatus(OrdStatus.PARTIALLY_FILLED));
		}
		
		// Set execution report fields
		executionReport.set(symbol);
		executionReport.set(side);
		executionReport.set(new OrderQty(qty));
		executionReport.set(new Price(price));
		executionReport.set(clOrdID);
		executionReport.set(expdate);
		executionReport.set(clientID);
		
		// send execution report
		try {
			Session.sendToTarget(executionReport, sessionID);
			System.out.println("Execution Report sent----->>>>>");
			System.out.println(executionReport.toString());
		} catch (SessionNotFound ex) {
			ex.printStackTrace();
			System.out.println("Error during order execution" + ex.getMessage());
		}
		
	}
	
}
