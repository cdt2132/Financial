/**
 * Exchange acceptor class fills orders
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build November 2015
 */
import java.io.File;

import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.ClientID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.Headline;
import quickfix.field.LeavesQty;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SecurityID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.News;

/** A dummy exchange acceptor, which receives order and response with execution report:
 *  Mechanism: one in five limit orders are partilly filled, and the rest are fully filled;
 *  Market order is filled by market price
 *  Pegged order is filled by market price plus or minus the offset */
public class ExchangeAcceptor extends MessageCracker implements Application {

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
	
	
	/** After receive the order, execute the order and send back to initiator the execution reports */
	public void onMessage(NewOrderSingle order, SessionID sessionID)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		// sending some news to the client.

		Symbol symbol = new Symbol();
		Side side = new Side();
		OrdType ordType = new OrdType();
		OrderQty orderQty = new OrderQty();
		Price price = new Price();
		ClOrdID clOrdID = new ClOrdID();
		ClientID clientID = new ClientID();
		MaturityMonthYear expdate = new MaturityMonthYear();
		
		//get Order Information
		order.get(symbol);
		order.get(side);
		order.get(orderQty);
		order.get(price);
		order.get(clOrdID);
		order.get(ordType);
		order.get(expdate);
		order.get(clientID);
		//execute the order
		
		if (ordType.getValue() == ordType.LIMIT){
			
			// one in 5 orders are partially filled and followed by fully filled, others are fully filled
			if (orderIDCounter%5 == 0){
				double qty = 0;
				qty = orderQty.getValue()/2;
				ExecutionReport executionReport = new ExecutionReport(
						getOrderIDCounter(), getExecutionIDCounter(),
						new ExecTransType(ExecTransType.NEW), new ExecType(
								ExecType.PARTIAL_FILL), new OrdStatus(OrdStatus.PARTIALLY_FILLED),
						symbol, side, new LeavesQty(qty),
						new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
				executionReport.set(symbol);
				executionReport.set(side);
				executionReport.set(new OrderQty(qty));
				executionReport.set(price);
				executionReport.set(clOrdID);
				executionReport.set(orderQty);
				executionReport.set(expdate);
				executionReport.set(clientID);
				//send first execution report
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
				executionReport2.set(symbol);
				executionReport2.set(side);
				executionReport2.set(new OrderQty(qty));
				executionReport2.set(price);
				executionReport2.set(clOrdID);
				executionReport2.set(orderQty);
				executionReport2.set(expdate);
				executionReport2.set(clientID);
				//send second execution report
				try {
					Session.sendToTarget(executionReport2, sessionID);
					System.out.println("Execution Report 2 sent----->>>>>");
				} catch (SessionNotFound ex) {
					ex.printStackTrace();
					System.out.println("Error during order execution" + ex.getMessage());
				}
				
			}else{
				System.out.print("Odd number limit order!");
				
				ExecutionReport executionReport = new ExecutionReport(
						getOrderIDCounter(), getExecutionIDCounter(),
						new ExecTransType(ExecTransType.NEW), new ExecType(
								ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
						symbol, side, new LeavesQty(0),
						new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
				executionReport.set(symbol);
				executionReport.set(side);
				executionReport.set(orderQty);
				executionReport.set(price);
				executionReport.set(clOrdID);
				executionReport.set(orderQty);
				executionReport.set(expdate);
				executionReport.set(clientID);
				
				try {
					Session.sendToTarget(executionReport, sessionID);
					System.out.println("Execution Report limit order sent----->>>>>");
				} catch (SessionNotFound ex) {
					ex.printStackTrace();
					System.out.println("Error during order execution" + ex.getMessage());
				}
			}
		}
		
		if (ordType.getValue() == ordType.MARKET){
			double marketprice = Market.genMarketData(100);
			System.out.println("the random market price:"+ marketprice);
			ExecutionReport executionReport = new ExecutionReport(
					getOrderIDCounter(), getExecutionIDCounter(),
					new ExecTransType(ExecTransType.NEW), new ExecType(
							ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
					symbol, side, new LeavesQty(0),
					new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
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
				System.out.println("Execution Report for market order sent----->>>>>");
			} catch (SessionNotFound ex) {
				ex.printStackTrace();
				System.out.println("Error during order execution" + ex.getMessage());
			}
		}
		
		//
		if (ordType.getValue() == ordType.PEGGED){
			double marketprice = Market.genMarketData(100);
			System.out.println("the random market price:"+ marketprice);
			ExecutionReport executionReport = new ExecutionReport(
					getOrderIDCounter(), getExecutionIDCounter(),
					new ExecTransType(ExecTransType.NEW), new ExecType(
							ExecType.FILL), new OrdStatus(OrdStatus.FILLED),
					symbol, side, new LeavesQty(0),
					new CumQty(orderQty.getValue()), new AvgPx(price.getValue()));
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
	


	/** Current order ID*/
	private int orderIDCounter;
	/** Current execution ID*/
	private int executionIDCounter;
	
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
