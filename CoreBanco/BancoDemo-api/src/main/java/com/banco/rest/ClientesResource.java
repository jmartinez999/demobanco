/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.rest;

import com.banco.dto.ClienteSaldos;
import com.banco.ejb.IClienteFacadeLocal;
import com.banco.entidades.Cliente;
import com.banco.entidades.Cuenta;
import com.banco.entidades.TipoIdentificacion;
import com.banco.errorhandling.ErrorMessage;
import com.banco.exceptions.BancoException;
import com.banco.pagination.PaginatedListWrapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author jmartinez
 */
@Path("clientes")
@RequestScoped
public class ClientesResource {

  private static final Logger LOGGER = Logger.getLogger(ClientesResource.class.getName());

  @Context
  private UriInfo context;

  //@EJB(lookup = "java:global/BancoDemo-ejb-1.0-SNAPSHOT/ClienteFacade!com.banco.ejb.IClienteFacadeLocal")
  @EJB
  IClienteFacadeLocal facadeCliente;

  /**
   * Creates a new instance of ClientesResource
   */
  public ClientesResource() {
  }

  /**
   * Retrieves representation of an instance of com.banco.rest.ClientesResource
   *
   * @return an instance of com.banco.entidades.Cliente
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PaginatedListWrapper listClientes(@DefaultValue("1")
          @QueryParam("page") Integer page,
          @DefaultValue("id")
          @QueryParam("sortFields") String sortFields,
          @DefaultValue("asc")
          @QueryParam("sortDirections") String sortDirections) {
    LOGGER.log(Level.FINEST, "Peticion GET para listarClientes Pagina: {0} ", page);
    PaginatedListWrapper paginatedListWrapper = new PaginatedListWrapper();
    paginatedListWrapper.setCurrentPage(page);
    paginatedListWrapper.setSortFields(sortFields);
    paginatedListWrapper.setSortDirections(sortDirections);
    paginatedListWrapper.setPageSize(10);
    return findClientes(paginatedListWrapper);
  }

  private PaginatedListWrapper findClientes(PaginatedListWrapper wrapper) {
    int totalClientes = facadeCliente.count();
    wrapper.setTotalResults(totalClientes);
    int start = (wrapper.getCurrentPage() - 1) * wrapper.getPageSize();
    wrapper.setList(facadeCliente.findRange(start,
            wrapper.getPageSize(),
            wrapper.getSortFields(),
            wrapper.getSortDirections()));
    return wrapper;
  }


  /*
    @SuppressWarnings("unchecked")
    private List<Cliente> findClientes(int startPosition, int maxResults, String sortFields, String sortDirections) {

        Query query =
                entityManager.createQuery("SELECT p FROM Person p ORDER BY p." + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

   */
  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Cliente consultarCliente(@PathParam("id") Long id) {
    return facadeCliente.find(id);

  }

  @GET
  @Path("/tipo/{tipoId}/numero/{identificacion}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultarCliente(@PathParam("tipoId") String tipoId, @PathParam("identificacion") Long numero) {
    LOGGER.log(Level.FINEST, tipoId + " - Numero de identificacion: {0}",numero);
    //TODO Obtener la enumeracion a partir del String recibido
    TipoIdentificacion enumTipoIdentificacion = TipoIdentificacion.valueOf(tipoId);
    Cliente cte =  facadeCliente.findByIdentificacion(enumTipoIdentificacion,numero);
    return Response.ok().entity(cte).type(MediaType.APPLICATION_JSON).build();
  }


  @GET
  @Path("/{tipoId}/{numero}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultarClienteV2(@PathParam("tipoId") String tipoId, @PathParam("numero") Long numero) {
    LOGGER.log(Level.FINEST, "V2 :: "+ tipoId + " - Numero: {0}",numero);
    //TODO Obtener la enumeracion a partir del String recibido
    TipoIdentificacion enumTipoIdentificacion = TipoIdentificacion.valueOf(tipoId);
    Cliente cte =  facadeCliente.findByIdentificacion(enumTipoIdentificacion,numero);
    return Response.ok().entity(cte).type(MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("{id}/saldos")
  @Produces(MediaType.APPLICATION_JSON)
  public Response consultarSaldosCliente(@PathParam("id") Long id) {

    // Dummy para ejemplo
    ClienteSaldos cteSaldos = new ClienteSaldos();

    Cliente cte = new Cliente();
    cte.setTipoIdentificacion(TipoIdentificacion.CC);
    cte.setIdentificacion(79660998);
    cteSaldos.setCte(cte);

    Cuenta ctaUno = new Cuenta();
    ctaUno.setSaldo(500000);
    cteSaldos.addCuenta(ctaUno);

    Cuenta ctaDos = new Cuenta();
    ctaDos.setSaldo(350000);
    cteSaldos.addCuenta(ctaDos);

    return Response.ok().entity(cteSaldos).type(MediaType.APPLICATION_JSON).build();
  }

  @DELETE
  @Path("{id}")
  public void eliminarCliente(@PathParam("id") Long id) {
    LOGGER.log(Level.FINE, "Request para eliminar cliente con id {0}", id);
    facadeCliente.remove(id);
  }

  /**
   * PUT method for updating or creating an instance of ClientesResource
   *
   * @param cliente representation for the resource
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response guardarCliente(Cliente cliente){

    if (cliente.getId() == null) {
      try {
        facadeCliente.crearCliente(cliente);
      } catch (BancoException ex) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setCode(ex.getErrorCode());
        errorMessage.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        errorMessage.setMessage(ex.getMessage());
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        errorMessage.setDeveloperMessage(errorStackTrace.toString());
        errorMessage.setLink("www.banco.com/soporte");

        return Response.status(errorMessage.getStatus())
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
      }
    } else {
      facadeCliente.modificarCliente(cliente);
    }
    return Response.ok().build();
  }
}
