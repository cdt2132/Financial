
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
					parser.parse(messageText);
					Document doc = parser.getDocument();
					
					
				}
			}	
			
		} catch (Exception e) {
			// Handle the exception appropriately
			e.printStackTrace();
		}
	}
}
