package com.banco.dto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by jmartinez on 5/6/16.
 */
public class ProcesoAuditoria implements Serializable {

  private BigInteger numeroProceso;

  public ProcesoAuditoria(BigInteger numeroProceso) {
    this.numeroProceso = numeroProceso;
  }

  public BigInteger getNumeroProceso() {
    return numeroProceso;
  }

  public void setNumeroProceso(BigInteger numeroProceso) {
    this.numeroProceso = numeroProceso;
  }
}
