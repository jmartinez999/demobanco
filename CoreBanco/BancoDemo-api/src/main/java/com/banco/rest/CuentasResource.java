/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.rest;

import com.banco.dto.NuevaCuentaDTO;
import com.banco.ejb.ICuentaFacadeLocal;
import com.banco.entidades.Cuenta;
import com.banco.entidades.TipoIdentificacion;
import com.banco.errorhandling.ErrorMessage;
import com.banco.exceptions.BancoException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author jmartinez
 */
@Path("cuentas")
@RequestScoped
public class CuentasResource {

    @Context
    private UriInfo context;
    
    private static final Logger LOGGER = Logger.getLogger(CuentasResource.class.getName());
    
    @EJB
    ICuentaFacadeLocal facadeCuenta;

    /**
     * Creates a new instance of CuentasResource
     */
    public CuentasResource() {
    }

    /**
     * Retrieves representation of an instance of com.banco.rest.CuentasResource
     * @return an instance of com.banco.entidades.Cuenta
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Cuenta getJson() {
        //TODO return proper representation object
        //throw new UnsupportedOperationException();
        return new Cuenta();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardarCuenta(NuevaCuentaDTO nuevaCuenta){
      LOGGER.log(Level.INFO, "Inicia ejecucion guardarCuenta(..)");
      Cuenta cta = new Cuenta();
      cta.setSaldo(nuevaCuenta.getValorInicial());
      try{
        Integer numCuenta = facadeCuenta.crearCuenta(cta, 
              TipoIdentificacion.valueOf(nuevaCuenta.getTipoIDCliente()), nuevaCuenta.getNumIdCliente());
        LOGGER.log(Level.INFO, "OK, Se creo cuenta No.{0}",  numCuenta);
        cta.setNumCuenta(numCuenta);
        return Response.ok().entity(cta).type(MediaType.APPLICATION_JSON).build();
        
      }catch(BancoException e){
        LOGGER.log(Level.SEVERE, "Error en servicio Rest guardarCuenta(...)" );
        LOGGER.log(Level.SEVERE, e.getMessage());
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCode(e.getErrorCode());
        errorMessage.setStatus(Response.Status.NOT_FOUND.getStatusCode());
        errorMessage.setMessage(e.getMessage());
        StringWriter errorStackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(errorStackTrace));
        errorMessage.setDeveloperMessage(errorStackTrace.toString());
        errorMessage.setLink("www.banco.com/soporte");

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
      }
      
    }
    
    /**
     * PUT method for updating or creating an instance of CuentasResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(Cuenta content) {
    }
    
    
    
}
