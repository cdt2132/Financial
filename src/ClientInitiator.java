/**
 * Client Initiator Gets Order Information
 *
 * @author Caroline Trimble, Kunal Jasty, Haoxiang Gao
 * @version 1 Build October 2015
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import quickfix.ApplicationAdapter;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.field.ClOrdID;
import quickfix.field.ClientID;
import quickfix.field.HandlInst;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PegDifference;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

/** Singleton class used to send FIX messages (orders) to exchange test harness */
public class ClientInitiator extends ApplicationAdapter{
	
	// Instance variables
	private SocketInitiator socketInitiator;
	private static ClientInitiator fixInitiator;
	
	// Prevent instantiation
	private ClientInitiator(){
		
	}
	
	/** Constructor
	 * @return ClientInitiator instance
	 */
	public static ClientInitiator getInstance() throws ConfigError{
		
		if (fixInitiator!= null)
			return fixInitiator;
		
		fixInitiator = new ClientInitiator();
		
		// Initiate connections
		SessionSettings sessionSettings = new SessionSettings("./conf/initiator.cfg");
		ApplicationAdapter application = new ClientInitiator();
		FileStoreFactory fileStoreFactory = new FileStoreFactory(sessionSettings);
		ScreenLogFactory screenLogFactory = new ScreenLogFactory(sessionSettings);
		DefaultMessageFactory defaultMessageFactory = new DefaultMessageFactory();
		
		// Connect
		fixInitiator.socketInitiator = new SocketInitiator(application,
				fileStoreFactory, sessionSettings, screenLogFactory,
				defaultMessageFactory);
		fixInitiator.socketInitiator.start();
		return fixInitiator;
	}

	@Override
	public void onLogon(SessionID sessionId) {
		super.onLogon(sessionId);
		System.out.println("Logon initiated");
	}

	@Override
	public void fromAdmin(quickfix.Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			RejectLogon {
		super.fromAdmin(message, sessionId);
	}

	@Override
	public void onCreate(SessionID sessionId) {
		super.onCreate(sessionId);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (null != this.socketInitiator) {
			this.socketInitiator.stop();
		}
	}

	@Override
	/** Extracts trade information and insert into database 
	 * @param message Fix message
	 * @param sessionId Session id of current fix session
	 */
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		
		// If message is an execution report
		if (message instanceof ExecutionReport) {
			ExecutionReport executionReport = (ExecutionReport) message;
			try {
				
				System.out.println("Received Execution Report from Exchange");
				
				//  Order and session information
				Symbol symbol = new Symbol();
				Side side = new Side();
				OrdType ordType = new OrdType();
				OrderQty orderQty = new OrderQty();
				Price price = new Price();
				ClOrdID clOrdID = new ClOrdID();
				ClientID clientID = new ClientID();
				MaturityMonthYear expdate = new MaturityMonthYear();
				
				
				// Extract order information from execution report
				executionReport.get(symbol);
				executionReport.get(side);
				executionReport.get(orderQty);
				executionReport.get(price);
				executionReport.get(clOrdID);
				executionReport.get(expdate);
				executionReport.get(clientID);
				
				Date expdat = new Date();
				DateFormat df = new SimpleDateFormat("MM:yyyy");
				expdat = df.parse(expdate.getValue());
				int m = expdat.getMonth();
				int y = expdat.getYear() + 1900;
				int b = 0;
				b = (side.getValue() == Side.BUY)?1:-1;
				
				// Create a new order record
				Order order = new Order(symbol.getValue(),m, y,(int)orderQty.getValue(),price.getValue(),b, 0 ,Integer.parseInt(clientID.getValue()));
				
				// print order and insert record into the database
				DatabaseManager db = DatabaseManager.getInstance();
				order.printOrder();
				db.insertOrder(order);
				
			} catch (FieldNotFound e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/** Generates order and sends to exchange acceptor
	 * @param trader traderid
	 * @param symbol symbol from Chicago Mercantile Exchange
	 * @param lot lot size
	 * @param price security price
	 * @param side type of trade (buy or sell)
	 * @param expMonth trade month expiry
	 * @param expYear trade year expiry
	 * @param ordertype type of order (limit, market or pegged)
	 */
	public void sendOrder(String trader, String symbol, int lot, double price, int side,  int expmonth, int expyear, int ordertype ){
		
		// Retrieve current sessionid
		ArrayList<SessionID> sessions = socketInitiator.getSessions();
		SessionID sessionID = sessions.get(0);
		
		// set basic order information
		// Default order is a limit buy order
		NewOrderSingle order = new NewOrderSingle(new ClOrdID("1"),
				new HandlInst(HandlInst.MANUAL_ORDER), new Symbol(symbol),
				new Side(Side.BUY), new TransactTime(new Date()), new OrdType(OrdType.LIMIT));
		
		order.set(new OrderQty(lot));
		order.set(new Price(price));
		order.set(new ClientID(trader));
		
		
		// Adjust side if necessary
		if (side == -1) order.set(new Side(Side.SELL));
		
		// Adjust ordertype if necessary
		if (ordertype == 0) {
			order.set(new OrdType (OrdType.MARKET));
			System.out.println("This is a merket order!");
		}
		if (ordertype == 2){
			order.set(new OrdType (OrdType.PEGGED));
			order.set(new PegDifference(price));
			System.out.println("This is a pegged order!");
		}
		
		//set expiration date
		Date expdate = new Date();
		expdate.setMonth(expmonth);
		expdate.setYear(expyear-1900);	
		DateFormat df = new SimpleDateFormat("MM:yyyy");
		order.set(new MaturityMonthYear(df.format(expdate)));;
		
		// print order
		System.out.println("SingleNewOrder to send:");
		System.out.println(order.toString());
		
		
		//send the order
		try {
			Session.sendToTarget(order, sessionID);
		} catch (SessionNotFound e) {
			e.printStackTrace();
		}
	}
}
