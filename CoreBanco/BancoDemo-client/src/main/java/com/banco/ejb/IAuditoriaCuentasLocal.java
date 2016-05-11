package com.banco.ejb;

import javax.ejb.Local;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by jmartinez on 5/5/16.
 */
@Local
public interface IAuditoriaCuentasLocal {

  public BigInteger registrarControlAuditoria();

  public int auditarCuentasCliente(Integer idCliente, BigInteger idProcesoAuditoria);

  public void incrementarControlCuentasAuditadas(BigInteger idAuditoria, int cantidad);

  public void actualizarEstadoAuditoria(Long idAuditoria,String estado);

  public List obtenerClientesParaAuditar();

  public void iniciarAuditoriaCuentas(BigInteger idProceso);

  public void finalizarProceso(BigInteger idAuditoria);
}
