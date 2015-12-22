
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Calendar;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;



import java.io.StringWriter;
import org.apache.activemq.ActiveMQConnectionFactory;



public class SwapClient implements MessageListener {
	
	private static int ackMode;
	private static String messageQueueName;
	private Session session;
	private boolean transacted = false;
	private MessageProducer replyProducer;

	static {
		messageQueueName = "client.messages";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}
	
	public SwapClient() {

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			this.session = connection.createSession(this.transacted, ackMode);
			Destination adminQueue = this.session.createQueue(messageQueueName);

			// Setup a message producer to respond to messages from clients, we
			// will get the destination
			// to send to from the JMSReplyTo header field from a Message
			this.replyProducer = this.session.createProducer(null);
			this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// Set up a consumer to consume messages off of the admin queue
			MessageConsumer consumer = this.session.createConsumer(adminQueue);
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			// Handle the exception appropriately
		}

	}

	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}

	public void onMessage(javax.jms.Message message) {
		try {
			DOMParser parser = new DOMParser();
			
			System.out.println("From Exchange:" + ((TextMessage) message).getText());
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String messageText = txtMsg.getText();
				
				//Accepting all request consent from exchange
				if (messageText.contains("CONSENTFIELD")){
					TextMessage response = this.session.createTextMessage();
					response.setText(messageText);
					this.replyProducer.send(message.getJMSReplyTo(), response);
				}
				
				//Accepting all request consent from exchange
				if (messageText.contains("Clearing Confirm")){
					parser.parse(new InputSource (new StringReader(messageText)));
					Document doc = parser.getDocument();
					
					int startDay = 0;
					int startMonth = 0;
					int startYear = 0;

					int termDay = 0;
					int termMonth = 0;
					int termYear = 0;
					
					double floatRate = 0;
					double floatRateSpread = 0;
					double fixedRate = 0;

					String PayerFixed = "";
					String PayerFloat = "";
					int trader = 0;
					
					Node t = doc.getElementsByTagName("trader").item(0);
					if(t != null) trader = Integer.parseInt(t.getTextContent());
					
					Node sd = doc.getElementsByTagName("startDay").item(0);
					if(sd != null) startDay = Integer.parseInt(sd.getTextContent());
					
					Node sm = doc.getElementsByTagName("startMonth").item(0);
					if(sm != null) startMonth = Integer.parseInt(sm.getTextContent());
					
					Node sy = doc.getElementsByTagName("startYear").item(0);
					if(sy != null) startYear = Integer.parseInt(sy.getTextContent());
					
					Node td = doc.getElementsByTagName("terminationDay").item(0);
					if (td != null) termDay = Integer.parseInt(td.getTextContent());
					
					Node tm = doc.getElementsByTagName("terminationMonth").item(0);
					if (tm != null) termMonth = Integer.parseInt(tm.getTextContent());
					
					Node ty = doc.getElementsByTagName("terminationYear").item(0);
					if (ty != null) termYear = Integer.parseInt(ty.getTextContent());
					
					Node whoFloat = doc.getElementsByTagName("whoFloat").item(0);
					if (whoFloat != null) PayerFloat = whoFloat.getTextContent();
					
					Node whoFixed = doc.getElementsByTagName("whoFixed").item(0);
					if (whoFixed != null) PayerFixed = whoFixed.getTextContent();
					
					Node floatingRate = doc.getElementsByTagName("floatingRate").item(0);
					if (floatingRate != null) floatRate = Double.parseDouble(floatingRate.getTextContent());
					
					Node floatingRateSpread = doc.getElementsByTagName("floatingRateSpread").item(0);
					if (floatingRateSpread != null) floatRateSpread = Double.parseDouble(floatingRateSpread.getTextContent());
					
					Node fRate = doc.getElementsByTagName("fixedRate").item(0);
					if (fRate != null) fixedRate = Double.parseDouble(fRate.getTextContent());
					
					Swap s = new Swap(startDay, startMonth, startYear, termDay, termMonth, termYear, floatRate, floatRateSpread, fixedRate, PayerFloat, PayerFixed,trader);
					s.printSwap();
					DatabaseManager db = DatabaseManager.getInstance();
					db.insertSwap(s);
				}
			}	
			
		} catch (Exception e) {
			// Handle the exception appropriately
			e.printStackTrace();
		}
	}
}
