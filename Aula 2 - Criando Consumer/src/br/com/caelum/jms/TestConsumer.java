package br.com.caelum.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.InitialContext;

public class TestConsumer {
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
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Get the destination queue as defined in jndi.properties file
		Destination queue = (Destination) context.lookup("financeiro");
		
		/** CONSUMER INFO */
		// Create a consumer
		MessageConsumer consumer = session.createConsumer(queue);
		// Get a single message from ActiveMQ 
		Message message = consumer.receive();
		// Print received message
		System.out.println("Recebendo Mensagem: " + message);

		/** CLOSING INSTANCES */
		session.close();
		connection.close();
		context.close();
	}
}
