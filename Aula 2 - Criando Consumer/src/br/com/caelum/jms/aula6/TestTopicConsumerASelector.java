package br.com.caelum.jms.aula6;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

public class TestTopicConsumerASelector {
	public static void main(String[] args) throws Exception {
		/** INITIALIZATION */
		// Initialize the context to get properties set on jndi.properties file
		InitialContext context = new InitialContext();
		// Gets the connection factory of the MOM, in ActiveMQ case it's the ConnectionFactory
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		// Creates, identifies and starts a connection
		Connection connection = factory.createConnection();
		connection.setClientID("topic-consumer-a"); /** !Important to check wich consumer is this*/
		connection.start();
		// Creates a session of the connection whitout transaction and auto-commiting that received a message when it's necessary
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Get the destination topic as defined in jndi.properties file
		Topic topic = (Topic) context.lookup("loja");
		
		/** CONSUMER INFO */
		// Create a consumer (topic, signatureName, messageSelector, noLoad)
		// Note that, selectors does not have access to message body content, only to headers
		// The noLoad parameter is used to define if the SAME connection is used to send and receive messages
		MessageConsumer consumer = session.createDurableSubscriber(topic, "assinatura-selector-a", "property1=false OR property2 < 4000", false);
		// Create an anonymous class implementing MessageListener Interface
		consumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				// Subclass of message to deal with text messages
				TextMessage textMessage = (TextMessage) message;

				try {
					System.out.println("Recebendo Mensagem: " + textMessage.getText());
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
