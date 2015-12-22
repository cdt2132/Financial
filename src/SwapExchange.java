
import java.util.Random;

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

public class SwapExchange implements MessageListener {
	
	private static int ackMode;
	private Session session;
	private static String clientQueueName;
	private boolean transacted = false;
	private MessageProducer producer;

	static {
		clientQueueName = "client.messages";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}
	
	public void RequestConsent(String fpml) throws JMSException{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		Connection connection;
		connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(transacted, ackMode);
		Destination adminQueue = session.createQueue(clientQueueName);
		
		this.producer = session.createProducer(adminQueue);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);		
		Destination tempDest = session.createTemporaryQueue();
        MessageConsumer responseConsumer = session.createConsumer(tempDest);
        responseConsumer.setMessageListener(this);
 
        TextMessage txtMessage = session.createTextMessage();
        txtMessage.setText(fpml);
        txtMessage.setJMSReplyTo(tempDest);
        this.producer.send(txtMessage);

	}
	
	public void ClearingConfirm(String messageText) throws JMSException{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		Connection connection;
		connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(transacted, ackMode);
		Destination adminQueue = session.createQueue(clientQueueName);
		
		this.producer = session.createProducer(adminQueue);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);		
		Destination tempDest = session.createTemporaryQueue();
        MessageConsumer responseConsumer = session.createConsumer(tempDest);
        responseConsumer.setMessageListener(this);
 
        TextMessage txtMessage = session.createTextMessage();
        messageText.replace("CONSENTFIELD", "Clearing Confirm");
        txtMessage.setText(messageText);
        txtMessage.setJMSReplyTo(tempDest);
        this.producer.send(txtMessage);

	}
	public void ClearingReject(String messageText) throws JMSException{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		Connection connection;
		connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(transacted, ackMode);
		Destination adminQueue = session.createQueue(clientQueueName);
		
		this.producer = session.createProducer(adminQueue);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);		
		Destination tempDest = session.createTemporaryQueue();
        MessageConsumer responseConsumer = session.createConsumer(tempDest);
        responseConsumer.setMessageListener(this);
 
        TextMessage txtMessage = session.createTextMessage();
        messageText.replace("CONSENTFIELD", "Clearing Reject");
        txtMessage.setText(messageText);
        txtMessage.setJMSReplyTo(tempDest);
        this.producer.send(txtMessage);

		
	}

	public void onMessage(Message message) {
		String messageText = null;
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageText = textMessage.getText();
				System.out.println("Response from Client = " + messageText);
			}
			
			//randomly reject or confirm clearing
			Random rand = new Random();
			
			if (rand.nextInt(2) == 0){
				//Confirm Clearing
				ClearingConfirm(messageText);
				
			}else{
				//reject clearing
				ClearingReject(messageText);
			}
		}catch(JMSException e){
			
		}
	}

}
