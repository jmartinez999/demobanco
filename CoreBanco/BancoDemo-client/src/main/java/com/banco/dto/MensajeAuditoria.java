package com.banco.dto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by jmartinez on 06/05/2016.
 */
public class MensajeAuditoria implements Serializable{
    private Integer idCliente;
    private BigInteger idProceso;

    public MensajeAuditoria(Integer idCliente, BigInteger idProceso) {
        this.idCliente = idCliente;
        this.idProceso = idProceso;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public BigInteger getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(BigInteger idProceso) {
        this.idProceso = idProceso;
    }
}
