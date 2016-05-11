package com.banco.mdb;

import com.banco.dto.MensajeAuditoria;
import com.banco.ejb.IAuditoriaCuentasLocal;

import javax.ejb.*;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jmartinez on 5/3/16.
 */

@MessageDriven(activationConfig = {
  @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
  @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/AuditoriaQueue"),
  @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "200")
})
public class AuditoriaMDB implements MessageListener{

  private static final Logger LOGGER = Logger.getLogger(AuditoriaMDB.class.getName());

  @EJB
  IAuditoriaCuentasLocal auditoriaEJB;

  @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
  public void onMessage(Message message) {
    LOGGER.finest("Queue: Se recibe mensaje, hora: " + new Date());
    try {
        ObjectMessage msg = (ObjectMessage) message;
        MensajeAuditoria datoAuditar = (MensajeAuditoria) msg.getObject();
        LOGGER.log(Level.FINEST,"ID Cliente a auditar: {0}", datoAuditar.getIdCliente());
        auditoriaEJB.auditarCuentasCliente(datoAuditar.getIdCliente(),datoAuditar.getIdProceso());

        //int cantidad = auditoriaEJB.auditarCuentasCliente(datoAuditar.getIdCliente(),datoAuditar.getIdProceso());
        //auditoriaEJB.incrementarControlCuentasAuditadas(datoAuditar.getIdProceso(),cantidad);
        //Se intenta cambiar el estado del proceso a Finalizado si ya termino
        auditoriaEJB.finalizarProceso(datoAuditar.getIdProceso());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,"Error procesando mensaje en AuditoriaMDB",e);
      throw new RuntimeException(e);
    }

  }

}
