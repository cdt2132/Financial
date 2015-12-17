
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

public class SwapExchangeListener implements Runnable, MessageListener {
	private static int ackMode;
	private static String messageQueueName;
	private Session session;
	private boolean transacted = false;
	private MessageProducer replyProducer;

	static {
		messageQueueName = "client.messages";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}

	public void run() {

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
			
			System.out.println("Request from client:" + ((TextMessage) message).getText());
			TextMessage response = this.session.createTextMessage();
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String messageText = txtMsg.getText();
				response.setText("Clearing's response");
			}
			// Set the correlation ID from the received message to be the
			// correlation id of the response message
			// this lets the client identify which message this is a response to
			// if it has more than
			// one outstanding message to the server

			// Send the response to the Destination specified by the JMSReplyTo
			// field of the received message,
			// this is presumably a temporary queue created by the client
			this.replyProducer.send(message.getJMSReplyTo(), response);
		} catch (JMSException e) {
			// Handle the exception appropriately
		}
	}
}
