package com.banco.util;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;



/**
 * Created by jmartinez on 5/3/16.
 */
public class JMSUtil {


  /**
   * log
   */
  private static final Logger log = Logger.getLogger(JMSUtil.class.getName());

  /**
   * se encarga de enviar mensajes a las colas
   *
   * @param obj
   * @param queueName
   */
  public static void sendMessage(Object obj, String queueName) {
    Connection connection = null;
    Session session = null;
    MessageProducer messageProducer = null;

    ObjectMessage objectMessage = null;

    try {
      InitialContext jndiContext = new InitialContext();
      // Se busca la cola para los mensajes
      Queue queue = (Queue) jndiContext.lookup(queueName);
      ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("java:/ConnectionFactory");

      connection = connectionFactory.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      messageProducer = session.createProducer(queue);

      objectMessage = session.createObjectMessage();
      objectMessage.setObject((Serializable) obj);
      messageProducer.send(objectMessage);
    } catch (JMSException e) {
      log.log(Level.SEVERE,"Error  JSMException  ", e);
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error Exception  ",  e);
    } finally {
      try {
        if (messageProducer != null) {
          messageProducer.close();
        }
        if (session != null) {
          session.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (JMSException e) {
        log.log(Level.SEVERE,"Error  JSMException  " + e);
      }
    }
  }

}
