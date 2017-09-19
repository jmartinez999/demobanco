/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.dto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author jmartinez
 */
public class NuevaCuentaDTO implements Serializable{
  
  private String tipoIDCliente;
  private Long numIdCliente;
  private Double valorInicial;

  public String getTipoIDCliente() {
    return tipoIDCliente;
  }

  public void setTipoIDCliente(String tipoIDCliente) {
    this.tipoIDCliente = tipoIDCliente;
  }

  public Long getNumIdCliente() {
    return numIdCliente;
  }

  public void setNumIdCliente(Long numIdCliente) {
    this.numIdCliente = numIdCliente;
  }

  public Double getValorInicial() {
    return valorInicial;
  }

  public void setValorInicial(Double valorInicial) {
    this.valorInicial = valorInicial;
  }
  
  
  
}
