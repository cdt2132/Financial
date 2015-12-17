
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

public class SwapClient implements Runnable, MessageListener {

	private static int ackMode;
	private static String clientQueueName;
	private boolean transacted = false;
	private MessageProducer producer;

	static {
		clientQueueName = "client.messages";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}

	public void run() {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
			Connection connection;
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(transacted, ackMode);
			Destination adminQueue = session.createQueue(clientQueueName);
			// Setup a message producer to send message to the queue the server
			// is consuming from
			this.producer = session.createProducer(adminQueue);
			this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// Create a temporary queue that this client will listen for
			// responses on then create a consumer
			// that consumes message from this temporary queue...for a real
			// application a client should reuse
			// the same temp queue for each message to the server...one temp
			// queue per client
			Destination tempDest = session.createTemporaryQueue();
			MessageConsumer responseConsumer = session.createConsumer(tempDest);
			// This class will handle the messages to the temp queue as well
			responseConsumer.setMessageListener(this);
			// Now create the actual message you want to send
			TextMessage txtMessage = session.createTextMessage();
			txtMessage.setText("Client Swap");

			// Set the reply to field to the temp queue you created above, this
			// is the queue the server
			// will respond to
			txtMessage.setJMSReplyTo(tempDest);
			this.producer.send(txtMessage);
		} catch (JMSException e) {
			// Handle the exception appropriately
		}

	}

	public void onMessage(Message message) {
		String messageText = null;
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				messageText = textMessage.getText();
				System.out.println("Response from Exchange = " + messageText);
			}
		}catch(JMSException e){
		}
	}

}
