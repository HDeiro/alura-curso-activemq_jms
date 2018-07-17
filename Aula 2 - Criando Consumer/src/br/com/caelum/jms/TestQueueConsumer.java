package br.com.caelum.jms;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

public class TestQueueConsumer {

	public static void main(String[] args) throws Exception {
		/** INITIALIZATION */
		// Initialize the context to get properties set on jndi.properties file
		InitialContext context = new InitialContext();
		// Gets the connection factory of the MOM, in ActiveMQ case it's the ConnectionFactory
        QueueConnectionFactory queueConnection = (QueueConnectionFactory)context.lookup("ConnectionFactory");
		// Create and starts queue connection
        QueueConnection connection = queueConnection.createQueueConnection();
        connection.start();
        // Create the session for queue connection
        QueueSession queueSession = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        // Get the queue defined on jndi.properties file
        Queue fila = (Queue) context.lookup("financeiro");
        // Get a receiver for the queue messages
        QueueReceiver receiver = (QueueReceiver) queueSession.createReceiver(fila);
        
		/** CONSUMER INFO */
        // Receive a single message from ActiveMQ
        Message message = receiver.receive();
        // Print the received message
        System.out.println(message);


		/** CLOSING INSTANCES */
        queueSession.close();
        connection.close();    
        context.close();
	}

}
