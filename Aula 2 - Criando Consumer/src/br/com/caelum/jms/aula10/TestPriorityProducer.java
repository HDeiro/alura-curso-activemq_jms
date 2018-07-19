package br.com.caelum.jms.aula10;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

public class TestPriorityProducer {
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
		Destination queue = (Destination) context.lookup("LOG");
		
		/** CONSUMER INFO */
		// Create a consumer
		MessageProducer producer = session.createProducer(queue);
		// Create a Message
		Message message = session.createTextMessage("INFO | Log de teste");
		
		// Send the message
		/**
		 * Parameter 1 = Message
		 * Parameter 2 = Delivery Mode -> Persistent (Save the message in local DB) or NON_PERSISTENT
		 * Parameter 3 = Priority -> (Less Persistent) 0 to 9 (More persistent)
		 * Parameter 4 = Time to Live (TTL), the time until the message will be automatically deleted if there is no consumer
		 * 
		 * NOTE: If you have to use the priority, it's necessary to ADD a policeEntry into the configuration XML (activemq/conf/activemq.xml) for ActiveMQ
		 * <policyEntry queue=">" prioritizeMessages="true"/>
		 * */
		producer.send(message, DeliveryMode.PERSISTENT, 3, 5000);			
		
		/** CLOSING INSTANCES */
		session.close();
		connection.close();
		context.close();
	}
}
