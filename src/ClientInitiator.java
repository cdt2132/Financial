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
import quickfix.IntField;
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
import quickfix.field.ExecType;
import quickfix.field.HandlInst;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PegDifference;
import quickfix.field.Price;
import quickfix.field.SecurityID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;


public class ClientInitiator extends ApplicationAdapter{
	private SocketInitiator socketInitiator;
	private static ClientInitiator fixIniator;
	private ClientInitiator(){
		
	}
	
	public static ClientInitiator getInstance() throws ConfigError{
		if (fixIniator!= null)
			return fixIniator;
		fixIniator = new ClientInitiator();
		SessionSettings sessionSettings = new SessionSettings(
				"./conf/initiator.cfg");
		ApplicationAdapter application = new ClientInitiator();
		FileStoreFactory fileStoreFactory = new FileStoreFactory(
				sessionSettings);
		ScreenLogFactory screenLogFactory = new ScreenLogFactory(
				sessionSettings);
		DefaultMessageFactory defaultMessageFactory = new DefaultMessageFactory();
		fixIniator.socketInitiator = new SocketInitiator(application,
				fileStoreFactory, sessionSettings, screenLogFactory,
				defaultMessageFactory);
		fixIniator.socketInitiator.start();
		return fixIniator;
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
	/** Once receive the execution report, this function extract trade information and insert into database */
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		
		if (message instanceof ExecutionReport) {
			ExecutionReport executionReport = (ExecutionReport) message;
			try {
				System.out.println("Got Execution Report from Exchange");
				Symbol symbol = new Symbol();
				Side side = new Side();
				OrdType ordType = new OrdType();
				OrderQty orderQty = new OrderQty();
				Price price = new Price();
				ClOrdID clOrdID = new ClOrdID();
				ClientID clientID = new ClientID();
				MaturityMonthYear expdate = new MaturityMonthYear();
				
				//get Order Information

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
				Order order = new Order(symbol.getValue(),m, y,(int)orderQty.getValue(),price.getValue(),b, 0 ,Integer.parseInt(clientID.getValue()));
				
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
	/** A function that generate order and send to exchange acceptor */
	public void sendOrder(String trader, String symbol, int lot, double price,int side,  int expmonth, int expyear, int ordertype ){
		ArrayList<SessionID> sessions = socketInitiator
				.getSessions();
		SessionID sessionID = sessions.get(0);
		
		//set basic order information
		NewOrderSingle order = new NewOrderSingle(new ClOrdID("1"),
				new HandlInst(HandlInst.MANUAL_ORDER), new Symbol(symbol),
				new Side(Side.BUY), new TransactTime(new Date()), new OrdType(
						OrdType.LIMIT));
		order.set(new OrderQty(lot));
		order.set(new Price(price));
		order.set(new ClientID(trader));
		// set side
		if (side == -1) order.set(new Side(Side.SELL));
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
