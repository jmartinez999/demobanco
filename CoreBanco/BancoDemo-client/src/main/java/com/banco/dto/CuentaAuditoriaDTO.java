package com.banco.dto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by jmartinez on 5/5/16.
 */
public class CuentaAuditoriaDTO implements Serializable {

  private String tipoIDCliente;
  private BigInteger identificacionCliente;
  private Integer numeroCuenta;
  private Double saldo;

  public String getTipoIDCliente() {
    return tipoIDCliente;
  }

  public void setTipoIDCliente(String tipoIDCliente) {
    this.tipoIDCliente = tipoIDCliente;
  }

  public BigInteger getIdentificacionCliente() {
    return identificacionCliente;
  }

  public void setIdentificacionCliente(BigInteger identificacionCliente) {
    this.identificacionCliente = identificacionCliente;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Integer getNumeroCuenta() {
    return numeroCuenta;
  }

  public void setNumeroCuenta(Integer numeroCuenta) {
    this.numeroCuenta = numeroCuenta;
  }
}
