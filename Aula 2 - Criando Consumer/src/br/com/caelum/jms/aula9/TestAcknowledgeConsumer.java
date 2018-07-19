package br.com.caelum.jms.aula9;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class TestAcknowledgeConsumer {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		/** INITIALIZATION */
		// Initialize the context to get properties set on jndi.properties file
		InitialContext context = new InitialContext();
		// Gets the connection factory of the MOM, in ActiveMQ case it's the ConnectionFactory
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		// Creates and starts a connection
		Connection connection = factory.createConnection();
		connection.start();
		// Creates a session of the connection whitout transaction and auto-commiting that received a message when it's necessary
		Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
		// Get the destination queue as defined in jndi.properties file
		Destination queue = (Destination) context.lookup("financeiro");
		
		/** CONSUMER INFO */
		// Create a consumer
		MessageConsumer consumer = session.createConsumer(queue);
		
		/** !Important
		 * 	
		 * Note that for multiple instances of consumers the JMS will distribute the
		 * messages. A same message will never be delivered to multiple consumers.
		 * */
		// Create an anonymous class implementing MessageListener Interface
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				// Subclass of message to deal with text messages
				TextMessage textMessage = (TextMessage) message;

				try {
					System.out.println("Recebendo Mensagem: " + textMessage.getText());
					session.commit();
				} catch (JMSException e) {
					e.printStackTrace();
				}	
			}
		});
		
		new Scanner(System.in).nextLine();

		/** CLOSING INSTANCES */
		session.close();
		connection.close();
		context.close();
	}
}
