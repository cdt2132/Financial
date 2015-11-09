
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
		//execute the order, we assume that the exchange fully fill all orders after 3 seconds
		try {
			System.out.print("Executing...");
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//generate execution report		
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
		//send execution report
		try {
			Session.sendToTarget(executionReport, sessionID);
			System.out.println("Execution Report sent----->>>>>");
			System.out.println(executionReport.toString());
		} catch (SessionNotFound ex) {
			ex.printStackTrace();
			System.out.println("Error during order execution" + ex.getMessage());
		}
	}

	private int orderIDCounter;
	private int executionIDCounter;

	public OrderID getOrderIDCounter() {
		orderIDCounter++;
		return new OrderID(String.valueOf(orderIDCounter));
	}

	public ExecID getExecutionIDCounter() {
		executionIDCounter++;
		return new ExecID(String.valueOf(executionIDCounter));
	}
	
}
