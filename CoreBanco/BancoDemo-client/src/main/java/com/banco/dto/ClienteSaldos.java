package com.banco.dto;

import com.banco.entidades.Cliente;
import com.banco.entidades.Cuenta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmartinez on 4/11/16.
 */
public class ClienteSaldos {



  private Cliente cte;

  private List<Cuenta> listaCuentas;

  public ClienteSaldos(){
    listaCuentas = new ArrayList<Cuenta>();
  }

  public double getSaldoTotal(){
    double suma = new Float(0);
    for (int i = 0; i < listaCuentas.size(); i++) {
      Cuenta c = listaCuentas.get(i);
      suma += c.getSaldo();
    }
    return suma;
  }

  public void addCuenta(Cuenta cta){
    if (listaCuentas== null){
      listaCuentas = new ArrayList<Cuenta>();
    }
    listaCuentas.add(cta);
  }

  public Cliente getCte() {
    return cte;
  }

  public void setCte(Cliente cte) {
    this.cte = cte;
  }

}
