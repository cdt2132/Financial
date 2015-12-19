
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
			
			System.out.println("From Exchange:" + ((TextMessage) message).getText());
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String messageText = txtMsg.getText();
				
				//Accepting all request consent from exchange
				if (messageText == "Request Consent"){
					TextMessage response = this.session.createTextMessage();
					response.setText("Consent!");
					this.replyProducer.send(message.getJMSReplyTo(), response);
				}
				
				//Accepting all request consent from exchange
				if (messageText == "Clearing Confirm"){
					//insert trade into database
					
				}
			}	
			
		} catch (JMSException e) {
			// Handle the exception appropriately
		}
	}
}
