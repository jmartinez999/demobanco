package com.banco.ejb;

import com.banco.dto.CuentaAuditoriaDTO;
import com.banco.dto.MensajeAuditoria;
import com.banco.entidades.TipoOperacion;
import com.banco.util.JMSUtil;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jmartinez on 5/5/16.
 */
@Stateless
public class AuditoriaFacade implements IAuditoriaCuentasLocal, IAuditoriaCuentasRemote {

  private static final String COLA_AUDITORIA = "java:/jms/queue/AuditoriaQueue";

  @PersistenceContext(unitName = "BancoPU")
  private EntityManager em;

  private static final  Logger LOGGER = Logger.getLogger(AuditoriaFacade.class.getName());


  @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
  public void iniciarAuditoriaCuentas(BigInteger idProceso){

    List listaClientes = obtenerClientesParaAuditar();

    LOGGER.log(Level.FINEST,"Se inicio Auditoria de cuentas, Proceso No:{0}", idProceso);
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/ HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    LOGGER.log(Level.FINEST,"Hora de inicio: {0}", cal.getTime());

    enviarMensajes(listaClientes, idProceso);
  }

  private void enviarMensajes(List<Integer> listaIDs, BigInteger idProceso){
    for (Integer idCliente: listaIDs){
      MensajeAuditoria msg = new MensajeAuditoria(idCliente,idProceso);
      //Se envia mensaje a cola
      JMSUtil.sendMessage(msg,COLA_AUDITORIA);
    }
    LOGGER.log(Level.FINEST,"Se enviaron {0} mensajes a la cola de Auditoria", listaIDs.size());
  }

  @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
  public BigInteger registrarControlAuditoria(){
    Query q = em.createNativeQuery("select nextval('control_auditoria_seq') as num");
    BigInteger idProceso = (BigInteger) q.getSingleResult();

    BigInteger totalCuentas = obtenerTotalCuentas();

    q = em.createNativeQuery("insert into control_auditoria values(?,?,?,?,?,?)");
    q.setParameter(1,idProceso);
    q.setParameter(2, new Date(), TemporalType.TIMESTAMP);
    q.setParameter(3, totalCuentas);
    q.setParameter(4, 0);
    q.setParameter(5, "EN_PROCESO");
    q.setParameter(6,"admin");
    int result = q.executeUpdate();
    if( result == 0){
      throw new RuntimeException("Error creando registro de control de auditoria");
    }
    em.flush(); //Para garantizar que la tabla se actualice en la BD
    return idProceso;
  }

  @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
  public List obtenerClientesParaAuditar(){
    Query q = em.createNativeQuery("select id from cliente");
    List listaClientes = q.getResultList();
    return  listaClientes;
  }

  @TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
  private BigInteger obtenerTotalCuentas(){
    Query q = em.createNativeQuery("select count (1) from cuenta");
    return (BigInteger)q.getSingleResult();
  }

  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
  public int auditarCuentasCliente(Integer idCliente, BigInteger idProcesoAuditoria){
    List<CuentaAuditoriaDTO> cuentasCliente = consultarCuentasCliente(idCliente);
    for(CuentaAuditoriaDTO cuenta : cuentasCliente){
       Double saldoEnMovimientos = consultarSaldoMovimientos(cuenta.getNumeroCuenta());
       registrarDetalleAuditoria(idProcesoAuditoria,cuenta,saldoEnMovimientos);
    }
    incrementarControlCuentasAuditadas(idProcesoAuditoria,cuentasCliente.size());
    return cuentasCliente.size();
  }

  private void registrarDetalleAuditoria(BigInteger idProcesoAuditoria, CuentaAuditoriaDTO cuenta, Double saldoEnMovimientos ) {
    String sql = "insert into detalle_auditoria values(nextval('detalle_auditoria_seq'),?,?,?,?,?,?,?)";
    Query q = em.createNativeQuery(sql);
    q.setParameter(1,idProcesoAuditoria);
    q.setParameter(2,cuenta.getNumeroCuenta());
    q.setParameter(3,cuenta.getSaldo());
    q.setParameter(4,saldoEnMovimientos);
    q.setParameter(5,cuenta.getTipoIDCliente());
    q.setParameter(6,cuenta.getIdentificacionCliente());

    if (cuenta.getSaldo().doubleValue() == saldoEnMovimientos.doubleValue()){
      q.setParameter(7,"OK");
    }else{
      q.setParameter(7,"ERROR");
    }
    int result = q.executeUpdate();
    if (result == 0){
      throw new RuntimeException("Error, no se registro el detalle de control de auditoria");
    }
  }


  private List<CuentaAuditoriaDTO> consultarCuentasCliente(Integer idCliente){
    String sql = "select tipo_identificacion, identificacion, num_cuenta, saldo \n" +
      "from cliente, cuenta \n" +
      "where id = ?\n" +
      "and cliente_id = id";
    Query q = em.createNativeQuery(sql);
    q.setParameter(1,idCliente);

    List<Object[]> listaResultados = q.getResultList();

    List<CuentaAuditoriaDTO> listaCuentas = new ArrayList<CuentaAuditoriaDTO>();

    for( Object[] arrayObjetos : listaResultados){
      CuentaAuditoriaDTO cta = new CuentaAuditoriaDTO();
      cta.setTipoIDCliente((String)arrayObjetos[0]);
      cta.setIdentificacionCliente((BigInteger) arrayObjetos[1]);
      cta.setNumeroCuenta((Integer) arrayObjetos[2]);
      cta.setSaldo((Double)arrayObjetos[3]);

      listaCuentas.add(cta);
    }
    return listaCuentas;

  }

  private Double consultarSaldoMovimientos(Integer numCuenta) {
    Query q = em.createNativeQuery("select tipo_operacion, sum(valor) as suma\n" +
      "from movimiento where cuenta_id = ?\n" +
      "group by tipo_operacion");
    q.setParameter(1, numCuenta);
    List<Object[]> resultados = q.getResultList();

    double total = 0;
    String tipoOperacion = null;

    for (Object[] registro : resultados) {
      tipoOperacion = (String)registro[0];
      if ("DEPOSITO".equals(tipoOperacion)) {
        total += (double) registro[1];
      }
      else if("RETIRO".equals(tipoOperacion)) {
        total -= (double) registro[1];
      }else{
        LOGGER.log(Level.WARNING,"Tipo de operacion desconocido {0}", tipoOperacion);
      }
    }
     return total;
  }


  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
  public void incrementarControlCuentasAuditadas(BigInteger idAuditoria, int cantidad){
    String sql= "update control_auditoria set cuentas_procesadas = cuentas_procesadas+ ? \n" +
      "where id_auditoria=?";
    Query q = em.createNativeQuery(sql);
    q.setParameter(1,cantidad);
    q.setParameter(2,idAuditoria);

    int result = q.executeUpdate();
    LOGGER.log(Level.FINEST,"resultado de Update =  {0}", result );
    /*
    if (result == 0) {
      throw new RuntimeException("Error, no se logró incrementar el contador de cuentas procesadas");
    }*/
  }

  @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
  public void actualizarEstadoAuditoria(Long idAuditoria, String estado){
    Query q = em.createNativeQuery("update control_auditoria set estado_proceso = ?");
    q.setParameter(1,estado);
    int result = q.executeUpdate();
    if( result == 0){
      throw new RuntimeException("Error, no se logro actualizar el estado del proceso de auditoria a " + estado);
    }
  }

  @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
  public void finalizarProceso(BigInteger idAuditoria){
    String sql="update control_auditoria set estado_proceso = ?, \n" +
      "tiempo_ejecucion = (current_timestamp - fecha) \n" +
      "where id_auditoria = ? and total_cuentas = cuentas_procesadas \n"+
      "and estado_proceso <> 'FINALIZADO'";

    Query q = em.createNativeQuery(sql);
    q.setParameter(1,"FINALIZADO");
    q.setParameter(2,idAuditoria);
    int result = q.executeUpdate();
    if(result == 1){
      LOGGER.log(Level.FINEST,"Se llego al final de proceso y se actualizo el estado a FINALIZADO");
      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/ HH:mm:ss");
      Calendar cal = Calendar.getInstance();
      LOGGER.log(Level.FINEST,"Hora de finalizacion: {0}", cal.getTime());
    }
  }
}
