/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.exceptions;

/**
 *
 * @author jmartinez
 */
public class BancoException extends Exception{
    private int errorCode;
    
    public BancoException(String msg){
        super(msg);
    }
    
    public BancoException(int errorCode, String msg){
        super(msg);
        this.errorCode = errorCode;
    }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }
    
}
