/**
 A dummy exchange acceptor, which receives order and response with execution report:
 *  Mechanism: one in five limit orders are partially filled, and the rest are fully filled;
 *  Market order is filled by market price
 *  Pegged order is filled by market price plus or minus the offset 
 *  
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */

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
		//System.out.println("fromAdmin " + arg0);
	}

	@Override
	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		//System.out.println("fromApp " + message);
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
	 */
	public void onMessage(NewOrderSingle order, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		
		// Order and session information
		Symbol symbol = new Symbol();
		Side side = new Side();
		OrdType ordType = new OrdType();
		OrderQty orderQty = new OrderQty();
		Price price = new Price();
		ClOrdID clOrdID = new ClOrdID();
		ClientID clientID = new ClientID();
		PegDifference offset = new PegDifference();
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
		
		// Execute limit order
		if (ordType.getValue() == ordType.LIMIT){
			
			// one in 5 orders are partially filled and followed by fully filled, others are fully filled
			// If partial fill order, execute order in two parts
			if (orderIDCounter%5 == 0){
				double qty = 0;
				qty = orderQty.getValue()/2;
				
				// create new execution report
				ExecutionReport executionReport = new ExecutionReport(
						getOrderIDCounter(), getExecutionIDCounter(),
						new ExecTransType(ExecTransType.NEW), new ExecType(
								ExecType.PARTIAL_FILL), new OrdStatus(OrdStatus.PARTIALLY_FILLED),
						symbol, side, new LeavesQty(qty),
						new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
				
				// Set execution report fields
				executionReport.set(symbol);
				executionReport.set(side);
				executionReport.set(new OrderQty(qty));
				executionReport.set(price);
				executionReport.set(clOrdID);
				executionReport.set(orderQty);
				executionReport.set(expdate);
				executionReport.set(clientID);
				
				//send first execution report (partial fill)
				try {
					Session.sendToTarget(executionReport, sessionID);
					System.out.println("Execution Report 1 sent----->>>>>");
				} catch (SessionNotFound ex) {
					ex.printStackTrace();
					System.out.println("Error during order execution" + ex.getMessage());
				}
				
				//generate second execution report
				ExecutionReport executionReport2 = new ExecutionReport(
						getOrderIDCounter(), getExecutionIDCounter(),
						new ExecTransType(ExecTransType.NEW), new ExecType(
								ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
						symbol, side, new LeavesQty(0),
						new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
				
				// set execution report fields
				executionReport2.set(symbol);
				executionReport2.set(side);
				executionReport2.set(new OrderQty(qty));
				executionReport2.set(price);
				executionReport2.set(clOrdID);
				executionReport2.set(orderQty);
				executionReport2.set(expdate);
				executionReport2.set(clientID);
				
				
				// send second execution report (partial fill)
				try {
					Session.sendToTarget(executionReport2, sessionID);
					System.out.println("Execution Report 2 sent----->>>>>");
				} catch (SessionNotFound ex) {
					ex.printStackTrace();
					System.out.println("Error during order execution" + ex.getMessage());
				}
				
			} 
			
			// If order is fully filled
			else {
				System.out.print("Fully filled limit order!");
				
				// create new execution report
				ExecutionReport executionReport = new ExecutionReport(
						getOrderIDCounter(), getExecutionIDCounter(),
						new ExecTransType(ExecTransType.NEW), new ExecType(
								ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
						symbol, side, new LeavesQty(0),
						new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
				
				// set execution report fields
				executionReport.set(symbol);
				executionReport.set(side);
				executionReport.set(orderQty);
				executionReport.set(price);
				executionReport.set(clOrdID);
				executionReport.set(orderQty);
				executionReport.set(expdate);
				executionReport.set(clientID);
				
				// send execution report (full fill)
				try {
					Session.sendToTarget(executionReport, sessionID);
					System.out.println("Execution Report limit order sent----->>>>>");
				} catch (SessionNotFound ex) {
					ex.printStackTrace();
					System.out.println("Error during order execution" + ex.getMessage());
				}
			}
		}
		
		// Execute market order
		if (ordType.getValue() == ordType.MARKET){
			
			// normally distributed market price
			double marketprice = Market.genMarketData(price.getValue());
			System.out.println("the normally distributed market price:"+ marketprice);
			
			// create new execution report
			ExecutionReport executionReport = new ExecutionReport(
					getOrderIDCounter(), getExecutionIDCounter(),
					new ExecTransType(ExecTransType.NEW), new ExecType(
							ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
					symbol, side, new LeavesQty(0),
					new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
			
			// Set execution report fields
			executionReport.set(symbol);
			executionReport.set(side);
			executionReport.set(orderQty);
			price.setValue(marketprice);
			executionReport.set(price);
			executionReport.set(clOrdID);
			executionReport.set(orderQty);
			executionReport.set(expdate);
			executionReport.set(clientID);
			
			// send execution report
			try {
				Session.sendToTarget(executionReport, sessionID);
				System.out.println("Execution Report for market order sent----->>>>>");
			} catch (SessionNotFound ex) {
				ex.printStackTrace();
				System.out.println("Error during order execution" + ex.getMessage());
			}
		}
		
		// Execute pegged order
		if (ordType.getValue() == ordType.PEGGED){
			
			// extract offset from order
			order.get(offset);
			double marketprice = Market.genMarketData(price.getValue());
			
			// calculate marketprice based on offset
			if (side.getValue() == side.BUY) marketprice = Math.max(marketprice - offset.getValue(),0);
			if (side.getValue() == side.SELL) marketprice = marketprice + offset.getValue();
			
			System.out.println("the current market price:"+ marketprice);
			
			// create new execution report
			ExecutionReport executionReport = new ExecutionReport(
					getOrderIDCounter(), getExecutionIDCounter(),
					new ExecTransType(ExecTransType.NEW), new ExecType(
							ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
					symbol, side, new LeavesQty(0),
					new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
			
			// set execution report fields
			executionReport.set(symbol);
			executionReport.set(side);
			executionReport.set(orderQty);
			price.setValue(marketprice);
			executionReport.set(price);
			executionReport.set(clOrdID);
			executionReport.set(orderQty);
			executionReport.set(expdate);
			executionReport.set(clientID);
			
			try {
				Session.sendToTarget(executionReport, sessionID);
				System.out.println("Execution Report for pegged order sent----->>>>>");
			} catch (SessionNotFound ex) {
				ex.printStackTrace();
				System.out.println("Error during order execution" + ex.getMessage());
			}
			
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
	
}
