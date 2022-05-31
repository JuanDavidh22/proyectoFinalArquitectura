/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edu.unipiloto.services;

import com.edu.unipiloto.Main.PersistenceManager;
import com.edu.unipiloto.models.Usuario;
import com.edu.unipiloto.dto.UsuarioDTO;
import com.edu.unipiloto.models.ActualizarUsuario;
import com.edu.unipiloto.models.Proyecto;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioService {

    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Usuario u order by u.nombres ASC");
        List<Usuario> usuarios = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(usuarios).build();
    }

    public Usuario getUser(String login) {
        Query q = entityManager.createQuery("select u from Usuario u WHERE u.login = '" + login + "'");
        Usuario actualizarUsuario = (Usuario) q.getSingleResult();
        return actualizarUsuario;
    }

    @POST
    @Path("/actualizarUsuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarUsuario(ActualizarUsuario usuario) {
        ActualizarUsuario userConActualizacion = usuario;
        JSONObject rta = new JSONObject();

        userConActualizacion.setApellidos(usuario.getApellidos());
        userConActualizacion.setDocumento(usuario.getDocumento());
        userConActualizacion.setEmail(usuario.getEmail());
        userConActualizacion.setLogin(usuario.getLogin());
        userConActualizacion.setNombres(usuario.getNombres());
        
        Usuario userActual = getUser(userConActualizacion.getLogin());
        
        userActual.setApellidos(userConActualizacion.getApellidos());
        userActual.setDocumento(userConActualizacion.getDocumento());
        userActual.setEmail(userConActualizacion.getEmail());
        userActual.setNombres(userConActualizacion.getNombres());
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(userActual);
            entityManager.getTransaction().commit();
            entityManager.refresh(userActual);
            rta.put("mensaje", "Se actualizó correctamente el usuario");
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            userConActualizacion = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @POST
    @Path("/addUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaateUser(UsuarioDTO usuario) {

        Usuario user = new Usuario();
        JSONObject rta = new JSONObject();
        user.setApellidos(usuario.getApellidos());
        user.setDocumento(usuario.getDocumento());
        user.setEmail(usuario.getEmail());
        user.setLogin(usuario.getLogin());
        user.setNombres(usuario.getNombres());
        user.setPassword(usuario.getPassword());
        user.setTipoUsuario(usuario.getTipoUsuario());

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            entityManager.refresh(user);
            rta.put("mensaje", "Se creo exitosamente el usuario, sus datos son");
            rta.put("login", user.getLogin());
            rta.put("email", user.getEmail());
            rta.put("tipoUsuario:", user.getTipoUsuario());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            user = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }
}
