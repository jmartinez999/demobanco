package com.banco.rest;

import com.banco.dto.ProcesoAuditoria;
import com.banco.ejb.IAuditoriaCuentasLocal;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jmartinez on 5/6/16.
 */
@Path("auditoria")
@RequestScoped
public class AuditoriaResource {

  private static final Logger LOGGER = Logger.getLogger(AuditoriaResource.class.getName());

  @EJB
  IAuditoriaCuentasLocal auditoriaEJB;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response iniciarAuditoria(){

    BigInteger idProceso = auditoriaEJB.registrarControlAuditoria();
    auditoriaEJB.iniciarAuditoriaCuentas(idProceso);
    ProcesoAuditoria proceso = new ProcesoAuditoria(idProceso);

    return Response.ok().entity(proceso).type(MediaType.APPLICATION_JSON).build();
  }

}
