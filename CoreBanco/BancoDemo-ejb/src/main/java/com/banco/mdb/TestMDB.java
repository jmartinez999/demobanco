package com.banco.mdb;

import com.banco.entidades.Cliente;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jmartinez on 5/3/16.
 */

@MessageDriven(activationConfig = {
  @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
  @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/BancoQueue"),
  @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "200")
})
public class TestMDB implements MessageListener{

  private static final Logger LOGGER = Logger.getLogger(TestMDB.class.getName());

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public void onMessage(Message message) {
    LOGGER.info("Se recibio mensaje JMS ");
    try {
      LOGGER.finest("Esperando 10 segungos ..... ");
      Thread.sleep(10000);//Espera 10 segundos
    }catch (InterruptedException ie){
      LOGGER.log(Level.WARNING,"Problemas con sleep del Thread", ie);
    }
    try {
      if (message instanceof TextMessage) {
        LOGGER.finest("Queue: Se recibe mensaje tipo TextMessage, hora: " + new Date());
        TextMessage msg = (TextMessage) message;
        LOGGER.finest("Mensaje : " + msg.getText());
      } else if (message instanceof ObjectMessage) {
        LOGGER.finest("Queue: Se recibe mensaje de tipo  ObjectMessage, hora: " + new Date());
        ObjectMessage msg = (ObjectMessage) message;
        Cliente cliente = (Cliente) msg.getObject();
        LOGGER.finest("Nombre Cliente:" + cliente.getIdentificacion());
        LOGGER.finest("Nombre Cliente:" + cliente.getNombre());

      } else {
        LOGGER.info("Mensaje no valido para este MDB");
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,"Error procesando mensaje en TestMDB",e);
    }

  }

}
