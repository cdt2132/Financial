
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
	
	public void RequestConsent() throws JMSException{
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
        txtMessage.setText("Request Consent");
        txtMessage.setJMSReplyTo(tempDest);
        this.producer.send(txtMessage);

	}
	
	public void ClearingConfirm() throws JMSException{
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
        txtMessage.setText("Clearing Confirm");
        txtMessage.setJMSReplyTo(tempDest);
        this.producer.send(txtMessage);

	}
	public void ClearingReject() throws JMSException{
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
        txtMessage.setText("Clearing Reject");
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
				ClearingConfirm();
				
			}else{
				//reject clearing
				ClearingReject();
			}
		}catch(JMSException e){
			
		}
	}

}
